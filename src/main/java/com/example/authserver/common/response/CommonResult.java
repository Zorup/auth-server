package com.example.authserver.common.response;

import lombok.Getter;
import lombok.Setter;

/*
    각종 Result에 공통부로 사용 or 결과데이터가 필요없는 응답일때 혼자 사용됨
 */

@Getter
@Setter
public class CommonResult {

    // 응답 성공 여부 (true / false)
    private boolean success;

    // 응답 코드 번호 (정상 >= 0 , 비정상 < 0)
    private int code;

    // 응답 메시지
    private String msg;
}
