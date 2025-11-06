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
    fs::{File, read_to_string},
    path::Path,
};

use serde::{Deserialize, Serialize};
use thiserror::Error;

use crate::{FeatureDefinition, calculate_default_allow_multiple_values};

#[derive(Debug, Deserialize, Serialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct Version0FeatureDefinition<I = String> {
    pub name: I,
    pub values: Vec<I>,
    pub description: I,
    pub product_applicabilities: Option<Vec<I>>,
    pub applic_constraint: Option<I>,
}
impl<I> From<Version0FeatureDefinition<I>> for FeatureDefinition<I>
where
    for<'a> &'a I: Into<String>,
{
    fn from(value: Version0FeatureDefinition<I>) -> Self {
        let values_as_string = value.values.iter().map(|x| x.into()).collect::<Vec<_>>();
        let values = values_as_string.as_slice();
        Self {
            name: value.name,
            values: value.values,
            description: value.description,
            product_applicabilities: value.product_applicabilities,
            applic_constraint: value.applic_constraint,
            allow_multiple_values: calculate_default_allow_multiple_values(values),
        }
    }
}
#[derive(Debug, Error)]
pub enum ReadVersion0FeatureDefinitionError {
    #[error("Could not parse PLE model JSON \n{:?}: \tat line {:?} column {:?}",.0.classify(), .0.line(),.0.column())]
    JSON(#[from] serde_json::Error),
    #[error("Error reading PLE model: {:?}",.0)]
    Io(#[from] std::io::Error),
    #[error("Could not parse PLE model TOML \n{:?}: \tat {:?}",.0.to_string(), .0.line_col())]
    Toml(#[from] toml::de::Error),
    #[error("PLE Model has incorrect file extension. Received: {:?}, want: [toml, json]", .0)]
    IncorrectFileExtension(String),
    #[error("PLE Model has no file extension. Expected formats: [toml, json]")]
    NoFileExtension,
}
pub fn read_version0_feature_definition(
    path: &Path,
) -> Result<Vec<Version0FeatureDefinition>, ReadVersion0FeatureDefinitionError> {
    let processing = (path, path.extension());
    match processing {
        (path, Some(file_ext)) => match file_ext.to_str() {
            Some("json") => {
                let applic_file = File::open(path);
                match applic_file {
                    Ok(file) => serde_json::from_reader(file).map_err(|x| x.into()),
                    Err(e) => Err(e.into()),
                }
            }
            Some("toml") => {
                let file_contents = read_to_string(path);
                match file_contents {
                    Ok(c) => toml::de::from_str(&c).map_err(|x| x.into()),
                    Err(e) => Err(e.into()),
                }
            }
            Some(x) => Err(ReadVersion0FeatureDefinitionError::IncorrectFileExtension(
                x.to_string(),
            )),
            _ => Err(ReadVersion0FeatureDefinitionError::NoFileExtension),
        },
        _ => Err(ReadVersion0FeatureDefinitionError::NoFileExtension),
    }
}
