package common.service;

import java.util.List;

import common.model.LabelValue;
import common.model.Role;

public interface RoleManager extends GenericManager<Role, Long> {

    List<Role> getRoles(Role role);

    Role getRole(String rolename);

    Role saveRole(Role role);

    void removeRole(String rolename);

    /**
     * プルダウン表示用の職種一覧を取得する.
     *
     * @return プルダウン表示用の子分類一覧
     */
    public List<LabelValue> getLabelValues();
}
