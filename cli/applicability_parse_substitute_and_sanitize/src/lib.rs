/*********************************************************************
 * Copyright (c) 2024 Boeing
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
use jni::JNIEnv;
use jni::sys::jstring;
use applicability_parser_config::applic_config::ApplicabilityConfigElement;
use applicability_parser::parse_applicability;
use applicability_sanitization::SanitizeApplicability;
use applicability_substitution::SubstituteApplicability;

#[no_mangle]
pub extern "system" fn Java_applicability_ApplicabilityParseSubstituteAndSanitize_parseSubstituteAndSanitizeApplicability<'a>(
    mut env: JNIEnv<'a>,                      // JNI environment to interact with Java
    _class: JClass<'a>,                      // Class reference; unused in this function
    input: JString<'a>,                      // Input string from Java
    custom_start_comment_syntax: JString<'a>, // Custom start comment syntax from Java
    custom_end_comment_syntax: JString<'a>,   // Custom end comment syntax from Java
    config_json: JString<'a>,                // Configuration JSON string from Java
) -> jstring {
    // Convert the input JString to a Rust String
    let input_string: String = match env.get_string(&input) {
        Ok(string) => string.into(),  // Successfully convert to String
        Err(e) => {
            let error_message = format!("Error converting input JString to Rust String: {:?}", e);
            // Return error message if conversion fails
            return env.new_string(error_message).expect("Failed to create error string").into_raw();
        }
    };

    // Convert the custom start comment syntax JString to Rust String
    let start_syntax: String = match env.get_string(&custom_start_comment_syntax) {
        Ok(string) => string.into(),
        Err(e) => {
            let error_message = format!("Error converting custom start syntax JString to Rust String: {:?}", e);
            // Return error message if conversion fails
            return env.new_string(error_message).expect("Failed to create error string").into_raw();
        }
    };

    // Convert the custom end comment syntax JString to Rust String
    let end_syntax: String = match env.get_string(&custom_end_comment_syntax) {
        Ok(string) => string.into(),
        Err(e) => {
            let error_message = format!("Error converting custom end syntax JString to Rust String: {:?}", e);
            // Return error message if conversion fails
            return env.new_string(error_message).expect("Failed to create error string").into_raw();
        }
    };

    // Convert the JSON configuration JString to Rust String
    let json_string: String = match env.get_string(&config_json) {
        Ok(string) => string.into(),
        Err(e) => {
            let error_message = format!("Error converting JString to Rust String: {:?}", e);
            // Return error message if conversion fails
            return env.new_string(error_message).expect("Failed to create error string").into_raw();
        }
    };

    // Deserialize the JSON string into ApplicabilityConfigElement
    let applicability_config: ApplicabilityConfigElement = match serde_json::from_str(&json_string) {
        Ok(config) => config,  // Successfully deserialize into the config struct
        Err(e) => {
            let error_message = format!("Error deserializing JSON: {:?}", e);
            // Return error message if deserialization fails
            return env.new_string(error_message).expect("Failed to create error string").into_raw();
        }
    };

    // Call parse_applicability to parse the input string with the specified syntaxes
    let content_result = parse_applicability(&input_string, &start_syntax, &end_syntax);
    let contents = match content_result {
        Ok((_remaining, results)) => results,  // Successfully parsed contents
        Err(_) => {
            let error_message = "Failed to unwrap parsed AST".to_string();
            // Return error message if parsing fails
            return env.new_string(error_message).expect("Failed to create error string").into_raw();
        },
    };

    // Create a copy of the parsed contents for processing
    let copy = contents.clone();
    // Get the substitutions from the config; defaults to empty if not present
    let substitutions = applicability_config.clone().get_substitutions().unwrap_or_default();

    // Sanitize the contents using the substitutions and configuration features
    let sanitized_content = copy.iter().cloned().map(|c| {
        c.substitute(&substitutions)  // Apply substitutions to each content item
            .sanitize(
                applicability_config.clone().get_features(),
                &applicability_config.clone().get_name(),
                &substitutions,
                applicability_config.get_parent_group(),
                Some(applicability_config.get_configs().as_slice()),
            )
            .into()  // Convert sanitized item back to String
    }).collect::<Vec<String>>()  // Collect all sanitized items into a Vec
    .join("");  // Join the sanitized items into a single String

    // Match against the ApplicabilityConfigElement enum to determine the type and create a response message
    let message = match applicability_config {
        ApplicabilityConfigElement::Config(_) => "Matched Config".to_string(),
        ApplicabilityConfigElement::ConfigGroup(_) => "Matched ConfigGroup".to_string(),
        ApplicabilityConfigElement::LegacyConfig(_) => "Matched LegacyConfig".to_string(),
    };

    // Combine the sanitized content and the message for the final response
    let response_message = format!("{}\n{}", sanitized_content, message);

    // Return the combined response message as a jstring to Java
    env.new_string(response_message).expect("Failed to create message string").into_raw()
}
