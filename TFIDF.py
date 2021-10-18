from sklearn.feature_extraction.text import TfidfVectorizer
import os

# Cambiar la ruta para cada caso, en esta ruta se guardan los archivos .txt con las transcripciones de cada página
path = "Transcripciones/"

tfidfvectorizer = TfidfVectorizer(stop_words={'english'}, max_df=0.9, min_df=0.02)


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


def obtener_textos(lista_documentos):
    lista_textos = []
    for doc in lista_documentos:
        lista_textos.append(open(doc, mode='r', encoding='utf-8').read())
    return lista_textos


def tf_idf(lista_textos):
    tfidfvectorizer.fit(lista_textos)
    tfidf_train_set = []
    for doc in lista_textos:
        tfidf_train_set.append(tfidfvectorizer.transform([doc]))
    return tfidf_train_set


def print_tfidf_values(lista_documentos, feature_names, tfidf_train):
    dict_keywords_paginas = {}
    for i, doc in enumerate(lista_documentos):
        tfidf_value = tfidf_train[i]
        lista_tuplas = []
        for col in tfidf_value.nonzero()[1]:
            lista_tuplas.append((feature_names[col], tfidf_value[0, col]))
        dict_keywords_paginas[doc] = lista_tuplas
    return dict_keywords_paginas


lista_de_documentos = obtener_archivos_txt()
lista_transcripciones = obtener_textos(lista_de_documentos)
tfidf_train = tf_idf(lista_transcripciones)
feature_names = tfidfvectorizer.get_feature_names_out()
dict_tfidf_alldocuments = print_tfidf_values(lista_de_documentos, feature_names, tfidf_train)

print(dict_tfidf_alldocuments['Transcripciones/appspublic.agci.clbecas\\PDF8.txt'])
