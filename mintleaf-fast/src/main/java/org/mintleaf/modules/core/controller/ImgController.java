package org.mintleaf.modules.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 图片管理相关控制器
 * @Author: MengchuZhang
 * @Date: 2018/9/14 9:17
 * @Version 1.0
 */
@Controller
@RequestMapping("img")
public class ImgController {

    /**
     * 进入首页
     * @return
     */
    @RequestMapping(value="index.html")
    public ModelAndView index(){
        ModelAndView view =new ModelAndView("modules/core/img/images.html");
        return view;
    }

}
