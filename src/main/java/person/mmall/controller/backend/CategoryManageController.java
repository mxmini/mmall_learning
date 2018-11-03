package person.mmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.pojo.Category;
import person.mmall.pojo.User;
import person.mmall.service.ICategoryService;
import person.mmall.service.IUserService;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private ICategoryService iCategoryService;

    @Autowired
    private IUserService iUserService;
    /**
     * 获取类目子节点（平级）
     * @param session
     * @param categoryId
     * @return
     */
    @RequestMapping(value = "get_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<List<Category>> getCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "status: 10，用户未登录，需要强制登录");

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iCategoryService.getLevelCategory(categoryId);

        return ServerResponse.CreateBySuccessMessage("权限不够，请使用管理员账户登录");
    }

    /**
     * 添加类目
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "add_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse addCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId, String categoryName){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "status: 10，用户未登录，需要强制登录");

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iCategoryService.addCategory(categoryId, categoryName);

        return ServerResponse.CreateBySuccessMessage("权限不够，请使用管理员账户登录");
    }

    /**
     * 修改品类名称
     * @param session
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping(value = "set_category_name.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setCategoryName(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId, String categoryName) {

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "status: 10，用户未登录，需要强制登录");

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iCategoryService.setCategory(categoryId, categoryName);

        return ServerResponse.CreateBySuccessMessage("权限不够，请使用管理员账户登录");

    }

    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<Set<Category>> getDeepCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "status: 10，用户未登录，需要强制登录");

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iCategoryService.getLevelAndDeepCategory(categoryId);

        return ServerResponse.CreateBySuccessMessage("权限不够，请使用管理员账户登录");
    }

}
