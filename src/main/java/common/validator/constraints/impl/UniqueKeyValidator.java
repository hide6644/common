package common.validator.constraints.impl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

import common.Constants;
import common.validator.constraints.UniqueKey;

/**
 * ユニークか確認する実装クラス.
 */
@Component
public class UniqueKeyValidator implements ConstraintValidator<UniqueKey, Object> {

    /** 確認対象の列名 */
    private String[] columnNames;

    /** 確認対象のエンティティクラス */
    Class<?> model;

    /** Entity Managerクラス */
    @PersistenceContext(unitName = Constants.PERSISTENCE_UNIT_NAME)
    protected EntityManager entityManager;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(UniqueKey constraintAnnotation) {
        this.columnNames = constraintAnnotation.columnNames();
        this.model = constraintAnnotation.model();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValid(Object target, ConstraintValidatorContext context) {
        boolean isValid = notExists(target);

        if (!isValid) {
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(columnNames[0]).addConstraintViolation().disableDefaultConstraintViolation();
        }

        return isValid;
    }

    /**
     * 入力内容が既に登録済みか確認する.
     *
     * @param target
     *            入力内容
     * @return true:存在しない、false:存在する
     */
    private boolean notExists(Object target) {
        if (entityManager == null) {
            return true;
        }

        var builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<?> root = criteriaQuery.from(model);
        var beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(target);
        List<Predicate> preds = new ArrayList<>();

        creatingWhereClauseByColumnNames(builder, root, beanWrapper, preds);
        // 検索対象のカラムが全て空であった場合
        if (preds.isEmpty()) {
            return true;
        }

        creatingWhereClauseById(builder, root, beanWrapper, preds);
        criteriaQuery.select(builder.count(root));
        criteriaQuery.where(builder.and(preds.toArray(new Predicate[preds.size()])));

        return entityManager.createQuery(criteriaQuery).getSingleResult() == 0;
    }

    /**
     * 検索クエリのWhere句(確認対象の列)を生成する.
     *
     * @param builder
     *            {@link CriteriaBuilder}
     * @param root
     *            {@link Root}
     * @param beanWrapper
     *            {@link BeanWrapper}
     * @param preds
     *            検索条件
     */
    private void creatingWhereClauseByColumnNames(CriteriaBuilder builder, Root<?> root, BeanWrapper beanWrapper, List<Predicate> preds) {
        for (String columnName : columnNames) {
            Optional.ofNullable(beanWrapper.getPropertyValue(columnName))
                    .ifPresent(propertyValue -> preds.add(builder.equal(root.get(columnName), propertyValue)));
        }
    }

    /**
     * 検索クエリのWhere句(ID列)を生成する.
     *
     * @param builder
     *            {@link CriteriaBuilder}
     * @param root
     *            {@link Root}
     * @param beanWrapper
     *            {@link BeanWrapper}
     * @param preds
     *            検索条件
     */
    private void creatingWhereClauseById(CriteriaBuilder builder, Root<?> root, BeanWrapper beanWrapper, List<Predicate> preds) {
        for (PropertyDescriptor pd : beanWrapper.getPropertyDescriptors()) {
            var type = beanWrapper.getPropertyTypeDescriptor(pd.getName());

            if (type != null && type.getAnnotation(Id.class) != null) {
                Optional.ofNullable(beanWrapper.getPropertyValue(pd.getName()))
                        .ifPresent(propertyValue -> preds.add(builder.notEqual(root.get(pd.getName()), propertyValue)));
            }
        }
    }
}
