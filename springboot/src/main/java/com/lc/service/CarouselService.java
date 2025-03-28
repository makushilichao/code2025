package com.lc.service;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Account;
import com.lc.entity.Carousel;
import com.lc.mapper.CarouselMapper;
import com.lc.mapper.CategoryMapper;
import com.lc.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CarouselService {

    @Resource
    CarouselMapper carouselMapper;

    public void add(Carousel carousel) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("未获取到当前用户信息，请先登录！");
        }
        Integer id = currentUser.getId();
        if (id == null) {
            throw new RuntimeException("当前用户 ID 为空！");
        }
        carousel.setAdminId(id.toString());
        if("USER".equals(currentUser.getRole())){
            throw new RuntimeException("您无权操作！");
        }
        if(carousel.getImg() == null || carousel.getImg().equals("")){
            throw new RuntimeException("请上传轮播图！");
        }
        carousel.setCreateTime(DateUtil.now());
        carouselMapper.insert(carousel);
    }

    public void updateById(Carousel carousel) {
        carousel.setCreateTime(DateUtil.now());
        carouselMapper.updateById(carousel);
    }

    public void deleteById(Integer id) {
        carouselMapper.deleteById(id);
    }

    public List<Carousel> selectAll(Carousel carousel) {
        return carouselMapper.selectAll(carousel);
    }

    public PageInfo<Carousel> selectPage(Integer pageNum, Integer pageSize, Carousel carousel) {
        PageHelper.startPage(pageNum, pageSize);
        List<Carousel> list = carouselMapper.selectAll(carousel);
        return PageInfo.of(list);
    }

}
