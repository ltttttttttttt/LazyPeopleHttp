package com.lt.lazy_people_http.common

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

private val json = Json { ignoreUnknownKeys = true }

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    suspend fun getData() {
        val client = HttpClient()
        val response: HttpResponse =
            client.get("http://t.weather.sojson.com/api/weather/city/101030100")
        val data = json.decodeFromString<MData>(response.body())
        text = data.cityInfo?.city ?: "..."
    }

    Button(onClick = {
        GlobalScope.launch {
            getData()
        }
    }) {
        Text(text)
    }
}
