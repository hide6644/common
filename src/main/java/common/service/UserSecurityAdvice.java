package common.service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import common.Constants;
import common.dto.UserDetailsForm;
import common.entity.User;
import lombok.extern.log4j.Log4j2;

/**
 * ユーザ処理のAdviceクラス.
 */
@Log4j2
public class UserSecurityAdvice implements MethodBeforeAdvice, AfterReturningAdvice {

    /** "Access Denied" エラーメッセージ (not i18n-ized). */
    public static final String ACCESS_DENIED = "Access Denied: Only administrators are allowed to modify other users.";

    /**
     * {@inheritDoc}
     */
    @Override
    public void before(Method method, Object[] args, Object target) {
        var ctx = SecurityContextHolder.getContext();

        if (ctx.getAuthentication() != null) {
            var auth = ctx.getAuthentication();
            var userDetailsForm = (UserDetailsForm) args[0];

            if (new AuthenticationTrustResolverImpl().isAnonymous(auth)) {
                log.debug("Registering new user '{}'", () -> userDetailsForm.getUsername());
            } else {
                checkAuthentication(auth, userDetailsForm);
            }
        }
    }

    /**
     * 行った操作に対して権限があるか確認する.
     *
     * @param auth
     *            {@link Authentication}
     * @param userDetailsForm
     *            ユーザ情報
     */
    private void checkAuthentication(Authentication auth, UserDetailsForm userDetailsForm) {
        boolean administrator = auth.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals(Constants.ADMIN_ROLE));
        var currentUser = getCurrentUser(auth);

        if (!Objects.equals(userDetailsForm.getId(), currentUser.getId()) && !administrator) {
            log.warn("Access Denied: '{}' tried to modify '{}'!", () -> currentUser.getUsername(), () -> userDetailsForm.getUsername());
            throw new AccessDeniedException(ACCESS_DENIED);
        } else if (userDetailsForm.getId() != null && userDetailsForm.getId().equals(currentUser.getId()) && !administrator) {
            // 入力されたユーザの権限
            Set<String> userRoles = Optional.ofNullable(userDetailsForm.getRoles()).orElseGet(HashSet::new).stream()
                    .map(role -> role.getName()).collect(Collectors.toSet());

            // ログインユーザの権限
            Set<String> authorizedRoles = auth.getAuthorities().stream()
                    .map(role -> role.getAuthority()).collect(Collectors.toSet());

            if (!CollectionUtils.isEqualCollection(userRoles, authorizedRoles)) {
                log.warn("Access Denied: '{}' tried to change their role(s)!", () -> currentUser.getUsername());
                throw new AccessDeniedException(ACCESS_DENIED);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) {
        if (returnValue == null) {
            return;
        }

        var user = (User) returnValue;

        if (user.getVersion() != null) {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
            boolean signupUser = resolver.isAnonymous(auth);

            if (auth != null && !signupUser) {
                var currentUser = getCurrentUser(auth);

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
