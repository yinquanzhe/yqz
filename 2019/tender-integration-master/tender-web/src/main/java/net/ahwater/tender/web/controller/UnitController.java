package net.ahwater.tender.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.*;
import net.ahwater.tender.db.mapper.*;
import net.ahwater.tender.web.annotation.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * Created by yyc on 2018/2/27 18:42.
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Api(tags = "部门管理控制类")
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/unit")
public class UnitController {

    @Autowired
    private UnitMapper unitMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ExcludedWordMapper excludedWordMapper;

    @Autowired
    private KeywordGroupMapper keywordGroupMapper;

    @Autowired
    private KeywordMapper keywordMapper;

    @Autowired
    private KeywordGroupUnitMapper keywordGroupUnitMapper;

    @PreAuthorize("hasRole('1')")
    @PostMapping("/addTopLevel")
    @ApiOperation(value = "管理员新增一个一级部门", response = BeanUnit.class)
    public R addTopLevel(
            @CurrentUser BeanUser user,
            String unitName) {
        BeanUnit unit = new BeanUnit()
                .setName(unitName)
                .setParentId(0)
                .setKeywordModify(true);
        if (unitMapper.insert(unit) == 1) {
            return R.ok("添加成功", unit);
        } else {
            return R.error("添加失败");
        }
    }

    @PreAuthorize("hasAnyRole('1', '2')")
    @PostMapping("/addChild")
    @ApiOperation(value = "新增一个二级部门", response = BeanUnit.class)
    public R addChild(
            @CurrentUser BeanUser user,
            BeanUnit unit) {
        boolean wrongParentId = unit.getParentId() == null || unit.getParentId().equals(0);
        boolean uncorrectedParentId = user.getRoleId() == 2 && !user.getUnitId().equals(unit.getParentId());
        if (wrongParentId || uncorrectedParentId) {
            return R.error("父部门ID不正确");
        }
        if (user.getRoleId() == 1 && unitMapper.selectById(unit.getParentId()) == null) { // 不存在的一级部门
            return R.of(R.NOT_FOUND,  "父部门不存在", null);
        }
        if (unitMapper.insert(unit) == 1) {
            return R.ok("添加成功", unit);
        } else {
            return R.error("添加失败");
        }
    }

    @Transactional
    @PreAuthorize("hasAnyRole('1', '2')")
    @PostMapping("/delete/{id}")
    @ApiOperation("删除部门")
    public R modifyPwd(
            @CurrentUser BeanUser user,
            @ApiParam(name = "id", value = "要删除的部门id", required = true, example = "1")
            @PathVariable Integer id) {
        BeanUnit unit = unitMapper.selectById(id);
        if (unit == null) {
            return R.of(R.NOT_FOUND,  "该部门不存在", null);
        }
        int res = 0;
        if (user.getRoleId() == 1) {
            res = unitMapper.deleteById(id);
            if (unit.getParentId() == 0) {  // 如果是一级部门
                int childrenUnitRes = unitMapper.delete(new QueryWrapper<BeanUnit>().lambda().eq(BeanUnit::getParentId, id));
                log.debug("admin user[id: {}] delete unit[id: {}]'s children > result: {}", user.getId(), id, childrenUnitRes);
            }
        }
        if (user.getRoleId() == 2 && unitMapper.selectById(id).getParentId().equals(user.getUnitId())) {    // 属于该用户部门下的子部门
            res = unitMapper.deleteById(id);
        }
        log.debug("user[id: {}] delete unit[id: {}]> result: {}", user.getId(), id, res);
        if (res == 1) {
            int userRes = userMapper.delete(new QueryWrapper<BeanUser>().apply("unit_id not in (select id from t_unit)"));
            log.debug("user[id: {}] delete unit[id: ], then caused users' deleted> result: {}", user.getId(), id, userRes);
            int excludedWordRes = excludedWordMapper.delete(new QueryWrapper<BeanExcludedWord>().apply("unit_id not in (select id from t_unit)"));
            log.debug("user[id: {}] delete unit[id: ], then caused excludedWords' deleted> result: {}", user.getId(), id, excludedWordRes);
            if (unit.getParentId() == 0) {  // 如果是一级部门
                int keywordGroupRes = keywordGroupMapper.delete(new QueryWrapper<BeanKeywordGroup>().apply("unit_id not in (select id from t_unit)"));
                log.debug("user[id: {}] delete unit[id: ], then caused keywordGroups' deleted> result: {}", user.getId(), id, keywordGroupRes);
                int keywordRes = keywordMapper.delete(new QueryWrapper<BeanKeyword>().apply("group_id not in (select id from t_keyword_group)"));
                log.debug("user[id: {}] delete unit[id: ], then caused keywords' deleted> result: {}", user.getId(), id, keywordRes);
            } else {    // 子部门
                int keywordGroupUnitRes = keywordGroupUnitMapper.delete(new QueryWrapper<BeanKeywordGroupUnit>().lambda().eq(BeanKeywordGroupUnit::getUnitId, id));
                log.debug("user[id: {}] delete unit[id: ], then caused keywordGroupUnits' deleted> result: {}", user.getId(), id, keywordGroupUnitRes);
            }
        }
        return res == 1 ? R.ok("删除成功", null) : R.error("删除失败");
    }

    @PreAuthorize("hasAnyRole('1', '2')")
    @PostMapping("/modify")
    @ApiOperation(value = "修改部门信息", response = BeanUnit.class)
    public R modify(@CurrentUser BeanUser user, BeanUnit unit) {
        if (user.getRoleId() == 2) {    // 非系统管理员 不得对分配地区 做修改
            unit.setDivisionStr(null);
        }
        if (user.getRoleId() == 1 || user.getUnitId().equals(unit.getParentId())) {
            int res = unitMapper.updateById(unit);
            return res == 1 ? R.ok("修改成功", unit) : R.error("修改失败");
        } else {
            return R.error("无权修改");
        }
    }

}
