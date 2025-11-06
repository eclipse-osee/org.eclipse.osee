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
use feature_definition::{ReadPLEModelError, read_ple_model};
use pat_config::{PleConfigReadError, read_ple_config};
use thiserror::Error;

pub struct AddPLEModelOptions {
    pub ple_model: std::path::PathBuf,
    pub ple_config: std::path::PathBuf,
}

pub fn add_model_to_config(args: AddPLEModelOptions) -> Result<(), AddPleModelError> {
    let ple_model = read_ple_model(&args.ple_model)?;
    let mut ple_config = read_ple_config(&args.ple_config)?;
    if let Some(mut definitions) = ple_config.features {
        definitions.extend(ple_model.features);
        ple_config.features = Some(definitions);
    } else {
        ple_config.features = Some(ple_model.features);
    }
    let serialized_data = toml::to_string_pretty(&ple_config)?;
    std::fs::write(&args.ple_config, serialized_data)?;
    Ok(())
}
#[derive(Debug, Error)]
pub enum AddPleModelError {
    #[error("{}", .0)]
    ReadPLEModelError(#[from] ReadPLEModelError),
    #[error("{}", .0)]
    ReadPLEConfigError(#[from] PleConfigReadError),
    #[error("{}", .0)]
    TomlSerializationError(#[from] toml::ser::Error),
    #[error("{}", .0)]
    Io(#[from] std::io::Error),
}
