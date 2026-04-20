# R8/ProGuard rules for FC Neftchi.
# Enable by setting isMinifyEnabled = true in app/build.gradle.kts.

# ==========================================================================
# Attributes needed at runtime
# ==========================================================================
-keepattributes Signature, Exceptions, InnerClasses, EnclosingMethod, AnnotationDefault
-keepattributes *Annotation*
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes RuntimeVisibleTypeAnnotations, SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# ==========================================================================
# Kotlin
# ==========================================================================
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class kotlinx.coroutines.** { volatile <fields>; }

# Coroutines internal classes
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# ==========================================================================
# Moshi (reflective Kotlin adapter used by this app)
# ==========================================================================
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier @interface *
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
    **[] values();
}
# Keep all data classes used as API payloads — Moshi reflects on them.
-keep class uz.umarov.fcneftchi.data.model.** { *; }
-keepclassmembers class uz.umarov.fcneftchi.data.model.** { *; }

# Moshi runtime
-dontwarn com.squareup.moshi.**
-keep class com.squareup.moshi.** { *; }

# ==========================================================================
# Retrofit
# ==========================================================================
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclasseswithmembers,includedescriptorclasses class * {
    @retrofit2.http.* <methods>;
}
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep the PFL API service interface intact.
-keep interface uz.umarov.fcneftchi.data.api.** { *; }

# Retrofit
-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.animal_sniffer.*

# ==========================================================================
# OkHttp
# ==========================================================================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# ==========================================================================
# Hilt / Dagger
# (Hilt plugin already contributes most rules, but these catch edge cases.)
# ==========================================================================
-keep,allowobfuscation @interface dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }
-keep @dagger.hilt.android.HiltAndroidApp class * { *; }
-keep @dagger.hilt.android.AndroidEntryPoint class * { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# ==========================================================================
# AndroidX Navigation safe-args
# ==========================================================================
-keep class **.*Args { *; }
-keep class **.*Directions { *; }

# ==========================================================================
# Coil (SVG decoder is registered reflectively via ImageLoader.Builder)
# ==========================================================================
-keep class coil.** { *; }
-dontwarn coil.**

# ==========================================================================
# Firebase
# ==========================================================================
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# ==========================================================================
# YouTube player
# ==========================================================================
-keep class com.pierfrancescosoffritti.androidyoutubeplayer.** { *; }
-dontwarn com.pierfrancescosoffritti.androidyoutubeplayer.**

# ==========================================================================
# Application classes referenced from AndroidManifest / deep links
# ==========================================================================
-keep class uz.umarov.fcneftchi.NeftchiApp
-keep class uz.umarov.fcneftchi.ui.SplashActivity
-keep class uz.umarov.fcneftchi.ui.MainActivity
-keep class uz.umarov.fcneftchi.service.** { *; }

# Native security helper loads a JNI lib.
-keep class uz.umarov.fcneftchi.util.NativeSecurity { *; }

# ==========================================================================
# ViewBinding generated classes — must not be stripped, they back ViewBinding.inflate.
# ==========================================================================
-keep class uz.umarov.fcneftchi.databinding.** { *; }

# ==========================================================================
# Enums accessed by name (valueOf/name) — common in Moshi etc.
# ==========================================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==========================================================================
# Parcelables (safe-args and Bundle passing)
# ==========================================================================
-keep class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ==========================================================================
# Keep line numbers (crash reports) — already in attributes, reiterated.
# ==========================================================================
-keepattributes SourceFile,LineNumberTable
