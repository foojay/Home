---
layout: post
title:  "java常用工具类"
date:   2016-12-21 13:24:00
categories: java util
excerpt: java常用工具类
---

* content
{:toc}


### Jackson

1.导入

jackson-core-asl-1.9.12.jar

jackson-mapper-asl-1.9.12.jar 


      public class JsonBinder {
      protected static final Logger logger = Logger.getLogger(JsonBinder.class);
      private ObjectMapper mapper;
      @SuppressWarnings("deprecation")
      public JsonBinder(Inclusion inclusion) {
            mapper = new ObjectMapper();
            // 设置输出包含的属性
            mapper.getSerializationConfig().setSerializationInclusion(inclusion);
            // 设置输入时忽略JSON字符串中存在而Java对象实际没有的属性
            mapper.getDeserializationConfig().set(
                        org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      }
      /***
       * 
       * getReadValue: 集合类型转换. <br/>
       *
       * @param json
       * @param javaType
       * @return
       * @since JDK 1.6
       */
      public Object getReadValue(String json, JavaType javaType) {
            try {
                  return mapper.readValue(json, javaType);
            } catch (Exception e) {
                  e.printStackTrace();
            }
            
            return null;
      }
      /**
       * 获取泛型的Collection Type
       * 
       * @param collectionClass
       *            泛型的Collection
       * @param elementClasses
       *            元素类
       * @return JavaType Java类型
       * @since 1.0
       */
      public JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
            return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);
      }
      /**
       * 创建输出全部属性到Json字符串的Binder.
       */
      public static JsonBinder buildNormalBinder() {
            return new JsonBinder(Inclusion.ALWAYS);
      }
      /**
       * 创建只输出非空属性到Json字符串的Binder.
       */
      public static JsonBinder buildNonNullBinder() {
            return new JsonBinder(Inclusion.NON_NULL);
      }
      /**
       * 创建只输出初始值被改变的属性到Json字符串的Binder.
       */
      public static JsonBinder buildNonDefaultBinder() {
            return new JsonBinder(Inclusion.NON_DEFAULT);
      }
      /**
       * 如果JSON字符串为Null或"null"字符串,返回Null. 如果JSON字符串为"[]",返回空集合.
       * 
       * 如需读取集合如List/Map,且不是List<String>这种简单类型时使用如下语句: List<MyBean> beanList = binder.getMapper().readValue(listString, new TypeReference<List<MyBean>>() {});
       */
      public <T> T fromJson(String jsonString, Class<T> clazz) {
            if (StringUtils.isEmpty(jsonString)) {
                  return null;
            }
            try {
                  return mapper.readValue(jsonString, clazz);
            } catch (IOException e) {
                  logger.warn("parse json string error:" + jsonString, e);
                  return null;
            }
      }
      /**
       * 如果对象为Null,返回"null". 如果集合为空集合,返回"[]".
       */
      public String toJson(Object object) {
            try {
                  return mapper.writeValueAsString(object);
            } catch (IOException e) {
                  logger.warn("write to json string error:" + object, e);
                  return null;
            }
      }
      /**
       * 设置转换日期类型的format pattern,如果不设置默认打印Timestamp毫秒数.
       */
      @SuppressWarnings("deprecation")
      public void setDateFormat(String pattern) {
            if (StringUtils.isNotBlank(pattern)) {
                  DateFormat df = new SimpleDateFormat(pattern);
                  mapper.getSerializationConfig().setDateFormat(df);
                  mapper.getDeserializationConfig().setDateFormat(df);
            }
      }
      /**
       * 取出Mapper做进一步的设置或使用其他序列化API.
       */
      public ObjectMapper getMapper() {
            return mapper;
      }
      }

### ResponseObject

      /**
       * 请求的返回结果。 如果:success=true resultObject=处理结果； 如果:success=false
       * errorMessage=错误信息
       * 
       *
       */
      public class ResponseObject {
            
      public static final Integer status_400 = 400;   //业务异常
      public static final Integer status_511 = 511;   //业务错误-未登录
      public static final Integer status_512 = 512;   //业务错误-校验失败
      public static final Integer status_513 = 513;   //业务错误-手机短信验证码发送失败
      public static final Integer status_100 = 100;   //业务错误-数据重复
      
      
      private  Integer status = 200;      //正常请求
      private String errorMessage;
      private Object resultObject;
      public static ResponseObject newErrorResponseObject(Integer status,String errorMessage) {
            ResponseObject res = new ResponseObject();
            res.setStatus(status);
            res.setErrorMessage(errorMessage);
            return res;
      }
      public static ResponseObject newSuccessResponseObject(Object resultObject) {
            ResponseObject res = new ResponseObject();
            res.setResultObject(resultObject);
            return res;
      }
      public String getErrorMessage() {
            return errorMessage;
      }
      public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
      }
      public Object getResultObject() {
            return resultObject;
      }
      public void setResultObject(Object resultObject) {
            this.resultObject = resultObject;
      }
      public Integer getStatus() {
            return status;
      }
      public void setStatus(Integer status) {
            this.status = status;
      }
      }

### 分页

业务层

      public Page<Resource> getResource(int pageNum, int pageSize,Resource r){
                  Integer totalCount = this.resourceDAO.listCount(r);
                  List<Resource> list=null;
                  int offset = (pageNum - 1) * pageSize; // 起始下标
            offset = offset > 0 ? offset : 0;
            offset = offset < totalCount ? offset : totalCount;
            if(totalCount > 0){
                  list=this.resourceDAO.list(offset, pageSize, r);
            }
                  return new Page<Resource>(list,totalCount,pageSize,pageNum);
            }





封装类

      public class Page<T> {
            public static final int DEFAULT_PAGE_SIZE = 15;
            private List<T> items;
            private int totalCount;// 总记录数
            private int totalPageCount;// 总页数
            private int pageSize;// 每页记录个数
            private int currentPage;
            
      public static void main(String[] args) {
            Page<Integer> p = new Page<Integer>(null, 9, 20, 11);
            System.out.println("totlPageCount:" + p.getTotalPageCount()
                        + " totalCount:" + p.getTotalCount() + " pagesize:"
                        + p.getPageSize() + " current:" + p.getCurrentPage() + " pre:"
                        + p.getPrevPage() + " next:" + p.getNextPage());
      }
      public Page(List<T> items, int totalCount, int pageSize, int currentPage) {
            this.items = items;
            this.pageSize = pageSize > 1 ? pageSize : 1;
            this.currentPage = currentPage > 0 ? currentPage : 1;
            if (totalCount > 0) {
                  this.totalCount = totalCount;
                  totalPageCount = totalCount / this.pageSize;
                  if (this.totalCount % this.pageSize > 0)
                        totalPageCount++;
            } else {
                  this.totalCount = 0;
                  totalPageCount = 0;
            }
      }
      @Override
      public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("totalCount:" + totalCount);
            sb.append(" totalPageCount:" + totalPageCount);
            sb.append(" pageSize:" + pageSize);
            sb.append(" currentPage:" + currentPage);
            sb.append(" items.size:" + items.size());
            return sb.toString();
      }
      public int getPrevPage() {
            int pre = this.currentPage - 1;
            pre = pre < 1 ? 1 : pre;
            pre = pre < totalPageCount ? pre : totalPageCount;
            return pre;
      }
      public int getNextPage() {
            int next = this.currentPage + 1;
            next = next > 1 ? next : 1;
            next = next < totalPageCount ? next : totalPageCount;
            return next;
      }
      public int getCurrentPage() {
            return currentPage;
      }
      
      public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
      }
      public List<T> getItems() {
            return items;
      }
      
      public void setItems(List<T> items) {
            this.items = items;
      }
      public int getPageSize() {
            return pageSize;
      }
      
      public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
      }
      public int getTotalCount() {
            return totalCount;
      }
      
      public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
      }
      public int getTotalPageCount() {
            return totalPageCount;
      }
      
      public void setTotalPageCount(int totalPageCount) {
            this.totalPageCount = totalPageCount;
      }     
      }





