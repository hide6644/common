package common.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.search.annotations.DocumentId;

/**
 * エンティティの基底クラス.
 */
@MappedSuperclass
public abstract class BaseObject {

    /** ID */
    private Long id;

    /** 更新回数 */
    private Long version;

    /** 登録ユーザ */
    private String createUser;

    /** 登録日時 */
    private Date createDate;

    /** 更新ユーザ */
    private String updateUser;

    /** 更新日時 */
    private Date updateDate;

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
     * 更新回数を取得する.
     *
     * @return 更新回数
     */
    @Version
    @XmlTransient
    public Long getVersion() {
        return version;
    }

    /**
     * 更新回数を設定する.
     *
     * @param version
     *            更新回数
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * 登録ユーザを取得する.
     *
     * @return 登録ユーザ
     */
    @Column(name = "create_user")
    @XmlTransient
    public String getCreateUser() {
        return createUser;
    }

    /**
     * 登録ユーザを設定する.
     *
     * @param createUser
     *            登録ユーザ
     */
    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    /**
     * 登録日時を取得する.
     *
     * @return 更新日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "create_date", updatable = false)
    @XmlTransient
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 登録日時を設定する.
     *
     * @param createDate
     *            登録日時
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    /**
     * 更新ユーザを取得する.
     *
     * @return 更新ユーザ
     */
    @Column(name = "update_user")
    @XmlTransient
    public String getUpdateUser() {
        return updateUser;
    }

    /**
     * 更新ユーザを設定する.
     *
     * @param updateUser
     *            更新ユーザ
     */
    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    /**
     * 更新日時を取得する.
     *
     * @return 更新日時
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    @XmlTransient
    public Date getUpdateDate() {
        return updateDate;
    }

    /**
     * 更新日時を設定する.
     *
     * @param updateDate
     *            更新日時
     */
    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseObject other = (BaseObject) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
