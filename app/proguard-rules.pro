# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-ignorewarnings

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-keepattributes EnclosingMethod
-keepattributes SourceFile,LineNumberTable
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.IntentService
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService

-keepattributes *Annotation*
-keep class com.lovechatapp.chat.util.ParamUtil{*;}
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.FragmentActivity
-keep public class * extends android.support.v7.app.AppCompatActivity

#Base
-keep class * extends com.lovechatapp.chat.base.BaseBean { *; }
-keep class * extends com.lovechatapp.chat.base.BaseActivity { *; }
-keep class * extends com.lovechatapp.chat.base.BaseFragment { *; }
-keep class * extends com.lovechatapp.chat.base.LazyFragment { *; }


#glide
-dontwarn com.bumptech.glide.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# for DexGuard only
#-keepresourcexmlelements manifest/application/meta-data@value=GlideModule

#知乎Matisse额外规则
-dontwarn com.squareup.picasso.**
-keep class com.zhihu.matisse.**{*;}

#微信开放平台
-keep class com.tencent.mm.opensdk.** {
*;
}
-keep class com.tencent.wxop.** {
*;
}
-keep class com.tencent.mm.sdk.** {
*;
}

#QQ登录
#-libraryjars libs/open_sdk_r6008_lite.jar
-dontwarn com.tencent.**
-keep class com.tencent.** {*;}

#ucrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

#腾讯实时音视频
-keep class com.tencent.**{*;}
-dontwarn com.tencent.**
-keep class tencent.**{*;}
-dontwarn tencent.**
-keep class qalsdk.**{*;}
-dontwarn qalsdk.**

#支付宝支付
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

#Mina
-keep class org.apache.mina.** { *; }
-keep class org.slf4j.** { *; }
#Socket
-keep class com.lovechatapp.chat.socket.** { *; }

#极光
-dontoptimize
-dontpreverify
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#极光im  --- start----
-keepattributes  EnclosingMethod,Signature
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

-dontwarn cn.jmessage.**
-keep class cn.jmessage.**{ *; }

-keepclassmembers class ** {
    public void onEvent*(**);
}

#========================gson================================
-dontwarn com.google.**
-keep class com.google.gson.** {*;}

#========================protobuf================================
-keep class com.google.protobuf.** {*;}

#极光im  --- end----

#Bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#JobService
-keep public class * extends android.app.job.JobService

#MTA
-keep class com.tencent.stat.*{*;}
-keep class com.tencent.mid.*{*;}

#三体云
-keep class ttt.ijk.media.**{*;}
-keep class project.android.imageprocessing.**{*;}
-keep class org.TTTRtc.voiceengine.**{*;}
-keep class com.wushuangtech.**{*;}
-dontwarn ttt.ijk.media.**

#枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#高德地图 3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}
#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#七牛云播放器
-keep class com.qiniu.** {*;}
-keep class com.pili.pldroid.player.** {*;}
-keep class com.qiniu.qplayer.mediaEngine.MediaPlayer{*;}

#美颜
-keep class cn.tillusory.**{*;}
-keep class com.hwangjr.**{*;}
-keep class rx.**{*;}
-keep class com.faceunity.** {*;}

#腾讯IM
-keep class com.tencent.** { *; }

#agora
-keep class io.agora.** { *; }
-keep class com.lovechatapp.chat.rtc.** { *; }

#支付
-keep class com.pay.paytypelibrary.PayUtil {*;}
-keep class com.pay.paytypelibrary.OrderInfo {*;}
-keep class com.tencent.mm.opensdk.** {*;}
-keep class com.tencent.mm.sdk.** {*;}
-keep class com.tencent.wxop.** {*;}
-dontwarn com.unionpay.**
-keep class com.unionpay.** {*;}
-keep class don.rsa.gkb.** {*;}