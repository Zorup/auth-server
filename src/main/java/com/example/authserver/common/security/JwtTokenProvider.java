package com.example.authserver.common.security;

import com.example.authserver.domain.Role;
import com.example.authserver.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

/*
    accessToken -> 인증서버는 발급만하고 검증은 api-gateway 에서.
    refreshToken -> api-gateway 검증 후, 인증서버에서 추가검증 (in AuthService).

 */

@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private long accessTokenValidTime = UserDto.ForRedis.accessTokenValidTime * 1000L;    // milliSecond 단위
    private long refreshTokenValidTime = UserDto.ForRedis.refreshTokenValidTime * 1000L;    // milliSecond 단위

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());   // 라이브러리에 맞게 포맷 변환
    }

    public String createAccessToken(UserDto.ForRedis tokenInfo){
        Claims claims = Jwts.claims().setSubject(tokenInfo.getUserId().toString());   // 주인
        claims.put("roles", tokenInfo.getRole()); // 권한
        claims.put("loginId", tokenInfo.getLoginId());  // 로그인아이디
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(UserDto.ForRedis tokenInfo){
        Claims claims = Jwts.claims().setSubject(tokenInfo.getUserId().toString());   // 주인
        claims.put("roles", tokenInfo.getRole()); // 권한 ... IP정보 추가 고려하기
        claims.put("loginId", tokenInfo.getLoginId());  // 로그인아이디
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public UserDto.ForRedis parseToken(String token){
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        Claims body = claims.getBody();

        // String to ROLE enum
        String r = body.get("roles", String.class);
        Role role;
        if (r.equals(Role.ROLE_ADMIN.getValue())){
            role = Role.ROLE_ADMIN;
        } else if (r.equals(Role.ROLE_USER.getValue())){
            role = Role.ROLE_USER;
        } else {
            role = Role.INVALID;
        }

        UserDto.ForRedis tokenInfo = UserDto.ForRedis.builder()
                .userId(Long.valueOf(body.getSubject()))
                .loginId(body.get("loginId", String.class))
                .role(role)
                .refreshToken(token)
                .build();
        return tokenInfo;
    }


}
