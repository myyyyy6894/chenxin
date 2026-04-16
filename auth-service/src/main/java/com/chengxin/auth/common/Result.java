package com.chengxin.auth.common;

import lombok.Data;

/**
 * 通用返回结果类
 * 所有接口统一用这个类返回，前端好解析
 * T 是泛型，可以装任何类型数据（对象、列表、字符串等）
 */
@Data
public class Result<T> {
    // 响应码：200=成功，500=失败
    private Integer code;

    // 提示信息
    private String message;

    // 返回的数据（泛型，可以是任意类型）
    private T data;

    /**
     * 成功：带数据返回
     * 例：return Result.success(用户对象);
     */
    public static <T> Result<T> success(T data){
        Result<T> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        r.setData(data);
        return r;
    }

    /**
     * 成功：不带数据，只返回状态
     * 例：return Result.success();
     */
    public static Result<?> success(){
        Result<?> r = new Result<>();
        r.setCode(200);
        r.setMessage("success");
        return r;
    }

    /**
     * 失败：返回错误信息
     * 例：return Result.fail("用户名已存在");
     */
    public static Result<?> fail(String message){
        Result<?> r = new Result<>();
        r.setCode(500);
        r.setMessage(message);
        return r;
    }
}
