# LazyPeopleHttp

懒人http客户端,相当于kotlin全平台的Retrofit

灵感来源于: https://github.com/ltttttttttttt/retrofit

如果你想使用更多功能的库,可以使用: https://github.com/Foso/Ktorfit

<p align="center">
<img src="https://img.shields.io/badge/license-Apache%202-blue.svg?maxAge=2592000">
<img src="https://img.shields.io/maven-central/v/io.github.ltttttttttttt/LazyPeopleHttp"/>
</p>

<div align="center"><a href="https://github.com/ltttttttttttt/LazyPeopleHttp/blob/main/README.md">us English</a> | cn 简体中文</div>

## 使用方式

Step 1.添加依赖:

version
= [![](https://img.shields.io/maven-central/v/io.github.ltttttttttttt/LazyPeopleHttp)](https://repo1.maven.org/maven2/io/github/ltttttttttttt/LazyPeopleHttp/)

* 如果是单平台,在app模块目录内的build.gradle.kts内添加

```kotlin
plugins {
    ...
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"//this,前面的1.7.10对应你的kotlin版本,更多版本参考: https://github.com/google/ksp/releases
}

//配置ksp生成目录参考链接的第四步: https://github.com/ltttttttttttt/Buff/blob/main/README_CN.md?plain=1

dependencies {
    ...
    implementation("io.github.ltttttttttttt:LazyPeopleHttp-lib:$version")//this,比如1.0.0
    ksp("io.github.ltttttttttttt:LazyPeopleHttp:$version")//this,比如1.0.0
}
```

* 如果是多平台,在common模块目录内的build.gradle.kts内添加

```kotlin
plugins {
    ...
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"//this,前面的1.7.10对应你的kotlin版本,更多版本参考: https://github.com/google/ksp/releases
}

...
val commonMain by getting {
    //配置ksp生成目录
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    dependencies {
        ...
        api("io.github.ltttttttttttt:LazyPeopleHttp-lib:$version")//this,比如1.0.0
    }
}

...
dependencies {
    add("kspCommonMainMetadata", "io.github.ltttttttttttt:LazyPeopleHttp:$version")
}
```

Step 2.接口声明:

```kotlin
@LazyPeopleHttpService
interface HttpFunctions {
    //标准post请求声明
    @POST("post/postB")
    fun postB(@Field("name") t: String): Call<NetBean<UserBean>>

    //懒人post请求声明,会把方法名当做url,其下划线会转换为斜杠
    fun post_postC(name: String): Call<NetBean<String>>

    //suspend post请求声明
    suspend fun post_postA(t: String): NetBean<String>

    //标准get请求声明
    @GET("get/getA")
    fun getA(@Query("t") t2: String): Call<NetBean<String>>
    
    //懒人get请求声明
    fun get_getB(name: String): Call<NetBean<UserBean>>

    //suspend get请求声明
    suspend fun suspendGetB(name: String): NetBean<UserBean>
    
    //添加静态的请求头
    @Header("aaa", "bbb")
    fun post_checkHeader(): Call<NetBean<String?>>

    //配置动态的url
    @GET("get/getD/{type}")
    fun getD(@Url("type") url: String): Call<NetBean<String?>>

    //可以声明具体函数,此时不会生成额外的方法
    fun ccc(): Int = 0
}
```

Step 3.接口使用:

```kotlin
//配置ktor的client,serialization的json
private val client = HttpClient {
    defaultRequest {
        //配置baseUrl
        url("http://127.0.0.1:666/")
    }
}
private val json = Json { ignoreUnknownKeys = true }
private val config = LazyPeopleHttpConfig(client, json)
//创建请求接口的实现类
private val hf = HttpFunctions::class.createService(config)

//使用接口的实现类
hf.postB("123").enqueue()//回调异步请求
hf.suspendGetB("111")//协程异步请求
```