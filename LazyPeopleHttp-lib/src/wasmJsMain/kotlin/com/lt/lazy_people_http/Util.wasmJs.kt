package com.lt.lazy_people_http

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val Dispatchers.Cache: CoroutineDispatcher
    get() = Dispatchers.Default