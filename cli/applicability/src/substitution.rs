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
#[cfg(feature = "serde")]
use std::{ops::Add, str::from_utf8};
#[derive(Debug, Clone, Default, PartialEq)]
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
use serde::Serialize;
#[cfg(feature = "serde")]
impl<Tag> Serialize for Substitution<Tag, String>
where
    Tag: for<'a> Add<&'a str, Output = String> + Clone,
{
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::Serializer,
    {
        serializer.serialize_str(&(self.match_text.clone() + "=" + &(self.substitute.clone())))
    }
}
#[cfg(feature = "serde")]
impl<Tag> Serialize for Substitution<Tag, &str>
where
    Tag: for<'a> Add<&'a str, Output = String> + Clone,
{
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::Serializer,
    {
        serializer.serialize_str(&(self.match_text.clone() + "=" + self.substitute))
    }
}
#[cfg(feature = "serde")]
impl<Tag> Serialize for Substitution<Tag, &[u8]>
where
    Tag: for<'a> Add<&'a str, Output = String> + Clone,
{
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::Serializer,
    {
        match from_utf8(self.substitute) {
            Ok(str) => serializer.serialize_str(&(self.match_text.clone() + "=" + str)),
            Err(e) => Err(serde::ser::Error::custom(e.to_string())),
        }
    }
}
#[cfg(feature = "serde")]
#[derive(Deserialize)]
#[serde(rename_all = "camelCase")]
struct SubstitutionInnerJson {
    pub match_text: String,
    pub substitute: String,
}
#[cfg(feature = "serde")]
impl From<SubstitutionInnerJson> for Substitution {
    fn from(value: SubstitutionInnerJson) -> Self {
        Self {
            match_text: value.match_text,
            substitute: value.substitute,
        }
    }
}
#[cfg(feature = "serde")]
#[derive(Deserialize)]
#[serde(rename_all = "snake_case")]
struct SubstitutionInnerToml {
    pub match_text: String,
    pub substitute: String,
}
#[cfg(feature = "serde")]
impl From<SubstitutionInnerToml> for Substitution {
    fn from(value: SubstitutionInnerToml) -> Self {
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
        #[serde(untagged)]
        enum SubstitutionVariants {
            StringSub(String),
            SubstitutionElementJson(SubstitutionInnerJson),
            SubstitutionElementToml(SubstitutionInnerToml),
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
                SubstitutionVariants::SubstitutionElementJson(sub) => Ok(sub.into()),
                SubstitutionVariants::SubstitutionElementToml(sub) => Ok(sub.into()),
            },
            Err(deserializer_error) => Err(D::Error::custom(deserializer_error.to_string())),
        }
    }
}
