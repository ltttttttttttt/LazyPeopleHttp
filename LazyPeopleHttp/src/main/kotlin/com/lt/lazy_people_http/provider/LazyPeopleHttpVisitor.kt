package com.lt.lazy_people_http.provider

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.lt.lazy_people_http.annotations.GET
import com.lt.lazy_people_http.annotations.POST
import com.lt.lazy_people_http.appendText
import com.lt.lazy_people_http.options.MethodInfo
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
                    "import com.lt.lazy_people_http.call.Call\n" +
                    "import com.lt.lazy_people_http.call._createCall\n" +
                    "import com.lt.lazy_people_http.request.RequestMethod\n" +
                    "import com.lt.lazy_people_http.service.HttpServiceImpl\n" +
                    "import kotlin.reflect.typeOf\n" +
                    "\n" +
                    "class $className(\n" +
                    "    val config: LazyPeopleHttpConfig,\n" +
                    ") : $originalClassName, HttpServiceImpl {\n"
        )
        writeFunction(file, classDeclaration)
        file.appendText("}")
    }

    //向文件中写入变换后的函数
    private fun writeFunction(file: OutputStream, classDeclaration: KSClassDeclaration) {
        classDeclaration.getAllFunctions().filter {
            it.isAbstract
        }.forEach {
            val functionName = it.simpleName.asString()
            val methodInfo = getMethodInfo(it, functionName)
            file.appendText(
                "    override fun $functionName(): Call<MData> {\n" +
                        "        return _createCall(\n" +
                        "            config,\n" +
                        "            \"${methodInfo.url}\",\n" +
                        "            mapOf(),\n" +
                        "            typeOf<MData>(),\n" +
                        "            ${methodInfo.method},\n" +
                        "        )\n" +
                        "    }\n" +
                        "\n"
            )
        }
    }

    /**
     * 获取函数的请求方法相关数据
     */
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