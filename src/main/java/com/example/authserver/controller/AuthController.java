package com.example.authserver.controller;


import com.example.authserver.common.response.CommonResult;
import com.example.authserver.common.response.ResponseService;
import com.example.authserver.common.response.SingleResult;
import com.example.authserver.dto.UserDto;
import com.example.authserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    // 회원가입
    @PostMapping(value="/singin")
    public CommonResult singIn(@RequestBody UserDto param){
        log.info("call signin Api - " + param.getLoginId());
        return responseService.getSuccessResult();
    }

    @PostMapping(value="/login")
    public SingleResult<UserDto.ForResponse> login(@RequestBody UserDto.ForLogin param, HttpServletResponse response){
        log.info("call login Api");
        return responseService.getSingleResult(authService.login(param, response));
    }

    @PostMapping(value="/logout/user/{userId}")
    public CommonResult logout(@PathVariable Long userId, HttpServletResponse response){
        log.info("call logout Api");
        authService.logout(userId, response);
        // 여기서 fcm서버로 token삭제요청 or 직접 토큰 삭제

        return responseService.getSuccessResult();
    }

    @GetMapping(value="/refresh")
    public CommonResult refresh(HttpServletRequest request, HttpServletResponse response){
        log.info("call refresh Api");

        Enumeration<String> headers = request.getHeaders("Authorization");
        boolean result = authService.reIssueAccessToken(headers, response);

        return responseService.getSuccessResult();
    }
}
