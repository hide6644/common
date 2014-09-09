package common.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.dao.RoleDao;
import common.model.LabelValue;
import common.model.Role;
import common.service.RoleManager;

/**
 * 権限処理の実装クラス.
 */
@Service("roleManager")
public class RoleManagerImpl extends GenericManagerImpl<Role, Long> implements RoleManager {

    /** 権限DAO */
    RoleDao roleDao;

    /**
     * コンストラクタ
     *
     * @param roleDao
     *            権限DAO
     */
    @Autowired
    public RoleManagerImpl(RoleDao roleDao) {
        super(roleDao);
        this.roleDao = roleDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Role getRole(String rolename) {
        return roleDao.getRoleByName(rolename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRole(String rolename) {
        roleDao.removeRole(rolename);
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
