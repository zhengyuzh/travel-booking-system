package com.zjy.controller;

import com.zjy.pojo.Notice;
import com.zjy.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @RequestMapping("listNotice")
    public String listNotice(Model model){
        List<Notice> list = noticeService.list(null);
        model.addAttribute("noticeList",list);
        return "admin-notice-list";
    }

    @RequestMapping("preSaveNotice")
    public String preSaveNotice(){
        return "admin-notice-save";
    }

    @RequestMapping("saveNotice")
    public String saveNotice(Notice notice){
        noticeService.save(notice);
        return "redirect:/notice/listNotice";
    }

    @RequestMapping("delNotice/{id}")
    public String delNotice(@PathVariable Integer id){
        boolean b = noticeService.removeById(id);
        return "redirect:/notice/listNotice";
    }
}
