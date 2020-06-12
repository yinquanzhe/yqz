package net.ahwater.tender.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.tender.db.entity.BeanDivision;
import net.ahwater.tender.db.entity.BeanUnit;
import net.ahwater.tender.db.entity.BeanUser;
import net.ahwater.tender.db.entity.R;
import net.ahwater.tender.db.mapper.DivisionMapper;
import net.ahwater.tender.db.mapper.UnitMapper;
import net.ahwater.tender.web.annotation.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yyc on 2019/3/12 17.12.
 * Nothing is true but improving yourself.
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Api(tags = "行政区划控制类")
@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/division")
public class DivisionController {

    @Autowired
    private DivisionMapper divisionMapper;

    @Autowired
    private UnitMapper unitMapper;

//    @PreAuthorize("hasRole('1')")
//    @GetMapping("/listFullTree")
//    @ApiOperation(value = "管理员查看所有的行政区划树", response = BeanUnit.class)
//    public R listFullTree() {
//        List<BeanDivision> divisions = divisionMapper.selectList(null);
//        List<BeanDivision> tree = divisions.stream()
//                .filter(e -> e.getCode().endsWith("0000"))
//                .collect(Collectors.toList());
//        List<BeanDivision> subs = divisions.stream()
//                .filter(e -> !e.getCode().endsWith("0000"))
//                .collect(Collectors.toList());
//        for (BeanDivision node : tree) {
//            for (BeanDivision sub : subs) {
//                if (sub.getCode().startsWith(node.getCode().substring(0, 2))) {
//                    node.getSubs().add(sub);
//                }
//            }
//        }
//        return R.ok(tree);
//    }

    @GetMapping("/listTree")
    @ApiOperation(value = "查看行踪区划树", response = BeanDivision.class)
    public R listTree(@CurrentUser BeanUser user) {
        List<BeanDivision> divisions = divisionMapper.selectList(null);
        if (user.getUnitId() != 0) {    // 企业用户
            String divisionStr;
            BeanUnit unit = unitMapper.selectById(user.getUnitId());
            if (unit.getParentId() == 0) {
                divisionStr = unit.getDivisionStr();
            } else {
                divisionStr = unitMapper.selectById(unit.getParentId()).getDivisionStr();
            }
            List<String> divisionList = Arrays.stream(divisionStr.split(","))
                    .collect(Collectors.toList());
            divisions = divisions.stream()
                    .filter(e -> divisionList.stream().anyMatch(d -> e.getCode().startsWith(d)))
                    .collect(Collectors.toList());
        }
        List<BeanDivision> tree = divisions.stream()
                .filter(e -> e.getCode().endsWith("0000"))
                .collect(Collectors.toList());
        if (tree.size() > 0) {
            List<BeanDivision> subs = divisions.stream()
                    .filter(e -> !e.getCode().endsWith("0000"))
                    .collect(Collectors.toList());
            for (BeanDivision node : tree) {
                for (BeanDivision sub : subs) {
                    if (sub.getCode().startsWith(node.getCode().substring(0, 2))) {
                        node.getSubs().add(sub);
                    }
                }
            }
            return R.ok(tree);
        } else {
            return R.ok(divisions);
        }
    }

}
