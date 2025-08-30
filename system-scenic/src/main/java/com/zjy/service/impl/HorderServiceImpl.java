package com.zjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.HorderMapper;
import com.zjy.pojo.Horder;
import com.zjy.service.HorderService;
import org.springframework.stereotype.Service;

@Service
public class HorderServiceImpl extends ServiceImpl<HorderMapper, Horder>implements HorderService {
}
