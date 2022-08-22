package com.pickme.reggie.controller.filter;

import com.alibaba.fastjson.JSON;
import com.pickme.reggie.common.BaseContext;
import com.pickme.reggie.common.R;
import com.pickme.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 管理端登录过滤器
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    // AntPathMatcher 是Spring中提供的路径匹配器，支持通配符格式
    // ?  匹配一个字符
    // *  匹配0个或多个字符
    // ** 匹配0个或多个目录/字符
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的URI
        String uri = request.getRequestURI();
        //log.info("拦截到请求URI：" + uri);

        //定义不需要过滤处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };

        //判断本次请求是否需要过滤处理
        if (check(urls,uri)) {
            filterChain.doFilter(request,response);
            return;
        }

        //获取Session中的数据（登录用户id），判断是否已经登录
        Long id = (Long) request.getSession().getAttribute("employee");
        if (id != null) {
            //将登录的用户id储存到ThreadLocal中
            BaseContext.setCurrentId(id);
            //已登录，放行
            filterChain.doFilter(request,response);
        } else {
            //未登录，跳转
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        }
    }

    /**
     * 路径匹配，检查本次的请求uri是否为不需要过滤处理的请求路径
     * @param urls 请求路径数组
     * @param requestURI 请求uri
     * @return
     */
    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            if (ANT_PATH_MATCHER.match(url,requestURI)) return true;
        }
        return false;
    }
}
