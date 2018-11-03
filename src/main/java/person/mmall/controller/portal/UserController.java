package person.mmall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.pojo.User;
import person.mmall.service.IUserService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class UserController {

    @Autowired
    private IUserService iUserService;

    /**
     * 用户登录
     * @param username
     * @param password
     * @param session
     * @return
     * @ResponseBody注解： 将方法返回的数据直接带入Http响应体中
     */
    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> Login(String username, String password, HttpSession session){

        ServerResponse<User> response = iUserService.login(username, password);

        //登录成功保存客户的登录信息
        if (response.isSuccess())
            session.setAttribute(Constant.CURRENT_USER, response.getData());

        return response;
    }

    /**
     * 用户注册
     * @param user
     * @return
     */

    //直接传User对象，使用了springMVC的数据绑定
    @RequestMapping(value = "register.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){

        return iUserService.register(user);

    }

    /**
     * 检验数据的有效性
     * @param str
     * @param type
     * @return
     */
    @RequestMapping(value = "check_valid.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str, String type) {

        return iUserService.checkValid(str, type);
    }

    /**
     * 用户退出
     * @param session
     * @return
     */
    @RequestMapping(value = "logout.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpSession session){

        session.removeAttribute(Constant.CURRENT_USER);

        return ServerResponse.CreateBySuccess("退出成功");
    }

    /**
     * 获取登录用户的信息
     * @param session
     * @return
     */
    @RequestMapping(value = "get_user_info.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpSession session){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null != user)
            return ServerResponse.CreateBySuccess(user);

        return ServerResponse.CreateByErrorMessage("用户未登录，无法获取当前用户信息");
    }

    /**
     * 忘记密码，得到用户预先设定的答案的问题
     * @param username
     * @return
     */
    @RequestMapping(value = "forget_get_question.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQuestion(String username){

        return iUserService.selectQuestion(username);
    }

    /**
     * 忘记密码， 得到用户预先设定问题的答案
     * @param username
     * @param question
     * @param answer
     * @return
     */
    @RequestMapping(value = "forget_check_answer.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username, String question, String answer){

        return iUserService.checkAnswer(username, question, answer);
    }

    /**
     * 忘记密码，修改密码
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @RequestMapping(value = "forget_reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetResetPassword(String username, String passwordNew, String forgetToken){

        return iUserService.forgetRestPassword(username, passwordNew, forgetToken);
    }

    /**
     * 登录状态中修改密码
     * @param passwordOld
     * @param passwordNew
     * @param session
     * @return
     */
    @RequestMapping(value = "reset_password.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> restPassword(String passwordOld, String passwordNew, HttpSession session){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == user)
            return ServerResponse.CreateByErrorMessage("用户未登录");

        return iUserService.restPassword(passwordOld, passwordNew, user);
    }

    /**
     * 登录状态中修改用户的信息
     * @param user
     * @param session
     * @return
     */
    @RequestMapping(value = "update_information.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(User user, HttpSession session){

        User currentUser = (User) session.getAttribute(Constant.CURRENT_USER);

        if (null == currentUser)
            return ServerResponse.CreateByErrorMessage("用户未登录");

        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());

        ServerResponse<User> response = iUserService.updateInformation(user);
        if (response.isSuccess())
        {
            response.getData().setUsername(currentUser.getUsername());

            session.setAttribute(Constant.CURRENT_USER, response.getData());
        }

        return response;
   }

    /**
     * 获取当前登录用户的详细信息
     * @param session
     * @return
     */
   @RequestMapping(value = "get_information.do", method = RequestMethod.POST)
   @ResponseBody
   public ServerResponse<User> getInformation(HttpSession session){

        User user = (User) session.getAttribute(Constant.CURRENT_USER);
        if (null == user)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登录,需要强制登录status=10");

        return iUserService.getInformation(user.getId());
   }

}
