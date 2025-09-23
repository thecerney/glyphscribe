package com.example.demo.web;

import com.example.demo.mybatis.LogTestMybatis;
import kr.cerney.hobby.glyphscribe.core.output.GlyphScribeEmitter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TestController {

    private final LogTestMybatis mapper;

    public TestController(LogTestMybatis mapper) {
        this.mapper = mapper;
    }

    @GetMapping("/test/log")
    public String testLog() {
        // 샘플 파라미터
        Map<String, String>      params = Map.of("almlCd", "DUMMY");
        List<Map<String,Object>> rows   = mapper.retrieveTestQuery(params);

        // MyBatis SQL ID = 네임스페이스 + id
        String sqlId = "retrieveTestQuery";
//        String sqlId = "com.example.demo.mybatis.LogTestMybatis.retrieveTestQuery";

        // 최근 실행 로그 출력
        return GlyphScribeEmitter.getInstance().printLatest(sqlId);
    }
}