package common.webapp.listener;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.web.context.ContextLoader;

import common.Constants;
import common.model.Role;
import common.model.User;

public class CounterListenerTest {

    private MockServletContext sc;

    private CounterListener listener;

    @BeforeEach
    public void setUp() {
        sc = new MockServletContext("");
        sc.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "classpath:common/dao/applicationContext-resources.xml, classpath:/common/dao/applicationContext-dao.xml, classpath:/applicationContext-service.xml");

        listener = new CounterListener();
    }

    @Test
    public void testContextInitialized() {
        listener.contextInitialized(new ServletContextEvent(sc));

        assertNotNull(sc.getAttribute(CounterListener.COUNT_KEY));
        assertNotNull(sc.getAttribute(CounterListener.USERS_KEY));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAttribute() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        HttpSession session =request.getSession();

        User user = new User("user");
        user.setId(1L);
        user.setPassword("password");
        user.addRole(new Role(Constants.USER_ROLE));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(user);
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);

        listener.attributeAdded(new HttpSessionBindingEvent(session, CounterListener.EVENT_KEY, context));

        Set<User> users = (Set<User>) session.getServletContext().getAttribute(CounterListener.USERS_KEY);
        assertNotNull(users);
        assertEquals(1, users.size());

        listener.attributeReplaced(new HttpSessionBindingEvent(session, CounterListener.EVENT_KEY, context));

        users = (Set<User>) session.getServletContext().getAttribute(CounterListener.USERS_KEY);
        assertNotNull(users);
        assertEquals(1, users.size());

        listener.attributeRemoved(new HttpSessionBindingEvent(session, CounterListener.EVENT_KEY, context));

        users = (Set<User>) session.getServletContext().getAttribute(CounterListener.USERS_KEY);
        assertNotNull(users);
        assertEquals(0, users.size());
    }
}
