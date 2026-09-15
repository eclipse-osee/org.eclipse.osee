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
use feature_definition::{
    FeatureDefinitionSerializationContainer, ReadVersion0FeatureDefinitionError,
    read_version0_feature_definition,
};
use thiserror::Error;

pub struct MigrateV0ToV1Options {
    pub ple_model: std::path::PathBuf,
}

pub fn migrate_v0_to_v1(args: MigrateV0ToV1Options) -> Result<(), MigrateV0ToV1Error> {
    let config = read_version0_feature_definition(&args.ple_model)?;
    let new_config: FeatureDefinitionSerializationContainer = config
        .into_iter()
        .map(|x| x.into())
        .collect::<Vec<_>>()
        .into();
    let serialized_data = toml::to_string_pretty(&new_config)?;
    std::fs::write(args.ple_model.with_extension("toml"), serialized_data)?;
    Ok(())
}
#[derive(Debug, Error)]
pub enum MigrateV0ToV1Error {
    #[error("{}", .0)]
    ReadVersion0FeatureDefinitionError(#[from] ReadVersion0FeatureDefinitionError),
    #[error("{}", .0)]
    TomlSerializationError(#[from] toml::ser::Error),
    #[error("{}", .0)]
    Io(#[from] std::io::Error),
}
