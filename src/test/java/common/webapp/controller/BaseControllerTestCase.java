package common.webapp.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@WebAppConfiguration
@ContextConfiguration(
        locations = { "classpath:/common/dao/applicationContext-resources.xml", "classpath:/common/dao/applicationContext-dao.xml",
                "classpath:/applicationContext-service.xml", "classpath:/applicationContext-security.xml",
                "classpath*:/applicationContext.xml",
                "classpath:/common/webapp/controller/dispatcher-servlet.xml" })
public abstract class BaseControllerTestCase extends AbstractTransactionalJUnit4SpringContextTests {

    protected transient Logger log = LogManager.getLogger(getClass());

    @Autowired
    protected WebApplicationContext wac;

    protected GreenMail greenMail;

    protected MockMvc mockMvc;

    @Before
    public void setUp() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();

        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(greenMail.getSmtp().getPort());
        mailSender.setHost("localhost");

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        mockMvc = webAppContextSetup(wac).build();
    }

    @After
    public void tearDown() {
        greenMail.stop();

        SecurityContextHolder.clearContext();
    }

    protected MockHttpServletRequest newPost(String url) {
        return new MockHttpServletRequest("POST", url);
    }

    protected MockHttpServletRequest newGet(String url) {
        return new MockHttpServletRequest("GET", url);
    }
}
