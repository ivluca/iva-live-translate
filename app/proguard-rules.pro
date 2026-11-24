# Project-specific ProGuard / R8 rules for iva Live Translate
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepclassmembers class * {
    @org.jetbrains.annotations.NotNull <methods>;
}
