<%@ include file="/includes/taglibs.jsp"%>
                <ul class="nav nav-pills nav-stacked">
<c:if test="${currentNavlistMenu eq 'admin' || currentNavlistMenu eq 'activeUsersForm'}">
                    <li class="nav-header"><fmt:message key="admin.topForm" /></li>
                    <li class="dropdown">
                        <a class="dropdown-toggle" data-toggle="dropdown" href="#" aria-expanded="false">
                            <fmt:message key="admin.master.topForm" /> <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu">
                            <li><a href="<c:url value="/admin/master/users" />"><fmt:message key="user" /></a></li>
                            <li class="divider"></li>
                            <li><a href="<c:url value="/admin/reload" />"><fmt:message key="admin.master.topForm.reload" /></a></li>
                        </ul>
                    </li>
                    <li class="<c:if test="${currentNavlistMenu eq 'activeUsersForm'}">active</c:if>"><a href="<c:url value="/admin/activeUsers" />"><fmt:message key="activeUsersForm" /></a></li>
</c:if>
<c:if test="${currentNavlistMenu eq 'master' || currentNavlistMenu eq 'userForm'}">
                    <li class="nav-header"><fmt:message key="admin.master.topForm" /></li>
                    <li class="<c:if test="${currentNavlistMenu eq 'userForm'}">active</c:if>"><a href="<c:url value="/admin/master/users" />"><fmt:message key="user" /></a></li>
                    <li><a href="<c:url value="/admin/reload" />"><fmt:message key="admin.master.topForm.reload" /></a></li>
</c:if>
                </ul>
