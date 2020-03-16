package common.service.impl;

import static common.dao.jpa.UserSpecifications.*;
import static org.springframework.data.jpa.domain.Specification.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.lucene.search.SortField;
import org.hibernate.search.jpa.FullTextQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import common.Constants;
import common.dao.HibernateSearch;
import common.dao.UserDao;
import common.dto.PaginatedList;
import common.dto.SearchTermAndField;
import common.dto.UploadForm;
import common.dto.UploadResult;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.entity.Role;
import common.entity.User;
import common.service.UserManager;
import common.service.UsersManager;
import common.webapp.converter.FileType;
import common.webapp.converter.UserFileConverterFactory;

/**
 *  複数ユーザ処理の実装クラス.
 */
@Service("usersManager")
public class UsersManagerImpl extends BaseManagerImpl implements UsersManager {

    /** ユーザDAO */
    private UserDao userDao;

    /** ユーザ処理 */
    @Autowired
    private UserManager userManager;

    /** UserのHibernate Search DAO */
    @Autowired
    @Qualifier("userSearch")
    private HibernateSearch userSearch;

    /** パスワードエンコーダー */
    @Autowired(required = false)
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /** 検証ツールクラス */
    @Autowired
    private Validator validator;

    /**
     * コンストラクタ.
     *
     * @param userDao
     *            ユーザDAO
     */
    @Autowired
    public UsersManagerImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsers() {
        return userDao.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void uploadUsers(UploadForm uploadForm) {
        UploadResult uploadResult = new UploadResult(2); // 1行目はヘッダー行のため、2から開始する
        uploadForm.setUploadResult(uploadResult);

        UserFileConverterFactory.createConverter(FileType.of(uploadForm.getFileType()))
                .convert(uploadForm.getFileData())
                .stream().map(user -> {
                    // デフォルトの要再認証日時を設定する
                    user.setCredentialsExpiredDate(LocalDateTime.now().plusDays(Constants.CREDENTIALS_EXPIRED_TERM));
                    // 新規登録時は権限を一般で設定する
                    user.addRole(new Role(Constants.USER_ROLE));
                    user.setConfirmPassword(user.getPassword());
                    user.setEnabled(true);
                    return user;
                }).forEach(user -> validateAndSaveUploadedUser(user, uploadResult));
    }

    /**
     * 入力内容を検証し保存する.
     *
     * @param user
     *            ユーザ
     * @param uploadResult
     *            取り込み結果
     */
    private void validateAndSaveUploadedUser(User user, UploadResult uploadResult) {
        Set<ConstraintViolation<User>> results = validator.validate(user);

        if (results.isEmpty()) {
            // エラー無しの場合、保存する
            userManager.saveUser(user);
            uploadResult.addSuccessTotalCount();
        } else {
            // エラー有りの場合、エラーメッセージを設定する
            uploadResult.addUploadErrors(results.stream()
                    .sorted(Comparator.<ConstraintViolation<User>, String>comparing(violation -> violation.getPropertyPath().toString())
                            .thenComparing(violation -> violation.getMessage()))
                    .map(error -> {
                        String fieldName = getText("user." + error.getPropertyPath().toString());
                        String message = error.getMessage().replaceAll("\\{0\\}", fieldName);
                        return uploadResult.createUploadError(fieldName, message);
                    })
                    .collect(Collectors.toList()));
        }

        uploadResult.addProcessingCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedList<UserSearchResults> createPaginatedList(UserSearchCriteria userSearchCriteria, Integer page) {
        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(page).orElse(1) - 1, Constants.PAGING_SIZE, Sort.by(UserSearchCriteria.USERNAME_FIELD));
        Page<User> pagedUser = userDao.findAll(where(usernameContains(userSearchCriteria.getUsername())).and(emailContains(userSearchCriteria.getEmail())), pageRequest);

        return new PaginatedList<>(new PageImpl<>(
                pagedUser.stream()
                        .map(user -> UserSearchResults.of(user))
                        .collect(Collectors.toList()),
                pagedUser.getPageable(), pagedUser.getTotalElements()));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public PaginatedList<UserSearchResults> createPaginatedListByFullText(UserSearchCriteria userSearchCriteria, Integer page) {
        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(page).orElse(1) - 1, Constants.PAGING_SIZE);
        SearchTermAndField searchTermAndField = getSearchTermAndField(userSearchCriteria);
        org.apache.lucene.search.Sort sort = new org.apache.lucene.search.Sort(new SortField(UserSearchCriteria.USERNAME_FIELD + "Sort", SortField.Type.STRING));
        FullTextQuery userQuery = null;

        if (searchTermAndField.isEmpty()) {
            userQuery = userSearch.search("*", pageRequest.getOffset(), pageRequest.getPageSize(), sort);
        } else {
            userQuery = userSearch.search(searchTermAndField.getTermToArray(), searchTermAndField.getFieldToArray(),
                    pageRequest.getOffset(), pageRequest.getPageSize(), sort);
        }

        return new PaginatedList<>(new PageImpl<>(
                ((Stream<User>) userQuery.getResultStream())
                        .map(user -> UserSearchResults.of(user))
                        .collect(Collectors.toList()),
                pageRequest, userQuery.getResultSize()));
    }

    /**
     * 全文検索用の検索文字列、検索項目を取得する.
     *
     * @param userSearchCriteria
     *            ユーザ検索条件
     * @return 全文検索用の検索文字列、検索項目
     */
    private SearchTermAndField getSearchTermAndField(UserSearchCriteria userSearchCriteria) {
        SearchTermAndField searchTermAndField = new SearchTermAndField();

        Optional.ofNullable(userSearchCriteria.getUsername())
                .ifPresent(username -> searchTermAndField.addTermAndField(username, UserSearchCriteria.USERNAME_FIELD));
        Optional.ofNullable(userSearchCriteria.getEmail())
                .ifPresent(email -> searchTermAndField.addTermAndField(email, UserSearchCriteria.EMAIL_FIELD));

        return searchTermAndField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public void reindex() {
        userSearch.reindex();
    }
}
