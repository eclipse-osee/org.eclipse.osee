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

static EQUALS_FINDER: LazyLock<memmem::Finder> = LazyLock::new(|| memmem::Finder::new("="));
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilityTag<I1 = String, I2 = String> {
    pub tag: I1,
    pub value: I2,
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
                value: "Included".to_string(),
            },
        }
    }
}

impl<'a> From<&'a str> for ApplicabilityTag<&'a str, String> {
    fn from(value: &'a str) -> Self {
        match value.split_once('=') {
            Some((feat, val)) => ApplicabilityTag {
                tag: feat,
                value: val.to_string(),
            },
            None => ApplicabilityTag {
                tag: value,
                value: "Included".to_string(),
            },
        }
    }
}

impl<'a> From<&'a [u8]> for ApplicabilityTag<&'a [u8], String> {
    fn from(value: &'a [u8]) -> Self {
        let finder = &*EQUALS_FINDER;
        let potential_position = finder.find(value);
        match potential_position {
            Some(position) => {
                let (feat, tag) = value.split_at(position);
                ApplicabilityTag {
                    tag: feat,
                    value: unsafe { String::from_utf8_unchecked(tag.to_vec()) },
                }
            }
            None => ApplicabilityTag {
                tag: value,
                value: "Included".to_string(),
            },
        }
    }
}
impl<I, X> From<LocatedSpan<I, X>> for ApplicabilityTag<I, String>
where
    I: Into<ApplicabilityTag<I, String>>,
{
    fn from(value: LocatedSpan<I, X>) -> Self {
        value.into_fragment().into()
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

impl From<ApplicabilityTag> for String {
    fn from(applic: ApplicabilityTag) -> Self {
        applic.tag + "=" + &applic.value
    }
}
use std::sync::LazyLock;

use memchr::memmem;
use nom_locate::LocatedSpan;
#[cfg(feature = "serde")]
use serde::{Deserialize, de::Error};
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
