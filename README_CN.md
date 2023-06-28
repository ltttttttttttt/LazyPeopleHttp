# LazyPeopleHttp

```kotlin
//下面代码相当于: https://xxx/getUser?userId=$userId
suspend fun getUser(userId: Int): User
或
fun getUser(userId: Int): Call<User>

//如果使用Compose可以这样用:
val user by remember { hf.getUser(0).toState() }
Text("UserName=${user?.name}")
```

懒人http客户端, 类型安全的HTTP客户端, 适用于: JVM(Android, Desktop), iOS, js web.

灵感来源于: https://github.com/ltttttttttttt/retrofit

如果你想使用更多功能的库,可以使用: https://github.com/Foso/Ktorfit

<p align="center">
<img src="https://img.shields.io/badge/Kotlin-Multiplatform-%237f52ff?logo=kotlin">
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
    id("com.google.devtools.ksp") version "1.8.20-1.0.10"//this,前面的1.8.20对应你的kotlin版本,更多版本参考: https://github.com/google/ksp/releases
}

dependencies {
    ...
    implementation("io.github.ltttttttttttt:LazyPeopleHttp-lib:$version")//this,比如1.1.1
    ksp("io.github.ltttttttttttt:LazyPeopleHttp:$version")//this,比如1.1.1
}
```

* 如果是多平台,在common模块目录内的build.gradle.kts内添加

```kotlin
plugins {
    ...
    id("com.google.devtools.ksp") version "1.8.20-1.0.10"//this,前面的1.8.20对应你的kotlin版本,更多版本参考: https://github.com/google/ksp/releases
}

...
val commonMain by getting {
    dependencies {
        ...
        api("io.github.ltttttttttttt:LazyPeopleHttp-lib:$version")//this,比如1.1.1
    }
}

...
dependencies {
    add("kspCommonMainMetadata", "io.github.ltttttttttttt:LazyPeopleHttp:$version")
}
```

* 如果你使用的ksp版本小于1.0.9则需要以下配置:

<a href="https://github.com/ltttttttttttt/Buff/blob/main/README_KSP_SRC_CN.md">ksp配置</a>

Step 2.接口声明:

```kotlin
@LazyPeopleHttpService
interface HttpFunctions : GetHf {
    //标准post请求声明
    @POST("post/postB")
    fun postB(@Field("name") t: String): Call<UserBean>

    //懒人post请求声明,会把方法名当做url,其下划线会转换为斜杠
    fun post_postC(name: String): Call<String>

    //suspend post请求声明
    suspend fun post_postA(t: String): String

    //标准get请求声明
    @GET("get/getA")
    fun getA(@Query("t") t2: String): Call<String>

    //懒人get请求声明
    fun get_getB(name: String): Call<UserBean>

    //suspend get请求声明
    suspend fun suspendGetB(name: String): UserBean

    //添加静态的请求头
    @Header("aaa", "bbb")
    fun post_checkHeader(): Call<String?>

    //配置动态的url
    @GET("get/getD/{type}")
    fun getD(@Url("type") url: String): Call<String?>

    //可以声明具体函数,此时不会生成额外的方法
    fun ccc(): Int = 0
}

@UrlMidSegment("get/")//这个文件里的所有方法都会自动加一个中缀
interface GetHf {
    @GET("getC")//相当于方法的url是:  BaseUrl + UrlMidSegment的url + 方法的url
    fun getC2(name: String): Call<NetBean<String>>

    //使用Query的key value map,可以更灵活
    @GET("getC")
    fun getC4(@QueryMap map: Map<String, String?>): Call<NetBean<String>>

    //使用Field的key value map,可以更灵活
    @POST("setUserName")
    fun setUserName2(@FieldMap map: Map<String, String?>): Call<NetBean<UserBean>>
}
```

Step 3.接口使用:

```kotlin
//配置ktor的client
private val client = HttpClient {
    defaultRequest {
        //配置baseUrl
        url("http://127.0.0.1:666/")
    }
}
private val config = LazyPeopleHttpConfig(client)
//创建请求接口的实现类
private val hf = HttpFunctions::class.createService(config)

//使用接口的实现类
hf.postB("123").enqueue()//回调异步请求
hf.suspendGetB("111")//协程异步请求
val data by remember { hf.get().toState() }//返回响应式的State,适用于Compose
```

Step 4.自定义配置:

```kotlin
/*
 * 当前LazyPeopleHttpService类全局配置
 * [client]ktor请求客户端
 * [serializer]序列化器
 * [encryptor]加解密器
 * [defaultRequestMethod]默认请求方式(不使用注解的方法)
 * [onSuspendError]suspend函数抛出异常时调用
 * [onRequest]成功构造了请求,但发送请求之前调用
 * [onResponse]请求之后调用
 */
class LazyPeopleHttpConfig(...) {
    /**
     * 添加用于构造网络请求的返回值对象的适配器
     * 添加完可以这样声明: fun getUser(): Flow<UserBean>
     */
    fun addCallAdapter()
}

//单独修改一个接口的配置
hf.postB("123").config {
    //this is HttpRequestBuilder
}.enqueue()

ksp {
    //开启运行时配置获取所有注解的功能,不开启时调用[RequestInfo#functionAnnotations]始终返回null
    //arg("getFunAnnotationsWithLazyPeopleHttp", "true")
    //你甚至可以修改创建Call的方法,来返回自定义的Call
    //arg("createCallFunNameWithLazyPeopleHttp", "CallAdapter.createCall2")
}
```

Step 5.混淆配置:

```kotlin
-keep class com.lt.lazy_people_http.call.Call { *;}
# 和你自定义的[CallAdapter]中使用到的名字
```