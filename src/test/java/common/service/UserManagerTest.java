package common.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import common.Constants;
import common.dto.UserDetailsForm;
import common.entity.Role;
import common.entity.User;

public class UserManagerTest extends BaseManagerTestCase {

    @Autowired
    private UserManager userManager;

    @Autowired
    private RoleManager roleManager;

    @Test
    public void testGetUser() {
        User user = userManager.getUserByUsername("normaluser");

        assertNotNull(user);
        assertTrue(user.isAccountNonLocked());
        assertTrue(user.isAccountNonExpired());
        assertTrue(user.isCredentialsNonExpired());

        log.debug(user);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testGetUserDetails() {
        User user = userManager.getUserByUsername("normaluser");
        UserDetailsForm userDetails = userManager.getUserDetails(user);

        assertNotNull(userDetails);
        assertEquals(1, userDetails.getRoleList().size());
    }

    @Test
    public void testSaveUser() {
        User user = userManager.getUserByUsername("normaluser");
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        BeanUtils.copyProperties(user, userDetailsForm);
        userDetailsForm.setConfirmPassword(user.getPassword());
        userDetailsForm.setLastName("smith");

        log.debug("saving user with updated last name: " + userDetailsForm);

        user = userManager.saveUserDetails(userDetailsForm);

        assertEquals("smith", user.getLastName());
        assertEquals(1, user.getRoles().size());

        userDetailsForm.removeRole(new Role(Constants.USER_ROLE));
        user = userManager.saveUserDetails(userDetailsForm);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    public void testAddAndRemoveUser() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm = (UserDetailsForm) populate(userDetailsForm);
        userDetailsForm.addRole(roleManager.getRole(Constants.USER_ROLE));
        User user = userManager.saveUserDetails(userDetailsForm);

        log.debug("removing user...");

        userManager.removeUser(user);

        assertNull(userManager.getUserByUsername("john"));
    }

    @Test
    public void testLockoutUser() {
        User user = userManager.getUserByUsername("normaluser");

        assertFalse(user.isAccountLocked());

        userManager.lockoutUser("normaluser");
        user = userManager.getUserByUsername("normaluser");

        assertTrue(user.isAccountLocked());
    }

    @Test
    public void testAddAndRemoveUserByPK() {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        userDetailsForm = (UserDetailsForm) populate(userDetailsForm);
        userDetailsForm.addRole(roleManager.getRole(Constants.USER_ROLE));
        User user = userManager.saveUserDetails(userDetailsForm);

        assertEquals("john_elway", user.getUsername());
        assertEquals(1, user.getRoles().size());

        log.debug("removing user...");

        userManager.removeUser(user.getId().toString());

        assertNull(userManager.getUserByUsername("john"));
    }
}
