package person.mmall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.pojo.User;
import person.mmall.service.IOrderService;
import person.mmall.service.IUserService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        if (iUserService.isAdminRole(currentUser.getRole()))
            return  iOrderService.getAllList(pageSize,pageNum);

         return    ServerResponse.CreateByErrorMessage("用户未权限，请登录管理员权限");
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse search(HttpSession session, Long orderNo, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, @RequestParam(value = "pageValue", defaultValue = "1") Integer pageNum){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iOrderService.searchOrder(orderNo, pageSize, pageNum);

        return ServerResponse.CreateByErrorMessage("用户未权限，请登录管理员权限");
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iOrderService.orderDetail(orderNo);

        return ServerResponse.CreateByErrorMessage("用户未权限，请登录管理员权限");
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public  ServerResponse sendGoods(HttpSession session, Long orderNo){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iOrderService.sendGoods(orderNo);

        return ServerResponse.CreateByErrorMessage("用户未权限，请登录管理员权限");
    }
}
