---
layout: post
title:  "mybatis动态SQL"
date:   2016-02-07 11:27:00
categories: mybatis
excerpt:  mybatis动态SQL
---

* content
{:toc}




### 概述

传统的使用JDBC的方法，相信大家在组合复杂的的SQL语句的时候，需要去拼接，稍不注意哪怕少了个空格，都会导致错误。Mybatis的动态SQL功能正是为了解决这种问题， 其通过 if, choose, when, otherwise, trim, where, set, foreach标签，可组合成非常灵活的SQL语句，从而提高开发人员的效率


### 标签

*  if,    你们能判断，我也能判断！

xml:

    <select id="findUserById" resultType="user">
               select * from user where 
               <if test="id != null">
                   id=#{id}
               </if>
                and deleteFlag=0;
    </select>

 上面例子： 如果传入的id 不为空， 那么才会SQL才拼接id = #{id}。 这个相信大家看一样就能明白，不多说。

细心的人会发现一个问题：“你这不对啊！ 要是你传入的id为null,  那么你这最终的SQL语句不就成了 select * from user where and deleteFlag=0,  这语句有问题！”

是啊，这时候，mybatis的 where 标签就该隆重登场啦：



*  where, 有了我，SQL语句拼接条件神马的都是浮云！

xml:

    <select id="findUserById" resultType="user">
               select * from user 
               <where>
                   <if test="id != null">
                       id=#{id}
                   </if>
                   and deleteFlag=0;
               </where>
     </select>

 有些人就要问了： “你这都是些什么玩意儿！ 跟上面的相比， 不就是多了个where标签嘛！ 那这个还会不会出现  select * from user where and deleteFlag=0 ？”

的确，从表面上来看，就是多了个where标签而已， 不过实质上， mybatis是对它做了处理，当它遇到AND或者OR这些，它知道怎么处理。其实我们可以通过 trim 标签去自定义这种处理规则。

上面的where标签，其实用trim 可以表示如下：

     <select id="findUserInfo" resultMap="userList" parameterType="java.lang.String">
      select *  from we_user
      <trim prefix="WHERE" prefixOverrides="AND |OR ">
      <if test="userName !=null">
            username=#{userName}
          </if>
          and status=0
    </trim>
     
    </select>

它的意思就是： 当WHERE后紧随AND或则OR的时候，就去除AND或者OR。 除了WHERE以外， 其实还有一个比较经典的实现，那就是SET。


*  set,  信我，不出错！

xml:

    <update id="updateUser" parameterType="com.dy.entity.User">
               update user set 
               <if test="name != null">
                   name = #{name},
               </if> 
               <if test="password != null">
                   password = #{password},
               </if> 
               <if test="age != null">
                   age = #{age}
               </if> 
               <where>
                   <if test="id != null">
                       id = #{id}
                   </if>
                   and deleteFlag = 0;
               </where>
    </update>

问题又来了： “如果我只有name不为null,  那么这SQL不就成了 update set name = #{name}, where ........ ?  你那name后面那逗号会导致出错啊！”

是的，这时候，就可以用mybatis为我们提供的set 标签了。下面是通过set标签改造后：


    <trim prefix="SET" suffixOverrides=",">
      ...
    </trim>

WHERE是使用的 prefixOverrides（前缀）， SET是使用的 suffixOverrides （后缀）， 看明白了吧！



*   foreach,  你有for, 我有foreach, 不要以为就你才屌！

xml:

    <select id="selectPostIn" resultType="domain.blog.Post">
      SELECT *
      FROM POST P
      WHERE ID in
      <foreach item="item" index="index" collection="list"
          open="(" separator="," close=")">
            #{item}
      </foreach>
    </select>


将一个 List 实例或者数组作为参数对象传给 MyBatis，当这么做的时候，MyBatis 会自动将它包装在一个 Map 中并以名称为键。List 实例将会以“list”作为键，而数组实例的键将是“array”。同样， 当循环的对象为map的时候，index其实就是map的key


*  choose, 我选择了你，你选择了我！

xml:

    <select id="findActiveBlogLike"
         resultType="Blog">
      SELECT * FROM BLOG WHERE state = ‘ACTIVE’
      <choose>
        <when test="title != null">
          AND title like #{title}
        </when>
        <when test="author != null and author.name != null">
          AND author_name like #{author.name}
        </when>
        <otherwise>
          AND featured = 1
        </otherwise>
      </choose>
    </select>



以上例子中： 当title和author都不为null的时候， 那么选择二选一（前者优先）， 如果都为null, 那么就选择 otherwise中的， 如果tilte和author只有一个不为null, 那么就选择不为null的那个。

纵观mybatis的动态SQL， 强大而简单， 相信大家简单看一下就能使用了。




### 例子

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
    <mapper namespace="weixin.office.dao.CompetitionPlatformDao" >
     <resultMap id="BaseResultMap" type="CompetitionPlatform" >
    <id column="id" property="id" jdbcType="VARCHAR" />
    <result column="special_entries_id" property="specialEntriesId" jdbcType="VARCHAR" />
    <result column="special_model_name" property="specialModelName" jdbcType="VARCHAR" />
    <result column="videoUrl" property="videourl" jdbcType="VARCHAR" />
    <result column="installationUrl" property="installationurl" jdbcType="VARCHAR" />
    <result column="videoCount" property="videocount" jdbcType="INTEGER" />
    <result column="installationCount" property="installationcount" jdbcType="INTEGER" />
    </resultMap>

    <select id="getList"  resultMap="BaseResultMap" >
    select * from competition_platform
     </select>
     <insert id="save" parameterType="weixin.office.entity.CompetitionPlatform">
        INSERT  INTO  competition_platform (<include refid="key"/>) VALUES (<include
          refid="value"/>);
    </insert>

    <select id="getListSearch" parameterType="CompetitionPlatform" resultMap="BaseResultMap">
    select * from  competition_platform
    <where>
      <choose>
        <when test="specialModelName!=null">
          special_model_name=#{specialModelName}
        </when>
        <when test="videourl!=null">
          and  videoUrl=#{videourl}
        </when>
        <otherwise>
          1=1
        </otherwise>
      </choose>
      order by  videoCount desc
    </where>
    </select>

    <update id="updateItem" parameterType="Map">
      UPDATE  competition_platform
      <set >
        <trim prefixOverrides=",">
          <choose>
            <when test="modelName!=null">
              special_model_name=#{modelName},
            </when>
          </choose>
         </trim>
      </set>
      <trim prefix="where" suffixOverrides="and |or">
         <choose>
            <when test="id!=null">
              id=#{id}
            </when>
         </choose>
      </trim>
      </update>

    <select id="getByIds" parameterType="List" resultMap="BaseResultMap">
    SELECT * from competition_platform cp where  cp.special_entries_id in <include
          refid="ids"></include>;
    </select>

     <insert id="saveAll"  parameterType="List">
    
    INSERT  INTO   competition_platform(id,special_entries_id,special_model_name) VALUES
    <foreach collection="list" item="item" separator=",">
    (
    
          #{item.id},
    
          #{item.specialEntriesId},
    
          #{item.specialModelName}
    
      )
    </foreach>
    </insert>


    <sql id="key">
    <trim suffixOverrides=",">
      <if test="id!=null">
        id,
      </if>
      <if test="specialEntriesId!=null">
        special_entries_id,
      </if>
      <if test="specialModelName!=null">
        special_model_name,
      </if>
      <if test="videourl!=null">
        videoUrl,
      </if>
      <if test="installationurl!=null">
        installationUrl,
      </if>
      <if test="videocount!=null">
        videoCount,
      </if>
      <if test="installationcount!=null">
        installationCount,
      </if>
    </trim>
    </sql>
  
    <sql id="value">
    <trim suffixOverrides=",">
      <if test="id!=null">
        #{id},
      </if>
      <if test="specialEntriesId!=null">
        #{specialEntriesId},
      </if>
       <if test="specialModelName!=null">
         #{specialModelName},
       </if>
      <if test="videourl!=null">
         #{videourl},
      </if>
      <if test="installationurl!=null">
        #{installationurl},
      </if>
      <if test="videocount!=null">
        #{videocount},
      </if>
      <if test="installationcount!=null">
        #{installationcount},
      </if>
    </trim>
    </sql>

    <sql id="ids">
    <foreach collection="list" item="item" index="index" open="(" separator="," close=")">
       #{item}
    </foreach>
    </sql>
    </mapper>






### 参考文献

*[南柯梦](http://www.cnblogs.com/dongying/p/4092662.html)