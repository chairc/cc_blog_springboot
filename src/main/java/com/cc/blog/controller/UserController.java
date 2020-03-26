package com.cc.blog.controller;

import com.cc.blog.model.User;
import com.cc.blog.service.UserService;
import com.cc.blog.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 注册页面
     *
     * @return registered页面
     */

    @RequestMapping("/registered")
    public String showRegisteredPage() {
        return "registered";
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     * @param nickname
     * @param phone
     * @param email
     * @param sex
     * @param birthday
     * @param wechat
     * @param qq
     * @param weibo
     * @param question
     * @param answer
     * @param request
     * @return 1:成功 0：username password answer为空 -1：username重复
     */

    @RequestMapping("/registeredUserByAjax")
    @ResponseBody
    public Map<String, String> registeredUserByAjax(@RequestParam(value = "username", required = false) String username,
                                                    @RequestParam(value = "password", required = false) String password,
                                                    @RequestParam(value = "nickname", required = false) String nickname,
                                                    @RequestParam(value = "phone", required = false) String phone,
                                                    @RequestParam(value = "email", required = false) String email,
                                                    @RequestParam(value = "sex", required = false) String sex,
                                                    @RequestParam(value = "birthday", required = false) String birthday,
                                                    @RequestParam(value = "wechat", required = false) String wechat,
                                                    @RequestParam(value = "qq", required = false) String qq,
                                                    @RequestParam(value = "weibo", required = false) String weibo,
                                                    @RequestParam(value = "question", required = false) String question,
                                                    @RequestParam(value = "answer", required = false) String answer,
                                                    HttpServletRequest request) {
        //map集合用来存放返回值
        Map<String, String> map = new HashMap<String, String>();
        //验证username的唯一性
        if (userService.usernameValidate(username).equals(0)) {
            if (username != null && password != null && !username.equals("") && !password.equals("") && !answer.equals("")) {
                User user = new User();
                //将数据放入user中
                user.setUser_common_private_id(Tools.CreateUserRandomPrivateId());
                user.setUser_common_username(username);
                user.setUser_common_password(password);
                user.setUser_common_nickname(nickname);
                user.setUser_secret_phone(phone);
                user.setUser_secret_email(email);
                user.setUser_secret_sex(sex);
                user.setUser_secret_birthday(birthday);
                user.setUser_secret_wechat(wechat);
                user.setUser_secret_qq(qq);
                user.setUser_secret_weibo(weibo);
                user.setUser_safe_question(question);
                user.setUser_safe_answer(answer);
                user.setUser_safe_logtime(Tools.getServerTime());
                user.setUser_safe_ip(Tools.getUserIp(request));
                user.setUser_safe_system(Tools.getSystemVersion(request));
                user.setUser_safe_browser(Tools.getBrowserVersion(request));
                System.out.println(user);
                userService.insertUser(user);
                map.put("result", "1");
            } else {
                map.put("result", "0");
            }
        } else {
            map.put("result", "-1");
        }

        return map;
    }

    /**
     * 登录页面
     *
     * @param request
     * @return 根据session判断页面跳转
     */

    @RequestMapping("/login")
    public String showUserLoginPage(HttpServletRequest request) {
        String username = Tools.usernameSessionValidate(request);
        System.out.println("当前Session：" + username);
        if (username == null) {
            return "login";
        } else if (username.equals("SuperAdmin")) {
            return "redirect:/superAdmin/admin";
        }
        System.out.println("登录人员：" + username);

        return "redirect:/user";
    }

    /**
     * 登出
     *
     * @param request
     * @return 重定向：主页
     */

    @RequestMapping("/logout")
    public String userLogout(HttpServletRequest request) {
        System.out.println("登出人员：" + Tools.usernameSessionValidate(request));
        request.getSession().invalidate();

        return "redirect:/";
    }

    /**
     * Ajax登录
     *
     * @param username
     * @param password
     * @param request
     * @return map
     */

    @RequestMapping("/loginUserByAjax")
    @ResponseBody
    public Map<String, String> userLoginByAjax(@RequestParam("username") String username,
                                               @RequestParam("password") String password,
                                               HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();

        if (username != null && password != null && !username.equals("")
                && !password.equals("") && !username.equals("SuperAdmin")) {
            if (userService.loginUser(username, password) == 1) {
                request.getSession().setAttribute("username", username);
                map.put("result", "1");
            } else {
                map.put("result", "0");
            }
        } else {
            map.put("result", "-1");
        }
        return map;
    }

    /**
     * 展示所有用户信息
     *
     * @return mav
     */

    @RequestMapping("/user")
    public ModelAndView getUserAll() {
        ModelAndView mav = new ModelAndView("user");
        List<User> list = userService.getUserAll();
        mav.addObject("user", list);
        return mav;
    }

    @RequestMapping("/getUser1/{id}")
    public String getUserById(@PathVariable int id) {
        return userService.getUserById(id).toString();
    }

    @RequestMapping("/getUser/{id}")
    public ModelAndView getUserByIdTest1(@PathVariable int id) {
        ModelAndView mav = new ModelAndView("user");
        mav.addObject("user", userService.getUserById(id));
        return mav;
    }


}
