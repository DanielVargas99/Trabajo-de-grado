import nltk
import gensim
import matplotlib.pyplot as plt
from gensim.models import Word2Vec
from gensim.models import KeyedVectors as kv
from gensim.scripts.glove2word2vec import glove2word2vec
from sklearn.manifold import TSNE

# Ruta en donde se encuentra el archivo .txt donde se almacena la transcripción de todas las paginas web
training_dataset = "Transcripciones/TranscripciónDeTodasLasPaginas.txt"

# Ruta en donde se encuentran todos los modelos de word embedding guardados:
path_models = "Word2Vec/"

'''
Definir los modelos de Word-Embedding que se utilizarán y cargarlos en la RAM
'''

# Diccionario que guarda la información de los modelos de word embedding:
embedding_path_dict = {'googlenews': {
                            'path': path_models+'GoogleNews-vectors-negative300.bin',
                            'format': 'word2vec',
                            'binary': True
                      },
                      'glove-300': {
                            'path': path_models+'glove.6B.300d.txt',
                            'format': 'glove',
                            'binary': False
                      },
                      'glove-200': {
                            'path': path_models+'glove.6B.200d.txt',
                            'format': 'glove',
                            'binary': False
                      },
                      'glove-100': {
                            'path': path_models+'glove.6B.100d.txt',
                            'format': 'glove',
                            'binary': False
                      },
                      'glove-50': {
                            'path': path_models+'glove.6B.50d.txt',
                            'format': 'glove',
                            'binary': False
                      },
                      'glove-300_word2vec': {
                            'path': path_models+'glove.6B.300d.word2vec.txt',
                            'format': 'word2vec',
                            'binary': False
                      },
                      'glove-200_word2vec': {
                            'path': path_models+'glove.6B.200d.word2vec.txt',
                            'format': 'word2vec',
                            'binary': False
                      },
                      'glove-100_word2vec': {
                            'path': path_models+'glove.6B.100d.word2vec.txt',
                            'format': 'word2vec',
                            'binary': False
                      },
                      'glove-50_word2vec': {
                            'path': path_models+'glove.6B.50d.word2vec.txt',
                            'format': 'word2vec',
                            'binary': False
                      },
                      'modelo_propio': {
                            'path': path_models+'ModeloPropio.bin',
                            'format': 'word2vec',
                            'binary': True
                      },
                    }


# Get word embeddings
def get_embeddings(embedding_dict, emb_name):
    """
    :params embedding_path_dict: a dictionary containing the path, binary flag, and format of the desired embedding,
            emb_name: the name of the embedding to retrieve
    :return embedding index: a dictionary containing the embeddings"""

    embeddings_index = {}

    if emb_name == 'googlenews':
        emb_path = embedding_dict[emb_name]['path']
        bin_flag = embedding_dict[emb_name]['binary']
        embeddings_index = kv.load_word2vec_format(emb_path, binary=bin_flag, limit=500000)
    elif emb_name == 'modelo_propio':
        emb_path = embedding_dict[emb_name]['path']
        embeddings_index = gensim.models.Word2Vec.load(emb_path)
    elif emb_name == 'glove-300':
        """
            Convert the GLOVE embedding format to a word2vec format
            :return output from the glove2word2vec script
            """
        glove_input_file = embedding_dict[emb_name]['path']
        word2vec_output_file = embedding_dict['glove-300_word2vec']['path']
        glove2word2vec(glove_input_file, word2vec_output_file)
        embeddings_index = get_embeddings(embedding_dict, 'glove-300_word2vec')
    elif emb_name == 'glove-200':
        glove_input_file = embedding_dict[emb_name]['path']
        word2vec_output_file = embedding_dict['glove-200_word2vec']['path']
        glove2word2vec(glove_input_file, word2vec_output_file)
        embeddings_index = get_embeddings(embedding_dict, 'glove-200_word2vec')
    elif emb_name == 'glove-100':
        glove_input_file = embedding_dict[emb_name]['path']
        word2vec_output_file = embedding_dict['glove-100_word2vec']['path']
        glove2word2vec(glove_input_file, word2vec_output_file)
        embeddings_index = get_embeddings(embedding_dict, 'glove-100_word2vec')
    elif emb_name == 'glove-50':
        glove_input_file = embedding_dict[emb_name]['path']
        word2vec_output_file = embedding_dict['glove-50_word2vec']['path']
        glove2word2vec(glove_input_file, word2vec_output_file)
        embeddings_index = get_embeddings(embedding_dict, 'glove-50_word2vec')
    elif emb_name == 'glove-300_word2vec' or emb_name == 'glove-200_word2vec' or emb_name == 'glove-100_word2vec'\
            or emb_name == 'glove-50_word2vec':
        emb_path = embedding_dict[emb_name]['path']
        bin_flag = embedding_dict[emb_name]['binary']
        embeddings_index = kv.load_word2vec_format(emb_path, binary=bin_flag)

    return embeddings_index


'''
Entrenamiento de un modelo propio basado en el vocabulario de las páginas web de las transcripciones
'''


def tokenizar(filename):
    file = open(filename, "r", encoding='utf-8').read()  # Abrir en modo "r" lectura
    # tokenizar el documento en oraciones
    sentences = nltk.sent_tokenize(file)
    # tokenizar cada oracion en palbras
    tokens = [nltk.tokenize.word_tokenize(sentence) for sentence in sentences]
    return tokens


def entrenar_modelo_propio(tokens):
    # Entrenar el modelo de Word2Vec basado en el vocabulario de las páginas web + Modelo GloVe 50d
    modelo_w2v = Word2Vec(window=6,
                          sentences=tokens,
                          vector_size=300,
                          min_count=2,
                          alpha=0.03,  # Initial learning rate
                          # min_alpha: Minimum value at which the learning rate will decrease linearly during training:
                          min_alpha=0.0007,
                          workers=2)
    # modelo_w2v.build_vocab(tokens, progress_per=10000)
    modelo_w2v.train(training_dataset, total_examples=modelo_w2v.corpus_count, epochs=modelo_w2v.epochs)
    # Guardar el modelo entrenado
    modelo_w2v.save(path_models+'ModeloPropio.bin')
    return modelo_w2v


'''
Graficar el modelo que se esté usando, cualquiera de los que estan en embedding_path_dict
'''


# Esta función se utiliza para graficar el vocabulario y poder comprobar de una manera más grafica como está
# estructurado el modelo que se le pasa como argumento
def tsne_plot(model):
    # Creates and TSNE model and plots it
    labels = []
    tokens = []
    vocab = list(model.wv.index_to_key)

    for word in vocab:
        tokens.append(model.wv[word])
        labels.append(word)

    tsne_model = TSNE(perplexity=40, n_components=2, init='pca', n_iter=2500, random_state=23)
    new_values = tsne_model.fit_transform(tokens)

    x = []
    y = []
    for value in new_values:
        x.append(value[0])
        y.append(value[1])

    plt.figure(figsize=(16, 16))
    for j in range(len(x)):
        plt.scatter(x[j], y[j])
        plt.annotate(labels[j],
                     xy=(x[j], y[j]),
                     xytext=(5, 2),
                     textcoords='offset points',
                     ha='right',
                     va='bottom')
    plt.show()


'''
Llamados a las funciones para entrenar el modelo propio y graficar.
Cambiar la variable embedding_name según el modelo de embedding que se desee utilizar
'''

# Entrenar el modelo propio (Si es la primera vez que se entrena, descomentar esto)
# word_tokens_modelo_propio = tokenizar(training_dataset)
# entrenar_modelo_propio(word_tokens_modelo_propio)

# Cambiar la variable embedding_name según el modelo de embedding que se desee utilizar
embedding_name = 'modelo_propio'
modelo = get_embeddings(embedding_path_dict, embedding_name)

# Descomentar la linea siguiente para graficar el espacio vectorial del modelo, No es recomendable
# ejecutar la función si el PC tiene poca RAM.
# tsne_plot(modelo)

'''
Pruebas de los modelos
'''

# Pruebas para el modelo propio (Solo aplican para este):

# Agregar aquí las palabras que se probarán en el modelo
pruebas_modelo_propio = ['beca', 'uk', 'colombia', 'deadline', 'master', 'study', 'scholarship', 'exchange', 'icetex',
                         'ciberseguridad', 'cybersecurity', 'game', 'iot', 'desarrollo', 'design']

for i in pruebas_modelo_propio:
    print(i + ": ", modelo.wv.most_similar(i))

print(modelo.wv.most_similar(positive=['beca', 'scholarships'], negative=['scholarship'], topn=1))
print(modelo.wv.similarity(w1="beca", w2="scholarship"), modelo.wv.similarity(w1="beca", w2="icetex"))

# Pruebas para los demás modelos, aplicable para el de google o los de glove:

# Agregar aquí las palabras que se probarán en el modelo
# pruebas_otros_modelos = ['germany', 'uk', 'canada', 'deadline', 'master', 'study', 'scholarship',
#                          'deadline', 'beca', 'colombia', 'intercambio']
#
# for i in pruebas_otros_modelos:
#     print(i + ": ", modelo.most_similar(i))
#
# print(modelo.most_similar(positive=['king', 'woman'], negative=['man'], topn=1))
# print(modelo.most_similar(positive=['beca', 'scholarships'], negative=['scholarship'], topn=1))
# print(modelo.similarity(w1="beca", w2="scholarship"))
