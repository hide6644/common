package common.webapp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import common.Constants;
import common.model.PaginatedList;
import common.model.User;
import common.service.UserManager;

/**
 * ユーザ一覧処理クラス.
 */
@Controller
@SessionAttributes("searchUser")
@RequestMapping("/admin/master/users*")
public class UserListController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /**
     * {@inheritDoc}
     */
    @InitBinder
    @Override
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(Constants.DATE_TIME_FORMAT), true));
    }

    /**
     * 画面入力値保持モデル初期化
     *
     * @return ユーザ
     */
    @ModelAttribute("searchUser")
    public User getSearchUser() {
        return new User();
    }

    /**
     * ユーザ一覧表示画面処理.
     *
     * @param user
     *            ユーザ
     * @param page
     *            表示ページ数
     * @return 遷移先jsp名
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequest(@ModelAttribute("searchUser") User user, @RequestParam(value = "page", required = false) Integer page) {
        PaginatedList<User> paginatedList = new PaginatedList<User>(page);
        paginatedList.setSearchCondition(user);
        paginatedList.setCurrentPage(userManager.getPaged(User.class, paginatedList));

        Model model = new ExtendedModelMap();
        model.addAttribute("paginatedList", paginatedList);

        return new ModelAndView("admin/master/users", model.asMap());
    }

    /**
     * アカウント削除処理.
     *
     * @param userIds
     *            ユーザID一覧
     * @param request
     *            {@link HttpServletRequest}
     * @return 遷移先jsp名
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public String onSubmit(@RequestParam("userIds") String[] userIds, HttpServletRequest request) {
        boolean logoutFlg = false;
        String remoteUserId = null;
        try {
            remoteUserId = String.valueOf(userManager.getUserByUsername(request.getRemoteUser()).getId());
        } catch (UsernameNotFoundException e) {
            remoteUserId = "";
        }

        for (int i = 0; i < userIds.length; i++) {
            if (userIds[i].equals(remoteUserId)) {
                logoutFlg = true;
            }

            userManager.removeUser(userIds[i]);
            saveFlashMessage(getText("deleted"));
        }

        if (logoutFlg) {
            // 自分自身を削除した場合は強制ログアウト
            return "redirect:/logout";
        } else {
            return "redirect:/admin/master/users";
        }
    }
}
