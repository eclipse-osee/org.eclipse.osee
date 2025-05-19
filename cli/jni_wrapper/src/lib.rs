/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

use std::ffi::{CStr};
use std::os::raw::c_char;
use std::ptr;

/// Helper: throw a Java exception with message
fn throw_java_exception(env: &mut JNIEnv, class_name: &str, message: &str) {
    let _ = env.throw_new(class_name, message);
}

/// # Safety
///
/// This function is marked `unsafe` because it uses raw pointers
/// Specifically, it performs the following unsafe operation:
///
/// - Calls `CStr::from_ptr(result_cstr)`, which assumes that `result_cstr` is:
///   - A valid, non-null pointer to a null-terminated C string,
///   - Properly aligned,
///   - Not dangling (i.e., still allocated),
///   - Contains valid UTF-8 data if later converted to a `&str`.
///
/// The safety of this call is guaranteed by the contract of the Rust function
/// `rust_parse_substitute`, which returns a pointer created by `CString::into_raw()`,
/// and is expected to return a valid C string pointer (or null on failure).
///
/// This function checks for null before dereferencing and converts the result safely
/// into a Java `String`. After use, the memory allocated by Rust is explicitly freed
/// using `rust_free_string`.
#[no_mangle]
pub unsafe extern "system" fn Java_org_eclipse_osee_java_rust_ffi_applicability_ApplicabilityParseSubstituteAndSanitize_parseSubstituteAndSanitizeApplicability(
    mut env: JNIEnv,           // JNI environment to interact with Java
    _class: JClass,            // Class reference; unused in this function
    j_input: JString,          // Input string from Java
    j_file_name: JString,      // Name of file whose input string is passed in from Java
    j_file_extension: JString, // Extension of file whose input string is passed in from Java
    j_config_json: JString,    // Configuration JSON string from Java
) -> jstring {
    // Convert Java strings to Rust-owned Strings
    let input: String = match env.get_string(&j_input) {
        Ok(s) => s.into(),
        Err(e) => {
            throw_java_exception(&mut env, "java/lang/IllegalArgumentException", &format!("Invalid input string: {}", e));
            return ptr::null_mut();
        }
    };

    let file_name: String = match env.get_string(&j_file_name) {
        Ok(s) => s.into(),
        Err(e) => {
            throw_java_exception(&mut env, "java/lang/IllegalArgumentException", &format!("Invalid fileName string: {}", e));
            return ptr::null_mut();
        }
    };

    let file_extension: String = match env.get_string(&j_file_extension) {
        Ok(s) => s.into(),
        Err(e) => {
            throw_java_exception(&mut env, "java/lang/IllegalArgumentException", &format!("Invalid fileExtension string: {}", e));
            return ptr::null_mut();
        }
    };

    let config_json: String = match env.get_string(&j_config_json) {
        Ok(s) => s.into(),
        Err(e) => {
            throw_java_exception(&mut env, "java/lang/IllegalArgumentException", &format!("Invalid configJson string: {}", e));
            return ptr::null_mut();
        }
    };

    // Call the wrapped Rust function from java_rust_ffi crate
    let result_cstr =
        java_rust_ffi::rust_parse_substitute(
            input.as_ptr() as *const c_char,
            file_name.as_ptr() as *const c_char,
            file_extension.as_ptr() as *const c_char,
            config_json.as_ptr() as *const c_char,
        );

    if result_cstr.is_null() {
        throw_java_exception(&mut env, "java/lang/RuntimeException", "rust_parse_substitute returned null");
        return ptr::null_mut();
    }

    // Convert returned *mut c_char back to Java String
    let result_str = CStr::from_ptr(result_cstr);
    let result_java_str = match result_str.to_str() {
        Ok(s) => s,
        Err(e) => {
            java_rust_ffi::rust_free_string(result_cstr as *mut c_char);
            throw_java_exception(&mut env, "java/lang/RuntimeException", &format!("Invalid UTF-8 result from rust_parse_substitute: {}", e));
            return ptr::null_mut();
        }
    };

    // Create a new Java string from Rust str
    let output = match env.new_string(result_java_str) {
        Ok(s) => s,
        Err(e) => {
            java_rust_ffi::rust_free_string(result_cstr as *mut c_char);
            throw_java_exception(&mut env, "java/lang/RuntimeException", &format!("Failed to create Java String: {}", e));
            return ptr::null_mut();
        }
    };

    // Free the Rust-allocated C string
    java_rust_ffi::rust_free_string(result_cstr as *mut c_char);

    output.into_raw()
}