package common.validator.constraints.impl;

import static common.dao.jpa.GenericDaoJpa.*;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import common.validator.constraints.UniqueKey;

/**
 * ユニークか確認する実装クラス.
 */
@Component
public class UniqueKeyValidator implements ConstraintValidator<UniqueKey, Serializable> {

    /** 確認対象の列名 */
    private String[] columnNames;

    /** Entity Managerクラス */
    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(UniqueKey constraintAnnotation) {
        this.columnNames = constraintAnnotation.columnNames();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Serializable target, ConstraintValidatorContext context) {
        boolean isValid = isValidCriteria(target);

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate()).addPropertyNode(columnNames[0]).addConstraintViolation().disableDefaultConstraintViolation();
        }

        return isValid;
    }

    /**
     * 検索クエリを作成する.
     *
     * @param target
     *            検索対象
     * @return true:検索結果無し、false:検索結果有り
     */
    private boolean isValidCriteria(Serializable target) {
        if (entityManager == null) {
            return true;
        }

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<?> root = criteriaQuery.from(target.getClass());
        List<Predicate> preds = new ArrayList<>();

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        boolean nullFlag = true;

        for (int i = 0; i < columnNames.length; i++) {
            Object propertyValue = beanWrapper.getPropertyValue(columnNames[i]);

            if (propertyValue != null) {
                preds.add(builder.equal(root.get(columnNames[i]), propertyValue));
                nullFlag = false;
            }
        }
        // 検索対象のカラムが全てnullであった場合
        if (nullFlag) {
            return true;
        }

        for (PropertyDescriptor p : beanWrapper.getPropertyDescriptors()) {
            if (beanWrapper.getPropertyTypeDescriptor(p.getName()).getAnnotation(Id.class) != null) {
                Object propertyValue = beanWrapper.getPropertyValue(p.getName());

                if (propertyValue != null) {
                    preds.add(builder.notEqual(root.get(p.getName()), propertyValue));
                }
            }
        }

        criteriaQuery.select(builder.count(root));
        criteriaQuery.where(builder.and(preds.toArray(new Predicate[] {})));

        return entityManager.createQuery(criteriaQuery).getSingleResult() == 0;
    }
}
