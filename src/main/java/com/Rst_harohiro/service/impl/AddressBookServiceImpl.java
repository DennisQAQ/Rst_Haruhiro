package com.Rst_harohiro.service.impl;

import com.Rst_harohiro.entities.AddressBook;
import com.Rst_harohiro.mapper.AddressBookMapper;
import com.Rst_harohiro.service.AddressBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
