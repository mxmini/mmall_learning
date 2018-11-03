package person.mmall.controller.backend;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.pojo.Product;
import person.mmall.pojo.User;
import person.mmall.service.IFileService;
import person.mmall.service.IProductService;
import person.mmall.service.IUserService;
import person.mmall.utils.PropertiesUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@Controller
@RequestMapping("/manager/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    @RequestMapping(value = "list.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse productList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iProductService.getProductList(pageNum, pageSize);

        return ServerResponse.CreateByErrorMessage("用户没有权限，需要管理员权限");
    }

    @RequestMapping(value = "search.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse searchedProduct(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize, Integer productId, String productName){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iProductService.getSearchProductList(pageNum, pageSize, productId, productName);

        return ServerResponse.CreateByErrorMessage("用户没有权限，需要管理员权限");
    }


    /**
     * 图片上传
     * @param request
     * @param file
     * @return
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpServletRequest request, @RequestParam(value = "upload_file", required = false) MultipartFile file){

        User currentUser = (User) request.getSession(false).getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");

        if (iUserService.isAdminRole(currentUser.getRole()))
        {
            String path = request.getSession(false).getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file,path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix");
            Map fileMap = Maps.newHashMap();

            fileMap.put("uri", targetFileName);
            fileMap.put("url", url);

            return ServerResponse.CreateBySuccess(fileMap);
        }

        return ServerResponse.CreateByErrorMessage("用户没有权限，需要管理员权限");
    }

    /**
     * 商品详情
     * @param session
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse productInformation(HttpSession session, Integer productId){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");

        if (iUserService.isAdminRole(currentUser.getRole()))
            return iProductService.getProductInformation(productId);

        return ServerResponse.CreateByErrorMessage("用户没有权限，需要管理员权限");
    }

    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richTextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request){

        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
//        {
//            "success": true/false,
//                "msg": "error message", # optional
//            "file_path": "[real file path]"
//        }

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);
        Map resultMap = Maps.newHashMap();

        if (null == currentUser){

            resultMap.put("success", false);
            resultMap.put("msg", "用户未登录，需要强制登录");

            return resultMap;
        }

        if (iUserService.isAdminRole(currentUser.getRole()))
        {
            String path = request.getSession().getServletContext().getRealPath("upload");
            String targetFileName = iFileService.upload(file, path);
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix");

            resultMap.put("success", true);
            resultMap.put("msg", "上传成功");
            resultMap.put("file_path",url + targetFileName);

            return resultMap;
        }

        resultMap.put("success", false);
        resultMap.put("msg", "用户没有权限，需要管理员权限");
        return resultMap;
    }

    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){

        User current = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == current)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        if (iUserService.isAdminRole(current.getRole()))
            return iProductService.setProductStatus(productId, status);

        return ServerResponse.CreateByErrorMessage("用户没有权限，需要管理员权限");
    }

    @RequestMapping(value = "save.do", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse save(HttpSession session, Product product){

        User current = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == current)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());

        if (iUserService.isAdminRole(current.getRole()))
            return iProductService.addOrUpdateProduct(product);

        return ServerResponse.CreateByErrorMessage("用户没有权限，需要管理员权限");
    }
}
