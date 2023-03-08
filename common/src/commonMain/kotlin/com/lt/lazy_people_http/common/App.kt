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

@Composable
fun App() {
    var text by remember { mutableStateOf("Hello, World!") }
    suspend fun getData() {
        val client = HttpClient()
        val response: HttpResponse = client.get("https://wanandroid.com/wenda/list/1/json")
        val list = Json.decodeFromString<Data>(response.body())
        text = list.data?.size?.toString() ?: "0"
    }

    Button(onClick = {
        GlobalScope.launch {
            getData()
        }
    }) {
        Text(text)
    }
}
