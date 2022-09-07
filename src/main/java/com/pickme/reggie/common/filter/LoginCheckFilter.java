package com.pickme.reggie.common.filter;

import com.alibaba.fastjson.JSON;
import com.pickme.reggie.common.Res;
import com.pickme.reggie.common.util.LocalContext;
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

    //定义不需要过滤处理的请求路径
    private final String[] urls = new String[]{
            "/employee/login",
            "/employee/logout",
            "/backend/**",
            "/front/**",
            "/common/**",
            "/user/sendMsg", //移动端发送验证码
            "/user/login",   //移动端登录
            "/doc.html",
            "/webjars/**",
            "/swagger-resources",
            "/v2/api-docs"
    };

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的URI
        String uri = request.getRequestURI();
        log.info("拦截到请求URI：" + uri);

        //判断本次请求是否需要过滤处理
        if (check(urls,uri)) {
            filterChain.doFilter(request,response);
            return;
        }

        //后台管理端：获取Session中的数据（登录员工id），判断是否已经登录
        if (request.getSession().getAttribute("employee") != null) {
            //将登录的用户id储存到ThreadLocal中
            Long employeeId = (Long) request.getSession().getAttribute("employee");
            LocalContext.setCurrentId(employeeId);
            //已登录，放行
            filterChain.doFilter(request,response);
            return;
        }

        //移动端：判断用户登录状态
        if(request.getSession().getAttribute("user") != null){

            Long userId = (Long) request.getSession().getAttribute("user");
            LocalContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            log.info("用户已登录，用户id为：{}",userId);
            return;
        }

        //未登录，页面跳转
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(Res.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次的请求uri是否为不需要过滤处理的请求路径
     * @param urls 请求路径数组
     * @param requestURI 请求uri
     */
    public boolean check(String[] urls,String requestURI) {
        for (String url : urls) {
            if (ANT_PATH_MATCHER.match(url,requestURI)) return true;
        }
        return false;
    }
}
