<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="userUploadForm.title" /></title>
    <meta name="menu" content="userForm" />
    <meta name="navlistMenu" content="userForm" />
</head>

<div class="col-sm-10">
    <fieldset id="legend">
        <legend><fmt:message key="userUploadForm.heading" /></legend>
    </fieldset>
<form:form modelAttribute="uploadForm" method="post" enctype="multipart/form-data" autocomplete="off" cssClass="form-inline">
    <label for="fileType" class="control-label">
        <fmt:message key="uploadForm.fileType" />
    </label>
    <form:radiobuttons path="fileType" items="${fileTypeList}" itemLabel="label" itemValue="value" />
    <input type="file" name="fileData" style="display:none" />
<spring:bind path="uploadForm.fileData">
    <div class="control-group${(not empty status.errorMessage) ? ' error' : ''}">
        <label for="fileData" class="control-label">
            <fmt:message key="uploadForm.fileData" /> <span class="required">*</span>
        </label>
        <div class="controls">
            <div class="input-append">
                <input type="text" id="fileData" readonly="readonly" class="input-medium" />
                <button type="button" class="btn btn-default" id="folderOpen">
                    <i class="icon-folder-open"></i> <fmt:message key="uploadForm.folderOpen" />
                </button>
            </div>
            <form:errors path="fileData" cssClass="help-inline" />
        </div>
    </div>
</spring:bind>
    <fieldset class="form-actions">
        <button type="submit" class="btn btn-primary">
            <i class="icon-ok icon-white"></i> <fmt:message key="button.upload" />
        </button>
        <button type="button" class="btn btn-default" id="button_cancel">
            <i class="icon-remove"></i> <fmt:message key="button.cancel" />
        </button>
    </fieldset>
</form:form>
<c:if test="${not empty errors_upload_list}">
<br />
<c:forEach var="error" items="${errors_upload_list}">
<div class="alert alert-danger alert-dismissable">
    <a href="#" data-dismiss="alert" class="close">&times;</a>
    <c:out value="${error}" />
</div>
</c:forEach>
</c:if>
</div>

<c:set var="scripts" scope="request">
<script type="text/javascript">
<!--
$(function() {
    $('#fileData').click(function() {
        $('input[name=fileData]').click();
    });

    $('#folderOpen').click(function() {
        $('input[name=fileData]').click();
    });

    $('input[name=fileData]').change(function() {
        $('#fileData').val($(this).val());
    });

    $('#button_cancel').click(function() {
        $(location).attr('href', '<c:url value="/admin/master/users" />');
    });
});
//-->
</script>
</c:set>
