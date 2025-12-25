# Add project specific ProGuard rules here.
-keep class com.hidex.app.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
