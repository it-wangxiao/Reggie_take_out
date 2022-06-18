package com.itheima.reggie.web.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.utils.BaseContextUtil;
import com.itheima.reggie.web.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author wx
 * @version 1.0
 * @date 2022/6/8 15:52
 */
@WebFilter("/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        //1. 获取本次请求的URI
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();

        log.info(requestURI);
        String[] urls = {"/employee/login", "/user/login", "/user/sendMsg", "/employee/logout", "/backend/**", "/front/**"};

        //2. 判断本次请求, 是否需要登录, 才可以访问
        if (check(urls, requestURI)) {

            //3. 如果不需要，则直接放行
            filterChain.doFilter(request, response);
            return;
        }

        //4. 判断员工登录状态，如果已登录，则直接放行
        HttpSession session = request.getSession();
        Long employeeId = (Long) session.getAttribute("employeeId");

        if (employeeId != null) {
            BaseContextUtil.setCurrentId(employeeId);
            filterChain.doFilter(request, response);
            return;
        }

        //4.1判断用户登录状态，如果已登录，则直接放行
        Long userId = (Long) session.getAttribute("userId");

        if (userId != null) {
            log.info("用户已登录，用户id为：{}",userId);
            BaseContextUtil.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        //5. 如果未登录, 则返回未登录结果
        log.info("拦截url：{}",requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    private boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }

        return false;
    }
}
