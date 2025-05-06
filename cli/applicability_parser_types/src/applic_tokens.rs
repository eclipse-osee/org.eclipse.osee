use std::fmt::Debug;

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
use applicability::{
    applic_tag::{ApplicabilityTag, ApplicabilityTagTypes},
    substitution::Substitution,
};
use tracing::warn;

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ApplicTokens<I = String> {
    NoTag(ApplicabilityNoTag<I>),
    Not(ApplicabilityNotTag<I>),
    And(ApplicabilityAndTag<I>),
    NotAnd(ApplicabilityNotAndTag<I>),
    Or(ApplicabilityOrTag<I>),
    NotOr(ApplicabilityNotOrTag<I>),
    NestedAnd(ApplicabilityNestedAndTag<I>),
    NestedNotAnd(ApplicabilityNestedNotAndTag<I>),
    NestedOr(ApplicabilityNestedOrTag<I>),
    NestedNotOr(ApplicabilityNestedNotOrTag<I>),
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNoTag<I = String>(
    pub ApplicabilityTag<I, String>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNotTag<I = String>(
    pub ApplicabilityTag<I, String>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityAndTag<I = String>(
    pub ApplicabilityTag<I, String>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNotAndTag<I = String>(
    pub ApplicabilityTag<I, String>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityOrTag<I = String>(
    pub ApplicabilityTag<I, String>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNotOrTag<I = String>(
    pub ApplicabilityTag<I, String>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNestedAndTag<I = String>(
    pub Vec<ApplicTokens<I>>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNestedNotAndTag<I = String>(
    pub Vec<ApplicTokens<I>>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNestedOrTag<I = String>(
    pub Vec<ApplicTokens<I>>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNestedNotOrTag<I = String>(
    pub Vec<ApplicTokens<I>>,
    //# of line endings within the ApplicabilityTag
    pub Option<u8>,
);

impl From<ApplicTokens<&str>> for ApplicTokens<String> {
    fn from(value: ApplicTokens<&str>) -> Self {
        match value {
            ApplicTokens::NoTag(applicability_no_tag) => ApplicTokens::NoTag(ApplicabilityNoTag(
                applicability_no_tag.0.into(),
                applicability_no_tag.1,
            )),
            ApplicTokens::Not(applicability_not_tag) => ApplicTokens::Not(ApplicabilityNotTag(
                applicability_not_tag.0.into(),
                applicability_not_tag.1,
            )),
            ApplicTokens::And(applicability_and_tag) => ApplicTokens::And(ApplicabilityAndTag(
                applicability_and_tag.0.into(),
                applicability_and_tag.1,
            )),
            ApplicTokens::NotAnd(applicability_not_and_tag) => {
                ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                    applicability_not_and_tag.0.into(),
                    applicability_not_and_tag.1,
                ))
            }
            ApplicTokens::Or(applicability_or_tag) => ApplicTokens::Or(ApplicabilityOrTag(
                applicability_or_tag.0.into(),
                applicability_or_tag.1,
            )),
            ApplicTokens::NotOr(applicability_not_or_tag) => {
                ApplicTokens::NotOr(ApplicabilityNotOrTag(
                    applicability_not_or_tag.0.into(),
                    applicability_not_or_tag.1,
                ))
            }
            ApplicTokens::NestedAnd(applicability_nested_and_tag) => {
                ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(
                    applicability_nested_and_tag
                        .0
                        .into_iter()
                        .map(|x| x.into())
                        .collect::<Vec<_>>(),
                    applicability_nested_and_tag.1,
                ))
            }
            ApplicTokens::NestedNotAnd(applicability_nested_not_and_tag) => {
                ApplicTokens::NestedNotAnd(ApplicabilityNestedNotAndTag(
                    applicability_nested_not_and_tag
                        .0
                        .into_iter()
                        .map(|x| x.into())
                        .collect::<Vec<_>>(),
                    applicability_nested_not_and_tag.1,
                ))
            }
            ApplicTokens::NestedOr(applicability_nested_or_tag) => {
                ApplicTokens::NestedOr(ApplicabilityNestedOrTag(
                    applicability_nested_or_tag
                        .0
                        .into_iter()
                        .map(|x| x.into())
                        .collect::<Vec<_>>(),
                    applicability_nested_or_tag.1,
                ))
            }
            ApplicTokens::NestedNotOr(applicability_nested_not_or_tag) => {
                ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(
                    applicability_nested_not_or_tag
                        .0
                        .into_iter()
                        .map(|x| x.into())
                        .collect::<Vec<_>>(),
                    applicability_nested_not_or_tag.1,
                ))
            }
        }
    }
}

impl From<ApplicTokens> for String {
    fn from(applic_tag: ApplicTokens) -> Self {
        match applic_tag {
            ApplicTokens::NoTag(t) => t.0.tag,
            ApplicTokens::Not(t) => "!".to_owned() + &t.0.tag,
            ApplicTokens::And(t) => "&".to_owned() + &t.0.tag,
            ApplicTokens::NotAnd(t) => "& !".to_owned() + &t.0.tag,
            ApplicTokens::Or(t) => "|".to_owned() + &t.0.tag,
            ApplicTokens::NotOr(t) => "| !".to_owned() + &t.0.tag,
            ApplicTokens::NestedAnd(t) => {
                "& (".to_owned()
                    + &t.0
                        .iter()
                        .cloned()
                        .map(|tag| tag.into())
                        .collect::<Vec<String>>()
                        .join("")
                    + ")"
            }
            ApplicTokens::NestedNotAnd(t) => {
                "& !(".to_owned()
                    + &t.0
                        .iter()
                        .cloned()
                        .map(|tag| tag.into())
                        .collect::<Vec<String>>()
                        .join("")
                    + ")"
            }
            ApplicTokens::NestedOr(t) => {
                "| (".to_owned()
                    + &t.0
                        .iter()
                        .cloned()
                        .map(|tag| tag.into())
                        .collect::<Vec<String>>()
                        .join("")
                    + ")"
            }
            ApplicTokens::NestedNotOr(t) => {
                "| !(".to_owned()
                    + &t.0
                        .iter()
                        .cloned()
                        .map(|tag| tag.into())
                        .collect::<Vec<String>>()
                        .join("")
                    + ")"
            }
        }
    }
}

pub trait GetApplicabilityTag<X1> {
    fn get_tag(&self) -> X1;
}

pub trait GetSubstitutionValue<X1> {
    fn get_substitution_value(&self, substitutes: &[Substitution<X1>]) -> String;
}
pub trait MatchToken<T> {
    type TagType;
    fn match_token(
        &self,
        match_list: &[T],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool;
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicTokens<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        match self {
            ApplicTokens::NoTag(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::Not(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::And(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NotAnd(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::Or(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NotOr(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NestedAnd(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NestedNotAnd(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NestedOr(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NestedNotOr(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
        }
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicTokens<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        match self {
            ApplicTokens::NoTag(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::Not(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::And(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NotAnd(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::Or(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NotOr(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NestedAnd(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NestedNotAnd(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NestedOr(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
            ApplicTokens::NestedNotOr(t) => t.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                previous_result,
                applic_type,
            ),
        }
    }
}

impl<I> GetApplicabilityTag<I> for ApplicTokens<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        match self {
            ApplicTokens::NoTag(t) => t.get_tag(),
            ApplicTokens::Not(t) => t.get_tag(),
            ApplicTokens::And(t) => t.get_tag(),
            ApplicTokens::NotAnd(t) => t.get_tag(),
            ApplicTokens::Or(t) => t.get_tag(),
            ApplicTokens::NotOr(t) => t.get_tag(),
            ApplicTokens::NestedAnd(t) => t.get_tag(),
            ApplicTokens::NestedNotAnd(t) => t.get_tag(),
            ApplicTokens::NestedOr(t) => t.get_tag(),
            ApplicTokens::NestedNotOr(t) => t.get_tag(),
        }
    }
}
impl<I> GetSubstitutionValue<I> for ApplicTokens<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        match self {
            ApplicTokens::NoTag(t) => t.get_substitution_value(substitutes),
            ApplicTokens::Not(t) => t.get_substitution_value(substitutes),
            ApplicTokens::And(t) => t.get_substitution_value(substitutes),
            ApplicTokens::NotAnd(t) => t.get_substitution_value(substitutes),
            ApplicTokens::Or(t) => t.get_substitution_value(substitutes),
            ApplicTokens::NotOr(t) => t.get_substitution_value(substitutes),
            ApplicTokens::NestedAnd(t) => t.get_substitution_value(substitutes),
            ApplicTokens::NestedNotAnd(t) => t.get_substitution_value(substitutes),
            ApplicTokens::NestedOr(t) => t.get_substitution_value(substitutes),
            ApplicTokens::NestedNotOr(t) => t.get_substitution_value(substitutes),
        }
    }
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityNoTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        _previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        if *applic_type == ApplicabilityTagTypes::Feature {
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag && self.0.value == applic_tag.value {
                    found = true;
                }
            }
        }
        if (*applic_type == ApplicabilityTagTypes::Configuration
            || *applic_type == ApplicabilityTagTypes::ConfigurationGroup)
            && self.0.tag == *name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == *group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration && configs.contains(&self.0.tag)
            {
                found = true;
            }
        }
        if !found && *applic_type == ApplicabilityTagTypes::Feature {
            let mut found_tag = false;
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag {
                    found_tag = true;
                }
            }
            if !found_tag {
                warn!(
                    "Error processing document. Feature {:#?} was not found.",
                    self.0.tag
                )
            }
        }
        found
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicabilityNoTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        _name: &Self::TagType,
        _parent_group: Option<&Self::TagType>,
        _child_configurations: Option<&[Self::TagType]>,
        _previous_result: bool,
        _applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        for substitution in match_list {
            if self.0.tag == substitution.match_text {
                found = true;
            }
        }
        found
    }
}

impl<I> GetApplicabilityTag<I> for ApplicabilityNoTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0.tag.clone()
    }
}

impl<I> GetSubstitutionValue<I> for ApplicabilityNoTag<I>
where
    I: Clone + PartialEq,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityNotTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        _previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        if *applic_type == ApplicabilityTagTypes::Feature {
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag && self.0.value == applic_tag.value {
                    found = true;
                }
            }
        }
        if (*applic_type == ApplicabilityTagTypes::Configuration
            || *applic_type == ApplicabilityTagTypes::ConfigurationGroup)
            && self.0.tag == *name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == *group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration && configs.contains(&self.0.tag)
            {
                found = true;
            }
        }
        if !found && *applic_type == ApplicabilityTagTypes::Feature {
            let mut found_tag = false;
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag {
                    found_tag = true;
                }
            }
            if !found_tag {
                warn!(
                    "Error processing document. Feature {:#?} was not found.",
                    self.0.tag
                )
            }
        }
        !found
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicabilityNotTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        _name: &Self::TagType,
        _parent_group: Option<&Self::TagType>,
        _child_configurations: Option<&[Self::TagType]>,
        _previous_result: bool,
        _applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        for substitution in match_list {
            if self.0.tag == substitution.match_text {
                found = true;
            }
        }
        !found
    }
}

impl<I> GetApplicabilityTag<I> for ApplicabilityNotTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0.tag.clone()
    }
}
impl<I> GetSubstitutionValue<I> for ApplicabilityNotTag<I>
where
    I: Clone + PartialEq,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityAndTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        if *applic_type == ApplicabilityTagTypes::Feature {
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag && self.0.value == applic_tag.value {
                    found = true;
                }
            }
        }
        if (*applic_type == ApplicabilityTagTypes::Configuration
            || *applic_type == ApplicabilityTagTypes::ConfigurationGroup)
            && self.0.tag == *name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == *group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration && configs.contains(&self.0.tag)
            {
                found = true;
            }
        }
        if !found && *applic_type == ApplicabilityTagTypes::Feature {
            let mut found_tag = false;
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag {
                    found_tag = true;
                }
            }
            if !found_tag {
                warn!(
                    "Error processing document. Feature {:#?} was not found.",
                    self.0.tag
                )
            }
        }
        found & previous_result
    }
}
impl<X1> MatchToken<Substitution<X1>> for ApplicabilityAndTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        _name: &Self::TagType,
        _parent_group: Option<&Self::TagType>,
        _child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        _applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        for substitution in match_list {
            if self.0.tag == substitution.match_text {
                found = true;
            }
        }
        found & previous_result
    }
}

impl<I> GetApplicabilityTag<I> for ApplicabilityAndTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0.tag.clone()
    }
}
impl<I> GetSubstitutionValue<I> for ApplicabilityAndTag<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}
impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityNotAndTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        if *applic_type == ApplicabilityTagTypes::Feature {
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag && self.0.value == applic_tag.value {
                    found = true;
                }
            }
        }
        if (*applic_type == ApplicabilityTagTypes::Configuration
            || *applic_type == ApplicabilityTagTypes::ConfigurationGroup)
            && self.0.tag == *name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == *group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration && configs.contains(&self.0.tag)
            {
                found = true;
            }
        }
        if !found && *applic_type == ApplicabilityTagTypes::Feature {
            let mut found_tag = false;
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag {
                    found_tag = true;
                }
            }
            if !found_tag {
                warn!(
                    "Error processing document. Feature {:#?} was not found.",
                    self.0.tag
                )
            }
        }
        !found && previous_result
    }
}
impl<X1> MatchToken<Substitution<X1>> for ApplicabilityNotAndTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        _name: &Self::TagType,
        _parent_group: Option<&Self::TagType>,
        _child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        _applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        for substitution in match_list {
            if self.0.tag == substitution.match_text {
                found = true;
            }
        }
        !found & previous_result
    }
}

impl<I> GetApplicabilityTag<I> for ApplicabilityNotAndTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0.tag.clone()
    }
}

impl<I> GetSubstitutionValue<I> for ApplicabilityNotAndTag<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityOrTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        if *applic_type == ApplicabilityTagTypes::Feature {
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag && self.0.value == applic_tag.value {
                    found = true;
                }
            }
        }
        if (*applic_type == ApplicabilityTagTypes::Configuration
            || *applic_type == ApplicabilityTagTypes::ConfigurationGroup)
            && self.0.tag == *name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == *group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration && configs.contains(&self.0.tag)
            {
                found = true;
            }
        }
        if !found && *applic_type == ApplicabilityTagTypes::Feature {
            let mut found_tag = false;
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag {
                    found_tag = true;
                }
            }
            if !found_tag {
                warn!(
                    "Error processing document. Feature {:#?} was not found.",
                    self.0.tag
                )
            }
        }
        found || previous_result
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicabilityOrTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        _name: &Self::TagType,
        _parent_group: Option<&Self::TagType>,
        _child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        _applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        for substitution in match_list {
            if self.0.tag == substitution.match_text {
                found = true;
            }
        }
        found || previous_result
    }
}

impl<I> GetApplicabilityTag<I> for ApplicabilityOrTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0.tag.clone()
    }
}

impl<I> GetSubstitutionValue<I> for ApplicabilityOrTag<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}
impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityNotOrTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        if *applic_type == ApplicabilityTagTypes::Feature {
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag && self.0.value == applic_tag.value {
                    found = true;
                }
            }
        }
        if (*applic_type == ApplicabilityTagTypes::Configuration
            || *applic_type == ApplicabilityTagTypes::ConfigurationGroup)
            && self.0.tag == *name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == *group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration && configs.contains(&self.0.tag)
            {
                found = true;
            }
        }
        if !found && *applic_type == ApplicabilityTagTypes::Feature {
            let mut found_tag = false;
            for applic_tag in match_list {
                if self.0.tag == applic_tag.tag {
                    found_tag = true;
                }
            }
            if !found_tag {
                warn!(
                    "Error processing document. Feature {:#?} was not found.",
                    self.0.tag
                )
            }
        }
        !found || previous_result
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicabilityNotOrTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        _name: &Self::TagType,
        _parent_group: Option<&Self::TagType>,
        _child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        _applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut found = false;
        for substitution in match_list {
            if self.0.tag == substitution.match_text {
                found = true;
            }
        }
        !found || previous_result
    }
}

impl<I> GetApplicabilityTag<I> for ApplicabilityNotOrTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0.tag.clone()
    }
}

impl<I> GetSubstitutionValue<I> for ApplicabilityNotOrTag<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityNestedAndTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        for tag in &self.0 {
            current_result = tag.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                current_result,
                applic_type,
            );
        }
        current_result && previous_result
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicabilityNestedAndTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        for tag in &self.0 {
            current_result = tag.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                current_result,
                applic_type,
            );
        }
        current_result && previous_result
    }
}

impl<I> GetApplicabilityTag<I> for ApplicabilityNestedAndTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0
            .iter()
            .cloned()
            .map(|t| t.get_tag())
            .collect::<Vec<_>>()
            .first()
            .unwrap()
            .clone()
        // .join("")
    }
}

impl<I> GetSubstitutionValue<I> for ApplicabilityNestedAndTag<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityNestedNotAndTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        for tag in &self.0 {
            current_result = tag.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                current_result,
                applic_type,
            );
        }
        !current_result && previous_result
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicabilityNestedNotAndTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        for tag in &self.0 {
            current_result = tag.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                current_result,
                applic_type,
            );
        }
        !current_result && previous_result
    }
}
impl<I> GetApplicabilityTag<I> for ApplicabilityNestedNotAndTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0
            .iter()
            .cloned()
            .map(|t| t.get_tag())
            .collect::<Vec<_>>()
            .first()
            .unwrap()
            .clone()
    }
}
impl<I> GetSubstitutionValue<I> for ApplicabilityNestedNotAndTag<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityNestedOrTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        for tag in &self.0 {
            current_result = tag.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                current_result,
                applic_type,
            );
        }
        current_result || previous_result
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicabilityNestedOrTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        for tag in &self.0 {
            current_result = tag.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                current_result,
                applic_type,
            );
        }
        current_result || previous_result
    }
}
impl<I> GetApplicabilityTag<I> for ApplicabilityNestedOrTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0
            .iter()
            .cloned()
            .map(|t| t.get_tag())
            .collect::<Vec<_>>()
            .first()
            .unwrap()
            .clone()
    }
}
impl<I> GetSubstitutionValue<I> for ApplicabilityNestedOrTag<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl<X1> MatchToken<ApplicabilityTag<X1>> for ApplicabilityNestedNotOrTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        for tag in &self.0 {
            current_result = tag.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                current_result,
                applic_type,
            );
        }
        !current_result || previous_result
    }
}

impl<X1> MatchToken<Substitution<X1>> for ApplicabilityNestedNotOrTag<X1>
where
    X1: PartialEq + Debug,
{
    type TagType = X1;
    fn match_token(
        &self,
        match_list: &[Substitution<X1>],
        name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        for tag in &self.0 {
            current_result = tag.match_token(
                match_list,
                name,
                parent_group,
                child_configurations,
                current_result,
                applic_type,
            );
        }
        !current_result || previous_result
    }
}
impl<I> GetApplicabilityTag<I> for ApplicabilityNestedNotOrTag<I>
where
    I: Clone,
{
    fn get_tag(&self) -> I {
        self.0
            .iter()
            .cloned()
            .map(|t| t.get_tag())
            .collect::<Vec<_>>()
            .first()
            .unwrap()
            .clone()
    }
}
impl<I> GetSubstitutionValue<I> for ApplicabilityNestedNotOrTag<I>
where
    I: PartialEq + Clone,
{
    fn get_substitution_value(&self, substitutes: &[Substitution<I>]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}
