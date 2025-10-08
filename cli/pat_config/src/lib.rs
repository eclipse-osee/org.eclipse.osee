use std::{fs, path::PathBuf};

use applicability_parser_config::applic_config::ApplicabilityConfigElement;
use feature_definition::{FeatureDefinition, FeatureDefinitionConversionError};
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
use tracing::error;

#[derive(Debug, Deserialize, Clone)]
pub struct PatConfig {
    pub project: ProjectConfig,
    pub includes: Option<Vec<PathBuf>>,
    #[serde(flatten)]
    pub feature: Option<Table>,
    pub config: Option<ApplicabilityConfigElement>,
    #[serde(skip)]
    pub path: PathBuf,
}

#[derive(Debug, Deserialize, Clone)]
pub struct ProjectConfig {
    pub inline_projection_exclusions: Vec<String>,
}

pub fn from_str(path: PathBuf, s: &str) -> Result<CompletePatConfig, toml::de::Error> {
    let pat_config: Result<PatConfig, toml::de::Error> = toml::de::from_str(s);
    match pat_config {
        Ok(mut c) => {
            c.path = path;
            Ok(Into::<CompletePatConfig>::into(c))
        }
        Err(e) => Err(e),
    }
}

#[derive(Debug, Deserialize, Clone)]
pub struct CompletePatConfig {
    pub project: ProjectConfig,
    pub features: Option<Vec<FeatureDefinition>>,
    pub config: Option<ApplicabilityConfigElement>,
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
        let includes = value.includes.map(|paths|{
            paths.into_iter().filter_map(|path|{
                let full_path = value.path.join(path);
                let contents = match fs::read_to_string(full_path.clone()){
                    Ok(c) => c,
                    Err(e) => {
                        error!("Attempted to read {full_path:#?} for includes path in ple configuration toml file, instead received error {e:#?}");
                        "".to_string()
                    },
                };
                from_str(full_path, &contents).ok()
            }).collect::<Vec<CompletePatConfig>>()
        });
        let project = value.project.clone();
        let included_features = includes.clone().map(|configs| {
            configs
                .iter()
                .filter_map(|c| c.features.as_ref().map(|f| f.iter()))
                .flatten()
                .cloned()
                .collect::<Vec<FeatureDefinition>>()
        });
        let included_exclusions = includes.clone().map(|configs| {
            configs
                .iter()
                .flat_map(|c| c.project.inline_projection_exclusions.clone())
                .collect::<Vec<_>>()
        });
        let included_configs = includes.map(|i| {
            i.iter()
                .filter_map(|c| c.config.clone())
                .collect::<Vec<_>>()
        });
        let merged_projects = included_exclusions.map_or_else(
            || project,
            |exc| {
                let result = value
                    .project
                    .inline_projection_exclusions
                    .into_iter()
                    .chain(exc)
                    .collect();
                ProjectConfig {
                    inline_projection_exclusions: result,
                }
            },
        );
        let base_features = unwrap_main_feature_table(value.feature);
        let features = base_features.clone();
        let merged_features = included_features.map_or_else(
            || base_features,
            |m_features| {
                if let Some(feat) = features {
                    return Some(feat.into_iter().chain(m_features).collect());
                }
                if !m_features.is_empty() {
                    return Some(m_features);
                }
                None
            },
        );
        let config = value.config.clone();
        let config2 = config.clone();
        let merged_config = included_configs.map_or_else(
            || config,
            |m_configs| {
                let internal_config = config2.clone();
                if let Some(mut x) = internal_config {
                    m_configs.iter().for_each(|c| {
                        x.merge(c);
                    });
                    return Some(x);
                }
                if !m_configs.is_empty() {
                    //merge the root of m_configs with the remaining configurations
                    let mut iter = m_configs.into_iter();
                    let internal_config = iter.next();
                    if let Some(mut x) = internal_config {
                        iter.for_each(|c| x.merge(&c));
                        return Some(x.clone());
                    }
                }
                None
            },
        );
        CompletePatConfig {
            project: merged_projects,
            features: merged_features,
            config: merged_config,
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
                && *key != "product_applicabilities"
                && *key != "applicability_constraint"
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

    use std::path::PathBuf;

    use crate::from_str;

    #[test]
    fn test_deserialize_just_empty_project() {
        let test_str = r#"[project]
inline_projection_exclusions = [  ]"#;
        let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
        assert_eq!(
            0,
            full_pat_config.project.inline_projection_exclusions.len()
        )
    }

    mod single_feature {
        use std::path::PathBuf;

        use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
        use applicability_parser_config::applic_config::ApplicabilityConfig;

        use crate::from_str;
        #[test]
        fn test() {
            let test_str = r#"[project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1))
        }
        #[test]
        fn test_legacy_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [config]
            "normalizedName" = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            substitutions=[
            {match_text="EVAL_A", substitute = "SOME_SUBSTITUTE"}
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_legacy_toml_table() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [config]
            normalizedName = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            [[config.substitutions]]
            match_text = "EVAL_A"
            substitute = "SOME_SUBSTITUTE"
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [config]
            name = "PRODUCT_A"
            group = "SHARED_GROUP"
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
            ]
            substitutions = [
                { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE" }    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(config.clone().get_parent_group(), Some("SHARED_GROUP"));
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }

        #[test]
        fn test_configuration_group_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [config]
            name = "SHARED_GROUP"
            configs = ["PRODUCT_A", "PRODUCT_B"]
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
                ]
            substitutions = [
                { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE"}    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_group_1line_eval_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [config]
            name = "SHARED_GROUP"
            configs = ["PRODUCT_A", "PRODUCT_B"]
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
                ]
            substitutions = [
                "EVAL_A=SOME_SUBSTITUTE"    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
    }

    mod multi_feature {
        use std::path::PathBuf;

        use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
        use applicability_parser_config::applic_config::ApplicabilityConfig;

        use crate::from_str;
        #[test]
        fn test() {
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
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 2))
        }
        #[test]
        fn test_legacy_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [[feature]]
            name="hello5"
            values=[""]
            description=""
            [config]
            "normalizedName" = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            substitutions=[
            {match_text="EVAL_A", substitute = "SOME_SUBSTITUTE"}
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 2));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_legacy_toml_table() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [[feature]]
            name="hello5"
            values=[""]
            description=""
            [config]
            normalizedName = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            [[config.substitutions]]
            match_text = "EVAL_A"
            substitute = "SOME_SUBSTITUTE"
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 2));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [[feature]]
            name="hello5"
            values=[""]
            description=""
            [config]
            name = "PRODUCT_A"
            group = "SHARED_GROUP"
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
            ]
            substitutions = [
                { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE" }    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 2));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(config.clone().get_parent_group(), Some("SHARED_GROUP"));
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }

        #[test]
        fn test_configuration_group_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [[feature]]
            name="hello5"
            values=[""]
            description=""
            [config]
            name = "SHARED_GROUP"
            configs = ["PRODUCT_A", "PRODUCT_B"]
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
                ]
            substitutions = [
                { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE"}    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 2));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_group_1line_eval_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature]]
            name="hello4"
            values=[""]
            description=""
            [[feature]]
            name="hello5"
            values=[""]
            description=""
            [config]
            name = "SHARED_GROUP"
            configs = ["PRODUCT_A", "PRODUCT_B"]
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
                ]
            substitutions = [
                "EVAL_A=SOME_SUBSTITUTE"    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 2));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }

        mod sub_feature {
            use std::path::PathBuf;

            use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
            use applicability_parser_config::applic_config::ApplicabilityConfig;

            use crate::from_str;
            #[test]
            fn test() {
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
                let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
                assert_eq!(
                    0,
                    full_pat_config.project.inline_projection_exclusions.len()
                );
                assert!(full_pat_config.features.is_some());
                assert!(full_pat_config.features.is_some_and(|x| x.len() == 6))
            }
            #[test]
            fn test_legacy_toml() {
                let test_str = r#"
        [project]
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
        description=""
        [config]
        "normalizedName" = "PRODUCT_A"
        features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
        substitutions=[
        {match_text="EVAL_A", substitute = "SOME_SUBSTITUTE"}
        ]
        "#;
                let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
                assert_eq!(
                    0,
                    full_pat_config.project.inline_projection_exclusions.len()
                );
                assert!(full_pat_config.features.is_some());
                assert!(full_pat_config.features.is_some_and(|x| x.len() == 6));
                assert!(full_pat_config.config.is_some());
                if full_pat_config.config.is_some() {
                    let config = full_pat_config.config.unwrap();
                    assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                    assert_eq!(
                        config.clone().get_features(),
                        vec![
                            ApplicabilityTag {
                                tag: "ROBOT_SPKR".to_string(),
                                value: "SPKR_A".to_string()
                            },
                            ApplicabilityTag {
                                tag: "JHU_CONTROLLER".to_string(),
                                value: "Included".to_string()
                            }
                        ]
                    );
                    assert_eq!(
                        config.get_substitutions(),
                        Some(vec![Substitution {
                            match_text: "EVAL_A".to_string(),
                            substitute: "SOME_SUBSTITUTE".to_string()
                        }])
                    )
                }
            }
            #[test]
            fn test_legacy_toml_table() {
                let test_str = r#"
        [project]
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
        description=""
        [config]
        normalizedName = "PRODUCT_A"
        features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
        [[config.substitutions]]
        match_text = "EVAL_A"
        substitute = "SOME_SUBSTITUTE"
        "#;
                let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
                assert_eq!(
                    0,
                    full_pat_config.project.inline_projection_exclusions.len()
                );
                assert!(full_pat_config.features.is_some());
                assert!(full_pat_config.features.is_some_and(|x| x.len() == 6));
                assert!(full_pat_config.config.is_some());
                if full_pat_config.config.is_some() {
                    let config = full_pat_config.config.unwrap();
                    assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                    assert_eq!(
                        config.clone().get_features(),
                        vec![
                            ApplicabilityTag {
                                tag: "ROBOT_SPKR".to_string(),
                                value: "SPKR_A".to_string()
                            },
                            ApplicabilityTag {
                                tag: "JHU_CONTROLLER".to_string(),
                                value: "Included".to_string()
                            }
                        ]
                    );
                    assert_eq!(
                        config.get_substitutions(),
                        Some(vec![Substitution {
                            match_text: "EVAL_A".to_string(),
                            substitute: "SOME_SUBSTITUTE".to_string()
                        }])
                    )
                }
            }
            #[test]
            fn test_configuration_toml() {
                let test_str = r#"
        [project]
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
        description=""
        [config]
        name = "PRODUCT_A"
        group = "SHARED_GROUP"
        features = [
            "ROBOT_SPKR=SPKR_A",
            "JHU_CONTROLLER"
        ]
        substitutions = [
            { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE" }    
        ]
        "#;
                let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
                assert_eq!(
                    0,
                    full_pat_config.project.inline_projection_exclusions.len()
                );
                assert!(full_pat_config.features.is_some());
                assert!(full_pat_config.features.is_some_and(|x| x.len() == 6));
                assert!(full_pat_config.config.is_some());
                if full_pat_config.config.is_some() {
                    let config = full_pat_config.config.unwrap();
                    assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                    assert_eq!(config.clone().get_parent_group(), Some("SHARED_GROUP"));
                    assert_eq!(
                        config.clone().get_features(),
                        vec![
                            ApplicabilityTag {
                                tag: "ROBOT_SPKR".to_string(),
                                value: "SPKR_A".to_string()
                            },
                            ApplicabilityTag {
                                tag: "JHU_CONTROLLER".to_string(),
                                value: "Included".to_string()
                            }
                        ]
                    );
                    assert_eq!(
                        config.get_substitutions(),
                        Some(vec![Substitution {
                            match_text: "EVAL_A".to_string(),
                            substitute: "SOME_SUBSTITUTE".to_string()
                        }])
                    )
                }
            }

            #[test]
            fn test_configuration_group_toml() {
                let test_str = r#"
        [project]
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
        description=""
        [config]
        name = "SHARED_GROUP"
        configs = ["PRODUCT_A", "PRODUCT_B"]
        features = [
            "ROBOT_SPKR=SPKR_A",
            "JHU_CONTROLLER"
            ]
        substitutions = [
            { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE"}    
        ]
        "#;
                let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
                assert_eq!(
                    0,
                    full_pat_config.project.inline_projection_exclusions.len()
                );
                assert!(full_pat_config.features.is_some());
                assert!(full_pat_config.features.is_some_and(|x| x.len() == 6));
                assert!(full_pat_config.config.is_some());
                if full_pat_config.config.is_some() {
                    let config = full_pat_config.config.unwrap();
                    assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                    assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                    assert_eq!(
                        config.clone().get_features(),
                        vec![
                            ApplicabilityTag {
                                tag: "ROBOT_SPKR".to_string(),
                                value: "SPKR_A".to_string()
                            },
                            ApplicabilityTag {
                                tag: "JHU_CONTROLLER".to_string(),
                                value: "Included".to_string()
                            }
                        ]
                    );
                    assert_eq!(
                        config.get_substitutions(),
                        Some(vec![Substitution {
                            match_text: "EVAL_A".to_string(),
                            substitute: "SOME_SUBSTITUTE".to_string()
                        }])
                    )
                }
            }
            #[test]
            fn test_configuration_group_1line_eval_toml() {
                let test_str = r#"
        [project]
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
        description=""
        [config]
        name = "SHARED_GROUP"
        configs = ["PRODUCT_A", "PRODUCT_B"]
        features = [
            "ROBOT_SPKR=SPKR_A",
            "JHU_CONTROLLER"
            ]
        substitutions = [
            "EVAL_A=SOME_SUBSTITUTE"    
        ]
        "#;
                let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
                assert_eq!(
                    0,
                    full_pat_config.project.inline_projection_exclusions.len()
                );
                assert!(full_pat_config.features.is_some());
                assert!(full_pat_config.features.is_some_and(|x| x.len() == 6));
                assert!(full_pat_config.config.is_some());
                if full_pat_config.config.is_some() {
                    let config = full_pat_config.config.unwrap();
                    assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                    assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                    assert_eq!(
                        config.clone().get_features(),
                        vec![
                            ApplicabilityTag {
                                tag: "ROBOT_SPKR".to_string(),
                                value: "SPKR_A".to_string()
                            },
                            ApplicabilityTag {
                                tag: "JHU_CONTROLLER".to_string(),
                                value: "Included".to_string()
                            }
                        ]
                    );
                    assert_eq!(
                        config.get_substitutions(),
                        Some(vec![Substitution {
                            match_text: "EVAL_A".to_string(),
                            substitute: "SOME_SUBSTITUTE".to_string()
                        }])
                    )
                }
            }
        }
    }

    mod sub_feature {
        use std::path::PathBuf;

        use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
        use applicability_parser_config::applic_config::ApplicabilityConfig;

        use crate::from_str;
        #[test]
        fn test() {
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
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 4))
        }
        #[test]
        fn test_legacy_toml() {
            let test_str = r#"
            [project]
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
            [config]
            "normalizedName" = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            substitutions=[
            {match_text="EVAL_A", substitute = "SOME_SUBSTITUTE"}
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 4));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_legacy_toml_table() {
            let test_str = r#"
            [project]
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
            [config]
            normalizedName = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            [[config.substitutions]]
            match_text = "EVAL_A"
            substitute = "SOME_SUBSTITUTE"
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 4));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_toml() {
            let test_str = r#"
            [project]
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
            [config]
            name = "PRODUCT_A"
            group = "SHARED_GROUP"
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
            ]
            substitutions = [
                { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE" }    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 4));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(config.clone().get_parent_group(), Some("SHARED_GROUP"));
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }

        #[test]
        fn test_configuration_group_toml() {
            let test_str = r#"
            [project]
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
            [config]
            name = "SHARED_GROUP"
            configs = ["PRODUCT_A", "PRODUCT_B"]
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
                ]
            substitutions = [
                { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE"}    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 4));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_group_1line_eval_toml() {
            let test_str = r#"
            [project]
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
            [config]
            name = "SHARED_GROUP"
            configs = ["PRODUCT_A", "PRODUCT_B"]
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
                ]
            substitutions = [
                "EVAL_A=SOME_SUBSTITUTE"    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 4));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
    }

    mod level3_features {
        use std::path::PathBuf;

        use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
        use applicability_parser_config::applic_config::ApplicabilityConfig;

        use crate::from_str;
        #[test]
        fn test() {
            let test_str = r#"[project]
            inline_projection_exclusions = [ ]
            [[feature.test1.test0]]
            name="hello6"
            values=[""]
            description=""
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1))
        }
        #[test]
        fn test_legacy_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature.test1.test0]]
            name="hello6"
            values=[""]
            description=""
            [config]
            "normalizedName" = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            substitutions=[
            {match_text="EVAL_A", substitute = "SOME_SUBSTITUTE"}
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_legacy_toml_table() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature.test1.test0]]
            name="hello6"
            values=[""]
            description=""
            [config]
            normalizedName = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            [[config.substitutions]]
            match_text = "EVAL_A"
            substitute = "SOME_SUBSTITUTE"
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature.test1.test0]]
            name="hello6"
            values=[""]
            description=""
            [config]
            name = "PRODUCT_A"
            group = "SHARED_GROUP"
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
            ]
            substitutions = [
                { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE" }    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(config.clone().get_parent_group(), Some("SHARED_GROUP"));
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }

        #[test]
        fn test_configuration_group_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature.test1.test0]]
            name="hello6"
            values=[""]
            description=""
            [config]
            name = "SHARED_GROUP"
            configs = ["PRODUCT_A", "PRODUCT_B"]
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
                ]
            substitutions = [
                { match_text = "EVAL_A", substitute = "SOME_SUBSTITUTE"}    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_group_1line_eval_toml() {
            let test_str = r#"
            [project]
            inline_projection_exclusions = [ ]
            [[feature.test1.test0]]
            name="hello6"
            values=[""]
            description=""
            [config]
            name = "SHARED_GROUP"
            configs = ["PRODUCT_A", "PRODUCT_B"]
            features = [
                "ROBOT_SPKR=SPKR_A",
                "JHU_CONTROLLER"
                ]
            substitutions = [
                "EVAL_A=SOME_SUBSTITUTE"    
            ]
            "#;
            let full_pat_config = from_str(PathBuf::new(), test_str).unwrap();
            assert_eq!(
                0,
                full_pat_config.project.inline_projection_exclusions.len()
            );
            assert!(full_pat_config.features.is_some());
            assert!(full_pat_config.features.is_some_and(|x| x.len() == 1));
            assert!(full_pat_config.config.is_some());
            if full_pat_config.config.is_some() {
                let config = full_pat_config.config.unwrap();
                assert_eq!(config.clone().get_name(), "SHARED_GROUP".to_string());
                assert_eq!(config.clone().get_configs(), vec!["PRODUCT_A", "PRODUCT_B"]);
                assert_eq!(
                    config.clone().get_features(),
                    vec![
                        ApplicabilityTag {
                            tag: "ROBOT_SPKR".to_string(),
                            value: "SPKR_A".to_string()
                        },
                        ApplicabilityTag {
                            tag: "JHU_CONTROLLER".to_string(),
                            value: "Included".to_string()
                        }
                    ]
                );
                assert_eq!(
                    config.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
    }
}
