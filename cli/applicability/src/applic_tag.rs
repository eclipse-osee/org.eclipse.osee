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
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilityTag {
    pub tag: String,
    pub value: String,
}
impl From<String> for ApplicabilityTag {
    fn from(value: String) -> Self {
        match value.split_once('=') {
            Some((feat, val)) => ApplicabilityTag {
                tag: feat.to_string(),
                value: val.to_string(),
            },
            None => ApplicabilityTag {
                tag: value,
                value: "INCLUDED".to_string(),
            },
        }
    }
}
impl PartialEq<str> for ApplicabilityTag {
    fn eq(&self, other: &str) -> bool {
        self.tag == *other
    }
}
impl PartialEq<String> for ApplicabilityTag {
    fn eq(&self, other: &String) -> bool {
        self.tag == *other
    }
}
#[cfg(feature = "serde")]
use serde::{de::Error, Deserialize};
#[cfg(feature = "serde")]
impl<'de> Deserialize<'de> for ApplicabilityTag {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        match String::deserialize(deserializer) {
            Ok(value) => Ok(value.into()),
            Err(deserializer_error) => Err(D::Error::custom(deserializer_error.to_string())),
        }
    }
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum ApplicabilityTagTypes {
    #[default]
    Feature,
    Configuration,
    ConfigurationGroup,
}
