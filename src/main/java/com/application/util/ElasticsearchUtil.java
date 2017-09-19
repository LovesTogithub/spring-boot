package com.application.util;

import com.application.util.*;
import com.application.util.CheckBlankUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by acer_liuyutong on 2017/5/23.
 * Elasticsearch版本2.4.5
 */
public class ElasticsearchUtil {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchUtil.class);

    /**
     * 利用Spring的restTemplate进行REST资源交互
     */
    private static RestTemplate restTemplate;

    /**
     * 请求头信息
     */
    private static HttpHeaders headers;

    static {
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(Integer.parseInt(ConfigUtil.getConfig("httpClientFactory.connectTimeout")));
        clientHttpRequestFactory.setReadTimeout(Integer.parseInt(ConfigUtil.getConfig("httpClientFactory.readTimeout")));

        // 添加转换器
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
        messageConverters.add(new FormHttpMessageConverter());

        restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.setMessageConverters(messageConverters);

        //请求头
        headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
    }

    //getter和setter方法
    public static RestTemplate getRestTemplate() {
        return restTemplate;
    }

    public static void setRestTemplate(RestTemplate restTemplate) {
        ElasticsearchUtil.restTemplate = restTemplate;
    }

    public static HttpHeaders getHeaders() {
        return headers;
    }

    public static void setHeaders(HttpHeaders headers) {
        ElasticsearchUtil.headers = headers;
    }

    /**
     * 把实体转换成Http传送的报文
     *
     * @param body 实体类对象
     */
    private static HttpEntity<String> generateHttpEntity(Object body) {
        if (body == null) return null;
        return new HttpEntity<>(JSONObject.toJSONString(body), headers);
    }

    /**
     * 把String转换成Http传送的报文
     *
     * @param body 实体类对象
     */
    private static HttpEntity<String> generateHttpEntity(String body) {
        if (StringUtils.isBlank(body)) return null;
        return new HttpEntity<>(body, headers);
    }

    /**
     * 创建索引
     *
     * @param index 索引名称
     */
    public static void save(String index) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isBlank(index)) {
            return;
        }
        url += "/" + index;
        restTemplate.put(url, null);
    }

    /**
     * 创建索引和类型
     *
     * @param index 索引名称
     * @param type  类型名称
     */
    public static void save(String index, String type) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type)) {
            return;
        }
        if (!isExists(index)) {
            return;
        }
        url += "/" + index + "/_mapping/" + type;
        String json = "{\"properties\" : {}}";
        generateHttpEntity(json);
        restTemplate.put(url, null);
    }

    /**
     * 创建索引、类型、文档--只支持插入一条数据
     *
     * @param index 索引名称
     * @param type  类型名称
     * @param body  文档实体对象
     * @param id    指定文档存入的Id，如果不指定Id，ES则自动生成Id
     */
    public static void save(String index, String type, String id, Object body) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type, id)) {
            return;
        }
        if (body == null) {
            //文档实体对象不能为空
            return;
        }
        url += "/" + index + "/" + type;

        //id不为空，用PUT请求
        url += "/" + id;
        restTemplate.put(url, generateHttpEntity(body));
    }

    /**
     * 创建索引、类型、文档--只支持插入一条数据
     *
     * @param index 索引名称
     * @param type  类型名称
     * @param body  文档实体对象
     *              如果不指定Id，ES则自动生成Id
     */
    public static void save(String index, String type, Object body) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type)) {
            return;
        }
        if (body == null) {
            //文档实体对象不能为空
            return;
        }
        url += "/" + index + "/" + type;

        //id为空，用POST请求
        restTemplate.postForObject(url, generateHttpEntity(body), String.class);
    }

    /**
     * 根据ID更新文档--完全更新
     *
     * @param index 索引名称
     * @param type  类型名称
     * @param body  文档实体对象
     */
    public static void update(String index, String type, String id, Object body) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type, id)) {
            return;
        }
        if (body == null) {
            //文档实体对象不能为空
            return;
        }
        url += "/" + index + "/" + type + "/" + id;


        restTemplate.postForObject(url, generateHttpEntity(body), String.class);
    }

    /**
     * 根据ID更新文档--可选择性的更新
     *
     * @param index   索引名称
     * @param type    类型名称
     * @param body    文档实体对象
     * @param partial 是否局部更新
     */
    public static void update(String index, String type, String id, Object body, boolean partial) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type, id)) {
            return;
        }
        if (body == null) {
            //文档实体对象不能为空
            return;
        }
        url += "/" + index + "/" + type + "/" + id;
        HttpEntity<String> formEntity;

        if (partial) {
            url += "/_update";
            formEntity = generateHttpEntity(createPartialUpdateJson(TransformUtil.object2Map(body)));
        } else {
            formEntity = generateHttpEntity(body);
        }

        restTemplate.postForObject(url, formEntity, String.class);
    }

    /**
     * 根据ID更新文档--局部更新
     *
     * @param index 索引名称
     * @param type  类型名称
     * @param body  需要更新的kv集合
     */
    public static void update(String index, String type, String id, Map<String, Object> body) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type, id)) {
            return;
        }
        if (body == null || body.isEmpty()) {
            //文档实体对象不能为空
            return;
        }

        url += "/" + index + "/" + type + "/" + id + "/_update";
        logger.debug(url);

        restTemplate.postForObject(url, generateHttpEntity(createPartialUpdateJson(body)), String.class);
    }

    /**
     * 删除索引
     *
     * @param index 索引
     */
    public static void delete(String index) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isBlank(index) || !isExists(index)) {
            return;
        }
        url += "/" + index;
        restTemplate.delete(url);
    }

    /**
     * 删除索引、类型、文档
     *
     * @param index 索引
     * @param type  类型
     * @param id    文档Id
     */
    public static void delete(String index, String type, String id) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type, id)) {
            return;
        }
        url += "/" + index + "/" + type + "/" + id;
        restTemplate.delete(url);
    }

    /**
     * 使用bulk机制批量插入
     *
     * @param index  索引
     * @param type   类型
     * @param bodies 要插入的数据
     * @return 执行结果
     */
    public static String bulkIndex(String index, String type, List<Map<String, Object>> bodies) {
        String url = ConfigUtil.getConfig("ES.url");
        HttpEntity<String> formEntity = generateHttpEntity(createBulkIndexJson(bodies));

        if (formEntity == null || formEntity.getBody() == null) {
            return null;
        }

        url += "/" + index + "/" + type + "/_bulk";
        logger.debug("url: {}", url);

        return restTemplate.postForObject(url, formEntity, String.class);
    }

    /**
     * 检查索引是否存在
     *
     * @param index 索引
     * @return 是否存在
     */
    public static boolean isExists(String index) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isBlank(index)) {
            return false;
        }
        url += "/" + index;
        try {
            restTemplate.headForHeaders(url);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 检查索引、类型是否存在
     *
     * @param index 索引
     * @param type  类型
     * @return 是否存在
     */
    public static boolean isExists(String index, String type) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type)) {
            return false;
        }
        url += "/" + index + "/" + type;
        try {
            restTemplate.headForHeaders(url);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static List<String> getMapping(String index, String type) {
        String url = ConfigUtil.getConfig("ES.url") + "/" + index + "/" + type + "/_mapping";

        String queryResult = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(queryResult);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return Collections.emptyList();
        }
        JSONObject indexJson = jsonObject.getJSONObject(index);
        JSONObject mappings = indexJson.getJSONObject("mappings");
        JSONObject typeJson = mappings.getJSONObject(type);
        JSONObject properties = typeJson.getJSONObject("properties");
        ArrayList<String> fields = new ArrayList<>(properties.keySet());
        Collections.sort(fields);

        return fields;
    }

    /**
     * 检查索引、类型、文档是否存在
     *
     * @param index 索引
     * @param type  类型
     * @param id    文档Id
     * @return 是否存在
     */
    public static boolean isExists(String index, String type, String id) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type, id)) {
            return false;
        }
        url += "/" + index + "/" + type + "/" + id;
        try {
            restTemplate.headForHeaders(url);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 简单查询，搜索索引下的全部字段
     *
     * @param index   索引
     * @param type    类型
     * @param keyWord 搜索关键词
     * @param from    分页起始
     * @param size    条数
     * @return 结果
     */
    public static String queryString(String index, String type, String keyWord, int from, int size) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type, keyWord)) {
            return null;
        }
        url += "/" + index + "/" + type + "/_search?q=\"" + keyWord + "\"&from=" + from + "&size=" + size;
        logger.debug(url);
        return restTemplate.getForObject(url, String.class);
    }

    /**
     * 简单查询，搜索索引下的全部字段
     *
     * @param index   索引
     * @param type    类型
     * @param keyWord 搜索关键词
     * @param from    分页起始
     * @param size    条数
     * @return 结果
     */
    public static String queryStringWithPost(String index, String type, String keyWord, int from, int size) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type, keyWord)) {
            return null;
        }
        url += "/" + index + "/" + type + "/_search";
        logger.debug(url);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.queryStringQuery("\"" + keyWord + "\"").phraseSlop(0));
        SearchSourceBuilder query = searchSourceBuilder.query(boolQueryBuilder).from(from).size(size);

        HttpEntity<String> formEntity = generateHttpEntity(query.toString());
        return restTemplate.postForObject(url, formEntity, String.class);
    }

    /**
     * 简单查询，搜索索引下的全部字段
     *
     * @param index    索引
     * @param type     类型
     * @param keyWords 搜索关键词
     * @param from     分页起始
     * @param size     条数
     * @return 结果
     */
    public static String queryStringWithPost(String index, String type,
                                             List<String> keyWords,
                                             Map<String, String> fieldAndValue,
                                             int from, int size) {
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isAnyBlank(index, type)) {
            return null;
        }
        url += "/" + index + "/" + type + "/_search";
        logger.debug(url);
        //query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //bool
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //should
        if (keyWords != null && !keyWords.isEmpty()) {
            keyWords.forEach(keyword -> boolQueryBuilder.should(QueryBuilders.queryStringQuery("\"" + keyword + "\"")));
            boolQueryBuilder.minimumShouldMatch("80%");
        }

        //must
        if (fieldAndValue != null && !fieldAndValue.isEmpty()) {
            Set<Map.Entry<String, String>> entries = fieldAndValue.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isNoneBlank(key, value)) {
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(key, value).slop(0));
                }
            }
        }

        //from和size
        SearchSourceBuilder query = searchSourceBuilder.query(boolQueryBuilder).from(from).size(size);
        logger.debug(query.toString());

        HttpEntity<String> formEntity = generateHttpEntity(query.toString());
        return restTemplate.postForObject(url, formEntity, String.class);
    }

    /**
     * 请求体查询
     *
     * @param index         索引
     * @param keyWord       搜索关键词
     * @param fieldAndValue k-字段名 v-值
     * @param startDate     开始时间
     * @param endDate       结束时间
     * @param from          分页
     * @param size          分页
     * @return 查询结果
     */
    public static String queryWithDsl(String index, String keyWord, Map<String, String> fieldAndValue, Long startDate, Long endDate, Integer from, Integer size) {
        //表示时间的字段
        String dateField = "createDate";
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isNotBlank(index)) {
            url += "/" + index;
        }
        url += "/_search";
        logger.debug("url: {}", url);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.queryStringQuery(keyWord).phraseSlop(0));

        if (fieldAndValue != null && !fieldAndValue.isEmpty()) {
            Set<Map.Entry<String, String>> entries = fieldAndValue.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isNoneBlank(key, value)) {
                    if (value.contains("/")) {
                        String[] vals = value.split("/");
                        for (String val : vals) {
                            boolQueryBuilder.should(QueryBuilders.matchPhraseQuery(key, val).slop(0)).minimumNumberShouldMatch(1);
                        }
                    } else {
                        boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(key, value).slop(0));
                    }
                }
            }
        }
        boolQueryBuilder.must(QueryBuilders.rangeQuery(dateField).from(startDate).to(endDate));
        SearchSourceBuilder query = searchSourceBuilder.query(boolQueryBuilder).from(from).size(size);

        logger.debug("queryString: {}", query);
        HttpEntity<String> formEntity = generateHttpEntity(query.toString());
        return restTemplate.postForObject(url, formEntity, String.class);
    }

    /**
     * 请求体查询
     *
     * @param index       索引
     * @param requestBody 请求体
     * @return 查询结果
     */
    public static String queryWithDsl(String index, String requestBody) {
        logger.debug("requestBody: {}", requestBody);
        String url = ConfigUtil.getConfig("ES.url");
        if (StringUtils.isNotBlank(index)) {
            url += "/" + index;
        }
        url += "/_search";

        logger.debug("url: {}", url);
        HttpEntity<String> formEntity = generateHttpEntity(requestBody);
        return restTemplate.postForObject(url, formEntity, String.class);
    }

    /**
     * 按_type和_index分组
     *
     * @param keyWord
     * @return
     */
    public static String queryWithAggs(String keyWord) {
        String url = ConfigUtil.getConfig("ES.url") + "/_search?search_type=count";
        logger.debug("url: {}", url);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.queryStringQuery("\"" + keyWord + "\""));
        searchSourceBuilder.query(boolQueryBuilder);

        TermsBuilder groupByType = AggregationBuilders.terms("groupByType").field("_type").size(100);
        groupByType.subAggregation(AggregationBuilders.terms("groupByIndex").field("_index").size(100));
        SearchSourceBuilder query = searchSourceBuilder.aggregation(groupByType);

        HttpEntity<String> formEntity = generateHttpEntity(query.toString());
        return restTemplate.postForObject(url, formEntity, String.class);
    }

    /**
     * 按_type和_index分组
     *
     * @return
     */
    public static String queryWithAggs(List<String> keyWords, Map<String, String> fieldAndValue) {
        String url = ConfigUtil.getConfig("ES.url") + "/_search?search_type=count";
        logger.debug("url: {}", url);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //should
        if (keyWords != null && !keyWords.isEmpty()) {
            keyWords.forEach(keyword -> boolQueryBuilder.should(QueryBuilders.queryStringQuery("\"" + keyword + "\"")));
            boolQueryBuilder.minimumShouldMatch("80%");
        }

        //must
        if (fieldAndValue != null && !fieldAndValue.isEmpty()) {
            Set<Map.Entry<String, String>> entries = fieldAndValue.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (StringUtils.isNoneBlank(key, value)) {
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(key, value).slop(0));
                }
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);

        TermsBuilder groupByType = AggregationBuilders.terms("groupByType").field("_type").size(100);
        groupByType.subAggregation(AggregationBuilders.terms("groupByIndex").field("_index").size(100));
        SearchSourceBuilder query = searchSourceBuilder.aggregation(groupByType);

        HttpEntity<String> formEntity = generateHttpEntity(query.toString());
        return restTemplate.postForObject(url, formEntity, String.class);
    }

    /**
     * 创建bulk批量执行的JSON格式
     *
     * @param bodies
     * @return
     */
    private static String createBulkIndexJson(List<Map<String, Object>> bodies) {
        if (bodies == null || bodies.isEmpty()) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (Map<String, Object> body : bodies) {
            if (CheckBlankUtil.hasNonNullAndNonEmptyValue(body.get("id"))) {
                result.append("{\"index\":{\"_id\":").append(body.get("id")).append("}}\n");
            } else {
                result.append("{\"index\":{}}\n");
            }
            result.append(JSONObject.toJSONString(body)).append("\n");

        }
        return result.toString();
    }

    /**
     * 创建局部更新的JSON格式
     *
     * @return
     */
    private static String createPartialUpdateJson(Map<String, Object> body) {
        if (body == null || body.isEmpty()) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("doc", body);
        return JSONObject.toJSONString(result);
    }

    /**
     * 解析出hits中的total和rows
     */
    public static Map<String, Object> parseHits(String result) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(result)) {
            return map;
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject hits = jsonObject.getJSONObject("hits");
        //total
        Integer total = hits.getInteger("total");

        JSONArray subHits = hits.getJSONArray("hits");
        List<TreeMap<String, Object>> rowList = subHits.stream().map(one -> {
            JSONObject hit = (JSONObject) one;
            Map<String, Object> source = hit.getObject("_source", Map.class);
            return new TreeMap<>(source);
        }).collect(Collectors.toList());

        map.put("total", total);
        map.put("rows", rowList);
        return map;
    }

    public static List<Map<String, Object>> parseHitsWithoutTotal(String result) {

        if (StringUtils.isBlank(result)) {
            return Collections.emptyList();
        }
        JSONObject jsonObject = JSONObject.parseObject(result);
        JSONObject hits = jsonObject.getJSONObject("hits");

        JSONArray subHits = hits.getJSONArray("hits");

        return subHits.stream().map(one -> {
            JSONObject hit = (JSONObject) one;
            Map<String, Object> source = hit.getObject("_source", Map.class);
            return new TreeMap<>(source);
        }).collect(Collectors.toList());
    }

    /**
     * 解析出hits中的total和rows
     */
    public static Map<String, Object> parseAggregations(String queryResult, int offset, int limit) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(queryResult)) {
            return map;
        }
        JSONObject jsonObject = JSONObject.parseObject(queryResult);
        JSONObject aggregations = jsonObject.getJSONObject("aggregations");
        JSONObject groupByType = aggregations.getJSONObject("groupByType");

        JSONArray buckets = groupByType.getJSONArray("buckets");
        if (buckets == null || buckets.isEmpty()) {
            return map;
        }

        List<Object> result = buckets.stream().flatMap(one -> {
            JSONObject bucket = (JSONObject) one;
            String type = bucket.getString("key");
            JSONObject groupByIndex = bucket.getJSONObject("groupByIndex");
            JSONArray indexBuckets = groupByIndex.getJSONArray("buckets");
            return indexBuckets.stream().map(body -> {
                JSONObject indexBucket = (JSONObject) body;
                indexBucket.put("type", type);
                return body;
            });
        }).sorted((a, b) -> {
            JSONObject jsonA = (JSONObject) a;
            JSONObject jsonB = (JSONObject) b;
            return -jsonA.getLong("doc_count").compareTo(jsonB.getLong("doc_count"));
        }).collect(Collectors.toList());

        int size = result.size();

        //用于分页
        int toIndex = offset + limit;
        if (toIndex > size) {
            toIndex = size;
        }
        List<Object> subList = result.subList(offset, toIndex);

        map.put("total", size);
        map.put("rows", subList);
        return map;
    }
}
