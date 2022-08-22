package com.pickme.reggie.config;

import com.pickme.reggie.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import java.util.List;

//TODO WebMvcConfigurationSupport 配置类

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 设置静态资源处理器
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
        //输出日志信息
        log.info("静态资源处理器初始化成功");
    }

    /**
     * 扩展Spring MVC框架的消息转换器
     * @param converters the list of configured converters to extend
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用的是Jackson将Java对象转为json数据
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将消息转换器对象添加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
        log.info("消息转换器扩展插入成功");
    }
}
