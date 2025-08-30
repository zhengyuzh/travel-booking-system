package com.zjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.TypeMapper;
import com.zjy.pojo.Type;
import com.zjy.service.TypeService;
import org.springframework.stereotype.Service;

@Service
public class TypeServiceImpl extends ServiceImpl<TypeMapper, Type>implements TypeService {
}
