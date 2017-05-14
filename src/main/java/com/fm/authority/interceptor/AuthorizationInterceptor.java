package com.fm.authority.interceptor;

import com.fm.util.CookieUtils;
import com.fm.authority.annotion.AuthAnnotation;
import com.fm.authority.token.Token;
import com.fm.authority.util.TokenManager;
import com.fm.exception.UserUnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by mac on 2016/11/28.
 * 注解@AuthAnnotation的处理器
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
   private TokenManager tokenManager;

    @Override
    /**
     * token成功，讲id放入request，在下个参数处理器注入当前用户对象。
     * token验证失败，且该方法带有@AuthAnnotation注解。直接返回401。没有带有注解说明不需要权限放行
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        String authorization = CookieUtils.getCookieValue(request, "token");
        Token token = tokenManager.getToken(authorization);
        if (tokenManager.checkToken(token)) {
            request.setAttribute("userId", token.getUid());
            return true;
        } else if (handlerMethod.getMethodAnnotation(AuthAnnotation.class) != null) {
            throw new UserUnauthorizedException();
        } else {
            return true;
        }
    }
}
