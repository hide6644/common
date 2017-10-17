package common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
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
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Role extends BaseObject implements Serializable, GrantedAuthority {

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
}
