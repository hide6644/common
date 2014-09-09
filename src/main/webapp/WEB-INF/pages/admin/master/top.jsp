<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="admin.master.topForm.title" /></title>
    <meta name="menu" content="admin.topForm" />
</head>

<body class="home">
<h3><fmt:message key="admin.master.topForm.heading" /></h3>

<ul>
    <li>
        <a href="<c:url value="/admin/master/users" />"><fmt:message key="user" /></a>
    </li>
    <li>
        <a href="<c:url value="/admin/reload" />"><fmt:message key="admin.master.topForm.reload" /></a>
    </li>
</ul>

<ul>
    <li>
        <a href="<c:url value="/admin/top" />"><fmt:message key="button.cancel" /></a>
    </li>
</ul>
</body>
