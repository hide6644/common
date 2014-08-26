package common.webapp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
 * ユーザ画面処理クラス.
 *
 * @author hide6644
 */
@Controller
@SessionAttributes("searchUser")
@RequestMapping("/admin/master/users*")
public class UserListController extends BaseController {

    /** User処理クラス */
    @Autowired
    private UserManager userManager;

    /*
     * (非 Javadoc)
     *
     * @see
     * common.web.controller.BaseController#initBinder(org.springframework.web.bind.WebDataBinder)
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        super.initBinder(binder);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat(Constants.DATE_TIME_FORMAT), true));
    }

    /**
     * 検索条件初期化
     *
     * @return 画面入力値保持モデル
     */
    @ModelAttribute("searchUser")
    public User getSearchUser() {
        return new User();
    }

    /**
     * ユーザ一覧表示画面処理.
     *
     * @param user
     *            画面入力値保持モデル
     * @param page
     *            表示ページ数
     * @param model
     *            画面汎用値保持モデル
     * @return 遷移先jsp名
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView handleRequest(@ModelAttribute("searchUser") User user, @RequestParam(value = "page", required = false) Integer page) {
        PaginatedList<User> paginatedList = new PaginatedList<User>(page);
        paginatedList.setSearchCondition(user);
        paginatedList.setCurrentPage(userManager.getPaged(User.class, paginatedList));

        Model model = new ExtendedModelMap();
        model.addAttribute("paginatedList", paginatedList);

        return new ModelAndView("admin/master/userList", model.asMap());
    }
}
