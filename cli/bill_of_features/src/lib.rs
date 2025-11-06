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
use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
use serde::{Deserialize, Serialize};
use std::{
    fs::{File, read_to_string},
    path::PathBuf,
};
use thiserror::Error;

pub trait BillOfFeatures {
    fn get_name(self) -> String;

    fn get_features(self) -> Vec<ApplicabilityTag>;

    fn get_substitutions(self) -> Option<Vec<Substitution>>;
    fn get_type(&self) -> BillOfFeaturesType;
    fn get_parent_group(&self) -> Option<&str>;
    fn get_configs(&self) -> Vec<&str>;
}

/// Applicability Config to setup valid features for parser for config
#[derive(Debug, Default, Serialize, Deserialize, Clone)]
pub struct BillOfFeaturesConfig {
    /// Name of the configuration or configuration group
    pub name: String,
    // Related Config Group
    pub group: String,
    /// list of valid feature tags to parse for this configuration
    pub features: Vec<ApplicabilityTag>,
    /// list of valid substitutions to make for this configuration
    pub substitutions: Option<Vec<Substitution>>,
}

impl From<BillOfFeaturesLegacy> for BillOfFeaturesConfig {
    fn from(value: BillOfFeaturesLegacy) -> Self {
        BillOfFeaturesConfig {
            name: value.normalized_name,
            group: "".to_string(),
            features: value.features,
            substitutions: value.substitutions,
        }
    }
}

impl BillOfFeatures for BillOfFeaturesConfig {
    fn get_name(self) -> String {
        self.name
    }

    fn get_features(self) -> Vec<ApplicabilityTag> {
        self.features
    }

    fn get_substitutions(self) -> Option<Vec<Substitution>> {
        self.substitutions
    }

    fn get_type(&self) -> BillOfFeaturesType {
        BillOfFeaturesType::Config
    }

    fn get_parent_group(&self) -> Option<&str> {
        Some(self.group.as_str())
    }

    fn get_configs(&self) -> Vec<&str> {
        vec![]
    }
}

impl BillOfFeaturesConfig {
    fn merge<T>(&mut self, value: &T)
    where
        T: BillOfFeatures + Clone,
    {
        if !self.name.is_empty() {
            self.name = value.clone().get_name()
        }
        if let None = self.get_parent_group()
            && let Some(x) = value.clone().get_parent_group()
        {
            self.group = x.to_string()
        }
        self.features = self
            .features
            .clone()
            .into_iter()
            .chain(value.clone().get_features())
            .collect();
        self.substitutions = match (
            self.substitutions.clone(),
            value.clone().get_substitutions(),
        ) {
            (None, None) => None,
            (None, Some(x)) => Some(x),
            (Some(x), None) => Some(x),
            (Some(orig), Some(merge)) => Some(orig.into_iter().chain(merge).collect()),
        };
    }
}

/// Applicability Config to setup valid features for parser for config(Legacy)
#[derive(Debug, Deserialize, Serialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct BillOfFeaturesLegacy {
    ///Name of the configuration or configuration group
    pub normalized_name: String,
    /// list of valid feature tags to parse for this configuration
    pub features: Vec<ApplicabilityTag>,
    /// list of valid substitutions to make for this configuration
    pub substitutions: Option<Vec<Substitution>>,
    //not actually present
    #[serde(skip)]
    pub parent_group: Option<String>,
}

impl BillOfFeatures for BillOfFeaturesLegacy {
    fn get_name(self) -> String {
        self.normalized_name
    }

    fn get_features(self) -> Vec<ApplicabilityTag> {
        self.features
    }

    fn get_substitutions(self) -> Option<Vec<Substitution>> {
        self.substitutions
    }

    fn get_type(&self) -> BillOfFeaturesType {
        BillOfFeaturesType::Config
    }

    fn get_parent_group(&self) -> Option<&str> {
        match &self.parent_group {
            Some(x) => Some(x.as_str()),
            None => None,
        }
    }

    fn get_configs(&self) -> Vec<&str> {
        vec![]
    }
}

impl BillOfFeaturesLegacy {
    fn merge<T>(&mut self, value: &T)
    where
        T: BillOfFeatures + Clone,
    {
        if !self.normalized_name.is_empty() {
            self.normalized_name = value.clone().get_name()
        }
        if let None = self.get_parent_group()
            && let Some(x) = value.clone().get_parent_group()
        {
            self.parent_group = Some(x.to_string())
        }
        self.features = self
            .features
            .clone()
            .into_iter()
            .chain(value.clone().get_features())
            .collect();
        self.substitutions = match (
            self.substitutions.clone(),
            value.clone().get_substitutions(),
        ) {
            (None, None) => None,
            (None, Some(x)) => Some(x),
            (Some(x), None) => Some(x),
            (Some(orig), Some(merge)) => Some(orig.into_iter().chain(merge).collect()),
        };
    }
}
/// Applicability Config to setup valid features for parser for config group
#[derive(Debug, Deserialize, Serialize, Clone)]
pub struct BillOfFeaturesConfigGroup {
    /// Name of the configuration or configuration group
    pub name: String,
    // Related Config Group
    pub configs: Vec<String>,
    /// list of valid feature tags to parse for this configuration
    pub features: Vec<ApplicabilityTag>,
    /// list of valid substitutions to make for this configuration
    pub substitutions: Option<Vec<Substitution>>,
    #[serde(skip)]
    pub group: Option<String>,
}
impl BillOfFeatures for BillOfFeaturesConfigGroup {
    fn get_name(self) -> String {
        self.name
    }

    fn get_features(self) -> Vec<ApplicabilityTag> {
        self.features
    }

    fn get_substitutions(self) -> Option<Vec<Substitution>> {
        self.substitutions
    }

    fn get_type(&self) -> BillOfFeaturesType {
        BillOfFeaturesType::Group
    }

    fn get_parent_group(&self) -> Option<&str> {
        match &self.group {
            Some(x) => Some(x.as_str()),
            None => None,
        }
    }

    fn get_configs(&self) -> Vec<&str> {
        self.configs.iter().map(|x| &**x).collect::<Vec<&str>>()
    }
}
impl BillOfFeaturesConfigGroup {
    fn merge<T>(&mut self, value: &T)
    where
        T: BillOfFeatures + Clone,
    {
        if !self.name.is_empty() {
            self.name = value.clone().get_name()
        }
        if let None = self.get_parent_group()
            && let Some(x) = value.clone().get_parent_group()
        {
            self.group = Some(x.to_string())
        }
        self.features = self
            .features
            .clone()
            .into_iter()
            .chain(value.clone().get_features())
            .collect();
        self.substitutions = match (
            self.substitutions.clone(),
            value.clone().get_substitutions(),
        ) {
            (None, None) => None,
            (None, Some(x)) => Some(x),
            (Some(x), None) => Some(x),
            (Some(orig), Some(merge)) => Some(orig.into_iter().chain(merge).collect()),
        };
    }
}

/// Applicability Config to setup valid features for parser
#[derive(Debug, Deserialize, Serialize, Clone)]
#[serde(untagged)]
pub enum BillOfFeaturesEnum {
    Config(BillOfFeaturesConfig),
    ConfigGroup(BillOfFeaturesConfigGroup),
    LegacyConfig(BillOfFeaturesLegacy),
}
impl Default for BillOfFeaturesEnum {
    fn default() -> Self {
        BillOfFeaturesEnum::Config(BillOfFeaturesConfig::default())
    }
}
#[derive(Debug, Default, Deserialize, Clone)]
pub enum BillOfFeaturesType {
    #[default]
    Config,
    Group,
}

impl BillOfFeatures for BillOfFeaturesEnum {
    fn get_name(self) -> String {
        match self {
            BillOfFeaturesEnum::Config(c) => c.get_name(),
            BillOfFeaturesEnum::ConfigGroup(g) => g.get_name(),
            BillOfFeaturesEnum::LegacyConfig(c) => c.get_name(),
        }
    }

    fn get_features(self) -> Vec<ApplicabilityTag> {
        match self {
            BillOfFeaturesEnum::Config(c) => c.get_features(),
            BillOfFeaturesEnum::ConfigGroup(g) => g.get_features(),
            BillOfFeaturesEnum::LegacyConfig(c) => c.get_features(),
        }
    }

    fn get_substitutions(self) -> Option<Vec<Substitution>> {
        match self {
            BillOfFeaturesEnum::Config(c) => c.get_substitutions(),
            BillOfFeaturesEnum::ConfigGroup(g) => g.get_substitutions(),
            BillOfFeaturesEnum::LegacyConfig(c) => c.get_substitutions(),
        }
    }
    fn get_type(&self) -> BillOfFeaturesType {
        match self {
            BillOfFeaturesEnum::Config(c) => c.get_type(),
            BillOfFeaturesEnum::ConfigGroup(g) => g.get_type(),
            BillOfFeaturesEnum::LegacyConfig(c) => c.get_type(),
        }
    }
    fn get_parent_group(&self) -> Option<&str> {
        match self {
            BillOfFeaturesEnum::Config(c) => c.get_parent_group(),
            BillOfFeaturesEnum::ConfigGroup(g) => g.get_parent_group(),
            BillOfFeaturesEnum::LegacyConfig(c) => c.get_parent_group(),
        }
    }
    fn get_configs(&self) -> Vec<&str> {
        match self {
            BillOfFeaturesEnum::Config(c) => c.get_configs(),
            BillOfFeaturesEnum::ConfigGroup(g) => g.get_configs(),
            BillOfFeaturesEnum::LegacyConfig(c) => c.get_configs(),
        }
    }
}
impl BillOfFeaturesEnum {
    pub fn merge<T>(&mut self, value: &T)
    where
        T: BillOfFeatures + Clone,
    {
        match self {
            BillOfFeaturesEnum::Config(applicability_config_element_config) => {
                applicability_config_element_config.merge(value)
            }
            BillOfFeaturesEnum::ConfigGroup(applicability_config_element_config_group) => {
                applicability_config_element_config_group.merge(value)
            }
            BillOfFeaturesEnum::LegacyConfig(applicability_config_element_legacy) => {
                applicability_config_element_legacy.merge(value)
            }
        }
    }
}
#[derive(Debug, Error)]
pub enum ReadBillOfFeaturesConfigError {
    #[error("Could not parse applicability config JSON \n{:?}: \tat line {:?} column {:?}",.0.classify(), .0.line(),.0.column())]
    JSON(#[from] serde_json::Error),
    #[error("Error reading applicability config: {:?}",.0)]
    Io(#[from] std::io::Error),
    #[error("Could not parse applicability config TOML \n{:?}: \tat {:?}",.0.to_string(), .0.line_col())]
    Toml(#[from] toml::de::Error),
    #[error("Applicability Config has incorrect file extension. Received: {:?}, want: [toml, json]", .0)]
    IncorrectFileExtension(String),
    #[error("Applicability Config has no file extension. Expected formats: [toml, json]")]
    NoFileExtension,
}
pub fn read_bill_of_features(
    path: PathBuf,
) -> Result<BillOfFeaturesEnum, ReadBillOfFeaturesConfigError> {
    let applic_processing = (path.clone(), path.extension());
    match applic_processing {
        (path, Some(file_ext)) => match file_ext.to_str() {
            Some("json") => {
                let applic_file = File::open(&path);
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
            Some(x) => Err(ReadBillOfFeaturesConfigError::IncorrectFileExtension(
                x.to_string(),
            )),
            _ => Err(ReadBillOfFeaturesConfigError::NoFileExtension),
        },
        _ => Err(ReadBillOfFeaturesConfigError::NoFileExtension),
    }
}
pub fn read_multiple_bill_of_features(
    path: PathBuf,
) -> Result<Vec<BillOfFeaturesEnum>, ReadBillOfFeaturesConfigError> {
    let applic_processing = (path.clone(), path.extension());
    match applic_processing {
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
            Some(x) => Err(ReadBillOfFeaturesConfigError::IncorrectFileExtension(
                x.to_string(),
            )),
            _ => Err(ReadBillOfFeaturesConfigError::NoFileExtension),
        },
        _ => Err(ReadBillOfFeaturesConfigError::NoFileExtension),
    }
}

#[cfg(test)]
mod tests {

    mod json {
        use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};

        use crate::{BillOfFeatures, BillOfFeaturesEnum};
        #[test]
        fn test_legacy() {
            let test_str = r#"
                {
                    "normalizedName":"PRODUCT_A",
                    "features":[
                        "ROBOT_SPKR=SPKR_A",
                        "JHU_CONTROLLER"
                    ],
                    "substitutions":[
                        {"matchText":"EVAL_A", "substitute":"SOME_SUBSTITUTE"}    
                    ]
                }
            "#;
            let result = serde_json::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(unwrapped_result.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration() {
            let test_str = r#"
                {
                    "name":"PRODUCT_A",
                    "group":"SHARED_GROUP",
                    "features":[
                        "ROBOT_SPKR=SPKR_A",
                        "JHU_CONTROLLER"
                    ],
                    "substitutions":[
                        {"matchText":"EVAL_A", "substitute":"SOME_SUBSTITUTE"}    
                    ]
                }
            "#;
            let result = serde_json::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(unwrapped_result.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    unwrapped_result.clone().get_parent_group(),
                    Some("SHARED_GROUP")
                );
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }

        #[test]
        fn test_configuration_group() {
            let test_str = r#"
                {
                    "name":"SHARED_GROUP",
                    "configs":["PRODUCT_A", "PRODUCT_B"],
                    "features":[
                        "ROBOT_SPKR=SPKR_A",
                        "JHU_CONTROLLER"
                    ],
                    "substitutions":[
                        {"matchText":"EVAL_A", "substitute":"SOME_SUBSTITUTE"}    
                    ]
                }
            "#;
            let result = serde_json::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(
                    unwrapped_result.clone().get_name(),
                    "SHARED_GROUP".to_string()
                );
                assert_eq!(
                    unwrapped_result.clone().get_configs(),
                    vec!["PRODUCT_A", "PRODUCT_B"]
                );
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_group_1line_eval() {
            let test_str = r#"
                {
                    "name":"SHARED_GROUP",
                    "configs":["PRODUCT_A", "PRODUCT_B"],
                    "features":[
                        "ROBOT_SPKR=SPKR_A",
                        "JHU_CONTROLLER"
                    ],
                    "substitutions":[
                        "EVAL_A=SOME_SUBSTITUTE"   
                    ]
                }
            "#;
            let result = serde_json::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(
                    unwrapped_result.clone().get_name(),
                    "SHARED_GROUP".to_string()
                );
                assert_eq!(
                    unwrapped_result.clone().get_configs(),
                    vec!["PRODUCT_A", "PRODUCT_B"]
                );
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
    }
    mod toml {
        use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};

        use crate::{BillOfFeatures, BillOfFeaturesEnum};
        #[test]
        fn test_legacy() {
            let test_str = r#"
            "normalizedName" = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            substitutions=[
            {match_text="EVAL_A", substitute = "SOME_SUBSTITUTE"}
            ]
            "#;
            let result = toml::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(unwrapped_result.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
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
            normalizedName = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            [[substitutions]]
            match_text = "EVAL_A"
            substitute = "SOME_SUBSTITUTE"
            "#;
            let result = toml::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(unwrapped_result.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration() {
            let test_str = r#"
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
            let result = toml::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(unwrapped_result.clone().get_name(), "PRODUCT_A".to_string());
                assert_eq!(
                    unwrapped_result.clone().get_parent_group(),
                    Some("SHARED_GROUP")
                );
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }

        #[test]
        fn test_configuration_group() {
            let test_str = r#"
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
            let result = toml::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(
                    unwrapped_result.clone().get_name(),
                    "SHARED_GROUP".to_string()
                );
                assert_eq!(
                    unwrapped_result.clone().get_configs(),
                    vec!["PRODUCT_A", "PRODUCT_B"]
                );
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
        #[test]
        fn test_configuration_group_1line_eval() {
            let test_str = r#"
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
            let result = toml::from_str::<BillOfFeaturesEnum>(test_str);
            assert!(
                result.is_ok(),
                "Legacy config parsing validity: {result:#?}"
            );
            if let Ok(unwrapped_result) = result {
                assert_eq!(
                    unwrapped_result.clone().get_name(),
                    "SHARED_GROUP".to_string()
                );
                assert_eq!(
                    unwrapped_result.clone().get_configs(),
                    vec!["PRODUCT_A", "PRODUCT_B"]
                );
                assert_eq!(
                    unwrapped_result.clone().get_features(),
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
                    unwrapped_result.get_substitutions(),
                    Some(vec![Substitution {
                        match_text: "EVAL_A".to_string(),
                        substitute: "SOME_SUBSTITUTE".to_string()
                    }])
                )
            }
        }
    }
}
