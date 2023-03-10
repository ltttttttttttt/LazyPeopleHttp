package com.lt.backend

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * creator: lt  2020/11/11  lt.dygzs@qq.com
 * effect : 测试接口
 * warning:
 */
@RestController
@RequestMapping("/post/")
class PostInterface() {

    @GetMapping("/postA")
    fun postA(u: String) {

    }
}