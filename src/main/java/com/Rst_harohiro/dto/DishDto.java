package com.Rst_harohiro.dto;


import com.Rst_harohiro.entities.Dish;
import com.Rst_harohiro.entities.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
