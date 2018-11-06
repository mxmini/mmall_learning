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
import person.mmall.service.ICartService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class CartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list,do")
    @ResponseBody
    public ServerResponse list(HttpSession session){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.cartList(currentUser.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpSession session, Integer productId, Integer count){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.addCart(currentUser.getId(),productId, count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpSession session, Integer productId, Integer count){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.updateCart(currentUser.getId(),productId, count);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public ServerResponse deleteProduct(HttpSession session, String productIds){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.removeProductFromCart(currentUser.getId(),productIds);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse select(HttpSession session, Integer productId){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.selectProduct(currentUser.getId(),productId);
    }

    @RequestMapping("un_select.do")
    @ResponseBody
    public ServerResponse unSelect(HttpSession session, Integer productId){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.unSelectProduct(currentUser.getId(),productId);
    }

    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public  ServerResponse getCartProductCount(HttpSession session ){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.queryCartProductCount(currentUser.getId());
    }

    @RequestMapping("select_all.do")
    @ResponseBody
    public ServerResponse selectAll(HttpSession session){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.selectAllProductAtCart(currentUser.getId());
    }

    @RequestMapping("un_select_all.do")
    @ResponseBody
    public ServerResponse unSelctAll(HttpSession session){
        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        return iCartService.unSelectAllProductAtCart(currentUser.getId());
    }
}
