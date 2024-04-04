package common.webapp.controller;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(
        locations = { "classpath:/common/dao/applicationContext-resources.xml", "classpath:/common/dao/applicationContext-dao.xml",
                "classpath:/applicationContext-service.xml", "classpath:/applicationContext-security.xml",
                "classpath*:/applicationContext.xml",
                "classpath:/common/webapp/controller/dispatcher-servlet.xml" })
@Transactional
@Rollback
public abstract class BaseControllerTestCase {

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected ApplicationContext applicationContext;

    protected static GreenMail greenMail;

    protected MockMvc mockMvc;

    @BeforeAll
    protected static void setUpClass() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
    }

    @BeforeEach
    protected void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
        JavaMailSenderImpl mailSender = (JavaMailSenderImpl) applicationContext.getBean("mailSender");
        mailSender.setPort(greenMail.getSmtp().getPort());
        mailSender.setHost("localhost");

        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        mockMvc = webAppContextSetup(wac).build();
    }

    @AfterEach
    protected void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @AfterAll
    protected static void tearDownClass() {
        greenMail.stop();
    }

    protected MockHttpServletRequest newPost(String url) {
        return new MockHttpServletRequest("POST", url);
    }

    protected MockHttpServletRequest newGet(String url) {
        return new MockHttpServletRequest("GET", url);
    }
}
