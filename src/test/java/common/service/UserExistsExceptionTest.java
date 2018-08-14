package common.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import common.Constants;
import common.dto.UserDetailsForm;
import common.exception.DatabaseException;
import common.model.Role;
import common.model.User;

public class UserExistsExceptionTest extends BaseManagerTestCase {

    @Autowired
    private UserManager manager;

    @Test
    public void testAddExistingUser() {
        User user = manager.getUser("-1");

        UserDetailsForm userDetailsForm = new UserDetailsForm();
        BeanUtils.copyProperties(user, userDetailsForm);
        userDetailsForm.setId(null);
        userDetailsForm.setVersion(null);
        userDetailsForm.getRoles().clear();
        userDetailsForm.addRole(new Role(Constants.USER_ROLE));

        Assertions.assertThrows(DatabaseException.class, () -> {
            manager.saveUserDetails(userDetailsForm);
        });
    }
}
