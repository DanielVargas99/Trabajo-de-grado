from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import os
import csv

# Cambiar la ruta para cada caso, en esta ruta se guardan los archivos .txt con las transcripciones de cada página
path = "Transcripciones/"

# Ruta donde se encuentra el archivo txt que guarda las skipwords
path_skipwords = "Skipwords.txt"

tfidfvectorizer = TfidfVectorizer(encoding='utf-8',
                                  # strip_accents='unicode',
                                  max_df=0.95,
                                  min_df=0.03,
                                  ngram_range=(1, 2),
                                  # sublinear_tf=True
                                  )


# Función encargada de retornar una lista con las ubicaciones/rutas de todos los archivos .txt
def obtener_archivos(extension):

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
                # Guardar solo los archivos .txt, excluir los archivos .csv
                if archivo_transcripcion.endswith(extension):
                    # Añadir cada archivo .txt a la lista de transcripciones
                    transcripciones.append(os.path.join(fichero_actual, archivo_transcripcion))

    return transcripciones


# Función encargada de retornar una lista con los textos/transcripciones de todos los archivos .txt
def obtener_textos(lista_documentos):
    lista_textos = []
    # index: 0,1,...,len(lista_documentos); doc: se refiere al elemento actual de lista_documentos
    for index, doc in enumerate(lista_documentos):
        # Añadir a lista_textos la transcripción de la página actual
        lista_textos.append(open(doc, mode='r', encoding='utf-8').read())
        # Para cada uno de los textos de lista_textos vamos a eliminar las skipwords definidas al inicio
        lista_textos[index] = delete_skipwords(lista_textos[index], read_skipwords(path_skipwords))
    return lista_textos


# Calcular el tfidf de cada documento
def tf_idf(lista_textos):
    tfidfvectorizer.fit(lista_textos)
    tfidf_train_set = []
    for doc in lista_textos:
        tfidf_train_set.append(tfidfvectorizer.transform([doc]))
    return tfidf_train_set


def print_tfidf_values(lista_documentos, feature_names, tfidf_train):

    # Crear un diccionario donde la llave es el nombre/ubicación/ruta de cada documento de transcripcion, y el
    # elemento asociado a esa llave es una lista de tuplas, donde cada tupla tiene la palabra o n-grama y su
    # respectivo valor tf-idf calculado
    dict_keywords_paginas = {}

    # i: 0,1,...,len(lista_documentos); doc: se refiere al elemento actual de lista_documentos
    for i, doc in enumerate(lista_documentos):

        tfidf_value = tfidf_train[i]
        lista_tuplas = []

        # Este for construye la lista de tuplas con el n-grama y su respectivo valor tfidf calculado
        for col in tfidf_value.nonzero()[1]:
            lista_tuplas.append((feature_names[col], tfidf_value[0, col]))

        lista_tuplas.sort(key=lambda x: x[1], reverse=True)
        lista_tuplas = lista_tuplas[:30]

        # Crear el archivo .csv correspondiente al archivo .txt actual
        nombre_tupla = doc + '.csv'
        with open(nombre_tupla, 'w', newline='', encoding='utf-8') as file:
            writer = csv.writer(file, delimiter=';')
            writer.writerows(lista_tuplas)

        # Asociar en el diccionario al documento actual su lista de caracteristicas
        dict_keywords_paginas[doc] = lista_tuplas

    return dict_keywords_paginas


# Metodo encargado de leer las skipwords (definidas al principio del documento) de cada transcripción,
# Esto para lograr obtener buenos valores tf-idf
def read_skipwords(filename):
    skipwords = []
    with open(filename) as list_skipwords:
        lineas = list_skipwords.readlines()
        for linea in lineas:
            skipwords.append(linea.strip('\n'))
    return skipwords


# Metodo encargado de eliminar las skipwords (definidas al principio del documento) de cada transcripción,
# Esto para lograr obtener buenos valores tf-idf
def delete_skipwords(text, skipwords):
    for i in range(len(skipwords)):
        # Aquí se eliminan todas las skipwords del texto actual, una a una
        text = text.replace(skipwords[i], " ")

    return text


lista_de_documentos = obtener_archivos('.txt')
lista_transcripciones = obtener_textos(lista_de_documentos)
tfidf_train = tf_idf(lista_transcripciones)
feature_names = tfidfvectorizer.get_feature_names_out()
dict_tfidf_alldocuments = print_tfidf_values(lista_de_documentos, feature_names, tfidf_train)

# print(lista_de_documentos[34])
# print(dict_tfidf_alldocuments[lista_de_documentos[34]])
# print(tfidfvectorizer.vocabulary_)
