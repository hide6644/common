package common.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "role")
@NamedQueries({
        @NamedQuery(
                name = "findRoleByName",
                query = "select r from Role r where r.name = :name"
        )
})
public class Role extends BaseObject implements Serializable, GrantedAuthority {

    private String name;

    private String description;

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    @Transient
    public String getAuthority() {
        return getName();
    }

    @Length(max = 16, message = "{common.validator.constraints.MaxLength.message}")
    @Column(length = 16)
    public String getName() {
        return this.name;
    }

    @Length(max = 64, message = "{common.validator.constraints.MaxLength.message}")
    @Column(length = 64)
    public String getDescription() {
        return this.description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Role)) {
            return false;
        }

        final Role role = (Role) o;

        return !(name != null ? !name.equals(role.name) : role.name != null);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return (name != null ? name.hashCode() : 0);
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SIMPLE_STYLE).append(name).toString();
    }
}
