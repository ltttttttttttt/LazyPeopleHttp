# LazyPeopleHttp

```kotlin
//The following code is equivalent to: https://xxx/getUser?userId=$userId
suspend fun getUser(userId: Int): User
or
fun getUser(userId: Int): Call<User>

//In Compose used:
val user by remember { hf.getUser(0).toState() }
Text("UserName=${user?.name}")
```

A type-safe HTTP client for Kotlin Multiplatform.

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
    id("com.google.devtools.ksp") version "1.8.20-1.0.10"//this,The left 1.8.20 corresponds to your the Kotlin version,more version: https://github.com/google/ksp/releases
}

dependencies {
    ...
    implementation("io.github.ltttttttttttt:LazyPeopleHttp-lib:$version")//this,such as 1.1.1
    ksp("io.github.ltttttttttttt:LazyPeopleHttp:$version")//this,such as 1.1.1
}
```

* If it is multi-platform, add it to build.gradle.kts in the common module directory

```kotlin
plugins {
    ...
    id("com.google.devtools.ksp") version "1.8.20-1.0.10"
}

...
val commonMain by getting {
    dependencies {
        ...
        api("io.github.ltttttttttttt:LazyPeopleHttp-lib:$version")//this,such as 1.1.1
    }
}

...
dependencies {
    add("kspCommonMainMetadata", "io.github.ltttttttttttt:LazyPeopleHttp:$version")
}
```

* If you are using a version of ksp less than 1.0.9, the following configuration is required:

<a href="https://github.com/ltttttttttttt/Buff/blob/main/README_KSP_SRC.md">Ksp configuration</a>

Step 2.interface declaration:

```kotlin
@LazyPeopleHttpService
interface HttpFunctions : GetHf {
    //Standard post request statement
    @POST("post/postB")
    fun postB(@Field("name") t: String): Call<UserBean>

    //The lazy people post request statement will treat the method name as a url, and its _ will be converted to /
    fun post_postC(name: String): Call<String>

    //suspend post request statement
    suspend fun post_postA(t: String): String

    //Standard get request statement
    @GET("get/getA")
    fun getA(@Query("t") t2: String): Call<String>
    
    //lazy people get request statement
    fun get_getB(name: String): Call<UserBean>
    
    //suspend get request statement
    suspend fun suspendGetB(name: String): UserBean
    
    //Add static request headers
    @Header("aaa", "bbb")
    fun post_checkHeader(): Call<String?>

    //Configure dynamic urls
    @GET("get/getD/{type}")
    fun getD(@Url("type") url: String): Call<String?>

    //Specific functions can be declared, and no additional methods will be generated at this time
    fun ccc(): Int = 0
}

@UrlMidSegment("get/")//All methods in this file will automatically add an infix
interface GetHf {
    @GET("getC")//The url equivalent to the method is: BaseUrl + UrlMidSegment url + method url
    fun getC2(name: String): Call<NetBean<String>>

    //Using Query's key value map can be more flexible
    @GET("getC")
    fun getC4(@QueryMap map: Map<String, String?>): Call<NetBean<String>>

    //Using Field's key value map can be more flexible
    @POST("setUserName")
    fun setUserName2(@FieldMap map: Map<String, String?>): Call<NetBean<UserBean>>
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
val data by remember { hf.get().toState() }//Return responsive State, suitable for Compose
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
class LazyPeopleHttpConfig(...) {
    /**
     * Add an adapter for constructing the return value object of a network request
     * After adding, you can declare it like this: fun getUser(): Flow<UserBean>
     */
    fun addCallAdapter()

    /**
     * Hook suspend response
     */
    fun addSuspendHook()
}

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

Step 5.R8/Proguard:

```kotlin
-keep class com.lt.lazy_people_http.call.Call { *;}
# And the name used in your customized [CallAdapter]
```
