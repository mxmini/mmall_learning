package person.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import person.mmall.commom.ServerResponse;
import person.mmall.service.IProductService;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "categoryId",required = false) Integer categoryId,@RequestParam(value = "keyWord",required = false) String keyWord, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, @RequestParam(value = "orderBy", defaultValue = "") String orderBy){

        return iProductService.searchProduct(categoryId, keyWord, pageNum, pageSize, orderBy);
    }


    @RequestMapping(value = "detail.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse detail(Integer productId){

        return iProductService.productDetail(productId);
    }
}
