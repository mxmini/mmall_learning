package person.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.pojo.User;
import person.mmall.service.IOrderService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order/")
public class OrderController {

    @Autowired
    private IOrderService orderService;

    @RequestMapping("create.do")
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return orderService.createOrder(currentUser.getId(), shippingId);
    }

    @RequestMapping("get_order_cart_product.do")
    @ResponseBody
    public  ServerResponse getOrderCartProduct(HttpSession session){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);
        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return orderService.getOrderCartProduct(currentUser.getId());
    }

    public ServerResponse list(HttpSession session, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum ){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);
        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return orderService.orderList(currentUser.getId(), pageSize, pageNum);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse detail(HttpSession session, Long orderNo){

        User currentUser = (User)session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return orderService.orderDetail(orderNo);
    }

    public ServerResponse cancel(HttpSession session, Long orderNo){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return orderService.cancelOrder(currentUser.getId(),orderNo);
    }
}
