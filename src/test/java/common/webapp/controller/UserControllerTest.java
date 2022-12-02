package common.webapp.controller;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.WebDataBinder;

import common.Constants;
import common.dto.UserDetailsForm;
import common.entity.User;
import common.service.UserManager;
import common.webapp.filter.FlashMap;

class UserControllerTest extends BaseControllerTestCase {

    @Autowired
    private UserController c;

    @Test
    void testInitBinder() {
        assertDoesNotThrow(() -> {
            c.initBinder(new WebDataBinder(User.class));
        });
    }

    @Test
    void testAdd() throws Exception {
        log.debug("testing add new user...");
        MockHttpServletRequest request = newGet("/userform.html");
        request.addParameter("mode", "Add");
        request.addUserRole(Constants.ADMIN_ROLE);

        UserDetailsForm userDetailsForm = c.showForm(request, new MockHttpServletResponse());
        assertNull(userDetailsForm.getUsername());
    }

    @Test
    void testAddWithoutPermission() throws Exception {
        log.debug("testing add new user...");
        MockHttpServletRequest request = newGet("/userform.html");
        request.addParameter("mode", "Add");

        try {
            c.showForm(request, new MockHttpServletResponse());
            fail("AccessDeniedException not thrown...");
        } catch (AccessDeniedException ade) {
            assertNotNull(ade.getMessage());
        }
    }

    @Test
    void testEdit() throws Exception {
        log.debug("testing edit...");
        MockHttpServletRequest request = newGet("/userform.html");
        request.addParameter("userId", "-1");
        request.addUserRole(Constants.ADMIN_ROLE);

        UserDetailsForm userDetailsForm = c.showForm(request, new MockHttpServletResponse());
        assertEquals("admin", userDetailsForm.getFirstName());
    }

    @Test
    void testEditWithoutPermission() throws Exception {
        log.debug("testing edit...");
        MockHttpServletRequest request = newGet("/userform.html");
        request.addParameter("userId", "-2");

        try {
            c.showForm(request, new MockHttpServletResponse());
            fail("AccessDeniedException not thrown...");
        } catch (AccessDeniedException ade) {
            assertNotNull(ade.getMessage());
        }
    }

    @Test
    void testEditProfile() throws Exception {
        log.debug("testing edit profile...");
        MockHttpServletRequest request = newGet("/userform.html");
        request.setRemoteUser("normaluser");

        UserDetailsForm userDetailsForm = c.showForm(request, new MockHttpServletResponse());
        assertEquals("user", userDetailsForm.getFirstName());
    }

    @Test
    void testSaveHasErrors() throws Exception {
        MockHttpServletRequest request = newPost("/userform.html");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("testuser");

        BindingResult errors = new DataBinder(userDetailsForm).getBindingResult();
        errors.rejectValue("email", "errors.required", "{0} is a required field.");
        String rtn = c.onSubmitByPostMethod(userDetailsForm, errors, request, new MockHttpServletResponse());

        assertEquals("user", rtn);
    }

    @Test
    void testSaveWithoutPermission() throws Exception {
        MockHttpServletRequest request = newPost("/userform.html");
        User user = ((UserManager) applicationContext.getBean("userManager")).getUser("-2");
        user.setConfirmPassword(user.getPassword());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        token.setDetails(user);
        SecurityContext context = new SecurityContextImpl();
        context.setAuthentication(token);
        SecurityContextHolder.setContext(context);

        BindingResult errors = new DataBinder(user).getBindingResult();

        User saveUser = ((UserManager) applicationContext.getBean("userManager")).getUser("-1");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        BeanUtils.copyProperties(saveUser, userDetailsForm);
        MockHttpServletResponse response = new MockHttpServletResponse();
        c.onSubmitByPutMethod(userDetailsForm, errors, request, response);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());

        response = new MockHttpServletResponse();
        c.onSubmitByPostMethod(userDetailsForm, errors, request, response);

        assertEquals(HttpServletResponse.SC_FORBIDDEN, response.getStatus());
    }

    @Test
    void testSave() throws Exception {
        MockHttpServletRequest request = newPost("/userform.html");
        User user = ((UserManager) applicationContext.getBean("userManager")).getUser("-1");
        user.setConfirmPassword(user.getPassword());
        user.setLastName("Updated Last Name");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        BeanUtils.copyProperties(user, userDetailsForm);

        request.setRemoteUser(user.getUsername());

        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmitByPutMethod(userDetailsForm, errors, request, new MockHttpServletResponse());

        assertFalse(errors.hasErrors());
        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    void testSaveAddFromList() throws Exception {
        MockHttpServletRequest request = newPost("/userform.html");
        request.setParameter("from", "list");
        request.setParameter("mode", "Add");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm.setUsername("testuser");
        userDetailsForm.setPassword("testuser");
        userDetailsForm.setEmail("test@foo.bar");
        userDetailsForm.setFirstName("First Name");
        userDetailsForm.setConfirmPassword(userDetailsForm.getPassword());

        request.setRemoteUser("administrator");

        BindingResult errors = new DataBinder(userDetailsForm).getBindingResult();
        c.onSubmitByPostMethod(userDetailsForm, errors, request, new MockHttpServletResponse());

        assertEquals(1, greenMail.getReceivedMessages().length);
        assertFalse(errors.hasErrors());
        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    void testSaveEditFromList() throws Exception {
        MockHttpServletRequest request = newPost("/userform.html");
        request.setParameter("from", "list");
        request.setParameter("version", "1");
        User user = ((UserManager) applicationContext.getBean("userManager")).getUser("-1");
        user.setConfirmPassword(user.getPassword());
        user.setLastName("Updated Last Name");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        BeanUtils.copyProperties(user, userDetailsForm);

        request.setRemoteUser(user.getUsername());

        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmitByPutMethod(userDetailsForm, errors, request, new MockHttpServletResponse());

        assertFalse(errors.hasErrors());
        assertNotNull(FlashMap.get("flash_info_messages"));
    }
}
