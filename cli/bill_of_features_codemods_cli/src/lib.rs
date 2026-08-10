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
use bill_of_features_codemods::{
    BillOfFeaturesCodeModOptions, JsonMigrationError, LegacyMigrationError, TomlMigrationError,
    migrate_from_legacy, migrate_to_json, migrate_to_toml,
};
use clap::{Parser, Subcommand};
use thiserror::Error;
#[derive(Parser, Debug)]
#[clap(
    verbatim_doc_comment,
    about = "Performs migrations on the bill of features.",
    long_about = r#"Performs migrations on the bill of features."#
)]
pub struct BillOfFeaturesCodeModArgs {
    #[command(subcommand)]
    pub command: Commands,
}
#[derive(Debug, Subcommand)]
pub enum Commands {
    #[command(name = "from-legacy")]
    Legacy(BillOfFeaturesCodeModLegacyArgs),
    #[command(name = "to-toml")]
    Toml(BillOfFeaturesCodeModTomlArgs),
    #[command(name = "to-json")]
    Json(BillOfFeaturesCodeModJsonArgs),
}

#[derive(Parser, Debug)]
#[clap(
    verbatim_doc_comment,
    about = "Convert Legacy Bill Of Features to Configuration Bill Of Features.",
    long_about = r#"Converts the Bill of Features to a "Configuration" Bill Of Features 
    which contains the following fields:
    name: string
    group: string,
    features: string | {tag:string, value:string}
    substitutions: string | {match_text: string, substitute: string}
    It will also convert it to TOML.
    "#
)]
pub struct BillOfFeaturesCodeModLegacyArgs {
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
}
#[derive(Parser, Debug)]
#[clap(
    verbatim_doc_comment,
    about = "Convert Bill Of Features to toml format."
)]
pub struct BillOfFeaturesCodeModTomlArgs {
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
}
#[derive(Parser, Debug)]
#[clap(
    verbatim_doc_comment,
    about = "Convert Bill Of Features to json format."
)]
pub struct BillOfFeaturesCodeModJsonArgs {
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
}

impl BillOfFeaturesCodeModLegacyArgs {
    pub fn new(path: std::path::PathBuf) -> Self {
        BillOfFeaturesCodeModLegacyArgs {
            applicability_config: path,
        }
    }
}
impl BillOfFeaturesCodeModTomlArgs {
    pub fn new(path: std::path::PathBuf) -> Self {
        BillOfFeaturesCodeModTomlArgs {
            applicability_config: path,
        }
    }
}
impl BillOfFeaturesCodeModJsonArgs {
    pub fn new(path: std::path::PathBuf) -> Self {
        BillOfFeaturesCodeModJsonArgs {
            applicability_config: path,
        }
    }
}

impl From<BillOfFeaturesCodeModLegacyArgs> for BillOfFeaturesCodeModOptions {
    fn from(value: BillOfFeaturesCodeModLegacyArgs) -> Self {
        BillOfFeaturesCodeModOptions::new(value.applicability_config)
    }
}

impl From<BillOfFeaturesCodeModTomlArgs> for BillOfFeaturesCodeModOptions {
    fn from(value: BillOfFeaturesCodeModTomlArgs) -> Self {
        BillOfFeaturesCodeModOptions::new(value.applicability_config)
    }
}

impl From<BillOfFeaturesCodeModJsonArgs> for BillOfFeaturesCodeModOptions {
    fn from(value: BillOfFeaturesCodeModJsonArgs) -> Self {
        BillOfFeaturesCodeModOptions::new(value.applicability_config)
    }
}

pub fn perform_conversions(
    args: BillOfFeaturesCodeModArgs,
) -> Result<(), BillOfFeaturesCodeModError> {
    match args.command {
        Commands::Legacy(bill_of_features_code_mod_legacy_args) => {
            convert_legacy(bill_of_features_code_mod_legacy_args)?;
        }
        Commands::Toml(bill_of_features_code_mod_toml_args) => {
            migrate_to_toml(bill_of_features_code_mod_toml_args.into())?;
        }
        Commands::Json(bill_of_features_code_mod_json_args) => {
            migrate_to_json(bill_of_features_code_mod_json_args.into())?;
        }
    }
    Ok(())
}

pub fn convert_legacy(args: BillOfFeaturesCodeModLegacyArgs) -> Result<(), LegacyMigrationError> {
    migrate_from_legacy(args.into())?;
    Ok(())
}

#[derive(Debug, Error)]
pub enum BillOfFeaturesCodeModError {
    #[error("{}",.0)]
    LegacyCodeMod(#[from] LegacyMigrationError),
    #[error("{}",.0)]
    TomlCodeMod(#[from] TomlMigrationError),
    #[error("{}",.0)]
    JsonCodeMod(#[from] JsonMigrationError),
}
