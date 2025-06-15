package com.jzo2o.foundations.controller.customer;

import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.service.HomeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Gods
 * @Date 2025/6/14 14:33
 * @description 为用户端提供服务查询接口
 */

@RestController
@RequestMapping("/customer/serve")
@Api(tags = "用户端 - 服务查询接口")
public class ServeCustomerController {

    @Resource
    private HomeService homeService;

    /**
     * 查询服务类型列表
     *
     * @param regionId 区域id
     * @return 服务类型列表
     */
    @GetMapping("/serveTypeList")
    @ApiOperation("查询服务类型列表")
    public List<ServeAggregationTypeSimpleResDTO> serveTypeList(@RequestParam("regionId") Long regionId) {
        return homeService.serveTypeList(regionId);
    }

    /**
     * 根据id查询服务
     *
     * @param id server的主键id
     * @return 服务图标列表
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询服务")
    public ServeAggregationSimpleResDTO queryServeById(@PathVariable("id") Long id) {
        return homeService.queryServeById(id);
    }

}
