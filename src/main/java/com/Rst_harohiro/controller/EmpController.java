package com.Rst_harohiro.controller;

import com.Rst_harohiro.common.R;
import com.Rst_harohiro.entities.Employee;
import com.Rst_harohiro.service.EmpService;
import com.Rst_harohiro.service.impl.EmpServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmpController {

    @Autowired
    private EmpServiceImpl empService;

    /*
    后台登录功能
        1.将页面提交的密码进行md5加密处理
        2.根据提交的用户名查询数据库
        3. 如果查询不到，则返回登录失败结果
        4.密码对比，如果不一致则返回登录失败结果
        5.查看员工状态，如果已经禁用状态，则返回员工已禁用结果
        6.登录成功，将员工id存入session并返回登录成功结果
     */
    @PostMapping("/login")
    R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //根据页面提交的密码进行md5加密
        String passwd=employee.getPassword();
        passwd= DigestUtils.md5DigestAsHex(passwd.getBytes());
        //根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee>  queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp= empService.getOne(queryWrapper);

        //如果没响应则返回失败结果
        if(emp==null){
            return R.error("登陆失败，员工不存在！");
        }
        //比对密码，如果不一致则返回失败结果
        if (!emp.getPassword().equals(passwd)){
            return R.error("登陆失败密码错误，请重试！");
        }
        //查看员工状态，如果已禁用状态，则返回员工已禁用结果
        if (emp.getStatus()==0){
            return R.error("该账号已禁用！");
        }
        //登录成功，将用户id存入Session并返回成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    /**
     * 后台退出功能
     * 员工登录成功后，页面跳转到后台系统首页面(backend/index.html)，此时会显示当前登录用户的姓名
     * 如果员工需要退出系统，直接点击右侧的退出按钮即可退出系统，退出系统后页面应跳转回登录页面
     */
    @PostMapping("/logout")
    R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前员工登录的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    /**
     * 添加新员工功能
     * 页面发送ajax请求，将新增员工页面中输入的数据以json的形式提交到服务端
     * 服务端Controller接收页面提交的数据并调用Service将数据进行保存
     * Service调用Mapper操作数据库，保存数据
     */
    @PostMapping
    R<String> seve(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工{}",employee.toString());
        //设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        //获取当前创建人的id
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);
        empService.save(employee);
        return R.success("新增员工成功！");

    }
}
