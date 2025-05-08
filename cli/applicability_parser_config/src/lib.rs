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
//     WORKSPACE
//     BUILD
//     fileApplicability
//     applicability
pub fn get_comment_syntax_from_file(
    file: &Path,
    start_comment_syntax: &str,
    end_comment_syntax: &str,
) -> (String, String) {
    let file_ref_copy = file;
    let ext = match file_ref_copy.extension() {
        Some(extension) => extension.to_str(),
        None => None, //do nothing
    };
    let name = match file_ref_copy.file_name() {
        Some(file_name) => file_name.to_str(),
        None => None,
    };
    let (start_comment_syntax, end_comment_syntax) =
        get_comment_syntax_from_file_name_and_extension(
            ext,
            name,
            start_comment_syntax,
            end_comment_syntax,
        );
    info!(
        "\r\n start comment syntax {:#?}\r\n end comment syntax {:#?}",
        start_comment_syntax, end_comment_syntax
    );
    (
        start_comment_syntax.to_string(),
        end_comment_syntax.to_string(),
    )
}

pub fn get_comment_syntax_from_file_name_and_extension<'a>(
    file_extension: Option<&'a str>,
    file_name: Option<&'a str>,
    start_comment_syntax: &'a str,
    end_comment_syntax: &'a str,
) -> (&'a str, &'a str) {
    match file_extension {
        Some("md") => ("``", "``"),
        Some("cpp" | "cxx" | "cc" | "c" | "hpp" | "hxx" | "hh" | "h" | "rs") => ("//", ""),
        Some("bzl" | "bazel" | "fileApplicability" | "applicability") => ("#", ""),
        None => match file_name {
            Some("WORKSPACE" | "BUILD" | ".fileApplicability" | ".applicability") => ("#", ""),
            _rest => (start_comment_syntax, end_comment_syntax),
        },
        _rest => (start_comment_syntax, end_comment_syntax),
    }
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
