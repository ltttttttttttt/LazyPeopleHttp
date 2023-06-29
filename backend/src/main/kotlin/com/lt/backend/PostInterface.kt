package com.lt.backend

import com.lt.backend.model.NetBean
import com.lt.backend.model.UserBean
import com.lt.backend.model.apiFail
import com.lt.backend.model.apiSuccess
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

/**
 * creator: lt  2020/11/11  lt.dygzs@qq.com
 * effect : 测试接口
 * warning:
 */
@RestController
@RequestMapping("/post/")
class PostInterface {

    @PostMapping("/postA")
    fun postA(t: String) = apiSuccess(t)

    @PostMapping("/postB")
    fun postB(name: String) = apiSuccess(UserBean(name, 2, "3"))

    @PostMapping("/postC")
    fun postC(name: String) = apiSuccess(name)

    @PostMapping("/setUserName")
    fun setUserName(lastName: String, firstName: String) = apiSuccess(UserBean("$firstName $lastName", 2, "3"))

    @PostMapping("/postError")
    fun postError(msg: String) = apiFail(msg)

    @PostMapping("/checkHeader")
    fun checkHeader(): NetBean<*> {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?)!!.request
        val aaa = request.getHeader("aaa")
        return if (aaa != "bbb")
            apiFail("请求失败")
        else
            apiSuccess(aaa)
    }
}