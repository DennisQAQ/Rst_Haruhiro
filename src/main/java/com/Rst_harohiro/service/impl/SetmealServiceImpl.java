package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.common.CustomException;
import com.Rst_harohiro.dto.SetmealDto;
import com.Rst_harohiro.entities.Dish;
import com.Rst_harohiro.entities.Setmeal;
import com.Rst_harohiro.entities.SetmealDish;
import com.Rst_harohiro.mapper.SetmealMapper;
import com.Rst_harohiro.service.SetmealDishService;
import com.Rst_harohiro.service.SetmealService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl  extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
    @Autowired
    private SetmealDishService setmealDishService;



    //新增套餐
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();

        setmealDishList.stream().map((item)->{
           item.setSetmealId(setmealDto.getId());
           return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishList);
    }

    @Transactional
    @Override
    public void deleteByids(List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(ids!=null,Setmeal::getId,ids);
        List<Setmeal> setmealList = this.list(queryWrapper);
        for (Setmeal setmeal:setmealList) {
            Integer status = setmeal.getStatus();
            if (status==0){
                this.removeById(setmeal.getId());
            }else {
                throw new CustomException("无法删除！");
            }
        }

    }

//    @DeleteMapping
//    public R<String> delete(String[] ids){
//        int index=0;
//        for(String id:ids) {
//            Setmeal setmeal = setmealService.getById(id);
//            if(setmeal.getStatus()!=1){
//                setmealService.removeById(id);
//            }else {
//                index++;
//            }
//        }
//        if (index>0&&index==ids.length){
//            return R.error("选中的套餐均为启售状态，不能删除");
//        }else {
//            return R.success("删除成功");
//        }
//    }

    @Override
    public void updateStatus(int status, String[] ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.in(ids!=null,Setmeal::getId,ids);

        List<Setmeal> setmealList = this.list(queryWrapper);
        for (Setmeal setmeal: setmealList) {
            if ((setmeal !=null)){
                setmeal.setStatus(status);
                this.updateById(setmeal);
            }
        }}

    @Override
    public SetmealDto updateByIdWithDish(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);
        //查询套餐菜品基本信息
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmeal.getId());
        List<SetmealDish> setmealDishList = setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //跟新setmeal表信息
        this.updateById(setmealDto);

        //更新setmeal_dish表信息delete操作
        LambdaQueryWrapper<SetmealDish>queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        //更新setmeal_dish表信息insert操作

        List<SetmealDish> setmealDishList = setmealDto.getSetmealDishes();
        setmealDishList.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return  item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishList);
    }

}