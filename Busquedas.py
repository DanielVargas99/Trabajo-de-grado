import csv
import unidecode
import os
import nltk
import spacy
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer

# Cambiar la ruta para cada caso, en esta ruta se guardan los archivos .txt con las transcripciones de cada página
path = "Transcripciones/"

# Ubicación del archivo csv que contiene un resumen de cada página web
resumen = path + 'resumenes.csv'

# Definir los diferentes idiomas que aparecerán en las páginas:
languages = ["spanish", "english"]

# Crear una lista de las palabras "Stopwords" que se eliminarán del texto
stop_words_sp = set(stopwords.words('spanish'))
stop_words_en = set(stopwords.words('english'))

# Definir un lematizador para el idioma inglés
lemmatizer_en = WordNetLemmatizer()

# Definir un lematizador para el idioma español, para que, por ejemplo, no existan palabras como:
# canto, cantas, canta, cantamos, cantais, cantan, sino solo una palabra: "cantar"
lemmatizer_sp = spacy.load('es_core_news_sm')


# Función encargada de retornar una lista con las ubicaciones/rutas de todos los archivos .csv
def obtener_archivos(extension):

    # Lista que guarda todos los elementos dentro de la ruta Transcripciones/ (incluyendo carpetas)
    contenido = os.listdir(path)

    # Lista que va a guardar todos los archivos .csv que se encuentren DENTRO DE UNA CARPETA, en la ruta path
    csvs_files = []

    # Recorrer cada fichero dentro de la ruta Transcripciones/
    for fichero in contenido:
        # fichero_actual: Este es el fichero actual, puede ser otro directorio o un archivo
        fichero_actual = os.path.join(path, fichero)
        # Solo operar cuando fichero_actual sea un directorio, una subcarpeta dentro de Transcripciones/
        if os.path.isdir(fichero_actual):
            # contenido_subcarpeta: Variable que guarda cada archivo .txt dentro de el directorio de fichero_actual
            contenido_subcarpeta = os.listdir(fichero_actual)
            for archivo_transcripcion in contenido_subcarpeta:
                # Guardar solo los archivos .txt, excluir los archivos .csv
                if archivo_transcripcion.endswith(extension):
                    # Añadir cada archivo .txt a la lista de transcripciones
                    csvs_files.append(os.path.join(fichero_actual, archivo_transcripcion))

    return csvs_files


def buscar_palabra_en_lista_csv(lista_csvs):

    lista_aux = []

    for i in range(len(lista_csvs)):
        cont = 0
        with open(lista_csvs[i]) as p:
            reader = csv.reader(p, delimiter=';')
            for row in reader:
                for j in range(len(busqueda)):
                    if row[0] == busqueda[j]:  # row[0] la primera columna del csv
                        cont += float(row[1])
        lista_aux.append((lista_csvs[i], cont))

    return lista_aux


def imprimir_resultados(lista_ordenada):
    for i in range(20):  # Tomar solo los primeros 20 elementos de la lista
        with open(resumen) as a:
            reader = csv.reader(a, delimiter=';')
            for row in reader:
                if row[0] == lista_ordenada[i][0]:
                    print("------------------------------------------------")
                    print("Documento: ", row[0])
                    print("Nombre: ", row[1])
                    print("País: ", row[2])
                    print("Tipo de estudio: ", row[3])
                    print("Entidad que ofrece la beca: ", row[4])
                    print("Descripción: ", row[5])
                    print("Enlace de la convocatoria: ", row[6])


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


def limpieza_busqueda(text):
    text = text.lower()
    text = eliminar_simbolos(text)

    # Hacer limpieza especificamente para textos en ingles
    if detectar_idioma(text[0:100]) == "english":
        text = lemmatize_words_en(text)
    else:
        # Hacer limpieza especificamente para textos en español
        text = lemmatize_words_sp(text)

    text = unidecode.unidecode(text)  # Eliminar cualquier tilde, dieresis o "ñ"
    print(text)
    return text


def eliminar_simbolos(text):
    simbolosparaborrar = "¡!#$€£¢¥%&'\"()*+,-./:;<=>¿?@[\]^_`{|}~“”‘’—–®©ⓒ»ªº™⭐♦※"
    for i in range(len(simbolosparaborrar)):
        text = text.replace(simbolosparaborrar[i], "")
    return text


def lemmatize_words_en(text):
    return " ".join([lemmatizer_en.lemmatize(word) for word in text.split()])


def lemmatize_words_sp(text):
    modelo_aplicado = lemmatizer_sp(text)
    lemmas = [tok.lemma_ for tok in modelo_aplicado]
    texto_lematizado = " ".join(lemmas)
    return texto_lematizado


print("Ingrese los términos de busqueda:\n")
busqueda = input()

csvs = obtener_archivos('.csv')  # Obtener una lista con las ubicaciones de todos los archivos CSV
busqueda = limpieza_busqueda(busqueda)  # Hacer limpieza a la busqueda que ingrese el usuario
busqueda = busqueda.split(" ")  # Separar la busqueda por espacios
resultadosBusqueda = buscar_palabra_en_lista_csv(csvs)  # Obtener los documentos que más se ajusten a la busqueda
sortedList = sorted(resultadosBusqueda, key=lambda aux: aux[1], reverse=True)  # Ordenar la lista de mayor a menor

print("\nRecomendación\n")

imprimir_resultados(sortedList)
                