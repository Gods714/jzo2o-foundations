package com.jzo2o.foundations.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.RegionMapper;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.request.ServePageQueryReqDTO;
import com.jzo2o.foundations.model.dto.request.ServeUpsertReqDTO;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import com.jzo2o.foundations.service.IServeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.mysql.utils.PageHelperUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 服务表 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-12-06
 */
@Service
public class ServeServiceImpl extends ServiceImpl<ServeMapper, Serve> implements IServeService {

    @Resource
    private ServeItemMapper serveItemMapper;

    @Resource
    private RegionMapper regionMapper;

    @Override
    public PageResult<ServeResDTO> page(ServePageQueryReqDTO servePageQueryReqDTO) {
        PageResult<ServeResDTO> serveResDTOPageResult = PageHelperUtils.selectPage(servePageQueryReqDTO,
                () -> baseMapper.queryServeListByRegionId(servePageQueryReqDTO.getRegionId()));


        return serveResDTOPageResult;
    }

    /**
     * 批量新增区域服务服务项
     *
     * @param reqDTOList 为添加参数
     */
    @Override
    public void batchAdd(List<ServeUpsertReqDTO> reqDTOList) {

        if (reqDTOList == null || reqDTOList.size() == 0) {
            throw new ForbiddenOperationException();
        }
        for (ServeUpsertReqDTO serveUpsertReqDTO : reqDTOList) {
            // 需要来进行合法性的校验
            // 判断服务项是否存在,和状态是否启用
            ServeItem serveItem = serveItemMapper.selectById(serveUpsertReqDTO.getServeItemId());
            if (serveItem == null || serveItem.getActiveStatus() != FoundationStatusEnum.ENABLE.getStatus()) {
                throw new ForbiddenOperationException();
            }

            // 判断一个区域下是否有相同的服务项
            Integer count = lambdaQuery()
                    .eq(Serve::getServeItemId, serveUpsertReqDTO.getServeItemId())
                    .eq(Serve::getRegionId, serveUpsertReqDTO.getRegionId())
                    .count();
            if (count > 0) {
                throw new ForbiddenOperationException();
            }

            // 实现添加
            // 需要构建添加的对象
            Serve serve = BeanUtils.toBean(serveUpsertReqDTO, Serve.class);
            // 查询添加数据
            // 查询城市的编码
            Region region = regionMapper.selectById(serveUpsertReqDTO.getRegionId());
            serve.setCityCode(region.getCityCode());
            int insert = baseMapper.insert(serve);
            if (insert <= 0) {
                throw new ForbiddenOperationException("添加数据失败");
            }

        }


    }

    /**
     * 修改服务价格
     *
     * @param id    服务id
     * @param price 价格
     * @return 修改后的服务对象
     */
    @Override
    public Serve update(Long id, BigDecimal price) {
        // 判断价格是否合法
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CommonException("价格不合法");
        }

        // 判断服务是否存在
        Serve serve = getById(id);
        if (serve == null) {
            throw new CommonException("服务不存在");
        }
        // 修改
        boolean update = lambdaUpdate().
                eq(Serve::getId, id).
                set(Serve::getPrice, price).
                update();
        if (update) {
            return getById(id);
        }

        return null;
    }

    /**
     * 上架服务
     *
     * @param id 服务id
     * @return 上架后的服务对象
     */
    @Override
    public Serve onSale(Long id) {
        // 需要进行条件判断
        // 判断当前区域服务的状态
        Serve serve = getById(id);
        if (serve == null) {
            throw new CommonException("服务不存在");
        }
        if (!(serve.getSaleStatus() == FoundationStatusEnum.INIT.getStatus() || serve.getSaleStatus() == FoundationStatusEnum.DISABLE.getStatus())) {
            throw new ForbiddenOperationException("区域服务状态不支持上架");
        }

        // 判断当前服务项的状态
        ServeItem serveItem = serveItemMapper.selectById(serve.getServeItemId());
        if (serveItem == null || serveItem.getActiveStatus() == FoundationStatusEnum.DISABLE.getStatus()) {
            throw new ForbiddenOperationException("服务项状态不支持上架");
        }

        // 执行修改
        boolean update = lambdaUpdate().
                eq(Serve::getId, id).
                set(Serve::getSaleStatus, FoundationStatusEnum.ENABLE.getStatus()).
                update();

        if (update) {
            return getById(id);
        }
        return null;
    }

    /**
     * 删除服务
     * @param id 服务id
     */
    @Override
    public void delete(Long id) {
        Serve serve = getById(id);
        if (serve == null) {
            throw new CommonException("服务不存在");
        }

        // 获取里面服务项的id
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);

        // 判断服务项状态
        if (serveItem == null || serveItem.getActiveStatus() != FoundationStatusEnum.INIT.getStatus()) {
            throw new ForbiddenOperationException("服务项状态不支持删除");
        }

        // 删除
        int deleteById = serveItemMapper.deleteById(serveItemId);
        if (deleteById <= 0) {
            throw new ForbiddenOperationException("删除失败");
        }
    }

    /**
     * 下架服务
     * @param id 服务id
     */
    @Override
    public void offSale(Long id) {
        //信息的校验
        Serve serve = getById(id);
        if (serve == null){
            throw new CommonException("服务不存在");
        }

        // 只有上架的服务才能下架
        if (serve.getSaleStatus() != FoundationStatusEnum.ENABLE.getStatus()){
            throw new ForbiddenOperationException("服务状态不支持下架");
        }

        // 获取服务项id
        Long serveItemId = serve.getServeItemId();
        ServeItem serveItem = serveItemMapper.selectById(serveItemId);
        if (serveItem == null || serveItem.getActiveStatus() == FoundationStatusEnum.DISABLE.getStatus()){
            throw new ForbiddenOperationException("服务项状态不支持下架");
        }

        // 下架
        boolean update = lambdaUpdate().eq(Serve::getId, id).set(Serve::getSaleStatus, FoundationStatusEnum.DISABLE.getStatus()).update();
        if (!update){
            // 修改失败
            throw new ForbiddenOperationException("服务状态不支持下架");
        }

    }

    /**
     * 添加服务项为热门服务
     * @param id 服务id
     */
    @Override
    public void onHot(Long id) {
        Serve serve = getById(id);
        if (serve == null){
            throw new CommonException("服务不存在");
        }
        // 只有不是热门的服务才能添加为热门服务
        if (serve.getIsHot() == 1){
            throw new ForbiddenOperationException("服务已经是热门服务");
        }

        // 设置热门
        boolean update = lambdaUpdate().eq(Serve::getId, id).set(Serve::getIsHot, 1).update();
        if (!update){
            // 修改失败
            throw new ForbiddenOperationException("服务状态不支持添加为热门服务");
        }

    }

    /**
     * 删除服务项为热门服务
     * @param id 服务id
     */
    @Override
    public void offHot(Long id) {
        Serve serve = getById(id);
        if (serve == null){
            throw new CommonException("服务不存在");
        }
        // 只有不是热门的服务才能添加为热门服务
        if (serve.getIsHot() == 0){
            throw new ForbiddenOperationException("服务已经是热门服务");
        }

        // 设置热门
        boolean update = lambdaUpdate().eq(Serve::getId, id).set(Serve::getIsHot, 0).update();
        if (!update){
            // 修改失败
            throw new ForbiddenOperationException("服务状态不支持添加为热门服务");
        }

    }
}
