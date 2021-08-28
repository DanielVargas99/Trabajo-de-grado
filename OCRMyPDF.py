import os
import requests
import pdfplumber

path = "C:/Users/Daniel Vargas/Documents/Tareas/Trabajo de Grado I/Transcripciones Páginas Web/PDF"
enlaces = "C:/Users/Daniel Vargas/Documents/Tareas/Trabajo de Grado I/Transcripciones Páginas Web/WebScrapping/PDFs.txt"


def download_file(url, cont):
    local_filename = path + str(cont)  # String para darle nombre al archivo PDF de salida a partir de la URL

    with requests.get(url) as r:
        # assert r.status_code == 500, f'error, status code is {r.status_code}'  # Esto es por si no logré acceder a la página
        escribir(local_filename, r.content)  # Con esto es como si descargara el PDF
        os.system(f'ocrmypdf {local_filename} output.pdf')

    return local_filename


def escribir(filename, text):
    with open(filename, 'wb') as f:  # Abrir el archivo en modo escritura
        f.write(text)  # Escribir en el archivo txt


def geturl(filename):  # Obtener las url del archvio txt llamado "PDFs.txt"
    file = open(filename, "r")  # Abrir en modo "r" lectura
    contador = 0  # Contador para asignar el nombre del archivo txt de cada página web
    while True:
        url = file.readline()  # Obtener cada url que hay en el archivo "PDFs.txt"
        contador += 1
        if not url:  # Condición de parada, cuando ya no hay más enlaces en el archivo "PDFs.txt"
            break
        # nombrearchivo = path + url.split('/')[-1] + ".txt"
        nombrepdf = download_file(url, contador)
        transcribir(nombrepdf, contador)  # Mandar a transcribir el texto del PDF que acabo de descargar
    file.close()


def transcribir(pdfconvocatoria, cont):
    with pdfplumber.open(pdfconvocatoria) as pdf:
        page = pdf.pages[0]
        text = page.extract_text()
        nombretxt = path + str(cont) + ".txt"
        escribir(nombretxt, text)


geturl(enlaces)

# invoice = 'https://bit.ly/2UJgUpO'
# invoice_pdf = download_file(invoice)

# os.system(f'ocrmypdf {invoice_pdf} output.pdf')

# with pdfplumber.open('example.pdf') as pdf:
#    page = pdf.pages[0]
#    text = page.extract_text(x_tolerance=2)
#    print(text)

# lines = text.split('\n')

# amt_re = re.compile(r'\.\d\d$')

# subt = 0

# for line in lines:
#    if 'SUBTOTAL' in line:
#        break
#    if amt_re.search(line):
#        subt += float(line.split()[-1].replace(',', '').replace('$', ''))

# print(lines)
