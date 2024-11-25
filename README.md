# LazyPeopleHttp

```kotlin
//The following code is equivalent to: https://xxx/getUser?userId=$userId
suspend fun getUser(userId: Int): User
or
fun getUser(userId: Int): Call<User>

//In Coroutine
val user = hf.getUser(0).await()
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
interface HttpFunctions : GetFuns {
    //Standard post request statement,url=post/postB
    @POST("post/postB")
    fun postB(@Field("name") t: String): Call<UserBean>

    //The lazy people post request statement will treat the method name as a url,url=postC
    fun post_postC(name: String): Call<String>

    //suspend post request statement,url=postA
    suspend fun post_postA(t: String): String

    //Configure dynamic urls
    @GET("get/getD/{type}")
    @Header("aaa", "bbb")//Add static request headers
    fun getD(
        @Url("type") url: String,
        @QueryMap map: Map<String, String?>//can be more flexible, post use @FieldMap,
    ): Call<String?>
}

@UrlMidSegment("get")//All methods in this file will automatically add an infix
interface GetHf {
    @GET("getC")//url=get/getC
    fun getC2(name: String): Call<String>

    //url=get/getB
    fun getB(name: String): Call<String>
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
hf.suspendGetB("111") or hf.postB("123").await()//Coroutine asynchronous request
val data by remember { hf.get().toState() }//Return responsive State, suitable for Compose
```

Step 4.Custom configuration(Optional):

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
    //Rules for customizing output files, refer to [CustomizeOutputFileBean], Need a JSON file, The content is List<CustomizeOutputFile>
    //arg("customizeOutputFileWithLazyPeopleHttp", "${project.projectDir.absoluteFile}/customizeOutputFile.json")
    //When using the name of a method as a URL, replace a value with [functionReplaceTo]
    //arg("functionReplaceFromWithLazyPeopleHttp", "_")
    //When using the method name as a URL, replace [functionReplaceFrom] with the set value
    //arg("functionReplaceToWithLazyPeopleHttp", "/")
}
```

Step 5.R8/Proguard(Optional):

```kotlin
# The name used in your customized [CallAdapter]
```
