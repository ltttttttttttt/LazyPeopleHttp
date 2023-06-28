package com.lt.lazy_people_http.call.adapter

import com.lt.lazy_people_http.call.CallCreator
import com.lt.lazy_people_http.config.LazyPeopleHttpConfig
import com.lt.lazy_people_http.request.RequestInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * creator: lt  2023/6/28  lt.dygzs@qq.com
 * effect : 构造[Flow]类型的返回值对象
 * warning:
 */
class FlowCallAdapter : CallAdapter<Flow<*>> {
    override val responseNames: Array<String> =
        arrayOf("kotlinx.coroutines.flow.Flow")

    override fun adapt(config: LazyPeopleHttpConfig, info: RequestInfo): Flow<*> {
        return flow {
            try {
                emit(
                    CallCreator.createCall<Any?>(config, info).await()
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}