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

use anyhow::Ok;
use clap::{Parser, Subcommand};
use validate_bof_cli_lib::ValidateBofOptions;
use validate_dot_applicability_cli_lib::ValidateDotApplicabilityArgs;

///
/// Various PLE validation utilities
#[derive(Parser, Debug)]
#[clap(
    author = "Luciano Vaglienti",
    version,
    verbatim_doc_comment,
    about = "Various PLE validation utilities"
)]
pub struct ValidateCliOptions {
    #[command(subcommand)]
    pub command: Commands,
}
#[derive(Debug, Subcommand)]
pub enum Commands {
    #[command(name = "all")]
    All(ValidateAllArgs),
    #[command(name = "bill-of-features")]
    Bof(ValidateBofOptions),
    #[command(name = ".applicability")]
    DotApplicability(ValidateDotApplicabilityArgs),
}
#[derive(Parser, Debug, Clone)]
#[clap(
    verbatim_doc_comment,
    about = "Perform all validations",
    long_about = r#"Perform all validations on the source repository."#
)]
pub struct ValidateAllArgs {
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
    applicability_config: std::path::PathBuf,
    /// The input directory to process files.
    #[clap(short, long)]
    in_dir: std::path::PathBuf,
}

impl From<ValidateAllArgs> for ValidateBofOptions {
    fn from(value: ValidateAllArgs) -> Self {
        ValidateBofOptions::new(value.applicability_config, value.in_dir)
    }
}

impl From<ValidateAllArgs> for ValidateDotApplicabilityArgs {
    fn from(value: ValidateAllArgs) -> Self {
        ValidateDotApplicabilityArgs::new(value.applicability_config, value.in_dir)
    }
}

pub fn validate(args: ValidateCliOptions) -> Result<(), anyhow::Error> {
    match args.command {
        Commands::Bof(validate_bof_options) => {
            validate_bof_cli_lib::validate_bill_of_features(validate_bof_options)?;
            Ok(())
        }
        Commands::DotApplicability(validate_dot_applicability_args) => {
            validate_dot_applicability_cli_lib::validate(validate_dot_applicability_args)?;
            Ok(())
        }
        Commands::All(validate_all_args) => {
            validate_bof_cli_lib::validate_bill_of_features(validate_all_args.clone().into())?;
            validate_dot_applicability_cli_lib::validate(validate_all_args.into())?;
            Ok(())
        }
    }
}
