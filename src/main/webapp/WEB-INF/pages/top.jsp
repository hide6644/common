<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="topForm.title" /></title>
    <meta name="menu" content="topForm" />
</head>

<body class="home">
<h3><fmt:message key="topForm.heading" /></h3>

<sec:authorize access="isAuthenticated()">
<ul>
    <li>
        <a href="<c:url value="/user" />"><fmt:message key="user" /><fmt:message key="button.edit" /></a>
    </li>
</ul>
<custom:constants/>
<sec:authorize access="hasRole('ROLE_ADMIN')">
<ul>
    <li>
        <a href="<c:url value="/admin/top" />"><fmt:message key="admin.topForm" /></a>
    </li>
</ul>
</sec:authorize>
<ul>
    <li>
        <a href="<c:url value="/logout" />"><fmt:message key="topForm.logout" /></a>
    </li>
</ul>
</sec:authorize>
</body>
