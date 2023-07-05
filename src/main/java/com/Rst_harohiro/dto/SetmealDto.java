package com.Rst_harohiro.dto;


import com.Rst_harohiro.entities.Setmeal;
import com.Rst_harohiro.entities.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
