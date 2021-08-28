# Import libraries
from bs4 import BeautifulSoup
from urllib.request import urlopen, Request
import ssl
import os
import errno

# Crear un certificado SSL por defecto, esto por si algunas páginas no lo tienen:
ssl._create_default_https_context = ssl._create_unverified_context

# Cambiar la ruta para cada caso, donde se quieran guardar los archivos .txt de las páginas web y el .txt de los enlaces
path = "Transcripciones/"

# Ruta en donde se encuentra el archivo .txt donde se almacena la lista de las url de cada página web
enlaces = "Paginas de becas.txt"


def getwebsite(url):
    req = Request(url, headers={'User-Agent': 'Mozilla / 5.0 (Windows NT 6.1) AppleWebKit / 537.36 (KHTML, como Gecko) Chrome / 41.0.2228.0 Safari / 537.3'})
    page = urlopen(req).read().decode("utf-8")
    pagina = BeautifulSoup(page, "html.parser")  # Este objeto contiene todo el contenido del sitio web
    return pagina


def escribir(filename, text):
    file = open(filename, 'w', encoding="utf-8")  # Abrir en modo "w" Escritura
    file.write(text)
    file.close()


def transcribir(url, cont, directorio):
    soup = getwebsite(url)
    transcript = soup.get_text()
    nombrearchivo = directorio + "Pagina" + str(cont) + ".txt"  # Darle al archivo el nombre PaginaN, donde N es un contador
    escribir(nombrearchivo, transcript)


def creardirectorio(url):

    titulocarpeta = url[0:32]
    caracteresaeliminar = "/\<>:?*|\n\r\t"

    for i in range(len(caracteresaeliminar)):
        titulocarpeta = titulocarpeta.replace(caracteresaeliminar[i], "")

    titulocarpeta = titulocarpeta.replace("https",  "").replace("http", "").replace("www", "")

    if titulocarpeta[0] == ".":
        titulocarpeta = titulocarpeta[1:]

    nuevodirectorio = path + titulocarpeta + "/"

    try:
        os.mkdir(nuevodirectorio)
    except OSError as e:
        if e.errno != errno.EEXIST:
            raise

    return nuevodirectorio


def geturl(filename):
    file = open(filename, "r")  # Abrir en modo "r" lectura
    contador = 0  # Contador para asignar el nombre del archivo txt de cada página web
    while True:
        url = file.readline()
        contador += 1
        if not url:
            break
        creardirectorio(url)
        directorio = creardirectorio(url)
        transcribir(url, contador, directorio)
    file.close()


geturl(enlaces)
