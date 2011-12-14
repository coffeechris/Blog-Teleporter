Blog Teleporter
=============
The purpose of this project is to import blog posts from one platform to Tumblr easily. Currently in the works is importing posts from Drupal.
After that, who knows :-)

Building
-------------
The app is built using Maven. Almost all its dependencies can be pulled from remote repositories. Unfortunately, there is one dependency that is not available,
crawler4j. I have included this dependency in this git repo in the lib folder. You will have to install it in your local maven repo using the following
command:

    mvn install:install-file -Dfile=lib/crawler4j-2.6.1.jar -DgroupId=edu.uci.ics \
                             -DartifactId=crawler4j -Dversion=2.6.1 -Dpackaging=jar

I have also taken the liberty to include a copy of the dsiutils and fastutils jars in the lib directory in the event that the DSI maven repo goes away.
Similar to the crawler4j jar, in that situation, you will have to add those jars to your local maven repo using the following commands:

    mvn install:install-file -Dfile=lib/dsiutils-1.0.10.jar -DgroupId=it.unimi.dsi \
                             -DartifactId=dsiutils -Dversion=1.0.10 -Dpackaging=jar

    mvn install:install-file -Dfile=lib/fastutil-6.2.2.jar -DgroupId=it.unimi.dsi \
                             -DartifactId=fastutil -Dversion=6.2.2 -Dpackaging=jar

For development purposes, I have configured in the maven pom the jetty plugin so you will be able to start the Blog Teleporter web app using the following command:

    clean package jetty:run

If you are interested is creating a war that can be deployed to a Java application server, use this command:

    mvn package

