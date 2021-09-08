# Import libraries
from PIL import Image
import pytesseract
import requests
from pdf2image import convert_from_path
import os
import errno

# Path of the PDF files
path = "Transcripciones/"

# Ruta en donde se encuentra el archivo .txt donde se almacena la lista de las url de descarga de los PDF
enlaces = "PDFs.txt"

'''
Part #1 : Downloading the PDF files
'''


def download_file(url, cont):

    # String para darle nombre al archivo PDF descargado a partir de la URL
    local_filename = path + "PDF" + str(cont) + '.pdf'

    myfile = requests.get(url, stream=True, allow_redirects=True)

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

        # Descrgar el PDF de la url de la actual linea del archivo "PDFs.txt"
        nombrepdf = download_file(url, contador)

        # Llamar a la funcion que convierte cada página del PDF a imagen
        convertirpaginasaimagenes(nombrepdf, contador, url)

    file.close()


'''
Part #2 : Converting PDF to images
'''


def convertirpaginasaimagenes(pdf_file, cont, url):

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

    # Delete the PDF file previously downloaded
    os.remove(pdf_file)

    # Create a new folder to store the .txt file of the current PDF transcript
    newpath = creardirectorio(url)

    # Call to the function that extract text from the images
    extraertextodeimagenes(filelimit, cont, newpath)


'''
Part #3 - Recognizing text from the images using OCR
'''


def extraertextodeimagenes(filelimit, cont, newpath):

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

        # Store the .txt file in the new location
        nombrearchivotxt = newpath + "PDF" + str(9) + ".txt"

        # Finally, write the processed text to the file.
        escribir(nombrearchivotxt, text)

    # Close the file after writing all the text.
    # f.close()
    # nombrearchivotxt.close()


def escribir(filename, text):
    file = open(filename, "a", encoding="utf-8")  # Abrir en modo "a" Append
    text = text.lower()
    text = EliminarSimbolos(text)
    file.write(text)


'''
Part #4 - Creating a new path for the .txt file
'''


def creardirectorio(url):

    # Aquí estoy haciendo un substring a partir de la url, esto para darle un titulo a la carpeta nueva.
    # Lo estoy limitando a 32 caracteres pero este numero puede modificarse a su gusto.
    titulocarpeta = url[0:32]

    # Proceso de eliminación de caracteres y subcadenas innecesarias:
    caracteresaeliminar = "/\<>:?*|\n\r\t"

    for i in range(len(caracteresaeliminar)):
        titulocarpeta = titulocarpeta.replace(caracteresaeliminar[i], "")

    titulocarpeta = titulocarpeta.replace("https",  "").replace("http", "").replace("www", "")

    if titulocarpeta[0] == ".":
        titulocarpeta = titulocarpeta[1:]

    # Esta es la ruta de la nueva carpeta
    nuevodirectorio = path + titulocarpeta + "/"

    # Aquí estoy creando una nueva carpeta en la ruta de nuevodirectorio
    try:
        os.mkdir(nuevodirectorio)

    # Este except es para ignorar todos los errores que se me presenten
    # en caso de que la ruta o el directorio ya exista.
    except OSError as e:
        if e.errno != errno.EEXIST:
            raise

    print(nuevodirectorio)
    return nuevodirectorio


def EliminarSimbolos(text):
    simbolosparaborrar = "!#$%&'()*+,-./:;<=>?@[\]^_`{|}~"
    for i in range(len(simbolosparaborrar)):
        text = text.replace(simbolosparaborrar[i], "")
    return text


geturl(enlaces)
