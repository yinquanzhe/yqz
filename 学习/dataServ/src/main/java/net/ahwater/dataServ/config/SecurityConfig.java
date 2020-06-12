package net.ahwater.dataServ.config;

import net.ahwater.dataServ.filter.JwtAuthenticationTokenFilter;
import net.ahwater.dataServ.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by Reeye on 2019/8/12 10:00
 * Nothing is true but improving yourself.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean("passwordEncoder")
    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    protected UserDetailsService userDetailsService() {
        return userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 使用 JWT，关闭session
                .and()
                .formLogin().loginPage("/403").passwordParameter("username").passwordParameter("pwd")
//                .loginProcessingUrl("/user/login").successForwardUrl("/user/login").failureForwardUrl("/error")
                .and()
                .authorizeRequests()
//                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // 允许跨域
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/*").permitAll()
                .antMatchers("/403", "/user/login", "/error").permitAll()
                .regexMatchers("/swagger.*", "/webjars.*", "/v2/api-docs", "/file/img/.*").permitAll()  // swagger
                .regexMatchers("/test/.*").permitAll()  // test
                .regexMatchers("/wx/cp/.*").permitAll()  // 企业微信
//                .anyRequest().authenticated()
                .anyRequest().permitAll()
                .and()
                .headers().cacheControl();
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
