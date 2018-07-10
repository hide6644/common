package common.service.impl;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import common.Constants;
import common.model.Role;
import common.model.User;
import common.service.UserManager;

@ExtendWith(MockitoExtension.class)
public class UserSecurityAdviceAnonymousTest {

    ApplicationContext ctx;

    SecurityContext initialSecurityContext;

    @BeforeEach
    public void setUp() {
        initialSecurityContext = SecurityContextHolder.getContext();

        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        SecurityContextHolder.setContext(context);
    }

    @AfterEach
    public void tearDown() {
        SecurityContextHolder.setContext(initialSecurityContext);
    }

    @Test
    public void testAddUser() throws Exception {
        User user = new User("user");
        user.setId(1L);
        user.getRoles().add(new Role(Constants.USER_ROLE));

        makeInterceptedTarget().saveUser(user);
    }

    private UserManager makeInterceptedTarget() throws Exception {
        ctx = new ClassPathXmlApplicationContext("/common/service/applicationContext-test.xml");

        UserManager userManager = (UserManager) ctx.getBean("target");

        return userManager;
    }
}
