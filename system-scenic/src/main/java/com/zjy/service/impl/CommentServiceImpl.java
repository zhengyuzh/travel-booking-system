package com.zjy.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.mapper.CommentMapper;
import com.zjy.pojo.Comment;
import com.zjy.service.CommentService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>implements CommentService {
}
