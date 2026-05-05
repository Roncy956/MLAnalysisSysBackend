package com.learn.mlanalysissysbackend.utils;

import com.learn.mlanalysissysbackend.pojo.LoginInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWT 工具类（基于 LoginInfo）
 * - 生成 Token 时只放入 id 和 role
 * - 解析 Token 时返回包含 id, role 的 LoginInfo 对象
 * - 有效期可单独配置
 */
@Component
public class JwtUtils {

    // ==================== 配置区（可随时修改） ====================
    // Token 有效期（毫秒），默认 2 小时
    private static long EXPIRATION_MILLIS = 2 * 60 * 60 * 1000L;

    // 密钥（生产环境请从配置读取或使用 Base64 编码的字符串）
    private static String SECRET_STRING;
    private static SecretKey SECRET_KEY;

    // 供 Spring 调用的 setter（注意非静态）
    @Value("${jwt-sign-key.key}")
    public void setSecretString(String secretString) {
        JwtUtils.SECRET_STRING = secretString;
        JwtUtils.SECRET_KEY = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * 设置 Token 有效期（毫秒）
     */
    public static void setExpirationMillis(long expirationMillis) {
        EXPIRATION_MILLIS = expirationMillis;
    }

    /**
     * 获取当前有效期（毫秒）
     */
    public static long getExpirationMillis() {
        return EXPIRATION_MILLIS;
    }

    // ==================== 生成 Token ====================

    /**
     * 根据 LoginInfo 生成 JWT
     *
     * @param loginInfo 包含 id 和 role（name 字段不会被放入 Token）
     * @return JWT 字符串
     */
    public static String generateToken(LoginInfo loginInfo) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(EXPIRATION_MILLIS);

        return Jwts.builder()
                .subject(String.valueOf(loginInfo.getId()))   // 存放用户ID
                .issuedAt(Date.from(now))                     // 签发时间（生成的时间）
                .expiration(Date.from(expiration))
                .id(UUID.randomUUID().toString())
                .claim("role", loginInfo.getRole())           // 存放角色
                .signWith(SECRET_KEY)
                .compact();
    }

    // ==================== 解析 Token -> LoginInfo ====================

    /**
     * 解析 JWT 并返回 LoginInfo 对象（仅填充 id 和 role，不填充 name 和 token）
     *
     * @param token JWT 字符串
     * @return LoginInfo 对象
     * @throws RuntimeException 如果 Token 无效、过期或签名错误
     */
    public static LoginInfo parseTokenToLoginInfo(String token) {
        Claims claims = parseClaims(token);
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setId(Integer.valueOf(claims.getSubject()));
        loginInfo.setRole(claims.get("role", String.class));
        // name 不设置（保持 null）
        // token 不设置（保持 null）
        return loginInfo;
    }

    /**
     * 解析并验证 Token 返回 Claims（内部方法）
     */
    private static Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("JWT 已过期：" + e.getMessage(), e);
        } catch (SignatureException e) {
            throw new RuntimeException("JWT 签名无效：" + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("JWT 解析失败：" + e.getMessage(), e);
        }
    }

    // ==================== 可选：额外辅助方法 ====================

    /**
     * 仅验证 Token 是否有效（不返回对象，仅判断）
     */
    public static boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}