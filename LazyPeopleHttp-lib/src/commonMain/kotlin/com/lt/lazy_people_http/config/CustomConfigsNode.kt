package com.lt.lazy_people_http.config

import io.ktor.client.request.*

/**
 * creator: lt  2023/3/14  lt.dygzs@qq.com
 * effect : 自定义配置的节点
 * warning:
 */
internal class CustomConfigsNode(val block: HttpRequestBuilder.() -> Unit) {
    var next: CustomConfigsNode? = null
}