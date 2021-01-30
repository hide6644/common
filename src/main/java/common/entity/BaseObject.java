package common.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * エンティティの基底クラス.
 */
@Setter
@Getter
@EqualsAndHashCode
@MappedSuperclass
public abstract class BaseObject implements Serializable {

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @DocumentId
    @XmlTransient
    private Long id;

    /** 更新回数 */
    @EqualsAndHashCode.Exclude
    @Version
    @XmlTransient
    private Long version;

    /** 登録ユーザ */
    @EqualsAndHashCode.Exclude
    @Column(name = "create_user")
    @XmlTransient
    private String createUser;

    /** 登録日時 */
    @EqualsAndHashCode.Exclude
    @Column(name = "create_date", updatable = false)
    @XmlTransient
    private LocalDateTime createDate;

    /** 更新ユーザ */
    @EqualsAndHashCode.Exclude
    @Column(name = "update_user")
    @XmlTransient
    private String updateUser;

    /** 更新日時 */
    @EqualsAndHashCode.Exclude
    @Column(name = "update_date")
    @XmlTransient
    private LocalDateTime updateDate;
}
