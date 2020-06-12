
package net.ahwater.web;

import net.ahwater.service.WxMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

@Controller
public class IndexController implements ServletContextAware {

    private Logger log = LoggerFactory.getLogger(IndexController.class);

    private String imgUploadDirPath;



    @Autowired
    private WxMsgService wxMsgService;

    @Override
    public void setServletContext(ServletContext servletContext) {
        imgUploadDirPath = servletContext.getRealPath("/wx_img");
    }

    /**
     * 首页展示
     * @return
     */
    @RequestMapping(value = "/news")
    public String news(){
        return "index";
    }
    /**
     * 公共上报
     * @return
     */
    @RequestMapping(value = "/report")
    public String zqsb() {
        return "zqsb";
    }


    /**
     * 上传公示牌
     * @return
     */
    @RequestMapping(value = "/billboard")
    public String pcd() {
        return "publicCard";
    }

    /**
     *
     * @return
     */
    @RequestMapping(value = "/main")
    public String main() {
        return "main";
    }

}

