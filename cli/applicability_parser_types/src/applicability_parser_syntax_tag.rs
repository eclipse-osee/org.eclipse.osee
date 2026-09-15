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
use applicability::applic_tag::ApplicabilityTagTypes;

use crate::applic_tokens::ApplicTokens;

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum LineEnding {
    #[default]
    NoLineEndings,
    StartLineEnding,
    EndLineEnding,
    StartAndEndLineEnding,
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ApplicabilityParserSyntaxTag<X1 = String> {
    Text(String),
    Tag(ApplicabilitySyntaxTag<X1>),
    TagNot(ApplicabilitySyntaxTagNot<X1>),
    Substitution(SubstitutionSyntaxTag<X1>),
    SubstitutionNot(String),
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilitySyntaxTag<X1 = String>(
    pub Vec<ApplicTokens<X1>>,
    pub Vec<ApplicabilityParserSyntaxTag>,
    pub ApplicabilityTagTypes,
    pub Vec<ApplicabilityParserSyntaxTag>,
    /// start syntax tag line length, note: actual value is n+1
    pub u8,
    /// else syntax tag line length, note: actual value is n
    pub u8,
    /// end syntax tag line length, note: actual value is n+1
    pub u8,
);
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilitySyntaxTagNot<X1 = String>(
    pub Vec<ApplicTokens<X1>>,
    pub Vec<ApplicabilityParserSyntaxTag>,
    pub ApplicabilityTagTypes,
    pub Vec<ApplicabilityParserSyntaxTag>,
    /// start syntax tag line length, note: actual value is n+1
    pub u8,
    /// else syntax tag line length, note: actual value is n
    pub u8,
    /// end syntax tag line length, note: actual value is n+1
    pub u8,
);

pub type SubstitutionSyntaxTag<X1 = String> = Vec<ApplicTokens<X1>>;

impl From<&str> for ApplicabilityParserSyntaxTag {
    fn from(item: &str) -> Self {
        ApplicabilityParserSyntaxTag::Text(item.to_string())
    }
}

impl From<ApplicabilityParserSyntaxTag> for String {
    fn from(applicability_parser_syntax_tag: ApplicabilityParserSyntaxTag) -> Self {
        match applicability_parser_syntax_tag {
            ApplicabilityParserSyntaxTag::Text(t) => t,
            ApplicabilityParserSyntaxTag::Tag(_) => panic!(
                "content did not get fully processed, expected ElementType::Text, found ElementType::Tag"
            ),
            ApplicabilityParserSyntaxTag::TagNot(_) => panic!(
                "content did not get fully processed, expected ElementType::Text, found ElementType::TagNot"
            ),
            ApplicabilityParserSyntaxTag::Substitution(_) => panic!(
                "content did not get fully processed, expected ElementType::Text, found ElementType::Substitution"
            ),
            ApplicabilityParserSyntaxTag::SubstitutionNot(_) => panic!(
                "content did not get fully processed, expected ElementType::Text, found ElementType::SubstitutionNot"
            ),
        }
    }
}
