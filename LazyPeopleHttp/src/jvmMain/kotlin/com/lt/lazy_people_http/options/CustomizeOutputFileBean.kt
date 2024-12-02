package com.lt.lazy_people_http.options

import kotlinx.serialization.*

/**
 * creator: lt  2024/11/25  lt.dygzs@qq.com
 * effect : 自定义输出文件配置
 * warning: 属性替换规则参考[ReplaceRule]
 */
interface CustomizeOutputFileBean {
    //生成的文件名
    val fileName: String

    //生成文件的扩展名
    val extensionName: String

    //文件顶部内容(导包,类声明等)
    val fileTopContent: String

    //文件底部内容(类结尾括号,扩展方法等)
    val fileBottomContent: String

    //suspend方法内容(方法的声明和方法体)
    val suspendFunContent: FunctionBean

    //方法内容(方法的声明和方法体)
    val funContent: FunctionBean

    //[suspendFunContent]等同于[funContent]
    val suspendFunEqualsFunContent: Boolean

    //类型显示
    val typeContent: String

    //输出目录
    val outputDir: String

    //可空的type声明
    val nullabilityType: String
}

/**
 * 属性替换规则
 */
object ReplaceRule {
    /*类级别*/
    fun String._packageName(packageName: String) = replace("##packageName##", packageName)//包名
    fun String._className(className: String) = replace("##className##", className)//生成后的类名
    fun String._originalClassName(originalClassName: String) = replace("##originalClassName##", originalClassName)//原类名

    /*方法级*/
    fun String._functionName(functionName: String) = replace("##functionName##", functionName)//方法名
    fun String._funParameter(funParameter: String) = replace("##funParameter##", funParameter)//方法参数及类型
    fun String._returnType(returnType: String) = replace("##returnType##", returnType)//方法返回值类型
    fun String._url(url: String) = replace("##url##", url)//请求url
    fun String._queryParameter(queryParameter: String) = replace("##queryParameter##", queryParameter)//query请求参数
    fun String._fieldParameter(fieldParameter: String) = replace("##fieldParameter##", fieldParameter)//field请求参数
    fun String._runtimeParameter(runtimeParameter: String) =
        replace("##runtimeParameter##", runtimeParameter)//运行时设置的请求参数

    fun String._type(type: String) = replace("##type##", type)//需要被解析的类型,如果是suspend方法则与returnType一致
    fun String._typeChild(typeChild: String) = replace("##typeChild##", typeChild)//需要被解析的类型中的泛型
    fun String._requestMethod(requestMethod: String) = replace("##requestMethod##", requestMethod)//网络请求使用的方法
    fun String._headers(headers: String) = replace("##headers##", headers)//注解中声明的请求头
    fun String._functionAnnotations(functionAnnotations: String) =
        replace("##functionAnnotations##", functionAnnotations)//方法上所有声明的注解

    fun String._responseName(responseName: String) = replace("##responseName##", responseName)//自定义的返回值类型,比如Flow
    fun String._doc(doc: String) = replace("##doc##", doc)//方法注释

    /*参数级*/
    fun String._kt(key: String, type: String) = replace("##key##", key).replace("##type##", type)//参数及类型
    fun String._kv(key: String, value: String) = replace("##key##", key).replace("##value##", value)//参数名及参数值
    fun String._value(value: String) = replace("##value##", value)//参数值

}

@Serializable
class CustomizeOutputFileBeanImpl(
    override val fileName: String = "##className##",
    override val extensionName: String = "kt",
    override val fileTopContent: String = "package ##packageName##\n" +
            "\n" +
            "import com.lt.lazy_people_http._lazyPeopleHttpFlatten\n" +
            "import com.lt.lazy_people_http.call.Call\n" +
            "import com.lt.lazy_people_http.call.CallCreator\n" +
            "import com.lt.lazy_people_http.config.LazyPeopleHttpConfig\n" +
            "import com.lt.lazy_people_http.request.RequestMethod\n" +
            "import com.lt.lazy_people_http.service.HttpServiceImpl\n" +
            "import kotlin.reflect.typeOf\n" +
            "\n" +
            "class ##className##(\n" +
            "    val config: LazyPeopleHttpConfig,\n" +
            ") : ##originalClassName##, HttpServiceImpl {\n\n",
    override val fileBottomContent: String =
        "    private inline fun <reified T> T?._toJson() = CallCreator.parameterToJson(config, this)\n" +
                "}\n\n" +
            "fun kotlin.reflect.KClass<##originalClassName##>.createService(config: LazyPeopleHttpConfig): ##originalClassName## =\n" +
            "    ##className##(config)",
    override val suspendFunContent: FunctionBean = FunctionBean(
        "    override suspend fun ##functionName##(##funParameter##): ##returnType## {\n" +
            "        return CallCreator.createResponse<Call<##returnType##>>(\n" +
            "            config,\n" +
            "            \"##url##\",\n" +
            "            ##queryParameter##,\n" +
            "            ##fieldParameter##,\n" +
            "            ##runtimeParameter##,\n" +
            "            typeOf<##type##>(),\n" +
            "            ##requestMethod##,\n" +
            "            ##headers##,\n" +
            "            ##functionAnnotations##,\n" +
            "            ##responseName##,\n" +
            "        ).await()\n" +
            "    }\n\n",
    ),
    override val funContent: FunctionBean = FunctionBean(
        "    override fun ##functionName##(##funParameter##): ##returnType## {\n" +
            "        return CallCreator.createResponse(\n" +
            "            config,\n" +
            "            \"##url##\",\n" +
            "            ##queryParameter##,\n" +
            "            ##fieldParameter##,\n" +
            "            ##runtimeParameter##,\n" +
            "            typeOf<##type##>(),\n" +
            "            ##requestMethod##,\n" +
            "            ##headers##,\n" +
            "            ##functionAnnotations##,\n" +
            "            ##responseName##,\n" +
            "        )\n" +
            "    }\n\n",
    ),
    override val suspendFunEqualsFunContent: Boolean = false,
    override val typeContent: String = "##packageName##.##type##",
    override val outputDir: String = "",
    override val nullabilityType: String = "##type##?",
) : CustomizeOutputFileBean

/**
 * 方法内容
 */
@Serializable
class FunctionBean(
    //方法内容
    val content: String,
    //方法参数及类型
    val funParameterKT: String = "##key##: ##type##",
    //参数
    val parameter: ParameterBean = ParameterBean(),
    //请求头
    val header: ParameterBean = ParameterBean(),
    //替换请求url中的指定字段
    val replaceUrlName: String = "\$##value##",
)

/**
 * 参数内容
 */
@Serializable
class ParameterBean(
    //参数列表为空时
    val emptyValue: String = "null",
    //参数列表开头
    val arrayStart: String = "arrayOf(",
    //参数列表结尾
    val arrayEnd: String = ")",
    //kv形式的参数
    val keyValue: String = "\"##key##\", ##value##._toJson()",
    //map形式的参数
    val mapValue: String = "*##value##._lazyPeopleHttpFlatten()",
)