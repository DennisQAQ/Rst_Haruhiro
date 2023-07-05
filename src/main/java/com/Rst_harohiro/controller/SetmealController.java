package com.Rst_harohiro.controller;

import com.Rst_harohiro.common.R;
import com.Rst_harohiro.dto.SetmealDto;
import com.Rst_harohiro.entities.Category;
import com.Rst_harohiro.entities.Setmeal;
import com.Rst_harohiro.service.CategoryService;
import com.Rst_harohiro.service.SetmealDishService;
import com.Rst_harohiro.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealDishService SetmealDishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    R<Page<SetmealDto>> page (int page,int pageSize,String name){
        //构造分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);

        Page<SetmealDto> dtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper <Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        //根据name模糊查询
        queryWrapper.like(!StringUtils.isEmpty(name),Setmeal::getName,name);
        //根据创建时间进行条件排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //分页查询
        setmealService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> setmealList = pageInfo.getRecords();

        List<SetmealDto> dtoList = setmealList.stream().map((item)->{
            //根据id查询对象
            SetmealDto dto = new SetmealDto();
            BeanUtils.copyProperties(item,dto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category!=null){
                dto.setCategoryName(category.getName());
            }
            return dto;
        }).collect(Collectors.toList());

        //根据id查询对象
        dtoPage.setRecords(dtoList);

        return R.success(dtoPage);
    }
    //新增
    @PostMapping
    R<String> save (@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加成功");
    }
    //修改停售启售状态
    @PostMapping("/status/{status}")
    public R<String> sale(@PathVariable int status,String[] ids){
        setmealService.updateStatus(status,ids);

        return R.success("修改成功");
    }
    //删除
    @DeleteMapping
    R<String> delete(@RequestParam("ids") List<Long> ids){
        setmealService.deleteByids(ids);
        return R.success("删除成功");
    }

    @GetMapping("/{id}")
    R<SetmealDto> list(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.updateByIdWithDish(id);
        return R.success(setmealDto);
    }

    //修改套餐
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("修改成功");
    }
}
