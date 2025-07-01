package com.xiaowa.writingassistant.common;

import lombok.Data;

@Data
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 0;
        r.msg = "success";
        r.data = data;
        return r;
    }

    public static <T> Result<T> fail(String msg) {
        Result<T> r = new Result<>();
        r.code = 1;
        r.msg = msg;
        r.data = null;
        return r;
    }

    public static <T> Result<T> of(int code, String msg, T data) {
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        r.data = data;
        return r;
    }
}
