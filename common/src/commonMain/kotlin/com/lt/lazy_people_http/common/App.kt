package com.lt.lazy_people_http.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.lt.lazy_people_http.LazyPeopleHttpConfig
import com.lt.lazy_people_http.call.Call
import com.lt.lazy_people_http.call.Callback
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val client = HttpClient {
    defaultRequest {
        //配置baseUrl
        url("http://127.0.0.1:666/")
    }
    install(Logging)
}
private val json = Json { ignoreUnknownKeys = true }
private val config = LazyPeopleHttpConfig(client, json)
private val hf = HttpFunctions::class.createService(config)

var text by mutableStateOf("普通请求")
var text2 by mutableStateOf("封装后的get请求")
var text3 by mutableStateOf("封装后的post请求")
var text4 by mutableStateOf("手动测试全部接口")//全平台测试不支持异步的

@Composable
fun App() {
    Column {
        Button(onClick = {
            GlobalScope.launch {
                getData()
            }
        }) {
            Text(text)
        }

        Button(onClick = {
            getData2()
        }) {
            Text(text2)
        }

        Button(onClick = {
            postData()
        }) {
            Text(text3)
        }
        Button(onClick = {
            testAll()
        }) {
            Text(text4)
        }
    }
}

fun testAll() {
    var i = 0
    fun assert(boolean: Boolean) {
        if (!boolean) throw RuntimeException()
        i++
        text4 = "测试完成:$i"
    }

    suspend fun <T> Call<NetBean<T>>.awaitData() = await().data
    GlobalScope.launch {
        assert(hf.ccc() == 0)
        assert(hf.postA("123").awaitData() == "123")
        assert(hf.postB("1").awaitData().name == "1")
        assert(hf.post_postC("1").awaitData() == "1")
        assert(hf.post_setUserName("1", "4").awaitData().name == "4 1")
        assert(hf.post_postError("error").await().msg == "error")
        assert(hf.post_checkHeader().awaitData() == "bbb")
        assert(hf.getA("bbb").awaitData() == "bbb")
        assert(hf.getB("1").awaitData().name == "1")
        assert(hf.get_getC2("1").awaitData() == "1")
        assert(hf.getD("success").await().code == 200)
        assert(hf.getD("fail").await().code == 400)
        assert(
            hf.get("http://t.weather.sojson.com/api/weather/city/101030100")
                .await().cityInfo?.city == "天津市"
        )
        text4 = "测试完成"
    }
}

suspend fun getData() {
    val response: HttpResponse =
        client.get("http://t.weather.sojson.com/api/weather/city/101030100")
    val data = json.decodeFromString<MData>(response.body())
    text = data.cityInfo?.city ?: "..."
}

fun getData2() {
    hf.get("http://t.weather.sojson.com/api/weather/city/101030100")
        .enqueue(object : Callback<MData> {
            override fun onResponse(call: Call<MData>, response: MData) {
                text2 = response.cityInfo?.city ?: "onResponse"
            }

            override fun onFailure(call: Call<MData>, t: Throwable?) {
                text2 = t?.message ?: "onFailure"
            }
        })
}

fun postData() {
    hf.postA("666").enqueue(object : Callback<NetBean<String>> {
        override fun onResponse(call: Call<NetBean<String>>, response: NetBean<String>) {
            text3 = response.data
        }

        override fun onFailure(call: Call<NetBean<String>>, t: Throwable?) {
            text3 = t?.message ?: "onFailure"
        }
    })
}