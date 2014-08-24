package common.webapp.listener;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import common.model.User;

/**
 * ログイン中のAccountを管理するクラス.
 *
 * @author hide6644
 */
public class CounterListener implements ServletContextListener, HttpSessionAttributeListener {

    public static final String COUNT_KEY = "LOGIN_USER_COUNTER";

    public static final String USERS_KEY = "LOGIN_USERS";

    public static final String EVENT_KEY = HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

    /*
     * (非 Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute(COUNT_KEY, 0);
        sce.getServletContext().setAttribute(USERS_KEY, new LinkedHashSet<User>());
    }

    /*
     * (非 Javadoc)
     *
     * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent event) {
        LogFactory.release(Thread.currentThread().getContextClassLoader());
    }

    /*
     * (非 Javadoc)
     *
     * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void attributeAdded(HttpSessionBindingEvent event) {
        if (event.getName().equals(EVENT_KEY) && !isAnonymous()) {
            Authentication auth = ((SecurityContext) event.getValue()).getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof User) {
                addUser((User) auth.getPrincipal(), event.getSession().getServletContext());
            }
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see javax.servlet.http.HttpSessionAttributeListener#attributeRemoved(javax.servlet.http.HttpSessionBindingEvent)
     */
    public void attributeRemoved(HttpSessionBindingEvent event) {
        if (event.getName().equals(EVENT_KEY) && !isAnonymous()) {
            Authentication auth = ((SecurityContext) event.getValue()).getAuthentication();

            if (auth != null && auth.getPrincipal() instanceof User) {
                User account = (User) auth.getPrincipal();
                removeUser(account, event.getSession().getServletContext());
            }
        }
    }

    /*
     * (非 Javadoc)
     *
     * @see javax.servlet.http.HttpSessionAttributeListener#attributeReplaced(javax.servlet.http.HttpSessionBindingEvent)
     */
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
     *            画面入力値保持モデル
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
     *            画面入力値保持モデル
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
     * 匿名ユーザか.
     *
     * @return true 匿名ユーザ
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
