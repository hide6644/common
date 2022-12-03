package common.webapp.controller;

import java.io.Serializable;
import java.util.Arrays;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.opencsv.bean.ColumnPositionMappingStrategy;

import common.dto.PaginatedList;
import common.dto.UserSearchCriteria;
import common.dto.UserSearchResults;
import common.entity.User;
import common.entity.Users;
import common.service.UserManager;
import common.service.UsersManager;

/**
 * ユーザ一覧処理クラス.
 */
@Controller
@SessionAttributes("userSearchCriteria")
public class UsersController extends BaseController {

    /** ユーザ処理クラス */
    @Autowired
    private UserManager userManager;

    /** 複数ユーザ処理クラス */
    @Autowired
    private UsersManager usersManager;

    /**
     * ユーザ一覧画面初期処理.
     *
     * @return ユーザ
     */
    @ModelAttribute("userSearchCriteria")
    public UserSearchCriteria getUserSearchCriteria(SessionStatus status) {
        status.setComplete();
        return new UserSearchCriteria();
    }

    /**
     * ユーザ一覧検索CSV出力処理.
     *
     * @param model
     *            {@link Model}
     * @param request
     *            {@link HttpServletRequest}
     * @param response
     *            {@link HttpServletResponse}
     * @return テンプレート名
     */
    @GetMapping("/admin/master/users.csv")
    public String setupCsvList(Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("Application/Octet-Stream");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1) + "\"");
        model.addAttribute("csv", usersManager.getUsers());
        ColumnPositionMappingStrategy<Serializable> strat = new ColumnPositionMappingStrategy<>();
        strat.setType(User.class);
        model.addAttribute("strategy", strat);

        return "admin/master/csv/users";
    }

    /**
     * ユーザ一覧検索XLS出力処理.
     *
     * @param model
     *            {@link Model}
     * @param request
     *            {@link HttpServletRequest}
     * @param response
     *            {@link HttpServletResponse}
     * @return テンプレート名
     */
    @GetMapping("/admin/master/users.xlsx")
    public String setupXlsList(Model model, HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("Application/Vnd.ms-Excel");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + request.getRequestURI().substring(request.getRequestURI().lastIndexOf('/') + 1) + "\"");
        model.addAttribute("users", usersManager.getUsers());

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
        return new Users(usersManager.getUsers());
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
        return usersManager.createPaginatedList(userSearchCriteria, page);
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
