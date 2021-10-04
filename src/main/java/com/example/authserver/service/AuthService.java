package com.example.authserver.service;

import com.example.authserver.common.exception.*;
import com.example.authserver.common.security.JwtTokenProvider;
import com.example.authserver.domain.Role;
import com.example.authserver.domain.User;
import com.example.authserver.dto.UserDto;
import com.example.authserver.repository.RedisRepo;
import com.example.authserver.repository.UserJpaRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.CookieGenerator;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserJpaRepo userJpaRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRepo redisRepo;

    private enum CookieName {
        ACCESS("X-Auth-Token");

        private String name;

        CookieName(String name){
            this.name = name;
        }

        public String getName(){ return this.name; };
    }

    @Transactional
    public void signIn(UserDto param){
        log.info("signIn Service..");

        // 이미 아이디 존재하면 예외발생
        if (userJpaRepo.findByLoginId(param.getLoginId()).isPresent())
            throw new AlreadyExistIdException();

        if (param.getPosition() != null)
            param.setPosition("없음");
        if (param.getDepartment() != null)
            param.setPosition("없음");

        param.setRole(Role.ROLE_USER);  // TODO 현재 모든 유저가 ROLE_USER 권한. admin관련기능 생성시 권한에따라 회원가입 로직 분리 필요
        param.setPassword(passwordEncoder.encode(param.getPassword())); // 패스워드 암호화
        
        User user = User.of(param);
        userJpaRepo.save(user);
    }

    @Transactional  // redisRepo에서 예외발생시에도 rollback됨
    public UserDto.ForResponse login(UserDto.ForLogin param, String clientIp, HttpServletResponse response){

        // 없는 아이디면 예외발생
        User user = userJpaRepo.findByLoginId(param.getLoginId())
                .orElseThrow(HUserNotFoundException::new);

        // 비밀번호 일치 검사
        if (!passwordEncoder.matches(param.getPassword(), user.getPassword()))
            throw new HUserNotFoundException();
        log.info("Password matching Success");

        // 토큰 발급
        UserDto.ForRedis tokenInfo = UserDto.ForRedis.of(user);
        String accessToken = jwtTokenProvider.createAccessToken(tokenInfo);
        String refreshToken = jwtTokenProvider.createRefreshToken(tokenInfo);

        // Redis에 insert
        tokenInfo.setRefreshToken(refreshToken);
        tokenInfo.setClientIp(clientIp);
        redisRepo.insertRefreshToken(tokenInfo);

        // RefreshToken은 body에 함께 내려줌
        UserDto.ForResponse result = UserDto.ForResponse.of(user);  // response body에 포함될내용
        result.setRefreshToken(refreshToken);

        // 쿠키에 access토큰 세팅
        tokenCookieOn(response, CookieName.ACCESS, accessToken);

        log.info("Login Success");
        return result;
    }

    @Transactional
    public void logout(Long userId, HttpServletResponse response){
        // Redis에서 Refresh 토큰 삭제
        redisRepo.deleteRefreshToken(userId);
        // 쿠키에서 access토큰 삭제
        tokenCookieOff(response, CookieName.ACCESS);
    }

    public boolean reIssueAccessToken(Enumeration<String> headers, String clientIp, HttpServletResponse response){

        // 헤더에서 토큰 추출
        String tokenIn = extractToken(headers).orElseThrow(NoRefreshTokenException::new);

        // redis에 저장해둔 refreshToken 꺼내기
        UserDto.ForRedis parsed = jwtTokenProvider.parseToken(tokenIn);
        UserDto.ForRedis valueInRedis = redisRepo.findRefreshToken(parsed.getUserId())
                .orElseThrow(InvalidRefreshTokenException::new);

        // redis 속 토큰과 같은지 비교
        String tokenInRedis = valueInRedis.getRefreshToken();
        if (!parsed.getRefreshToken().equals(tokenInRedis))
            throw new InvalidRefreshTokenException();

        // refresh토큰 발급받았던 ip와 같은지 비교
        String ipInRedis = valueInRedis.getClientIp();
        if (!clientIp.equals(ipInRedis))
            throw new DifferentClientException();

        // access토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(valueInRedis);

        // 쿠키에 토큰 세팅
        tokenCookieOn(response, CookieName.ACCESS, accessToken);

        return true;
    }

    // @Transactional 내에 있어도 이 작업은 rollback되지 않으므로 메소드 가장 마지막에 호출되어야 안전함.
    private void tokenCookieOn(HttpServletResponse response, CookieName cookieName, String token){
        CookieGenerator cg = new CookieGenerator();
        cg.setCookieMaxAge(-1); // 세션쿠키. 토큰 수명은 토큰에 표기, 쿠키는 따로 만료시키지 않음
        cg.setCookieHttpOnly(true); // 브라우저에서 컨트롤 불가
        cg.setCookieName(cookieName.getName());
        cg.addCookie(response, token);
        log.info(cookieName.getName() + " 쿠키 세팅 완료");
    }

    private void tokenCookieOff(HttpServletResponse response, CookieName...cookieNames){
        CookieGenerator cg = new CookieGenerator();
        cg.setCookieHttpOnly(true);
        cg.setCookieMaxAge(0);

        for(CookieName cookieName: cookieNames){
            cg.setCookieName(cookieName.getName());
            cg.addCookie(response, null);
            log.info(cookieName.getName() + " 쿠키 삭제 완료");
        }
    }

    private Optional<String> extractToken(Enumeration<String> headers){
        String type = "refresh";
        while(headers.hasMoreElements()){
            String header = headers.nextElement();
            if (header.toLowerCase().startsWith(type.toLowerCase()))
                return Optional.of(header.substring(type.length()).trim());
        }

        return Optional.empty();
    }


}
