<%@ include file="/includes/taglibs.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <decorator:head />
    <title><decorator:title /> | <fmt:message key="webapp.name" /></title>

    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/lib/bootstrap.css' />" />
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/lib/bootstrap-responsive.css' />" />
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/style.css' />" />

    <script type="text/javascript" src="<c:url value='/scripts/lib/jquery.js' />"></script>
    <script type="text/javascript" src="<c:url value='/scripts/lib/bootstrap.js' />"></script>
    <script type="text/javascript" src="<c:url value='/scripts/lib/plugins/jquery.cookie.js' />"></script>
    <script type="text/javascript" src="<c:url value='/scripts/script.jsp' />"></script>

    <link rel="stylesheet" type="text/css" href="<c:url value='/scripts/datepicker/css/datepicker.css' />" />
    <link rel="stylesheet" type="text/css" href="<c:url value='/scripts/datepicker/less/datepicker.less' />" />
    <script type="text/javascript" src="<c:url value='/scripts/datepicker/js/bootstrap-datepicker.js' />"></script>
    <script type="text/javascript" src="<c:url value='/scripts/datepicker/js/locales/bootstrap-datepicker.ja.js' />"></script>

</head>
<body<decorator:getProperty property="body.id" writeEntireProperty="true" /><decorator:getProperty property="body.class" writeEntireProperty="true" />>
    <c:set var="currentMenu" scope="request"><decorator:getProperty property="meta.menu" /></c:set>

    <div class="navbar navbar-fixed-top">
        <div class="navbar-inner">
            <div class="container-fluid">
                <%-- For smartphones and smaller screens --%>
                <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                    <span class="icon-bar"></span>
                </button>
                <a class="brand" href="<c:url value='/' />"><fmt:message key="webapp.name" /></a>
<%@ include file="/includes/menu.jsp" %>
            </div>
        </div>
    </div>
    <div class="container-fluid">
<%@ include file="/includes/messages.jsp" %>
        <div class="row-fluid">
<decorator:body />
        </div>
    </div>
    <div id="footer">
        <span class="left"><fmt:message key="webapp.version"/>
            <c:if test="${pageContext.request.remoteUser != null}">
            | <fmt:message key="user.status" /> ${pageContext.request.remoteUser}
            </c:if>
        </span>
        <span class="right">
            &copy; <fmt:message key="copyright.year" /> <a href="<fmt:message key="company.url" />"><fmt:message key="company.name" /></a>
        </span>
    </div>
<%= (request.getAttribute("scripts") != null) ?  request.getAttribute("scripts") : "" %>
</body>
</html>
