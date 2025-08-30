package com.zjy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjy.pojo.Route;
import com.zjy.service.RouteService;
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
@RequestMapping("route")
public class RouteController {
    @Autowired
    private RouteService routeService;

    @Value("${location}")
    public String location;
    @RequestMapping("listRoute/{id}")
    public String listRoute(@PathVariable Integer id, HttpSession session, Model model){
        session.setAttribute("sid",id);

        QueryWrapper<Route>qw=new QueryWrapper<>();
        qw.eq("sid",id);
        List<Route> list = routeService.list(qw);
        model.addAttribute("routeList",list);
        return "admin-scenic-route";

    }

    @RequestMapping("preSaveRoute")
    public String preSaveRoute(){
        return "admin-route-save";
    }

    @RequestMapping("saveRoute")
    public String saveRoute(Route route, MultipartFile file,HttpSession session){
        Integer sid = (Integer) session.getAttribute("sid");
        if (!file.isEmpty()){
            transfile(route,file);
        }
        route.setSid(sid);
        routeService.save(route);
        return "redirect:/route/listRoute/"+sid;
    }

    private void transfile(Route route, MultipartFile file) {
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

        route.setRimage(path);
    }

    @RequestMapping("preUpdateRoute/{id}")
    public String preUpdateRoute(@PathVariable Integer id,Model model){
        Route byId = routeService.getById(id);
        model.addAttribute("route",byId);
        return "admin-route-update";
    }

    @RequestMapping("updateRoute")
    public String updateRoute(Route route,MultipartFile file,HttpSession session){
        Integer sid = (Integer) session.getAttribute("sid");
        if (!file.isEmpty()){
            transfile(route,file);
        }

        boolean b = routeService.updateById(route);
        return "redirect:/route/listRoute/"+sid;
    }

    @RequestMapping("delRoute/{id}")
    public String delRoute(@PathVariable Integer id,HttpSession session){
        Integer sid = (Integer) session.getAttribute("sid");
        boolean b = routeService.removeById(id);
        return "redirect:/route/listRoute/"+sid;
    }
}
