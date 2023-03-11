package com.lt.backend

import com.lt.backend.model.UserBean
import com.lt.backend.model.apiFail
import com.lt.backend.model.apiSuccess
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * creator: lt  2020/11/11  lt.dygzs@qq.com
 * effect : 测试接口
 * warning:
 */
@RestController
@RequestMapping("/get/")
class GetInterface {

    @GetMapping("/getA")
    fun getA(t: String) = apiSuccess(t)

    @GetMapping("/getB")
    fun getB(t: UserBean) = apiSuccess(t)

    @GetMapping("/getC")
    fun getC(t: UserBean) = apiSuccess(t.name)

    @GetMapping("/getD/success")
    fun success() = apiSuccess(null)

    @GetMapping("/getD/fail")
    fun fail() = apiFail("fail")

}