package com.zjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.RouteMapper;
import com.zjy.pojo.Route;
import com.zjy.service.RouteService;
import org.springframework.stereotype.Service;

@Service
public class RouteServiceImpl extends ServiceImpl<RouteMapper, Route>implements RouteService {
}
