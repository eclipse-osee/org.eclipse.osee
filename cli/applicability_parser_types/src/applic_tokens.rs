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
pub enum ApplicTokens {
    NoTag(ApplicabilityNoTag),
    Not(ApplicabilityNotTag),
    And(ApplicabilityAndTag),
    NotAnd(ApplicabilityNotAndTag),
    Or(ApplicabilityOrTag),
    NotOr(ApplicabilityNotOrTag),
    NestedAnd(ApplicabilityNestedAndTag),
    NestedNotAnd(ApplicabilityNestedNotAndTag),
    NestedOr(ApplicabilityNestedOrTag),
    NestedNotOr(ApplicabilityNestedNotOrTag),
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNoTag(
    pub ApplicabilityTag,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNotTag(
    pub ApplicabilityTag,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityAndTag(
    pub ApplicabilityTag,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNotAndTag(
    pub ApplicabilityTag,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityOrTag(
    pub ApplicabilityTag,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNotOrTag(
    pub ApplicabilityTag,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNestedAndTag(
    pub Vec<ApplicTokens>,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNestedNotAndTag(
    pub Vec<ApplicTokens>,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNestedOrTag(
    pub Vec<ApplicTokens>,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNestedNotOrTag(
    pub Vec<ApplicTokens>,
    //# of line endings within the ApplicabilityTag
    pub u8,
);

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

pub trait GetApplicabilityTag {
    fn get_tag(&self) -> String;
}

pub trait GetSubstitutionValue {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String;
}
pub trait MatchToken<T> {
    fn match_token(
        &self,
        match_list: &[T],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool;
}

impl MatchToken<ApplicabilityTag> for ApplicTokens {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
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

impl MatchToken<Substitution> for ApplicTokens {
    fn match_token(
        &self,
        match_list: &[Substitution],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
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

impl GetApplicabilityTag for ApplicTokens {
    fn get_tag(&self) -> String {
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
impl GetSubstitutionValue for ApplicTokens {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
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

impl MatchToken<ApplicabilityTag> for ApplicabilityNoTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
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
            && self.0.tag == name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration
                && configs.contains(&self.0.tag.as_str())
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

impl MatchToken<Substitution> for ApplicabilityNoTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        _name: &str,
        _parent_group: Option<&str>,
        _child_configurations: Option<&[&str]>,
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

impl GetApplicabilityTag for ApplicabilityNoTag {
    fn get_tag(&self) -> String {
        self.0.tag.clone()
    }
}

impl GetSubstitutionValue for ApplicabilityNoTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl MatchToken<ApplicabilityTag> for ApplicabilityNotTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
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
            && self.0.tag == name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration
                && configs.contains(&self.0.tag.as_str())
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

impl MatchToken<Substitution> for ApplicabilityNotTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        _name: &str,
        _parent_group: Option<&str>,
        _child_configurations: Option<&[&str]>,
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

impl GetApplicabilityTag for ApplicabilityNotTag {
    fn get_tag(&self) -> String {
        self.0.tag.clone()
    }
}
impl GetSubstitutionValue for ApplicabilityNotTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl MatchToken<ApplicabilityTag> for ApplicabilityAndTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
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
            && self.0.tag == name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration
                && configs.contains(&self.0.tag.as_str())
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
impl MatchToken<Substitution> for ApplicabilityAndTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        _name: &str,
        _parent_group: Option<&str>,
        _child_configurations: Option<&[&str]>,
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

impl GetApplicabilityTag for ApplicabilityAndTag {
    fn get_tag(&self) -> String {
        self.0.tag.clone()
    }
}
impl GetSubstitutionValue for ApplicabilityAndTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}
impl MatchToken<ApplicabilityTag> for ApplicabilityNotAndTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
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
            && self.0.tag == name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration
                && configs.contains(&self.0.tag.as_str())
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
impl MatchToken<Substitution> for ApplicabilityNotAndTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        _name: &str,
        _parent_group: Option<&str>,
        _child_configurations: Option<&[&str]>,
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

impl GetApplicabilityTag for ApplicabilityNotAndTag {
    fn get_tag(&self) -> String {
        self.0.tag.clone()
    }
}

impl GetSubstitutionValue for ApplicabilityNotAndTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl MatchToken<ApplicabilityTag> for ApplicabilityOrTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
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
            && self.0.tag == name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration
                && configs.contains(&self.0.tag.as_str())
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

impl MatchToken<Substitution> for ApplicabilityOrTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        _name: &str,
        _parent_group: Option<&str>,
        _child_configurations: Option<&[&str]>,
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

impl GetApplicabilityTag for ApplicabilityOrTag {
    fn get_tag(&self) -> String {
        self.0.tag.clone()
    }
}

impl GetSubstitutionValue for ApplicabilityOrTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}
impl MatchToken<ApplicabilityTag> for ApplicabilityNotOrTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
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
            && self.0.tag == name
        {
            found = true;
        }
        if let Some(group) = parent_group {
            if *applic_type == ApplicabilityTagTypes::ConfigurationGroup && self.0.tag == group {
                found = true;
            }
        }
        if let Some(configs) = child_configurations {
            if *applic_type == ApplicabilityTagTypes::Configuration
                && configs.contains(&self.0.tag.as_str())
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

impl MatchToken<Substitution> for ApplicabilityNotOrTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        _name: &str,
        _parent_group: Option<&str>,
        _child_configurations: Option<&[&str]>,
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

impl GetApplicabilityTag for ApplicabilityNotOrTag {
    fn get_tag(&self) -> String {
        self.0.tag.clone()
    }
}

impl GetSubstitutionValue for ApplicabilityNotOrTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl MatchToken<ApplicabilityTag> for ApplicabilityNestedAndTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        let tags = self.0.to_vec();
        for tag in tags {
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

impl MatchToken<Substitution> for ApplicabilityNestedAndTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        let tags = self.0.to_vec();
        for tag in tags {
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

impl GetApplicabilityTag for ApplicabilityNestedAndTag {
    fn get_tag(&self) -> String {
        self.0
            .iter()
            .cloned()
            .map(|t| t.get_tag())
            .collect::<Vec<String>>()
            .join("")
    }
}

impl GetSubstitutionValue for ApplicabilityNestedAndTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl MatchToken<ApplicabilityTag> for ApplicabilityNestedNotAndTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        let tags = self.0.to_vec();
        for tag in tags {
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

impl MatchToken<Substitution> for ApplicabilityNestedNotAndTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        let tags = self.0.to_vec();
        for tag in tags {
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
impl GetApplicabilityTag for ApplicabilityNestedNotAndTag {
    fn get_tag(&self) -> String {
        self.0
            .iter()
            .cloned()
            .map(|t| t.get_tag())
            .collect::<Vec<String>>()
            .join("")
    }
}
impl GetSubstitutionValue for ApplicabilityNestedNotAndTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl MatchToken<ApplicabilityTag> for ApplicabilityNestedOrTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        let tags = self.0.to_vec();
        for tag in tags {
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

impl MatchToken<Substitution> for ApplicabilityNestedOrTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        let tags = self.0.to_vec();
        for tag in tags {
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
impl GetApplicabilityTag for ApplicabilityNestedOrTag {
    fn get_tag(&self) -> String {
        self.0
            .iter()
            .cloned()
            .map(|t| t.get_tag())
            .collect::<Vec<String>>()
            .join("")
    }
}
impl GetSubstitutionValue for ApplicabilityNestedOrTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}

impl MatchToken<ApplicabilityTag> for ApplicabilityNestedNotOrTag {
    fn match_token(
        &self,
        match_list: &[ApplicabilityTag],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        let tags = self.0.to_vec();
        for tag in tags {
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

impl MatchToken<Substitution> for ApplicabilityNestedNotOrTag {
    fn match_token(
        &self,
        match_list: &[Substitution],
        name: &str,
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        previous_result: bool,
        applic_type: &ApplicabilityTagTypes,
    ) -> bool {
        let mut current_result = false;
        let tags = self.0.to_vec();
        for tag in tags {
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
impl GetApplicabilityTag for ApplicabilityNestedNotOrTag {
    fn get_tag(&self) -> String {
        self.0
            .iter()
            .cloned()
            .map(|t| t.get_tag())
            .collect::<Vec<String>>()
            .join("")
    }
}
impl GetSubstitutionValue for ApplicabilityNestedNotOrTag {
    fn get_substitution_value(&self, substitutes: &[Substitution]) -> String {
        substitutes
            .iter()
            .filter(|sub| sub.match_text == self.get_tag())
            .cloned()
            .map(|sub| sub.substitute)
            .collect::<Vec<String>>()
            .join("")
    }
}
