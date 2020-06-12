package org.mintleaf.modules.video.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 图像识别相关控制器
 * @Author: MengchuZhang
 * @Date: 2018/9/14 9:17
 * @Version 1.0
 */
@Controller
@RequestMapping("tracking")
public class TrackingController {

    /**
     * 进入首页
     * @return
     */
    @RequestMapping(value="index.html")
    public ModelAndView index(){
        ModelAndView view =new ModelAndView("modules/video/tracking/index.html");
        return view;
    }

}
