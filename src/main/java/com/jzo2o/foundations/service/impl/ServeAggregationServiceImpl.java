package com.jzo2o.foundations.service.impl;

import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldAndFormat;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.es.core.ElasticSearchTemplate;
import com.jzo2o.es.utils.SearchResponseUtils;
import com.jzo2o.foundations.model.domain.ServeAggregation;
import com.jzo2o.foundations.model.dto.response.ServeSimpleResDTO;
import com.jzo2o.foundations.service.ServeAggregationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author Gods
 * @Date 2025/6/17 15:34
 * @description 为ServeAggregationService实现类
 */

@Service
@Slf4j
public class ServeAggregationServiceImpl implements ServeAggregationService {

    @Resource
    private ElasticSearchTemplate elasticSearchTemplate;


    /**
     * 查询服务列表
     *
     * @param cityCode    城市编码
     * @param serveTypeId 服务类型id
     * @param keyword     关键词
     * @return 服务列表
     */
    @Override
    public List<ServeSimpleResDTO> findServeList(String cityCode, Long serveTypeId, String keyword) {
        // 为初始化查询条件
        SearchRequest.Builder builder = new SearchRequest.Builder();

        // 构建查询条件
        builder.query(query ->
                query.bool(bool -> {
                    bool.must(must ->
                            must.term(term ->
                                    term.field("city_code").value(cityCode)));


                    if (serveTypeId != null) {
                        bool.must(must ->
                                must.term(term ->
                                        term.field("serve_type_id").value(serveTypeId)));
                    }
                    if (keyword != null) {
                        bool.must(must ->
                                must.multiMatch(multiMatch ->
                                        multiMatch.query(keyword).fields("serve_item_name", "serve_type_name")));
                    }
                    return bool;
                }));

        // 指定索引
        builder.index("serve_aggregation");
        //排序
        List<SortOptions> sortOptions = new ArrayList<>();
        SortOptions serveItemSortNum = SortOptions.of(sortOption -> sortOption.field(field -> field.field("serve_item_sort_num").order(SortOrder.Asc)));
        sortOptions.add(serveItemSortNum);

        builder.sort(sortOptions);

        // 查询es信息
        SearchResponse<ServeAggregation> search = elasticSearchTemplate.opsForDoc().search(builder.build(), ServeAggregation.class);
        // 构建返回结果
        if (SearchResponseUtils.isSuccess(search)) {
            // 为查询成功
            List<ServeAggregation> collect = search.hits().hits().stream().map(Hit::source).collect(Collectors.toList());

            // 构建返回结果
            List<ServeSimpleResDTO> serveSimpleResDTOS = BeanUtils.copyToList(collect, ServeSimpleResDTO.class);
            return serveSimpleResDTOS;
        }

        //返回null
        return Collections.emptyList();
    }
}
