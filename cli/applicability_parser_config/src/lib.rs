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
use std::path::Path;

use tracing::info;
pub mod applic_config;

enum SupportedSchema {
    Markdown,
    CppLike,
    Rust,
    BuildFile,
    LaTeX,
    Custom(String, String),
    NotSupported,
}

// Sets the comment syntax to the defaults if they are defined for a given file type.
//
// Currently supported:
//
//     .md
//     .cpp
//     .cxx
//     .cc
//     .c
//     .hpp
//     .hxx
//     .hh
//     .h
//     .rs
//     .bzl
//     .bazel
//     .tex
//     .gpj
//     .mk
//     .opt
//     WORKSPACE
//     BUILD
//     fileApplicability
//     applicability
pub fn get_comment_syntax(
    file: &Path,
    start_comment_syntax: &str,
    end_comment_syntax: &str,
) -> (String, String) {
    let schema = get_schema(file, start_comment_syntax, end_comment_syntax);
    let (start_comment_syntax, end_comment_syntax) = match schema {
        SupportedSchema::Markdown => ("``".to_owned(), "``".to_owned()),
        SupportedSchema::CppLike => ("//".to_owned(), "".to_owned()),
        SupportedSchema::Rust => ("//".to_owned(), "".to_owned()),
        SupportedSchema::LaTeX => ("\\if".to_owned(), "{}".to_owned()),
        SupportedSchema::BuildFile => ("#".to_owned(), "".to_owned()),
        SupportedSchema::Custom(start, end) => (start, end),
        SupportedSchema::NotSupported => ("".to_owned(), "".to_owned()),
    };
    info!(
        "\r\n start comment syntax {:#?}\r\n end comment syntax {:#?}",
        start_comment_syntax, end_comment_syntax
    );
    (start_comment_syntax, end_comment_syntax)
}

pub fn is_schema_supported(
    file: &Path,
    start_comment_syntax: &str,
    end_comment_syntax: &str,
) -> bool {
    let schema = get_schema(file, start_comment_syntax, end_comment_syntax);
    match schema {
        SupportedSchema::NotSupported => false,
        _rest => true,
    }
}

fn get_schema(
    file: &Path,
    start_comment_syntax: &str,
    end_comment_syntax: &str,
) -> SupportedSchema {
    let file_ref_copy = file;
    let ext = match file_ref_copy.extension() {
        Some(extension) => extension.to_str(),
        None => None, //do nothing
    };
    let name = match file_ref_copy.file_name() {
        Some(file_name) => file_name.to_str(),
        None => None,
    };
    let start_comment_syntax_length = start_comment_syntax.len();
    let end_comment_syntax_length = end_comment_syntax.len();
    let custom_syntax_length = start_comment_syntax_length + end_comment_syntax_length;
    let schema = match ext {
        Some("md") => SupportedSchema::Markdown,
        Some("cpp" | "cxx" | "cc" | "c" | "hpp" | "hxx" | "hh" | "h") => SupportedSchema::CppLike,
        Some("rs") => SupportedSchema::Rust,
        Some("tex") => SupportedSchema::LaTeX,
        Some("bzl" | "bazel" | "fileApplicability" | "applicability" | "gpj" | "mk" | "opt") => {
            SupportedSchema::BuildFile
        }
        None => match name {
            Some(
                "WORKSPACE" | "BUILD" | ".fileApplicability" | ".applicability" | "Makefile"
                | "makefile" | "MAKEFILE",
            ) => SupportedSchema::BuildFile,
            _rest => match custom_syntax_length {
                0 => SupportedSchema::NotSupported,
                _rest => SupportedSchema::Custom(
                    start_comment_syntax.to_owned(),
                    end_comment_syntax.to_owned(),
                ),
            },
        },
        _rest => match custom_syntax_length {
            0 => SupportedSchema::NotSupported,
            _rest => SupportedSchema::Custom(
                start_comment_syntax.to_owned(),
                end_comment_syntax.to_owned(),
            ),
        },
    };
    schema
}

pub fn get_file_contents(file: &Path) -> String {
    match std::fs::read_to_string(file) {
        Ok(i) => i,
        Err(e) => panic!(
            "Can't convert file {:#?} to bytes. \n Error: {:#?}",
            file.as_os_str(),
            e
        ),
    }
}
