from bs4 import BeautifulSoup
from urllib.request import urlopen, Request
import ssl

# Crear un certificado SSL por defecto, esto por si algunas páginas no lo tienen:
ssl._create_default_https_context = ssl._create_unverified_context
path = "C:/Users/Daniel Vargas/Documents/Tareas/Trabajo de Grado I/Transcripciones Páginas Web/WebScrapping/Transcripciones/Pagina"
enlaces = "C:/Users/Daniel Vargas/Documents/Tareas/Trabajo de Grado I/Transcripciones Páginas Web/WebScrapping/Paginas de becas.txt"


def getwebsite(url):
    req = Request(url, headers={'User-Agent': 'Mozilla / 5.0 (Windows NT 6.1) AppleWebKit / 537.36 (KHTML, como Gecko) Chrome / 41.0.2228.0 Safari / 537.3'})
    page = urlopen(req).read().decode("utf-8")
    pagina = BeautifulSoup(page, "html.parser")  # Este objeto contiene todo el contenido del sitio web
    return pagina


def escribir(filename, text):
    file = open(filename, 'w', encoding="utf-8")  # Abrir en modo "w" Escritura
    file.write(text)
    file.close()


def transcribir(url, cont):
    soup = getwebsite(url)
    transcript = soup.get_text()
    nombrearchivo = path + str(cont) + ".txt"  # Darle al archivo el nombre PaginaN, donde N es un contador
    escribir(nombrearchivo, transcript)


def geturl(filename):
    file = open(filename, "r")  # Abrir en modo "r" lectura
    contador = 0  # Contador para asignar el nombre del archivo txt de cada página web
    while True:
        url = file.readline()
        contador += 1
        if not url:
            break
        transcribir(url, contador)
    file.close()


geturl(enlaces)
