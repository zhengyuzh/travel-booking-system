package com.zjy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zjy.pojo.CountNumber;
import com.zjy.pojo.SOrder;

import java.util.List;

public interface SorderService extends IService<SOrder> {
    List<SOrder> listSorder(Integer id);

    List<CountNumber> queryNum();

    List<SOrder> listWish(Integer userId);

    List<SOrder> listMySorder(Integer userId);
}
