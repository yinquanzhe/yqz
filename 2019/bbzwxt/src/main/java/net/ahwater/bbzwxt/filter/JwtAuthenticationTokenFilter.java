package net.ahwater.bbzwxt.filter;

import net.ahwater.bbzwxt.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by yqz on 2019/7/8
 */
@SuppressWarnings("all")
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final String TOKEN_HEAD = "Authorization";
    private final String TOKEN_HEAD_PREFIX = "Bearer ";

    @Autowired
    private JwtUtils jwtUtils;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(TOKEN_HEAD);
        if(authHeader !=null && authHeader.startsWith(TOKEN_HEAD_PREFIX)){

        }

    }
}