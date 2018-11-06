package person.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.pojo.Shipping;
import person.mmall.pojo.User;
import person.mmall.service.IShippingService;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import java.rmi.ServerError;


@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    private IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Shipping shipping){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingService.addShipping(currentUser.getId(), shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse delete(HttpSession session, Integer shippingId){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingService.delShipping(currentUser.getId(), shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Shipping shipping){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingService.updateShipping(currentUser.getId(), shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer shippingId){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingService.selectShipping(currentUser.getId(), shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse list(HttpSession session, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iShippingService.shippingList(currentUser.getId(), pageSize, pageNum);
    }
}
