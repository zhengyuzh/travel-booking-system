package com.zjy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjy.pojo.Hotel;
import com.zjy.pojo.Notice;
import com.zjy.pojo.Scenic;
import com.zjy.pojo.ScenicRoute;
import com.zjy.service.ScenicRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Description: 景点路线规划
 * @Version: 1.0
 */

@Controller
@RequestMapping("scenicroute")
public class ScenicRouteController {

    @Value("${location}")
    public String location;

    @Autowired
    private ScenicRouteService routeService;

    /**
     * @description:推荐路线列表
     **/
    @RequestMapping("listScenicRoute")
    public String listScenicRoute(Model model){
        List<ScenicRoute> list = routeService.list(null);
        model.addAttribute("scenicRouteList",list);
        return "admin-scenicRoute-list";
    }

    /**
     * @description:跳转路线添加页面
     **/
    @RequestMapping("preSaveScenicRoute")
    public String preSaveNotice(){
        return "admin-scenicRoute-save";
    }

    /**
     * @description:保存数据
     **/
    @RequestMapping("saveScenicRoute")
    public String saveNotice(ScenicRoute scenicRoute, MultipartFile file){
        if (!file.isEmpty()){
            transfile(scenicRoute,file);
        }
        scenicRoute.setUpdateTime(new Date());
        routeService.save(scenicRoute);
        return "redirect:/scenicroute/listScenicRoute";
    }

    private void transfile(ScenicRoute scenicRoute, MultipartFile file) {
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

        scenicRoute.setSimage(path);
    }

    /**
     * @description:删除推荐
     **/
    @RequestMapping("delScenicRoute/{id}")
    public String delScenicRoute(@PathVariable("id") Integer id){
        boolean b = routeService.removeById(id);
        return "redirect:/scenicroute/listScenicRoute";
    }




}
