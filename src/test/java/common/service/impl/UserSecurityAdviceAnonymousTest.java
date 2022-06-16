package common.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import common.Constants;
import common.dto.UserDetailsForm;
import common.entity.Role;
import common.entity.User;
import common.service.UserManager;

@ExtendWith(MockitoExtension.class)
class UserSecurityAdviceAnonymousTest {

    private static UserManager userManager;

    @BeforeAll
    static void setUpClass() {
        try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("/common/service/applicationContext-test.xml")) {
            userManager = (UserManager) ctx.getBean("target");
            User user = new User("user");
            user.setId(1L);
            user.setVersion(1L);
            when(userManager.getUserByUsername("user")).thenReturn(user);
            when(userManager.saveUser(user)).thenReturn(user);
        }
    }

    @BeforeEach
    void setUp() {
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(new AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS")));
        SecurityContextHolder.setContext(context);
    }

    @Test
    void testAddUser() throws Exception {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("user");
        userDetailsForm.setId(1L);
        userDetailsForm.getRoles().add(new Role(Constants.USER_ROLE));

        assertDoesNotThrow(() -> {
            userManager.saveUserDetails(userDetailsForm);
        });
    }
}
