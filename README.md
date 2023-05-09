# LazyPeopleHttp

Lazy people http, A type-safe HTTP client for JVM(Android, Desktop), iOS, js web.

Inspired by: https://github.com/ltttttttttttt/retrofit

If you want to use a more functional library, you can use: https://github.com/Foso/Ktorfit

<p align="center">
<img src="https://img.shields.io/badge/Kotlin-Multiplatform-%237f52ff?logo=kotlin">
<img src="https://img.shields.io/badge/license-Apache%202-blue.svg?maxAge=2592000">
<img src="https://img.shields.io/maven-central/v/io.github.ltttttttttttt/LazyPeopleHttp"/>
</p>

<div align="center">us English | <a href="https://github.com/ltttttttttttt/LazyPeopleHttp/blob/main/README_CN.md">cn 简体中文</a></div>

## How to use

Step 1.add dependencies:

version
= [![](https://img.shields.io/maven-central/v/io.github.ltttttttttttt/LazyPeopleHttp)](https://repo1.maven.org/maven2/io/github/ltttttttttttt/LazyPeopleHttp/)

* If it is a single platform, add it to build.gradle.kts in the app module directory

```kotlin
plugins {
    ...
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"//this,The left 1.7.10 corresponds to your the Kotlin version,more version: https://github.com/google/ksp/releases
}

//The fourth step of configuring ksp to generate directory reference links: https://github.com/ltttttttttttt/Buff/blob/main/README.md

dependencies {
    ...
    implementation("io.github.ltttttttttttt:LazyPeopleHttp-lib:$version")//this,such as 1.0.0
    ksp("io.github.ltttttttttttt:LazyPeopleHttp:$version")//this,such as 1.0.0
}
```

* If it is multi-platform, add it to build.gradle.kts in the common module directory

```kotlin
plugins {
    ...
    id("com.google.devtools.ksp") version "1.7.10-1.0.6"
}

...
val commonMain by getting {
    //Configure the ksp generation directory
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
    dependencies {
        ...
        api("io.github.ltttttttttttt:LazyPeopleHttp-lib:$version")//this,such as 1.0.0
    }
}

...
dependencies {
    add("kspCommonMainMetadata", "io.github.ltttttttttttt:LazyPeopleHttp:$version")
}
```

Step 2.interface declaration:

```kotlin
@LazyPeopleHttpService
interface HttpFunctions {
    //Standard post request statement
    @POST("post/postB")
    fun postB(@Field("name") t: String): Call<NetBean<UserBean>>

    //The lazy people post request statement will treat the method name as a url, and its _ will be converted to /
    fun post_postC(name: String): Call<NetBean<String>>

    //suspend post request statement
    suspend fun post_postA(t: String): NetBean<String>

    //Standard get request statement
    @GET("get/getA")
    fun getA(@Query("t") t2: String): Call<NetBean<String>>
    
    //lazy people get request statement
    fun get_getB(name: String): Call<NetBean<UserBean>>
    
    //suspend get request statement
    suspend fun suspendGetB(name: String): NetBean<UserBean>
    
    //Add static request headers
    @Header("aaa", "bbb")
    fun post_checkHeader(): Call<NetBean<String?>>

    //Configure dynamic urls
    @GET("get/getD/{type}")
    fun getD(@Url("type") url: String): Call<NetBean<String?>>

    //Specific functions can be declared, and no additional methods will be generated at this time
    fun ccc(): Int = 0
}
```

Step 3.interface use:

```kotlin
//Configure the client of ktor
private val client = HttpClient {
    defaultRequest {
        //Configure baseUrl
        url("http://127.0.0.1:666/")
    }
}
private val config = LazyPeopleHttpConfig(client)
//Create an implementation class for the request interface
private val hf = HttpFunctions::class.createService(config)

//Implementation class using the interface
hf.postB("123").enqueue()//callback asynchronous request
hf.suspendGetB("111")//Coroutine asynchronous request
```

Step 4.Custom configuration:

```kotlin
/*
 * Current LazyPeopleHttpService Class Global Configuration
 * [client]Ktor request client
 * [serializer]serializer
 * [encryptor]encryptor
 * [defaultRequestMethod]Default request method (without annotation)
 * [onSuspendError]Called when the suspend function throws an exception
 * [onRequest]Successfully constructed the request, but called before sending the request
 * [onResponse]Called after request
 */
class LazyPeopleHttpConfig(...)

//Modify the configuration of an interface separately
hf.postB("123").config {
    //this is HttpRequestBuilder
}.enqueue()

ksp {
    //Enable runtime configuration to obtain all annotations, call [RequestInfo # functionAnnotations] when not enabled and always return null
    //arg("getFunAnnotationsWithLazyPeopleHttp", "true")
    //You can even modify the method of creating a Call to return a custom Call
    //arg("createCallFunNameWithLazyPeopleHttp", "CallAdapter.createCall2")
}
```
