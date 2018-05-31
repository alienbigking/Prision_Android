# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/Raleigh.Luo/Documents/android-sdk-macosx/tools/proguard/proguard-android.txt
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

-dontskipnonpubliclibraryclasses # 不忽略非公共的库类
-optimizationpasses 5            # 指定代码的压缩级别
-dontusemixedcaseclassnames      # 是否使用大小写混合
-dontpreverify                   # 混淆时是否做预校验
-verbose                         # 混淆时是否记录日志
-keepattributes *Annotation*     # 保持注解
-ignorewarning                   # 忽略警告
-dontoptimize                    # 优化不优化输入的类文件
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*  # 混淆时所采用的算法
#生成日志数据，gradle build时在本项目根目录输出
 -dump class_files.txt            #apk包内所有class的内部结构
 -printseeds seeds.txt            #未混淆的类和成员
 -printusage unused.txt           #打印未被使用的代码
 -printmapping mapping.txt        #混淆前后的映射
#如果引用了v4或者v7包
-dontwarn android.support.**
-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
-keep class android.support.v4.** { *; }
-keep public class * extends android.support.v4.**
-keep class android.support.v7.** { *; }
-keep public class * extends android.support.v7.**
# 不被混淆的
 -keep public class * extends android.app.Fragment
 -keep public class * extends android.app.Activity
 -keep public class * extends android.app.Application
 -keep public class * extends android.app.Service
 -keep public class * extends android.content.BroadcastReceiver
 -keep public class * extends android.preference.Preference
 -keep public class * extends android.content.ContentProvider

 #这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
 -keepclassmembers class * extends android.support.v7.app.AppCompatActivity {
    public void *(android.view.View);
 }
 -keepclassmembers class * extends android.support.v4.app.FragmentActivity {
    public void *(android.view.View);
 }

#使用GSON、fastjson等框架解析服务端数据时，JSON对象实体类不混淆
-dontwarn com.gkzxhn.prison.entity.**
-keep class com.gkzxhn.prison.entity.** { *; }
#nineoldandrois包
-dontwarn com.nineoldandroids.**
-keep class com.nineoldandroids.** { *; }
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
     native <methods>;
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
      public static final android.os.Parcelable$Creator *;
}
  # 保持 imageloader图片下载框架 不被混淆
-keep class com.nostra13.universalimageloader.** { *; }
#自定义控件不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
#不混淆加载动画
-keep class com.starlight.mobile.android.lib.view.dotsloading.JumpingSpan {*;}
# 云信混淆
-dontwarn com.netease.**
-keep class com.netease.** {*;}
#如果你使用全文检索插件，需要加入
-dontwarn org.apache.lucene.**
-keep class org.apache.lucene.** {*;}