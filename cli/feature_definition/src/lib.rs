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
use serde::{Deserialize, Serialize, de::Error};
use std::{
    fs::{File, read_to_string},
    path::Path,
};
use thiserror::Error;
use toml::map::Map;
mod v0;
pub use v0::{ReadVersion0FeatureDefinitionError, read_version0_feature_definition};
#[derive(Debug, Deserialize, Serialize, Clone)]
pub struct FeatureDefinitionSerializationContainer<I = String> {
    #[serde(rename = "feature")]
    pub features: Vec<FeatureDefinition<I>>,
}
impl<I> From<Vec<FeatureDefinition<I>>> for FeatureDefinitionSerializationContainer<I> {
    fn from(value: Vec<FeatureDefinition<I>>) -> Self {
        Self { features: value }
    }
}

#[derive(Debug, Serialize, Clone)]
pub struct FeatureDefinition<I = String> {
    pub name: I,
    pub values: Vec<I>,
    pub description: I,
    pub product_applicabilities: Option<Vec<I>>,
    pub applic_constraint: Option<I>,
    pub allow_multiple_values: bool,
}
#[derive(Debug, Deserialize, Serialize, Clone)]
#[serde(rename_all = "camelCase")]
struct FeatureDefinitionJson<I = String> {
    pub name: I,
    pub values: Vec<I>,
    pub description: I,
    pub product_applicabilities: Option<Vec<I>>,
    pub applic_constraint: Option<I>,
    pub allow_multiple_values: bool,
}
impl<I> From<FeatureDefinitionJson<I>> for FeatureDefinition<I> {
    fn from(value: FeatureDefinitionJson<I>) -> Self {
        Self {
            name: value.name,
            values: value.values,
            description: value.description,
            product_applicabilities: value.product_applicabilities,
            applic_constraint: value.applic_constraint,
            allow_multiple_values: value.allow_multiple_values,
        }
    }
}

#[derive(Debug, Deserialize, Serialize, Clone)]
#[serde(rename_all = "snake_case")]
struct FeatureDefinitionToml<I = String> {
    pub name: I,
    pub values: Vec<I>,
    pub description: I,
    pub product_applicabilities: Option<Vec<I>>,
    pub applic_constraint: Option<I>,
    pub allow_multiple_values: bool,
}

impl<I> From<FeatureDefinitionToml<I>> for FeatureDefinition<I> {
    fn from(value: FeatureDefinitionToml<I>) -> Self {
        Self {
            name: value.name,
            values: value.values,
            description: value.description,
            product_applicabilities: value.product_applicabilities,
            applic_constraint: value.applic_constraint,
            allow_multiple_values: value.allow_multiple_values,
        }
    }
}

impl<'de, I> Deserialize<'de> for FeatureDefinition<I>
where
    I: Deserialize<'de>,
{
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        #[derive(Deserialize)]
        #[serde(untagged)]
        enum FeatureDefinitionVariants<I> {
            Json(FeatureDefinitionJson<I>),
            Toml(FeatureDefinitionToml<I>),
        }
        match FeatureDefinitionVariants::<I>::deserialize(deserializer) {
            Ok(result) => match result {
                FeatureDefinitionVariants::Json(feature_definition_json) => {
                    Ok(feature_definition_json.into())
                }
                FeatureDefinitionVariants::Toml(feature_definition_toml) => {
                    Ok(feature_definition_toml.into())
                }
            },
            Err(deserializer_error) => Err(D::Error::custom(deserializer_error.to_string())),
        }
    }
}

pub enum FeatureDefinitionConversionError {
    MissingFields,
}

impl TryFrom<Map<String, toml::Value>> for FeatureDefinition {
    type Error = FeatureDefinitionConversionError;
    fn try_from(value: Map<String, toml::Value>) -> Result<Self, Self::Error> {
        if !value.contains_key("name") || !value.contains_key("values") {
            return Err(FeatureDefinitionConversionError::MissingFields);
        }
        let name = match value.get("name").and_then(|x| x.as_str()) {
            Some(x) => String::from(x),
            _ => String::from(""),
        };
        let values = match value.get("values").and_then(|x| x.as_array()) {
            Some(v) => v
                .iter()
                .filter_map(|x| match x {
                    toml::Value::String(i) => Some(i.clone()),
                    _ => None,
                })
                .collect::<Vec<String>>(),
            _ => vec![],
        };
        let description = match value.get("description").and_then(|x| x.as_str()) {
            Some(x) => String::from(x),
            _ => String::from(""),
        };
        let product_applicabilities = value
            .get("product_applicabilities")
            .and_then(|x: &toml::Value| x.as_array())
            .map(|apps| {
                apps.iter()
                    .filter_map(|x| match x {
                        toml::Value::String(i) => Some(i.clone()),
                        _ => None,
                    })
                    .collect::<Vec<String>>()
            });
        let constraint = value
            .get("applicability_constraint")
            .and_then(|x| x.as_str())
            .map(String::from);
        let allow_multiple_values =
            match value.get("allow_multiple_values").and_then(|x| x.as_bool()) {
                Some(allow) => allow,
                None => calculate_default_allow_multiple_values(values.as_slice()),
            };
        Ok(FeatureDefinition {
            name,
            values,
            description,
            product_applicabilities,
            applic_constraint: constraint,
            allow_multiple_values,
        })
    }
}
fn calculate_default_allow_multiple_values(values: &[String]) -> bool {
    if values.len() == 2
        && values.contains(&"Included".to_string())
        && values.contains(&"Excluded".to_string())
    {
        return false;
    }
    true
}

#[derive(Debug, Error)]
pub enum ReadPLEModelError {
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
pub fn read_ple_model(
    path: &Path,
) -> Result<FeatureDefinitionSerializationContainer, ReadPLEModelError> {
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
            Some(x) => Err(ReadPLEModelError::IncorrectFileExtension(x.to_string())),
            _ => Err(ReadPLEModelError::NoFileExtension),
        },
        _ => Err(ReadPLEModelError::NoFileExtension),
    }
}

#[cfg(test)]
mod tests {
    use crate::{FeatureDefinition, FeatureDefinitionSerializationContainer};

    #[test]
    fn test_serialization_of_feature_definition() {
        let config = FeatureDefinitionSerializationContainer {
            features: vec![
                FeatureDefinition {
                    name: "hello4".to_string(),
                    values: vec!["".to_string()],
                    description: "".to_string(),
                    product_applicabilities: None,
                    applic_constraint: None,
                    allow_multiple_values: false,
                },
                FeatureDefinition {
                    name: "hello5".to_string(),
                    values: vec!["".to_string()],
                    description: "".to_string(),
                    product_applicabilities: None,
                    applic_constraint: None,
                    allow_multiple_values: false,
                },
            ],
        };
        let expected_str = r#"[[feature]]
name = 'hello4'
values = ['']
description = ''
allow_multiple_values = false

[[feature]]
name = 'hello5'
values = ['']
description = ''
allow_multiple_values = false
"#;
        assert_eq!(toml::to_string_pretty(&config).unwrap(), expected_str)
    }
}
