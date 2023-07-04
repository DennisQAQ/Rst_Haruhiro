package com.Rst_harohiro.controller;

import com.Rst_harohiro.common.R;
import com.Rst_harohiro.entities.Employee;
import com.Rst_harohiro.service.impl.EmpServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
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

    /**
     * 员工分页功能
     * 页面发送ajax请求，将分页查询参数(page.pageSize、name)提交到服务端
     * 服务端Controller接收页面提交的数据并调用Service查询数据
     * Service调用Mapper操作数据库，查询分页数据
     * Controller将查询到的分页数据响应给页面
     * 页面接收到分页数据并通过ElementUI的Table组件展示到页面上
     */
    @GetMapping("/page")
    R<Page> page(int page, int pageSize, String name) {
       log.info("page={},pageSize={},name={}",page,pageSize,name);
       //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        empService.page(pageInfo,queryWrapper);

        //执行查询条件
        return R.success(pageInfo);

    }

    /**
     * 修改员工账号状态
     * 1、页面发送ajax请求，将参数(id、 status)提交到服务端
     * <p>
     * 2、服务端Controller接收页面提交的数据并调用Service更新数据
     * <p>
     * 3、Service调用Mapper操作数据库
     */
    @PutMapping
    R<String> UpdateStatus (HttpServletRequest request, @RequestBody Employee employee) {
        log.info(employee.toString());
        //获取当前登录的员工ID
        Long empID = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empID);
        empService.updateById(employee);
        return R.success("员工状态修改成功！");
    }

    /**
     * 编辑员工信息
     * 1、点击编辑按钮时，页面跳转到add.html，并在url中携带参数[员工id]
     * <p>
     * 2、在add.html页面获取url中的参数[员工id]
     * <p>
     * 3、发送ajax请求，请求服务端，同时提交员工id参数
     * <p>
     * 4、服务端接收请求，根据员工id查询员工信息，将员工信息以json形式响应给页面
     * *****************************************************
     * 注意:add.html页面为公共页面，新增员工和编辑员工都是在此页面操作，
     * 所以该代码部分与之前添加员工代码对应，不需要重写。
     * *****************************************************
     */

    @GetMapping("/{id}")
    R<Employee> GetById(@PathVariable String id){
        log.info("根据id{}查询对象",id);
        Employee employee = empService.getById(id);
        if (employee!=null){
            return R.success(employee);
        }
        return R.error("没有查询到员工信息");
    }

}
