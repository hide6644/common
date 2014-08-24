package common.webapp.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import common.exception.DBCheckException;
import common.exception.DBException;
import common.service.MailEngine;
import common.service.util.SecurityUtil;
import common.service.util.ValidateUtil;
import common.webapp.filter.FlashMap;

/**
 * 画面処理クラスの基底クラス.
 *
 * @author hide6644
 */
public abstract class BaseController {

    /** ログ処理クラス */
    protected final transient Log log = LogFactory.getLog(getClass());

    /** メッセージソースアクセサー */
    protected MessageSourceAccessor messages;

    /** メールを送信するクラス */
    @Autowired
    protected MailEngine mailEngine;

    /** Simple Mailメッセージモデル */
    @Autowired
    protected SimpleMailMessage mailMessage;

    /**
     * 入力値を型変換するためのカスタムエディターを初期化する.
     *
     * @param binder
     *            データバインダ
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
        binder.registerCustomEditor(Long.class, new CustomNumberEditor(Long.class, true));
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
    }

    /**
     * FlashMapにエラーメッセージを設定する.
     *
     * @param error
     *            エラーメッセージ
     */
    protected void saveFlashError(String error) {
        FlashMap.add("flash_error_messages", error);
    }

    /**
     * FlashMapに例外からのエラーメッセージを設定する.
     *
     * @param e
     *            例外クラス
     */
    protected void saveFlashError(DBException e) {
        saveFlashError(getText(e.getMessage()));
    }

    /**
     * FlashMapにメッセージを設定する.
     *
     * @param message
     *            メッセージ
     */
    protected void saveFlashMessage(String message) {
        FlashMap.add("flash_info_messages", message);
    }

    /**
     * エラーメッセージを設定する.
     *
     * @param error
     *            エラーメッセージ
     */
    protected void saveError(String error) {
        save("error_messages", error);
    }

    /**
     * 例外からエラーメッセージを設定する.
     *
     * @param e
     *            例外クラス
     */
    protected void saveError(RuntimeException e) {
        saveError(getText(e.getMessage()));
    }

    /**
     * メッセージを設定する.
     *
     * @param message
     *            メッセージ
     */
    protected void saveMessage(String message) {
        save("info_messages", message);
    }

    /**
     * Requestの指定されたキーに要素を追加する.
     *
     * @param key
     *            キー
     * @param value
     *            要素
     */
    protected void save(String key, String value) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) request.getAttribute(key);

        if (messages == null) {
            messages = new ArrayList<String>();
        }

        messages.add(value);
        request.setAttribute(key, messages);
    }

    /**
     * Requestの指定されたキーに要素を追加する.
     *
     * @param key
     *            キー
     * @param list
     *            要素リスト
     */
    protected void save(String key, List<String> list) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        request.setAttribute(key, list);
    }

    /**
     * 例外を入力値チェックに設定する.
     *
     * @param result
     *            入力値チェック結果
     * @param e
     *            例外クラス
     */
    protected void rejectValue(BindingResult result, DBException e) {
        if (e instanceof DBCheckException) {
            for (Map<String, Serializable> error : ((DBCheckException) e).getAllErrors()) {
                String filedName = (String) error.get(ValidateUtil.FILED_NAME);
                String massage = (String) error.get(ValidateUtil.MESSAGE);
                Object[] args = (Object[]) error.get(ValidateUtil.ARGS);

                if (filedName != null && filedName.length() > 0) {
                    result.rejectValue(filedName, massage, args, getText(massage));
                } else {
                    saveError(getText(massage, args));
                }
            }
        } else {
            saveError(e);
        }
    }

    /**
     * メッセージソースからメッセージを取得する.
     *
     * @param msgKey
     *            キー
     * @return メッセージ
     */
    protected String getText(String msgKey) {
        return getText(msgKey, "");
    }

    /**
     * メッセージソースからメッセージを取得する.
     *
     * @param msgKey
     *            キー
     * @param args
     *            引数
     * @return メッセージ
     */
    protected String getText(String msgKey, Object... args) {
        return messages.getMessage(msgKey, args, Locale.getDefault());
    }

    /**
     * ログインユーザ名を取得する.
     *
     * @return ログインユーザ名
     */
    protected String getLoginUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null ? "" : SecurityUtil.getCurrentAccount(auth).getUsername();
    }

    /**
     * メッセージソースを設定する.
     *
     * @param messageSource
     *            メッセージソース
     */
    @Autowired
    public void setMessages(MessageSource messageSource) {
        messages = new MessageSourceAccessor(messageSource);
    }
}
