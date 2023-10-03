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
use crate::{
    applicability_parser_syntax_tag::{
        SubstitutionSyntaxTag, SubstitutionSyntaxTagAnd, SubstitutionSyntaxTagOr,
    },
    ApplicabilityParserSyntaxTag,
};
use std::str::FromStr;

pub trait SubstituteApplicability {
    fn substitute(&self, substitutes: &[Substitution]) -> ApplicabilityParserSyntaxTag;
}

#[derive(Debug, Clone, Default)]
pub struct Substitution {
    pub match_text: String,
    pub substitute: String,
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
use serde::{de::Error, Deserialize};
#[cfg(feature = "serde")]
impl<'de> Deserialize<'de> for Substitution {
    fn deserialize<D>(deserializer: D) -> Result<Self, D::Error>
    where
        D: serde::Deserializer<'de>,
    {
        #[derive(Deserialize)]
        #[serde(rename_all = "camelCase")]
        struct SubstitutionInner {
            pub match_text: String,
            pub substitute: String,
        }
        impl From<SubstitutionInner> for Substitution {
            fn from(value: SubstitutionInner) -> Self {
                Self {
                    match_text: value.match_text,
                    substitute: value.substitute,
                }
            }
        }
        #[derive(Deserialize)]
        #[serde(rename_all = "camelCase")]
        #[serde(untagged)]
        enum SubstitutionVariants {
            StringSub(String),
            Substitution(SubstitutionInner),
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
                SubstitutionVariants::Substitution(sub) => Ok(sub.into()),
            },
            Err(deserializer_error) => Err(D::Error::custom(deserializer_error.to_string())),
        }
    }
}

impl SubstituteApplicability for ApplicabilityParserSyntaxTag {
    fn substitute(&self, substitutes: &[Substitution]) -> ApplicabilityParserSyntaxTag {
        match self {
            ApplicabilityParserSyntaxTag::Text(_)
            | ApplicabilityParserSyntaxTag::Tag(_)
            | ApplicabilityParserSyntaxTag::TagAnd(_)
            | ApplicabilityParserSyntaxTag::TagOr(_)
            | ApplicabilityParserSyntaxTag::TagNot(_)
            | ApplicabilityParserSyntaxTag::TagNotAnd(_)
            | ApplicabilityParserSyntaxTag::TagNotOr(_) => self.clone(),
            ApplicabilityParserSyntaxTag::Substitution(t) => t.substitute(substitutes),
            ApplicabilityParserSyntaxTag::SubstitutionOr(t) => t.substitute(substitutes),
            ApplicabilityParserSyntaxTag::SubstitutionAnd(t) => t.substitute(substitutes),
            //intentionally not implemented, future growth if needed, these paths don't exist yet.
            ApplicabilityParserSyntaxTag::SubstitutionNot(_) => todo!(),
            ApplicabilityParserSyntaxTag::SubstitutionNotAnd(_) => todo!(),
            ApplicabilityParserSyntaxTag::SubstitutionNotOr(_) => todo!(),
        }
    }
}
impl SubstituteApplicability for SubstitutionSyntaxTag {
    fn substitute(&self, substitutes: &[Substitution]) -> ApplicabilityParserSyntaxTag {
        //look for the substitutes in the array and return the matching ElementType::Text
        match substitutes
            .iter()
            .filter(|&substitution| &substitution.match_text == self)
            .cloned()
            .map(|substitution_syntax_tag| {
                ApplicabilityParserSyntaxTag::Text(substitution_syntax_tag.substitute)
            })
            .last()
        {
            Some(elem) => elem,
            None => ApplicabilityParserSyntaxTag::Text("".to_string()),
        }
    }
}
impl SubstituteApplicability for SubstitutionSyntaxTagOr {
    fn substitute(&self, substitutes: &[Substitution]) -> ApplicabilityParserSyntaxTag {
        match substitutes
            .iter()
            .filter(|&substitution| self.0.contains(&substitution.match_text))
            .cloned()
            .map(|substitution_syntax_tag| {
                ApplicabilityParserSyntaxTag::Text(substitution_syntax_tag.substitute)
            })
            .last()
        {
            Some(elem) => elem,
            None => ApplicabilityParserSyntaxTag::Text("".to_string()),
        }
    }
}

impl SubstituteApplicability for SubstitutionSyntaxTagAnd {
    fn substitute(&self, substitutes: &[Substitution]) -> ApplicabilityParserSyntaxTag {
        let count = substitutes
            .iter()
            .filter(|&substitution| self.0.contains(&substitution.match_text))
            .cloned()
            .count();
        match count == self.0.len() {
            true => match substitutes
                .iter()
                .filter(|&substitution| self.0.contains(&substitution.match_text))
                .cloned()
                .map(|substitution_syntax_tag| {
                    ApplicabilityParserSyntaxTag::Text(substitution_syntax_tag.substitute)
                })
                .last()
            {
                Some(elem) => elem,
                None => ApplicabilityParserSyntaxTag::Text("".to_string()),
            },
            false => ApplicabilityParserSyntaxTag::Text("".to_string()),
        }
    }
}
