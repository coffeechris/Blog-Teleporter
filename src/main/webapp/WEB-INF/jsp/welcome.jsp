<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
    <head><title><fmt:message key="welcome.title"/></title></head>
    <body>
        <p>
        <fmt:message key="welcome.intro"/>
        </p>
        <p>
        <fmt:message key="welcome.teleport.label"/>
        <a href="drupal_teleporter.htm"><fmt:message key="teleporter.drupal.title"/></a>
        </p>
        <p>
        <fmt:message key="welcome.info"/>
        <a href="https://github.com/cpjordan79/Blog-Teleporter"><fmt:message key="welcome.github.link"/></a>
        </p>
    </body>
</html>