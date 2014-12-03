<%@ include file="/includes/taglibs.jsp"%>
                <div class="collapse navbar-collapse" id="navbar">
                    <ul class="nav navbar-nav">
<c:if test="${empty pageContext.request.remoteUser}">
                        <li class="active">
                            <a href="<c:url value='/login' />"><fmt:message key="loginForm.heading" /></a>
                        </li>
</c:if>
<sec:authorize access="isAuthenticated()">
                        <li class="<c:if test="${currentMenu eq 'topForm'}">active</c:if>">
                            <a href="<c:url value="/top" />"><fmt:message key="topForm.title" /></a>
                        </li>
                        <li class="<c:if test="${currentMenu eq 'userSaveForm'}">active</c:if>">
                            <a href="<c:url value="/user" />"><fmt:message key="user" /><fmt:message key="button.edit" /></a>
                        </li>
</sec:authorize>
<sec:authorize access="hasRole('ROLE_ADMIN')">
                        <li class="dropdown<c:if test="${currentMenu eq 'admin.topForm'}"> active</c:if>">
                            <a href="#" class="dropdown-toggle" data-toggle="dropdown"><fmt:message key="admin.topForm" /><span class="caret"></span></a>
                            <ul class="dropdown-menu">
                                <li>
                                    <a href="<c:url value='/admin/top' />"><fmt:message key="admin.topForm" /></a>
                                </li>
                                <li class="divider"></li>
                                <li>
                                    <a href="<c:url value="/admin/master/top" />"><fmt:message key="admin.master.topForm" /></a>
                                </li>
                                <li>
                                    <a href="<c:url value="/admin/activeUsers" />"><fmt:message key="activeUsersForm" /></a>
                                </li>
                            </ul>
                        </li>
</sec:authorize>
<sec:authorize access="isAuthenticated()">
                        <li>
                            <a href="<c:url value="/logout" />"><fmt:message key="topForm.logout" /></a>
                        </li>
</sec:authorize>
                    </ul>
                </div>