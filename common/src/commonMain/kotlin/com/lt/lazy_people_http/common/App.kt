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
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val client = HttpClient {
    install(Logging)
}
private val json = Json { ignoreUnknownKeys = true }
private val config = LazyPeopleHttpConfig(client, json)
private val hf = HttpFunctions::class.createService(config)

var text by mutableStateOf("普通请求")
var text2 by mutableStateOf("封装后的get请求")
var text3 by mutableStateOf("封装后的post请求")

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