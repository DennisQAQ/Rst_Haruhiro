package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.common.CustomException;
import com.Rst_harohiro.common.R;
import com.Rst_harohiro.dto.DishDto;
import com.Rst_harohiro.entities.Dish;
import com.Rst_harohiro.entities.DishFlavor;
import com.Rst_harohiro.mapper.DishMapper;
import com.Rst_harohiro.service.DishFlavorService;
import com.Rst_harohiro.service.DishService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    DishFlavorService dishFlavorService;

    @Transactional
    @Override
    public void saveWtihFlavor(DishDto dishDto) {
        //保存菜品信息到菜品表DTO
        this.save(dishDto);

        Long dishId= dishDto.getId();

        //菜品口味
        List<DishFlavor> flavorList = dishDto.getFlavors();

        flavorList= flavorList.stream().map((item)->{
            item.setDishId(dishId);
            return  item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavorList);
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);
        //更新dish_flavor表信息delete操作
        LambdaQueryWrapper<DishFlavor>queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //更新dish_flavor表信息insert操作
        //获取前端提交的菜品口味信息
        List<DishFlavor> flavors =dishDto.getFlavors();

        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    @Transactional
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();

        //对象拷贝，将获取的菜品基本信息拷贝到dishDto中
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper= new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavorList);

        return dishDto;

    }


    @Override
    public void deleteByids(List<Long> ids) {
        //构造条件查询器
        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper<>();
        //先查询该菜品是否在售卖，如果是则抛出业务异常
        queryWrapper.in(ids!=null,Dish::getId,ids);
        List<Dish> dishList = this.list(queryWrapper);
        for (Dish dish:dishList) {
            Integer status = dish.getStatus();
            //如果不是在售卖,则可以删除
            if(status==0){
                this.removeById(dish.getId());
            }else {
                //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
                throw  new CustomException("删除菜品中有正在售卖菜品,无法全部删除");
            }

        }


    }

    @Override
    public void updateStatus(String[] ids,Integer status) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Dish::getId,ids);

        List<Dish> dishList = this.list(queryWrapper);
        for (Dish dish: dishList) {
            if ((dish !=null)){
                dish.setStatus(status);
                this.updateById(dish);
            }
        }


    }
}
