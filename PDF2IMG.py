# Import libraries
from PIL import Image
from pdf2image import convert_from_path
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
import spacy
import unidecode
import os
import errno
import re
import pytesseract
import requests
import nltk

# Path of the PDF files
path = "Transcripciones/"

# Ruta en donde se encuentra el archivo .txt donde se almacena la lista de las url de descarga de los PDF
enlaces = "PDFs.txt"

# En caso de que el pdf se encuentre de manera local, cambiar el nombre según sea el caso
local_archive = path + "BecasQuiero-desarrollo-aplicaciones-servicios-web.pdf"

# Ruta en donde se encuentra el archivo .txt donde se almacena la transcripción de todas las paginas web
training_dataset = path + "TranscripciónDeTodasLasPaginas.txt"

# Definir los diferentes idiomas que aparecerán en las páginas:
languages = ["spanish", "english"]

'''
Descomentar estas lineas de código si es la primera vez que ejecuta el código
'''
# nltk.download('stopwords')
# nltk.download('punkt')
# nltk.download('wordnet')

# Crear una lista de las palabras "Stopwords" que se eliminarán del texto
stop_words_sp = set(stopwords.words('spanish'))
stop_words_en = set(stopwords.words('english'))

# Concatenar las stopwords
stop_words = stop_words_sp | stop_words_en

# Definir un lematizador para el idioma inglés
lemmatizer = WordNetLemmatizer()

# Definir un lematizador para el idioma español, para que, por ejemplo, no existan palabras como:
# canto, cantas, canta, cantamos, cantais, cantan, sino solo una palabra: "cantar"
lemmatizer_sp = spacy.load('es_core_news_sm')

'''
Part #1 : Downloading the PDF files
'''


def download_file(url, cont):

    # String para darle nombre al archivo PDF descargado a partir de la URL
    local_filename = path + "PDF" + str(cont) + '.pdf'

    myfile = requests.get(url, stream=True, allow_redirects=True, headers={'User-Agent': 'Mozilla / 5.0 (Windows NT 6.1)'
                                                                          'AppleWebKit / 537.36 (KHTML, como Gecko)'
                                                                          'Chrome / 41.0.2228.0 Safari / 537.3'})

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

        # En caso de que el PDF se encuentre local y no haya que descargarlo, descomentar esta linea
        # nombrepdf = local_archive

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
        nombrearchivotxt = newpath + "PDF" + str(cont) + ".txt"

        # Finally, write the processed text to the file.
        escribir(nombrearchivotxt, text)

    # Close the file after writing all the text.
    # f.close()
    # nombrearchivotxt.close()



# Función que retorna un arreglo donde cada posición es una linea del texto del documento ingresado
def leer_fichero_linea_por_linea(filename):
    words = []
    with open(filename, encoding='utf-8') as diccionario:
        lineas = diccionario.readlines()
        for linea in lineas:
            words.append(linea.strip('\n'))
    return words


dict_all_words = leer_fichero_linea_por_linea("Word2Vec/dictionary_sp_en.txt")


def escribir(filename, text):
    file = open(filename, "a", encoding="utf-8")  # Abrir en modo "a" Append
    training_file = open(training_dataset, 'a', encoding="utf-8")  # Abrir en modo "a" Append

    # A continuación se hace el llamado a las funciones que realizan la limpieza del texto
    text = limpieza_textos(text)

    # Una vez hecha la limpieza, se escribe en el archivo .txt
    file.write(text)
    training_file.write(" " + text)


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


'''
Part #5 - Cleanning text functions
'''


def detectar_idioma(text_to_detect):
    text_to_detect = text_to_detect.lower()
    tokens = nltk.tokenize.word_tokenize(text_to_detect)

    # Creamos un dict donde almacenaremos la cuenta de las stopwords para cada idioma
    lang_count = {}

    # Variable para determinar el set de stopwords a utilizar según el lenguaje a detectar
    current_lang_stop_words = stop_words_sp

    for lang in languages:

        if lang == "english":
            current_lang_stop_words = stop_words_en

        lang_count[lang] = 0  # Inicializa a 0 el contador para cada idioma

        # Recorremos las palabras del texto a analizar
        for word in tokens:
            # Si la palabra se encuentra entre las stopwords, incrementa el contador:
            if word in current_lang_stop_words:
                lang_count[lang] += 1

    # Obtener y retornar el idioma con el número mayor de coincidencias
    return max(lang_count, key=lang_count.get)


# Esta funcion se encarga de llamar a las otras funciones encargadas de limpiar el texto en español,
# al final me retorna el texto con todas las transformaciones que se le hicieron en la limpieza
def limpieza_textos(text):

    text = text.lower()  # Minusculas
    text = re.sub("\n+", " ", text)  # Eliminar saltos de linea
    text = re.sub("\r+", " ", text)
    text = re.sub("\t+", " ", text)
    text = re.sub(r"[0-9]+", "", text)  # Eliminar cualquier numero del texto

    # Hacer limpieza especificamente para textos en ingles
    if detectar_idioma(text[0:80]) == "english":
        text = expandir_contracciones(text)
        text = lemmatize_words_en(text)
    else:
        # Hacer limpieza especificamente para textos en español
        text = lemmatize_words_sp(text)

    text = eliminar_simbolos(text)
    text = buscar_en_diccionario(text)
    text = unidecode.unidecode(text)  # Eliminar cualquier tilde, dieresis o "ñ"
    text = eliminar_stopwords(text)

    return text


def expandir_contracciones(text):
    text = re.sub("’", "'", text)
    text = re.sub(r"n\'t", " not", text)
    text = re.sub(r"\'re", " are", text)
    text = re.sub(r"\'s", " is", text)
    text = re.sub(r"\'d", " would", text)
    text = re.sub(r"\'ll", " will", text)
    text = re.sub(r"\'t", " not", text)
    text = re.sub(r"\'ve", " have", text)
    text = re.sub(r"\'m", " am", text)
    return text


def lemmatize_words_en(text):
    return " ".join([lemmatizer.lemmatize(word) for word in text.split()])


def lemmatize_words_sp(text):
    modelo_aplicado = lemmatizer_sp(text)
    lemmas = [tok.lemma_ for tok in modelo_aplicado]
    texto_lematizado = " ".join(lemmas)
    return texto_lematizado


def buscar_en_diccionario(text):
    path_out_of_dictionary_words = path + "PalabrasFueraDelDiccionario.txt"
    tokens = text.split()

    palabras_en_el_diccionario = ' '.join([word for word in tokens if word in dict_all_words])
    #palabras_fuera_del_diccionario = ' '.join([word for word in tokens if word not in dict_all_words])
    #escribir(path_out_of_dictionary_words, " " + palabras_fuera_del_diccionario)

    return palabras_en_el_diccionario


def eliminar_simbolos(text):
    simbolosparaborrar = "¡!#$€£¢¥%&'\"()*+,-./:;<=>¿?@[\]^_`{|}~“”‘’—–®©ⓒ»ªº™⭐♦"
    for i in range(len(simbolosparaborrar)):
        text = text.replace(simbolosparaborrar[i], "")
    return text


def eliminar_stopwords(texto):
    return ' '.join([word for word in texto.split(' ') if word not in stop_words])


geturl(enlaces)
