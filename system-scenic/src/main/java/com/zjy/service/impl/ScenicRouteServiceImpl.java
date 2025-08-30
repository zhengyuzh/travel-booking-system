package com.zjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.ScenicRouteMapper;
import com.zjy.pojo.ScenicRoute;
import com.zjy.service.ScenicRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: TODO
 * @Version: 1.0
 */
@Service
@Transactional
public class ScenicRouteServiceImpl extends ServiceImpl<ScenicRouteMapper, ScenicRoute> implements ScenicRouteService {

    @Autowired
    private ScenicRouteMapper routeMapper;
}
