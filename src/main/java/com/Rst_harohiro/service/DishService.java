package com.Rst_harohiro.service;

import com.Rst_harohiro.dto.DishDto;
import com.Rst_harohiro.entities.Dish;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface DishService extends IService<Dish> {


    void saveWtihFlavor(DishDto dishDto);

    void updateWithFlavor(DishDto dishDto);

    DishDto getByIdWithFlavor(Long id);

    void deleteByids(List<Long> ids);

    void updateStatus(String[] ids,Integer status);
}
