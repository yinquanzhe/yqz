package net.ahwater.zjk.service;

import net.ahwater.zjk.entity.dto.ExpertDTO;
import net.ahwater.zjk.entity.vo.R;

public interface ExpertService{
    R listAll();

    R add(ExpertDTO dto);

    R modify(ExpertDTO dto);

    R remove(Integer id);

    R listById(Integer id);
}
