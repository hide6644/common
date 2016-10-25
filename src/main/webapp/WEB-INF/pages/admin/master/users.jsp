<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="userForm.title" /></title>
    <meta name="menu" content="admin" />
    <meta name="navlistMenu" content="userForm" />
</head>

<div class="col-sm-10">
    <h3><fmt:message key="userForm.heading" /></h3>

<form:form modelAttribute="searchUser" action="users" method="get" class="form-inline">
    <input type="text" size="20" name="username" id="username" value="${searchUser.username}" placeholder="<fmt:message key="user.username" />" class="form-control input-sm">
    <input type="text" size="20" name="email" id="email" value="${searchUser.email}" placeholder="<fmt:message key="user.email" />" class="form-control input-sm">
    <button type="submit" class="btn btn-primary btn-sm">
        <i class="icon-search"></i> <fmt:message key="button.search" />
    </button>
    <button type="button" class="btn btn-default btn-sm" id="button_clear">
        <i class="icon-remove"></i> <fmt:message key="button.clear" />
    </button>
</form:form>

<%@ include file="/includes/pagelinks.jsp"%>

<form:form action="users" method="delete">
<c:if test="${paginatedList.currentPage != null && fn:length(paginatedList.currentPage) > 0}">
<table class="table table-condensed table-striped table-hover">
<thead>
    <tr>
        <th><input type="checkbox" id="toggle_checkAll" /></th>
        <th><fmt:message key="user.username" /></th>
        <th><fmt:message key="user.email" /></th>
        <th><fmt:message key="user.enabled" /></th>
    </tr>
</thead>
<tbody>
<c:forEach var="user" items="${paginatedList.currentPage}">
    <tr>
        <td><input type="checkbox" name="userIds" value="${user.id}" /></td>
        <td>${user.username}</td>
        <td>${user.email}</td>
        <td><input type="checkbox" disabled="disabled" <c:if test="${user.enabled}">checked="checked"</c:if> /></td>
    </tr>
</c:forEach>
</tbody>
</table>
</c:if>
    <div class="form-group">
        <p>
        <button type="button" class="btn btn-primary" id="button_new" value="<c:url value="/user" />?method=Add&from=list">
            <i class="icon-plus"></i> <fmt:message key="button.add" />
        </button>
        <button type="button" class="btn btn-primary" id="button_edit" value="<c:url value="/user" />?from=list&userId=">
            <i class="icon-pencil"></i> <fmt:message key="button.edit" />
        </button>
        <button type="submit" class="btn btn-warning" id="button_delete">
            <i class="icon-minus"></i> <fmt:message key="button.delete" />
        </button>
        <button type="button" class="btn btn-default" id="button_cancel">
            <i class="icon-remove"></i> <fmt:message key="button.cancel" />
        </button>
        </p>
        <p>
        <button type="button" class="btn btn-default" id="button_upload">
            <i class="icon-arrow-up"></i> <fmt:message key="button.upload" />
        </button>
        <button type="button" class="btn btn-default" data-toggle="modal" data-target="#downloadModal" id="button_download">
            <i class="icon-arrow-down"></i> <fmt:message key="button.download" />
        </button>
        </p>
    </div>
    <div id="downloadModal" class="modal" role="dialog" aria-labelledby="downloadModalLabel" tabindex="-1">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                    <h4 class="modal-title" id="downloadModalLabel"><fmt:message key="fileType" /></h4>
                </div>
                <div class="modal-body">
                    <fmt:message key="fileType.message" />
                </div>
                <div class="modal-footer">
                    <a href="#" class="btn btn-default" id="dialog_download_xml">
                        <fmt:message key="fileType.xml" /><fmt:message key="button.download" />
                    </a>
                    <a href="#" class="btn btn-default" id="dialog_download_xls">
                        <fmt:message key="fileType.xls" /><fmt:message key="button.download" />
                    </a>
                    <a href="#" class="btn btn-default" id="dialog_download_csv">
                        <fmt:message key="fileType.csv" /><fmt:message key="button.download" />
                    </a>
                </div>
            </div>
        </div>
    </div>
</form:form>
</div>

<c:set var="scripts" scope="request">
<script type="text/javascript">
<!--
$(function() {
    $('#button_clear').click(function() {
        $('#searchUser').find('input').attr('value', '');
        $('#searchUser').submit();
    });

    $('#button_cancel').click(function() {
        $(location).attr('href', '<c:url value="/admin/master/top" />');
    });

    $('#button_upload').click(function() {
        $(location).attr('href', '<c:url value="/admin/master/uploadUsers" />');
    });

    $('#dialog_download_csv').click(function() {
        $(location).attr('href', '<c:url value="/admin/master/users.csv" />');
    });

    $('#dialog_download_xls').click(function() {
        $(location).attr('href', '<c:url value="/admin/master/users.xlsx" />');
    });

    $('#dialog_download_xml').click(function() {
        $(location).attr('href', '<c:url value="/admin/master/users.xml" />');
    });
});
//-->
</script>
</c:set>
