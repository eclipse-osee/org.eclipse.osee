#include <jni.h>
#include <string.h>

// Declarations of Rust functions
extern char* rust_parse_substitute(const char* input, const char* file_name, const char* file_ext, const char* config_json);
extern void rust_free_string(char* s);

JNIEXPORT jstring JNICALL Java_org_eclipse_osee_java_rust_ffi_applicability_ApplicabilityParseSubstituteAndSanitize_parseSubstituteAndSanitizeApplicability(
    JNIEnv* env,
    jclass clazz,
    jstring input,
    jstring file_name,
    jstring file_extension,
    jstring config_json) {

    const char* c_input = (*env)->GetStringUTFChars(env, input, NULL);
    const char* c_file_name = (*env)->GetStringUTFChars(env, file_name, NULL);
    const char* c_file_extension = (*env)->GetStringUTFChars(env, file_extension, NULL);
    const char* c_config_json = (*env)->GetStringUTFChars(env, config_json, NULL);

    char* result_cstr = rust_parse_substitute(c_input, c_file_name, c_file_extension, c_config_json);

    // Release the Java strings
    (*env)->ReleaseStringUTFChars(env, input, c_input);
    (*env)->ReleaseStringUTFChars(env, file_name, c_file_name);
    (*env)->ReleaseStringUTFChars(env, file_extension, c_file_extension);
    (*env)->ReleaseStringUTFChars(env, config_json, c_config_json);

    if (result_cstr == NULL) {
        return (*env)->NewStringUTF(env, "Error: Unable to parse substitute.");
    }

    jstring result = (*env)->NewStringUTF(env, result_cstr);

    // Free Rust allocated string
    rust_free_string(result_cstr);

    return result;
}
