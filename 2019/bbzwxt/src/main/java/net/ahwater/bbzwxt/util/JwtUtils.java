package net.ahwater.bbzwxt.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.ahwater.bbzwxt.entity.po.User;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yqz on 2019/7/8
 */
@Component
public class JwtUtils {
    private final String SECRET = "Bbzwxt";
    private final String CREATED_TAG = "created";
    /**
     *  从数据声明生成令牌
     * @param claims 数据声明
     * @return 令牌
     */
    private String generateToken(Map<String, Object> claims){
        Date expirationDate = new Date(System.currentTimeMillis() + (long) 60 * 60 *1000);//1小时
        return Jwts.builder().setClaims(claims).setExpiration(expirationDate).signWith(SignatureAlgorithm.HS256,SECRET).compact();
    }

    /**
     * 从令牌中获取数据声明
     * @param token 令牌
     * @return 数据声明
     */
    private Claims getClaimsFromToken(String token){
        Claims claims;
        try{
            claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        }catch (Exception e){
            claims = null;
        }
        return claims;
    }

    /**
     * 生成令牌
     *
     * @param user 用户
     * @return 令牌
     */
    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>(2);
        claims.put(Claims.SUBJECT, user.getUsername());
        claims.put(CREATED_TAG, new Date());
        return generateToken(claims);
    }

    /**
     * 从令牌中获取当前租户ID
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getSubject();
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * 判断令牌是否过期
     *
     * @param token 令牌
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 刷新令牌
     *
     * @param token 原令牌
     * @return 新令牌
     */
    public String refreshToken(String token) {
        String refreshedToken;
        try {
            Claims claims = getClaimsFromToken(token);
            claims.put(CREATED_TAG, new Date());
            refreshedToken = generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    /**
     * 验证令牌
     *
     * @param token  令牌
     * @param user 用户
     * @return 是否有效
     */
    public Boolean validateToken(String token, User user) {
        String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}