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
use std::str::FromStr;
#[derive(Debug, Clone, Default)]
pub struct Substitution<I1 = String, I2 = String> {
    pub match_text: I1,
    pub substitute: I2,
}

impl FromStr for Substitution {
    type Err = ();

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        let split = s.split_once('=');
        match split {
            Some((matching_text, substitution_text)) => Ok(Self {
                match_text: matching_text.to_string(),
                substitute: substitution_text.to_string(),
            }),
            None => Ok(Self::default()),
        }
    }
}
#[cfg(feature = "serde")]
#[derive(Deserialize)]
#[serde(rename_all = "camelCase")]
struct SubstitutionInner {
    pub match_text: String,
    pub substitute: String,
}
#[cfg(feature = "serde")]
impl From<SubstitutionInner> for Substitution {
    fn from(value: SubstitutionInner) -> Self {
        Self {
            match_text: value.match_text,
            substitute: value.substitute,
        }
    }
}
#[cfg(feature = "serde")]
use serde::{Deserialize, de::Error};
#[cfg(feature = "serde")]
impl<'de> Deserialize<'de> for Substitution {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        #[derive(Deserialize)]
        #[serde(rename_all = "camelCase")]
        #[serde(untagged)]
        enum SubstitutionVariants {
            StringSub(String),
            SubstitutionElement(SubstitutionInner),
        }
        match SubstitutionVariants::deserialize(deserializer) {
            Ok(result) => match result {
                SubstitutionVariants::StringSub(str) => match str.split_once('=') {
                    Some((match_text, substitution_text)) => Ok(Self {
                        match_text: match_text.to_string(),
                        substitute: substitution_text.to_string(),
                    }),
                    None => Ok(Self {
                        match_text: str,
                        substitute: "".to_string(),
                    }),
                },
                SubstitutionVariants::SubstitutionElement(sub) => Ok(sub.into()),
            },
            Err(deserializer_error) => Err(D::Error::custom(deserializer_error.to_string())),
        }
    }
}
