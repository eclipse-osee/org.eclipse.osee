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
use clap::{ArgAction, Parser};
use clap_verbosity_flag::{Verbosity, WarnLevel};
use doc_definitions::{
    applic_file_example, applicability_definitions, dot_applicability_syntax_and_notes,
    pat_config_note, ple_applicability_tag_syntax_rules, ple_config_example, supported_file_types,
};

#[derive(Parser, Debug)]
#[clap(
    author = "Luciano Vaglienti",
    version,
    about = PatCliOptions::about(),
    long_about = PatCliOptions::long_about()
)]
pub struct PatCliOptions {
    #[command(flatten)]
    pub options: PatInternalCliOptions,
    /// Verbosity of output, defaults to warnings and errors.
    /// -q will have no output
    /// -v will show warnings,info and errors
    /// -vv will show warnings,info,errors, and debug
    /// -vvv will show warnings,info,errors, debug and trace output
    #[command(flatten)]
    pub verbose: Verbosity<WarnLevel>,
}

impl PatCliOptions {
    pub fn about() -> String {
        "".to_string()
            + r#"Project Applicability Tool(PAT)
----------------------------------------------------{n}"#
            + &PatInternalCliOptions::pat_about()
    }
    pub fn long_about() -> String {
        "".to_string()
            + r#"Project Applicability Tool(PAT)
----------------------------------------------------{n}"#
            + &PatInternalCliOptions::pat_long_about()
    }
}

#[derive(Parser, Debug)]
#[clap(author = "Luciano Vaglienti", version,about=PatInternalCliOptions::ple_about(), long_about=PatInternalCliOptions::ple_long_about(), verbatim_doc_comment)]
pub struct PatInternalCliOptions {
    /// Config file containing the valid applicabilities,configurations, and substitutions.
    /// An example:
    ///     {
    ///         "name":"PRODUCT_A",
    ///         "group":["abGroup"],
    ///         "features":["ENGINE_5=A2543","JHU_CONTROLLER=Excluded","ROBOT_ARM_LIGHT=Excluded","ROBOT_SPEAKER=SPKR_A"],
    ///         "substitutions":[
    ///             {"matchText":"SOME_SUBSTITUTION","substitute":"SOME NEW TEXT CONTENT"}
    ///         ]
    ///     }
    #[clap(short, long, verbatim_doc_comment)]
    pub(crate) applicability_config: std::path::PathBuf,

    /// The output directory for processed files.
    #[clap(short, long)]
    pub(crate) out_dir: std::path::PathBuf,

    /// The input directory to process files.
    #[clap(short, long)]
    pub(crate) in_dir: std::path::PathBuf,

    /// Excludes folders by default, and treats paths in .fileApplicability/.applicability as files/folders/patterns to include
    #[clap(short = 'x', long)]
    pub(crate) exclude: bool,

    /// Hides dotfiles. Default value: on
    #[clap(short = 'z', long, action=ArgAction::SetFalse)]
    pub(crate) skip_hidden: bool,
}
impl PatInternalCliOptions {
    pub fn base_about() -> String {
        "".to_string()
            + r#"Process .applicability & .fileApplicability files
to include/exclude certain folders/files based on
the PLE model and Bill of Features.{n}"#
    }
    pub fn pat_about() -> String {
        Self::base_about()
            + applicability_definitions()
            + dot_applicability_syntax_and_notes()
            + applic_file_example()
            + "{n}"
            + pat_config_note()
            + ple_config_example()
    }
    pub fn pat_long_about() -> String {
        Self::pat_about()
            + ple_applicability_tag_syntax_rules()
            + supported_file_types()
            + r#"
For unsupported file types, PAT will copy the file directly to the output directory instead of sanitizing it."#
    }
    pub fn ple_about() -> String {
        Self::base_about()
            + dot_applicability_syntax_and_notes()
            + applic_file_example()
            + "{n}"
            + pat_config_note()
            + ple_config_example()
    }
    pub fn ple_long_about() -> String {
        Self::ple_about()
            + applicability_definitions()
            + ple_applicability_tag_syntax_rules()
            + supported_file_types()
            + r#"
For unsupported file types, the PLE Compiler will copy the file directly to the output directory instead of sanitizing it."#
    }
}
