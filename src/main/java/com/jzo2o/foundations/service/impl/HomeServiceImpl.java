package com.jzo2o.foundations.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jzo2o.api.foundations.dto.response.ServeItemResDTO;
import com.jzo2o.common.utils.ObjectUtils;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.enums.FoundationStatusEnum;
import com.jzo2o.foundations.mapper.ServeItemMapper;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.domain.Region;
import com.jzo2o.foundations.model.domain.Serve;
import com.jzo2o.foundations.model.domain.ServeItem;
import com.jzo2o.foundations.model.dto.response.ServeAggregationSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeAggregationTypeSimpleResDTO;
import com.jzo2o.foundations.model.dto.response.ServeCategoryResDTO;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.jzo2o.foundations.service.IServeItemService;
import com.jzo2o.foundations.service.IServeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Gods
 * @Date 2025/6/14 13:06
 * @description 为HomeService的实现类提供默认实现
 */

@Service
@Slf4j
public class HomeServiceImpl implements HomeService {

    @Resource
    private ServeMapper serveMapper;

    @Resource
    private IRegionService regionService;

    @Resource
    private IServeService serveService;

    @Resource
    private ServeItemMapper serveItemMapper;

    @Resource
    private IServeItemService serveItemService;


    @Caching(
            cacheable = {
                    //result为null时,属于缓存穿透情况，缓存时间30分钟
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() != 0", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    //result不为null时,永久缓存
                    @Cacheable(value = RedisConstants.CacheName.SERVE_ICON, key = "#regionId", unless = "#result.size() == 0", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    @Override
    public List<ServeCategoryResDTO> queryServeIconCategoryByRegionIdCache(Long regionId) {
        // 需要对区域信息来判断
        Region region = regionService.getById(regionId);
        if (ObjectUtils.isNull(region) || !region.getActiveStatus().equals(FoundationStatusEnum.ENABLE.getStatus())) {
            return Collections.emptyList();
        }

        // 查询数据库信息
        List<ServeCategoryResDTO> dtoList = serveMapper.queryServeIconCategoryByRegionIdCache(regionId);
        // 对数据进行处理
        int index = dtoList.size() >= 2 ? 2 : dtoList.size();
        // 对数据进行截取
        ArrayList<ServeCategoryResDTO> list = new ArrayList<>(dtoList.subList(0, index));
        // 对里面的服务项进行处理
        list.stream().forEach(item -> {
            List<ServeSimpleResDTO> serveResDTOList = item.getServeResDTOList();
            int index2 = serveResDTOList.size() >= 4 ? 4 : serveResDTOList.size();
            ArrayList<ServeSimpleResDTO> serveSimpleResDTOS = new ArrayList<>(serveResDTOList.subList(0, index2));
            item.setServeResDTOList(serveSimpleResDTOS);
        });
        return list;
    }

    /**
     * 查询服务类型列表
     * @param regionId 区域id
     * @return 服务类型列表
     */
    @Cacheable(value = RedisConstants.CacheName.SERVE_TYPE, key = "#regionId", cacheManager = RedisConstants.CacheManager.FOREVER)
    @Override
    public List<ServeAggregationTypeSimpleResDTO> serveTypeList(Long regionId) {
        // 需要对区域信息来判断
        Region region = regionService.getById(regionId);
        if (ObjectUtils.isNull(region) || !region.getActiveStatus().equals(FoundationStatusEnum.ENABLE.getStatus())) {
            return Collections.emptyList();
        }

        // 查询数据库信息
        List<ServeCategoryResDTO> dtoList = serveMapper.queryServeIconCategoryByRegionIdCache(regionId);

        // 对数据进行处理,映射为DTO
        List<ServeAggregationTypeSimpleResDTO> collect = dtoList.stream().map(item -> {
            ServeAggregationTypeSimpleResDTO serveAggregationTypeSimpleResDTO = ServeAggregationTypeSimpleResDTO.builder()
                    .serveTypeId(item.getServeTypeId())
                    .serveTypeName(item.getServeTypeName())
                    .serveTypeImg(item.getServeTypeIcon())
                    .serveTypeSortNum(item.getServeTypeSortNum())
                    .build();

            return serveAggregationTypeSimpleResDTO;
        }).collect(Collectors.toList());


        return collect;
    }

    /**
     * 查询热门服务列表
     * @param regionId 区域id
     * @return 热门服务列表
     */
    @Caching(
            cacheable = {
                    @Cacheable(value = RedisConstants.CacheName.HOT_SERVE, key = "#regionId", unless = "#result.size() != 0", cacheManager = RedisConstants.CacheManager.THIRTY_MINUTES),
                    @Cacheable(value = RedisConstants.CacheName.HOT_SERVE, key = "#regionId", unless = "#result.size() == 0", cacheManager = RedisConstants.CacheManager.FOREVER)
            }
    )
    @Override
    public List<ServeAggregationSimpleResDTO> hotServeList(Long regionId) {
        LambdaQueryWrapper<Serve> queryWrapper = new LambdaQueryWrapper<Serve>()
                .eq(Serve::getRegionId, regionId)
                .eq(Serve::getIsHot, 1);

        List<Serve> serves = serveMapper.selectList(queryWrapper);
        if (ObjectUtils.isNull(serves)){
            // 为空时，直接返回空
            return Collections.emptyList();
        }

        //封装返回数据
        List<ServeAggregationSimpleResDTO> collect = serves.stream().map(item -> {
            ServeAggregationSimpleResDTO resDTO = new ServeAggregationSimpleResDTO();
            // 根据服务项id查询服务项信息
            ServeItem serveItem = serveItemMapper.selectById(item.getServeItemId());
            resDTO.setId(item.getId());
            resDTO.setServeItemId(serveItem.getId());
            resDTO.setServeItemName(serveItem.getName());
            resDTO.setServeItemImg(serveItem.getImg());
            resDTO.setPrice(item.getPrice());
            resDTO.setDetailImg(serveItem.getDetailImg());
            resDTO.setCityCode(item.getCityCode());
            return resDTO;
        }).collect(Collectors.toList());

        return collect;

    }

    /**
     * 根据id查询服务
     * @param id 服务id
     * @return 服务详情
     */
    @Override
    public ServeAggregationSimpleResDTO queryServeById(Long id) {
        Serve serve = serveService.queryServeByIdCache(id);
        if (ObjectUtils.isNull(serve)){
            return null;
        }
        // 获取服务项id
        Long serveItemId = serve.getServeItemId();
        ServeItemResDTO serveItemResDTO = serveItemService.queryServeItemAndTypeById(serveItemId);
        if (ObjectUtils.isNull(serveItemResDTO)){
            return null;
        }

        ServeAggregationSimpleResDTO serveAggregationSimpleResDTO = ServeAggregationSimpleResDTO.builder()
                .id(serve.getId())
                .serveItemId(serveItemResDTO.getId())
                .serveItemName(serveItemResDTO.getName())
                .serveItemImg(serveItemResDTO.getImg())
                .price(serve.getPrice())
                .detailImg(serveItemResDTO.getDetailImg())
                .cityCode(serve.getCityCode())
                .unit(serveItemResDTO.getUnit())
                .build();


        return serveAggregationSimpleResDTO;
    }
}
