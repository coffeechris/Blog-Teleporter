Blog Teleporter
=============
The purpose of this project is to import blog posts from one platform to Tumblr easily. Currently in the works is importing posts from Drupal.
After that, who knows :-)

Building
-------------
The app is built using Maven. Almost all its dependencies can be pulled from remote repositories. Unfortunately, there is one dependency that is available,
crawler4j. I have included this dependency in this git repo in the lib folder. You will have to install it in your local maven repo using the following
command: