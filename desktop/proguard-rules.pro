# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\ruanjian\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#规则*************************
#-keepclasseswithmembernames class *{ native <methods>;}#保留类名和成员函数名  该定义是定义保留所有native方法的方法名和其类名
#-keepclassmembers public class * extends android.view.View{ void set*(***); *** get*();}#保留成员函数名  该定义是保留继承与View的getset方法
#-keepclassmembers class * extends android.app.Activity{public void *(android.view.View);}#保留成员函数名  该定义是保留activity类中以View为参数的方法名
#-keep class * implements android.os.Parcelable{public static final android.os.Parcelable$Creator *;}#保留类名  该定义是保留实现了Parcelable的类名
#-keepclassmembers class **.R$*{public static <fields>;}#保留字段名  该定义是保留了所有R类内部类的静态字段名


#-------------------------------------------定制化区域---------------------------------------
#---------------------------------0.骚功能---------------------------------

#网络请求封装不能被混淆
-keep class com.lt.lazy_people_http.common.HttpFunctions { *;}
-keep class com.lt.lazy_people_http.common.HttpFunctions2 { *;}

#自带混淆的加密不能被混淆
-keep class androidx.appcompat.app.* { *;}

#Proguard 会保留这些类型的 @Metadata 的同时，在混淆时也会对 data2 字段的值做一致性的修改。
#-keepkotlinmetadata
#---------------------------------1.实体类(model)---------------------------------
-keep class com.lt.lazy_people_http.common.** { *;}
#-------------------------------------------------------------------------
#---------------------------------2.第三方包-------------------------------

#协程
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatcherLoader {
    boolean FAST_SERVICE_LOADER_ENABLED return false;
}
-assumenosideeffects class kotlinx.coroutines.internal.FastServiceLoaderKt {
    boolean ANDROID_DETECTED return true;
}
-assumenosideeffects class kotlinx.coroutines.internal.MainDispatchersKt {
    boolean SUPPORT_MISSING return false;
}
-assumenosideeffects class kotlinx.coroutines.DebugKt {
    boolean getASSERTIONS_ENABLED() return false;
    boolean getDEBUG() return false;
    boolean getRECOVER_STACK_TRACES() return false;
}
-keep class kotlinx.coroutines.android.AndroidDispatcherFactory {*;}
-keep class kotlinx.coroutines.android.AndroidExceptionPreHandler {*;}

#ViewBinding
-keepclassmembers class * implements androidx.viewbinding.ViewBinding {
  public static ** inflate(...);
}

#kotlin反射
-keep class kotlin.reflect.jvm.internal.impl.load.java.**{*; }#防止kt反射被混淆
-keep class kotlin.Metadata{*; }#防止kt元注解被混淆
-keep interface kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoader
-keep class kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins.BuiltInsLoaderImpl

#kt序列化
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.SerializationKt
-keep,includedescriptorclasses class com.lt.lazy_people_http.common.**$$serializer { *; } # <-- change package name to your app's
-keepclassmembers class com.lt.lazy_people_http.common.** { # <-- change package name to your app's
    *** Companion;
}
-keepclasseswithmembers class com.lt.lazy_people_http.common.** { # <-- change package name to your app's
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$* *;
}
-keepclassmembers class <1>$<3> {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}
-keepattributes RuntimeVisibleAnnotations,AnnotationDefault

#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}

# appcompat
-keep public class * extends androidx.core.view.ActionProvider {
public <init>(android.content.Context);
}

# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#OkHttp3
-dontwarn javax.annotation.**
-adaptresourcefilenames okhttp3/internal/publicsuffix/PublicSuffixDatabase.gz
-dontwarn okhttp3.internal.platform.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

#OkIO
-dontwarn org.codehaus.mojo.animal_sniffer.*

#ktor client
-keep class io.ktor.server.netty.EngineMain { *; }
-keep class io.ktor.server.config.HoconConfigLoader { *; }
-keep class com.example.ApplicationKt { *; }
-keep class kotlin.reflect.jvm.internal.** { *; }
-keep class kotlin.text.RegexOption { *; }
#-dontwarn io.ktor.events.** #忽略ktor混淆时找不到某些类
#-keep class io.ktor.events.** { *; }

#-------------------------------------------------------------------------
#---------------------------------3.与 js 互相调用的类------------------------
#-------------------------------------------------------------------------
#---------------------------------4.反射相关的类和方法-----------------------

#ViewModel的构造由于要被反射调用,所以不能被混淆
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
  public <init>(...);
}
#----------------------------------------------------------------------------
#---------------------------------以下基本不用动---------------------------------------------
#---------------------------------基本指令区----------------------------------
# 代码混淆压缩比，在0~7之间
-optimizationpasses 7
# 混淆时不使用大小写混合，混淆后的类名为小写(经测试混淆后的代码还是区分了大小写),windows下的同学还是加入这个选项吧(windows大小写不敏感)
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
# 不做预检验，preverify是proguard的四个步骤之一,Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify
# 有了verbose这句话，混淆后就会生成映射文件
#-verbose #TODO 需要混淆对照表时再打开
#生成的文件--文件名和混淆名的对应文件
#-printmapping proguardMapping.txt #TODO 需要混淆对照表时再打开
#apk 包内所有 class 的内部结构
#-dump class_files.txt
#未混淆的类和成员
#-printseeds seeds.txt
#列出从 apk 中删除的代码
#-printusage proguardUnused.txt
# 指定混淆时采用的算法，后面的参数是一个过滤器,这个过滤器是谷歌推荐的算法，一般不改变
-optimizations !code/simplification/cast,!field/*,!class/merging/*
# 保护代码中的Annotation不被混淆
-keepattributes *Annotation*,InnerClasses
# 避免混淆泛型
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#忽略警告
#-ignorewarning
#----------------------------------------------------------------------------
#---------------------------------默认保留区---------------------------------
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
# 保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
public void *(android.view.View);
}
# 枚举类不能被混淆
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
*** get*();
void set*(***);
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
static final long serialVersionUID;
private static final java.io.ObjectStreamField[] serialPersistentFields;
private void writeObject(java.io.ObjectOutputStream);
private void readObject(java.io.ObjectInputStream);
java.lang.Object writeReplace();
java.lang.Object readResolve();
}
-keep class **.R$* {
*;
}
-keepclassmembers class * {
void *(**On*Event);
}
#----------------------------------------------------------------------------
#---------------------------------webview------------------------------------
-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
public *;
}
-keepclassmembers class * extends android.webkit.WebViewClient {
public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
public void *(android.webkit.WebView, jav.lang.String);
}
#----------------------------------------------------------------------------
