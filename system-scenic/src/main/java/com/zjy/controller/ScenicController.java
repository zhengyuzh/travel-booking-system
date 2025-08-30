package com.zjy.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjy.mapper.ScenicImageMapper;
import com.zjy.pojo.HotelImage;
import com.zjy.pojo.Scenic;
import com.zjy.pojo.ScenicImage;
import com.zjy.pojo.Type;
import com.zjy.service.ScenicService;
import com.zjy.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("scenic")
public class ScenicController {
    @Autowired
    private ScenicService scenicService;
    @Autowired
    private TypeService typeService;
    @Autowired
    private ScenicImageMapper scenicImageMapper;

    @Value("${location}")
    private String location;
    @RequestMapping("listScenic")
    public String listScenic(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                             @RequestParam(value = "pageSize",defaultValue = "6",required = false)Integer pageSize, Scenic scenic, Model model){
        if (pageNum<0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<0||pageSize.equals("")||pageSize==null){
            pageSize=6;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Scenic>qw=new QueryWrapper<>();
        if (scenic.getSname()!=null){
            qw.like("sname",scenic.getSname());
        }
        List<Scenic> list = scenicService.list(qw);
        PageInfo<Scenic>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        return "admin-scenic-list";
    }

    @RequestMapping("preSaveScenic")
    public String preSaveScenic(Model model){
        List<Type> list = typeService.list(null);
        model.addAttribute("typeList",list);
        return "admin-scenic-save";
    }

    @RequestMapping("saveScenic")
    public String saveScenic(Scenic scenic, @RequestParam("files") MultipartFile[] files){
        //生成一个UUID
        String idTwo = UUID.randomUUID().toString().replace("-", "");
        scenic.setIdTwo(idTwo);
        // 如果文件不为空，进行文件处理
        if (files != null && files.length > 0) {
            // 遍历每个上传的文件，处理并保存路径
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // 调用处理文件的方法，生成文件路径
                    transfile(scenic, file);
                }
            }
        }
        boolean save = scenicService.save(scenic);
        return "redirect:/scenic/listScenic";
    }

    private void transfile(Scenic scenic, MultipartFile file) {

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

        // 保存图片路径到数据库
        ScenicImage scenicImage = new ScenicImage();
        scenicImage.setScenicId(scenic.getIdTwo());
        scenicImage.setImagePath(path);
        scenicImageMapper.insert(scenicImage);

        scenic.setSimage(path);
    }

    @RequestMapping("preUpdateScenic/{id}")
    public String preUpdateScenic(@PathVariable Integer id,Model model){
        Scenic byId = scenicService.getById(id);
        model.addAttribute("scenic",byId);

        List<Type> list = typeService.list(null);
        model.addAttribute("typeList",list);
        return "admin-scenic-update";

    }

    @RequestMapping("updateScenic")
    public String updateScenic(Scenic scenic,@RequestParam("files") MultipartFile[] files){
        //生成一个UUID
        if(scenic.getIdTwo() == null){
            String idTwo = UUID.randomUUID().toString().replace("-", "");
            scenic.setIdTwo(idTwo);
        }
        // 如果文件不为空，进行文件处理
        if (files != null && files.length > 0) {
            // 遍历每个上传的文件，处理并保存路径
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    // 调用处理文件的方法，生成文件路径
                    transfile(scenic, file);
                }
            }
        }
        boolean b = scenicService.updateById(scenic);
        return "redirect:/scenic/listScenic";
    }

    @RequestMapping("delScenic/{id}")
    public String delScenic(@PathVariable Integer id){
        boolean b = scenicService.removeById(id);
        return "redirect:/scenic/listScenic";
    }

    @ResponseBody
    @RequestMapping("batchDeleteScenic")
    public String batchDeleteScenic(String idList){
        String[] split = StrUtil.split(idList, ",");
        List<Integer>list=new ArrayList<>();
        for (String s : split) {
            if (!s.isEmpty()){
                list.add(Integer.valueOf(s));
            }
        }

        boolean b = scenicService.removeByIds(list);
        if (b){
            return "OK";
        }else {
            return "error";
        }
    }
}
