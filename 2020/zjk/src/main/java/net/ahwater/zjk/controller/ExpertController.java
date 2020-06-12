package net.ahwater.zjk.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.zjk.entity.dto.ExpertDTO;
import net.ahwater.zjk.entity.vo.R;
import net.ahwater.zjk.service.ExpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by yqz on 2020/5/26
 */
@Api(tags = "专家信息模块")
@SuppressWarnings("all")
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("expert")
public class ExpertController{

    @Autowired
    private ExpertService expertService;

    @ApiOperation("查看所有的专家信息")
    @GetMapping("/listAll")
    public R listAll(){
        return expertService.listAll();
    }

    @ApiOperation("新增专家信息")
    @PostMapping("/add")
    public R add(ExpertDTO dto){
        return expertService.add(dto);
    }

    @ApiOperation("修改专家信息")
    @PostMapping("/modify")
    public R modify(ExpertDTO dto){
        return expertService.modify(dto);
    }

    @ApiOperation("修改专家信息")
    @PostMapping("/remove")
    public R remove(Integer id){
        return expertService.remove(id);
    }

    @ApiOperation("查询单个专家信息")
    @GetMapping("/listById")
    public R listById(Integer id) {
        return expertService.listById(id);
    }
}