package com.lt.lazy_people_http.provider

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

/**
 * creator: lt  2022/10/20  lt.dygzs@qq.com
 * effect : ksp处理程序的创建
 * warning:
 */
internal class LazyPeopleHttpSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        LazyPeopleHttpSymbolProcessor(environment)
}