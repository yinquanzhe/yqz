package net.ahwater.tender.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.config.Constants;
import net.ahwater.tender.db.entity.BeanTime;
import net.ahwater.tender.db.entity.R;
import net.ahwater.tender.db.mapper.TimeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yyc on 2018/1/24 17:35.
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Api(tags = "抓取时间控制类")
@Slf4j
@RestController
@RequestMapping("/time")
@CrossOrigin
@PreAuthorize("hasRole('1')")
public class TimeController {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private TimeMapper timeMapper;

    @ApiOperation(value = "获取所有时间点", response = BeanTime.class)
    @GetMapping("/listAll")
    public List<BeanTime> listAllByWsid() {
        return timeMapper.selectList(null);
    }

    @ApiOperation(value = "新增时间点", response = BeanTime.class)
    @PostMapping("/add")
    public R add(BeanTime bean) {
        log.debug(bean.toString());
        int res = bean.getId() != null ? timeMapper.updateById(bean) : timeMapper.insert(bean);
        if (res == 1) {
            jmsMessagingTemplate.convertAndSend(Constants.TIME_QUEUE, bean.getId() + "");
            return R.ok("保存成功", bean);
        } else {
            return R.error("保存失败");
        }
    }

    @ApiOperation("根据ID删除时间点")
    @PostMapping("/delete/{id}")
    public R deleteById(@PathVariable Integer id) {
        int res = timeMapper.deleteById(id);
        if (res == 1) {
            jmsMessagingTemplate.convertAndSend(Constants.TIME_QUEUE, id + "");
            return R.ok("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    @ApiOperation(value = "修改时间点", response = BeanTime.class)
    @PostMapping("/modify")
    public R modify(BeanTime bean) {
        int res = timeMapper.updateById(bean);
        return res == 1 ? R.ok("修改成功", bean) : R.error("修改失败");
    }

}
