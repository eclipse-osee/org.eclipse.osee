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

use std::{
    env,
    fs::{File, read_to_string},
    path::Path,
};

use applicability_parser_config::applic_config::ApplicabilityConfigElement;
use clap::{Parser, Subcommand};
use pat_config::{CompletePatConfig, from_str};
use thiserror::Error;
use tracing::{error, warn};

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
    #[command(name = "bill-of-features")]
    Bof(ValidateBofOptions),
}
///
/// Validate Bill Of Features and PLE Model(s) are valid
/// The following conditions are checked:
/// - Feature Name exists in the PLE Model, but not the Bill Of Features
/// - Feature Name exists in the Bill Of Features, but not the PLE Model
/// - Feature Value exists in the Bill Of Features, but not the PLE Model
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

fn read_pat_config(starting_path: &std::path::Path) -> Result<CompletePatConfig, anyhow::Error> {
    let config_path = starting_path.join(Path::new("ple-config.toml"));
    let contents = read_to_string(config_path.clone())?;
    let cwd = env::current_dir()?;
    match from_str(
        config_path.parent().unwrap_or(cwd.as_path()).to_owned(),
        &contents,
    ) {
        Ok(o) => Ok(o),
        Err(e) => Err(e.into()),
    }
}
pub fn validate_bof(args: ValidateBofOptions) -> anyhow::Result<()> {
    let in_dir = args.in_dir.as_path();
    let applic_processing = (
        args.applicability_config.clone(),
        args.applicability_config.extension(),
    );
    let mut applic_config: ApplicabilityConfigElement = match applic_processing {
        (path, Some(file_ext)) => match file_ext.to_str() {
            Some("json") => {
                let applic_file = File::open(path);
                match applic_file {
                    Ok(file) => match serde_json::from_reader(file) {
                        Ok(res) => res,
                        Err(e) => panic!(
                            "Could not parse applicability config JSON \n{:?}: \tat line {:?} column {:?}",
                            e.classify(),
                            e.line(),
                            e.column()
                        ),
                    },
                    Err(e) => panic!("Could not find applicability config {e:?}"),
                }
            }
            Some("toml") => {
                let file_contents = read_to_string(path);
                match file_contents {
                    Ok(c) => match toml::de::from_str(&c) {
                        Ok(res) => res,
                        Err(e) => panic!(
                            "Could not parse applicability config TOML \n{:?}: \tat {:?}",
                            e.to_string(),
                            e.line_col()
                        ),
                    },
                    Err(e) => panic!("Could not find applicability config {e:?}"),
                }
            }
            Some(x) => {
                panic!(
                    "Applicability Config has incorrect file extension. Received: {x:#?}, want: [toml, json]"
                )
            }
            _ => {
                panic!("Applicability Config has no file extension")
            }
        },
        _ => {
            panic!("Applicability Config has no file extension")
        }
    };
    let pat_config = read_pat_config(in_dir);
    if let Ok(config) = &pat_config
        && let Some(new_config) = &config.config
    {
        applic_config.merge(new_config);
    };
    let ple_model = match &pat_config {
        Ok(config) => match &config.features {
            Some(f) => f.as_slice(),
            _ => &[],
        },
        Err(_) => &[],
    };
    let results = validate_bof::validate(ple_model, &applic_config);
    if let Err(unwrapped_results) = results {
        unwrapped_results.errors.iter().for_each(|e1| match e1 {
            validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(
                tag,
            ) => error!("Tag Missing From PLE Model: {:#?}", tag),
            validate_bof::BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(
                tag,
                value,
            ) => error!(
                "Value Missing From PLE Model: {:#?} for Tag {:#?}",
                value, tag
            ),
            validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromBillOfFeatures(
                tag,
            ) => warn!("Tag Missing From Bill Of Features: {:#?}", tag),
        });
        if !unwrapped_results.errors.iter().filter(|x| matches!(x, validate_bof::BillOfFeaturesInternalValidationError::TagMissingFromFeatureModel(_) | validate_bof::BillOfFeaturesInternalValidationError::ValueMissingFromFeatureModel(_,_))).collect::<Vec<_>>().is_empty() {
            return Err(ValidateBofError::ValidationFailed.into());
        }
    }
    Ok(())
}

#[derive(Debug, Error)]
enum ValidateBofError {
    #[error("ValidationFailed")]
    ValidationFailed,
}
