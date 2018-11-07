package common.webapp.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import common.dto.PaginatedList;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.entity.Users;
import common.service.UserManager;

/**
 * ユーザ一覧処理クラス.
 */
@Controller
public class UserListController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * ユーザ一覧画面初期処理.
     *
     * @return ユーザ
     */
    @ModelAttribute("userSearchCriteria")
    public UserSearchCriteria getUserSearchCriteria() {
        return new UserSearchCriteria();
    }

    /**
     * ユーザ一覧検索CSV出力処理.
     *
     * @param model
     *            {@link ModelMap}
     * @param request
     *            {@link HttpServletRequest}
     * @param response
     *            {@link HttpServletResponse}
     * @return テンプレート名
     */
    @GetMapping("/admin/master/users.csv")
    public String setupCsvList(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("Application/Octet-Stream");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1) + "\"");
        model.addAttribute("csv", userManager.getUsers());

        return "admin/master/csv/users";
    }

    /**
     * ユーザ一覧検索XLS出力処理.
     *
     * @param model
     *            {@link ModelMap}
     * @param request
     *            {@link HttpServletRequest}
     * @param response
     *            {@link HttpServletResponse}
     * @return テンプレート名
     */
    @GetMapping("/admin/master/users.xlsx")
    public String setupXlsList(ModelMap model, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("Application/Vnd.ms-Excel");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1) + "\"");
        model.addAttribute("users", userManager.getUsers());

        return "admin/master/jxls/users";
    }

    /**
     * ユーザ一覧検索XML出力処理.
     *
     * @param request
     *            {@link HttpServletRequest}
     * @param response
     *            {@link HttpServletResponse}
     * @return ユーザ一覧
     */
    @GetMapping("/admin/master/users.xml")
    @ResponseBody
    public Users setupXmlList(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("text/xml");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1) + "\"");
        return new Users(userManager.getUsers());
    }

    /**
     * ユーザ一覧表示画面処理.
     *
     * @param userSearchCriteria
     *            ユーザ検索条件
     * @param page
     *            表示ページ数
     * @return 遷移先画面設定
     */
    @GetMapping("/admin/master/users")
    public PaginatedList<UserSearchResults> showForm(@ModelAttribute("userSearchCriteria") UserSearchCriteria userSearchCriteria, @RequestParam(value = "page", required = false) Integer page) {
        return userManager.createPaginatedList(userSearchCriteria, page);
    }

    /**
     * アカウント削除処理.
     *
     * @param userIds
     *            ユーザID一覧
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先
     */
    @DeleteMapping("/admin/master/users")
    public String onSubmit(@RequestParam("userIds") String[] userIds, HttpServletRequest request) {
        boolean logoutFlg = Arrays.stream(userIds)
                .anyMatch(userId -> userId.equals(String.valueOf(userManager.getUserByUsername(request.getRemoteUser()).getId())));

        Arrays.stream(userIds).forEach(userId -> userManager.removeUser(userId));

        saveFlashMessage(getText("deleted"));

        if (logoutFlg) {
            // 自分自身を削除した場合は強制ログアウト
            return "redirect:/logout";
        } else {
            return "redirect:/admin/master/users";
        }
    }
}
