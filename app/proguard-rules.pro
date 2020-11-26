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
#-keepattributes Signature
#-keepattributes *Annotation*
#-dontnote okhttp3.**, okio.**, retrofit2.**
#-dontwarn retrofit2.**
#-keepclasseswithmembers class * {
#    @retrofit.http.* <methods>;
#}
#
#-keep class retrofit.http.* { *; }
#-dontwarn okio.**
#-dontwarn retrofit2.**
#-keep class retrofit2.* { *; }
#-keepattributes Signature
#-keepattributes Exceptions
#
#
#-keep class com.codearlystudio.homedelivery.models.CartItems
#-keep class com.codearlystudio.homedelivery.models.OrderProducts
#-keep class com.codearlystudio.homedelivery.models.Orders
#-keep class com.codearlystudio.homedelivery.models.PojoAddress
#-keep class com.codearlystudio.homedelivery.models.PojoCategoryItems
#-keep class com.codearlystudio.homedelivery.models.PojoExProducts
#-keep class com.codearlystudio.homedelivery.models.PojoProducts
#-keep class com.codearlystudio.homedelivery.models.PojoSearch
#-keep class com.codearlystudio.homedelivery.models.PojoSections
#-keep class com.codearlystudio.homedelivery.models.PojoSimilarProducts
#-keep class com.codearlystudio.homedelivery.models.PojoTimeSlot
#-keep class com.codearlystudio.homedelivery.models.PojoVariantDetails
#-keep class com.codearlystudio.homedelivery.models.PojoVariants
#-keep class com.codearlystudio.homedelivery.models.ScreenItem
#-keep class com.codearlystudio.homedelivery.models.SectionCategories