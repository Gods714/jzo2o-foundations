package com.jzo2o.foundations.controller.operation;

import com.jzo2o.common.model.PageResult;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Author Gods
 * @Date 2025/6/6 11:44
 * @description 为区域服务表现层
 */

@RestController("operationServeController")
@RequestMapping("/operation/serve/")
@Api(tags = "运营端 - 区域服务相关接口")
public class ServeController {

    @Resource
    private IServeService serveService;


    /**
     * 区域服务分页查询
     *
     * @param servePageQueryReqDTO 查询条件
     * @return 分页统一结果
     */
    //GET/foundations/operation/serve/page
    @GetMapping("page")
    @ApiOperation("区域服务分页查询")
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
        return serveService.page(servePageQueryReqDTO);
    }

    /**
     * 批量新增区域服务服务项
     *
     * @param reqDTOList 新增服务项
     */
    // POST/foundations/operation/serve/batch
    @PostMapping("batch")
    @ApiOperation("批量新增区域服务服务项")
    public void add(@RequestBody List<ServeUpsertReqDTO> reqDTOList) {
        serveService.batchAdd(reqDTOList);
    }


    /**
     * 区域服务服务项修改价格
     *
     * @param id    服务id server的主键
     * @param price 修改的价格
     */
    //PUT/foundations/operation/serve/{id}
    @PutMapping("{id}")
    @ApiOperation("区域服务服务项修改价格")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
            @ApiImplicitParam(name = "price", value = "价格", required = true, dataTypeClass = BigDecimal.class)
    })
    public void update(@PathVariable("id") Long id, @RequestParam("price") BigDecimal price) {
        Serve serve = serveService.update(id, price);

    }


    @PutMapping("/onSale/{id}")
    @ApiOperation("区域服务上架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void onSale(@PathVariable("id") Long id) {
        Serve serve = serveService.onSale(id);
    }

    /**
     *  区域服务删除
     * @param id 为server表的主键
     */
    @DeleteMapping("/{id}")
    @ApiOperation("区域服务删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void delete(@PathVariable("id") Long id) {
        serveService.delete(id);
    }


    /**
     * 区域服务下架
     * @param id 为server表的主键
     */
    @PutMapping("/offSale/{id}")
    @ApiOperation("区域服务下架")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void offSale(@PathVariable("id") Long id) {
        serveService.offSale(id);
    }

    /**
     * 区域服务设为热门
     * @param id 为server表的主键
     */
    @PutMapping("/onHot/{id}")
    @ApiOperation("区域服务设为热门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void onHot(@PathVariable("id") Long id) {
        serveService.onHot(id);
    }


    /**
     * 区域服务取消热门
     * @param id 为server表的主键
     */
    @PutMapping("/offHot/{id}")
    @ApiOperation("区域服务取消热门")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "服务id", required = true, dataTypeClass = Long.class),
    })
    public void offHot(@PathVariable("id") Long id) {
        serveService.offHot(id);
    }




}
