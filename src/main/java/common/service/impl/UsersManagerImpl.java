package common.service.impl;

import static common.dao.jpa.UserSpecifications.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

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
import common.dao.UserSearch;
import common.dao.jpa.UserDao;
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
    private UserSearch userSearch;

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
        var uploadResult = new UploadResult(2); // 1行目はヘッダー行のため、2から開始する
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
                        String message = error.getMessage().replace("{0}", fieldName);
                        return uploadResult.createUploadError(fieldName, message);
                    })
                    .toList());
        }

        uploadResult.addProcessingCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedList<UserSearchResults> createPaginatedList(UserSearchCriteria userSearchCriteria, Integer page) {
        var pageRequest = PageRequest.of(Optional.ofNullable(page).orElse(1) - 1, Constants.PAGING_SIZE, Sort.by(UserSearchCriteria.USERNAME_FIELD));
        Page<User> pagedUser = userDao.findAll(
                usernameContains(userSearchCriteria.getUsername()).and(emailContains(userSearchCriteria.getEmail())),
                pageRequest);

        return new PaginatedList<>(new PageImpl<>(
                pagedUser.stream()
                        .map(user -> UserSearchResults.of(user))
                        .toList(),
                pagedUser.getPageable(), pagedUser.getTotalElements()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public PaginatedList<UserSearchResults> createPaginatedListByFullText(UserSearchCriteria userSearchCriteria, Integer page) {
        var pageRequest = PageRequest.of(Optional.ofNullable(page).orElse(1) - 1, Constants.PAGING_SIZE);
        List<User> result = userSearch.search(userSearchCriteria, pageRequest);

        return new PaginatedList<>(new PageImpl<>(
                result.stream()
                        .map(user -> UserSearchResults.of(user))
                        .toList(),
                pageRequest, result.size()));
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
