package com.lt.lazy_people_http

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal actual val Dispatchers.Cache: CoroutineDispatcher
    get() = Dispatchers.IO