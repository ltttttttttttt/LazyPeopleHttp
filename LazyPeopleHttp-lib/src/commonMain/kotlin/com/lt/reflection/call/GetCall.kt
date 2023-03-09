package com.lt.reflection.call

import com.lt.reflection.LazyPeopleHttpConfig
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.serializer
import kotlin.reflect.KType

/**
 * creator: lt  2023/3/9  lt.dygzs@qq.com
 * effect : 使用GlobalScope作用域的协程进行get请求
 * warning:
 */
class GetCall<T>(
    val config: LazyPeopleHttpConfig,
    val url: String?,
    val parameter: Map<String?, String?>,
    val returnType: KType,
) : Call<T> {
    override fun enqueue(callback: Callback<T>,scope: CoroutineScope) {
        scope.launch {
            try {
                val response: HttpResponse =
                    config.client.get(url ?: "") {
                        parameter.forEach {
                            parameter(it.key ?: "", it.value ?: "")
                        }
                    }
                val data = config.json.decodeFromString(
                    config.json.serializersModule.serializer(returnType),
                    response.body()
                ) as T
                callback.onResponse(this@GetCall, data)
            } catch (e: Exception) {
                if (e is CancellationException)
                    throw e
                callback.onFailure(this@GetCall, e)
            }
        }
    }
}