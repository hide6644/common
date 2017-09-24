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
    public static final String COUNT_KEY = "loginUserCounter";

    /** ユーザ保存用の変数名 */
    public static final String USERS_KEY = "loginUsers";

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
        // 何もしない
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
            } else if (auth != null && auth.getDetails() instanceof User) {
                addUser((User) auth.getDetails(), event.getSession().getServletContext());
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
                removeUser((User) auth.getPrincipal(), event.getSession().getServletContext());
            } else if (auth != null && auth.getDetails() instanceof User) {
                removeUser((User) auth.getDetails(), event.getSession().getServletContext());
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
            } else if (auth != null && auth.getDetails() instanceof User) {
                addUser((User) auth.getDetails(), event.getSession().getServletContext());
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
        Set<User> users = getUsers(servletContext);

        if (!users.contains(user)) {
            users.add(user);
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
        getUsers(servletContext).remove(user);
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

    /**
     * ログインユーザ一覧を取得する.
     *
     * @param servletContext
     *            {@link ServletContext}
     * @return ログインユーザ一覧
     */
    @SuppressWarnings("unchecked")
    private Set<User> getUsers(ServletContext servletContext) {
        Set<User> users = (Set<User>) servletContext.getAttribute(USERS_KEY);

        if (users == null) {
            users = new LinkedHashSet<>();
            servletContext.setAttribute(COUNT_KEY, 0);
            servletContext.setAttribute(USERS_KEY, users);
        }

        return users;
    }
}
