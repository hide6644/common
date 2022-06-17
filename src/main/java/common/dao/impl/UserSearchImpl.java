package common.dao.impl;

import java.util.List;
import java.util.Map;

import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import common.dao.UserSearch;
import common.dto.UserSearchCriteria;
import common.entity.User;

/**
 * UserのHibernate Search DAOの実装クラス.
 */
@Repository("userSearch")
public class UserSearchImpl extends HibernateSearchImpl<User> implements UserSearch {

    /**
     * デフォルト・コンストラクタ.
     */
    public UserSearchImpl() {
        super(User.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> search(UserSearchCriteria userSearchCriteria, PageRequest pageRequest) {
        return Search.session(entityManager).search(User.class)
                .where(f -> {
                    if (userSearchCriteria.getUsername() == null && userSearchCriteria.getEmail() == null) {
                        return f.matchAll();
                    } else {
                        return f.bool(b -> {
                            if (userSearchCriteria.getUsername() != null) {
                                b.should(f.match().field(UserSearchCriteria.USERNAME_FIELD)
                                        .matching(userSearchCriteria.getUsername()));
                            }
                            if (userSearchCriteria.getEmail() != null) {
                                b.should(f.match().field(UserSearchCriteria.EMAIL_FIELD)
                                        .matching(userSearchCriteria.getEmail()));
                            }
                        });
                    }
                })
                .sort(f -> f.field(UserSearchCriteria.USERNAME_FIELD + "Sort"))
                .fetchHits((int) pageRequest.getOffset(), pageRequest.getPageSize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Long> facet(String field, int maxCount) {
        var searchSession = Search.session(entityManager);
        AggregationKey<Map<String, Long>> countByKey = AggregationKey.of(field);

        SearchResult<User> result = searchSession.search(User.class)
                .where(f -> f.matchAll())
                .aggregation(countByKey, f -> f.terms()
                        .field(field, String.class)
                        .orderByCountDescending()
                        .minDocumentCount(1)
                        .maxTermCount(maxCount))
                .fetch(20);

        result.hits();

        return result.aggregation(countByKey);
    }
}
