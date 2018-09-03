package common.webapp.controller;

import java.io.IOException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import common.Constants;
import common.service.RoleManager;
import common.service.UserManager;

/**
 * 再読込処理クラス.
 */
@Controller
public class ReloadController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /** Role処理クラス */
    @Autowired
    private RoleManager roleManager;

    /**
     * マスタを再読み込し、プルダウンリストを生成する.
     * リファー情報を取得し、遷移元の画面へリダイレクトする.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @throws IOException
     *             {@link IOException}
     * @return 遷移先
     */
    @GetMapping("admin/reload")
    public String handleRequest(HttpServletRequest request) throws IOException {
        request.getSession().getServletContext().setAttribute(Constants.AVAILABLE_ROLES, (roleManager.getLabelValues()));
        userManager.reindex();

        String referer = request.getHeader("Referer");

        if (referer != null) {
            saveFlashMessage(getText("master.updated"));
            return "redirect:" + new URL(referer).getFile().substring(request.getContextPath().length());
        }

        return "redirect:/top";
    }
}
