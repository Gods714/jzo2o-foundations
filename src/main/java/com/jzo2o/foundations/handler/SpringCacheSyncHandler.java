package com.jzo2o.foundations.handler;

import com.jzo2o.api.foundations.dto.response.RegionSimpleResDTO;
import com.jzo2o.foundations.constants.RedisConstants;
import com.jzo2o.foundations.service.HomeService;
import com.jzo2o.foundations.service.IRegionService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Gods
 * @Date 2025/6/13 20:48
 * @description 为xxl-job创建调度器
 */

@Component
@Slf4j
public class SpringCacheSyncHandler {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IRegionService regionService;

    @Resource
    private HomeService homeService;

    @XxlJob("activeRegionCacheSync")
    public void activeRegionCacheSync() {
        log.info("开始同步缓存");
        // 删除缓存数据
        String key = RedisConstants.CacheName.JZ_CACHE + "::ACTIVE_REGIONS";
        Boolean delete = redisTemplate.delete(key);
        if (delete) {
            log.info("删除缓存成功");
        }
        // 缓存启用区域的数据
        List<RegionSimpleResDTO> regionSimpleResDTOS = regionService.queryActiveRegionListCache();

        // 缓存启用区域的服务类型和服务项
        regionSimpleResDTOS.forEach(regionSimpleResDTO -> {
            // 先删除
            String key1 = RedisConstants.CacheName.SERVE_ICON + "::" + regionSimpleResDTO.getId();
            redisTemplate.delete(key1);

            // 在缓存
            homeService.queryServeIconCategoryByRegionIdCache(regionSimpleResDTO.getId());

            // 缓存区域的服务项信息
            // 先删除
            String key2 = RedisConstants.CacheName.SERVE_TYPE + "::" + regionSimpleResDTO.getId();
            redisTemplate.delete(key2);

            // 在缓存
            homeService.serveTypeList(regionSimpleResDTO.getId());


            // 缓存区域的热门信息
            // 先删除
            String key3 = RedisConstants.CacheName.HOT_SERVE + "::" + regionSimpleResDTO.getId();
            redisTemplate.delete(key3);

            // 在缓存
            homeService.hotServeList(regionSimpleResDTO.getId());

        });



    }


}
