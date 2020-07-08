package com.cc.blog.service.impl;

import com.cc.blog.mapper.UserDao;
import com.cc.blog.model.*;
import com.cc.blog.service.UserService;
import com.cc.blog.util.Tools;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserService userService;

    /**
     * 获取所有用户
     *
     * @return List<User>
     */

    @Override
    public List<User> getUserAll() {
        String username = Tools.usernameSessionValidate();
        User admin = userService.getUserByUsername(username);
        if(Tools.usernameSessionIsAdminValidate(admin.getUser_safe_role())){
            return userDao.getUserAll();
        }
        return null;
    }

    @Override
    public ResultSet getUserAllByAdmin(int pageNum) {
        ResultSet resultSet = new ResultSet();
        try {
            String username = Tools.usernameSessionValidate();
            User admin = userService.getUserByUsername(username);
            if(Tools.usernameSessionIsAdminValidate(admin.getUser_safe_role())){
                Page<User> userPages = PageHelper.startPage(pageNum, 10);
                List<User> userList = userDao.getUserAll();
                resultSet.success("超级管理员用户列表获取成功");
                resultSet.setData(userList);
            }else {
                resultSet.fail("超级管理员用户列表获取失败");
            }
        } catch (Exception e) {
            resultSet.error();
        }
        return resultSet;
    }

    /**
     * 通过ID获取用户
     *
     * @param id
     * @return User
     */

    @Override
    public User getUserById(int id) {
        String username = Tools.usernameSessionValidate();
        User admin = userService.getUserByUsername(username);
        if(Tools.usernameSessionIsAdminValidate(admin.getUser_safe_role())){
            return userDao.getUserById(id);
        }
        return null;
    }

    /**
     * 用户登录
     *
     * @param username
     * @param password
     * @return 1 或 0
     */

    @Override
    public Integer loginUser(String username, String password) {
        Integer flag = userDao.loginUser(username, password);
        System.out.println("登陆状态（1:success/2:failure）：" + flag);
        return flag;
    }

    /**
     * 查找私有ID获取用户
     *
     * @param privateId
     * @return 1 或 0
     */

    @Override
    public Integer getUserPrivateId(String privateId) {
        return userDao.getUserPrivateId(privateId);
    }

    /**
     * 通过用户名获取用户信息
     *
     * @param username
     * @return User
     */

    @Override
    public User getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    /**
     * 通过OpenId获取用户名
     *
     * @param openId
     * @return 用户名
     */

    @Override
    public User getUserByOpenId(String openId) {
        return userDao.getUserByOpenId(openId);
    }

    /**
     * 新增用户信息
     *
     * @param user
     */

    @Override
    public void insertUser(User user) {
        Integer flag = userService.getUserPrivateId(user.getUser_common_private_id());
        while (flag == 1) {
            user.setUser_common_private_id(Tools.CreateUserRandomPrivateId());
            flag = userService.getUserPrivateId(user.getUser_common_private_id());
        }
        userDao.insertUser(user);
    }

    /**
     * 通过ID删除用户信息（暂时停用此方法）
     *
     * @param id
     */

    @Override
    public void deleteUserById(int id) {
        userDao.deleteUserById(id);
    }

    /**
     * 更新用户信息（暂时停用此方法）
     *
     * @param user
     */

    @Override
    public void updateUser(User user) {
        userDao.updateUser(user);
    }

    /**
     * 用户名唯一性验证
     *
     * @param username
     * @return 1 或 0
     */

    @Override
    public Integer usernameValidate(String username) {
        return userDao.usernameValidate(username);
    }

    /**
     * 通过QQ快速登录来验证openId
     *
     * @param openId
     * @return 1 或 0
     */

    @Override
    public Integer openIdValidate(String openId) {
        return userDao.openIdValidate(openId);
    }

    /**
     * 获取用户数
     *
     * @return 用户数
     */

    @Override
    public Integer getUserCount() {
        return userDao.getUserCount();
    }

    /**
     * 获取用户权限
     *
     * @return 权限
     */

    @Override
    public Role getUserRole(String role) {
        return userDao.getUserRole(role);
    }

    /**
     * 获取用户身份许可
     *
     * @param permission
     * @return 许可
     */

    @Override
    public Permission getUserPermission(String permission) {
        return userDao.getUserPermission(permission);
    }

}
