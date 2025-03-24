package com.lc.controller;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.lc.common.Result;
import com.lc.entity.Blog;
import com.lc.entity.Category;
import com.lc.entity.User;
import com.lc.service.BlogService;
import com.lc.service.CategoryService;
import com.lc.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Resource
    private BlogService blogService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private UserService userService;

    @GetMapping("/pie")
    public Result pie() {
        List<Map<String, Object>> list = new ArrayList<>();
        List<Category> categories = categoryService.selectAll(new Category());
        List<Blog> blogs = blogService.selectAll(new Blog());
        for (Category category : categories) {
            long count = blogs.stream().filter(blog -> category.getId().equals(blog.getCategoryId())).count();
            Map<String, Object> map = new HashMap<>();
            map.put("name", category.getTitle());
            map.put("value", count);
            list.add(map);
        }
        list.sort((a, b) -> Long.compare((Long) b.get("value"), (Long) a.get("value")));

        List<Map<String, Object>> topFive = new ArrayList<>();
        long otherCount = 0;
        for (int i = 0; i < list.size(); i++) {
            if (i < 5) {
                topFive.add(list.get(i));
            } else {
                otherCount += (Long) list.get(i).get("value");
            }
        }

        if (otherCount > 0) {
            Map<String, Object> otherMap = new HashMap<>();
            otherMap.put("name", "其他");
            otherMap.put("value", otherCount);
            topFive.add(otherMap);
        }
        return Result.success(topFive);
    }

    @GetMapping("/bar")
    public Result bar() {
        Map<String, Object> list = new HashMap<>();
        List<String> xList = new ArrayList<>();
        List<Long> yList = new ArrayList<>();
        Map<String, Long> map = new HashMap<>();

        List<User> users = userService.selectAll(new User());
        List<Blog> blogs = blogService.selectAll(new Blog());
        for (User user : users) {
            long count = blogs.stream().filter(blog -> user.getId().equals(blog.getUserId())).count();
            map.put(user.getName(), count);
        }
        //对map进行排序
        LinkedHashMap<String, Long> collect = map.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        for (String key : collect.keySet()) {
            xList.add(key);
            yList.add(collect.get(key));
        }

        //选取Top5
        if (xList.size() > 5 && yList.size() > 5) {
            xList = xList.subList(0, 5);
            yList = yList.subList(0, 5);
        }

        list.put("xAxis", xList);
        list.put("yAxis", yList);
        return Result.success(list);
    }

    @GetMapping("/line")
    public Result line() {
        Map<String, Object> resultMap = new HashMap<>();
        List<Long> yList = new ArrayList<>();

        Date date = new Date();
        DateTime dateTime = DateUtil.offsetDay(date, -7);
        List<String> xList = DateUtil.rangeToList(dateTime, date, DateField.DAY_OF_MONTH).stream().map(DateUtil::formatDate).toList();

        List<Blog> blogs = blogService.selectAll(new Blog());
        for (String day : xList) {
            long count = blogs.stream().filter(x -> ObjectUtil.isNotEmpty(x.getCreateTime()) && x.getCreateTime().contains(day)).count();
            yList.add(count);
        }
        
        resultMap.put("xAxis", xList);
        resultMap.put("yAxis", yList);
        return Result.success(resultMap);
    }
}