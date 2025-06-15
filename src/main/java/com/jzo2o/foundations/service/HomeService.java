package com.jzo2o.foundations.service;

import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;

import java.util.List;

/**
 * @Author Gods
 * @Date 2025/6/14 13:06
 * @description 为门户服务提供接口
 */


public interface HomeService {

    /**
     * 根据区域id获取服务图标信息
     *
     * @param regionId 区域id
     * @return 服务图标列表
     */
    List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId);

    /**
     * 根据区域id获取服务类型列表
     *
     * @param regionId 区域id
     * @return 服务类型列表
     */
    List<ServeAggregationTypeSimpleResDTO> serveTypeList(Long regionId);

    /**
     * 根据区域id获取热门服务列表
     *
     * @param regionId 区域id
     * @return 热门服务列表
     */
    List<ServeAggregationSimpleResDTO> hotServeList(Long regionId);

    /**
     * 根据服务id查询服务
     *
     * @param id 服务id
     * @return 服务信息
     */
    ServeAggregationSimpleResDTO queryServeById(Long id);
}
