package com.Rst_harohiro.service;

import com.Rst_harohiro.dto.SetmealDto;
import com.Rst_harohiro.entities.Setmeal;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {


    void saveWithDish(SetmealDto setmealDto);

    void deleteByids(List<Long> ids);

    void updateStatus(int status, String[] ids);
    SetmealDto updateByIdWithDish(Long id);
    void updateWithDish(SetmealDto setmealDto);
}
