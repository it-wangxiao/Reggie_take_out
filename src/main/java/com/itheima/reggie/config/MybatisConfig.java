package com.itheima.reggie.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Author Vsunks.v
 * @Blog blog.sunxiaowei.net/996.mba
 * @Description: Mybatis配置类，配置诸如分页插件等内容
 */
@Configuration
@MapperScan("com.itheima.reggie.mapper")// 推荐使用扫描mapper的方式，一劳永逸
public class MybatisConfig {

    //TODO 分页查询插件
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor pageInterceptor = new PaginationInnerInterceptor();
        // 分页条件合理化
        // 设置请求的页面大于最大页后操作， true则查询首页，false继续查询错误页面  默认false
        pageInterceptor.setOverflow(true);
        mybatisPlusInterceptor.addInnerInterceptor(pageInterceptor);

        return mybatisPlusInterceptor;
    }

}
