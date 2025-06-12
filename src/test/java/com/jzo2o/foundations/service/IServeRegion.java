package com.jzo2o.foundations.service;

import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.jzo2o.foundations.mapper.ServeMapper;
import com.jzo2o.foundations.model.dto.response.ServeResDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Gods
 * @Date 2025/6/6 12:36
 * @description 为区域服务测试类
 */

@SpringBootTest
@Slf4j
public class IServeRegion {


    @Resource
    private ServeMapper  serveMapper;

    @Test
    void test_queryServeListByRegionId(){
        List<ServeResDTO> serveResDTOS = serveMapper.queryServeListByRegionId(1686303222843662337L);
        Assert.notEmpty(serveResDTOS, "区域服务列表为空");

    }

}
