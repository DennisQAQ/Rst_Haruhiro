package com.Rst_harohiro.controller;

import com.Rst_harohiro.common.R;
import com.Rst_harohiro.dto.DishDto;
import com.Rst_harohiro.entities.Category;
import com.Rst_harohiro.entities.Dish;
import com.Rst_harohiro.service.CategoryService;
import com.Rst_harohiro.service.DishFlavorService;
import com.Rst_harohiro.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    R<String> save(@RequestBody DishDto dishDto){
      log.info(dishDto.toString());
      dishService.saveWtihFlavor(dishDto);
      return R.success("添加成功");
    }
    /**
     * 菜品分页展示
     * 1、页面(backend/page/food/list.html)发送ajax请求，将分页查询参数(page、pageSize、name)提交到服务端，获取分页数据
     * 2、页面发送请求，请求服务端进行图片下载，用于页面图片展示
     * 开发菜品信息分页查询功能，其实就是在服务端编写代码去处理前端页面发送的这2次请求即可。
     */

    @GetMapping("/page")
    R<Page> pageR(int page,int pageSize,String name){

        Page<DishDto> dishDtoPage = new Page<>(page,pageSize);
        Page<Dish> dishPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(!StringUtils.isEmpty(name),Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,queryWrapper);
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<Dish> records = dishPage.getRecords();
        List<DishDto> list =records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long CategoryId = item.getCategoryId();
            Category category = categoryService.getById(CategoryId);
            if(category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 在菜品管理列表页面点击修改按钮，跳转到修改菜品页面，
     * 在修改页面回显菜品相关信息并进行修改，最后点击确定按钮完成修改操作
     * 1、页面发送ajax请求，请求服务端获取分类数据，用于菜品分类下拉框中数据展示
     * 2、页面发送ajax请求，请求服务端，根据id查询当前菜品信息，用于菜品信息回显
     */
    //根据Id查询菜品信息与对应的口味信息
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    /**
     * 3、页面发送请求，请求服务端进行图片下载，用于页图片回显
     * 4、点击保存按钮，页面发送ajax请求，将修改后的菜品相关数据以json形式提交到服务端
     */
    //修改菜品
    @PutMapping
    R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);
        return R.success("修改成功!");
    }

    //停售起售菜品
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status" )Integer status,@RequestParam String[] ids){
        dishService.updateStatus(ids,status);
        return R.success("修改菜品信息成功！");
    }
    //删除成功
    @DeleteMapping
    public R<String> delete(@RequestParam("ids") List<Long> ids){
//        log.info("接受删除菜品id",ids);
        //删除菜品，逻辑删除
        dishService.deleteByids(ids);
        return R.success("菜品删除成功");
    }

    //求服务端获取套餐分类数据并展示到下拉框中
    @GetMapping("/list")
    R<List<Dish>> list(Dish dish){
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);
        return R.success(dishList);
    }
}