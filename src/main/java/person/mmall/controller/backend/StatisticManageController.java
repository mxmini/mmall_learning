package person.mmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.pojo.User;
import person.mmall.service.IStatisticService;
import person.mmall.service.IUserService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "manage/statistic/")
public class StatisticManageController {

    @Autowired
    private IStatisticService iStatisticService;

    @Autowired
    private IUserService iUserService;


    @RequestMapping(value = "base_count.do")
    @ResponseBody
    public ServerResponse baseCount(HttpSession session){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        if(iUserService.isAdminRole(currentUser.getRole()))
            return ServerResponse.CreateByErrorMessage("用户没权限，请登录管理员权限");

        return iStatisticService.baseCount();
    }

}
