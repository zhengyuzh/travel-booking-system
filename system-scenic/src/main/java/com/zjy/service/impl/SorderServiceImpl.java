package com.zjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.SorderMapper;
import com.zjy.pojo.CountNumber;
import com.zjy.pojo.SOrder;
import com.zjy.service.SorderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SorderServiceImpl extends ServiceImpl<SorderMapper, SOrder> implements SorderService {
    @Autowired
    private SorderMapper sorderMapper;
    @Override
    public List<SOrder> listSorder(Integer id) {
        return sorderMapper.listSorder(id);
    }

    @Override
    public List<CountNumber> queryNum() {
        return sorderMapper.queryNum();
    }

    @Override
    public List<SOrder> listWish(Integer userId) {
        return sorderMapper.listWish(userId);
    }

    @Override
    public List<SOrder> listMySorder(Integer userId) {
        return sorderMapper.listMySorder(userId);
    }
}
