# Import libraries
from PIL import Image
import pytesseract
import requests
from pdf2image import convert_from_path
import os

# Path of the PDF files
path = "C:/Users/Daniel Vargas/Documents/Tareas/Trabajo de Grado I/Transcripciones Páginas Web/WebScrapping/Transcripciones/"

# Ruta en donde se encuentra el archivo .txt donde se almacena la lista de las url de descarga de los PDF
# PDF_file = "C:/Users/Daniel Vargas/Documents/Tareas/Trabajo de Grado I/Transcripciones Páginas Web/Italy.pdf"
enlaces = "C:/Users/Daniel Vargas/Documents/Tareas/Trabajo de Grado I/Transcripciones Páginas Web/WebScrapping/PDFs.txt"

'''
Part #1 : Downloading the PDF files
'''


def download_file(url, cont):

    # String para darle nombre al archivo PDF descargado a partir de la URL
    local_filename = path + "PDF" + str(cont) + '.pdf'

    myfile = requests.get(url, stream=True, allow_redirects=True)

    # Con esto es como si descargara el PDF
    # open(local_filename, 'wb').write(myfile.content)

    with open(local_filename, "wb") as pdf:
        for chunk in myfile.iter_content(chunk_size=1024):
            # writing one chunk at a time to pdf file
            if chunk:
                pdf.write(chunk)

    myfile.close()

    return local_filename


# Obtener las url del archvio txt llamado "PDFs.txt"
def geturl(archivoenlaces):

    # Abrir en modo "r" lectura
    file = open(archivoenlaces, "r")

    # Contador para asignar el nombre del archivo txt de cada página web
    contador = 0

    while True:

        # Obtener cada url que hay en el archivo "PDFs.txt"
        url = file.readline()

        # Incrementar el contador para actualizar el nombre del archivo
        contador += 1

        # Condición de parada, cuando ya no hay más enlaces en el archivo "PDFs.txt"
        if not url:
            break

        nombrepdf = download_file(url, contador)
        convertirpaginasaimagenes(nombrepdf, contador)

    file.close()


'''
Part #2 : Converting PDF to images
'''


def convertirpaginasaimagenes(pdf_file, cont):

    # Store all the pages of the PDF in a variable
    pages = convert_from_path(pdf_file, 500, poppler_path=r'C:\Program Files\poppler-0.68.0\bin')

    # Counter to store images of each page of PDF to image
    image_counter = 1

    # Iterate through all the pages stored above
    for page in pages:
        # Declaring filename for each page of PDF as JPG
        # For each page, filename will be:
        # PDF page 1 -> page_1.jpg
        # PDF page 2 -> page_2.jpg
        # PDF page 3 -> page_3.jpg
        # ....
        # PDF page n -> page_n.jpg
        nombreimagen = path + "page_" + str(image_counter) + ".jpg"

        # Save the image of the page in system
        page.save(nombreimagen, 'JPEG')

        # Increment the counter to update filename
        image_counter = image_counter + 1

    # Variable to get count of total number of pages
    filelimit = image_counter - 1
    os.remove(pdf_file)
    extraertextodeimagenes(filelimit, cont)


'''
Part #3 - Recognizing text from the images using OCR
'''

# Creating a text file to write the output
# outfile = "C:/Users/Daniel Vargas/Documents/Tareas/Trabajo de Grado I/Transcripciones Páginas Web/outtext.txt"

# Open the file in append mode so that
# All contents of all images are added to the same file
# f = open(outfile, "a")


def extraertextodeimagenes(filelimit, cont):

    # contador = 0  # Contador para asignar el nombre del archivo txt que contiene el texto extraido de cada PDF

    # Iterate from 1 to total number of pages
    for i in range(1, filelimit + 1):
        # Set filename to recognize text from
        # Again, these files will be:
        # page_1.jpg
        # page_2.jpg
        # ....
        # page_n.jpg
        filename = path + "page_" + str(i) + ".jpg"

        # Recognize the text as string in image using pytesserct
        pytesseract.pytesseract.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'
        text = str((pytesseract.image_to_string(Image.open(filename))))
        os.remove(filename)  # Eliminar la imagen cuando ya no se necesite

        # The recognized text is stored in variable text
        # Any string processing may be applied on text
        # Here, basic formatting has been done:
        # In many PDFs, at line ending, if a word can't
        # be written fully, a 'hyphen' is added.
        # The rest of the word is written in the next line
        # Eg: This is a sample text this word here GeeksF-
        # orGeeks is half on first line, remaining on next.
        # To remove this, we replace every '-\n' to ''.
        text = text.replace('-\n', '')

        # contador += 1
        nombrearchivotxt = path + "PDF" + str(cont) + ".txt"

        # Finally, write the processed text to the file.
        escribir(nombrearchivotxt, text)
        # f.write(text)

    # Close the file after writing all the text.
    # f.close()
    # nombrearchivotxt.close()


def escribir(filename, text):
    file = open(filename, "a", encoding="utf-8")  # Abrir en modo "a" Append
    file.write(text)


'''
Part #4 - Execute the program
'''

geturl(enlaces)
