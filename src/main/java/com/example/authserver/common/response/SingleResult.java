package com.example.authserver.common.response;


import lombok.Getter;
import lombok.Setter;

/*
    예외처리 및 결과 데이터 구조의 정형화를 위한 클래스
    단일결과를 감싸는 응답객체
 */

@Getter
@Setter
public class SingleResult<T> extends CommonResult {
    private T data;
}
