package com.kl.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.kl.common.constants.CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.Date;

/**
 * token 工具类
 */
@Slf4j
public class KlTokenUtil {

    public static String SECRET = "6SqOQ5TCpYnoYYIg3V4DAyyzj8fAuoqXCE6vdx9bHTb6JS4VuNJ5x0EFltmC0IPk";
    //定义JWT的发布者，这里可以起项目的拥有者
    private static final String TOKEN_ISSUSER = "ewp";

    private static Algorithm algorithm = Algorithm.HMAC256(SECRET);
    //定义JWT的有效时长
    // 有效时间(分钟) 超级三天不允许刷新token
    public static final int TOKEN_VAILDITY_TIME = 60 * 24 * 3;
    // 允许刷新token的时间(分钟) 生成token后两天后允许刷新，不能超过三天
    private static final int ALLOW_EXPIRES_TIME = 60 * 70;

    /**
     * 初始化jwt配置
     *
     * @param secret
     */
    public static void initJwtConfig(String secret) {
        log.info("[jwtTokenInit] secret:{}", secret);
        if (StringUtils.isNotEmpty(secret)) {
            KlTokenUtil.SECRET = secret;
        }
        try {
            algorithm = Algorithm.HMAC256(SECRET);
        } catch (IllegalArgumentException e) {
            log.error("[JwtToken] HMAC256 error,SECRET:{}", SECRET, e);
        }
    }

    /**
     * 生成token
     *
     * @param uid          用户id
     * @param partnerCode  合作方标识
     * @param createTime   创建时间
     * @param validityTime 有效时长（分钟）
     * @return
     */
    public static String create(String uid, int partnerCode, Date createTime, int validityTime) {
        String token = "";
        Date exp = DateHelper.addMinutes(createTime, validityTime);
        token = JWT.create()
                .withIssuer(TOKEN_ISSUSER)
                .withSubject(uid)
                .withIssuedAt(createTime)
                .withExpiresAt(exp)
                .withClaim(CommonConstants.PARTNER_CODE, partnerCode)
                .sign(algorithm);
        log.info("[JwtToken]create uid [{}]; token [{}] ;validityTime:[{}]", uid, token, validityTime);
        return token;
    }

    /**
     * 字符串token 解析为 jwtToken
     *
     * @param token 要解析的Token
     * @return DecodedJWT
     */
    public static DecodedJWT decode(String token) {
        DecodedJWT jwtToken = null;
        try {
            jwtToken = JWT.decode(token);
        } catch (Exception e) {
            log.warn("[JwtToken]解析JWT异常", e);
        }
        return jwtToken;
    }

    /**
     * 解析有效的用户ID
     *
     * @param token token
     * @return 用户id
     */
    public static Long decodeCustomerId(String token) {
        // 校验签名
        DecodedJWT decodedJwt = null;
        try {
            decodedJwt = decode(token);
            Date exp = decodedJwt.getExpiresAt();
            // 签名有效
            if (exp.getTime() < System.currentTimeMillis()) {
                log.warn("[JwtToken] 校验JWT token签名异常 签名已过期{}", token);
                return null;
            }
            return Long.parseLong(decodedJwt.getSubject());
        } catch (Exception e) {
            log.warn("[JwtToken] 解析JWT token数据异常 token:{}", token, e);
            return null;
        }
    }

    /**
     * 验证token
     *
     * @param token token
     */
    public static void verify(String token) {
        //校验Token
        JWTVerifier verifier = JWT.require(algorithm).withIssuer(TOKEN_ISSUSER).build();
        verifier.verify(token);
    }

    /**
     * 刷新token
     *
     * @param token token
     * @return 刷新后的token
     */
    public static String getRefreshToken(String token) {
        return getRefreshToken(token, TOKEN_VAILDITY_TIME, ALLOW_EXPIRES_TIME);
    }

    /**
     * 根据要过期的token获取新token
     * 此方法暂时不用，需前端配合着一起做token交换
     *
     * @param oldToken          上次的JWT经过解析后的token
     * @param tokenVaildityTime 有效时间
     * @param allowExpiresTime  允许过期的时间
     * @return
     */
    public static String getRefreshToken(String oldToken, int tokenVaildityTime, int allowExpiresTime) {
        try {
            KlTokenUtil.verify(oldToken);
        } catch (TokenExpiredException expiredException) {
            log.warn("[JwtToken] token过期{}", oldToken);
        } catch (Exception e) {
            log.warn("[JwtToken] 校验JWT token签名异常 非法签名{}", oldToken, e);
            return null;
        }
        DecodedJWT jwtToken = decode(oldToken);
        Instant now = Instant.now();
        Instant exp = jwtToken.getExpiresAt().toInstant();
        Instant create = jwtToken.getIssuedAt().toInstant();
        // 失效token不能刷新
        if (now.getEpochSecond() > exp.getEpochSecond()) {
            return null;
        }
        // 未到允许刷新的时候
        if (create.getEpochSecond() + allowExpiresTime * 60 > now.getEpochSecond()) {
            return null;
        }
        // 保证多次调用得到同一个token
        //在原有的JWT的过期时间的基础上，加上这次的有效时间，得到新的JWT的过期时间
        Instant newExp = exp.plusSeconds(tokenVaildityTime * 60);
        // 在原有的创建时间加上允许刷新token的时间为本次创建jwt的时间
        Instant iatTime = create.plusSeconds(allowExpiresTime * 60);
        //创建JWT
        String token = JWT.create()
                .withIssuer(TOKEN_ISSUSER)
                .withSubject(jwtToken.getSubject())
                .withIssuedAt(Date.from(iatTime))
                .withExpiresAt(Date.from(newExp))
                .withClaim(CommonConstants.PARTNER_CODE, jwtToken.getClaim(CommonConstants.PARTNER_CODE).asString())
                .sign(algorithm);
        log.info("[JwtToken] create refresh token [" + token + "]; iat: " + Date.from(iatTime) + " exp: " + Date.from(newExp));
        return token;
    }
}