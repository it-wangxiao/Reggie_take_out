package com.itheima.reggie.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession session;

    /**
     * 发送验证码
     *
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) {
        //获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotBlank(phone)) {

            //获取验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("{}手机号的验证码为{}", phone, code);
            log.info(code);

            //发送短信
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);
            session.setAttribute(phone, code);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");

    }

    /**
     * 登录
     *
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map) {
        log.info("{}", map);
        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        String codeSession = (String) session.getAttribute(phone);
        if (codeSession != null) {

            if (code != null && code.equals(codeSession)) {
                LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
                qw.eq(User::getPhone, phone);
                User user = userService.getOne(qw);
                if (user == null) {
                    user = new User();
                    user.setPhone(phone);
                    user.setStatus(1);
                    userService.save(user);
                }

                session.setAttribute("userId", user.getId());

                return R.success("登录成功", user);
            }
            return R.error("登录失败");
        }
        return R.error("请先获取验证码");
    }

    /**
     * 登出
     *
     * @return
     */
    @PostMapping("loginout")
    public R loginOut() {
        session.removeAttribute("userId");
        return R.success("登出成功");
    }


}