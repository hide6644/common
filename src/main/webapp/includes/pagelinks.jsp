<%@ include file="/includes/taglibs.jsp"%>
<div class="row">
<div class="col-sm-6">
<c:choose>
<c:when test="${paginatedList.allPageCount == 0}">
    <fmt:message key="paging.no_items_found" />
</c:when>
<c:when test="${paginatedList.allPageCount == 1}">
    <fmt:message key="paging.all_items_found">
        <fmt:param>${paginatedList.allRecordCount}</fmt:param>
    </fmt:message>
</c:when>
<c:otherwise>
    <fmt:message key="paging.some_items_found">
        <fmt:param>${paginatedList.allRecordCount}</fmt:param>
        <fmt:param>${paginatedList.currentStartRecordNumber}</fmt:param>
        <fmt:param>${paginatedList.currentEndRecordNumber}</fmt:param>
    </fmt:message>
</c:otherwise>
</c:choose>
</div>
<div class="col-sm-6">
<c:if test="${paginatedList.allPageCount > 0}">
<ul class="pagination pagination-sm pull-right">
<c:choose>
<c:when test="${paginatedList.existPrePage}">
    <li><a href="./?page=1"><fmt:message key="button.first" /></a></li>
    <li><a href="./?page=${paginatedList.prePageNumber}"><fmt:message key="button.previous" /></a></li>
</c:when>
<c:otherwise>
    <li class="disabled"><a href="#"><fmt:message key="button.first" /></a></li>
    <li class="disabled"><a href="#"><fmt:message key="button.previous" /></a></li>
</c:otherwise>
</c:choose>
<c:forEach var="index" items="${paginatedList.pageNumberList}">
<c:choose>
<c:when test="${index == paginatedList.currentPageNumber}">
    <li class="active"><a href="#">${index}</a></li>
</c:when>
<c:otherwise>
    <li><a href="./?page=${index}">${index}</a></li>
</c:otherwise>
</c:choose>
</c:forEach>
<c:choose>
<c:when test="${paginatedList.existNextPage}">
    <li><a href="./?page=${paginatedList.nextPageNumber}"><fmt:message key="button.next" /></a></li>
    <li><a href="./?page=${paginatedList.allPageCount}"><fmt:message key="button.last" /></a></li>
</c:when>
<c:otherwise>
    <li class="disabled"><a href="#"><fmt:message key="button.next" /></a></li>
    <li class="disabled"><a href="#"><fmt:message key="button.last" /></a></li>
</c:otherwise>
</c:choose>
</ul>
</c:if>
</div>
</div>