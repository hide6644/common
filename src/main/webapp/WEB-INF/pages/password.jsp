<%@ include file="/includes/taglibs.jsp"%>
<head>
    <title><fmt:message key="updatePasswordForm.title" /></title>
</head>

<div class="span3">
    <h3><fmt:message key="updatePasswordForm.heading" /></h3>
<c:choose>
    <c:when test="${not empty token}">
        <p><fmt:message key="updatePasswordForm.passwordReset.message"/></p>
    </c:when>
    <c:otherwise>
        <p><fmt:message key="updatePasswordForm.changePassword.message"/></p>
    </c:otherwise>
</c:choose>
</div>
<div class="span6">
<form method="post" id="updatePassword" action="<c:url value='/updatePassword' />" class="well" autocomplete="off">
    <input type="hidden" name="from" value="<c:out value="${param.from}" escapeXml="true" />" />

    <div class="form-group">
        <label class="control-label"><fmt:message key="user.username" /> <span class="required">*</span></label>
        <input type="text" name="username" class="form-control" id="username" value="<c:out value="${username}" escapeXml="true" />" readonly>
    </div>
<c:choose>
<c:when test="${not empty token}">
    <input type="hidden" name="token" value="<c:out value="${token}" escapeXml="true" />" />
</c:when>
<c:otherwise>
    <div class="form-group">
    	<label class="control-label"><fmt:message key="updatePasswordForm.currentPassword" /> <span class="required">*</span></label>
        <input type="password" class="form-control" name="currentPassword" id="currentPassword">
    </div>
</c:otherwise>
</c:choose>
    <div class="form-group">
    	<label class="control-label"><fmt:message key="updatePasswordForm.newPassword" /> <span class="required">*</span></label>
        <input type="password" class="form-control" name="password" id="password">
    </div>
    <fieldset class="form-actions">
        <button type="submit" class="btn btn-primary">
            <i class="icon-ok icon-white"></i> <fmt:message key="button.edit" />
        </button>
        <button type="reset" class="btn">
            <i class="icon-refresh"></i> <fmt:message key="button.reset" />
        </button>
        <button type="button" class="btn" id="button_cancel">
            <i class="icon-remove"></i> <fmt:message key="button.cancel" />
        </button>
    </fieldset>
</form>
</div>

<c:set var="scripts" scope="request">
<c:choose>
<c:when test="${not empty token}">
<script type="text/javascript">
<!--
$(function() {
    $('#button_cancel').click(function() {
        $(location).attr('href', '<c:url value="/logout" />');
    });
});
//-->
</script>
</c:when>
<c:when test="${param.from eq 'list'}">
<script type="text/javascript">
<!--
$(function() {
    $('#button_cancel').click(function() {
        $(location).attr('href', '<c:url value="/user" />?from=list');
    });
});
//-->
</script>
</c:when>
<c:otherwise>
<script type="text/javascript">
<!--
$(function() {
    $('#button_cancel').click(function() {
        $(location).attr('href', '<c:url value="/user" />');
    });
});
//-->
</script>
</c:otherwise>
</c:choose>
</c:set>
