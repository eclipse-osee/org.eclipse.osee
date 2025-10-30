/*********************************************************************
 * Copyright (c) 2024 Boeing
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
use serde::Deserialize;

pub trait ApplicabilityConfig {
    fn get_name(self) -> String;

    fn get_features(self) -> Vec<ApplicabilityTag>;

    fn get_substitutions(self) -> Option<Vec<Substitution>>;
    fn get_type(&self) -> ApplicabilityConfigElementType;
    fn get_parent_group(&self) -> Option<&str>;
    fn get_configs(&self) -> Vec<&str>;
}

/// Applicability Config to setup valid features for parser for config
#[derive(Debug, Deserialize, Clone)]
pub struct ApplicabilityConfigElementConfig {
    /// Name of the configuration or configuration group
    pub name: String,
    // Related Config Group
    pub group: String,
    /// list of valid feature tags to parse for this configuration
    pub features: Vec<ApplicabilityTag>,
    /// list of valid substitutions to make for this configuration
    pub substitutions: Option<Vec<Substitution>>,
}

impl ApplicabilityConfig for ApplicabilityConfigElementConfig {
    fn get_name(self) -> String {
        self.name
    }

    fn get_features(self) -> Vec<ApplicabilityTag> {
        self.features
    }

    fn get_substitutions(self) -> Option<Vec<Substitution>> {
        self.substitutions
    }

    fn get_type(&self) -> ApplicabilityConfigElementType {
        ApplicabilityConfigElementType::Config
    }

    fn get_parent_group(&self) -> Option<&str> {
        Some(self.group.as_str())
    }

    fn get_configs(&self) -> Vec<&str> {
        vec![]
    }
}

impl ApplicabilityConfigElementConfig {
    fn merge<T>(&mut self, value: &T)
    where
        T: ApplicabilityConfig + Clone,
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
#[derive(Debug, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct ApplicabilityConfigElementLegacy {
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

impl ApplicabilityConfig for ApplicabilityConfigElementLegacy {
    fn get_name(self) -> String {
        self.normalized_name
    }

    fn get_features(self) -> Vec<ApplicabilityTag> {
        self.features
    }

    fn get_substitutions(self) -> Option<Vec<Substitution>> {
        self.substitutions
    }

    fn get_type(&self) -> ApplicabilityConfigElementType {
        ApplicabilityConfigElementType::Config
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

impl ApplicabilityConfigElementLegacy {
    fn merge<T>(&mut self, value: &T)
    where
        T: ApplicabilityConfig + Clone,
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
#[derive(Debug, Deserialize, Clone)]
pub struct ApplicabilityConfigElementConfigGroup {
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
impl ApplicabilityConfig for ApplicabilityConfigElementConfigGroup {
    fn get_name(self) -> String {
        self.name
    }

    fn get_features(self) -> Vec<ApplicabilityTag> {
        self.features
    }

    fn get_substitutions(self) -> Option<Vec<Substitution>> {
        self.substitutions
    }

    fn get_type(&self) -> ApplicabilityConfigElementType {
        ApplicabilityConfigElementType::Group
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
impl ApplicabilityConfigElementConfigGroup {
    fn merge<T>(&mut self, value: &T)
    where
        T: ApplicabilityConfig + Clone,
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
#[derive(Debug, Deserialize, Clone)]
#[serde(untagged)]
pub enum ApplicabilityConfigElement {
    Config(ApplicabilityConfigElementConfig),
    ConfigGroup(ApplicabilityConfigElementConfigGroup),
    LegacyConfig(ApplicabilityConfigElementLegacy),
}

pub enum ApplicabilityConfigElementType {
    Config,
    Group,
}

impl ApplicabilityConfig for ApplicabilityConfigElement {
    fn get_name(self) -> String {
        match self {
            ApplicabilityConfigElement::Config(c) => c.get_name(),
            ApplicabilityConfigElement::ConfigGroup(g) => g.get_name(),
            ApplicabilityConfigElement::LegacyConfig(c) => c.get_name(),
        }
    }

    fn get_features(self) -> Vec<ApplicabilityTag> {
        match self {
            ApplicabilityConfigElement::Config(c) => c.get_features(),
            ApplicabilityConfigElement::ConfigGroup(g) => g.get_features(),
            ApplicabilityConfigElement::LegacyConfig(c) => c.get_features(),
        }
    }

    fn get_substitutions(self) -> Option<Vec<Substitution>> {
        match self {
            ApplicabilityConfigElement::Config(c) => c.get_substitutions(),
            ApplicabilityConfigElement::ConfigGroup(g) => g.get_substitutions(),
            ApplicabilityConfigElement::LegacyConfig(c) => c.get_substitutions(),
        }
    }
    fn get_type(&self) -> ApplicabilityConfigElementType {
        match self {
            ApplicabilityConfigElement::Config(c) => c.get_type(),
            ApplicabilityConfigElement::ConfigGroup(g) => g.get_type(),
            ApplicabilityConfigElement::LegacyConfig(c) => c.get_type(),
        }
    }
    fn get_parent_group(&self) -> Option<&str> {
        match self {
            ApplicabilityConfigElement::Config(c) => c.get_parent_group(),
            ApplicabilityConfigElement::ConfigGroup(g) => g.get_parent_group(),
            ApplicabilityConfigElement::LegacyConfig(c) => c.get_parent_group(),
        }
    }
    fn get_configs(&self) -> Vec<&str> {
        match self {
            ApplicabilityConfigElement::Config(c) => c.get_configs(),
            ApplicabilityConfigElement::ConfigGroup(g) => g.get_configs(),
            ApplicabilityConfigElement::LegacyConfig(c) => c.get_configs(),
        }
    }
}
impl ApplicabilityConfigElement {
    pub fn merge<T>(&mut self, value: &T)
    where
        T: ApplicabilityConfig + Clone,
    {
        match self {
            ApplicabilityConfigElement::Config(applicability_config_element_config) => {
                applicability_config_element_config.merge(value)
            }
            ApplicabilityConfigElement::ConfigGroup(applicability_config_element_config_group) => {
                applicability_config_element_config_group.merge(value)
            }
            ApplicabilityConfigElement::LegacyConfig(applicability_config_element_legacy) => {
                applicability_config_element_legacy.merge(value)
            }
        }
    }
}

#[cfg(test)]
mod tests {

    mod json {
        use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};

        use crate::applic_config::{ApplicabilityConfig, ApplicabilityConfigElement};
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
            let result = serde_json::from_str::<ApplicabilityConfigElement>(test_str);
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
            let result = serde_json::from_str::<ApplicabilityConfigElement>(test_str);
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
            let result = serde_json::from_str::<ApplicabilityConfigElement>(test_str);
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
            let result = serde_json::from_str::<ApplicabilityConfigElement>(test_str);
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

        use crate::applic_config::{ApplicabilityConfig, ApplicabilityConfigElement};
        #[test]
        fn test_legacy() {
            let test_str = r#"
            "normalizedName" = "PRODUCT_A"
            features = ["ROBOT_SPKR=SPKR_A","JHU_CONTROLLER"]
            substitutions=[
            {match_text="EVAL_A", substitute = "SOME_SUBSTITUTE"}
            ]
            "#;
            let result = toml::from_str::<ApplicabilityConfigElement>(test_str);
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
            let result = toml::from_str::<ApplicabilityConfigElement>(test_str);
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
            let result = toml::from_str::<ApplicabilityConfigElement>(test_str);
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
            let result = toml::from_str::<ApplicabilityConfigElement>(test_str);
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
            let result = toml::from_str::<ApplicabilityConfigElement>(test_str);
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
