package common.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 共通処理クラス.
 */
@Controller
public class MiscController extends BaseController {

    /**
     * トップ画面保存処理.
     *
     * @return 遷移先
     */
    @GetMapping("top")
    public String topRequest() {
        return "top";
    }

    /**
     * 管理トップ画面保存処理.
     *
     * @return 遷移先
     */
    @GetMapping("admin/top")
    public String adminTopRequest() {
        return "admin/top";
    }

    /**
     * マスタメンテトップ画面保存処理.
     *
     * @return 遷移先
     */
    @GetMapping("admin/master/top")
    public String adminMasterTopRequest() {
        return "admin/master/top";
    }

    /**
     * アクティブユーザ一覧画面保存処理.
     *
     * @return 遷移先
     */
    @GetMapping("admin/activeUsers")
    public String activeUsersRequest() {
        return "admin/activeUsers";
    }
}
