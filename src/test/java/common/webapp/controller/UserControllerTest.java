package common.webapp.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;

import common.Constants;
import common.model.User;
import common.service.UserManager;
import common.webapp.filter.FlashMap;

public class UserControllerTest extends BaseControllerTestCase {

    @Autowired
    private UserController c = null;

    @Test
    public void testAdd() throws Exception {
        log.debug("testing add new user...");
        MockHttpServletRequest request = newGet("/userform.html");
        request.addParameter("mode", "Add");
        request.addUserRole(Constants.ADMIN_ROLE);

        User user = c.showForm(request, new MockHttpServletResponse());
        assertNull(user.getUsername());
    }

    @Test
    public void testAddWithoutPermission() throws Exception {
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
    public void testEdit() throws Exception {
        log.debug("testing edit...");
        MockHttpServletRequest request = newGet("/userform.html");
        request.addParameter("userId", "-1");
        request.addUserRole(Constants.ADMIN_ROLE);

        User user = c.showForm(request, new MockHttpServletResponse());
        assertEquals("admin", user.getFirstName());
    }

    @Test
    public void testEditWithoutPermission() throws Exception {
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
    public void testEditProfile() throws Exception {
        log.debug("testing edit profile...");
        MockHttpServletRequest request = newGet("/userform.html");
        request.setRemoteUser("normaluser");

        User user = c.showForm(request, new MockHttpServletResponse());
        assertEquals("user", user.getFirstName());
    }

    @Test
    public void testSave() throws Exception {
        MockHttpServletRequest request = newPost("/userform.html");
        User user = ((UserManager) applicationContext.getBean("userManager")).getUser("-1");
        user.setConfirmPassword(user.getPassword());
        user.setLastName("Updated Last Name");

        request.setRemoteUser(user.getUsername());

        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmit(user, errors, request, new MockHttpServletResponse());

        assertFalse(errors.hasErrors());
        assertNotNull(FlashMap.get("flash_info_messages"));
    }

    @Test
    public void testSaveFromList() throws Exception {
        MockHttpServletRequest request = newPost("/userform.html");
        request.setParameter("from", "list");
        request.setParameter("version", "1");
        User user = ((UserManager) applicationContext.getBean("userManager")).getUser("-1");
        user.setConfirmPassword(user.getPassword());
        user.setLastName("Updated Last Name");

        request.setRemoteUser(user.getUsername());

        BindingResult errors = new DataBinder(user).getBindingResult();
        c.onSubmit(user, errors, request, new MockHttpServletResponse());

        assertFalse(errors.hasErrors());
        assertNotNull(FlashMap.get("flash_info_messages"));
    }
}
