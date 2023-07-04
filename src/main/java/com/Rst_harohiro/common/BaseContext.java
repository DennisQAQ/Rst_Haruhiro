package com.Rst_harohiro.common;

/**
 * 基于LocalThread封装的工具，用于获取当前登录员工的id
 */
public class BaseContext {
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return threadLocal.get();
    }

    //在LogincheckFilter的doFilter方法中调用BaseContext来设置当前登录用户的id
}
