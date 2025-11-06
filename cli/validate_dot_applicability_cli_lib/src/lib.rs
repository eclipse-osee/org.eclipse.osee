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
use clap::Parser;
use validate_dot_applicability::ValidateDotApplicabilityOptions;

#[derive(Parser, Debug)]
#[clap(
    verbatim_doc_comment,
    about = "Validate paths within .applicability, .fileApplicability are present.",
    long_about = r#"Validate paths within .applicability, .fileApplicability are present. 
    This will process for all configurations, even though an applicability config is required"#
)]
pub struct ValidateDotApplicabilityArgs {
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
impl ValidateDotApplicabilityArgs {
    pub fn new(
        applicability_config: std::path::PathBuf,
        input_directory: std::path::PathBuf,
    ) -> Self {
        ValidateDotApplicabilityArgs {
            applicability_config,
            in_dir: input_directory,
        }
    }
}
impl From<ValidateDotApplicabilityArgs> for ValidateDotApplicabilityOptions {
    fn from(value: ValidateDotApplicabilityArgs) -> Self {
        ValidateDotApplicabilityOptions::new(value.applicability_config, value.in_dir)
    }
}

pub fn validate(
    args: ValidateDotApplicabilityArgs,
) -> Result<(), validate_dot_applicability::ValidateDotApplicabilityErrors> {
    validate_dot_applicability::validate(args.into())
}
