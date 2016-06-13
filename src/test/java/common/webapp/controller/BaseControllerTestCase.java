package common.webapp.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ContextConfiguration(
        locations = { "classpath:/common/dao/applicationContext-resources.xml", "classpath:/common/dao/applicationContext-dao.xml",
                "classpath:/applicationContext-service.xml", "classpath:/applicationContext-security.xml",
                "classpath*:/applicationContext.xml",
                "classpath:/common/webapp/controller/dispatcher-servlet.xml" })
public abstract class BaseControllerTestCase extends AbstractTransactionalJUnit4SpringContextTests {

    protected transient final Logger log = LogManager.getLogger(getClass());

    private int smtpPort = 25250;

    @Before
    public void onSetUp() {
        smtpPort = smtpPort + (int) (Math.random() * 100);
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(getSmtpPort());
        mailSender.setHost("localhost");

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @After
    public void clear() {
        SecurityContextHolder.clearContext();
    }

    protected int getSmtpPort() {
        return smtpPort;
    }

    protected MockHttpServletRequest newPost(String url) {
        return new MockHttpServletRequest("POST", url);
    }

    protected MockHttpServletRequest newGet(String url) {
        return new MockHttpServletRequest("GET", url);
    }
}
