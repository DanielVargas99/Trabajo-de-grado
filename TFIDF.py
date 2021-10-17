import os
from math import log
import nltk
from nltk import TextCollection

# Cambiar la ruta para cada caso, en esta ruta se guardan los archivos .txt con las transcripciones de cada página
path = "Transcripciones/"

# Ruta en donde se encuentra el archivo .txt donde se almacena la transcripción de todas las paginas web
training_dataset = path + "TranscripciónDeTodasLasPaginas.txt"


def obtener_archivos_txt():

    # Lista que guarda todos los elementos dentro de la ruta Transcripciones/ (incluyendo carpetas)
    contenido = os.listdir(path)

    # Lista que va a guardar todos los archivos .txt que se encuentren DENTRO DE UNA CARPETA, en la ruta path
    transcripciones = []

    # Recorrer cada fichero dentro de la ruta Transcripciones/
    for fichero in contenido:
        # fichero_actual: Este es el fichero actual, puede ser otro directorio o un archivo
        fichero_actual = os.path.join(path, fichero)
        # Solo operar cuando fichero_actual sea un directorio, una subcarpeta dentro de Transcripciones/
        if os.path.isdir(fichero_actual):
            # contenido_subcarpeta: Variable que guarda cada archivo .txt dentro de el directorio de fichero_actual
            contenido_subcarpeta = os.listdir(fichero_actual)
            for archivo_transcripcion in contenido_subcarpeta:
                # Añadir cada archivo .txt a la lista de transcripciones
                transcripciones.append(os.path.join(fichero_actual, archivo_transcripcion))

    return transcripciones


def tokenizar(filename):
    file = open(filename, "r", encoding='utf-8').read()  # Abrir en modo "r" lectura
    # tokenizar el documento en oraciones
    sentences = nltk.sent_tokenize(file)
    # tokenizar cada oracion en palbras
    tokens = [nltk.tokenize.word_tokenize(sentence) for sentence in sentences]
    return tokens[0]


'''
Calcular TF (Term Frequency)
'''


def tf(termino, documento):

    # Primero: Hallar la frecuencia de la palabra en el documento
    tokens = tokenizar(documento)
    term_frequency = tokens.count(termino)

    # Segundo: Frecuencia de termino normalizada
    normalized_term_frequency = term_frequency / len(tokens)

    return normalized_term_frequency


'''
Calcular IDF (Inverse Document Frequency)
'''


def idf(termino, lista_documentos):

    # Primero: Calcular el numero de documentos donde el termino t aparece

    cont = 0
    for document in lista_documentos:
        tokens_actual_document = tokenizar(document)
        if termino in tokens_actual_document:
            cont += 1

    # Segundo: Calcular IDF
    idf = log(len(lista_documentos)/cont)

    return idf


def tf_idf(tf, idf):
    # Multiplicar tf con idf para obtener tf-idf
    tfidf = tf * idf
    return tfidf


def term_frequency_in_all_documents(termino, lista_documentos):

    term_frequency_all_documents = []
    for i in range(len(lista_documentos)):
        term_frequency_all_documents.append(tf(termino, lista_documentos[i]))
    return term_frequency_all_documents


def best_match_documents(term_frequency_all_documents, idf_value):

    lista_tf_idf_calculos = []
    for i in range(len(term_frequency_all_documents)):
        lista_tf_idf_calculos.append(tf_idf(term_frequency_all_documents[i], idf_value))
    topn_documents = sorted(range(len(lista_tf_idf_calculos)), key=lambda i: lista_tf_idf_calculos[i],reverse=True)[:10]
    

    return topn_documents


termino = 'becas'
lista_de_documentos = obtener_archivos_txt()
tf_normalizada = tf(termino, lista_de_documentos[17])
inverse_document_frequency = idf(termino, lista_de_documentos)
tfidf = tf_idf(tf_normalizada, inverse_document_frequency)
# frecuencia_todos_los_documentos = term_frequency_in_all_documents(termino, lista_de_documentos)


print(tf_normalizada, inverse_document_frequency, tfidf)
# print(best_match_documents(frecuencia_todos_los_documentos, inverse_document_frequency))

