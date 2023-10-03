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
use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ApplicabilityParserSyntaxTag {
    Text(String),
    Tag(ApplicabilitySyntaxTag),
    TagAnd(ApplicabilitySyntaxTagAnd),
    TagOr(ApplicabilitySyntaxTagOr),
    TagNot(ApplicabilitySyntaxTagNot),
    TagNotAnd(ApplicabilitySyntaxTagNotAnd),
    TagNotOr(ApplicabilitySyntaxTagNotOr),
    Substitution(SubstitutionSyntaxTag),
    SubstitutionOr(SubstitutionSyntaxTagOr),
    SubstitutionAnd(SubstitutionSyntaxTagAnd),
    SubstitutionNot(String),
    SubstitutionNotAnd(Vec<String>),
    SubstitutionNotOr(Vec<String>),
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilitySyntaxTag(
    pub Vec<ApplicabilityTag>,
    pub Vec<ApplicabilityParserSyntaxTag>,
    pub ApplicabilityTagTypes,
    pub Vec<ApplicabilityParserSyntaxTag>,
);
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilitySyntaxTagAnd(
    pub Vec<ApplicabilityTag>,
    pub Vec<ApplicabilityParserSyntaxTag>,
    pub ApplicabilityTagTypes,
    pub Vec<ApplicabilityParserSyntaxTag>,
);
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilitySyntaxTagOr(
    pub Vec<ApplicabilityTag>,
    pub Vec<ApplicabilityParserSyntaxTag>,
    pub ApplicabilityTagTypes,
    pub Vec<ApplicabilityParserSyntaxTag>,
);
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilitySyntaxTagNot(
    pub Vec<ApplicabilityTag>,
    pub Vec<ApplicabilityParserSyntaxTag>,
    pub ApplicabilityTagTypes,
    pub Vec<ApplicabilityParserSyntaxTag>,
);
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilitySyntaxTagNotAnd(
    pub Vec<ApplicabilityTag>,
    pub Vec<ApplicabilityParserSyntaxTag>,
    pub ApplicabilityTagTypes,
    pub Vec<ApplicabilityParserSyntaxTag>,
);
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilitySyntaxTagNotOr(
    pub Vec<ApplicabilityTag>,
    pub Vec<ApplicabilityParserSyntaxTag>,
    pub ApplicabilityTagTypes,
    pub Vec<ApplicabilityParserSyntaxTag>,
);
pub type SubstitutionSyntaxTag = String;
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct SubstitutionSyntaxTagOr(pub Vec<String>);
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct SubstitutionSyntaxTagAnd(pub Vec<String>);
pub enum TagVariants {
    And,
    Or,
    Normal,
}
impl From<&str> for ApplicabilityParserSyntaxTag {
    fn from(item: &str) -> Self {
        ApplicabilityParserSyntaxTag::Text(item.to_string())
    }
}

impl From<ApplicabilityParserSyntaxTag> for String {
    fn from(applicability_parser_syntax_tag: ApplicabilityParserSyntaxTag) -> Self {
        match applicability_parser_syntax_tag{
            ApplicabilityParserSyntaxTag::Text(t) => t,
            ApplicabilityParserSyntaxTag::Tag(_) =>                  panic!("content did not get fully processed, expected ElementType::Text, found ElementType::Tag"),
            ApplicabilityParserSyntaxTag::TagAnd(_) =>               panic!("content did not get fully processed, expected ElementType::Text, found ElementType::TagAnd"),
            ApplicabilityParserSyntaxTag::TagOr(_) =>                panic!("content did not get fully processed, expected ElementType::Text, found ElementType::TagOr"),
            ApplicabilityParserSyntaxTag::TagNot(_) =>               panic!("content did not get fully processed, expected ElementType::Text, found ElementType::TagNot"),
            ApplicabilityParserSyntaxTag::TagNotAnd(_) =>            panic!("content did not get fully processed, expected ElementType::Text, found ElementType::TagNotAnd"),
            ApplicabilityParserSyntaxTag::TagNotOr(_) =>             panic!("content did not get fully processed, expected ElementType::Text, found ElementType::TagNotOr"),
            ApplicabilityParserSyntaxTag::Substitution(_) =>         panic!("content did not get fully processed, expected ElementType::Text, found ElementType::Substitution"),
            ApplicabilityParserSyntaxTag::SubstitutionOr(_) =>       panic!("content did not get fully processed, expected ElementType::Text, found ElementType::SubstitutionOr"),
            ApplicabilityParserSyntaxTag::SubstitutionAnd(_) =>      panic!("content did not get fully processed, expected ElementType::Text, found ElementType::SubstitutionAnd"),
            ApplicabilityParserSyntaxTag::SubstitutionNot(_) =>      panic!("content did not get fully processed, expected ElementType::Text, found ElementType::SubstitutionNot"),
            ApplicabilityParserSyntaxTag::SubstitutionNotAnd(_) =>   panic!("content did not get fully processed, expected ElementType::Text, found ElementType::SubstitutionNotAnd"),
            ApplicabilityParserSyntaxTag::SubstitutionNotOr(_) =>    panic!("content did not get fully processed, expected ElementType::Text, found ElementType::SubstitutionNotOr"),
        }
    }
}
