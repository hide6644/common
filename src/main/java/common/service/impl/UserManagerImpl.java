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

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import common.Constants;
import common.dao.HibernateSearch;
import common.dao.UserDao;
import common.dto.PaginatedList;
import common.dto.PasswordForm;
import common.dto.SignupUserForm;
import common.dto.UploadForm;
import common.dto.UploadResult;
import common.dto.UserDetailsForm;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.entity.Role;
import common.entity.User;
import common.exception.DatabaseException;
import common.service.PasswordTokenManager;
import common.service.RoleManager;
import common.service.UserManager;
import common.service.mail.UserMail;
import common.webapp.converter.FileType;
import common.webapp.converter.UserFileConverterFactory;

/**
 * ユーザ処理の実装クラス.
 */
@Service("userManager")
public class UserManagerImpl extends BaseManagerImpl implements UserManager {

    /** ユーザDAO */
    private UserDao userDao;

    /** UserのHibernate Search DAO */
    @Autowired
    @Qualifier("userSearch")
    private HibernateSearch<User> userSearch;

    /** パスワードエンコーダー */
    @Autowired(required = false)
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;

    /** パスワードトークン処理のクラス */
    @Autowired(required = false)
    private PasswordTokenManager passwordTokenManager;

    /** Role処理クラス */
    private RoleManager roleManager;

    /** Userメール処理クラス */
    @Autowired
    private UserMail userMail;

    /** 検証ツールクラス */
    @Autowired
    private Validator validator;

    /**
     * コンストラクタ.
     *
     * @param userDao
     *            ユーザDAO
     * @param roleManager
     *            Role処理クラス
     */
    @Autowired
    public UserManagerImpl(UserDao userDao, RoleManager roleManager) {
        this.userDao = userDao;
        this.roleManager = roleManager;
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
    public User getUser(String userId) {
        return userDao.getOne(Long.valueOf(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserDetailsForm getUserDetails(User user) {
        UserDetailsForm userDetailsForm = new UserDetailsForm();
        BeanUtils.copyProperties(user, userDetailsForm);
        return userDetailsForm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUserDetails(UserDetailsForm userDetailsForm) {
        User user = null;

        if (userDetailsForm.getVersion() == null) {
            // 登録の場合
            user = new User();
            BeanUtils.copyProperties(userDetailsForm, user);
        } else {
            user = getUserByUsername(userDetailsForm.getUsername());
            // 画面で入力可能な項目のみコピーする
            if (userDetailsForm.getRoles().isEmpty()) {
                BeanUtils.copyProperties(userDetailsForm, user, "password", "enabled", "accountLocked", "accountExpiredDate", "credentialsExpiredDate", "roles");
            } else {
                BeanUtils.copyProperties(userDetailsForm, user, "password");
                user.setRoles(roleManager.getRoles(user.getRoles()));
            }
        }

        return saveUser(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User saveUser(User user) {
        String currentPassword = null;

        if (user.getVersion() == null) {
            // 登録の場合
            user.setUsername(user.getUsername().toLowerCase());
            user.setRoles(roleManager.getRoles(user.getRoles()));
        } else {
            currentPassword = userDao.findPasswordById(user.getId());
        }

        if (passwordEncoder != null) {
            user.setPassword(passwordEncode(currentPassword, user.getPassword()));
        } else {
            log.warn("PasswordEncoder not set, skipping password encryption...");
        }

        try {
            return userDao.saveAndFlush(user);
        } catch (Exception e) {
            user.setPassword(null);
            user.setConfirmPassword(null);

            if (user.getVersion() == null) {
                throw new DatabaseException("errors.insert", e);
            } else {
                throw new DatabaseException("errors.update", e);
            }
        }
    }

    /**
     * ユーザのパスワードを暗号化する.
     *
     * @param currentPassword
     *            現在のパスワード
     * @param newPassword
     *            新しいパスワード
     * @return 暗号化済みパスワード
     */
    private String passwordEncode(String currentPassword, String newPassword) {
        boolean passwordChanged = false;

        if (currentPassword == null) {
            // 登録の場合
            passwordChanged = true;
        } else {
            // 更新の場合
            if (!currentPassword.equals(newPassword)) {
                passwordChanged = true;
            }
        }

        if (passwordChanged) {
            // パスワードが変更有りの場合
            return passwordEncoder.encode(newPassword);
        } else {
            return newPassword;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUser(User user) {
        userDao.delete(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeUser(String userId) {
        userDao.deleteById(Long.valueOf(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            saveUser(user);
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
    public User saveSignupUser(SignupUserForm signupUser) {
        User user = new User();
        BeanUtils.copyProperties(signupUser, user);
        // デフォルトの要再認証日時を設定する
        user.setCredentialsExpiredDate(LocalDateTime.now().plusDays(Constants.CREDENTIALS_EXPIRED_TERM));
        // 新規登録時は権限を一般で設定する
        user.addRole(new Role(Constants.USER_ROLE));
        User managedUser = saveUser(user);

        // 登録完了メールを送信する
        userMail.sendSignupEmail(managedUser);

        return managedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enableUser(String username) {
        User user = getUserByUsername(username);
        user.setConfirmPassword(user.getPassword());
        user.setEnabled(true);

        // 登録した"username"、"password"でログイン処理を行う
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        userDao.saveAndFlush(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void lockoutUser(String username) {
        User user = getUserByUsername(username);
        user.setConfirmPassword(user.getPassword());
        user.setAccountLocked(true);
        userDao.saveAndFlush(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecoveryTokenValid(String username, String token) {
        return isRecoveryTokenValid(getUserByUsername(username), token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecoveryTokenValid(User user, String token) {
        return passwordTokenManager.isRecoveryTokenValid(user, token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recoveryPassword(String username) {
        userMail.sendPasswordRecoveryEmail(getUserByUsername(username));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User updatePassword(PasswordForm passwordForm) {
        User user = getUserByUsername(passwordForm.getUsername());

        if (isRecoveryTokenValid(user, passwordForm.getToken())) {
            user.setPassword(passwordEncode(user.getPassword(), passwordForm.getNewPassword()));
            user.setEnabled(true);
            user = userDao.saveAndFlush(user);

            userMail.sendUpdatePasswordEmail(user);
        } else if (passwordForm.getCurrentPassword() != null && passwordEncoder.matches(passwordForm.getCurrentPassword(), user.getPassword())) {
            user.setPassword(passwordEncode(user.getPassword(), passwordForm.getNewPassword()));
            user = userDao.saveAndFlush(user);
        } else {
            throw new IllegalArgumentException();
        }

        return user;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
    public PaginatedList<UserSearchResults> createPaginatedListByFullText(UserSearchCriteria userSearchCriteria, Integer page) {
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

        PageRequest pageRequest = PageRequest.of(Optional.ofNullable(page).orElse(1) - 1, Constants.PAGING_SIZE);
        Stream<User> userList = null;
        Long userCount = null;

        if (searchTermList.isEmpty()) {
            userList = userSearch.search("*", pageRequest.getPageNumber(), pageRequest.getPageSize());
            userCount = userSearch.count("*");
        } else {
            String[] searchTerms = searchTermList.toArray(new String[searchTermList.size()]);
            String[] searchFields = searchFieldList.toArray(new String[searchFieldList.size()]);
            userList = userSearch.search(searchTerms, searchFields, pageRequest.getPageNumber(), pageRequest.getPageSize());
            userCount = userSearch.count(searchTerms, searchFields);
        }

        return new PaginatedList<>(new PageImpl<>(
                userList
                        .sorted(Comparator.comparing(user -> user.getUsername()))
                        .map(user -> UserSearchResults.of(user))
                        .collect(Collectors.toList()),
                pageRequest, userCount));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reindex() {
        userSearch.reindex();
    }
}
