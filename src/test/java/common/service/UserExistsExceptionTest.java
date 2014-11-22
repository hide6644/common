package common.service;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import common.exception.DBException;
import common.model.User;

public class UserExistsExceptionTest extends BaseManagerTestCase {

    @Autowired
    private UserManager manager;

    @Test(expected = DBException.class)
    public void testAddExistingUser() throws Exception {
        log.debug("entered 'testAddExistingUser' method");
        assertNotNull(manager);

        User user = manager.getUser("-1");

        User user2 = new User();
        BeanUtils.copyProperties(user, user2);
        user2.setId(null);
        user2.setVersion(null);
        user2.setRoles(null);

        manager.saveUser(user2);
    }
}
