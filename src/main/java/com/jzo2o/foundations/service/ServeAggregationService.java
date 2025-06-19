package com.jzo2o.foundations.service;

import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;

import java.util.List;

/**
 * @Author Gods
 * @Date 2025/6/17 15:33
 * @description 为es添加服务聚合数据
 */
public interface ServeAggregationService {

    /**
     * 查询服务列表
     *
     * @param cityCode    城市编码
     * @param serveTypeId 服务类型id
     * @param keyword     关键词
     * @return 服务列表
     */
    List<ServeSimpleResDTO> findServeList(String cityCode, Long serveTypeId, String keyword);

}
