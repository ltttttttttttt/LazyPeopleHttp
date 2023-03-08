package com.lt.lazy_people_http.common

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

val client = HttpClient()
private val json = Json { ignoreUnknownKeys = true }

@Composable
fun App() {
    var text by remember { mutableStateOf("普通请求") }
    var text2 by remember { mutableStateOf("封装后的请求") }
    suspend fun getData() {
        val response: HttpResponse =
            client.get("http://t.weather.sojson.com/api/weather/city/101030100")
        val data = json.decodeFromString<MData>(response.body())
        text = data.cityInfo?.city ?: "..."
    }
    suspend fun getData2() {
        val response: HttpResponse =
            client.get("http://t.weather.sojson.com/api/weather/city/101030100")
        val data = json.decodeFromString<MData>(response.body())
        text2 = data.cityInfo?.city ?: "..."
    }

    Column {
        Button(onClick = {
            GlobalScope.launch {
                getData()
            }
        }) {
            Text(text)
        }
        Button(onClick = {
            GlobalScope.launch {
                getData2()
            }
        }) {
            Text(text2)
        }
    }
}
