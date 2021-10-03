package com.example.authserver.controller;


import com.example.authserver.common.response.CommonResult;
import com.example.authserver.common.response.ResponseService;
import com.example.authserver.common.response.SingleResult;
import com.example.authserver.dto.UserDto;
import com.example.authserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

@Slf4j
@RestController
@RequiredArgsConstructor    // 생성자주입
@ResponseBody
@RequestMapping(value = "/v1")
public class AuthController {
    private final AuthService authService;
    private final ResponseService responseService;
    private final Environment env;

    // 회원가입
    @PostMapping(value="/singin")
    public CommonResult singIn(@RequestBody UserDto param){
        log.info("call signin Api - " + param.getLoginId());
        authService.signIn(param);
        return responseService.getSuccessResult();
    }

    @PostMapping(value="/login")
    public SingleResult<UserDto.ForResponse> login(@RequestBody UserDto.ForLogin param, HttpServletRequest request, HttpServletResponse response){
        log.info("call login Api");

        String clientIp = request.getHeader("X-Forwarded-For");
        return responseService.getSingleResult(authService.login(param, clientIp, response));
    }

    @PostMapping(value="/logout/user/{userId}")
    public CommonResult logout(@PathVariable Long userId, HttpServletResponse response){
        // fcm서버로 push-token삭제요청
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uri = UriComponentsBuilder.fromHttpUrl(env.getProperty("noti-disable-api-url") + userId.toString());
        ResponseEntity<String> restResponse = restTemplate.exchange(uri.toUriString(), HttpMethod.DELETE, null, String.class);
        log.info("fcm서버로 push-token 삭제요청 완료, response: " + restResponse.getBody());

        log.info("call logout Api");
        authService.logout(userId, response);

        return responseService.getSuccessResult();
    }

    @GetMapping(value="/refresh")
    public CommonResult refresh(HttpServletRequest request, HttpServletResponse response){
        log.info("call refresh Api");

        Enumeration<String> headers = request.getHeaders("Authorization");
        String clientIp = request.getHeader("X-Forwarded-For");
        authService.reIssueAccessToken(headers, clientIp, response);

        return responseService.getSuccessResult();
    }

}
