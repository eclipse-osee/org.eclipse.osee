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

use bill_of_features::{
    BillOfFeaturesConfig, BillOfFeaturesEnum, ReadBillOfFeaturesConfigError, read_bill_of_features,
};
use thiserror::Error;

pub struct BillOfFeaturesCodeModOptions {
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
    applicability_config: std::path::PathBuf,
}

impl BillOfFeaturesCodeModOptions {
    pub fn new(path: std::path::PathBuf) -> Self {
        BillOfFeaturesCodeModOptions {
            applicability_config: path,
        }
    }
}

pub fn migrate_from_legacy(args: BillOfFeaturesCodeModOptions) -> Result<(), LegacyMigrationError> {
    let existing_config = read_bill_of_features(args.applicability_config.as_path())?;
    let new_config = match existing_config {
        BillOfFeaturesEnum::LegacyConfig(bill_of_features_legacy) => {
            BillOfFeaturesEnum::Config(Into::<BillOfFeaturesConfig>::into(bill_of_features_legacy))
        }
        BillOfFeaturesEnum::Config(bill_of_features_config) => {
            BillOfFeaturesEnum::Config(bill_of_features_config)
        }
        BillOfFeaturesEnum::ConfigGroup(bill_of_features_config_group) => {
            BillOfFeaturesEnum::ConfigGroup(bill_of_features_config_group)
        }
    };
    let serialized_data = toml::to_string_pretty(&new_config)?;
    let path_to_write = args.applicability_config.with_extension("toml");
    if let Ok(exists) = std::fs::exists(&path_to_write)
        && exists
    {
        std::fs::remove_file(&path_to_write)?;
    }
    std::fs::write(&path_to_write, serialized_data)?;
    Ok(())
}

pub fn migrate_to_toml(args: BillOfFeaturesCodeModOptions) -> Result<(), TomlMigrationError> {
    let existing_config = read_bill_of_features(args.applicability_config.as_path())?;
    let serialized_data = toml::to_string_pretty(&existing_config)?;
    let path_to_write = args.applicability_config.with_extension("toml");
    if let Ok(exists) = std::fs::exists(&path_to_write)
        && exists
    {
        std::fs::remove_file(&path_to_write)?;
    }
    std::fs::write(&path_to_write, serialized_data)?;
    Ok(())
}

pub fn migrate_to_json(args: BillOfFeaturesCodeModOptions) -> Result<(), JsonMigrationError> {
    let existing_config = read_bill_of_features(args.applicability_config.as_path())?;
    let serialized_data = serde_json::to_string_pretty(&existing_config)?;
    let path_to_write = args.applicability_config.with_extension("json");
    if let Ok(exists) = std::fs::exists(&path_to_write)
        && exists
    {
        std::fs::remove_file(&path_to_write)?;
    }
    std::fs::write(&path_to_write, serialized_data)?;
    Ok(())
}
#[derive(Debug, Error)]
pub enum LegacyMigrationError {
    #[error("{}", .0)]
    ReadBillOfFeaturesConfigError(#[from] ReadBillOfFeaturesConfigError),
    #[error("{}", .0)]
    TomlSerializationError(#[from] toml::ser::Error),
    #[error("{}", .0)]
    Io(#[from] std::io::Error),
}
#[derive(Debug, Error)]
pub enum TomlMigrationError {
    #[error("{}", .0)]
    ReadBillOfFeaturesConfigError(#[from] ReadBillOfFeaturesConfigError),
    #[error("{}", .0)]
    TomlSerializationError(#[from] toml::ser::Error),
    #[error("{}", .0)]
    Io(#[from] std::io::Error),
}
#[derive(Debug, Error)]
pub enum JsonMigrationError {
    #[error("{}", .0)]
    ReadBillOfFeaturesConfigError(#[from] ReadBillOfFeaturesConfigError),
    #[error("{}", .0)]
    JsonSerializationError(#[from] serde_json::Error),
    #[error("{}", .0)]
    Io(#[from] std::io::Error),
}
