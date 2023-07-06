package com.Rst_harohiro.controller;

import com.Rst_harohiro.common.R;
import com.Rst_harohiro.dto.DishDto;
import com.Rst_harohiro.entities.Category;
import com.Rst_harohiro.entities.Dish;
import com.Rst_harohiro.entities.DishFlavor;
import com.Rst_harohiro.service.CategoryService;
import com.Rst_harohiro.service.DishFlavorService;
import com.Rst_harohiro.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate redisTemplate;
    @Autowired
    private CategoryService categoryService;
    @PostMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> save(@RequestBody DishDto dto){

        log.info(dto.toString());

        dishService.saveWtihFlavor(dto);

        return R.success("新增菜品成功！");
    }

    /**
     * 在开发代码之前，需要梳理一下菜品分页查询时前端页面和服务端的交互过程:
     * 1、页面(backend/page/food/list.html)发送ajax请求，将分页查询参数(page、pageSize、name)提交到服务端，获取分页数据
     * 2、页面发送请求，请求服务端进行图片下载，用于页面图片展示
     * @return
     */
    @GetMapping("/page")

    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }


    /**
     * 在开发代码之前，需要梳理一下修改菜品时前端页面（add.html)和服务端的交互过程:1、页面发送ajax请求，请求服务端获取分类数据，用于菜品分类下拉框中数据展示
     * 2、页面发送ajax请求，请求服务端，根据id查询当前菜品信息，用于菜品信息回显3、页面发送请求，请求服务端进行图片下载，用于页图片回显
     * 4、点击保存按钮，页面发送ajax请求，将修改后的菜品相关数据以json形式提交到服务端
     * 开发修改菜品功能，其实就是在服务端编写代码去处理前端页面发送的这4次请求即可。
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }
    @PutMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> update(@RequestBody DishDto dto){
        log.info(dto.toString());
        dishService.updateWithFlavor(dto);
        String keys= "dish_"+dto.getCategoryId()+"_1";
        //清理redis缓存
//        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
        return R.success("菜品修改成功！");
    }

    /**
     * 对菜品进行批量或者单个的删除操作
     *  1.判断要删除的菜品在不在售卖的套餐中，如果在那不能删除
     * 2.要先判断要删除的菜品是否在售卖，如果在售卖也不能删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "dishCache",allEntries = true)
    public R<String> delete(@RequestParam("ids") List<Long> ids){
        log.info("接受删除菜品id",ids);
        //删除菜品，逻辑删除
        dishService.deleteByids(ids);
        return R.success("菜品删除成功");
    }

    /**
     * 对菜品进行批量或者单个的起售或停售操作
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable("status" )Integer status,@RequestParam String[] ids){
        LambdaQueryWrapper<Dish>queryWrapper =new LambdaQueryWrapper();
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> dishList = dishService.list(queryWrapper);
        for (Dish dish: dishList) {
            if ((dish !=null)){
                dish.setStatus(status);
                dishService.updateById(dish);
            }
        }
        return R.success("菜品售卖状态修改成功");
    }

    /**
     * 根据条件查询菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list (Dish dish){
//        //构造条件查询
//        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper();
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        //查询状态为1的菜品（启售状态）
//        queryWrapper.eq(Dish::getStatus,1);
//
//        //排序条件
//        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(queryWrapper);
//        return  R.success(list);
//    }

    @GetMapping("/list")
    @Cacheable(value = "dishCache",key = "#dish.categoryId+'_'+#dish.status")
    public R<List<DishDto>> list (Dish dish){
        List<DishDto> listDishDto=null;


        //构造条件查询
        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态为1的菜品（启售状态）
        queryWrapper.eq(Dish::getStatus,1);

        //排序条件
        queryWrapper.orderByDesc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        listDishDto = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            //菜品id
            Long itemId = item.getId();
            LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(DishFlavor::getDishId,itemId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(queryWrapper1);
            dishDto.setFlavors(dishFlavors);
            return dishDto;
        }).collect(Collectors.toList());
        return  R.success(listDishDto);
    }
}
