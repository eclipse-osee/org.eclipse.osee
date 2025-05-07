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
use std::{default, path::Path};

use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_config_build_file::ApplicabilityBuildFileLexerConfig;
use applicability_lexer_config_cpp_like::ApplicabilityCppLikeLexerConfig;
use applicability_lexer_config_custom::ApplicabilityCustomLexerConfig;
use applicability_lexer_config_latex::ApplicabilityLatexLexerConfig;
use applicability_lexer_config_markdown::ApplicabilityMarkdownLexerConfig;
use applicability_lexer_config_rust::ApplicabilityRustLexerConfig;
use applicability_parser_v2::parse_applicability;
use applicability_tokens_to_ast::{
    tree::{ApplicabilityExprKind, Text},
    updatable::UpdatableValue,
};
use nom::{AsBytes, AsChar, Compare, FindSubstring, Input, Offset};
use nom_locate::LocatedSpan;
use tracing::debug;
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

    match ext {
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
    }
}
#[derive(Default)]
enum DocTypeConfig<'a, 'b, 'c, 'd, 'e> {
    Md(ApplicabilityMarkdownLexerConfig<'a, 'b>),
    Cpp(ApplicabilityCppLikeLexerConfig<'a, 'b, 'c, 'd, 'e>),
    Build(ApplicabilityBuildFileLexerConfig<'a>),
    Rust(ApplicabilityRustLexerConfig<'a, 'b, 'c, 'd, 'e>),
    Latex(ApplicabilityLatexLexerConfig<'a, 'b>),
    Custom(ApplicabilityCustomLexerConfig<'a, 'b, 'c, 'd>),
    #[default]
    NotSupported,
}
pub fn get_config<I>(file: &Path) -> impl Fn(I) -> Vec<ApplicabilityExprKind<I>>
where
    I: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default
        + Clone,
    <I as Input>::Item: AsChar,
    ApplicabilityTag<I, String>: From<I>,
    // T: IdentifyComments + SingleLineTerminated + SingleLineNonTerminated + MultiLine + Sync,
{
    let schema = get_schema(file, "", "");
    // let (start_comment_syntax, end_comment_syntax) = match schema {
    //     SupportedSchema::Markdown => ("``".to_owned(), "``".to_owned()),
    //     SupportedSchema::CppLike => ("//".to_owned(), "".to_owned()),
    //     SupportedSchema::Rust => ("//".to_owned(), "".to_owned()),
    //     SupportedSchema::LaTeX => ("\\if".to_owned(), "{}".to_owned()),
    //     SupportedSchema::BuildFile => ("#".to_owned(), "".to_owned()),
    //     SupportedSchema::Custom(start, end) => (start, end),
    //     SupportedSchema::NotSupported => ("".to_owned(), "".to_owned()),
    // };
    let config: DocTypeConfig = match schema {
        SupportedSchema::Markdown => DocTypeConfig::Md(ApplicabilityMarkdownLexerConfig::default()),
        SupportedSchema::CppLike => DocTypeConfig::Cpp(ApplicabilityCppLikeLexerConfig::default()),
        SupportedSchema::Rust => DocTypeConfig::Rust(ApplicabilityRustLexerConfig::default()),
        SupportedSchema::BuildFile => {
            DocTypeConfig::Build(ApplicabilityBuildFileLexerConfig::default())
        }
        SupportedSchema::LaTeX => DocTypeConfig::Latex(ApplicabilityLatexLexerConfig::default()),
        SupportedSchema::Custom(start, end) => {
            DocTypeConfig::Custom(ApplicabilityCustomLexerConfig::new("", ""))
        }
        SupportedSchema::NotSupported => DocTypeConfig::NotSupported,
    };
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
            DocTypeConfig::Custom(applicability_custom_lexer_config) => {
                parse_applicability(span, applicability_custom_lexer_config)
            }
            DocTypeConfig::NotSupported => {
                vec![ApplicabilityExprKind::Text(Text {
                    text: span.into_fragment(),
                    start_position: UpdatableValue::new((0, 0)),
                    end_position: UpdatableValue::new((0, 0)),
                })]
            }
        }
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
