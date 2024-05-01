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
/// Applicability Config to setup valid features for parser for config
#[derive(Debug, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
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
}
/// Applicability Config to setup valid features for parser for config group
#[derive(Debug, Deserialize, Clone)]
#[serde(rename_all = "camelCase")]
pub struct ApplicabilityConfigElementConfigGroup {
    /// Name of the configuration or configuration group
    pub name: String,
    // Related Config Group
    pub configs: Vec<String>,
    /// list of valid feature tags to parse for this configuration
    pub features: Vec<ApplicabilityTag>,
    /// list of valid substitutions to make for this configuration
    pub substitutions: Option<Vec<Substitution>>,
}

// /// Applicability Config to setup valid features for parser
// #[derive(Debug, Deserialize, Clone)]
// #[serde(rename_all = "camelCase")]
// pub struct ApplicabilityConfigElement {
//     /// Name of the configuration or configuration group
//     pub name: String,
//     /// list of valid feature tags to parse for this configuration
//     pub features: Vec<ApplicabilityTag>,
//     /// list of valid substitutions to make for this configuration
//     pub substitutions: Option<Vec<Substitution>>,
// }

/// Applicability Config to setup valid features for parser
#[derive(Debug, Deserialize, Clone)]
#[serde(rename_all = "camelCase", untagged)]
pub enum ApplicabilityConfigElement {
    Config(ApplicabilityConfigElementConfig),
    ConfigGroup(ApplicabilityConfigElementConfigGroup),
    LegacyConfig(ApplicabilityConfigElementLegacy),
}

pub enum ApplicabilityConfigElementType {
    Config,
    Group,
}

impl ApplicabilityConfigElement {
    pub fn get_name(self) -> String {
        match self {
            ApplicabilityConfigElement::Config(c) => c.name,
            ApplicabilityConfigElement::ConfigGroup(g) => g.name,
            ApplicabilityConfigElement::LegacyConfig(c) => c.normalized_name,
        }
    }

    pub fn get_features(self) -> Vec<ApplicabilityTag> {
        match self {
            ApplicabilityConfigElement::Config(c) => c.features,
            ApplicabilityConfigElement::ConfigGroup(g) => g.features,
            ApplicabilityConfigElement::LegacyConfig(c) => c.features,
        }
    }

    pub fn get_substitutions(self) -> Option<Vec<Substitution>> {
        match self {
            ApplicabilityConfigElement::Config(c) => c.substitutions,
            ApplicabilityConfigElement::ConfigGroup(g) => g.substitutions,
            ApplicabilityConfigElement::LegacyConfig(c) => c.substitutions,
        }
    }
    pub fn get_type(&self) -> ApplicabilityConfigElementType {
        match self {
            ApplicabilityConfigElement::Config(_) => ApplicabilityConfigElementType::Config,
            ApplicabilityConfigElement::ConfigGroup(_) => ApplicabilityConfigElementType::Group,
            ApplicabilityConfigElement::LegacyConfig(_) => ApplicabilityConfigElementType::Config,
        }
    }
    pub fn get_parent_group(&self) -> Option<&str> {
        match self {
            ApplicabilityConfigElement::Config(c) => Some(c.group.as_str()),
            ApplicabilityConfigElement::ConfigGroup(_) => None,
            ApplicabilityConfigElement::LegacyConfig(_) => None,
        }
    }
    pub fn get_configs(&self) -> Vec<&str> {
        match self {
            ApplicabilityConfigElement::Config(_) => vec![],
            ApplicabilityConfigElement::ConfigGroup(g) => {
                g.configs.iter().map(|x| &**x).collect::<Vec<&str>>()
            }
            ApplicabilityConfigElement::LegacyConfig(_) => vec![],
        }
    }
}
