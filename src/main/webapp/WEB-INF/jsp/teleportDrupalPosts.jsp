<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
    <head><title><fmt:message key="teleport.drupal.posts.title"/></title></head>
    <body>
    <p>
    <fmt:message key="teleport.drupal.posts.settings"/>
    </p>
    <p>
    <fmt:message key="teleport.drupal.posts.more.settings"/>
    </p>
    <p>
    <fmt:message key="teleport.drupal.posts.tumblr.settings"/>
    </p>
    <form:form method="post" commandName="crawlTeleport">
        <form:hidden path="preview"/>
        <form:label path="targetUrl"><fmt:message key="teleport.drupal.posts.label.targetUrl"/></form:label>
        <form:input path="targetUrl"/>
        <br/>
        <form:label path="articleUrlPrefix"><fmt:message key="teleport.drupal.posts.label.articleUrlPrefix"/></form:label>
        <form:input path="articleUrlPrefix"/>
        <br/>
        <form:label path="blogEntryStartComment"><fmt:message key="teleport.drupal.posts.label.blogEntryStartComment"/></form:label>
        <form:input path="blogEntryStartComment"/>
        <br/>
        <form:label path="blogEntryEndComment"><fmt:message key="teleport.drupal.posts.label.blogEntryEndComment"/></form:label>
        <form:input path="blogEntryEndComment"/>
        <br/>
        <form:label path="blogName"><fmt:message key="teleport.drupal.posts.label.blogname"/></form:label>
        <form:input path="blogName"/>
        <br/>
        <form:label path="tag"><fmt:message key="teleport.drupal.posts.label.tag"/></form:label>
        <form:input path="tag"/>
        <br/>
        <input name="submit" type="submit" value="<fmt:message key="teleport.drupal.posts.preview"/>" onClick="document.getElementById('removable').value=false"/>
        <c:if test="${!crawlTeleport.preview}">    
            <input name="submit" type="submit" value="<fmt:message key="teleport.drupal.posts.submit"/>" onClick="document.getElementById('removable').value=true"/>
        </c:if>  
    </form:form>
    </body>
    
    <c:if test="${!empty tumblrTextPosts}">
        <fmt:message key="teleport.drupal.posts.candidates"/>
        <ul>
        <c:forEach var="textPost" items="${tumblrTextPosts}">
            <li>${textPost.title}</li>
        </c:forEach>
        </ul>
    </c:if>
</html>
