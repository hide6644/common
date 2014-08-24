package common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.dao.RoleDao;
import common.model.LabelValue;
import common.model.Role;
import common.service.RoleManager;

@Service("roleManager")
public class RoleManagerImpl extends GenericManagerImpl<Role, Long> implements RoleManager {

    RoleDao roleDao;

    @Autowired
    public RoleManagerImpl(RoleDao roleDao) {
        super(roleDao);
        this.roleDao = roleDao;
    }

    /**
     * {@inheritDoc}
     */
    public List<Role> getRoles(Role role) {
        return dao.getAll();
    }

    /**
     * {@inheritDoc}
     */
    public Role getRole(String rolename) {
        return roleDao.getRoleByName(rolename);
    }

    /**
     * {@inheritDoc}
     */
    public Role saveRole(Role role) {
        return dao.save(role);
    }

    /**
     * {@inheritDoc}
     */
    public void removeRole(String rolename) {
        roleDao.removeRole(rolename);
    }

    /**
     * {@inheritDoc}
     */
    public List<LabelValue> getLabelValues() {
        List<LabelValue> list = new ArrayList<LabelValue>();
        List<Role> roles = dao.getAll();

        if (roles != null) {
            for (Role role : roles) {
                list.add(new LabelValue(role.getDescription(), role.getName()));
            }
        }

        return list;
    }
}
