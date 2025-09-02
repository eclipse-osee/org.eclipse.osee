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
use toml::map::Map;

#[derive(Debug, Deserialize, Clone)]
pub struct FeatureDefinition<I = String> {
    pub name: I,
    pub values: Vec<I>,
    pub description: I,
    pub product_applicabilities: Option<Vec<I>>,
    pub applic_constraint: Option<I>,
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
