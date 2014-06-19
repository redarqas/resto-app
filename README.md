Présentation
==============
Il s'agit d'une petite web app. Le framework utilisé est Dropwizard.
Le rôle de l'application est de choisir aléatoirement un restaurant où manger le midi parmi une liste.

Prépartion 
-----------
Créer le livrable :

    mvn clean package 

Préparer la base de données :

    java -jar target/resto-app-1.0.jar db migrate conf/dev.yaml

Démmarer l'application 

    java -jar target/resto-app-1.0.jar server conf/dev.yaml

Services REST
--------
Créer un restaurant

    curl --header "Content-Type: application/json" -v -X POST -d '{"name": "resto1"}' http://localhost:8080/restaurants

Lister les restaurants

    curl http://localhost:8080/restaurants

Un restaurant au hasard 

    curl http://localhost:8080/restaurants/random

Supprimer un restaurant 

    curl  -X DELETE http://localhost:8080/restaurants/resto1

Une liste de noms de restaurants est passée en GET.
Cette méthode retourne de manière aléatoire l'un des restaurants de la liste.


Tests
--------

L'application couvre en test les DAO, les Resources, et le client HTTP
