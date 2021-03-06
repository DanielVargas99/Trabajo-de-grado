# Import libraries
from bs4 import BeautifulSoup
from urllib.request import urlopen, Request
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
import unidecode
import spacy
import ssl
import os
import errno
import re
import nltk

# Crear un certificado SSL por defecto, esto por si algunas páginas no lo tienen:
ssl._create_default_https_context = ssl._create_unverified_context

# Cambiar la ruta para cada caso, donde se quieran guardar los archivos .txt de las páginas web y el .txt de los enlaces
path = "Transcripciones/"

# Ruta en donde se encuentra el archivo .txt donde se almacena la lista de las url de cada página web
enlaces = "Paginas de becas.txt"

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
lemmatizer_en = WordNetLemmatizer()

# Definir un lematizador para el idioma español, para que, por ejemplo, no existan palabras como:
# canto, cantas, canta, cantamos, cantais, cantan, sino solo una palabra: "cantar"
lemmatizer_sp = spacy.load('es_core_news_sm')

'''
Parte #1: Obtener las transcripciones de los sitios web (web scrapper) y guardarlas en carpetas
'''


def getwebsite(url):
    req = Request(url, headers={'User-Agent': 'Mozilla / 5.0 (Windows NT 6.1) AppleWebKit / 537.36 (KHTML, como Gecko)'
                                              'Chrome / 41.0.2228.0 Safari / 537.3'})
    page = urlopen(req).read().decode("utf-8")
    pagina = BeautifulSoup(page, "html.parser")  # Este objeto contiene todo el contenido del sitio web
    return pagina


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
    with open(filename, 'a', encoding="utf-8") as file:
        file.write(text)


def escribir_preprocesamiento(filename, text):
    file = open(filename, 'w', encoding="utf-8")  # Abrir en modo "w" Escritura
    training_file = open(training_dataset, 'a', encoding="utf-8")  # Abrir en modo "a" Append

    # A continuación se hace el llamado a las funciones que realizan la limpieza del texto
    text = limpieza_textos(text)

    # Una vez hecha la limpieza, se escribe en el archivo .txt
    file.write(text)
    training_file.write(" " + text)
    file.close()


def transcribir(url, cont, directorio):
    soup = getwebsite(url)
    transcript = soup.get_text()
    # Darle al archivo el nombre PaginaN, donde N es un contador:
    nombrearchivo = directorio + "Pagina" + str(cont) + ".txt"
    escribir_preprocesamiento(nombrearchivo, transcript)


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


def geturl(filename):
    file = open(filename, "r")  # Abrir en modo "r" lectura
    contador = 0  # Contador para asignar el nombre del archivo txt de cada página web
    while True:
        url = file.readline()
        contador += 1
        if not url:
            break
        directorio = creardirectorio(url)
        transcribir(url, contador, directorio)
    file.close()


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
            # Si la palabra se encuentra entre las stopwords, incrementa el contador
            if word in current_lang_stop_words:
                lang_count[lang] += 1

    # Obtener y retornar el idioma con el número mayor de coincidencias
    return max(lang_count, key=lang_count.get)


'''
Parte #2: Funciones para realizar limpieza a los textos y preprocesamiento
'''


# Esta funcion se encarga de llamar a las otras funciones encargadas de limpiar el texto en español,
# al final me retorna el texto con todas las transformaciones que se le hicieron en la limpieza
def limpieza_textos(text):

    text = text.lower()  # Minusculas
    text = re.sub("\n+", " ", text)  # Eliminar saltos de linea
    text = re.sub("\r+", " ", text)
    text = re.sub("\t+", " ", text)
    text = re.sub(r"[0-9]+", "", text)  # Eliminar cualquier numero del texto
    # text = re.sub("  +", " ", text)  # Eliminar espacios en blanco

    # Hacer limpieza especificamente para textos en ingles
    if detectar_idioma(text[0:100]) == "english":
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
    return " ".join([lemmatizer_en.lemmatize(word) for word in text.split()])


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
    simbolosparaborrar = "¡!#$€£¢¥%&'\"()*+,-./:;<=>¿?@[\]^_`{|}~“”‘’—–®©ⓒ»ªº™⭐♦※"
    for i in range(len(simbolosparaborrar)):
        text = text.replace(simbolosparaborrar[i], "")
    return text


def eliminar_stopwords(text):
    return ' '.join([word for word in text.split(' ') if word not in stop_words])


geturl(enlaces)
