package com.lt.lazy_people_http.provider

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Nullability
import com.lt.lazy_people_http.annotations.Field
import com.lt.lazy_people_http.annotations.FieldMap
import com.lt.lazy_people_http.annotations.GET
import com.lt.lazy_people_http.annotations.Header
import com.lt.lazy_people_http.annotations.POST
import com.lt.lazy_people_http.annotations.Query
import com.lt.lazy_people_http.annotations.QueryMap
import com.lt.lazy_people_http.annotations.Url
import com.lt.lazy_people_http.annotations.UrlMidSegment
import com.lt.lazy_people_http.appendText
import com.lt.lazy_people_http.getKSTypeArguments
import com.lt.lazy_people_http.getKSTypeInfo
import com.lt.lazy_people_http.getKSTypeOutermostName
import com.lt.lazy_people_http.getNewAnnotationString
import com.lt.lazy_people_http.montageUrl
import com.lt.lazy_people_http.options.KspOptions
import com.lt.lazy_people_http.options.MethodInfo
import com.lt.lazy_people_http.options.ParameterInfo
import com.lt.lazy_people_http.request.RequestMethod
import java.io.OutputStream

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
    private val createCallFunName = options.getCreateCallFunName()
    private val functionReplaceFrom = options.getFunctionReplaceFrom()
    private val functionReplaceTo = options.getFunctionReplaceTo()
    private val isFunctionNameReplace =//是否对方法名进行替换
        functionReplaceFrom.isNotEmpty() && functionReplaceTo.isNotEmpty()

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
                    "import com.lt.lazy_people_http._lazyPeopleHttpFlatten\n" +
                    "import com.lt.lazy_people_http.call.Call\n" +
                    "import com.lt.lazy_people_http.call.CallCreator\n" +
                    "import com.lt.lazy_people_http.config.LazyPeopleHttpConfig\n" +
                    "import com.lt.lazy_people_http.request.RequestMethod\n" +
                    "import com.lt.lazy_people_http.service.HttpServiceImpl\n" +
                    "import kotlin.reflect.typeOf\n" +
                    "\n" +
                    "class $className(\n" +
                    "    val config: LazyPeopleHttpConfig,\n" +
                    ") : $originalClassName, HttpServiceImpl {\n" +
                    "    private inline fun <reified T> T?._toJson() = CallCreator.parameterToJson(config, this)\n\n"
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
        classDeclaration.superTypes.mapNotNull {
            it.resolve().declaration as? KSClassDeclaration
        }.forEach {
            writeFunction(file, it)
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
            val headers = getHeaders(it)
            val parameterInfo = getParameters(it, methodInfo.method)
            var url = methodInfo.url
            parameterInfo.replaceUrlFunction?.forEach {
                url = url.replace(it.key, "\$${it.value}")
            }
            val functionAnnotations = getFunctionAnnotations(it)

            file.appendText("    override ${if (isSuspendFun) "suspend " else ""}fun $functionName(${parameterInfo.funParameter}): $returnType {\n")
            if (functionAnnotations.isNotEmpty())
                file.appendText(
                    "        var annotations: Array<Annotation>? = null\n" +
                            "        val getAnnotations: () -> Array<Annotation> = _f@{\n" +
                            "            annotations?.let { return@_f it }\n" +
                            "            val array = arrayOf<Annotation>($functionAnnotations)\n" +
                            "            annotations = array\n" +
                            "            array\n" +
                            "        }\n"
                )
            file.appendText(
                "        return $createCallFunName${if (isSuspendFun) "<Call<$returnType>>" else ""}(\n" +
                        "            config,\n" +
                        "            \"$url\",\n" +
                        "            ${parameterInfo.queryParameter},\n" +
                        "            ${parameterInfo.fieldParameter},\n" +
                        "            ${parameterInfo.runtimeParameter},\n" +
                        "            typeOf<$typeOf>(),\n" +
                        "            ${if (methodInfo.method == null) "null" else "RequestMethod.${methodInfo.method}"},\n" +
                        "            $headers,\n" +
                        "            ${if (functionAnnotations.isEmpty()) "null" else "getAnnotations"},\n" +
                        "            $responseName,\n" +
                        "        )${if (isSuspendFun) ".await()" else ""}\n" +
                        "    }\n\n"
            )
        }
    }

    //获取方法的参数和请求参数
    private fun getParameters(it: KSFunctionDeclaration, method: RequestMethod?): ParameterInfo {
        //如果没有参数
        if (it.parameters.isEmpty()) return ParameterInfo("", "null", "null", "null", null)
        //有参数的话就将参数拆为:方法参数,query参数,field参数,和只有运行时才能处理的参数
        val funPList = ArrayList<String>()
        val queryPList = ArrayList<String>()
        val fieldPList = ArrayList<String>()
        val runtimePList = ArrayList<String>()
        val replaceUrlMap = HashMap<String, String>()
        it.parameters.forEach {
            val funPName = it.name!!.asString()
            val type = getKSTypeInfo(it.type).toString()
            funPList.add("$funPName: $type")
            getParameterInfo(it, funPName, queryPList, fieldPList, runtimePList, replaceUrlMap)
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
            if (queryPList.isEmpty()) "null" else queryPList.joinToString(
                prefix = "arrayOf(",
                postfix = ")"
            ),
            if (fieldPList.isEmpty()) "null" else fieldPList.joinToString(
                prefix = "arrayOf(",
                postfix = ")"
            ),
            if (runtimePList.isEmpty()) "null" else runtimePList.joinToString(
                prefix = "arrayOf(",
                postfix = ")"
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
    ) {
        val list =
            (it.getAnnotationsByType(Query::class)
                    + it.getAnnotationsByType(QueryMap::class)
                    + it.getAnnotationsByType(Field::class)
                    + it.getAnnotationsByType(FieldMap::class)
                    + it.getAnnotationsByType(Url::class)
                    ).toList()
        if (list.isEmpty()) {
            runtimePList.add("\"$funPName\", $funPName._toJson()")
            return
        }
        when (val annotation = list.first()) {
            is Query -> queryPList.add("\"${annotation.name}\", $funPName._toJson()")
            is QueryMap -> {
                checkMapType(it, funPName)
                queryPList.add("*$funPName._lazyPeopleHttpFlatten()")
            }

            is Field -> fieldPList.add("\"${annotation.name}\", $funPName._toJson()")
            is FieldMap -> {
                checkMapType(it, funPName)
                fieldPList.add("*$funPName._lazyPeopleHttpFlatten()")
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
    private fun getHeaders(it: KSFunctionDeclaration): String {
        val list = it.getAnnotationsByType(Header::class).toList()
        if (list.isEmpty()) return "null"
        return list.joinToString(
            prefix = "arrayOf(",
            postfix = ")"
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