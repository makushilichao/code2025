package com.lc.service;

import cn.hutool.core.date.DateUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lc.entity.Account;
import com.lc.entity.Notice;
import com.lc.exception.CustomerException;
import com.lc.mapper.NoticeMapper;
import com.lc.utils.TokenUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class NoticeService {

    @Resource
    NoticeMapper noticeMapper;

    public void add(Notice notice) {
        Account currentUser = TokenUtils.getCurrentUser();
        if("USER".equals(currentUser.getRole())){
            throw new CustomerException("500", "您无权操作！");
        }
        notice.setCreateTime(DateUtil.now());
        noticeMapper.insert(notice);
    }

    public void updateById(Notice notice) {
        Account currentUser = TokenUtils.getCurrentUser();
        if("USER".equals(currentUser.getRole())){
            throw new CustomerException("500", "您无权操作！");
        }
        notice.setCreateTime(DateUtil.now());
        noticeMapper.updateById(notice);
    }

    public void deleteById(Integer id) {
        Account currentUser = TokenUtils.getCurrentUser();
        if("USER".equals(currentUser.getRole())){
            throw new CustomerException("500", "您无权操作！");
        }
        noticeMapper.deleteById(id);
    }

    public List<Notice> selectAll(Notice notice) {
        return noticeMapper.selectAll(notice);
    }

    public PageInfo<Notice> selectPage(Integer pageNum, Integer pageSize, Notice notice) {
        PageHelper.startPage(pageNum, pageSize);
        List<Notice> list = noticeMapper.selectAll(notice);
        return PageInfo.of(list);
    }

}
