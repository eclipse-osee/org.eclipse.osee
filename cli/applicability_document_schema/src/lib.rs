use applicability_lexer_config_build_file::ApplicabilityBuildFileLexerConfig;
use applicability_lexer_config_cpp_like::ApplicabilityCppLikeLexerConfig;
use applicability_lexer_config_custom::ApplicabilityCustomLexerConfig;
use applicability_lexer_config_latex::ApplicabilityLatexLexerConfig;
use applicability_lexer_config_markdown::ApplicabilityMarkdownLexerConfig;
use applicability_lexer_config_plantuml::ApplicabilityPlantumlLexerConfig;
use applicability_lexer_config_rust::ApplicabilityRustLexerConfig;
use std::path::Path;
pub enum SupportedSchema {
    Markdown,
    CppLike,
    Rust,
    BuildFile,
    LaTeX,
    Plantuml,
    Custom(String, String),
    NotSupported,
}
pub fn is_schema_supported(
    file: &Path,
    start_comment_syntax: &str,
    end_comment_syntax: &str,
) -> bool {
    let schema = get_schema_from_file(file, start_comment_syntax, end_comment_syntax);
    match schema {
        SupportedSchema::NotSupported => false,
        _rest => true,
    }
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
pub fn get_schema_from_file(
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
        Some("md" | "adoc") => get_schema("md".into()),
        Some("cpp" | "cxx" | "cc" | "c" | "hpp" | "hxx" | "hh" | "h" | "java") => get_schema("c".into()),
        Some("rs") => get_schema("rs".into()),
        Some("tex") => get_schema("latex".into()),
        Some("bzl" | "bazel" | "fileApplicability" | "applicability" | "gpj" | "mk" | "opt" | "bat" | "cmd") => {
            get_schema("build".into())
        }
        Some("puml" | "pu" | "plantuml") => get_schema("plantuml".into()),
        None => match name {
            Some(
                "WORKSPACE" | "BUILD" | ".fileApplicability" | ".applicability" | "Makefile"
                | "makefile" | "MAKEFILE",
            ) => get_schema("build".into()),
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
//TODO: find a better place for this to live
pub enum StringOrByteArray<'a> {
    Str(&'a str),
    U8(&'a [u8]),
}

impl<'a, 'b> From<&'a str> for StringOrByteArray<'b>
where
    'a: 'b,
{
    fn from(value: &'a str) -> Self {
        StringOrByteArray::Str(value)
    }
}

impl<'a, 'b> From<&'a [u8]> for StringOrByteArray<'b>
where
    'a: 'b,
{
    fn from(value: &'a [u8]) -> Self {
        StringOrByteArray::U8(value)
    }
}

pub fn get_schema(schema_type: StringOrByteArray) -> SupportedSchema {
    match schema_type {
        StringOrByteArray::Str("md")
        | StringOrByteArray::Str("")
        | StringOrByteArray::U8(b"md")
        | StringOrByteArray::U8(b"")
        | StringOrByteArray::Str("adoc") 
        | StringOrByteArray::U8(b"adoc") => SupportedSchema::Markdown,
        StringOrByteArray::Str("plantuml") | StringOrByteArray::U8(b"plantuml") => {
            SupportedSchema::Plantuml
        }
        StringOrByteArray::Str("c")
        | StringOrByteArray::Str("h")
        | StringOrByteArray::Str("cpp")
        | StringOrByteArray::Str("hpp")
        | StringOrByteArray::Str("cc")
        | StringOrByteArray::Str("hh")
        | StringOrByteArray::Str("c++")
        | StringOrByteArray::Str("h++")
        | StringOrByteArray::Str("cxx")
        | StringOrByteArray::Str("hxx")
        | StringOrByteArray::U8(b"c")
        | StringOrByteArray::U8(b"h")
        | StringOrByteArray::U8(b"cpp")
        | StringOrByteArray::U8(b"hpp")
        | StringOrByteArray::U8(b"cc")
        | StringOrByteArray::U8(b"hh")
        | StringOrByteArray::U8(b"c++")
        | StringOrByteArray::U8(b"h++")
        | StringOrByteArray::U8(b"cxx")
        | StringOrByteArray::U8(b"hxx")
        | StringOrByteArray::Str("java") 
        | StringOrByteArray::U8(b"java") => SupportedSchema::CppLike,
        StringOrByteArray::Str("rs")
        | StringOrByteArray::Str("rust")
        | StringOrByteArray::U8(b"rs")
        | StringOrByteArray::U8(b"rust") => SupportedSchema::Rust,
        StringOrByteArray::Str("latex")
        | StringOrByteArray::Str("tex")
        | StringOrByteArray::Str("LaTeX")
        | StringOrByteArray::U8(b"latex")
        | StringOrByteArray::U8(b"tex")
        | StringOrByteArray::U8(b"LaTeX") => SupportedSchema::LaTeX,
        StringOrByteArray::Str("build")
        | StringOrByteArray::Str("starlark")
        | StringOrByteArray::Str(".mak")
        | StringOrByteArray::Str("MakeFile")
        | StringOrByteArray::Str("makefile")
        | StringOrByteArray::Str("GNUmakefile")
        | StringOrByteArray::U8(b"build")
        | StringOrByteArray::U8(b"starlark")
        | StringOrByteArray::U8(b".mak")
        | StringOrByteArray::U8(b"MakeFile")
        | StringOrByteArray::U8(b"makefile")
        | StringOrByteArray::U8(b"GNUmakefile")
        | StringOrByteArray::Str("bat") 
        | StringOrByteArray::U8(b"bat")
        | StringOrByteArray::Str("cmd") 
        | StringOrByteArray::U8(b"cmd") => SupportedSchema::BuildFile,
        _ => SupportedSchema::NotSupported,
    }
}

#[derive(Default)]
pub enum DocTypeConfig<'a, 'b, 'c, 'd, 'e> {
    Md(ApplicabilityMarkdownLexerConfig<'a, 'b, 'c, 'd>),
    Cpp(ApplicabilityCppLikeLexerConfig<'a, 'b, 'c, 'd, 'e>),
    Build(ApplicabilityBuildFileLexerConfig<'a>),
    Rust(ApplicabilityRustLexerConfig<'a, 'b, 'c, 'd, 'e>),
    Latex(ApplicabilityLatexLexerConfig<'a, 'b>),
    Plantuml(ApplicabilityPlantumlLexerConfig<'a, 'b, 'c, 'd>),
    Custom(ApplicabilityCustomLexerConfig<'a, 'b, 'c, 'd>),
    #[default]
    NotSupported,
}

pub fn get_doc_config<'a, 'b, 'c, 'd, 'e>(
    schema: SupportedSchema,
) -> DocTypeConfig<'a, 'b, 'c, 'd, 'e> {
    match schema {
        SupportedSchema::Markdown => DocTypeConfig::Md(ApplicabilityMarkdownLexerConfig::default()),
        SupportedSchema::CppLike => DocTypeConfig::Cpp(ApplicabilityCppLikeLexerConfig::default()),
        SupportedSchema::Rust => DocTypeConfig::Rust(ApplicabilityRustLexerConfig::default()),
        SupportedSchema::BuildFile => {
            DocTypeConfig::Build(ApplicabilityBuildFileLexerConfig::default())
        }
        SupportedSchema::LaTeX => DocTypeConfig::Latex(ApplicabilityLatexLexerConfig::default()),
        SupportedSchema::Custom(_, _) => {
            DocTypeConfig::Custom(ApplicabilityCustomLexerConfig::new("", ""))
        }
        SupportedSchema::NotSupported => DocTypeConfig::NotSupported,
        SupportedSchema::Plantuml => {
            DocTypeConfig::Plantuml(ApplicabilityPlantumlLexerConfig::default())
        }
    }
}
