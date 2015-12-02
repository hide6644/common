package common.webapp.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import common.Constants;
import junit.framework.TestCase;

public class StartupListenerTest extends TestCase {

    private MockServletContext sc = null;

    private ServletContextListener listener = null;

    private ContextLoaderListener springListener = null;

    protected void setUp() throws Exception {
        super.setUp();
        sc = new MockServletContext("");

        sc.addInitParameter(ContextLoader.CONFIG_LOCATION_PARAM, "classpath:common/dao/applicationContext-resources.xml, classpath:/common/dao/applicationContext-dao.xml, classpath:/applicationContext-service.xml");

        springListener = new ContextLoaderListener();
        springListener.contextInitialized(new ServletContextEvent(sc));
        listener = new StartupListener();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        springListener.closeWebApplicationContext(sc);
        springListener = null;
        listener = null;
        sc = null;
    }

    public void testContextInitialized() {
        listener.contextInitialized(new ServletContextEvent(sc));

        assertTrue(sc.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null);
        assertTrue(sc.getAttribute(Constants.AVAILABLE_ROLES) != null);

        assertNotNull(sc.getAttribute(Constants.ASSETS_VERSION));
    }
}
