package person.mmall.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.mmall.commom.Constant;
import person.mmall.commom.ServerResponse;
import person.mmall.commom.TokenCache;
import person.mmall.dao.UserMapper;
import person.mmall.pojo.User;
import person.mmall.service.IUserService;
import person.mmall.utils.MD5Util;

import java.util.UUID;

@Service("iUserService")
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {

        int resultCount = userMapper.checkUsername(username);

        if (0 == resultCount)
            return ServerResponse.CreateByErrorMessage("用户不存在");

        //MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username, md5Password);
        if (null == user)
            return ServerResponse.CreateByErrorMessage("密码错误");

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.CreateBySuccess("登录成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {

        ServerResponse validResponse = this.checkValid(user.getUsername(), Constant.USERNAME);

        if (!validResponse.isSuccess())
                return validResponse;

        validResponse = this.checkValid(user.getEmail(), Constant.EMAIL);

        if (!validResponse.isSuccess())
            return validResponse;

        user.setRole(Constant.Role.ROLE_CUSTOMER);
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (0 == resultCount)
            return ServerResponse.CreateByErrorMessage("注册失败");

        return ServerResponse.CreateBySuccessMessage("注册成功");
    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type))
        {
            int resultCount = -1;
            if (type.equals(Constant.USERNAME))
            {
                resultCount = userMapper.checkUsername(str);
                if (0 < resultCount)
                    return ServerResponse.CreateByErrorMessage("用户名已存在");
            }
            if (type.equals(Constant.EMAIL))
            {
                resultCount = userMapper.checkEmail(str);
                if (0 < resultCount)
                    return ServerResponse.CreateByErrorMessage("email已存在");
            }
        }else
            return ServerResponse.CreateBySuccessMessage("参数错误");

        return ServerResponse.CreateBySuccessMessage("检验成功");
    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {

        ServerResponse validResponse = this.checkValid(username, Constant.USERNAME);

        if (validResponse.isSuccess())
            return ServerResponse.CreateBySuccessMessage("用户不存在");

        String question = userMapper.selectQuestionByNmae(username);
        if (StringUtils.isNotBlank(question))
            return ServerResponse.CreateBySuccess(question);

        return ServerResponse.CreateByErrorMessage("找回密码的问题是空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {

        int resultCount = userMapper.checkAnswer(username, question, answer);

        if (0 < resultCount)
        {
            String forgetToken = UUID.randomUUID().toString();

            TokenCache.setKey(TokenCache.TOKEN_PREFIX+username, forgetToken);

            return ServerResponse.CreateBySuccess(forgetToken);
        }

        return ServerResponse.CreateByErrorMessage("问题的答案错误");
    }

    /**
     * 传token避免横向越权
     * @param username
     * @param passwordNew
     * @param forgetToken
     * @return
     */
    @Override
    public ServerResponse<String> forgetRestPassword(String username, String passwordNew, String forgetToken) {

        if (StringUtils.isBlank(forgetToken))
            return ServerResponse.CreateByErrorMessage("参数错误，token需要被传递");

        ServerResponse validResponse = this.checkValid(username, Constant.USERNAME);

        if (validResponse.isSuccess())
            return ServerResponse.CreateByErrorMessage("用户不存在");

        String token = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);

        if (StringUtils.isBlank(token))
            return ServerResponse.CreateByErrorMessage("token无效或者过期");

        if (StringUtils.equals(forgetToken, token))
        {
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);

            int resultCount = userMapper.updatePasswordByUsername(username,md5Password);
            if (0 < resultCount)
                return  ServerResponse.CreateBySuccessMessage("修改密码成功");

        }else
            return ServerResponse.CreateByErrorMessage("token错误，请重新获取修改密码的token");

        return ServerResponse.CreateByErrorMessage("修改密码失败");
    }

    @Override
    public ServerResponse<String> restPassword(String passwordOld, String passwordNew ,User user) {

        //防止横向越权的操作，验证用户的旧密码，一定要指定是这个用户
        String md5Password = MD5Util.MD5EncodeUtf8(passwordOld);

        int resultCount = userMapper.checkPassword(md5Password, user.getId());
        if (0 == resultCount)
            return ServerResponse.CreateByErrorMessage("旧密码错误");

        md5Password = MD5Util.MD5EncodeUtf8(passwordNew);

        user.setPassword(md5Password);

        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (0 < resultCount)
            return ServerResponse.CreateBySuccessMessage("密码修改成功");

        return ServerResponse.CreateByErrorMessage("密码修改失败");
    }

    @Override
    public ServerResponse<User> updateInformation(User user) {

        int resultCount = userMapper.checkEmailByUserId(user.getEmail(), user.getId());

        if (0 < resultCount)
            return ServerResponse.CreateByErrorMessage("email已存在，请尝试更换后再试");

        User updateUser = new User();

        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);

        if (0 < updateCount)
            return ServerResponse.CreateBySuccess("更新信息成功", updateUser);

        return ServerResponse.CreateByErrorMessage("更新信息失败");
    }

    @Override
    public ServerResponse<User> getInformation(Integer id) {

        User user = userMapper.selectByPrimaryKey(id);

        if (null == user)
            return ServerResponse.CreateByErrorMessage("找不到当前用户");

        user.setPassword(StringUtils.EMPTY);

        return ServerResponse.CreateBySuccess(user);
    }

    @Override
    public boolean isAdminRole(int role) {

        return role == Constant.Role.ROLE_ADMIN ? true : false;
    }
}
