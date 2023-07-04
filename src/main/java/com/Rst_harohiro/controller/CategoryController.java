package com.Rst_harohiro.controller;

import com.Rst_harohiro.common.R;
import com.Rst_harohiro.entities.Category;
import com.Rst_harohiro.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * 后台系统中可以管理分类信息，分类包括两种类型，分别是菜品分类和套餐分类。
     * 当我们在后台系统中添加菜品时需要选择一个菜品分类，
     * 当我们在后台系统中添加一个套餐时需要选择一个套餐分类，
     * 在移动端也会按照菜品分类和套餐分类来展示对应的菜品和套餐。
     *
     * 1、页面(backend/page/category/list.html)发送ajax请求，将新增分类窗口输入的数据以json形式提交到服务端
     *
     * 2、服务端Controller接收页面提交的数据并调用Service将数据进行保存
     *
     * 3、Service调用Mapper操作数据库，保存数据
     *
     * 可以看到新增菜品分类和新增套餐分类请求的服务端地址和提交的json数据结构相同，所以服务端只需要提供一个方法统一处理即可
     */
    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    /**
     * 分类信息分页查询
     * 需求分析
     * 系统中的分类很多的时候，如果在一个页面中全部展示出来会显得比较乱，不便于查看，所以一般的系统中都会以分页的方式来展示列表数据。
     * 代码开发
     * 1、页面发送ajax请求，将分页查询参数(page.pageSize)提交到服务端
     * 2、服务端Controller接收页面提交的数据并调用Service查询数据
     * 3、Service调用Mapper操作数据库，查询分页数据
     * 4、Controller将查询到的分页数据响应给页面
     * 5、页面接收到分页数据并通过ElementUI的Table组件展示到页面上
     */
    @GetMapping("/page")
    R<Page> page(int page,int pageSize){
        //1.构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //2.构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper<>();
        //3.排序条件
        queryWrapper.orderByAsc(Category::getSort);
        //4.进行分页查询
        categoryService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 删除分类
     * 在分类管理列表页面，可以对某个分类进行删除操作。需要注意的是当分类关联了菜品或者套餐时，此分类不允许删除。
     * 1、页面发送ajax请求，将参数(id)提交到服务端
     * 2、服务端Controller接收页面提交的数据并调用Service删除数据
     * 3、Service调用Mapper操作数据库
     */

    @DeleteMapping
    R<String> remove(Long id){
        log.info("删除{}",id);
        categoryService.remove(id);
        return R.success("删除成功！");
    }

    /**
     * 修改分类
     * 在分类管理列表页面点击修改按钮，弹出修改窗口，
     * 在修改窗口回显分类信息并进行修改，最后点击确定按钮完成修改操作
     */

    @PutMapping
    R<String> update(@RequestBody Category category){
        log.info("修改商品分类{}",category.toString());
        categoryService.updateById(category);
        return R.success("修改分类成功！");

    }
}
