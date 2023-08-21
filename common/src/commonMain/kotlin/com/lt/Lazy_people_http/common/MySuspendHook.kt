package com.lt.lazy_people_http.common

import com.lt.lazy_people_http.call.Call
import com.lt.lazy_people_http.call.adapter.SuspendHook
import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestInfo
import com.lt.lazy_people_http.type.MyKType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlin.coroutines.coroutineContext

/**
 * creator: lt  2023/6/29  lt.dygzs@qq.com
 * effect : hook suspend方法,使其code=200的时候返回数据,否则或异常则取消协程,并弹出相应toast(懒得实现了)
 * warning:
 */
class MySuspendHook : SuspendHook<Any?> {
    override fun whetherToHook(config: LazyPeopleHttpConfig, info: RequestInfo): Boolean {
        return true
    }

    override suspend fun hook(
        config: LazyPeopleHttpConfig,
        info: RequestInfo,
        call: Call<Any?>,
        callFunction: suspend (LazyPeopleHttpConfig, RequestInfo) -> Any?
    ): Any? {
        try {
            val newReturnType = MyKType(NetBean::class, info.returnType)
            val newInfo = info.copy(returnType = newReturnType)
            val netBean = callFunction(config, newInfo) as NetBean<Any?>
            if (netBean.code == 200) {
                return netBean.data
            }
            netBean.msg.showToast()
            coroutineContext.cancel()
            throw CancellationException("")
        } catch (e: Exception) {
            if (e is CancellationException)
                throw e
            "服务器异常".showToast()
            e.printStackTrace()
            coroutineContext.cancel()
            throw CancellationException("")
        }
    }
}

fun String?.showToast() = println(this)