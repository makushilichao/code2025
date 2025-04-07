package com.lc.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Category2;
import com.lc.mapper.Category2Mapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class Category2Service {

    @Resource
    Category2Mapper category2Mapper;

    public void add(Category2 category2) {
        category2Mapper.insert(category2);
    }

    public void updateById(Category2 category2) {
        category2Mapper.updateById(category2);
    }

    public void deleteById(Integer id) {
        category2Mapper.deleteById(id);
    }

    public List<Category2> selectAll(Category2 category2) {
        return category2Mapper.selectAll(category2);
    }

    public PageInfo<Category2> selectPage(Integer pageNum, Integer pageSize, Category2 category2) {
        PageHelper.startPage(pageNum, pageSize);
        List<Category2> list = category2Mapper.selectAll(category2);
        return PageInfo.of(list);
    }

    public Category2 selectById(Integer category2Id) {
        return category2Mapper.selectById(category2Id);
    }
}
