package com.lc.controller;

import com.github.pagehelper.PageInfo;
import com.lc.common.Result;
import com.lc.entity.Carousel;
import com.lc.service.CarouselService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Resource
    CarouselService carouselService;

    @GetMapping("/selectAll")
    public Result selectAll(Carousel carousel) {
        List<Carousel> carouselList = carouselService.selectAll(carousel);
        return Result.success(carouselList);
    }

    @GetMapping("/selectPage")
    public Result selectPage(@RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize,
                             Carousel carousel){
        PageInfo<Carousel> carouselPageInfo = carouselService.selectPage(pageNum, pageSize, carousel);
        return Result.success(carouselPageInfo);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Carousel carousel){
        carouselService.add(carousel);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Carousel carousel){
        carouselService.updateById(carousel);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id){
        carouselService.deleteById(id);
        return Result.success();
    }

}
