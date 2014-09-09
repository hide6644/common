<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="admin.topForm.title" /></title>
    <meta name="menu" content="admin.topForm" />
</head>

<body class="home">
<h3><fmt:message key="admin.topForm.heading" /></h3>

<ul>
    <li>
        <a href="<c:url value="/admin/master/top" />"><fmt:message key="admin.master.topForm" /></a>
    </li>
    <li>
        <a href="<c:url value="/admin/activeUsers" />"><fmt:message key="activeUsersForm" /></a>
    </li>
</ul>

<ul>
    <li>
        <a href="<c:url value="/top" />"><fmt:message key="button.cancel" /></a>
    </li>
</ul>
</body>
