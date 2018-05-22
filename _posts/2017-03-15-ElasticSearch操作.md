---
layout: post
title:  "ElasticSearch操作"
date:   2017-03-15 14:27:00
categories: java
excerpt:  ElasticSearch操作
---

* content
{:toc}




### 添加索引



     Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch").build();
        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName
                            ("172.16.1.186"), 9300));
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
         //批量添加
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        try {
            bulkRequest.add(client.prepareIndex("item_index", "item", "2")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("productId", "2")
                            .field("salePrice", 300)
                            .field("productName", "鞋子")
                            .field("subTitle", "夏季鞋子")
                            .endObject()
                    )
            );
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        BulkResponse bulkResponse = bulkRequest.get();
        client.close();


### 查询


    Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch").build();
        TransportClient client = null;
        try {
            client = new PreBuiltTransportClient(settings)
                    .addTransportAddress(new TransportAddress(InetAddress.getByName
                            ("172.16.1.186"), 9300));
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
     try{
            int page=1;
            //设置查询条件
            QueryBuilder qb = matchQuery("productName","鞋子");
            //设置分页
            if (page <=0 ){
                page =1;
            }
            int start=(page - 1) * 5;
            //设置高亮显示
            HighlightBuilder hiBuilder=new HighlightBuilder();
            hiBuilder.preTags("<a style=\"color: #e4393c\">");
            hiBuilder.postTags("</a>");
            hiBuilder.field("productName");
            //执行搜索
            SearchResponse searchResponse = null;
                searchResponse=client.prepareSearch("item_index")
                        .setTypes("item")
                        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                        .setQuery(qb)   // Query
                        .setFrom(start).setSize(5).setExplain(true) //从第几个开始，显示size个数据
                        .highlighter(hiBuilder)     //设置高亮显示
                        .setPostFilter(QueryBuilders.rangeQuery("salePrice").gt(100).lt(400))
                        //过滤条件
                        .addSort("salePrice", SortOrder.ASC)
                        .get();
            SearchHits hits = searchResponse.getHits();
            if (hits.totalHits > 0) {
                for (SearchHit hit : hits) {
                    //设置高亮字段
                    String json=hit.getSourceAsString();
                    String productName = hit.getHighlightFields().get("productName").getFragments
                            ()[0].toString();
                    System.out.println(json);
                    System.out.println(productName);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }