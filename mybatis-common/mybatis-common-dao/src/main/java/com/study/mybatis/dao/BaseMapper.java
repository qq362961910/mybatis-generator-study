package com.study.mybatis.dao;

import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface BaseMapper<T, E, PK extends Serializable> {

    void insert(T t);

    int insertSelective(T record);

    void insertList(List<T> list);

    int deleteByPrimaryKey(PK pk);

    int deleteByExample(E example);

    long countByExample(E example);

    T selectByPrimaryKey(PK pk);

    List<T> selectByExample(E example);

    List<T> selectAll();

    int updateByPrimaryKey(T record);

    int updateByPrimaryKeySelective(T record);

    int updateByExample(@Param("record") T record, @Param("example") E example);

    int updateByExampleSelective(@Param("record") T record,
                                 @Param("example") E example);
}
