package com.zjy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjy.pojo.Room;
import com.zjy.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @Value("${location}")
    public String location;
    @RequestMapping("listRoom/{id}")
    public String listRoom(@PathVariable Integer id, Model model, HttpSession session){
        session.setAttribute("hid",id);
        QueryWrapper<Room>qw=new QueryWrapper<>();
        qw.eq("hid",id);
        List<Room> list = roomService.list(qw);
        model.addAttribute("roomList",list);
        return "admin-room-list";
    }

    @RequestMapping("preSaveRoom")
    public String preSaveRoom(){
        return "admin-room-save";
    }

    @RequestMapping("saveRoom")
    public String saveRoom(Room room, MultipartFile file,HttpSession session){
        Integer hid = (Integer) session.getAttribute("hid");
        if (!file.isEmpty()){
            transfile(room,file);
        }
        room.setHid(hid);
        boolean save = roomService.save(room);

        return "redirect:/room/listRoom/"+hid;
    }

    private void transfile(Room room, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        String prefix=System.nanoTime()+"";
        String path=prefix+suffix;
        File file1 = new File(location);
        if (!file1.exists()){
            file1.mkdirs();
        }
        File file2 = new File(file1, path);
        try {
            file.transferTo(file2);
        } catch (IOException e) {
            e.printStackTrace();
        }

        room.setRimage(path);
    }

    @RequestMapping("delRoom/{id}")
    public String delRoom(@PathVariable Integer id,HttpSession session){
        Integer hid = (Integer) session.getAttribute("hid");
        boolean b = roomService.removeById(id);
        return "redirect:/room/listRoom/"+hid;
    }
}
