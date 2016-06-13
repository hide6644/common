package common.webapp.listener;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import common.model.User;

/**
 * ログイン中のユーザを管理するクラス.
 */
public class CounterListener implements ServletContextListener, HttpSessionAttributeListener {

    /** ユーザ数保存用の変数名 */
    public static final String COUNT_KEY = "LOGIN_USER_COUNTER";

    /** ユーザ保存用の変数名 */
    public static final String USERS_KEY = "LOGIN_USERS";

    /** セッションに保存されているSecurity Contextの変数名 */
    public static final String EVENT_KEY = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute(COUNT_KEY, 0);
        sce.getServletContext().setAttribute(USERS_KEY, new LinkedHashSet<User>());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (event.getName().equals(EVENT_KEY) && !isAnonymous()) {
            Authentication auth = ((SecurityContext) event.getValue()).getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof User) {
                addUser((User) auth.getPrincipal(), event.getSession().getServletContext());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (event.getName().equals(EVENT_KEY) && !isAnonymous()) {
            Authentication auth = ((SecurityContext) event.getValue()).getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof User) {
                User account = (User) auth.getPrincipal();
                removeUser(account, event.getSession().getServletContext());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        if (event.getName().equals(EVENT_KEY) && !isAnonymous()) {
            Authentication auth = ((SecurityContext) event.getValue()).getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof User) {
                addUser((User) auth.getPrincipal(), event.getSession().getServletContext());
            }
        }
    }

    /**
     * ログインユーザ数をカウントアップする.
     *
     * @param servletContext
     *            {@link ServletContext}
     */
    private void incrementCounter(ServletContext servletContext) {
        int count = (Integer) servletContext.getAttribute(COUNT_KEY);
        count++;

        servletContext.setAttribute(COUNT_KEY, count);
    }

    /**
     * ログインユーザ数をカウントダウンする.
     *
     * @param servletContext
     *            {@link ServletContext}
     */
    private void decrementCounter(ServletContext servletContext) {
        int count = (Integer) servletContext.getAttribute(COUNT_KEY);
        count--;

        servletContext.setAttribute(COUNT_KEY, count);
    }

    /**
     * ログインユーザ一覧に追加する.
     *
     * @param user
     *            ユーザ
     * @param servletContext
     *            {@link ServletContext}
     */
    private void addUser(User user, ServletContext servletContext) {
        @SuppressWarnings("unchecked")
        Set<User> users = (Set<User>) servletContext.getAttribute(USERS_KEY);

        if (!users.contains(user)) {
            users.add(user);
            servletContext.setAttribute(USERS_KEY, users);
            incrementCounter(servletContext);
        }
    }

    /**
     * ログインユーザ一覧から削除する.
     *
     * @param user
     *            ユーザ
     * @param servletContext
     *            {@link ServletContext}
     */
    private void removeUser(User user, ServletContext servletContext) {
        @SuppressWarnings("unchecked")
        Set<User> users = (Set<User>) servletContext.getAttribute(USERS_KEY);

        users.remove(user);

        servletContext.setAttribute(USERS_KEY, users);
        decrementCounter(servletContext);
    }

    /**
     * 匿名ユーザか確認する.
     *
     * @return true:匿名ユーザ、false:認証済みユーザ
     */
    private boolean isAnonymous() {
        AuthenticationTrustResolver resolver = new AuthenticationTrustResolverImpl();
        SecurityContext ctx = SecurityContextHolder.getContext();

        if (ctx != null) {
            Authentication auth = ctx.getAuthentication();
            return resolver.isAnonymous(auth);
        }

        return true;
    }
}
