package com.example.authserver.common.response;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    // 코드-메시지 결합용 enum
    public enum CommonResponse {
        SUCCESS(0, "success"),
        FAIL(-1, "failure");

        private int code;
        private String msg;

        CommonResponse(int code, String msg){
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public <T> SingleResult<T> getSingleResult(T data) {
        SingleResult<T> result = new SingleResult<>();
        result.setData(data);
        this.setSuccessResult(result);
        return result;
    }

    public <T> ListResult<T> getListResult(List<T> list){
        ListResult<T> result = new ListResult<>();
        result.setList(list);
        this.setSuccessResult(result);
        return result;
    }

    public CommonResult getSuccessResult(){ // CommonResult 혼자 사용될때
        CommonResult result = new CommonResult();
        this.setSuccessResult(result);
        return result;
    }

    private void setSuccessResult(CommonResult result) {    // 공통부에 success정보 세팅
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    public CommonResult getFailResult(int code, String msg){    // ExceptionAdvice 에서 사용
        CommonResult result = new CommonResult();
        result.setSuccess(false);
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }

}
