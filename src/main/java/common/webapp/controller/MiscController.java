package common.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 共通処理クラス.
 */
@Controller
public class MiscController extends BaseController {

    /**
     * トップ画面保存処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "top", method = RequestMethod.GET)
    public String topRequest() {
        return "top";
    }

    /**
     * 管理トップ画面保存処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "admin/top", method = RequestMethod.GET)
    public String adminTopRequest() {
        return "admin/top";
    }

    /**
     * マスタメンテトップ画面保存処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "admin/master/top", method = RequestMethod.GET)
    public String adminMasterTopRequest() {
        return "admin/master/top";
    }

    /**
     * アクティブユーザ一覧画面保存処理.
     *
     * @return 遷移先jsp名
     */
    @RequestMapping(value = "admin/activeUsers", method = RequestMethod.GET)
    public String activeUsersRequest() {
        return "admin/activeUsers";
    }
}
