package com.jzo2o.foundations.service;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务表 服务类
 * </p>
 *
 * @author itcast
 * @since 2023-12-06
 */
public interface IServeService extends IService<Serve> {

    /**
     * 区域服务分页查询
     * @param servePageQueryReqDTO
     * @return
     */
    PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO);

    /**
     * 批量新增区域服务服务项
     * @param reqDTOList 为添加参数
     */
    void batchAdd(List<ServeUpsertReqDTO> reqDTOList);

    /**
     * 服务价格修改
     *
     * @param id    服务id
     * @param price 价格
     * @return 服务
     */
    Serve update(Long id, BigDecimal price);


    /**
     * 上架
     *
     * @param id         服务id
     */
    Serve onSale(Long id);

    /**
     * 删除
     *
     * @param id 服务id
     */
    void delete(Long id);

    /**
     * 下架
     * @param id 服务id
     */
    void offSale(Long id);

    /**
     * 设为热门
     * @param id 服务id
     */
    void onHot(Long id);

    /**
     * 取消设为热门
     * @param id 服务id
     */
    void offHot(Long id);
}
