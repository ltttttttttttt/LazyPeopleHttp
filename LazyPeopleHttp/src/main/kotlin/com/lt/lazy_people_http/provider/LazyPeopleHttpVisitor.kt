package com.lt.lazy_people_http.provider

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.*
import com.lt.lazy_people_http.annotations.*
import com.lt.lazy_people_http.appendText
import com.lt.lazy_people_http.getKSTypeInfo
import com.lt.lazy_people_http.options.MethodInfo
import com.lt.lazy_people_http.options.ParameterInfo
import java.io.OutputStream

/**
 * creator: lt  2022/10/20  lt.dygzs@qq.com
 * effect : 访问并处理相应符号
 * warning:
 */
internal class LazyPeopleHttpVisitor(
    private val environment: SymbolProcessorEnvironment,
) : KSVisitorVoid() {

    /**
     * 访问class的声明
     */
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        //获取class信息并创建kt文件
        val packageName = classDeclaration.packageName.asString()
        val originalClassName = classDeclaration.simpleName.asString()
        val className = "_${originalClassName}ServiceImpl"
        if (classDeclaration.classKind != ClassKind.INTERFACE)
            throw RuntimeException("LazyPeopleHttpService can only decorate interface")
        val file = environment.codeGenerator.createNewFile(
            Dependencies(
                true,
                classDeclaration.containingFile!!
            ), packageName, className
        )
        writeFile(file, packageName, className, originalClassName, classDeclaration)
        file.close()
    }

    //向文件中写入完整内容
    private fun writeFile(
        file: OutputStream,
        packageName: String,
        className: String,
        originalClassName: String,
        classDeclaration: KSClassDeclaration
    ) {
        file.appendText(
            "package $packageName\n" +
                    "\n" +
                    "import com.lt.lazy_people_http.LazyPeopleHttpConfig\n" +
                    "import com.lt.lazy_people_http.call.CallAdapter\n" +
                    "import com.lt.lazy_people_http.request.RequestMethod\n" +
                    "import com.lt.lazy_people_http.service.HttpServiceImpl\n" +
                    "import kotlin.reflect.typeOf\n" +
                    "\n" +
                    "class $className(\n" +
                    "    val config: LazyPeopleHttpConfig,\n" +
                    ") : $originalClassName, HttpServiceImpl {\n" +
                    "    fun Any?.asString() = CallAdapter.parameterToJson(config, this)\n\n"
        )
        writeFunction(file, classDeclaration)
        file.appendText(
            "}\n\n" +
                    "fun kotlin.reflect.KClass<$originalClassName>.createService(config: LazyPeopleHttpConfig): $originalClassName =\n" +
                    "    $className(config)"
        )
    }

    //向文件中写入变换后的函数
    private fun writeFunction(file: OutputStream, classDeclaration: KSClassDeclaration) {
        classDeclaration.getAllFunctions().filter {
            it.isAbstract
        }.forEach {
            val functionName = it.simpleName.asString()
            val methodInfo = getMethodInfo(it, functionName)
            val returnType = getKSTypeInfo(it.returnType!!).toString()
            val typeOf =
                getKSTypeInfo(it.returnType!!.element!!.typeArguments.first().type!!).toString()
            val headers = getHeaders(it)
            val parameterInfo = getParameters(it, methodInfo.method)
            file.appendText(
                "    override fun $functionName(${parameterInfo.funParameter}): $returnType = CallAdapter.createCall(\n" +
                        "        config,\n" +
                        "        \"${methodInfo.url}\"${parameterInfo.replaceUrlFunction},\n" +
                        "        ${parameterInfo.queryParameter},\n" +
                        "        ${parameterInfo.fieldParameter},\n" +
                        "        ${parameterInfo.runtimeParameter},\n" +
                        "        typeOf<$typeOf>(),\n" +
                        "        ${methodInfo.method},\n" +
                        "        $headers,\n" +
                        "    )\n\n"
            )
        }
    }

    //获取方法的参数和请求参数
    private fun getParameters(it: KSFunctionDeclaration, method: String): ParameterInfo {
        //如果没有参数
        if (it.parameters.isEmpty()) return ParameterInfo("", "null", "null", "null", "")
        //有参数的话就将参数拆为:方法参数,query参数,field参数,和只有运行时才能处理的参数
        val funPList = ArrayList<String>()
        val queryPList = ArrayList<String>()
        val fieldPList = ArrayList<String>()
        val runtimePList = ArrayList<String>()
        val replaceUrlFList = ArrayList<String>()
        it.parameters.forEach {
            val funPName = it.name!!.asString()
            val type = getKSTypeInfo(it.type).toString()
            funPList.add("$funPName: $type")
            getParameterInfo(it, funPName, queryPList, fieldPList, runtimePList, replaceUrlFList)
        }
        //处理方法加了注解,但参数没加注解的情况
        if (method == "RequestMethod.GET") {
            queryPList.addAll(runtimePList)
            runtimePList.clear()
        } else if (method == "RequestMethod.POST") {
            fieldPList.addAll(runtimePList)
            runtimePList.clear()
        }
        //将所有参数拼接成代码
        return ParameterInfo(
            if (funPList.isEmpty()) "" else funPList.joinToString(),
            if (queryPList.isEmpty()) "null" else queryPList.joinToString(
                prefix = "mapOf(",
                postfix = ")"
            ),
            if (fieldPList.isEmpty()) "null" else fieldPList.joinToString(
                prefix = "mapOf(",
                postfix = ")"
            ),
            if (runtimePList.isEmpty()) "null" else runtimePList.joinToString(
                prefix = "mapOf(",
                postfix = ")"
            ),
            if (replaceUrlFList.isEmpty()) "" else replaceUrlFList.joinToString("")
        )
    }

    //获取参数设置的参数的内容
    @OptIn(KspExperimental::class)
    private fun getParameterInfo(
        it: KSValueParameter,
        funPName: String,
        queryPList: ArrayList<String>,
        fieldPList: ArrayList<String>,
        runtimePList: ArrayList<String>,
        replaceUrlFList: ArrayList<String>
    ) {
        val list =
            (it.getAnnotationsByType(Query::class)
                    + it.getAnnotationsByType(Field::class)
                    + it.getAnnotationsByType(Url::class)
                    ).toList()
        if (list.isEmpty()) {
            runtimePList.add("\"$funPName\" to $funPName.asString()")
            return
        }
        when (val annotation = list.first()) {
            is Query -> queryPList.add("\"${annotation.name}\" to $funPName.asString()")
            is Field -> fieldPList.add("\"${annotation.name}\" to $funPName.asString()")
            is Url -> replaceUrlFList.add(".replace(\"{${annotation.replaceUrlName}}\", $funPName)")
            else -> throw RuntimeException("There is a problem with the getParameterInfo function")
        }
    }

    //获取请求头的内容
    @OptIn(KspExperimental::class)
    private fun getHeaders(it: KSFunctionDeclaration): String {
        val list = it.getAnnotationsByType(Header::class).toList()
        if (list.isEmpty()) return "null"
        return list.joinToString(
            prefix = "mapOf(",
            postfix = ")"
        ) { "\"${it.name}\" to \"${it.value}\"" }
    }

    //获取函数的请求方法相关数据
    @OptIn(KspExperimental::class)
    private fun getMethodInfo(it: KSFunctionDeclaration, functionName: String): MethodInfo {
        val list =
            (it.getAnnotationsByType(GET::class) + it.getAnnotationsByType(POST::class)).toList()
        if (list.isEmpty())
            return MethodInfo("null", functionName.replace("_", "/"))
        if (list.size > 1)
            throw RuntimeException("Function $functionName there are multiple http method annotations")
        return when (val annotation = list.first()) {
            is GET -> MethodInfo("RequestMethod.GET", annotation.url)
            is POST -> MethodInfo("RequestMethod.POST", annotation.url)
            else -> throw RuntimeException("There is a problem with the getMethodInfo function")
        }
    }
}