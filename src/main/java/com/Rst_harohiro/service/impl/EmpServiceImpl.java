package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.entities.Employee;
import com.Rst_harohiro.mapper.EmpMapper;
import com.Rst_harohiro.service.EmpService;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class EmpServiceImpl extends ServiceImpl<EmpMapper,Employee> implements EmpService{

}
