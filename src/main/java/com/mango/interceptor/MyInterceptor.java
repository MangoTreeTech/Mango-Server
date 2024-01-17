package com.mango.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class MyInterceptor  implements HandlerInterceptor {
    //TODO 拦截器暂未完成

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 在请求处理之前执行，返回true表示继续执行，返回false中断执行
        System.out.println("Interceptor: Pre-handle");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // 请求处理之后执行，但在视图渲染之前
        System.out.println("Interceptor: Post-handle");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        // 在整个请求完成之后执行，通常用于资源清理工作
        System.out.println("Interceptor: After completion");
    }
}
