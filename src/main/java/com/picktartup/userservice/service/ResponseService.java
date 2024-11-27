package com.picktartup.userservice.service;

import com.picktartup.userservice.common.CommonResult;
import com.picktartup.userservice.common.ListResult;
import com.picktartup.userservice.common.SingleResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ResponseService {

    @AllArgsConstructor
    @Getter
    private enum CommonResponse {
        SUCCESS(200, "성공하였습니다.");

        int code;
        String message;
    }
    public <T> SingleResult<T> getSingleResult(T data) {
        SingleResult<T> result = new SingleResult<T>();
        result.setData(data);
        this.setSuccessResult(result);
        return result;
    }

    // 단일건 결과 처리 메서드 - 비동기
    public <T> Mono<SingleResult<T>> getMonoSingleResult(Mono<T> data) {
        return data.map(result -> {
            SingleResult<T> singleResult = new SingleResult<>();
            singleResult.setData(result);
            singleResult.setSuccess(true);
            singleResult.setCode(0);
            singleResult.setMessage("성공하였습니다.");
            return singleResult;
        });
    }

    public <T> ListResult<T> getListResult(List<T> list) {
        ListResult<T> result = new ListResult<T>();
        result.setList(list);
        this.setSuccessResult(result);
        return result;
    }

    public CommonResult getSuccessfulResult() {
        CommonResult result = new CommonResult();
        this.setSuccessResult(result);
        return result;
    }

    public CommonResult getSuccessfulResultWithMessage(String message) {
        CommonResult result = new CommonResult();
        result.setMessage(message);
        result.setSuccess(true);
        result.setCode(HttpStatus.OK.value());
        return result;
    }

    public CommonResult getFailResult(int code, String message) {
        CommonResult result = new CommonResult();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public CommonResult getErrorResult(int code, String message) {
        CommonResult result = new CommonResult();
        result.setSuccess(false);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    private void setSuccessResult(CommonResult result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMessage(CommonResponse.SUCCESS.getMessage());
    }

}
