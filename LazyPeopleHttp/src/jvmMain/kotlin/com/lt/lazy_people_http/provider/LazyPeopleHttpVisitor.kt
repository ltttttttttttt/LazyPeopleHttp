package com.lt.lazy_people_http.provider

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.lt.lazy_people_http.*
import com.lt.lazy_people_http.annotations.*
import com.lt.lazy_people_http.options.*
import com.lt.lazy_people_http.options.ReplaceRule._className
import com.lt.lazy_people_http.options.ReplaceRule._doc
import com.lt.lazy_people_http.options.ReplaceRule._fieldParameter
import com.lt.lazy_people_http.options.ReplaceRule._funParameter
import com.lt.lazy_people_http.options.ReplaceRule._functionAnnotations
import com.lt.lazy_people_http.options.ReplaceRule._functionName
import com.lt.lazy_people_http.options.ReplaceRule._headers
import com.lt.lazy_people_http.options.ReplaceRule._kt
import com.lt.lazy_people_http.options.ReplaceRule._kv
import com.lt.lazy_people_http.options.ReplaceRule._originalClassName
import com.lt.lazy_people_http.options.ReplaceRule._packageName
import com.lt.lazy_people_http.options.ReplaceRule._queryParameter
import com.lt.lazy_people_http.options.ReplaceRule._requestMethod
import com.lt.lazy_people_http.options.ReplaceRule._responseName
import com.lt.lazy_people_http.options.ReplaceRule._returnType
import com.lt.lazy_people_http.options.ReplaceRule._runtimeParameter
import com.lt.lazy_people_http.options.ReplaceRule._type
import com.lt.lazy_people_http.options.ReplaceRule._url
import com.lt.lazy_people_http.options.ReplaceRule._value
import com.lt.lazy_people_http.request.*
import kotlinx.serialization.json.*
import java.io.*

/**
 * creator: lt  2022/10/20  lt.dygzs@qq.com
 * effect : 访问并处理相应符号
 * warning:
 */
internal class LazyPeopleHttpVisitor(
    private val environment: SymbolProcessorEnvironment,
) : KSVisitorVoid() {

    private val options = KspOptions(environment)
    private val isGetFunAnnotations = options.isGetFunAnnotations()
    private val functionReplaceFrom = options.getFunctionReplaceFrom()
    private val functionReplaceTo = options.getFunctionReplaceTo()
    private val isFunctionNameReplace =//是否对方法名进行替换
        functionReplaceFrom.isNotEmpty() && functionReplaceTo.isNotEmpty()
    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        var typeShowPackage = true
    }

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

        val jsonFile = File(options.getCustomizeOutputFile())
        val beans = ArrayList<CustomizeOutputFileBean>()
        if (!jsonFile.exists())
            beans.add(CustomizeOutputFileBeanImpl())
        else
            beans.addAll(json.decodeFromString<List<CustomizeOutputFileBeanImpl>>(jsonFile.readText()))
        beans.forEach { bean ->
            typeShowPackage = bean.typeShowPackage
            val file = environment.codeGenerator.createNewFile(
                Dependencies(
                    true,
                    classDeclaration.containingFile!!
                ),
                packageName,
                bean.fileName._className(className)._originalClassName(originalClassName),
                bean.extensionName,
            )
            writeFile(file, packageName, className, originalClassName, classDeclaration, bean)
            file.close()
        }
    }

    //向文件中写入完整内容
    private fun writeFile(
        file: OutputStream,
        packageName: String,
        className: String,
        originalClassName: String,
        classDeclaration: KSClassDeclaration,
        bean: CustomizeOutputFileBean,
    ) {
        file.appendText(
            bean.fileTopContent._packageName(packageName)._className(className)._originalClassName(originalClassName)
        )
        writeFunction(file, classDeclaration, bean)
        file.appendText(
            bean.fileBottomContent._className(className)._originalClassName(originalClassName)
        )
    }

    //向文件中写入变换后的函数
    private fun writeFunction(file: OutputStream, classDeclaration: KSClassDeclaration, bean: CustomizeOutputFileBean) {
        classDeclaration.superTypes.mapNotNull {
            it.resolve().declaration as? KSClassDeclaration
        }.forEach {
            writeFunction(file, it, bean)
        }
        classDeclaration.getDeclaredFunctions().filter {
            it.isAbstract
        }.forEach {
            val functionName = it.simpleName.asString()
            val methodInfo = getMethodInfo(it, functionName, classDeclaration)
            //返回的全类型
            val returnType = getKSTypeInfo(it.returnType!!).toString()
            val isSuspendFun = Modifier.SUSPEND in it.modifiers
            //返回的最外层的类型
            val responseName = if (isSuspendFun)
                null
            else {
                val responseType = getKSTypeOutermostName(it.returnType!!)
                if (responseType == "com.lt.lazy_people_http.call.Call") null
                else "\"$responseType\""
            }
            val typeOf =
                if (isSuspendFun) returnType else getKSTypeArguments(it.returnType!!).first()
            val funBean =
                if (!isSuspendFun || bean.suspendFunEqualsFunContent) bean.funContent else bean.suspendFunContent
            val headers = getHeaders(it, funBean.header)
            val parameterInfo =
                getParameters(it, methodInfo.method, funBean)
            var url = methodInfo.url
            parameterInfo.replaceUrlFunction?.forEach {
                url = url.replace(it.key, funBean.replaceUrlName._value(it.value))
            }
            val functionAnnotations = getFunctionAnnotations(it)

            val funContent = funBean.content
                ._functionName(functionName)
                ._funParameter(parameterInfo.funParameter)
                ._returnType(returnType)
                ._url(url)
                ._queryParameter(parameterInfo.queryParameter)
                ._fieldParameter(parameterInfo.fieldParameter)
                ._runtimeParameter(parameterInfo.runtimeParameter)
                ._type(typeOf)
                ._requestMethod(if (methodInfo.method == null) "null" else "RequestMethod.${methodInfo.method}")
                ._headers(headers)
                ._functionAnnotations(if (functionAnnotations.isEmpty()) "null" else "arrayOf($functionAnnotations)")
                ._responseName(responseName.toString())
                ._doc(it.docString?.trim() ?: "")
            file.appendText(funContent)
        }
    }

    //获取方法的参数和请求参数
    private fun getParameters(it: KSFunctionDeclaration, method: RequestMethod?, funBean: FunctionBean): ParameterInfo {
        //如果没有参数
        if (it.parameters.isEmpty()) return ParameterInfo(
            "",
            funBean.parameter.emptyValue,
            funBean.parameter.emptyValue,
            funBean.parameter.emptyValue,
            null
        )
        //有参数的话就将参数拆为:方法参数,query参数,field参数,和只有运行时才能处理的参数
        val funPList = ArrayList<String>()
        val queryPList = ArrayList<String>()
        val fieldPList = ArrayList<String>()
        val runtimePList = ArrayList<String>()
        val replaceUrlMap = HashMap<String, String>()
        it.parameters.forEach {
            val funPName = it.name!!.asString()
            val type = getKSTypeInfo(it.type).toString()
            funPList.add(funBean.funParameterKT._kt(funPName,type))
            getParameterInfo(it, funPName, queryPList, fieldPList, runtimePList, replaceUrlMap, funBean.parameter)
        }
        //处理方法加了注解,但参数没加注解的情况
        when (method) {
            RequestMethod.GET_QUERY -> {
                queryPList.addAll(runtimePList)
                runtimePList.clear()
            }

            RequestMethod.POST_FIELD -> {
                fieldPList.addAll(runtimePList)
                runtimePList.clear()
            }

            else -> {}
        }
        //将所有参数拼接成代码
        return ParameterInfo(
            if (funPList.isEmpty()) "" else funPList.joinToString(),
            if (queryPList.isEmpty()) funBean.parameter.emptyValue else queryPList.joinToString(
                prefix = funBean.parameter.arrayStart,
                postfix = funBean.parameter.arrayEnd
            ),
            if (fieldPList.isEmpty()) funBean.parameter.emptyValue else fieldPList.joinToString(
                prefix = funBean.parameter.arrayStart,
                postfix = funBean.parameter.arrayEnd
            ),
            if (runtimePList.isEmpty()) funBean.parameter.emptyValue else runtimePList.joinToString(
                prefix = funBean.parameter.arrayStart,
                postfix = funBean.parameter.arrayEnd
            ),
            replaceUrlMap,
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
        replaceUrlMap: HashMap<String, String>,
        parameter: ParameterBean,
    ) {
        val list =
            (it.getAnnotationsByType(Query::class)
                    + it.getAnnotationsByType(QueryMap::class)
                    + it.getAnnotationsByType(Field::class)
                    + it.getAnnotationsByType(FieldMap::class)
                    + it.getAnnotationsByType(Url::class)
                    ).toList()
        if (list.isEmpty()) {
            runtimePList.add(parameter.keyValue._kv(funPName, funPName))
            return
        }
        when (val annotation = list.first()) {
            is Query -> queryPList.add(parameter.keyValue._kv(annotation.name, funPName))
            is QueryMap -> {
                checkMapType(it, funPName)
                queryPList.add(parameter.mapValue._value(funPName))
            }

            is Field -> fieldPList.add(parameter.keyValue._kv(annotation.name, funPName))
            is FieldMap -> {
                checkMapType(it, funPName)
                fieldPList.add(parameter.mapValue._value(funPName))
            }

            is Url -> replaceUrlMap["{${annotation.replaceUrlName}}"] = funPName
            else -> throw RuntimeException("There is a problem with the getParameterInfo function")
        }
    }

    //校验map的类型和泛型
    private fun checkMapType(
        it: KSValueParameter,
        funPName: String
    ) {
        val ksType = it.type.resolve()
        //如果类型不是Map则报错
        if (ksType.nullability == Nullability.NULLABLE)
            environment.logger.error(
                "FieldMap annotation only supports Map type: $funPName",
                it
            )
        val ksTypeName = ksType.declaration.qualifiedName?.asString()
            ?: "${ksType.declaration.packageName.asString()}.${ksType.declaration.simpleName.asString()}"
        when (ksTypeName) {
            "kotlin.collections.Map", "kotlin.collections.MutableMap", "kotlin.collections.HashMap", "kotlin.collections.LinkedHashMap" -> {}
            else -> {
                if (!Map::class.java.isAssignableFrom(Class.forName(ksTypeName)))
                    environment.logger.error(
                        "FieldMap annotation only supports Map type: $funPName",
                        it
                    )
            }
        }
        //如果泛型不是String和String(?)则报错
        ksType.arguments[0].type?.resolve()!!.let { type ->
            if (type.nullability == Nullability.NULLABLE)
                environment.logger.error(
                    "The generic type of the Map key annotated by FieldMap only supports String: $funPName",
                    it
                )
            if (type.declaration.simpleName.asString() != "String")
                environment.logger.error(
                    "The generic type of the Map key annotated by FieldMap only supports String: $funPName",
                    it
                )
        }
        ksType.arguments[1].type?.resolve()!!.let { type ->
            if (type.declaration.simpleName.asString() != "String")
                environment.logger.error(
                    "The generic type of the Map value annotated by FieldMap only supports String: $funPName",
                    it
                )
        }
    }

    //获取请求头的内容
    @OptIn(KspExperimental::class)
    private fun getHeaders(it: KSFunctionDeclaration, header: ParameterBean): String {
        val list = it.getAnnotationsByType(Header::class).toList()
        if (list.isEmpty()) return header.emptyValue
        return list.joinToString(
            prefix = header.arrayStart,
            postfix = header.arrayEnd
        ) { "\"${it.name}\", \"${it.value}\"" }
    }

    //获取函数的请求方法相关数据
    @OptIn(KspExperimental::class)
    private fun getMethodInfo(
        it: KSFunctionDeclaration,
        functionName: String,
        classDeclaration: KSClassDeclaration
    ): MethodInfo {
        val urlMidSegment =
            classDeclaration.getAnnotationsByType(UrlMidSegment::class).firstOrNull()?.url ?: ""
        val list =
            (it.getAnnotationsByType(GET::class) + it.getAnnotationsByType(POST::class)).toList()
        if (list.isEmpty())
            return MethodInfo(null, montageUrl(urlMidSegment, functionName.let {
                if (isFunctionNameReplace)
                    it.replace(functionReplaceFrom, functionReplaceTo)
                else
                    it
            }))
        if (list.size > 1)
            throw RuntimeException("Function $functionName there are multiple http method annotations")
        return when (val annotation = list.first()) {
            is GET -> MethodInfo(RequestMethod.GET_QUERY, montageUrl(urlMidSegment, annotation.url))
            is POST -> MethodInfo(RequestMethod.POST_FIELD, montageUrl(urlMidSegment, annotation.url))
            else -> throw RuntimeException("There is a problem with the getMethodInfo function")
        }
    }

    //获取方法和其参数以及返回值上的注解(不包含Type的注解)
    private fun getFunctionAnnotations(it: KSFunctionDeclaration): String {
        if (!isGetFunAnnotations) return ""
        val annotations = StringBuilder()
        it.annotations.forEach {
            annotations.append(getNewAnnotationString(it))
                .append(", ")
        }
        it.parameters.forEach {
            it.annotations.forEach {
                annotations.append(getNewAnnotationString(it))
                    .append(", ")
            }
        }
        it.returnType?.annotations?.forEach {
            annotations.append(getNewAnnotationString(it))
                .append(", ")
        }
        return annotations.toString()
    }
}