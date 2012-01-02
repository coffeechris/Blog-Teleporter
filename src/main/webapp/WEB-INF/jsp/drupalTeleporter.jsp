<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
    <head><title><fmt:message key="teleporter.drupal.title"/></title></head>
    <body>
        <p>
        <fmt:message key="teleporter.drupal.import.intro"/><br />
        <a href="teleport_drupal_posts.htm"><fmt:message key="teleport.drupal.posts.title"/></a>
        </p>
        <p>
        <fmt:message key="teleporter.tumblr.delete.intro"/><br />
        <a href="delete_tumblr_posts.htm"><fmt:message key="delete.tumblr.posts.title"/></a>
        </p>
    </body>
</html>
