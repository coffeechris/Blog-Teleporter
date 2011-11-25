<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>

<html>
    <head><title><fmt:message key="delete.posts.title"/></title></head>
    <body>
    <fmt:message key="delete.posts.intro"/>
    <form:form method="post" commandName="deletePosts">
        <form:hidden path="removable"/>
        <form:label path="blogName"><fmt:message key="delete.posts.label.blogname"/></form:label>
        <form:input path="blogName"/>
        <br/>
        <form:label path="tag"><fmt:message key="delete.posts.label.tag"/></form:label>
        <form:input path="tag"/>
        <br/>
        <input name="submit" type="submit" value="<fmt:message key="delete.posts.update"/>" onClick="document.getElementById('removable').value=false"/>
        <c:if test="${deletePosts.removable}">    
            <input name="submit" type="submit" value="<fmt:message key="delete.posts.submit"/>" onClick="document.getElementById('removable').value=true"/>
        </c:if>  
    </form:form>
    </body>
    
    <c:if test="${!empty tumblrTextPosts}">
        <fmt:message key="delete.posts.candidates"/>
        <ul>
        <c:forEach var="textPost" items="${tumblrTextPosts}">
            <li>${textPost.title}</li>
        </c:forEach>
        </ul>
    </c:if>
</html>
