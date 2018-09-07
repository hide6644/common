package common.webapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import common.service.impl.BaseManagerImpl;
import common.service.mail.MailEngine;
import common.webapp.filter.FlashMap;

/**
 * 画面処理の基底クラス.
 */
public abstract class BaseController extends BaseManagerImpl {

    /** メールを送信するクラス */
    @Autowired(required = false)
    protected MailEngine mailEngine;

    /** Simple Mailメッセージモデル */
    @Autowired(required = false)
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
        saveFlash("flash_error_messages", error);
    }

    /**
     * FlashMapに例外からのエラーメッセージを設定する.
     *
     * @param e
     *            実行時例外
     */
    protected void saveFlashError(RuntimeException e) {
        saveFlashError(getText(e.getMessage()));
    }

    /**
     * FlashMapにメッセージを設定する.
     *
     * @param message
     *            メッセージ
     */
    protected void saveFlashMessage(String message) {
        saveFlash("flash_info_messages", message);
    }

    /**
     * FlashMapにメッセージを設定する.
     *
     * @param key
     *            キー
     * @param message
     *            メッセージ
     */
    protected void saveFlash(String key, String message) {
        FlashMap.add(key, message);
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
     *            実行時例外
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
     * Request変数に要素を追加する.
     *
     * @param key
     *            キー
     * @param value
     *            要素
     */
    protected void save(String key, String value) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        @SuppressWarnings("unchecked")
        List<String> messages = Optional.ofNullable((List<String>) request.getAttribute(key)).orElseGet(ArrayList::new);
        messages.add(value);
        request.setAttribute(key, messages);
    }
}
