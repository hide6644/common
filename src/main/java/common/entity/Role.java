package common.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.groups.Default;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

import common.validator.groups.Modify;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 権限.
 */
@NoArgsConstructor
@RequiredArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "role")
@Cache(region = "roleCache", usage = CacheConcurrencyStrategy.READ_WRITE)
public final class Role extends BaseObject implements Serializable, GrantedAuthority {

    /** 名称 */
    @NonNull
    @Length(max = 16, groups = { Default.class, Modify.class })
    @Column(length = 16)
    private String name;

    /** 説明 */
    @EqualsAndHashCode.Exclude
    @Length(max = 64, groups = { Default.class, Modify.class })
    @Column(length = 64)
    private String description;

    /**
     * {@inheritDoc}
     */
    @Transient
    @Override
    public String getAuthority() {
        return getName();
    }
}
