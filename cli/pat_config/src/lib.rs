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
use serde::Deserialize;
use toml::{map::Map, value::Table};

#[derive(Debug, Deserialize, Clone)]
pub struct PatConfig {
    pub project: ProjectConfig,
    #[serde(flatten)]
    pub feature: Option<Table>,
}

#[derive(Debug, Deserialize, Clone)]
pub struct ProjectConfig {
    pub inline_projection_exclusions: Vec<String>,
}

pub fn from_str(s: &str) -> Result<CompletePatConfig, toml::de::Error> {
    let pat_config: Result<PatConfig, toml::de::Error> = toml::de::from_str(s);
    pat_config.map(Into::<CompletePatConfig>::into)
}

#[derive(Debug, Deserialize, Clone)]
pub struct FeatureDefinition {
    pub name: String,
    pub values: Vec<String>,
    pub description: String,
    pub product_applicabilities: Option<Vec<String>>,
    pub applic_constraint: Option<String>,
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
        let name = match value["name"].as_str() {
            Some(x) => String::from(x),
            _ => String::from(""),
        };
        let values = match value["values"].as_array() {
            Some(v) => v
                .iter()
                .filter_map(|x| match x {
                    toml::Value::String(i) => Some(i.clone()),
                    _ => None,
                })
                .collect::<Vec<String>>(),
            _ => vec![],
        };
        let description = match value["description"].as_str() {
            Some(x) => String::from(x),
            _ => String::from(""),
        };
        let product_applicabilities = value
            .get("productApplicabilities")
            .and_then(|x| x.as_array())
            .map(|apps| {
                apps.iter()
                    .filter_map(|x| match x {
                        toml::Value::String(i) => Some(i.clone()),
                        _ => None,
                    })
                    .collect::<Vec<String>>()
            });
        let constraint = value
            .get("applicabilityConstraint")
            .and_then(|x| x.as_str())
            .map(String::from);
        Ok(FeatureDefinition {
            name,
            values,
            description,
            product_applicabilities,
            applic_constraint: constraint,
        })
    }
}
#[derive(Debug, Deserialize, Clone)]
pub struct CompletePatConfig {
    pub project: ProjectConfig,
    pub features: Option<Vec<FeatureDefinition>>,
}

impl From<PatConfig> for CompletePatConfig {
    ///
    /// value.feature contains an Option<Table>
    ///
    /// The first key must be called "feature"
    ///
    /// This key can contain:
    ///
    /// A Table containing arrays which have tables inside
    /// Each of these tables is a fully formed feature
    /// This case occurs when you only have sub-tables i.e like [[feature.categoryA]] [[feature.categoryA]] [[feature.categoryB]]
    ///
    /// An Array containing Tables
    ///
    /// Each of these tables is a fully formed feature
    ///
    /// These tables CAN contain arrays which contain tables
    ///
    /// These tables CAN contain tables which contain arrays with tables inside(TODO: validate this statement again)
    ///
    fn from(value: PatConfig) -> Self {
        CompletePatConfig {
            project: value.project,
            features: unwrap_main_feature_table(value.feature),
        }
    }
}
fn unwrap_main_feature_table(potential_tables: Option<Table>) -> Option<Vec<FeatureDefinition>> {
    if let Some(feature_def_tables_map) = potential_tables {
        let feature_def_map_iter = feature_def_tables_map
            .iter()
            .filter_map(|(key, value)| {
                if let toml::Value::Array(a) = value
                    && *key == "feature"
                {
                    //these values must be tables which can contain arrays
                    return Some(a.to_owned());
                };
                if let toml::Value::Table(t) = value
                    && *key == "feature"
                {
                    let a = t.values().cloned().collect();
                    //these values must be arrays that contain tables
                    return Some(a);
                }
                None
            })
            .flat_map(|x| x.into_iter())
            // at this point, it could be a toml::Value::Table, toml::Value::String, or toml::Value::Array
            .filter_map(|x| match x {
                toml::Value::String(_) => None,
                toml::Value::Array(vec) => {
                    // the vec must be filled with tables
                    let transformed_vec = vec.iter().map(|x| x.as_table().unwrap().to_owned());
                    Some(unwrap_feature_table_array(transformed_vec).collect())
                }
                toml::Value::Table(map) => {
                    // the map can be the proper keys, and can also have arrays inside
                    Some(unwrap_feature_table(map))
                }
                toml::Value::Integer(_) => None,
                toml::Value::Float(_) => None,
                toml::Value::Boolean(_) => None,
                toml::Value::Datetime(_) => None,
            })
            .flatten()
            .collect();
        return Some(feature_def_map_iter);
    }
    None
}
/// each element of feature table array will represent a concrete sub-table i.e.
/// [[feature.categoryA]] or [[feature.categoryB]]
/// the table does not need to contain values, but in most cases it probably should (only if the table exists to create a sub-table, should it not)
fn unwrap_feature_table_array<T>(potential_tables: T) -> impl Iterator<Item = FeatureDefinition>
where
    T: Iterator<Item = toml::value::Table>,
{
    potential_tables.flat_map(unwrap_feature_table)
}

fn unwrap_feature_table(table: Map<String, toml::Value>) -> Vec<FeatureDefinition> {
    let feature_def_result: Result<FeatureDefinition, FeatureDefinitionConversionError> =
        table.clone().try_into();
    let remaining_fields = table
        .iter()
        .filter(|(key, v)| {
            // these fields are the necessary fields to create a feature definition, all other fields should be assumed to be an array
            *key != "name"
                && *key != "description"
                && *key != "values"
                && *key != "productApplicabilities"
                && *key != "applicabilityConstraint"
                && matches!(v, toml::Value::Array(_))
        })
        .filter_map(|(_, value)| {
            if let toml::Value::Array(a) = value {
                return Some(a);
            }
            None
        })
        .flat_map(|x| x.iter())
        .filter_map(|x| {
            if let toml::Value::Table(t) = x {
                return Some(unwrap_feature_table(t.clone()));
            }
            None
        })
        .flatten();
    match feature_def_result {
        Ok(res) => vec![res].into_iter().chain(remaining_fields).collect(),
        Err(_) => remaining_fields.collect(),
    }
}

#[cfg(test)]
mod tests {
    use crate::from_str;

    #[test]
    fn test_deserialize_just_empty_project() {
        let test_str = r#"[project]
inline_projection_exclusions = [  ]"#;
        let full_pat_config = from_str(test_str).unwrap();
        assert_eq!(
            0,
            full_pat_config.project.inline_projection_exclusions.len()
        )
    }
    #[test]
    fn test_deserialize_single_feature() {
        let test_str = r#"[project]
inline_projection_exclusions = [ ]
[[feature]]
name="hello4"
values=[""]
description=""
"#;
        let full_pat_config = from_str(test_str).unwrap();
        assert_eq!(
            0,
            full_pat_config.project.inline_projection_exclusions.len()
        );
        assert!(full_pat_config.features.is_some());
        assert!(full_pat_config.features.is_some_and(|x| x.len() == 1))
    }

    #[test]
    fn test_deserialize_multi_feature() {
        let test_str = r#"[project]
inline_projection_exclusions = [ ]
[[feature]]
name="hello4"
values=[""]
description=""
[[feature]]
name="hello5"
values=[""]
description=""
"#;
        let full_pat_config = from_str(test_str).unwrap();
        assert_eq!(
            0,
            full_pat_config.project.inline_projection_exclusions.len()
        );
        assert!(full_pat_config.features.is_some());
        assert!(full_pat_config.features.is_some_and(|x| x.len() == 2))
    }
    #[test]
    fn test_deserialize_only_sub_features() {
        let test_str = r#"[project]
inline_projection_exclusions = [ ]
[[feature.test0]]
name="hello"
values=[""]
description=""
[[feature.test0]]
name="hello2"
values=[""]
description=""
[[feature.test1]]
name="hello3"
values=[""]
description=""
[[feature.test1.test0]]
name="hello6"
values=[""]
description=""
"#;
        let full_pat_config = from_str(test_str).unwrap();
        assert_eq!(
            0,
            full_pat_config.project.inline_projection_exclusions.len()
        );
        assert!(full_pat_config.features.is_some());
        assert!(full_pat_config.features.is_some_and(|x| x.len() == 4))
    }

    #[test]
    fn test_deserialize_only_level_3_features() {
        let test_str = r#"[project]
inline_projection_exclusions = [ ]
[[feature.test1.test0]]
name="hello6"
values=[""]
description=""
"#;
        let full_pat_config = from_str(test_str).unwrap();
        assert_eq!(
            0,
            full_pat_config.project.inline_projection_exclusions.len()
        );
        assert!(full_pat_config.features.is_some());
        assert!(full_pat_config.features.is_some_and(|x| x.len() == 1))
    }

    #[test]
    fn test_deserialize_multi_feature_multi_sub_feature_multi_level() {
        let test_str = r#"[project]
inline_projection_exclusions = [ ]
[[feature]]
name="hello4"
values=[""]
description=""
[[feature]]
name="hello5"
values=[""]
description=""
[[feature.test0]]
name="hello"
values=[""]
description=""
[[feature.test0]]
name="hello2"
values=[""]
description=""
[[feature.test1]]
name="hello3"
values=[""]
description=""
[[feature.test1.test0]]
name="hello6"
values=[""]
description="""#;
        let full_pat_config = from_str(test_str).unwrap();
        assert_eq!(
            0,
            full_pat_config.project.inline_projection_exclusions.len()
        );
        assert!(full_pat_config.features.is_some());
        assert!(full_pat_config.features.is_some_and(|x| x.len() == 6))
    }
}
