## Pour commencer, il faut ouvrir docker et lancer la commande suivante dans un cmd : `docker run -p 8500:8500 consul:1.9.13`

# Partie Banque

## Lancement :

Il faut se rendre dans le dossier "Banque" et "target". Il suffit d'ouvrir un cmd dans ce dossier et de lancer le programme avec : `java -jar Banque-0.0.1-SNAPSHOT.jar`
L'application se lance sur le port 8082.

## Accéder a la doc : 
On peut accéder a la doc via ce lien : http://127.0.0.1:8082/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config

## Accéder par http : 

On peut accéder par le navigateur via : http://127.0.0.1:8082/clients

## Tester avec postman : 

Vous pouvez accéder au fichier "Banque-postman.json" qui contient les différentes requêtes http pour tester l'application banque.

# Partie Commerçant

## Lancement :

Il faut se rendre dans le dossier "Banque" et "target". Il suffit d'ouvrir un cmd dans ce dossier et de lancer le programme avec : `java -jar Commercant-0.0.1-SNAPSHOT.jar`
L'application se lance sur le port 8098.

## Tester avec postman : 

Vous pouvez accéder au fichier "Commercant-postman.json" qui contient les différentes requêtes http pour tester l'application commerçant.
