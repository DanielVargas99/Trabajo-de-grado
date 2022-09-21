## Español

Este es un proyecto de software realizado en Python para las diferentes actividades de nuestro trabajo de grado, requisito para optar por el titulo de Ingeniero de Sistemas, se trata de un sistema de recomendación y difusión de becas de estudios para colombianos orientadas en ciencias de la computación, consta de un backend hecho en Python usando la librería Flask; y un frontend hecho en Android Studio. La aplicación funciona para cualquier persona que tenga el archivo .apk y que tenga conexión a internet.

Base de datos:
Esta hecha en PostgreSQL y alojada en el servidor web de Heroku, en este mismo servidor se encuentra alojado y funcionando el servidor API-REST hecho en Python, el cliente móvil puede hacer peticiones a este servidor en cualquier momento y cualquier lugar, siempre y cuando tenga conexión a internet, puedes encontrar el link al servidor de Heroku en este enlace: https://dashboard.heroku.com/apps/trabajo-de-grado-2022

Backend:
En el código se encuentra un Web Scrapper que utiliza la librería Beautiful Soup de Python, en el cual, mediante una lista de paginas web de becas, se recoge el texto de cada beca de la lista, y este texto lo guarda en archivos txt. También se encuentra un OCR que analiza el contenido de archivos PDF y extrae todo su texto, sin importar si es un PDF escaneado o una imagen.

A todos los documentos de texto las becas se les aplican técnicas de procesamiento de lenguaje natural, tecnicas de limpieza, filtrado, TF-IDF, Word Embedding y otras, para posteriormente crear el algoritmo de recomendación y retornar en un JSON la lista de becas recomendadas basadas en el perfil e intereses del usuario.

Puedes encontrar el archivo apk aquí: https://drive.google.com/file/d/1CkZ6wmEvRiLAawtfHKdwB7dSYnQdGME4/view?usp=sharing

Cualquier inquietud, puedes contactar a los desarrolladores:
- Daniel Vargas Cano <daniel.vargas3128@hotmail.com>
- Carlos Andres Jimenez: <carlosandresborussiadortmund@gmail.com>

## English

This is a software project developed in Python for the different activities of our degree work, a requirement to opt for the Systems Engineer titulation, it is a recommendation and diffusion system of scholarships for Colombians oriented in computer sciences, it consists of a backend developed in Python using the Flask library; and a frontend developed in Android Studio. The app works for anyone who has the .APK file and an internet connection.

Database:
It is made in PostgreSQL and hosted on the Heroku web server, the API-REST server made in Python is hosted and running on this same server, the mobile client can make requests to this server at any time and anywhere, as long as you have an internet connection, you can find the link to the Heroku server here: https://dashboard.heroku.com/apps/trabajo-de-grado-2022

Backend:
In the code there is a Web Scrapper that uses the Beautiful Soup Python library, in which, through a list of scholarship web pages, the text of each scholarship on the list is collected, and this text is saved in txt files. There is also an OCR that analyzes the content of PDF files and extracts all of its text, regardless of whether it is a scanned PDF or an image.
Natural language processing techniques, cleaning techniques, filtering, TF-IDF, Word Embedding and others are applied to all the scholarship text documents, to subsequently create the recommendation algorithm and return the list of recommended scholarships in a JSON, based on the profile and interests of the user.

You can find the apk file here: https://drive.google.com/file/d/1CkZ6wmEvRiLAawtfHKdwB7dSYnQdGME4/view?usp=sharing

Any questions, you can contact the developers:
- Daniel Vargas Cano <daniel.vargas3128@hotmail.com>
- Carlos Andres Jimenez: <carlosandresborussiadortmund@gmail.com>
