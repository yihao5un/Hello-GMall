package com.matrix.gmall.ware.mapper;

import com.matrix.gmall.ware.bean.WareInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @param
 * @return
 */
@Repository
public interface WareInfoMapper extends BaseMapper<WareInfo> {


    public List<WareInfo> selectWareInfoBySkuid(String skuid);



}
