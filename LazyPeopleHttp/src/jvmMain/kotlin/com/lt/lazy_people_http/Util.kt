package com.lt.lazy_people_http

import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import com.lt.ksp.model.KSTypeInfo
import com.lt.ksp.model.TypeName
import com.lt.lazy_people_http.options.ReplaceRule._packageName
import com.lt.lazy_people_http.options.ReplaceRule._type
import com.lt.lazy_people_http.provider.LazyPeopleHttpVisitor
import org.jetbrains.kotlin.js.descriptorUtils.getKotlinTypeFqName
import org.jetbrains.kotlin.types.KotlinType

/**
 * creator: lt  2022/10/21  lt.dygzs@qq.comS
 * effect : 工具类
 * warning:
 */

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

internal fun KSTypeInfo.asString(): String {
    val info = this
    val typeString = info.thisTypeName.asString()
    val childTypeString =
        if (info.childType.isEmpty()) "" else info.childType.joinToString(prefix = "<", postfix = ">") {
            it.asString()
        }
    val nullableString = if (info.nullable) LazyPeopleHttpVisitor.nullabilityType else ""
    val type = typeString + childTypeString
    return if (nullableString.isEmpty())
        type
    else
        nullableString._type(type)
}

internal fun TypeName.asString(): String {
    return LazyPeopleHttpVisitor.typeContent
        ._packageName(packageName.asString())
        ._type(simpleName.asString())
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
 * 查找获取lambda中的数据,如果不为空则停止遍历
 */
inline fun <T, R : Any> Iterable<T>.findBy(find: (T) -> R?): R? {
    var r: R? = null
    for (t in this) {
        r = find(t)
        if (r != null)
            break
    }
    return r
}