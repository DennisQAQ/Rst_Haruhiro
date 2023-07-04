package com.Rst_harohiro.service;

import com.Rst_harohiro.entities.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);

}
