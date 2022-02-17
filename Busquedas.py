import csv
import unidecode
import os
import spacy
import re
import psycopg2
import nltk
from nltk import ngrams
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
from flask import Flask, jsonify, request
from googletrans import Translator

app = Flask(__name__)

@app.route('/', methods=['POST'])
def principal():

    # Definir un objeto de la libreria googletrans
    traductor = Translator()

    # Cambiar la ruta para cada caso, en esta ruta se guardan los archivos .txt con las transcripciones de cada página
    path = "Transcripciones/"

    # Ubicación del archivo csv que contiene un resumen de cada página web
    resumen = path + 'resumenes.csv'

    # Crear una lista de las palabras "Stopwords" que se eliminarán del texto
    stop_words_sp = set(stopwords.words('spanish'))
    stop_words_en = set(stopwords.words('english'))

    # Concatenar las stopwords
    stop_words = stop_words_sp | stop_words_en

    # Definir un lematizador para el idioma inglés
    lemmatizer_en = WordNetLemmatizer()

    # Esto es para el lematizador en ingles
    nltk.download('wordnet')

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

    def imprimir_resultados(lista_ordenada):
        lista_aux = []
        beca_seleccionada = {}
        for i in range(20):  # Tomar solo los primeros 20 elementos de la lista
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

    def limpieza_busqueda(text_en, text_sp):
        text_en = text_en.lower()
        text_sp = text_sp.lower()

        text_en = eliminar_simbolos(text_en)
        text_sp = eliminar_simbolos(text_sp)

        text_en = re.sub(r"[0-9]+", "", text_en)  # Eliminar cualquier numero del texto
        text_sp = re.sub(r"[0-9]+", "", text_sp)

        # A continuación se lematiza la busqueda en ambos idiomas y se concatenan los textos:
        text = lemmatize_words_en(text_en) + " " + lemmatize_words_sp(text_sp)
        text = unidecode.unidecode(text)  # Eliminar cualquier tilde, dieresis o "ñ"
        text = eliminar_stopwords(text)
        return text

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

    def eliminar_simbolos(text):
        simbolosparaborrar = "¡!#$€£¢¥%&'\"()*+,-./:;<=>¿?@[\]^_`{|}~“”‘’—–®©ⓒ»ªº™⭐♦※"
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

    def eliminar_stopwords(text):
        return ' '.join([word for word in text.split(' ') if word not in stop_words])

    # Función que permite conectarse a la base de datos alojada en Heroku
    def conexionBD():
        try:
            conexion = psycopg2.connect(host="ec2-52-203-74-38.compute-1.amazonaws.com", user="nqbtcbwoqhjisp", 
                password="715efa6d7d856275fc6c0b52db0961a9d24f0d9f5a7f7da77d98a2ddbcbd8323", database="d95lni663n81s0")
        except Exception as err:
            print("Error al crear conexion", err)
        else:
            print("Conexion creada correctamente")

        cursor_bd = conexion.cursor()

        return cursor_bd

    # Crea un objeto de tipo cursor que apunta a la BD y ejecuta un Query, para hacer la consulta a la BD
    def consultas(Query):
        cursor = conexionBD()

        cursor.execute(Query)
        consulta = cursor.fetchone()

        cursor.close()

        return consulta

    # Retorna los intereses del usuario que se le envíe
    def consultaUser(correo):
        
        intereses = consultas("SELECT interes1, interes2, interes3, interes4, interes5 FROM usuarios WHERE correo = " + correo)
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

    def definir_busqueda():

        busqueda = ""

        if request.json['BANDERA'] == 'u':
            # Aquí llega el usuario mediante un request POST, obtengo la lista de los intereses
            busqueda = consultaUser("'" + request.json['USUARIO'] + "'")
            for i in range(len(busqueda)):
                if busqueda[i] == None: busqueda.pop(i) # En caso de que un interes esté vacio, se elimina
        else:
            busqueda = [request.json['BUSQUEDA']]

        return busqueda


    busqueda = definir_busqueda()
    traduccion = traducir(busqueda) # Se manda la busqueda a traducir y asi tenerla en ambos idiomas
    csvs = obtener_archivos('.csv')  # Obtener una lista con las ubicaciones de todos los archivos CSV
    busqueda = limpieza_busqueda(traduccion[0], traduccion[1])  # Hacer limpieza a la busqueda que ingrese el usuario
    busqueda = crear_ngrams_busqueda(busqueda)  # Separar la busqueda en unigramas, bigramas y trigramas
    resultados_busqueda = buscar_palabra_en_lista_csv(csvs, busqueda)  # Obtener los documentos que más se ajusten a la busqueda
    sorted_list = sorted(resultados_busqueda, key=lambda aux: (aux[1], aux[2]), reverse=True)  # Ordenar la lista de mayor a menor

    return jsonify({"BECAS": imprimir_resultados(sorted_list)})

if __name__ == '__main__':
    app.debug = True
    app.run()