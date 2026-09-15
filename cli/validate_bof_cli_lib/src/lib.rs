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
use validate_bof::{
    ValidateBillOfFeaturesError, ValidateBillOfFeaturesOptions, validate_bill_of_features_project,
};
#[derive(Parser, Debug)]
#[clap(
    verbatim_doc_comment,
    about = "Validate Bill Of Features and PLE Model(s) are valid",
    long_about = r#"Validate Bill Of Features and PLE Model(s) are valid
The following conditions are checked:
- Feature Name exists in the PLE Model, but not the Bill Of Features
- Feature Name exists in the Bill Of Features, but not the PLE Model
- Feature Value exists in the Bill Of Features, but not the PLE Model"#
)]
pub struct ValidateBofOptions {
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
impl ValidateBofOptions {
    pub fn new(applicability_config: std::path::PathBuf, in_dir: std::path::PathBuf) -> Self {
        ValidateBofOptions {
            applicability_config,
            in_dir,
        }
    }
}
pub fn validate_bill_of_features(
    args: ValidateBofOptions,
) -> Result<(), ValidateBillOfFeaturesError> {
    let options = args.into();
    validate_bill_of_features_project(options)
}

impl From<ValidateBofOptions> for ValidateBillOfFeaturesOptions {
    fn from(value: ValidateBofOptions) -> Self {
        ValidateBillOfFeaturesOptions::new(value.applicability_config, value.in_dir)
    }
}
