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
use applicability_parser::parse_applicability;
use applicability_parser_config::{
    applic_config::ApplicabilityConfigElement, get_comment_syntax_from_file_name_and_extension,
};
use applicability_sanitization::SanitizeApplicability;
use applicability_substitution::SubstituteApplicability;

use std::ffi::{CStr, CString};
use std::os::raw::c_char;

#[cfg(not(target_env = "msvc"))]
#[global_allocator]
static GLOBAL: mimalloc::MiMalloc = mimalloc::MiMalloc;

fn run_parse_logic(
    input: &str,
    file_name: &str,
    file_extension: &str,
    config_json: &str,
) -> String {
    // Deserialize the JSON string into ApplicabilityConfigElement
    let applicability_config: ApplicabilityConfigElement = match serde_json::from_str(config_json) {
        Ok(config) => config,
        Err(e) => {
            return format!("Error deserializing JSON: {:?}", e);
        }
    };

    // Get the start and end syntax from file name and extension
    let (start_syntax, end_syntax) = get_comment_syntax_from_file_name_and_extension(
        Some(file_extension),
        Some(file_name),
        "``", // Default start syntax
        "``", // Default end syntax
    );

    // Call parse_applicability to parse the input string with the specified syntaxes
    let content_result = parse_applicability(input, start_syntax, end_syntax);
    let contents = match content_result {
        Ok((_remaining, results)) => results,
        Err(_) => {
            return "Failed to unwrap parsed AST".to_string();
        }
    };

    // Create a copy of the parsed contents for processing
    let copy = contents.clone();
    // Get the substitutions from the config; defaults to empty if not present
    let substitutions = applicability_config
        .clone()
        .get_substitutions()
        .unwrap_or_default();

    // Sanitize the contents using the substitutions and configuration features
    let sanitized_content = copy
        .iter()
        .cloned()
        .map(|c| {
            c.substitute(&substitutions) // Apply substitutions to each content item
                .sanitize(
                    applicability_config.clone().get_features(),
                    &applicability_config.clone().get_name(),
                    &substitutions,
                    applicability_config.get_parent_group(),
                    Some(applicability_config.get_configs().as_slice()),
                )
                .into() // Convert sanitized item back to String
        })
        .collect::<Vec<String>>() // Collect all sanitized items into a Vec
        .join(""); // Join the sanitized items into a single String

    // Match against the ApplicabilityConfigElement enum to determine the type and create a response message
    let message = match applicability_config {
        ApplicabilityConfigElement::Config(_) => "Matched Config".to_string(),
        ApplicabilityConfigElement::ConfigGroup(_) => "Matched ConfigGroup".to_string(),
        ApplicabilityConfigElement::LegacyConfig(_) => "Matched LegacyConfig".to_string(),
    };

    // Combine the sanitized content and the message for the final response
    format!("{}\n{}", sanitized_content, message)
}

/// C ABI exposed function for the wrapper to call
///
/// # Safety
/// This function expects all input pointers to be valid C-style null-terminated UTF-8 strings.
/// The caller must ensure:
/// - Each pointer is non-null and points to valid memory.
/// - Each string is properly null-terminated.
/// - The memory returned from this function must be freed using `rust_free_string`.
#[no_mangle]
pub unsafe extern "C" fn rust_parse_substitute(
    input: *const c_char,
    file_name: *const c_char,
    file_extension: *const c_char,
    config_json: *const c_char,
) -> *mut c_char {
    // Convert C strings to Rust &str with detailed error handling
    let input = match input.as_ref() {
        Some(ptr) => match CStr::from_ptr(ptr).to_str() {
            Ok(s) => s,
            Err(_) => {
                return CString::new("Error: invalid UTF-8 in input")
                    .unwrap()
                    .into_raw()
            }
        },
        None => {
            return CString::new("Error: null pointer for input")
                .unwrap()
                .into_raw()
        }
    };

    let file_name = match file_name.as_ref() {
        Some(ptr) => match CStr::from_ptr(ptr).to_str() {
            Ok(s) => s,
            Err(_) => {
                return CString::new("Error: invalid UTF-8 in file_name")
                    .unwrap()
                    .into_raw()
            }
        },
        None => {
            return CString::new("Error: null pointer for file_name")
                .unwrap()
                .into_raw()
        }
    };

    let file_extension = match file_extension.as_ref() {
        Some(ptr) => match CStr::from_ptr(ptr).to_str() {
            Ok(s) => s,
            Err(_) => {
                return CString::new("Error: invalid UTF-8 in file_extension")
                    .unwrap()
                    .into_raw()
            }
        },
        None => {
            return CString::new("Error: null pointer for file_extension")
                .unwrap()
                .into_raw()
        }
    };

    let config_json = match config_json.as_ref() {
        Some(ptr) => match CStr::from_ptr(ptr).to_str() {
            Ok(s) => s,
            Err(_) => {
                return CString::new("Error: invalid UTF-8 in config_json")
                    .unwrap()
                    .into_raw()
            }
        },
        None => {
            return CString::new("Error: null pointer for config_json")
                .unwrap()
                .into_raw()
        }
    };

    let result_string = run_parse_logic(input, file_name, file_extension, config_json);

    // Return as a newly allocated C string (caller must free)
    CString::new(result_string).unwrap().into_raw()
}

/// Free a C string previously returned by rust_parse_substitute
///
/// # Safety
/// The pointer must have been allocated by `CString::into_raw` in `rust_parse_substitute`.
/// Passing a null or invalid pointer results in undefined behavior.
#[no_mangle]
pub unsafe extern "C" fn rust_free_string(ptr: *mut c_char) {
    if ptr.is_null() {
        return;
    }

    let _ = CString::from_raw(ptr);
}
