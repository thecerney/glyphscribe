package com.example.demo.mybatis;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface LogTestMybatis {
    List<Map<String,Object>> retrieveTestQuery(Map<String,String> params);
}