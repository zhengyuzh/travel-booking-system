package com.zjy.controller;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjy.mapper.HotelImagesMapper;
import com.zjy.mapper.ScenicImageMapper;
import com.zjy.mapper.ScenicMapper;
import com.zjy.pojo.*;
import com.zjy.service.*;
import com.zjy.service.recommend.MyUserBasedRecommenderImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Slf4j
@Controller
public class UserController {
    @Value("${location}")
    private String location;
    @Autowired
    private CommentService commentService;
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private ScoreScenicService scoreScenicService;
    @Autowired
    private ScenicService scenicService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private HotelService  hotelService;
    @Autowired
    private SorderService sorderService;
    @Autowired
    private HorderService horderService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private RoomService roomService;

    @Autowired
    private ScenicRouteService scenicRouteService;

    @Autowired
    UserSecurityAnswersService userSecurityAnswersService;

    @Autowired
    private HotelImagesMapper hotelImagesMapper;

    @Autowired
    private ScenicImageMapper scenicImageMapper;

    @Autowired
    private ScenicMapper scenicMapper;

    //游客登录浏览 Tourist login
    @RequestMapping("toTouristLogin")
    public String toTouristLogin(Model model,HttpSession session){
        session.setAttribute("currentUser",""); //将当前用户名存入会话中

        List<Scenic> list = scenicService.list(null);
        model.addAttribute("scenicList",list);
        List<Hotel> list1 = hotelService.list(null);
        model.addAttribute("hotelList",list1);


        List<Customer> list2 = customerService.list(null);
        model.addAttribute("peopleCount",list2.size());
        model.addAttribute("scenicCount",list.size());

        QueryWrapper<SOrder>qw1=new QueryWrapper<>();
        qw1.notLike("status",3);
        List<SOrder> list3 = sorderService.list(qw1);
        model.addAttribute("sorderCount",list3.size());

        QueryWrapper<Comment>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("star",5);
        List<Comment> list4 = commentService.list(queryWrapper);
        model.addAttribute("starCount",list4.size());

        Set<String>set=new HashSet<>();
        for (Scenic scenic : list) {
            set.add(scenic.getCountry());
        }
        model.addAttribute("countryList",set);

        //推荐部分
        QueryWrapper<Scenic> lambdaQueryWrapper = new QueryWrapper<>();
        lambdaQueryWrapper.orderBy(true, true, "RAND()");
        lambdaQueryWrapper.last("LIMIT " + 4); //随机推荐4个景点
        List<Scenic> listByU = scenicMapper.selectList(lambdaQueryWrapper);
        model.addAttribute("recommendList",listByU);//将查到的推荐保存在model中


        //设置用户未登录
        model.addAttribute("isUserLogin", false);

        return "user-home";
    }

    @RequestMapping("userlogin")
    public String userlogin(String username, String password, Model model, HttpSession session){
        boolean b=customerService.login(username,password);
        if (b){
            QueryWrapper<Customer>qw=new QueryWrapper<>();
            qw.eq("customer_name",username);
            Customer user = customerService.getOne(qw);
            session.setAttribute("currentUser",username);
            session.setAttribute("userId",user.getId());
            session.setAttribute("image",user.getCimage());
            session.setAttribute("email",user.getEmail());
            session.setAttribute("phone",user.getPhone());
            session.setAttribute("password",password);


            List<Scenic> list = scenicService.list(null);
            model.addAttribute("scenicList",list);
            List<Hotel> list1 = hotelService.list(null);
            model.addAttribute("hotelList",list1);


            List<Customer> list2 = customerService.list(null);
            model.addAttribute("peopleCount",list2.size());
            model.addAttribute("scenicCount",list.size());

            QueryWrapper<SOrder>qw1=new QueryWrapper<>();
            qw1.notLike("status",3);
            List<SOrder> list3 = sorderService.list(qw1);
            model.addAttribute("sorderCount",list3.size());

            QueryWrapper<Comment>queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("star",5);
            List<Comment> list4 = commentService.list(queryWrapper);
            model.addAttribute("starCount",list4.size());

            Set<String>set=new HashSet<>();
            for (Scenic scenic : list) {
                set.add(scenic.getCountry());
            }
            model.addAttribute("countryList",set);

            //推荐部分
            if(session.getAttribute("userId")!=null)
            {
                Integer userId= (Integer) session.getAttribute("userId");//获取当前用户的id
                String currentUser= (String) session.getAttribute("currentUser");//获取当前用户的姓名
                log.info(currentUser);
                MyUserBasedRecommenderImpl muser=new MyUserBasedRecommenderImpl();//new 一个业务层实现类来调取其中的一些关键方法
                List<RecommendedItem> listU=muser.userBasedRecommender(userId,4);//调用userBasedRecommender方法，4代表最终推荐的数量
                log.info(listU.toString());
                if(listU.equals(null)){//对返回的list集合进行遍历，目的是封装图书的list集合
                    List<Scenic> listByU = null;//判断集合是否为空
                    //List<BookNew> listByI = null;

                    model.addAttribute("recommendList",listByU);//将查到的推荐的图书集合的数据库保存在model中
                    //model.addAttribute("listByI",listByI);
                }
                else {
                    List<Scenic> listByU = getRecommend(listU);//调用getRecommend方法封装关于推荐图书的list集合
                    //List<BookNew> listByI = getRecommend(listI);
                    model.addAttribute("recommendList",listByU);//将查到的推荐的图书集合的数据库保存在model中
                    //model.addAttribute("listByI",listByI);
                }

            }
            //设置用户登录
            model.addAttribute("isUserLogin", true);

            return "user-home";
        }else {
            model.addAttribute("msg","用户名或密码错误！");
            return "user-login";
        }
    }
    public List<Scenic> getRecommend(List<RecommendedItem> list){//此方法的作用是封装关于推荐图书的list集合
        List<Scenic> listBook = new ArrayList<>();//new 一个关于图书的list集合
        for(RecommendedItem r:list){//遍历RecommendedItem的集合
            Integer id= Math.toIntExact(r.getItemID());//获取图书id
            Scenic book=scenicService.getById(id);//调用mybatis-plus的getById方法根据id获取图书对象
            listBook.add(book);//将图书封装到图书集合中
        }
        return listBook;//返回推荐的图书的集合
    }

    /**
     * @description: 忘记密码，密保登录
     **/
    @RequestMapping("userloginmiBao")
    public String userloginMiBao(String username, String questionOneAnswer, String questionTwoAnswer,Model model, HttpSession session){

        Boolean userFlag = customerService.selectUser(username);
        if(!userFlag){
            model.addAttribute("msg","用户不存在");
            return "user-login-mibao";
        }
        UserSecurityAnswers userInfo = customerService.selectByUserName(username);

        String originOneAnswer = userInfo.getQuestionOneAnswer();
        String originTwoAnswer = userInfo.getQuestionTwoAnswer();
        boolean b = false;
        if(originOneAnswer.equals(questionOneAnswer.trim()) || originTwoAnswer.equals(questionTwoAnswer.trim())){
            b = true;
        }


        if (b){
            QueryWrapper<Customer>qw=new QueryWrapper<>();
            qw.eq("customer_name",username);
            Customer user = customerService.getOne(qw);
            session.setAttribute("currentUser",username);
            session.setAttribute("userId",user.getId());
            session.setAttribute("image",user.getCimage());
            session.setAttribute("email",user.getEmail());
            session.setAttribute("phone",user.getPhone());


            List<Scenic> list = scenicService.list(null);
            model.addAttribute("scenicList",list);
            List<Hotel> list1 = hotelService.list(null);
            model.addAttribute("hotelList",list1);


            List<Customer> list2 = customerService.list(null);
            model.addAttribute("peopleCount",list2.size());
            model.addAttribute("scenicCount",list.size());

            QueryWrapper<SOrder>qw1=new QueryWrapper<>();
            qw1.notLike("status",3);
            List<SOrder> list3 = sorderService.list(qw1);
            model.addAttribute("sorderCount",list3.size());

            QueryWrapper<Comment>queryWrapper=new QueryWrapper<>();
            queryWrapper.eq("star",5);
            List<Comment> list4 = commentService.list(queryWrapper);
            model.addAttribute("starCount",list4.size());

            Set<String>set=new HashSet<>();
            for (Scenic scenic : list) {
                set.add(scenic.getCountry());
            }
            model.addAttribute("countryList",set);

            //推荐部分
            if(session.getAttribute("userId")!=null)
            {
                Integer userId= (Integer) session.getAttribute("userId");//获取当前用户的id
                String currentUser= (String) session.getAttribute("currentUser");//获取当前用户的姓名
                log.info(currentUser);
                MyUserBasedRecommenderImpl muser=new MyUserBasedRecommenderImpl();//new 一个业务层实现类来调取其中的一些关键方法
                List<RecommendedItem> listU=muser.userBasedRecommender(userId,4);//调用userBasedRecommender方法，4代表最终推荐的数量
                log.info(listU.toString());
                if(listU.equals(null)){//对返回的list集合进行遍历，目的是封装图书的list集合
                    List<Scenic> listByU = null;//判断集合是否为空
                    //List<BookNew> listByI = null;

                    model.addAttribute("recommendList",listByU);//将查到的推荐的图书集合的数据库保存在model中
                    //model.addAttribute("listByI",listByI);
                }
                else {
                    List<Scenic> listByU = getRecommend(listU);//调用getRecommend方法封装关于推荐图书的list集合
                    //List<BookNew> listByI = getRecommend(listI);
                    model.addAttribute("recommendList",listByU);//将查到的推荐的图书集合的数据库保存在model中
                    //model.addAttribute("listByI",listByI);
                }

            }

            //设置用户登录
            model.addAttribute("isUserLogin", true);
            return "user-home";
        }else {
            model.addAttribute("msg","密保答案输入错误！！！");
            return "user-login-mibao";
        }
    }


    @RequestMapping("listScenic")
    public String listScenic(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                             @RequestParam(value = "pageSize",defaultValue = "6",required = false)Integer pageSize, Model model,Scenic scenic, HttpSession session){
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

        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-scenic-list";
    }

    @RequestMapping("scenicSingle/{id}")
    public String scenicSingle(@PathVariable Integer id,Model model,HttpSession session){
        session.setAttribute("scenicId",id);
        Scenic byId = scenicService.getById(id);
        model.addAttribute("scenic",byId);

        List<String>list=new ArrayList<>();
        list.add(byId.getSimage());
        QueryWrapper<Route>qw=new QueryWrapper<>();
        qw.eq("sid",id);
        List<Route> list1 = routeService.list(qw);
        if (list1!=null){
            for (Route route : list1) {
                list.add(route.getRimage());
            }
        }

        model.addAttribute("routeList",list1);
        model.addAttribute("imgList",list);

        /**
         * 只查询审核通过的评论
         **/
        QueryWrapper<Comment>queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("scenic_id",id)
                .eq("status",1);
        List<Comment> list2 = commentService.list(queryWrapper);
        model.addAttribute("commentList",list2);

        //酒店图片合集
        List<String> scenicImageList = new ArrayList<>();

        LambdaQueryWrapper<ScenicImage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ScenicImage::getScenicId,byId.getIdTwo());
        List<ScenicImage> scenicImageList1 = scenicImageMapper.selectList(lambdaQueryWrapper);
        if(scenicImageList1 != null){
            for(ScenicImage scenicImage : scenicImageList1){
                scenicImageList.add(scenicImage.getImagePath());
            }
        }
        model.addAttribute("scenicImageList",scenicImageList);


        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }


        return "user-scenic-single";
    }

    @RequestMapping("addComment")
    public String addComment(Comment comment,HttpSession session){

        String currentUser = (String) session.getAttribute("currentUser");
        String image = (String) session.getAttribute("image");
        Integer userId = (Integer) session.getAttribute("userId");
        comment.setCustomer(currentUser);
        comment.setCimage(image);
        comment.setCommentTime(new Date());
        comment.setStatus(0);
        boolean save = commentService.save(comment);

        ScoreScenic scoreScenic = new ScoreScenic();
        scoreScenic.setUserId(userId);
        scoreScenic.setScenicId(comment.getScenicId());
        scoreScenic.setScore(comment.getStar());
        scoreScenic.setTime(new Date());
        scoreScenicService.save(scoreScenic);

        return "redirect:/scenicSingle/"+comment.getScenicId();
    }

    @RequestMapping("addOrder")
    public String addOrder(SOrder sOrder,String start,String end,HttpSession session,Model model) throws ParseException {
        Integer userId = (Integer) session.getAttribute("userId");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");
//        Date startTime = simpleDateFormat.parse(start);
//        Date endTime = simpleDateFormat.parse(end);
        Date startTime = parseToStandard(start);
        Date endTime = parseToStandard(end);
        Scenic scenic = scenicService.getById(sOrder.getSid());
        double total = scenic.getPrice() * sOrder.getCount();

        //查询用户信息 身份证 电话
        Customer customer = customerService.selectByUserId(userId);
        if(StringUtils.isEmpty(customer.getPhone()) || StringUtils.isEmpty(customer.getIdNumber())){
            String currentUser = (String) session.getAttribute("currentUser");
            if(currentUser == null || currentUser.isEmpty()){
                //设置用户登录
                model.addAttribute("isUserLogin", false);
            }else{
                model.addAttribute("isUserLogin", true);

            }
            return "user-inputOrderInfo";
        }
        sOrder.setPhone(customer.getPhone());
        sOrder.setIdNumber(customer.getIdNumber());
        sOrder.setCid(userId);
        sOrder.setStartTime(startTime);
        sOrder.setEndTime(endTime);
        sOrder.setTotal(total);
        sOrder.setStatus(0);
        boolean save = sorderService.save(sOrder);
        return "redirect:/listMySorder";
    }

    public static Date parseToStandard(String dateStr) {
        // 定义可能的输入格式
        String[] patterns = {
                "MM/dd/yyyy",
                "M/d/yyyy",
                "yyyy/M/d",
                "yyyy-MM-dd"
        };

        LocalDate date = null;
        for (String pattern : patterns) {
            try {
                date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
                break;  // 成功解析就跳出循环
            } catch (DateTimeParseException e) {
                // 尝试下一个格式
            }
        }

        if (date == null) {
            throw new IllegalArgumentException("无法解析日期: " + dateStr);
        }

        // 输出统一格式
        return Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());

    }

    @RequestMapping("listHotel")
    public String listHotel(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                            @RequestParam(value = "pageSize",defaultValue = "6",required = false)Integer pageSize, Model model, Hotel hotel,HttpSession session){
        if (pageNum<0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<0||pageSize.equals("")||pageSize==null){
            pageSize=6;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Hotel>queryWrapper=new QueryWrapper<>();
        if (hotel.getSid()!=null){
            queryWrapper.eq("sid",hotel.getSid());
        }
        List<Hotel> list = hotelService.list(queryWrapper);
        for (Hotel hotel1 : list) {
            hotel1.setSname(scenicService.getById(hotel1.getSid()).getSname());
        }
        PageInfo<Hotel>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        List<Scenic> list1 = scenicService.list(null);
        model.addAttribute("scenicList",list1);

        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-hotel-list";
    }

    @RequestMapping("hotelSingle/{id}")
    public String hotelSingle(@PathVariable Integer id,Model model,HttpSession session){
        session.setAttribute("hotelId",id);
        Hotel hotel = hotelService.getById(id);
        hotel.setSname(scenicService.getById(hotel.getSid()).getSname());
        model.addAttribute("hotel",hotel);
        List<String>list=new ArrayList<>();
        list.add(hotel.getHimage());

        QueryWrapper<Room>qw=new QueryWrapper<>();
        qw.eq("hid",id);
        List<Room> list1 = roomService.list(qw);
        if (list1!=null){
            for (Room room : list1) {
                list.add(room.getRimage());
            }
        }

        //酒店图片合集
        List<String> HotelImageList = new ArrayList<>();

        LambdaQueryWrapper<HotelImage> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(HotelImage::getHotelId,hotel.getIdTwo());
        List<HotelImage> hotelImageList = hotelImagesMapper.selectList(lambdaQueryWrapper);
        if(hotelImageList != null){
            for(HotelImage hotelImage : hotelImageList){
                HotelImageList.add(hotelImage.getImagePath());
            }
        }

        model.addAttribute("roomList",list1);
        model.addAttribute("imgList",list);
        model.addAttribute("HotelImageList",HotelImageList);

        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-hotel-single";
    }


    @RequestMapping("addHorder")
    public String addHorder(Horder horder,String start,String end,Model model,HttpSession session) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yy");

//        Date startTime = simpleDateFormat.parse(start);
//        Date endTime = simpleDateFormat.parse(end);
        Date startTime = parseToStandard(start);
        Date endTime = parseToStandard(end);


        Integer userId = (Integer) session.getAttribute("userId");

        //查询用户信息 身份证 电话
        Customer customer = customerService.selectByUserId(userId);
        if(StringUtils.isEmpty(customer.getPhone()) || StringUtils.isEmpty(customer.getIdNumber())){
            String currentUser = (String) session.getAttribute("currentUser");
            if(currentUser == null || currentUser.isEmpty()){
                //设置用户登录
                model.addAttribute("isUserLogin", false);
            }else{
                model.addAttribute("isUserLogin", true);

            }
            return "user-inputOrderInfo";
        }
        horder.setPhone(customer.getPhone());
        horder.setIdNumber(customer.getIdNumber());
        long i =daysBetween(startTime,endTime);
        horder.setCid(userId);
        horder.setStartTime(startTime);
        horder.setEndTime(endTime);
        horder.setCount((int) i);
        horder.setTotal(i*roomService.getById(horder.getRid()).getPrice());
        horder.setStatus(0);
        boolean save = horderService.save(horder);

        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "redirect:/listMyHorder";
    }

    public static long daysBetween(Date startTime, Date endTime) {
        ZoneId zone = ZoneId.of("Asia/Taipei"); // 或者用 ZoneId.systemDefault()
        LocalDate start = startTime.toInstant().atZone(zone).toLocalDate();
        LocalDate end   = endTime.toInstant().atZone(zone).toLocalDate();
        return ChronoUnit.DAYS.between(start, end); // 结果为“相差多少天（不含当天，左闭右开）”
    }

    @RequestMapping("listWish")
    public String listWish(HttpSession session,Model model){
        Integer userId = (Integer) session.getAttribute("userId");
        List<SOrder>list=sorderService.listWish(userId);
        model.addAttribute("wishList",list);

        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-wish-list";

    }

    @RequestMapping("addWish/{id}")
    public String addWish(@PathVariable Integer id,HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");
        QueryWrapper<SOrder>qw=new QueryWrapper<>();
        qw.eq("cid",userId);
        qw.eq("sid",id);
        qw.eq("status",3);
        SOrder one = sorderService.getOne(qw);
        if (one!=null){
            return "redirect:/listWish";
        }
        SOrder sOrder = new SOrder();
        sOrder.setCid(userId);
        sOrder.setSid(id);
        sOrder.setStatus(3);
        sorderService.save(sOrder);

        return "redirect:/listWish";
    }

    @RequestMapping("delWish/{id}")
    public String delWish(@PathVariable Integer id){
        boolean b = sorderService.removeById(id);
        return "redirect:/listWish";
    }

    @RequestMapping("listMySorder")
    public String listMySorder(HttpSession session,Model model){
        Integer userId = (Integer) session.getAttribute("userId");

        List<SOrder>list=sorderService.listMySorder(userId);

        for(int i = 0; i < list.size(); i++){
            SOrder sOrder = list.get(i);
            Integer status = sOrder.getStatus();
            if(status == 0){
                sOrder.setStatusShow("待支付");
            }else if(status == 1){
                sOrder.setStatusShow("已支付");
            }else if(status == 2){
                sOrder.setStatusShow("已取消");
            }
        }

        PageInfo<SOrder>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }

        return "user-order-scenic";
    }

    @RequestMapping("delMyOrder/{id}")
    public String delMyOrder(@PathVariable Integer id){
        boolean b = sorderService.removeById(id);
        return "redirect:/listMySorder";
    }

    @RequestMapping("listMyHorder")
    public String listMyHorder(HttpSession session,Model model){
        Integer userId = (Integer) session.getAttribute("userId");
        QueryWrapper<Horder>qw=new QueryWrapper<>();
        qw.eq("cid",userId);
        List<Horder> list = horderService.list(qw);
        for (Horder horder : list) {
            horder.setHimage(hotelService.getById(roomService.getById(horder.getRid()).getHid()).getHimage());
            horder.setHname(hotelService.getById(roomService.getById(horder.getRid()).getHid()).getHname());
            horder.setCity(hotelService.getById(roomService.getById(horder.getRid()).getHid()).getCity());
            horder.setAddress(hotelService.getById(roomService.getById(horder.getRid()).getHid()).getAddress());
            horder.setRname(roomService.getById(horder.getRid()).getRname());
            horder.setPrice(roomService.getById(horder.getRid()).getPrice());
        }
        PageInfo<Horder>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);

        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-order-hotel";
    }

    @RequestMapping("delMyHorder/{id}")
    public String delMyHorder(@PathVariable Integer id){
        boolean b = horderService.removeById(id);
        return "redirect:/listMyHorder";
    }

    @RequestMapping("listNotice")
    public String listNotice(Model model,HttpSession session){
        List<Notice> list = noticeService.list(null);
        model.addAttribute("noticeList",list);

        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }

        return "user-notice-list";
    }

    @RequestMapping("userProfile")
    public String profile(HttpSession session,Model model ){
        session.removeAttribute("loginFail");
        String currentUser = (String) session.getAttribute("currentUser");
        String  password = (String) session.getAttribute("password");
        QueryWrapper<Customer>qw=new QueryWrapper<>();
        qw.eq("customer_name",currentUser);
        Customer one = customerService.getOne(qw);
        one.setPassword(password);
        model.addAttribute("user",one);

        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-profile-info";

    }

    //查询所有订单
    @RequestMapping("/userProfileSorder")
    public String userProfileSorder(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                                    @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model,HttpSession session){
        if (pageNum<=0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<=0||pageSize.equals("")||pageSize==null){
            pageSize=10;
        }
        Integer userId = (Integer) session.getAttribute("userId");


        PageHelper.startPage(pageNum,pageSize);
        List<SOrder> list = sorderService.listMySorder(userId);
        PageInfo<SOrder>pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);
        }
        return "user-profile-sorder";
    }

//    @RequestMapping("pay/{id}")
//    public String pay(@PathVariable Integer id){
//        SOrder byId = sorderService.getById(id);
//        byId.setStatus(1);
//        sorderService.updateById(byId);
//        return "redirect:/userProfileSorder";
//    }
    @RequestMapping("cancel/{id}")
    public String cancel(@PathVariable Integer id){
        SOrder byId = sorderService.getById(id);
        byId.setStatus(2);
        sorderService.updateById(byId);
        return "redirect:/userProfileSorder";
    }

    @RequestMapping("/userProfileHorder")
    public String userProfileHorder(@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum,
                                    @RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize, Model model,HttpSession session){
        if (pageNum<=0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<=0||pageSize.equals("")||pageSize==null){
            pageSize=10;
        }
        Integer userId = (Integer) session.getAttribute("userId");


        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<Horder>qw=new QueryWrapper<>();
        qw.eq("cid",userId);
        List<Horder> horderList = horderService.list(qw);
        for (Horder horder : horderList) {
            horder.setHimage(hotelService.getById(roomService.getById(horder.getRid()).getHid()).getHimage());
            horder.setHname(hotelService.getById(roomService.getById(horder.getRid()).getHid()).getHname());
            horder.setCity(hotelService.getById(roomService.getById(horder.getRid()).getHid()).getCity());
            horder.setAddress(hotelService.getById(roomService.getById(horder.getRid()).getHid()).getAddress());
            horder.setRname(roomService.getById(horder.getRid()).getRname());
            horder.setPrice(roomService.getById(horder.getRid()).getPrice());
            horder.setRimage(roomService.getById(horder.getRid()).getRimage());
            horder.setBed(roomService.getById(horder.getRid()).getBed());
            horder.setBig(roomService.getById(horder.getRid()).getBig());
        }
        PageInfo<Horder> list = new PageInfo<>(horderList);
        model.addAttribute("pageInfo",list);
        log.info(list.toString());
        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-profile-Horder";
    }

//    @RequestMapping("payHorder/{id}")
//    public String payHorder(@PathVariable Integer id){
//        Horder byId = horderService.getById(id);
//        byId.setStatus(1);
//        horderService.updateById(byId);
//        return "redirect:/userProfileHorder";
//    }
    @RequestMapping("cancelHorder/{id}")
    public String cancelHorder(@PathVariable Integer id){
        Horder byId = horderService.getById(id);
        byId.setStatus(2);
        horderService.updateById(byId);
        return "redirect:/userProfileHorder";
    }

    @RequestMapping("userProfileWish")
    public String userProfileWish(Model model,HttpSession session){
        Integer userId = (Integer) session.getAttribute("userId");

        List<SOrder> list1 = sorderService.listWish(userId);
        model.addAttribute("wishList",list1);
        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-profile-wish";
    }

    @RequestMapping("userSetting")
    public String userSetting(HttpSession session,Model model ){

        String currentUser = (String) session.getAttribute("currentUser");
        String  password = (String) session.getAttribute("password");
        QueryWrapper<Customer>qw=new QueryWrapper<>();
        qw.eq("customer_name",currentUser);
        Customer one = customerService.getOne(qw);
        one.setPassword(password);
        model.addAttribute("user",one);
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-profile-set";

    }


    @RequestMapping("userUpdateProfile")
    public String userUpdateProfile(Customer customer, MultipartFile file, HttpSession session){
        if (!file.isEmpty()){
            transFilea(customer,file);//给customer对象传头像
        }
        String s = DigestUtil.md5Hex(customer.getPassword());
        customer.setPassword(s);
        boolean b = customerService.updateById(customer);

        QueryWrapper<Customer>qw=new QueryWrapper<>();
        qw.eq("customer_name",customer.getCustomerName());
        Customer user = customerService.getOne(qw);
        session.setAttribute("currentUser",user.getCustomerName());
        session.setAttribute("userId",user.getId());
        session.setAttribute("image",user.getCimage());
        session.setAttribute("email",user.getEmail());
        session.setAttribute("phone",user.getPhone());
        session.setAttribute("password",user.getPassword());
        return "redirect:/userSetting";
    }

    private void transFilea(Customer customer, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();//获得文件全名
        int index = originalFilename.lastIndexOf(".");//将文件名前后分开
        String suffix = originalFilename.substring(index);//构造后缀
        String prefix =System.nanoTime()+"";//构造前缀
        String path=prefix+suffix;//拼接成文件名
        File file1 = new File(location);//new 一个要下载的路径的file对象
        if (!file1.exists()){//如果电脑上不存在这个目录
            file1.mkdirs();//创建这个目录
        }
        File file2 = new File(file1,path);
        try {
            file.transferTo(file2);//调用上传方法
        } catch (IOException e) {
            e.printStackTrace();
        }
        customer.setCimage(path);//赋cimage字段
    }

    //修改密码
    @RequestMapping("userUpdatePassword")
    public String userUpdatePassword(String userPwd, String newPwd,String confirmPwd, Model model, HttpSession session) {
        if (!newPwd.equals(confirmPwd)){
            session.setAttribute("loginFail", "两次输入的的新密码不一致");
            return "redirect:/userSetting";
        }
        String currentUser = (String) session.getAttribute("currentUser");
        boolean login = customerService.login(currentUser, userPwd);
        if (login) {
            Customer user = new Customer();
            user.setCustomerName(currentUser);
            String newPassword = DigestUtil.md5Hex(newPwd);
            user.setPassword(newPassword);
            QueryWrapper<Customer>qw=new QueryWrapper<>();
            qw.eq("customer_name",user.getCustomerName());
            boolean b = customerService.update(user,qw);
            if (b) {
                session.setAttribute("loginFail", "修改密码成功");
                return "redirect:/userSetting";
            } else {
                session.setAttribute("loginFail", "修改密码失败");
            }
        } else {
            session.setAttribute("loginFail", "用户验证失败");
        }
        return "redirect:/userSetting";
    }

    @RequestMapping("userlogout")
    public String userlogout(HttpSession session){
        session.setAttribute("currentUser","");
        return "user-login";
    }


    @RequestMapping("toUserLogin")
    public String toUserLogin(){
        return "user-login";
    }
    @RequestMapping("toUserRegister")
    public String toUserRegister(){
        return "user-register";
    }
    @RequestMapping("toUserLoginMiBao")
    public String toUserLoginMiBao(){
        return "user-login-mibao";
    }
    //注册
    @RequestMapping("/userRegister")
    public String register(String userName, String userPwd,String confirmPwd,String questionOneAnswer,String questionTwoAnswer, Model model) {

        if(StringUtils.isEmpty(userName) || StringUtils.isEmpty(userPwd) || StringUtils.isEmpty(confirmPwd) || StringUtils.isEmpty(questionOneAnswer) || StringUtils.isEmpty(questionTwoAnswer)){
            model.addAttribute("msg", "请填写完整信息");
            return "user-register";
        }
        QueryWrapper<Customer>qw=new QueryWrapper<>();
        qw.eq("customer_name",userName);
        Customer one = customerService.getOne(qw);
        if (one!=null){
            model.addAttribute("msg", "该用户已存在");
            return "user-register";
        }

        if (!userPwd.equals(confirmPwd)){
            model.addAttribute("msg", "输入密码不一致");
            return "user-register";
        }else {
            Customer customer = new Customer();
            customer.setCustomerName(userName);
            String s = DigestUtil.md5Hex(userPwd);
            customer.setPassword(s);
            customerService.save(customer);

            //密保问题保存
            String questionOne = "密保1：您的母亲姓名是什么？";
            String questionTwo = "密保2：您的小学名称是什么？";

            String oneAnswer = questionOneAnswer;
            String twoAnswer = questionTwoAnswer;
            UserSecurityAnswers securityAnswers = new UserSecurityAnswers();
            securityAnswers.setCustomerName(userName);
            securityAnswers.setQuestionOne(questionOne);
            securityAnswers.setQuestionOneAnswer(oneAnswer);
            securityAnswers.setQuestionTwo(questionTwo);
            securityAnswers.setQuestionTwoAnswer(twoAnswer);

            //先判断密保是否已经存在
            UserSecurityAnswers userSecurityAnswers = userSecurityAnswersService.selectByUserName(userName);
            if(userSecurityAnswers != null){
                //修改
                securityAnswers.setId(userSecurityAnswers.getId());
                userSecurityAnswersService.updateById(securityAnswers);
            }else{
                userSecurityAnswersService.save(securityAnswers);
            }
            return "user-login";
        }

    }

    @RequestMapping("user")
    public String user(Model model,HttpSession session){
        List<Scenic> list = scenicService.list(null);
        List<Hotel> list1 = hotelService.list(null);
        Set<String>set=new HashSet<>();
        for (Scenic scenic : list) {
            set.add(scenic.getCountry());
        }
        model.addAttribute("countryList",set);
        model.addAttribute("scenicList",list);
        model.addAttribute("hotelList",list1);


        QueryWrapper<SOrder>qw1=new QueryWrapper<>();
        qw1.notLike("status","3");
        List<SOrder> sOrderList = sorderService.list(qw1);
        log.info(sOrderList.size()+"");
        model.addAttribute("sorderCount",sOrderList.size());
        model.addAttribute("scenicCount",list.size());
        List<Customer> list2 = customerService.list(null);
        model.addAttribute("peopleCount",list2.size());
        QueryWrapper<Comment>qw2=new QueryWrapper<>();
        qw2.eq("star",5);
        List<Comment> list3 = commentService.list(qw2);
        model.addAttribute("starCount",list3.size());


        //推荐部分
        if(session.getAttribute("userId")!=null)
        {
            Integer userId= (Integer) session.getAttribute("userId");//获取当前用户的id
            String currentUser= (String) session.getAttribute("currentUser");//获取当前用户的姓名
            log.info(currentUser);
            MyUserBasedRecommenderImpl muser=new MyUserBasedRecommenderImpl();//new 一个业务层实现类来调取其中的一些关键方法
            List<RecommendedItem> listU=muser.userBasedRecommender(userId,4);//调用userBasedRecommender方法，5代表最终推荐的图书的数量
            log.info(listU.toString());
            if(listU.equals(null)){//对返回的list集合进行遍历，目的是封装图书的list集合
                List<Scenic> listByU = null;//判断集合是否为空
                //List<BookNew> listByI = null;

                model.addAttribute("recommendList",listByU);//将查到的推荐的图书集合的数据库保存在model中
                //model.addAttribute("listByI",listByI);
            }
            else {
                List<Scenic> listByU = getRecommend(listU);//调用getRecommend方法封装关于推荐图书的list集合
                //List<BookNew> listByI = getRecommend(listI);
                model.addAttribute("recommendList",listByU);//将查到的推荐的图书集合的数据库保存在model中
                //model.addAttribute("listByI",listByI);
            }

        }
        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);
        }

        return "user-home";
    }


    /**
     * @description: 路线推荐
     * @author: author
     * @date: 2024/12/14 14:16
     * @param: [pageNum, pageSize, model, scenicRoute]
     * @return: java.lang.String
     **/

    @RequestMapping("listScenicRouteFront")
    public String listScenicRouteFront(@RequestParam(value = "pageNum",defaultValue = "1",required = false)Integer pageNum,
                                       @RequestParam(value = "pageSize",defaultValue = "6",required = false)Integer pageSize, Model model, ScenicRoute scenicRoute,HttpSession session){
        if (pageNum<0||pageNum.equals("")||pageNum==null){
            pageNum=1;
        }
        if (pageSize<0||pageSize.equals("")||pageSize==null){
            pageSize=6;
        }
        PageHelper.startPage(pageNum,pageSize);
        QueryWrapper<ScenicRoute> qw=new QueryWrapper<>();
        if (scenicRoute.getTitle()!=null){
            qw.like("title",scenicRoute.getTitle());
        }
        List<ScenicRoute> list = scenicRouteService.list(qw);

        PageInfo<ScenicRoute> pageInfo=new PageInfo<>(list);
        model.addAttribute("pageInfo",pageInfo);
        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);
        }

        return "user-scenicRoute-list";
    }

    @RequestMapping("listScenicRouteFront/{id}")
    public String listScenicRouteFront(@PathVariable Integer id,Model model,HttpSession session){
        session.setAttribute("scenicRouteId",id);
        ScenicRoute scenicRoute = scenicRouteService.getById(id);
        model.addAttribute("scenicRoute",scenicRoute);

        String currentUser = (String) session.getAttribute("currentUser");
        if(currentUser == null || currentUser.isEmpty()){
            //设置用户登录
            model.addAttribute("isUserLogin", false);
        }else{
            model.addAttribute("isUserLogin", true);

        }
        return "user-scenicRoute-single";
    }



}
