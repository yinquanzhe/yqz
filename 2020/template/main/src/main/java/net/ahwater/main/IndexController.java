package net.ahwater.main;

import lombok.extern.slf4j.Slf4j;
import net.ahwater.main.entity.vo.R;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Reeye on 2020/4/20 12:42
 * Nothing is true but improving yourself.
 */
@Slf4j
@Controller
@RestControllerAdvice
@RequestMapping("/")
public class IndexController extends AbstractErrorController {
    
    @Value("${spring.profiles.active}")
    private String profile;

    public IndexController(ErrorAttributes errorAttributes) {
        super(errorAttributes);
    }

    @GetMapping
    @ResponseBody
    public String index() {
        return "Hello world!";
    }

    @RequestMapping("/error")
    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public R error(Exception e) {
        if ("dev".equals(profile)) {
            e.printStackTrace();
        }
        return R.error(e.getMessage());
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
