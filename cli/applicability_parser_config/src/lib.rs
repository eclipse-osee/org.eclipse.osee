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
use std::{fmt::Debug, path::Path};

use applicability::applic_tag::ApplicabilityTag;
use applicability_document_schema::{
    DocTypeConfig, StringOrByteArray, SupportedSchema, get_doc_config, get_schema_from_file,
    get_schema_from_file_name_and_extension,
};
use applicability_parser_errors::ApplicabilityParserError;
use applicability_parser_v2::parse_applicability;
use applicability_tokens_to_ast::{
    tree::{ApplicabilityExprKind, Text},
    updatable::UpdatableValue,
};
use nom::{AsBytes, AsChar, Compare, FindSubstring, Input, Offset};
use nom_locate::LocatedSpan;
use tracing::debug;
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
//     .tex
//     .adoc
//     .bat
//     .cmd
//     .java
//     .gpj
//     .mk
//     .opt
//     WORKSPACE
//     BUILD
//     fileApplicability
//     applicability
pub fn get_comment_syntax_from_file(
    file: &Path,
    start_comment_syntax: &str,
    end_comment_syntax: &str,
) -> (String, String) {
    let schema = get_schema_from_file(file, start_comment_syntax, end_comment_syntax);
    let (start_comment_syntax, end_comment_syntax) = match schema {
        SupportedSchema::Markdown => ("``".to_owned(), "``".to_owned()),
        SupportedSchema::CppLike => ("//".to_owned(), "".to_owned()),
        SupportedSchema::Rust => ("//".to_owned(), "".to_owned()),
        SupportedSchema::LaTeX => ("\\if".to_owned(), "{}".to_owned()),
        SupportedSchema::BuildFile => ("#".to_owned(), "".to_owned()),
        SupportedSchema::Custom(start, end) => (start, end),
        SupportedSchema::NotSupported => ("".to_owned(), "".to_owned()),
        SupportedSchema::Plantuml => ("'".to_owned(), "".to_owned()),
    };
    debug!(
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
    applicability_document_schema::is_schema_supported(
        file,
        start_comment_syntax,
        end_comment_syntax,
    )
}

type GetConfigError<I> =
    nom::Err<ApplicabilityParserError<LocatedSpan<I, ((usize, u32), (usize, u32))>>>;

pub fn get_config_for_name_and_ext<'a, 'b, I>(
    file_name: &str,
    file_extension: &str,
) -> impl Fn(I) -> Result<Vec<ApplicabilityExprKind<I>>, GetConfigError<I>>
where
    I: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default
        + Clone
        + Debug
        + 'a,
    StringOrByteArray<'b>: From<I>,
    'a: 'b,
    <I as Input>::Item: AsChar,
    ApplicabilityTag<I, String>: From<I>,
{
    let schema =
        get_schema_from_file_name_and_extension(Some(file_name), Some(file_extension), "", "");
    let config = get_doc_config(schema);
    move |input| {
        let span = LocatedSpan::new_extra(input, ((0, 0), (0, 0)));
        match &config {
            DocTypeConfig::Md(applicabilty_markdown_lexer_config) => {
                parse_applicability(span, applicabilty_markdown_lexer_config)
            }
            DocTypeConfig::Cpp(applicabilty_cpp_like_lexer_config) => {
                parse_applicability(span, applicabilty_cpp_like_lexer_config)
            }
            DocTypeConfig::Build(applicability_build_file_lexer_config) => {
                parse_applicability(span, applicability_build_file_lexer_config)
            }
            DocTypeConfig::Rust(applicabilty_rust_lexer_config) => {
                parse_applicability(span, applicabilty_rust_lexer_config)
            }
            DocTypeConfig::Latex(applicability_latex_lexer_config) => {
                parse_applicability(span, applicability_latex_lexer_config)
            }
            DocTypeConfig::Plantuml(applicability_plantuml_lexer_config) => {
                parse_applicability(span, applicability_plantuml_lexer_config)
            }
            DocTypeConfig::Custom(applicability_custom_lexer_config) => {
                parse_applicability(span, applicability_custom_lexer_config)
            }
            DocTypeConfig::NotSupported => Ok(vec![ApplicabilityExprKind::Text(Text {
                text: span.into_fragment(),
                start_position: UpdatableValue::new((0, 0)),
                end_position: UpdatableValue::new((0, 0)),
            })]),
        }
    }
}

#[tracing::instrument(name = "Fetching document configuration")]
pub fn get_config_from_file<'a, 'b, I>(
    file: &Path,
) -> impl Fn(I) -> Result<Vec<ApplicabilityExprKind<I>>, GetConfigError<I>>
where
    I: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default
        + Clone
        + Debug
        + 'a,
    StringOrByteArray<'b>: From<I>,
    'a: 'b,
    <I as Input>::Item: AsChar,
    ApplicabilityTag<I, String>: From<I>,
{
    let schema = get_schema_from_file(file, "", "");
    let config = get_doc_config(schema);
    move |input| {
        let span = LocatedSpan::new_extra(input, ((0, 0), (0, 0)));
        match &config {
            DocTypeConfig::Md(applicabilty_markdown_lexer_config) => {
                parse_applicability(span, applicabilty_markdown_lexer_config)
            }
            DocTypeConfig::Cpp(applicabilty_cpp_like_lexer_config) => {
                parse_applicability(span, applicabilty_cpp_like_lexer_config)
            }
            DocTypeConfig::Build(applicability_build_file_lexer_config) => {
                parse_applicability(span, applicability_build_file_lexer_config)
            }
            DocTypeConfig::Rust(applicabilty_rust_lexer_config) => {
                parse_applicability(span, applicabilty_rust_lexer_config)
            }
            DocTypeConfig::Latex(applicability_latex_lexer_config) => {
                parse_applicability(span, applicability_latex_lexer_config)
            }
            DocTypeConfig::Plantuml(applicability_plantuml_lexer_config) => {
                parse_applicability(span, applicability_plantuml_lexer_config)
            }
            DocTypeConfig::Custom(applicability_custom_lexer_config) => {
                parse_applicability(span, applicability_custom_lexer_config)
            }
            DocTypeConfig::NotSupported => Ok(vec![ApplicabilityExprKind::Text(Text {
                text: span.into_fragment(),
                start_position: UpdatableValue::new((0, 0)),
                end_position: UpdatableValue::new((0, 0)),
            })]),
        }
    }
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
