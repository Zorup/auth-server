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


    // code정보에 해당하는 메시지를 조회합니다.
    private String getMessage(String code) {
        return getMessage(code, null);
    }
    // code정보, 추가 argument로 현재 locale에 맞는 메시지를 조회합니다.
    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

}

/*
    Controller의 호출 흐름 (controller -> service -> ...등등) 을 주시하다가 Exception 발생시 여기서 캐치해
    적절한 로직 처리 후 response 응답을 해버린다.

    @ControllerAdvice 내에있는 @ExceptionHandler는 모든 Controller의 Exception을 담당. (패캐지단위 지정도 가능)
    (@Controller 내에있는 @ExceptionHandler는 해당 메소드만 담당)
    (이 상황엔 이 방식이 AOP보다 개별설정하기 용이하게 느껴진다. @ControllerAdvice도 결국 AOP 기반이며 Spring MVC 에서 오류처리에 특화된듯 )

    ExceptionAdvice보다 각 코드의 try-catch가 더 우선됨.
 */
