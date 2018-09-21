---
layout: post
title:  " Spring Boot jpa 数据操作基类  "
date:   2017-02-05 13:39:00
categories: java
excerpt:  Spring Boot jpa 数据操作基类
---

* content
{:toc}


###  serviceImpl


    public class JpaServiceImpl<R extends JpaRepository<T, ID>, T extends Model, ID extends Serializable> implements IJpaService<T, ID> {
    @Autowired
    protected R repository;
    @PersistenceContext
    private EntityManager entityManager;
    
    public JpaServiceImpl() {
    }
    
    public T insert(T entity) {
        this.repository.save(entity);
        return entity;
    }
    
    public List<T> insert(List<T> entityList) {
        Iterator var2 = entityList.iterator();
    
        while(var2.hasNext()) {
            Model entity = (Model)var2.next();
            this.repository.save(entity);
        }
    
        return entityList;
    }
    
    public Set<T> insert(Set<T> entitySet) {
        Iterator var2 = entitySet.iterator();
    
        while(var2.hasNext()) {
            Model entity = (Model)var2.next();
            this.repository.save(entity);
        }
    
        return entitySet;
    }
    
    public T update(T entity) {
        return (Model)this.repository.save(entity);
    }
    
    public List<T> update(List<T> entityList) {
        Iterator var2 = entityList.iterator();
    
        while(var2.hasNext()) {
            Model entity = (Model)var2.next();
            this.repository.save(entity);
        }
    
        return entityList;
    }
    
    public Set<T> update(Set<T> entitySet) {
        Iterator var2 = entitySet.iterator();
    
        while(var2.hasNext()) {
            Model entity = (Model)var2.next();
            this.repository.save(entity);
        }
    
        return entitySet;
    }
    
    public void deleteById(ID id) {
        this.repository.deleteById(id);
    }
    
    public void delete(T entity) {
        this.repository.delete(entity);
    }
    
    public void deleteIdList(List<ID> idList) {
        Iterator var2 = idList.iterator();
    
        while(var2.hasNext()) {
            Serializable id = (Serializable)var2.next();
            this.repository.deleteById(id);
        }
    
    }
    
    public void deleteEntityList(List<T> entityList) {
        this.repository.deleteInBatch(entityList);
    }
    
    public void deleteSet(Set<T> entitySet) {
        this.repository.deleteInBatch(new ArrayList(entitySet));
    }
    
    public void deleteAll() {
        this.repository.deleteAll();
    }
    
    public T save(T entity) {
        return (Model)this.repository.save(entity);
    }
    
    public List<T> save(List<T> entityList) {
        return this.repository.saveAll(entityList);
    }
    
    public Set<T> save(Set<T> entitySet) {
        return new HashSet(this.repository.saveAll(new ArrayList(entitySet)));
    }
    
    public T findById(ID id) {
        return (Model)this.repository.findById(id).orElse((Object)null);
    }
    
    public List<T> selectBatchIds(List<ID> idList) {
        return this.repository.findAllById(idList);
    }
    
    public <X extends T> List<T> selectList() {
        return this.repository.findAll();
    }
    public <X extends T> PageQuery<T> selectPage(PageQuery<T> page) {
        page = this.selectPage(page, (Example)null);
        return page;
    }
    public <X extends T> long selectCount(Example<X> example) {
        return this.repository.count(example);
    }
    public <X extends T> List<X> selectList(Example<X> example, Sort sort) {
        return this.repository.findAll(example, sort);
    }
    public <X extends T> List<X> selectList(Example<X> example, String... properties) {
        Sort sort = new Sort(Direction.ASC, properties);
        return this.repository.findAll(example, sort);
    }
    public <X extends T> List<X> selectList(Example<X> example) {
        return this.repository.findAll(example);
    }
    public <X extends T> PageQuery<X> selectPage(PageQuery<X> page, Example<X> example) {
        Page pageRet = this.repository.findAll(example, page);
        page.setRecords(pageRet.getContent());
        page.setTotal((new Long(pageRet.getTotalElements())).intValue());
        return page;
    }
    @Modifying
    public void executeUpdate(String sql, Object... params) {
        Query query = this.entityManager.createNativeQuery(sql);
        int i = 1;
        if(params != null) {
            Object[] var5 = params;
            int var6 = params.length;
            for(int var7 = 0; var7 < var6; ++var7) {
                Object object = var5[var7];
                query.setParameter(i++, object);
            }
        }
        query.executeUpdate();
    }
    public List<Map<String, Object>> selectList(String sql, boolean isJavaFieldName, Object... params) {
        Query query = this.entityManager.createNativeQuery(sql);
        int i = 1;
        if(params != null) {
            Object[] list = params;
            int camelCaseRecords = params.length;
            for(int var8 = 0; var8 < camelCaseRecords; ++var8) {
                Object object = list[var8];
                query.setParameter(i++, object);
            }
        }
        ((NativeQuery)query.unwrap(NativeQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List var10 = query.getResultList();
        if(isJavaFieldName) {
            List var11 = ListUtil.convertCamelCase(var10);
            return var11;
        } else {
            return var10;
        }
    }
    public List<Map<String, Object>> selectList(String sql) {
        return this.selectList(sql, false, (Object[])null);
    }
    public PageQuery<Map<String, Object>> selectPage(PageQuery<Map<String, Object>> page, String sql, boolean isJavaFieldName, Object... params) {
        Query query = this.entityManager.createNativeQuery(sql);
        int i = 1;
        if(params != null) {
            Object[] list = params;
            int camelCaseRecords = params.length;
            for(int var9 = 0; var9 < camelCaseRecords; ++var9) {
                Object object = list[var9];
                query.setParameter(i++, object);
            }
        }
        ((NativeQuery)query.unwrap(NativeQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setFirstResult(page.getPageNumber() * page.getPageSize());
        query.setMaxResults(page.getPageSize());
        List var11 = query.getResultList();
        if(isJavaFieldName) {
            List var12 = ListUtil.convertCamelCase(var11);
            page.setRecords(var12);
        } else {
            page.setRecords(var11);
        }
        return page;
    }
    public PageQuery<Map<String, Object>> selectPage(PageQuery<Map<String, Object>> page, String sql) {
        return this.selectPage(page, sql, false, new Object[0]);
    }
    public Map<String, Object> findOne(String sql, boolean isJavaFieldName, Object... params) {
        Query query = this.entityManager.createNativeQuery(sql);
        int i = 1;
        if(params != null) {
            Object[] map = params;
            int var7 = params.length;
            for(int var8 = 0; var8 < var7; ++var8) {
                Object object = map[var8];
                query.setParameter(i++, object);
            }
        }
        ((NativeQuery)query.unwrap(NativeQuery.class)).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        query.setFirstResult(1);
        query.setMaxResults(1);
        Map var10 = (Map)query.getSingleResult();
        return isJavaFieldName?ListUtil.convertCamelCase(var10):var10;
    }
    public Map<String, Object> findOne(String sql) {
        return this.findOne(sql, false, new Object[0]);
    }
    public Integer count(String sql, Object... params) {
        Query query = this.entityManager.createNativeQuery(sql);
        int i = 1;
        if(params != null) {
            Object[] ret = params;
            int var6 = params.length;
            for(int var7 = 0; var7 < var6; ++var7) {
                Object object = ret[var7];
                query.setParameter(i++, object);
            }
        }
        BigInteger var9 = (BigInteger)query.getSingleResult();
        return var9 == null?null:Integer.valueOf(var9.intValue());
    }
    @Modifying
    public void executeUpdateByHql(String hql, Object... params) {
        Query query = this.entityManager.createQuery(hql);
        int i = 1;
        if(params != null) {
            Object[] var5 = params;
            int var6 = params.length;
            for(int var7 = 0; var7 < var6; ++var7) {
                Object object = var5[var7];
                query.setParameter(i++, object);
            }
        }
        query.executeUpdate();
    }
    public List<?> selectListByHql(String hql, Object... params) {
        Query query = this.entityManager.createQuery(hql);
        int i = 1;
        if(params != null) {
            Object[] list = params;
            int var6 = params.length;
            for(int var7 = 0; var7 < var6; ++var7) {
                Object object = list[var7];
                query.setParameter(i++, object);
            }
        }
        List var9 = query.getResultList();
        return var9;
    }
    public PageQuery<?> selectPageByHql(PageQuery<?> page, String hql, Object... params) {
        Query query = this.entityManager.createQuery(hql);
        int i = 1;
        if(params != null) {
            Object[] list = params;
            int var7 = params.length;
            for(int var8 = 0; var8 < var7; ++var8) {
                Object object = list[var8];
                query.setParameter(i++, object);
            }
        }
        query.setFirstResult(page.getPageNumber() * page.getPageSize());
        query.setMaxResults(page.getPageSize());
        List var10 = query.getResultList();
        page.setRecords(var10);
        return page;
    }
    public Object findOneByHql(String hql, Object... params) {
        Query query = this.entityManager.createQuery(hql);
        int i = 1;
        if(params != null) {
            Object[] var5 = params;
         
            for(int var7 = 0; var7 < var6; ++var7) {
                Object object = var5[var7];
                query.setParameter(i++, object);
            }
        }
        query.setFirstResult(1);
        query.setMaxResults(1);
        return query.getSingleResult();
    }
    public Integer countByHql(String hql, Object... params) {
        Query query = this.entityManager.createQuery(hql);
        int i = 1;
        if(params != null) {
            Object[] ret = params;
            int var6 = params.length;
            for(int var7 = 0; var7 < var6; ++var7) {
                Object object = ret[var7];
                query.setParameter(i++, object);
            }
        }
    
          BigInteger var9 = (BigInteger)query.getSingleResult();
          return var9 == null?null:Integer.valueOf(var9.intValue());
      }
       }





###   baseDAO


    public abstract class BaseDao<K extends JpaRepository, T extends Serializable> {
    public K jpaRepository;
    public K getJpaRepository() {
        return jpaRepository;
    }
    public void setJpaRepository(K jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    /**
     * find Object By Serializable Id
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public T selectById(Serializable id) {
        return (T) jpaRepository.getOne(id);
    }
    /**
     * select All entity
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        return jpaRepository.findAll();
    }
    /**
     * save entity
     *
     * @param entity
     */
    @SuppressWarnings("unchecked")
    public T save(T entity) {
        Object savedEntity = jpaRepository.saveAndFlush(entity);
        return (T) savedEntity;
    }
    @SuppressWarnings("unchecked")
    public void update(T entity) {
        jpaRepository.saveAndFlush(entity);
    }
    /**
     *
     * @param builder
     * @param entityManager
     * @param param
     * @return
     */
    public <T extends Pageable> PageQuery<T> selectPaging(JpaCriteriaBuilder builder, EntityManager entityManager, T param) {
        //构建PageQuery
        PageQuery<T> pageQuery = param.build();
        //克隆查询构建类
        JpaCriteriaBuilder countBuilder = JpaCriteriaBuilder.cloneFrom(builder);
        //排序
        if(pageQuery.getSort() != null){
            Sort.Order order = pageQuery.getSort().iterator().next();
            String sort = order.getDirection().name().toLowerCase();
            String property = order.getProperty();
            builder.addOrder(property, sort);
        }
        CriteriaQuery<T> criteriaQuery = builder.newCriteriaQuery();
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        List<T> list = query.setFirstResult(pageQuery.getSize()*(pageQuery.getCurrent()-1)).setMaxResults(pageQuery.getSize()).getResultList();
        Integer total = countBuilder.count();
        pageQuery.setRecords(list);
        pageQuery.setTotal(total);
        return pageQuery;
    }
    /**
     * 查询单个实体
     *
     * @param builder
     * @param entityManager
     * @return
     */
    public T selectSingle(JpaCriteriaBuilder builder, EntityManager entityManager) {
        CriteriaQuery<T> criteriaQuery = builder.newCriteriaQuery();
        TypedQuery<T> query = entityManager.createQuery(criteriaQuery);
        return query.getSingleResult();
    }
        }



###  JpaCriteriaBuilder


    public class JpaCriteriaBuilder {
    private static final long serialVersionUID = 5064932771068929342L;
    private EntityManager entityManager;
    /** 要查询的模型对象 */
    private Class clazz;
    /** 查询条件列表 */
    private Root from;
    private List<Predicate> predicates;
    private CriteriaQuery criteriaQuery;
    private CriteriaBuilder criteriaBuilder;
    /** 排序方式列表 */
    private List<Order> orders;
    /** 关联模式 */
    private Map<String, JpaCriteriaBuilder> subQuery;
    private Map<String, JpaCriteriaBuilder> linkQuery;
    private String projection;
    /** 或条件 */
    private List<JpaCriteriaBuilder> orQuery;
    private String groupBy;
    /** 关联关系 */
    private Map<String, JoinType> relation = new TreeMap<>();
    private JpaCriteriaBuilder() {
    }
    private JpaCriteriaBuilder(Class clazz, EntityManager entityManager) {
        this.clazz = clazz;
        this.entityManager = entityManager;
        this.criteriaBuilder = this.entityManager.getCriteriaBuilder();
        this.criteriaQuery = criteriaBuilder.createQuery(this.clazz);
        this.from = criteriaQuery.from(this.clazz);
        this.predicates = new ArrayList();
        this.orders = new ArrayList();
    }
    /** 通过类创建查询条件 */
    public static JpaCriteriaBuilder forClass(Class clazz, EntityManager entityManager) {
        return new JpaCriteriaBuilder(clazz, entityManager);
    }
    /** 复制Builder */
    public static JpaCriteriaBuilder cloneFrom(JpaCriteriaBuilder builder) {
        JpaCriteriaBuilder jpaCriteriaBuilder = new JpaCriteriaBuilder(builder.getClazz(), builder.getEntityManager());
        jpaCriteriaBuilder.predicates = builder.predicates;
        builder.relation.entrySet().forEach(entry -> {
            jpaCriteriaBuilder.from.join(entry.getKey(), entry.getValue());
        });
        return jpaCriteriaBuilder;
    }
    /**
     * 内连接查询
     *
     * @param propertyName
     * @return
     */
    public JpaCriteriaBuilder innerJoin(String propertyName) {
        relation.put(propertyName, JoinType.INNER);
        this.from.join(propertyName, JoinType.INNER);
        return this;
    }
    /**
     * 内连接查询,可设置查询条件
     *
     * @param propertyName
     * @return
     */
    public Join<?, ?> innerJoinFilter(String propertyName) {
        relation.put(propertyName, JoinType.INNER);
        Join<?, ?> join = (Join<?, ?>) this.from.join(propertyName, JoinType.INNER);
        return join;
    }
    /**
     * 根据现有Join路径继续Join
     *
     * @param path
     * @param propertyName
     * @return
     */
    public Join<?, ?> innerJoinFilter(Join<?, ?> path, String propertyName) {
        relation.put(propertyName, JoinType.INNER);
        Join<?, ?> join = (Join<?, ?>) path.join(propertyName, JoinType.INNER);
        return join;
    }
    /**
     * 内连接查询(抓取实体属性)
     *
     * @param propertyName
     * @return
     */
    public JpaCriteriaBuilder innerJoinFetch(String propertyName) {
        relation.put(propertyName, JoinType.INNER);
        this.from.fetch(propertyName, JoinType.INNER);
        return this;
    }
    /**
     * 内连接查询(抓取实体属性),可设置查询条件
     *
     * @param propertyName
     * @return
     */
    public Join<?, ?> innerJoinFetchFilter(String propertyName) {
        relation.put(propertyName, JoinType.INNER);
        Join<?, ?> join = (Join<?, ?>) this.from.fetch(propertyName, JoinType.INNER);
        return join;
    }
    /**
     * 根据现有Join路径继续Fetch
     *
     * @param path
     * @param propertyName
     * @return
     */
    public Join<?, ?> innerJoinFetchFilter(Join<?, ?> path, String propertyName) {
        relation.put(propertyName, JoinType.INNER);
        Join<?, ?> join = (Join<?, ?>) path.fetch(propertyName, JoinType.INNER);
        return join;
    }
    /**
     * 属性匹配
     *
     * @param join
     * @param propertyName
     * @param value
     * @return
     */
    public JpaCriteriaBuilder eq(Join<?, ?> join, String propertyName, Object value) {
        if (isNullOrEmpty(value))
            return this;
        this.predicates.add(criteriaBuilder.equal(join.get(propertyName), value));
        return this;
    }
    /**
     * 模糊匹配
     *
     * @param propertyName
     *            属性名称
     * @param value
     *            属性值
     */
    public void like(Join<?, ?> join, String propertyName, String value) {
        if (isNullOrEmpty(value))
            return;
        if (value.indexOf("%") < 0)
            value = "%" + value + "%";
        this.predicates.add(criteriaBuilder.like(join.get(propertyName), value));
    }
    /**
     * 左连接查询
     *
     * @param propertyName
     * @return
     */
    public JpaCriteriaBuilder leftJoin(String propertyName) {
        relation.put(propertyName, JoinType.LEFT);
        this.from.join(propertyName, JoinType.LEFT);
        return this;
    }
    /**
     * 左连接查询(抓取实体属性)
     *
     * @param propertyName
     * @return
     */
    public JpaCriteriaBuilder leftJoinFetch(String propertyName) {
        relation.put(propertyName, JoinType.LEFT);
        this.from.fetch(propertyName, JoinType.LEFT);
        return this;
    }
    /** 增加子查询 */
    private void addSubQuery(String propertyName, JpaCriteriaBuilder query) {
        if (this.subQuery == null)
            this.subQuery = new HashMap();
        if (query.projection == null)
            throw new RuntimeException("子查询字段未设置");
        this.subQuery.put(propertyName, query);
    }
    private void addSubQuery(JpaCriteriaBuilder query) {
        addSubQuery(query.projection, query);
    }
    /** 增关联查询 */
    public void addLinkQuery(String propertyName, JpaCriteriaBuilder query) {
        if (this.linkQuery == null)
            this.linkQuery = new HashMap();
        this.linkQuery.put(propertyName, query);
    }
    /** 相等 */
    public JpaCriteriaBuilder eq(String propertyName, Object value) {
        if (isNullOrEmpty(value))
            return this;
        this.predicates.add(criteriaBuilder.equal(from.get(propertyName), value));
        return this;
    }
    private boolean isNullOrEmpty(Object value) {
        if (value instanceof String) {
            return value == null || "".equals(value);
        }
        return value == null;
    }
    public void or(List<String> propertyName, Object value) {
        if (isNullOrEmpty(value))
            return;
        if ((propertyName == null) || (propertyName.size() == 0))
            return;
        Predicate predicate = criteriaBuilder.or(criteriaBuilder.equal(from.get(propertyName.get(0)), value));
        for (int i = 1; i < propertyName.size(); ++i)
            predicate = criteriaBuilder.or(predicate, criteriaBuilder.equal(from.get(propertyName.get(i)), value));
        this.predicates.add(predicate);
    }

    public void orLike(List<String> propertyName, String value) {
        if (isNullOrEmpty(value) || (propertyName.size() == 0))
            return;
        if (value.indexOf("%") < 0)
            value = "%" + value + "%";
        Predicate predicate = criteriaBuilder.or(criteriaBuilder.like(from.get(propertyName.get(0)), value.toString()));
        for (int i = 1; i < propertyName.size(); ++i)
            predicate = criteriaBuilder.or(predicate, criteriaBuilder.like(from.get(propertyName.get(i)), value));
        this.predicates.add(predicate);
    }
    /** 空 */
    public void isNull(String propertyName) {
        this.predicates.add(criteriaBuilder.isNull(from.get(propertyName)));
    }
    /** 非空 */
    public void isNotNull(String propertyName) {
        this.predicates.add(criteriaBuilder.isNotNull(from.get(propertyName)));
    }
    /** 不相等 */
    public void notEq(String propertyName, Object value) {
        if (isNullOrEmpty(value)) {
            return;
        }
        this.predicates.add(criteriaBuilder.notEqual(from.get(propertyName), value));
    }
    /**
     * not in
     *
     * @param propertyName
     *            属性名称
     * @param value
     *            值集合
     */
    public void notIn(String propertyName, Collection value) {
        if ((value == null) || (value.size() == 0)) {
            return;
        }
        Iterator iterator = value.iterator();
        In in = criteriaBuilder.in(from.get(propertyName));
        while (iterator.hasNext()) {
            in.value(iterator.next());
        }
        this.predicates.add(criteriaBuilder.not(in));
    }
    /**
     * 模糊匹配
     *
     * @param propertyName
     *            属性名称
     * @param value
     *            属性值
     */
    public void like(String propertyName, String value) {
        if (isNullOrEmpty(value))
            return;
        if (value.indexOf("%") < 0)
            value = "%" + value + "%";
        this.predicates.add(criteriaBuilder.like(from.get(propertyName), value));
    }
    /**
     * 时间区间查询
     *
     * @param propertyName
     *            属性名称
     * @param lo
     *            属性起始值
     * @param go
     *            属性结束值
     */
    public void between(String propertyName, Date lo, Date go) {
        if (!isNullOrEmpty(lo) && !isNullOrEmpty(go)) {
            this.predicates.add(criteriaBuilder.between(from.get(propertyName), lo, go));
        }
    }
    public void between(String propertyName, Number lo, Number go) {
        if (!(isNullOrEmpty(lo)))
            ge(propertyName, lo);
        if (!(isNullOrEmpty(go)))
            le(propertyName, go);
    }
    /**
     * 小于等于
     *
     * @param propertyName
     *            属性名称
     * @param value
     *            属性值
     */
    public void le(String propertyName, Number value) {
        if (isNullOrEmpty(value)) {
            return;
        }
        this.predicates.add(criteriaBuilder.le(from.get(propertyName), value));
    }
    /**
     * 小于
     *
     * @param propertyName
     *            属性名称
     * @param value
     *            属性值
     */
    public void lt(String propertyName, Number value) {
        if (isNullOrEmpty(value)) {
            return;
        }
        this.predicates.add(criteriaBuilder.lt(from.get(propertyName), value));
    }
    /**
     * 大于等于
     *
     * @param propertyName
     *            属性名称
     * @param value
     *            属性值
     */
    public void ge(String propertyName, Number value) {
        if (isNullOrEmpty(value)) {
            return;
        }
        this.predicates.add(criteriaBuilder.ge(from.get(propertyName), value));
    }
    /**
     * 大于
     *
     * @param propertyName
     *            属性名称
     * @param value
     *            属性值
     */
    public void gt(String propertyName, Number value) {
        if (isNullOrEmpty(value)) {
            return;
        }
        this.predicates.add(criteriaBuilder.gt(from.get(propertyName), value));
    }
    /**
     * in
     *
     * @param propertyName
     *            属性名称
     * @param value
     *            值集合
     */
    public void in(String propertyName, Collection value) {
        if ((value == null) || (value.size() == 0)) {
            return;
        }
        Iterator iterator = value.iterator();
        In in = criteriaBuilder.in(from.get(propertyName));
        while (iterator.hasNext()) {
            in.value(iterator.next());
        }
        this.predicates.add(in);
    }
    /** 直接添加JPA内部的查询条件,用于应付一些复杂查询的情况,例如或 */
    public void addCriterions(Predicate predicate) {
        this.predicates.add(predicate);
    }
    /**
     * 创建查询条件
     *
     * @return JPA离线查询
     */
    public <T> CriteriaQuery<T> newCriteriaQuery() {
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        if (!isNullOrEmpty(groupBy)) {
            criteriaQuery.groupBy(from.get(groupBy));
        }
        if (this.orders != null) {
            criteriaQuery.orderBy(orders);
        }
        addLinkCondition(this);
        return criteriaQuery;
    }
    /**
     * 统计查询数量
     *
     * @return
     */
    public Integer count() {
        CriteriaQuery<Long> criteriaQuery = this.newCriteriaQuery();
        criteriaQuery.select(this.criteriaBuilder.count(from));
        return entityManager.createQuery(criteriaQuery).getSingleResult().intValue();
    }
    private void addLinkCondition(JpaCriteriaBuilder query) {
        Map subQuery = query.linkQuery;
        if (subQuery == null)
            return;
        for (Iterator queryIterator = subQuery.keySet().iterator(); queryIterator.hasNext();) {
            String key = (String) queryIterator.next();
            JpaCriteriaBuilder sub = (JpaCriteriaBuilder) subQuery.get(key);
            relation.put(key, JoinType.LEFT);
            from.join(key, JoinType.LEFT);
            criteriaQuery.where(sub.predicates.toArray(new Predicate[0]));
            addLinkCondition(sub);
        }
    }
    public void addOrder(String propertyName, String order) {
        if (StringUtils.isBlank(propertyName) || StringUtils.isBlank(order))
            return;
        if (this.orders == null)
            this.orders = new ArrayList();
        if (order.equalsIgnoreCase("asc"))
            this.orders.add(criteriaBuilder.asc(from.get(propertyName)));
        else if (order.equalsIgnoreCase("desc"))
            this.orders.add(criteriaBuilder.desc(from.get(propertyName)));
    }
    public Class getModleClass() {
        return this.clazz;
    }
    public String getProjection() {
        return this.projection;
    }
    public void setProjection(String projection) {
        this.projection = projection;
    }
    public Class getClazz() {
        return this.clazz;
    }
    public List<Order> getOrders() {
        return orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    public EntityManager getEntityManager() {
        return this.entityManager;
    }
    public void setEntityManager(EntityManager em) {
        this.entityManager = em;
    }
    public Root getFrom() {
        return from;
    }
    public List<Predicate> getPredicates() {
        return predicates;
    }
    public void setPredicates(List<Predicate> predicates) {
        this.predicates = predicates;
    }
    public CriteriaQuery getCriteriaQuery() {
        return criteriaQuery;
    }
    public CriteriaBuilder getCriteriaBuilder() {
        return criteriaBuilder;
    }
    public String getGroupBy() {
        return groupBy;
    
    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
    }