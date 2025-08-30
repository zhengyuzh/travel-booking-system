package com.zjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.ScenicMapper;
import com.zjy.pojo.Scenic;
import com.zjy.service.ScenicService;
import org.springframework.stereotype.Service;

@Service
public class ScenicServiceImpl extends ServiceImpl<ScenicMapper, Scenic>implements ScenicService {
}
