package com.Rst_harohiro.controller;

import com.Rst_harohiro.common.BaseContext;
import com.Rst_harohiro.common.R;
import com.Rst_harohiro.entities.AddressBook;
import com.Rst_harohiro.service.AddressBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/addressBook")

public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    //新增
    @PostMapping
    R<AddressBook> save (@RequestBody AddressBook addressBook){
        //为地址簿设置当前用户的id
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }
    //设置默认地址
    @PutMapping("default")
    R<AddressBook> setDefaultAddressBook (@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault,0);

        addressBookService.update(wrapper);
        return R.success(addressBook);
    }
    //根据id查询地址
    @GetMapping("/{id}")
    R<AddressBook> getAddressBookById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }
    //查询默认地址
    @GetMapping("default")
    R<AddressBook> getDefaultAddressBook(){
        LambdaQueryWrapper <AddressBook>queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault,1);

        AddressBook one = addressBookService.getOne(queryWrapper);

        if (one!=null){
            return R.success(one);
        }else return R.error("没有找到该对象");
    }
    //查询指定用户的全部地址
    @GetMapping("/list")
    R<List<AddressBook>> list(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null!=addressBook.getUserId(),AddressBook::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        List<AddressBook> addressBookList = addressBookService.list(queryWrapper);
        return R.success(addressBookList);
    }

    //删除

    @DeleteMapping
    R<String> delete(Long ids){
        if (ids!=null){
            addressBookService.removeById(ids);
            return R.success("删除成功");
        }else
            return R.error("删除失败");

    }

    //修改
    @PutMapping
    R<AddressBook> update(@RequestBody AddressBook addressBook) {

//        addressBook.setUserId(BaseContext.getCurrentId());
        log.info("修改地址簿{}",addressBook);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }
}
