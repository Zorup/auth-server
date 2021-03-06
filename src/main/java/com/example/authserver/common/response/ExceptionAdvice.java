package com.example.authserver.common.response;

import javax.servlet.http.HttpServletRequest;

import com.example.authserver.common.exception.*;
import io.jsonwebtoken.ExpiredJwtException;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvice {

    private final ResponseService responseService;
    private final MessageSource messageSource;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult defaultException(HttpServletRequest request, Exception e) {
        log.info("defaultException:"+e.toString());
        return responseService.getFailResult(Integer.parseInt(getMessage("unKnown.code")), getMessage("unKnown.msg"));
    }

    @ExceptionHandler({RedisException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult redisException(HttpServletRequest request, RedisException e) {
        log.info("redisException:"+e.toString());
        return responseService.getFailResult(Integer.parseInt(getMessage("redisInternal.code")), getMessage("redisInternal.msg"));
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    protected CommonResult validationException(HttpServletRequest request, Exception e) {
        log.info("bindException:"+e.toString());
        return responseService.getFailResult(Integer.parseInt(getMessage("unKnown.code")), getMessage("unKnown.code.msg"));
    }

    @ExceptionHandler(AlreadyExistIdException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    protected CommonResult alreadyExitIdException(HttpServletRequest request, AlreadyExistIdException e) {
        log.info("alreadyExistIdException:"+e.toString());
        return responseService.getFailResult(Integer.parseInt(getMessage("alreadyExistId.code")), getMessage("alreadyExitId.msg"));
    }

    @ExceptionHandler(HUserNotFoundException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResult userNotFoundException(HttpServletRequest request, HUserNotFoundException e) {
        log.info("userNotFoundException:"+e.toString());
        return responseService.getFailResult(Integer.parseInt(getMessage("userNotFound.code")), getMessage("userNotFound.msg"));
    }

    @ExceptionHandler(NoRefreshTokenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    protected CommonResult noRefreshTokenException(HttpServletRequest request, NoRefreshTokenException e){
        log.info("noRefreshTokenException:"+e.toString());
        return responseService.getFailResult(Integer.parseInt(getMessage("noRefreshToken.code")), getMessage("noRefreshToken.msg"));
    }

    @ExceptionHandler({InvalidRefreshTokenException.class, ExpiredJwtException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResult invalidRefreshTokenException(HttpServletRequest request, Exception e) {
        log.info("invalidRefreshTokenException:"+e.toString());
        return responseService.getFailResult(Integer.parseInt(getMessage("invalidRefreshToken.code")), getMessage("invalidRefreshToken.msg"));
    }

    @ExceptionHandler(DifferentClientException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    protected CommonResult differentClientException(HttpServletRequest request, Exception e) {
        log.info("differentClientException:"+e.toString());
        return responseService.getFailResult(Integer.parseInt(getMessage("differentClient.code")), getMessage("differentClient.msg"));
    }

    // code????????? ???????????? ???????????? ???????????????.
    private String getMessage(String code) {
        return getMessage(code, null);
    }
    // code??????, ?????? argument??? ?????? locale??? ?????? ???????????? ???????????????.
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

}

/*
    Controller??? ?????? ?????? (controller -> service -> ...??????) ??? ??????????????? Exception ????????? ????????? ?????????
    ????????? ?????? ?????? ??? response ????????? ????????????.

    @ControllerAdvice ???????????? @ExceptionHandler??? ?????? Controller??? Exception??? ??????. (??????????????? ????????? ??????)
    (@Controller ???????????? @ExceptionHandler??? ?????? ???????????? ??????)
    (??? ????????? ??? ????????? AOP?????? ?????????????????? ???????????? ????????????. @ControllerAdvice??? ?????? AOP ???????????? Spring MVC ?????? ??????????????? ???????????? )

    ExceptionAdvice?????? ??? ????????? try-catch??? ??? ?????????.
 */
