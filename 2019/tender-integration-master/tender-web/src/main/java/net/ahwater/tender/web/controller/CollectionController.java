package net.ahwater.tender.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanCollection;
import net.ahwater.tender.db.entity.BeanItem;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.db.entity.R;
import net.ahwater.tender.db.mapper.CollectionMapper;
import net.ahwater.tender.db.mapper.ItemMapper;
import net.ahwater.tender.web.annotation.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by yyc on 2018/2/27 18:42.
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Api(tags = "用户收藏控制类")
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/collection")
public class CollectionController {

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private ItemMapper itemMapper;

    @PostMapping("/add/{id}")
    @ApiOperation(value = "新增一个收藏", response = BeanCollection.class)
    public R add(
            @CurrentUser BeanUser user,
            @ApiParam(name = "id", value = "item的id", required = true, example = "1")
            @PathVariable Integer id) {
        BeanItem item = itemMapper.selectById(id);
        if (item != null) {
            BeanCollection collection = new BeanCollection().setItemId(id).setUserId(user.getId());
            if (collectionMapper.insert(collection) == 1) {
                return R.ok("添加成功", collection);
            } else {
                return R.error("添加失败");
            }
         } else {
            return R.of(R.NOT_FOUND, "id:" + id + "不存在", null);
        }
    }

    @PostMapping("/delete/{id}")
    @ApiOperation("删除收藏")
    public R delete(
            @CurrentUser BeanUser user,
            @ApiParam(name = "id", value = "要删除的收藏id", required = true, example = "1")
            @PathVariable Integer id) {
        int res = collectionMapper.delete(new LambdaQueryWrapper<BeanCollection>()
                .eq(BeanCollection::getUserId, user.getId())
                .eq(BeanCollection::getId, id));
        return res == 1 ? R.ok("删除成功", null) : R.error("删除失败");
    }

}
