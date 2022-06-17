package common.dao;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import common.Constants;
import common.dao.jpa.RoleDao;
import common.entity.Role;

class RoleDaoTest extends BaseDaoTestCase {

    @Autowired
    private RoleDao dao;

    @Test
    void testGetRoleInvalid() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getReferenceById("badrolename");
        });
    }

    @Test
    void testGetRole() {
        Role role = dao.getReferenceById(Constants.USER_ROLE);

        assertNotNull(role);
        assertEquals(role, new Role(Constants.USER_ROLE));
    }

    @Test
    void testUpdateRole() {
        Role role = dao.getReferenceById(Constants.USER_ROLE);
        role.setDescription("test descr");
        dao.save(role);
        role = dao.getReferenceById(Constants.USER_ROLE);

        assertEquals("test descr", role.getDescription());
    }

    @Test
    void testAddAndRemoveRole() {
        Role role = new Role("testrole");
        role.setDescription("new role descr");
        dao.save(role);
        role = dao.getReferenceById("testrole");

        assertNotNull(role.getDescription());

        dao.deleteById("testrole");

        Assertions.assertThrows(DataAccessException.class, () -> {
            dao.getReferenceById("testrole");
        });
    }
}
