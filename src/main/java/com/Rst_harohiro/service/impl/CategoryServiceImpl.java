package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.common.CustomException;
import com.Rst_harohiro.entities.Category;
import com.Rst_harohiro.entities.Dish;
import com.Rst_harohiro.entities.Setmeal;
import com.Rst_harohiro.mapper.CategoryMapper;
import com.Rst_harohiro.service.CategoryService;
import com.Rst_harohiro.service.DishService;
import com.Rst_harohiro.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> queryWrapperForDish= new LambdaQueryWrapper();
        //添加查询条件，根据分类id进行查询
        queryWrapperForDish.eq(Dish::getCategoryId,id);
        int countForDish = dishService.count(queryWrapperForDish);
        //查询当前分类是否关联菜品,如果已经关联，抛出业务异常
        if (countForDish>1){
            //已经关联菜品，抛出异常
            throw new CustomException("分类关联了菜品，不能删除！");
        }
        //查询当前分类是否关联了套餐，如果已经关联，抛出业务异常
        LambdaQueryWrapper<Setmeal> queryWrapperForSetmeal = new LambdaQueryWrapper<>();

        //添加查询条件，根据分类id进行查询
        queryWrapperForSetmeal.eq(Setmeal::getCategoryId,id);
        int countForSetmeal = setmealService.count(queryWrapperForSetmeal);

        if(countForSetmeal>0){
            throw new CustomException("已经关联的套餐，不能删除！");

        }
        //正常删除分类
        super.removeById(id);
    }
}
