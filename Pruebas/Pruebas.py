# Archivo encargado de hacer diferentes tipos de pruebas de presición al sistema de recomendación, no afecta
# en absoluto al servidor api-rest ni al modelo de caracterización de becas

import csv
import os
import psycopg2
import pandas as pd
import numpy as np
from nltk import ngrams
from scipy import sparse
from googletrans import Translator
from sklearn.metrics.pairwise import cosine_similarity

# En esta ruta se guardan los archivos .txt con las transcripciones de cada página
path = "Transcripciones/"

# En esta ruta se guardan los archivos necesarios para hacer pruebas al recomendador
path_pruebas = "Pruebas/"

# Ubicación del archivo csv que contiene un resumen de cada página web
resumen = path + 'resumenes.csv'

# Definir un objeto de la libreria googletrans
traductor = Translator()

'''
Las siguientes lineas de código corresponden al mismo código del recomendador, que se encuentra en el servidor
en la ruta principal (/), aquí no se incliyen las funciones de limpieza de busqueda ni busquedas relacionadas.
'''

# Función que permite conectarse a la base de datos alojada en Heroku
def conexionBD():
    try:
        conexion = psycopg2.connect(host="ec2-52-203-74-38.compute-1.amazonaws.com", user="nqbtcbwoqhjisp", 
            password="715efa6d7d856275fc6c0b52db0961a9d24f0d9f5a7f7da77d98a2ddbcbd8323", database="d95lni663n81s0")
    except Exception as err: print("Error al crear conexion", err)
    else: print("Conexion creada correctamente")

    cursor_bd = conexion.cursor()
    return cursor_bd

# Crea un objeto de tipo cursor que apunta a la BD y ejecuta un Query, para hacer la consulta a la BD
def consultas(Query, modo): # Modo 0: fetchone, Modo 1: fetchall
    cursor = conexionBD()
    cursor.execute(Query)
    if modo == 0: consulta = cursor.fetchone()
    else: consulta = cursor.fetchall()
    cursor.close()
    return consulta

# Función encargada de retornar una lista con las ubicaciones/rutas de todos los archivos .csv
def obtener_archivos(path, extension):

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

def buscar_palabra_en_lista_csv(lista_csvs, text):

    lista_aux = []

    for i in range(len(lista_csvs)):
        cont = 0
        tfidf = 0.0
        with open(lista_csvs[i], encoding='utf-8') as p: # Se abre el csv asignado en la posición i
            reader = csv.reader(p, delimiter=';') # Se lee el contenido del csv
            for row in reader: # Se lee fila por fila
                for j in range(len(text)): # Se recorre la lista de los textos que trae la busqueda
                    if row[0] == text[j]:  # row[0] la primera columna del csv
                        cont += 1 # Contador de la cantidad de palabras de la busqueda que aparecen en el csv
                        # Se hace una sumatoria del valor TFIDF cuando una fila del csv es igual a uno de los
                        # terminos de la busqueda:
                        tfidf += float(row[1])
        lista_aux.append((lista_csvs[i], cont, tfidf))

    return lista_aux

# A diferencia del código original, aquí no se retorna un JSON con las becas, sino que, se imprimen por consola
# Ya que para hacer las pruebas no se ve necesario retornar un JSON.
def imprimir_resultados(lista_ordenada, cant_resulados):
    lista_aux = []
    beca_seleccionada = {}
    for i in range(cant_resulados):  # Tomar solo los primeros 20 elementos de la lista
        with open(resumen, encoding='utf-8') as a: # Se abre el archivo que contiene los resumenes de las becas
            reader = csv.reader(a, delimiter=';')
            for row in reader:
                if row[0] == lista_ordenada[i][0]:
                    beca_seleccionada = {
                        "Documento": row[0],
                        "Nombre": row[1],
                        "Lugar": row[2],
                        "Tipo de estudio": row[3],
                        "Entidad que ofrece la beca": row[4],
                        "Descripcion": row[5],
                        "Enlace de la convocatoria": row[6]
                    }
        lista_aux.append(beca_seleccionada)

    return lista_aux

# Este metodo retorna una lista con unigramas, bigramas y trigramas basados en la busqueda realizada por el usuario
def crear_ngrams_busqueda(text):
    unigrams = ngrams(text.split(), 1)
    bigrams = ngrams(text.split(), 2)
    trigrams = ngrams(text.split(), 3)

    unigrams = [' '.join(grams) for grams in unigrams]
    bigrams = [' '.join(grams) for grams in bigrams]
    trigrams = [' '.join(grams) for grams in trigrams]

    text = unigrams + bigrams + trigrams
    return text

# Retorna los intereses del usuario que se le envíe
def consultaUser(correo):       
    intereses = consultas("SELECT interes1, interes2, interes3, interes4, interes5 FROM usuarios WHERE correo = " + correo, 0)
    consulta = [intereses[0], intereses[1], intereses[2], intereses[3], intereses[4]]
    return consulta

def traducir(busqueda):

    text_en = text_sp = ""
    for i in range(len(busqueda)):
        language = traductor.detect(busqueda[i])

        if language.lang == "en":
            text_en += busqueda[i] + " "

            if busqueda[i] == "master":
                busqueda[i] = "master's degree"

            # Traducir la busqueda a español 
            text_sp += traductor.translate(busqueda[i], dest='es').text + " "
            
        else: # En caso de que el idioma de la busqueda no sea ingles, admite cualquier idioma
            text_sp += traductor.translate(busqueda[i], dest='es').text + " "

            if busqueda[i] == "maestria":
                text_en += "master" + " "
            elif busqueda[i] == "juego":
                text_en += "game" + " "
            else:
                text_en += traductor.translate(busqueda[i]).text + " " # Traducir la busqueda a ingles

    return (text_en, text_sp)

def definir_busqueda(correo):

    # A diferencia del código original, aquí solo necesito recibir los intereses del usuario para la busqueda,
    # no recibo otros tipos de busqueda
    busqueda = consultaUser(correo)
    for i in range(len(busqueda)):
        if busqueda[i] == None:
            busqueda[i] = "Example" # En caso de que un interes esté vacio, para que no de error

    return busqueda

'''
Fin de las funciones del recomendador, inicio de las funciones de las pruebas
'''

# Retorna los correos de todos los usuarios de la BD
def consultaCorreos():
    correos = consultas("SELECT correo FROM usuarios", 1)
    consulta = []
    for correoActual in correos:
        consulta.append(correoActual[0])
    print(consulta)
    return consulta

# listaRecomendaciones: Retorna la lista ordenada de las recomendaciones que hace el programa a todos los usuarios
# existentes en la BD. Para cada usuario se hace todo el proceso de recomendación de intereses,
# luego se retorna la lista de recomendaciones resultante
def listaRecomendaciones(listaCorreos):
    recomendaciones_usuarios = []
    for correo in listaCorreos:
        predictions_current_user = main(correo)
        recomendaciones_usuarios.append(predictions_current_user[:20])
    return recomendaciones_usuarios

# matrizPredicciones: Recibe el archivo csv que contiene la información del tipo de estudio de cada una de las becas
# y la lista de recomendaciones hechas para un (1) solo usuario; lee linea por linea el csv y retorna la matriz
# de tipo de estudios por cada beca recomendada al usuario.
def matrizPredicciones(feature_csv, lista_ordenada):
    predicciones_usuario = []
    for i in range(len(lista_ordenada)):  # Tomar solo los primeros 20 elementos de la lista
        with open(feature_csv, encoding='utf-8') as a: # Se abre el archivo que contiene los tipos de estudio
            reader = csv.reader(a, delimiter=';')
            for row in reader:
                if row[0] == lista_ordenada[i][0]:
                    predicciones_usuario.append([row[1],row[2],row[3],row[4],row[5],row[6],row[7]])
    predicciones_usuario = np.array(predicciones_usuario)
    return predicciones_usuario

# Calcular la similaridad coseno de la matriz de tipos de estudios de las becas recomendadas a un (1) usuario
def cosineSimilarity(matrix):
    matrix_sparse = sparse.csr_matrix(matrix,dtype=float)
    similarities = cosine_similarity(matrix_sparse)
    #print('pairwise dense output:\n {}\n'.format(similarities))
    return similarities

# Aquí se calcula la media de los valores que se encuentran en el triangulo superior de la matriz coseno
def averageUpperTriangleCosineSimilarity(matrix):
    sum = cantElementos = 0
    for i in range(len(matrix)-1):
        for j in range(i+1, len(matrix[0])):
            sum += matrix[i][j]
            cantElementos += 1
    return sum/cantElementos

# Hacer el proceso de calculo de la similaridad coseno para cada usuario de la base de datos, retorna el valor
# promedio de similaridad dentro de la lista de todos los usuarios, es decir, del recomendador
def intraListSimilarityAllUsers(listaRecomendacionesAllUsers):
    sum = cantElementos = 0
    for i in range(len(listaRecomendacionesAllUsers)): # Para cada usuario
        predictionsMatrix = matrizPredicciones(path_pruebas+"tipoEstudios.csv",listaRecomendacionesAllUsers[i])
        matrizCoseno = cosineSimilarity(predictionsMatrix)
        similaridadUsuarioActual = averageUpperTriangleCosineSimilarity(matrizCoseno)
        print("Similaridad usuario " + str(i) + ": ", similaridadUsuarioActual)
        sum += similaridadUsuarioActual
        cantElementos += 1
    return sum/cantElementos

# Aquí se encapsularon los llamados a todas las funciones que se encargan de hacer la recomendacion de becas a
# un (1) solo usuario, recibe como parametro el correo del usuario y retorna una lista de recomendaciones basadas
# en sus intereses, es muy parecido al código original
def main(correo):
    busqueda = definir_busqueda("'" + correo + "'")
    traduccion = traducir(busqueda) # Se manda la busqueda a traducir y asi tenerla en ambos idiomas
    csvs = obtener_archivos(path, '.csv')  # Obtener una lista con las ubicaciones de todos los archivos CSV
    busqueda = crear_ngrams_busqueda(traduccion[0] + " " + traduccion[1]) # Separar la busqueda en unigramas, bigramas y trigramas
    resultados_busqueda = buscar_palabra_en_lista_csv(csvs, busqueda) # Obtener los documentos que más se ajusten a la busqueda
    sorted_list = sorted(resultados_busqueda, key=lambda aux: (aux[1], aux[2]), reverse=True) # Ordenar la lista de mayor a menor
    #print(imprimir_resultados(sorted_list,20))
    return sorted_list

listaCorreos = consultaCorreos()
listaRecomendacionesUsuarios = listaRecomendaciones(listaCorreos)
intraListSimilarityRecommenderSystem = intraListSimilarityAllUsers(listaRecomendacionesUsuarios)
print("Similaridad todos los usuarios: ", intraListSimilarityRecommenderSystem)