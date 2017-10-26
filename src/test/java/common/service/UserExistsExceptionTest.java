package common.service;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import common.exception.DatabaseException;
import common.model.User;

public class UserExistsExceptionTest extends BaseManagerTestCase {

    @Autowired
    private UserManager manager;

    @Test(expected = DatabaseException.class)
    public void testAddExistingUser() {
        User user = manager.getUser("-1");

        User newUser = new User();
        BeanUtils.copyProperties(user, newUser);
        newUser.setId(null);
        newUser.setVersion(null);
        newUser.setRoles(null);

        manager.saveUser(newUser);
    }
}
