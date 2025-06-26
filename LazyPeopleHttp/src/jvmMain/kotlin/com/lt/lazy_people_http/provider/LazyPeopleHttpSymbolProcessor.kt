package com.lt.lazy_people_http.provider

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate
import com.lt.lazy_people_http.annotations.LazyPeopleHttpService

/**
 * creator: lt  2022/10/20  lt.dygzs@qq.com
 * effect : ksp处理程序
 * warning:
 */
internal class LazyPeopleHttpSymbolProcessor(private val environment: SymbolProcessorEnvironment) :
    SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val ret = mutableListOf<KSAnnotated>()
        resolver.getSymbolsWithAnnotation(LazyPeopleHttpService::class.qualifiedName!!)
            .toList()
            .forEach {
                if (it is KSClassDeclaration) {
                    if (!it.validate()) ret.add(it)
                    else it.accept(LazyPeopleHttpVisitor(environment, resolver), Unit)//处理符号
                }
            }
        //返回无法处理的符号
        return ret
    }
}