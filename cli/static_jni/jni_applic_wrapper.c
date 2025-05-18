#include <jni.h>

extern jstring Java_org_eclipse_osee_java_rust_ffi_applicability_ApplicabilityParseSubstituteAndSanitize_parseSubstituteAndSanitizeApplicability(
    JNIEnv* env,
    jclass clazz,
    jstring input,
    jstring file_name,
    jstring file_extension,
    jstring config_json);
