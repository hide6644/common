package common.service;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import common.Constants;
import common.model.Role;
import common.model.User;

/**
 * ユーザ処理のAdviceクラス.
 */
public class UserSecurityAdvice implements MethodBeforeAdvice, AfterReturningAdvice {

    /** "Access Denied" エラーメッセージ (not i18n-ized). */
    public static final String ACCESS_DENIED = "Access Denied: Only administrators are allowed to modify other users.";

    /** ログ出力クラス */
    private final Logger log = LogManager.getLogger(getClass());

    /**
     * {@inheritDoc}
     */
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        SecurityContext ctx = SecurityContextHolder.getContext();

        if (ctx.getAuthentication() != null) {
            Authentication auth = ctx.getAuthentication();
            boolean administrator = false;
            Collection<? extends GrantedAuthority> roles = auth.getAuthorities();

            for (GrantedAuthority role : roles) {
                if (role.getAuthority().equals(Constants.ADMIN_ROLE)) {
                    administrator = true;
                    break;
                }
            }

            User user = (User) args[0];

            AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
            boolean signupUser = resolver.isAnonymous(auth);

            if (!signupUser) {
                User currentUser = getCurrentUser(auth);

                if (user.getId() != null && !user.getId().equals(currentUser.getId()) && !administrator) {
                    log.warn("Access Denied: '" + currentUser.getUsername() + "' tried to modify '" + user.getUsername() + "'!");
                    throw new AccessDeniedException(ACCESS_DENIED);
                } else if (user.getId() != null && user.getId().equals(currentUser.getId()) && !administrator) {
                    // 入力されたユーザの権限
                    Set<String> userRoles = new HashSet<String>();
                    if (user.getRoles() != null) {
                        for (Role role : user.getRoles()) {
                            userRoles.add(role.getName());
                        }
                    }

                    // ログインユーザの権限
                    Set<String> authorizedRoles = new HashSet<String>();
                    for (GrantedAuthority role : roles) {
                        authorizedRoles.add(role.getAuthority());
                    }

                    if (!CollectionUtils.isEqualCollection(userRoles, authorizedRoles)) {
                        log.warn("Access Denied: '" + currentUser.getUsername() + "' tried to change their role(s)!");
                        throw new AccessDeniedException(ACCESS_DENIED);
                    }
                }
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("Registering new user '" + user.getUsername() + "'");
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        User user = (User) args[0];

        if (user.getVersion() != null) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
            boolean signupUser = resolver.isAnonymous(auth);

            if (auth != null && !signupUser) {
                User currentUser = getCurrentUser(auth);

                if (currentUser.getId().equals(user.getId())) {
                    auth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
    }

    /**
     * ログインユーザ情報を取得する.
     *
     * @param auth
     *            認証情報
     * @return ユーザ
     */
    public static User getCurrentUser(Authentication auth) {
        User currentUser = null;

        if (auth.getPrincipal() instanceof UserDetails) {
            currentUser = (User) auth.getPrincipal();
        } else if (auth.getDetails() instanceof UserDetails) {
            currentUser = (User) auth.getDetails();
        } else {
            throw new AccessDeniedException("User not properly authenticated.");
        }

        return currentUser;
    }
}
