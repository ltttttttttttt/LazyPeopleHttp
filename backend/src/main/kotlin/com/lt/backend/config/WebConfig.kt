package com.lt.backend.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer


/**
 * 创    建:  lt  2019/2/19--18:25
 * 作    用:  适用于SpringMvc的配置(有部分配置不适用于SpringWebFlux)
 * 注意事项:
 */

@Configuration
open class WebConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        //添加拦截器
        registry.addInterceptor(SpringHttpLogger())
    }
}