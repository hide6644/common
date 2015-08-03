package common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

/**
 * 権限.
 */
@Entity
@Table(name = "role")
@NamedQueries({
        @NamedQuery(
                name = "findRoleByName",
                query = "from Role r where r.name = :name"
        )
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends BaseObject implements Serializable, GrantedAuthority {

    /** ID */
    private Long id;

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
     * IDを取得する.
     *
     * @return ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @DocumentId
    @XmlTransient
    public Long getId() {
        return id;
    }

    /**
     * IDを設定する.
     *
     * @param id
     *            ID
     */
    public void setId(Long id) {
        this.id = id;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }

        Role role = (Role) o;

        return name != null ? name.equals(role.name) : role.name == null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
