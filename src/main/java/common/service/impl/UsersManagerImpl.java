package common.service.impl;

import static common.dao.jpa.UserSpecifications.*;
import static org.springframework.data.jpa.domain.Specification.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

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
    private HibernateSearch<User> userSearch;

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
        uploadForm.setUploadResult(new UploadResult(2)); // 1行目はヘッダー行のため、2から開始する

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
                }).forEach(user -> validateAndSaveUploadedUser(uploadForm, user));
    }

    /**
     * 検証し保存する.
     *
     * @param uploadForm
     *            アップロードファイルの情報
     * @param user
     *            ユーザ
     */
    private void validateAndSaveUploadedUser(UploadForm uploadForm, User user) {
        Set<ConstraintViolation<User>> results = validator.validate(user);

        if (results.isEmpty()) {
            // エラー無しの場合、保存する
            userManager.saveUser(user);
            uploadForm.getUploadResult().addSuccessTotalCount();
        } else {
            // エラー有りの場合、エラーメッセージを設定する
            uploadForm.getUploadResult().addUploadErrors(results.stream()
                    .sorted(Comparator.<ConstraintViolation<User>, String>comparing(violation -> violation.getPropertyPath().toString())
                            .thenComparing(violation -> violation.getMessage()))
                    .map(error -> {
                        String fieldName = getText("user." + error.getPropertyPath().toString());
                        String message = error.getMessage().replaceAll("\\{0\\}", fieldName);
                        return uploadForm.getUploadResult().createUploadError(fieldName, message);
                    })
                    .collect(Collectors.toList()));
        }

        uploadForm.getUploadResult().addProcessingCount();
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
    @Override
    @Transactional(readOnly = true)
    public PaginatedList<UserSearchResults> createPaginatedListByFullText(UserSearchCriteria userSearchCriteria, Integer page) {
        String[][] searchTermAndField = getSearchTermAndField(userSearchCriteria);
        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(page).orElse(1) - 1, Constants.PAGING_SIZE);
        Stream<User> userList = null;
        Long userCount = null;

        if (searchTermAndField[0].length == 0) {
            userList = userSearch.search("*", pageRequest.getPageNumber(), pageRequest.getPageSize());
            userCount = userSearch.count("*");
        } else {
            userList = userSearch.search(searchTermAndField[0], searchTermAndField[1], pageRequest.getPageNumber(), pageRequest.getPageSize());
            userCount = userSearch.count(searchTermAndField[0], searchTermAndField[1]);
        }

        return new PaginatedList<>(new PageImpl<>(
                userList
                        .sorted(Comparator.comparing(user -> user.getUsername()))
                        .map(user -> UserSearchResults.of(user))
                        .collect(Collectors.toList()),
                pageRequest, userCount));
    }

    /**
     * 全文検索用の検索文字列、検索項目を取得する.
     *
     * @param userSearchCriteria
     *            ユーザ検索条件
     * @return [0]:検索文字列の配列、[1]:検索項目の配列
     */
    private String[][] getSearchTermAndField(UserSearchCriteria userSearchCriteria) {
        List<String> searchTermList = new ArrayList<>();
        List<String> searchFieldList = new ArrayList<>();

        Optional.ofNullable(userSearchCriteria.getUsername()).ifPresent(username -> {
            searchTermList.add(username);
            searchFieldList.add(UserSearchCriteria.USERNAME_FIELD);
        });
        Optional.ofNullable(userSearchCriteria.getEmail()).ifPresent(email -> {
            searchTermList.add(email);
            searchFieldList.add(UserSearchCriteria.EMAIL_FIELD);
        });

        return new String[][] { searchTermList.toArray(new String[searchTermList.size()]), searchFieldList.toArray(new String[searchFieldList.size()]) };
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
