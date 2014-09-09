<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>

<html>
<head>
    <title><fmt:message key="error.title" /> | <fmt:message key="webapp.name" /></title>
</head>
<body id="error">
    <div class="container">
        <h1><fmt:message key="error.heading" /></h1>
        <%@ include file="/includes/messages.jsp" %>
    </div>
</body>
</html>
