package common.webapp.listener;

import static org.junit.jupiter.api.Assertions.*;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import common.Constants;

public class StartupListenerTest {

    private MockServletContext sc;

    private ServletContextListener listener;

    private ContextLoaderListener springListener;

    @BeforeEach
    public void setUp() {
        sc = new MockServletContext("");
        sc.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "classpath:common/dao/applicationContext-resources.xml, classpath:/common/dao/applicationContext-dao.xml, classpath:/applicationContext-service.xml");

        springListener = new ContextLoaderListener();
        springListener.contextInitialized(new ServletContextEvent(sc));
        listener = new StartupListener();
    }

    @AfterEach
    public void tearDown() {
        springListener.closeWebApplicationContext(sc);
        springListener.contextDestroyed(new ServletContextEvent(sc));
    }

    @Test
    public void testContextInitialized() {
        listener.contextInitialized(new ServletContextEvent(sc));

        assertNotNull(sc.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE));
        assertNotNull(sc.getAttribute(Constants.AVAILABLE_ROLES));
        assertNotNull(sc.getAttribute(Constants.ASSETS_VERSION));
    }
}
