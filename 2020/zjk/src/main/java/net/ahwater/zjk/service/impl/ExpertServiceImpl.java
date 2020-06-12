package net.ahwater.zjk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import net.ahwater.zjk.dao.ExpertDao;
import net.ahwater.zjk.entity.dto.ExpertDTO;
import net.ahwater.zjk.entity.po.ExpertPO;
import net.ahwater.zjk.entity.vo.R;
import net.ahwater.zjk.service.ExpertService;
import net.ahwater.zjk.utils.DozerUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by yqz on 2020/5/27
 */
@SuppressWarnings("all")
@Slf4j
@Service
public class ExpertServiceImpl extends ServiceImpl<ExpertDao, ExpertPO> implements ExpertService{

    @Autowired
    private DozerUtil dozerUtil;

    @Override
    public R listAll() {
        return R.ok("查询成功",list(null));
    }

    @Override
    public R add(ExpertDTO dto) {
        return save(dozerUtil.convert(dto,ExpertPO.class)) ? R.ok("新增成功") : R.error("新增失败");
    }

    @Override
    public R modify(ExpertDTO dto) {
        return updateById(dozerUtil.convert(dto,ExpertPO.class)) ? R.ok("修改成功") : R.error("修改失败");
    }

    @Override
    public R remove(Integer id) {
        if(id == null){
            return R.error("参数id不存在");
        }
        return removeById(id) ? R.ok("删除成功") : R.error("删除失败");
    }

    @Override
    public R listById(Integer id) {
        return R.ok("查询成功",baseMapper.selectById(id));
    }

}