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

#[cfg(feature = "serde")]
use std::{ops::Add, str::from_utf8};

use std::sync::LazyLock;

use memchr::memmem;
use nom_locate::LocatedSpan;
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

impl<'a> From<&'a str> for ApplicabilityTag<String> {
    fn from(value: &'a str) -> Self {
        Into::<ApplicabilityTag<&'a str, String>>::into(value).into()
    }
}

impl From<ApplicabilityTag<&str>> for ApplicabilityTag<String> {
    fn from(value: ApplicabilityTag<&str>) -> Self {
        ApplicabilityTag {
            tag: value.tag.to_string(),
            value: value.value,
        }
    }
}

impl<'a> From<&'a [u8]> for ApplicabilityTag<&'a [u8], String> {
    fn from(value: &'a [u8]) -> Self {
        let finder = &*EQUALS_FINDER;
        let potential_position = finder.find(value);
        match potential_position {
            Some(position) => {
                // Split into the feature bytes and the value bytes, dropping the
                // `=` delimiter that sits at `position`.
                let (feat, value_with_delimiter) = value.split_at(position);
                let value_bytes = &value_with_delimiter[1..];
                ApplicabilityTag {
                    tag: feat,
                    // `&[u8]` carries no UTF-8 guarantee, so convert losslessly
                    // rather than assuming validity via `from_utf8_unchecked`.
                    value: String::from_utf8_lossy(value_bytes).into_owned(),
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
#[cfg(feature = "serde")]
use serde::Serialize;
#[cfg(feature = "serde")]
use serde::{Deserialize, de::Error};
#[cfg(feature = "serde")]
impl<'de, X> Deserialize<'de> for ApplicabilityTag<X>
where
    ApplicabilityTag<X>: From<String>,
{
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
#[cfg(feature = "serde")]
impl<Tag> Serialize for ApplicabilityTag<Tag, String>
where
    Tag: for<'a> Add<&'a str, Output = String> + Clone,
{
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::Serializer,
    {
        serializer.serialize_str(&(self.tag.clone() + "=" + &(self.value.clone())))
    }
}
#[cfg(feature = "serde")]
impl<Tag> Serialize for ApplicabilityTag<Tag, &str>
where
    Tag: for<'a> Add<&'a str, Output = String> + Clone,
{
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::Serializer,
    {
        serializer.serialize_str(&(self.tag.clone() + "=" + self.value))
    }
}
#[cfg(feature = "serde")]
impl<Tag> Serialize for ApplicabilityTag<Tag, &[u8]>
where
    Tag: for<'a> Add<&'a str, Output = String> + Clone,
{
    fn serialize<S>(&self, serializer: S) -> Result<S::Ok, S::Error>
    where
        S: serde::Serializer,
    {
        match from_utf8(self.value) {
            Ok(str) => serializer.serialize_str(&(self.tag.clone() + "=" + str)),
            Err(e) => Err(serde::ser::Error::custom(e.to_string())),
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

#[cfg(test)]
mod applicability_tag_conversion_tests {
    use super::ApplicabilityTag;

    // The three `From` impls (`String`, `&str`, `&[u8]`) must agree on how a
    // `tag=value` pair is split: the tag is everything before the first `=`,
    // the value is everything after it (the `=` itself is dropped).

    #[test]
    fn from_string_splits_on_equals() {
        let tag: ApplicabilityTag = ApplicabilityTag::from("hello=world".to_string());
        assert_eq!(tag.tag, "hello");
        assert_eq!(tag.value, "world");
    }

    #[test]
    fn from_str_splits_on_equals() {
        let tag: ApplicabilityTag<&str, String> = ApplicabilityTag::from("hello=world");
        assert_eq!(tag.tag, "hello");
        assert_eq!(tag.value, "world");
    }

    #[test]
    fn from_bytes_splits_on_equals() {
        // Regression test: `From<&[u8]>` previously kept the `=` in the value
        // (yielding "=world") and used `from_utf8_unchecked`.
        let tag: ApplicabilityTag<&[u8], String> =
            ApplicabilityTag::from(b"hello=world".as_slice());
        assert_eq!(tag.tag, b"hello");
        assert_eq!(tag.value, "world");
    }

    #[test]
    fn from_string_without_equals_defaults_to_included() {
        let tag: ApplicabilityTag = ApplicabilityTag::from("hello".to_string());
        assert_eq!(tag.tag, "hello");
        assert_eq!(tag.value, "Included");
    }

    #[test]
    fn from_str_without_equals_defaults_to_included() {
        let tag: ApplicabilityTag<&str, String> = ApplicabilityTag::from("hello");
        assert_eq!(tag.tag, "hello");
        assert_eq!(tag.value, "Included");
    }

    #[test]
    fn from_bytes_without_equals_defaults_to_included() {
        let tag: ApplicabilityTag<&[u8], String> = ApplicabilityTag::from(b"hello".as_slice());
        assert_eq!(tag.tag, b"hello");
        assert_eq!(tag.value, "Included");
    }

    #[test]
    fn from_bytes_with_invalid_utf8_value_is_lossy_not_ub() {
        // 0xFF is not valid UTF-8; the fix converts losslessly instead of
        // invoking undefined behavior via `from_utf8_unchecked`.
        let tag: ApplicabilityTag<&[u8], String> =
            ApplicabilityTag::from([b'k', b'=', 0xFF].as_slice());
        assert_eq!(tag.tag, b"k");
        assert_eq!(tag.value, "\u{FFFD}");
    }
}
