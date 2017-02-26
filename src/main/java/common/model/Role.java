package common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

/**
 * 権限.
 */
@Entity
@Table(name = "role")
@NamedQueries({
    @NamedQuery(
        name = Role.FIND_BY_NAME,
        query = "from Role r where name = :name"
    )
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends BaseObject implements Serializable, GrantedAuthority {

    /** 権限を名称で検索するクエリ */
    public static final String FIND_BY_NAME = "Role.findByName";

    /** 名称 */
    private String name;

    /** 説明 */
    private String description;

    /**
     * デフォルト・コンストラクタ
     */
    public Role() {
    }

    /**
     * コンストラクタ
     *
     * @param name
     *            名称
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * 名称を取得する.
     *
     * @return 名称
     */
    @Length(max = 16)
    @Column(length = 16)
    public String getName() {
        return name;
    }

    /**
     * 名称を設定する.
     *
     * @param name
     *            名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 説明を取得する.
     *
     * @return 説明
     */
    @Length(max = 64)
    @Column(length = 64)
    public String getDescription() {
        return description;
    }

    /**
     * 説明を設定する.
     *
     * @param description
     *            説明
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Transient
    @Override
    public String getAuthority() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Role other = (Role) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
