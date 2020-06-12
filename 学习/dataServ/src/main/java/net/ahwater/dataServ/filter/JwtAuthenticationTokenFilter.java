package net.ahwater.dataServ.filter;

import net.ahwater.dataServ.entity.bo.UserBO;
import net.ahwater.dataServ.service.UserService;
import net.ahwater.dataServ.util.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Reeye on 2019/8/12 10:00
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("all")
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final String TOKEN_HEAD = "Authorization";
    private final String TOKEN_HEAD_PREFIX = "Bearer ";

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String authHeader = request.getHeader(TOKEN_HEAD);
        if (authHeader != null && authHeader.startsWith(TOKEN_HEAD_PREFIX)) {
            String authToken = authHeader.substring(TOKEN_HEAD_PREFIX.length());
            String username = jwtUtils.getUsernameFromToken(authToken);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserBO user = (UserBO) userService.loadUserByUsername(username);
                if (jwtUtils.validateToken(authToken, user)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        chain.doFilter(request, response);
    }

}
