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

use jni::objects::{JClass, JString};
use jni::sys::jstring;
use jni::JNIEnv;

use std::ffi::{CStr, CString};
use std::os::raw::c_char;
use std::ptr;

/// Helper: throw a Java exception with message
fn throw_java_exception(env: &mut JNIEnv, class_name: &str, message: &str) {
    let _ = env.throw_new(class_name, message);
}

/// Helper: Convert `JString` to `CString`, or throw Java exception
fn jstring_to_cstring(env: &mut JNIEnv, jstr: JString, field_name: &str) -> Option<CString> {
    match env.get_string(&jstr) {
        Ok(java_str) => match CString::new(java_str.to_bytes()) {
            Ok(cstring) => Some(cstring),
            Err(_) => {
                throw_java_exception(
                    env,
                    "java/lang/IllegalArgumentException",
                    &format!("{} string contains null byte", field_name),
                );
                None
            }
        },
        Err(e) => {
            throw_java_exception(
                env,
                "java/lang/IllegalArgumentException",
                &format!("Failed to read {} string: {}", field_name, e),
            );
            None
        }
    }
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
    // Convert each JString into a CString, with validation
    let input_cstr = match jstring_to_cstring(&mut env, j_input, "input") {
        Some(cstr) => cstr,
        None => return ptr::null_mut(),
    };
    let file_name_cstr = match jstring_to_cstring(&mut env, j_file_name, "fileName") {
        Some(cstr) => cstr,
        None => return ptr::null_mut(),
    };
    let file_extension_cstr = match jstring_to_cstring(&mut env, j_file_extension, "fileExtension")
    {
        Some(cstr) => cstr,
        None => return ptr::null_mut(),
    };
    let config_json_cstr = match jstring_to_cstring(&mut env, j_config_json, "configJson") {
        Some(cstr) => cstr,
        None => return ptr::null_mut(),
    };

    // Call the core FFI logic
    let result_cstr = java_rust_ffi_core::rust_parse_substitute(
        input_cstr.as_ptr(),
        file_name_cstr.as_ptr(),
        file_extension_cstr.as_ptr(),
        config_json_cstr.as_ptr(),
    );

    if result_cstr.is_null() {
        throw_java_exception(
            &mut env,
            "java/lang/RuntimeException",
            "rust_parse_substitute returned null",
        );
        return ptr::null_mut();
    }

    // Convert returned C string to Rust &str
    let result_str = CStr::from_ptr(result_cstr);
    let result_java_str = match result_str.to_str() {
        Ok(s) => s,
        Err(e) => {
            java_rust_ffi_core::rust_free_string(result_cstr as *mut c_char);
            throw_java_exception(
                &mut env,
                "java/lang/RuntimeException",
                &format!("Invalid UTF-8 result: {}", e),
            );
            return ptr::null_mut();
        }
    };

    // Allocate Java string
    let output = match env.new_string(result_java_str) {
        Ok(s) => s,
        Err(e) => {
            java_rust_ffi_core::rust_free_string(result_cstr as *mut c_char);
            throw_java_exception(
                &mut env,
                "java/lang/RuntimeException",
                &format!("Failed to create Java String: {}", e),
            );
            return ptr::null_mut();
        }
    };

    // Free the Rust-allocated C string
    java_rust_ffi_core::rust_free_string(result_cstr as *mut c_char);

    output.into_raw()
}
