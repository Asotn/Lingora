# Lingora ProGuard / R8 rules.
# Most defaults are provided by proguard-android-optimize.txt; the rules
# below cover the libraries that need extra guidance when code shrinking
# and obfuscation are enabled in a release build.

# Retrofit & OkHttp -----------------------------------------------------
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn javax.annotation.**

# kotlinx.serialization ---------------------------------------------------
# kotlinx-serialization ships its own consumer ProGuard rules, but we keep
# our @Serializable model classes explicitly as a safety net.
-keepattributes *Annotation*, InnerClasses
-keep,includedescriptorclasses class com.lingora.app.**$$serializer { *; }
-keepclassmembers class com.lingora.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.lingora.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep data/DTO classes referenced only through reflection-like serialization
-keep class com.lingora.app.data.model.** { *; }
-keep class com.lingora.app.data.remote.** { *; }
