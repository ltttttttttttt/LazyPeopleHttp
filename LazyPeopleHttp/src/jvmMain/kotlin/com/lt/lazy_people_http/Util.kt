package com.lt.lazy_people_http

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Nullability
import com.lt.lazy_people_http.options.KSTypeInfo
import java.io.OutputStream

/**
 * creator: lt  2022/10/21  lt.dygzs@qq.comS
 * effect : 工具类
 * warning:
 */

/**
 * 向os中写入文字
 */
internal fun OutputStream.appendText(str: String) {
    this.write(str.toByteArray())
}

/**
 * 如果字符串为空或长度为0,就使用lambda中的字符串
 */
internal inline fun String?.ifNullOfEmpty(defaultValue: () -> String): String =
    if (this.isNullOrEmpty()) defaultValue() else this

/**
 * 打印日志
 */
internal fun String?.w(environment: SymbolProcessorEnvironment) {
    environment.logger.warn("lllttt VirtualReflection: ${this ?: "空字符串"}")
}

/**
 * 获取ksType的完整泛型信息,返回可直接使用的String
 * [ks] KSTypeReference信息
 */
internal fun getKSTypeInfo(ks: KSTypeReference): KSTypeInfo {
    //type对象
    val ksType = ks.resolve()
    val typeString = if (ksType.arguments.isEmpty()) "" else {
        //有泛型
        ksType.arguments.filter { it.type != null }.joinToString(prefix = "<", postfix = ">") {
            getKSTypeInfo(it.type!!).toString()
        }
    }
    //完整type字符串
    val typeName =
        "${ksType.declaration.packageName.asString()}.${ksType.declaration.simpleName.asString()}"
    //是否可空
    val nullable = if (ksType.nullability == Nullability.NULLABLE) "?" else ""
    return KSTypeInfo(
        //自身或泛型包含Buff注解
        typeName,
        nullable,
        typeString
    )
}

/**
 * 通过[KSAnnotation]获取还原这个注解的String
 */
fun getNewAnnotationString(ksa: KSAnnotation): String {
    val ksType = ksa.annotationType.resolve()
    //完整type字符串
    val typeName =
        "${ksType.declaration.packageName.asString()}.${ksType.declaration.simpleName.asString()}"
    val args = StringBuilder()
    ksa.arguments.forEach {
        val name = it.name
        if (name != null)
            args.append(name.asString())
                .append(" = \"")
        args.append(it.value)
            .append("\", ")
    }
    return "$typeName($args)"
}