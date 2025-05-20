package com.lt.lazy_people_http

import com.google.devtools.ksp.findActualType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.lt.lazy_people_http.options.*
import com.lt.lazy_people_http.options.ReplaceRule._packageName
import com.lt.lazy_people_http.options.ReplaceRule._type
import com.lt.lazy_people_http.provider.*
import org.jetbrains.kotlin.js.descriptorUtils.*
import org.jetbrains.kotlin.types.*
import java.io.*

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
    environment.logger.warn("lllttt LazyPeopleHttp: ${this ?: "空字符串"}")
}

/**
 * 获取ksType的完整泛型信息,返回可直接使用的String
 * [ks] KSTypeReference信息
 */
internal fun getKSTypeInfo(ks: KSTypeReference): KSTypeInfo {
    //type对象
    val ksType = ks.resolve()
    val arguments = ksType.arguments
    val childTypeString = if (arguments.isEmpty()) "" else {
        //有泛型
        arguments.filter { it.type != null }.joinToString(prefix = "<", postfix = ">") {
            getKSTypeInfo(it.type!!).toString()
        }
    }
    //完整type字符串
    val thisTypeName =
        ksType.declaration.let {
//            it.qualifiedName?.asString()?: "${it.packageName.asString()}.${it.simpleName.asString()}"
            LazyPeopleHttpVisitor.typeContent
                ._packageName(it.packageName.asString())
                ._type(it.simpleName.asString())
        }
    //是否可空
    val nullable = if (ksType.nullability == Nullability.NULLABLE) LazyPeopleHttpVisitor.nullabilityType else ""
    return KSTypeInfo(
        //自身或泛型包含Buff注解
        thisTypeName,
        nullable,
        childTypeString
    )
}


private val getKotlinTypeMethod =
    Class.forName("com.google.devtools.ksp.symbol.impl.kotlin.KSTypeImpl")
        .getMethod("getKotlinType")

/**
 * 获取ksType的完整子泛型信息列表,返回可直接使用的String
 * 可以自动判断是否是typealias类型并获取其中的真实类型
 * [ks] KSTypeReference信息
 * 参考: https://github.com/google/ksp/issues/1371 方案C
 */
internal fun getKSTypeArguments(ks: KSTypeReference): List<String> {
    //type对象
    val ksType = ks.resolve()
    //如果是typealias类型
    return if (ksType.declaration is KSTypeAlias) {
        // TODO by lt test KSType.expand https://github.com/google/ksp/issues/1371
        val kotlinType = getKotlinTypeMethod.invoke(ksType) as KotlinType
        kotlinType.arguments.map {
            getKotlinTypeInfo(it.type)
        }
    } else {
        ks.element?.typeArguments?.map {
            getKSTypeInfo(it.type!!).toString()
        } ?: listOf()
    }
}

/**
 * 获取传入的type的最外层类的全类名
 * 比如Call<String>,获取到Call的全类名
 */
internal fun getKSTypeOutermostName(ks: KSTypeReference): String {
    //type对象
    val ksType = ks.resolve()
    //如果是typealias类型
    return if (ksType.declaration is KSTypeAlias) {
        (ksType.declaration as KSTypeAlias).findActualType()
    } else {
        ksType.declaration
    }.let {
        it.qualifiedName?.asString()
            ?: "${it.packageName.asString()}.${it.simpleName.asString()}"
    }
}

/**
 * 通过[KSAnnotation]获取还原(构造)这个注解的String
 */
internal fun getNewAnnotationString(ksa: KSAnnotation): String {
    val ksType = ksa.annotationType.resolve()
    //完整type字符串
    val typeName = ksType.declaration.let {
        //todo bug? <ERROR TYPE: xxx>
        return@let if (ksType.isError)
            ksa.annotationType.toString()
        else
            LazyPeopleHttpVisitor.typeContent
                ._packageName(it.packageName.asString())
                ._type(it.simpleName.asString())
    }
    val args = StringBuilder()
    ksa.arguments.forEach {
        val value = it.value
        if (value != null) {
            val name = it.name
            if (name != null)
                args.append(name.asString())
                    .append(" = ")
            fun appendValue(value: Any?) {
                when (value) {
                    is String -> {
                        args.append("\"")
                            .append(value)
                            .append("\"")
                    }

                    is List<*> -> {
                        args.append("arrayOf(")
                        value.forEach(::appendValue)
                        args.append(")")
                    }

                    is KSType -> args.append(value).append("::class")
                    null -> args.append("null")
                    else -> args.append(value)
                }
                args.append(", ")
            }
            appendValue(value)
        }
    }
    return "$typeName($args)"
}

//通过[KotlinType]获取完整的泛型信息
private fun getKotlinTypeInfo(type: KotlinType): String {
    var typeString = type.getKotlinTypeFqName(false)
    val lastIndex = typeString.lastIndexOf(".")
    if (lastIndex > 0) {
        typeString = LazyPeopleHttpVisitor.typeContent
            ._packageName(typeString.substring(0, lastIndex))
            ._type(typeString.substring(lastIndex + 1))
    }
    val arguments = type.arguments
    if (arguments.isEmpty())
        return typeString
    val argTypeString = arguments.joinToString(prefix = "<", postfix = ">") {
        getKotlinTypeInfo(it.type)
    }
    return typeString + argTypeString
}

/**
 * 拼接url,自动处理中间的'/'
 */
internal fun montageUrl(url1: String, url2: String): String {
    if (url1.isEmpty())
        return url2
    if (url2.isEmpty())
        return url1
    val sb = StringBuilder()
    if (url1.endsWith("/"))
        sb.append(url1.substring(0, url1.length - 1))
    else
        sb.append(url1)
    sb.append("/")
    if (url2.startsWith("/"))
        sb.append(url2.substring(1, url2.length))
    else
        sb.append(url2)
    return sb.toString()
}

/**
 * 获取字符串type的子type,如果没有返回自身
 */
internal fun getTypeChild(type: String): String {
    if (!type.contains("<"))
        return type
    return type.substring(type.indexOf("<") + 1, type.lastIndexOf(">"))
}