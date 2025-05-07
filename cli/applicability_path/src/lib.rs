use std::fmt::Debug;

// /*********************************************************************
//  * Copyright (c) 2024 Boeing
//  *
//  * This program and the accompanying materials are made
//  * available under the terms of the Eclipse Public License 2.0
//  * which is available at https://www.eclipse.org/legal/epl-2.0/
//  *
//  * SPDX-License-Identifier: EPL-2.0
//  *
//  * Contributors:
//  *     Boeing - initial API and implementation
//  **********************************************************************/
use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
use applicability_match::MatchApplicability;
use applicability_parser_types::{
    applic_tokens::{ApplicTokens, GetSubstitutionValue, MatchToken},
    applicability_parser_syntax_tag::{
        ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag, ApplicabilitySyntaxTagNot,
    },
};
use applicability_tokens_to_ast::tree::{
    ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition, ApplicabilityExprKind,
    ApplicabilityExprSubstitution, ApplicabilityExprTag, Text,
};
// use applicability_substitution::SubstituteApplicability;
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum FileApplicabilityPath<I = String> {
    Included(I),
    Excluded(I),
    Text(I),
} //I is actually String

// impl From<FileApplicabilityPath> for String {
//     fn from(value: FileApplicabilityPath) -> Self {
//         match value {
//             FileApplicabilityPath::Included(text, else_text) => {
//                 if text.is_empty() {
//                     return text;
//                 }
//                 else_text
//             }
//             FileApplicabilityPath::Excluded(text, else_text) => {
//                 if else_text.is_empty() {
//                     return else_text;
//                 }
//                 text
//             }
//             FileApplicabilityPath::Text(t) => t,
//         }
//     }
// }
pub trait ParsePaths<X1> {
    //     /// Turns featurized text into it's corresponding path
    //     ///
    //     /// After calling .parse_path, .into() should be used to turn into it's string, as the outer shell of the text is no longer useful.
    fn parse_path(
        &self,
        features: &[ApplicabilityTag<X1>],
        config_name: &X1,
        substitutes: &[Substitution<X1, X1>],
        parent_group: Option<&X1>,
        child_configurations: Option<&[X1]>,
        is_match: Option<bool>,
    ) -> Vec<FileApplicabilityPath<X1>>;
}

impl<I> ParsePaths<I> for Text<I>
where
    I: Clone + PartialEq + Debug,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn parse_path(
        &self,
        _features: &[ApplicabilityTag<I>],
        _config_name: &I,
        _substitutes: &[Substitution<I, I>],
        _parent_group: Option<&I>,
        _child_configurations: Option<&[I]>,
        _is_match: Option<bool>,
    ) -> Vec<FileApplicabilityPath<I>> {
        vec![FileApplicabilityPath::Text(self.text.clone())]
    }
}

impl<I> ParsePaths<I> for ApplicabilityExprTag<I>
where
    I: Clone + PartialEq + Debug,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn parse_path(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        is_match: Option<bool>,
    ) -> Vec<FileApplicabilityPath<I>> {
        self.contents
            .iter()
            .flat_map(|c| match c {
                ApplicabilityExprKind::None(_applicability_expr_container) => vec![], //invalid condition
                ApplicabilityExprKind::Text(text) => text.parse_path(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    is_match,
                ),
                ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                    applicability_expr_container_with_position.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        is_match,
                    )
                }
                ApplicabilityExprKind::Tag(_applicability_expr_tag) => vec![], //invalid condition
                ApplicabilityExprKind::TagNot(_applicability_expr_tag) => vec![], //invalid condition
                ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                    applicability_expr_substitution.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        is_match,
                    )
                }
            })
            .map(|c| {
                if let Some(matched) = is_match {
                    if !matched {
                        return match c {
                            FileApplicabilityPath::Included(path) => {
                                FileApplicabilityPath::Excluded(path)
                            }
                            FileApplicabilityPath::Excluded(path) => {
                                FileApplicabilityPath::Excluded(path)
                            }
                            FileApplicabilityPath::Text(text) => {
                                FileApplicabilityPath::Excluded(text)
                            }
                        };
                    } else {
                        return match c {
                            FileApplicabilityPath::Included(path) => {
                                FileApplicabilityPath::Included(path)
                            }
                            FileApplicabilityPath::Excluded(path) => {
                                FileApplicabilityPath::Excluded(path)
                            }
                            FileApplicabilityPath::Text(text) => FileApplicabilityPath::Text(text),
                        };
                    }
                }
                c
            })
            .collect()
    }
}

impl<I> ParsePaths<I> for ApplicabilityExprContainerWithPosition<I>
where
    I: Clone + PartialEq + Debug,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn parse_path(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        _is_match: Option<bool>,
    ) -> Vec<FileApplicabilityPath<I>> {
        let mut has_a_match = false;
        self.contents
            .iter()
            .flat_map(|c| match c {
                ApplicabilityExprKind::None(_applicability_expr_container) => vec![], //invalid condition
                ApplicabilityExprKind::Text(text) => {
                    vec![FileApplicabilityPath::Text(text.text.clone())]
                }
                ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                    applicability_expr_container_with_position.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        _is_match,
                    )
                }
                ApplicabilityExprKind::Tag(applicability_expr_tag) => {
                    let is_matches = applicability_expr_tag.match_applicability(
                        features,
                        config_name,
                        parent_group,
                        child_configurations,
                    ) && !has_a_match;
                    //if there is already a match, we want to make it excluded
                    //latch the has_a_match value so we don't flip-flop toggle it and instead only toggle it upon first true occurance
                    if !has_a_match {
                        has_a_match = is_matches;
                    }
                    applicability_expr_tag.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(is_matches),
                    )
                } //if this matches_applicability = wrap in included, else wrap in excluded
                ApplicabilityExprKind::TagNot(applicability_expr_tag) => {
                    let is_matches = !applicability_expr_tag.match_applicability(
                        features,
                        config_name,
                        parent_group,
                        child_configurations,
                    ) && !has_a_match;
                    //if there is already a match, we want to make it excluded

                    //latch the has_a_match value so we don't flip-flop toggle it and instead only toggle it upon first true occurance
                    if !has_a_match {
                        has_a_match = is_matches;
                    }
                    applicability_expr_tag.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(is_matches),
                    )
                } //if this matches_applicability = wrap in excluded, else wrap in included
                ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                    applicability_expr_substitution.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        _is_match,
                    )
                }
            })
            .collect()
    }
}

impl<I> ParsePaths<I> for ApplicabilityExprContainer<I>
where
    I: Clone + PartialEq + Debug,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn parse_path(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        _is_match: Option<bool>,
    ) -> Vec<FileApplicabilityPath<I>> {
        let mut has_a_match = false;
        self.contents
            .iter()
            .flat_map(|c| match c {
                ApplicabilityExprKind::None(_applicability_expr_container) => vec![], //invalid condition
                ApplicabilityExprKind::Text(text) => {
                    vec![FileApplicabilityPath::Text(text.text.clone())]
                }
                ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                    applicability_expr_container_with_position.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        _is_match,
                    )
                }
                ApplicabilityExprKind::Tag(applicability_expr_tag) => {
                    let is_matches = applicability_expr_tag.match_applicability(
                        features,
                        config_name,
                        parent_group,
                        child_configurations,
                    ) && !has_a_match;
                    //if there is already a match, we want to make it excluded
                    //latch the has_a_match value so we don't flip-flop toggle it and instead only toggle it upon first true occurance
                    if !has_a_match {
                        has_a_match = is_matches;
                    }
                    applicability_expr_tag.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(is_matches),
                    )
                } //if this matches_applicability = wrap in included, else wrap in excluded
                ApplicabilityExprKind::TagNot(applicability_expr_tag) => {
                    let is_matches = !applicability_expr_tag.match_applicability(
                        features,
                        config_name,
                        parent_group,
                        child_configurations,
                    ) && !has_a_match;
                    //if there is already a match, we want to make it excluded

                    //latch the has_a_match value so we don't flip-flop toggle it and instead only toggle it upon first true occurance
                    if !has_a_match {
                        has_a_match = is_matches;
                    }
                    applicability_expr_tag.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(is_matches),
                    )
                } //if this matches_applicability = wrap in excluded, else wrap in included
                ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                    applicability_expr_substitution.parse_path(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        _is_match,
                    )
                }
            })
            .collect()
    }
}
impl<I> ParsePaths<I> for ApplicabilityExprSubstitution<I>
where
    I: Clone + PartialEq + Debug,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn parse_path(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        _is_match: Option<bool>,
    ) -> Vec<FileApplicabilityPath<I>> {
        //for right now discard substitution
        vec![]
    }
}
impl<I> ParsePaths<I> for ApplicabilityExprKind<I>
where
    I: Clone + PartialEq + Debug,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn parse_path(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        is_match: Option<bool>,
    ) -> Vec<FileApplicabilityPath<I>> {
        let mut has_a_match = false;
        match self {
            ApplicabilityExprKind::None(applicability_expr_container) => {
                applicability_expr_container.parse_path(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    is_match,
                )
            }
            ApplicabilityExprKind::Text(text) => {
                vec![FileApplicabilityPath::Text(text.text.clone())]
            }
            ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                applicability_expr_container_with_position.parse_path(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    is_match,
                )
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => {
                let is_matches = applicability_expr_tag.match_applicability(
                    features,
                    config_name,
                    parent_group,
                    child_configurations,
                ) && !has_a_match;
                //if there is already a match, we want to make it excluded
                //latch the has_a_match value so we don't flip-flop toggle it and instead only toggle it upon first true occurance
                if !has_a_match {
                    has_a_match = is_matches;
                }
                applicability_expr_tag.parse_path(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    Some(is_matches),
                )
            } //if this matches_applicability = wrap in included, else wrap in excluded
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => {
                let is_matches = !applicability_expr_tag.match_applicability(
                    features,
                    config_name,
                    parent_group,
                    child_configurations,
                ) && !has_a_match;
                //if there is already a match, we want to make it excluded

                //latch the has_a_match value so we don't flip-flop toggle it and instead only toggle it upon first true occurance
                if !has_a_match {
                    has_a_match = is_matches;
                }
                applicability_expr_tag.parse_path(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    Some(is_matches),
                )
            } //if this matches_applicability = wrap in excluded, else wrap in included
            ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                applicability_expr_substitution.parse_path(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    is_match,
                )
            }
        }
    }
}
// pub trait ParsePaths {
//     /// Turns featurized text into it's corresponding path
//     ///
//     /// After calling .parse_path, .into() should be used to turn into it's string, as the outer shell of the text is no longer useful.
//     fn parse_path(
//         &self,
//         features: Vec<ApplicabilityTag>,
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//     ) -> FileApplicabilityPath;

//     fn parse_path_else(
//         &self,
//         features: Vec<ApplicabilityTag>,
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//     ) -> FileApplicabilityPath;
// }

// impl ParsePaths for ApplicabilityParserSyntaxTag {
//     fn parse_path(
//         &self,
//         features: Vec<ApplicabilityTag>,
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//     ) -> FileApplicabilityPath {
//         match &self {
//             ApplicabilityParserSyntaxTag::Text(t) => FileApplicabilityPath::Text(t.clone()),
//             ApplicabilityParserSyntaxTag::Tag(t) => t.parse_path(
//                 features,
//                 config_name,
//                 substitutes,
//                 parent_group,
//                 child_configurations,
//             ),
//             ApplicabilityParserSyntaxTag::TagNot(t) => t.parse_path(
//                 features,
//                 config_name,
//                 substitutes,
//                 parent_group,
//                 child_configurations,
//             ),
//             ApplicabilityParserSyntaxTag::Substitution(t) => FileApplicabilityPath::Text(
//                 t.iter()
//                     .cloned()
//                     .map(|v| v.get_substitution_value(substitutes))
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//             ApplicabilityParserSyntaxTag::SubstitutionNot(t) => {
//                 FileApplicabilityPath::Text(t.clone())
//             }
//         }
//     }

//     fn parse_path_else(
//         &self,
//         features: Vec<ApplicabilityTag>,
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//     ) -> FileApplicabilityPath {
//         match &self {
//             ApplicabilityParserSyntaxTag::Text(t) => FileApplicabilityPath::Text(t.clone()),
//             ApplicabilityParserSyntaxTag::Tag(t) => t.parse_path_else(
//                 features,
//                 config_name,
//                 substitutes,
//                 parent_group,
//                 child_configurations,
//             ),
//             ApplicabilityParserSyntaxTag::TagNot(t) => t.parse_path_else(
//                 features,
//                 config_name,
//                 substitutes,
//                 parent_group,
//                 child_configurations,
//             ),
//             ApplicabilityParserSyntaxTag::Substitution(t) => FileApplicabilityPath::Text(
//                 t.iter()
//                     .cloned()
//                     .map(|v| v.get_substitution_value(substitutes))
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//             ApplicabilityParserSyntaxTag::SubstitutionNot(t) => {
//                 FileApplicabilityPath::Text(t.clone())
//             }
//         }
//     }
// }
// impl ParsePaths for ApplicabilitySyntaxTag {
//     fn parse_path(
//         &self,
//         features: Vec<ApplicabilityTag>,
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//     ) -> FileApplicabilityPath {
//         match self.match_applicability(&features, config_name, parent_group, child_configurations) {
//             true => FileApplicabilityPath::Included(
//                 self.1
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//                 self.3
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//             false => FileApplicabilityPath::Excluded(
//                 self.1
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//                 self.3
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//         }
//     }
//     fn parse_path_else(
//         &self,
//         features: Vec<ApplicabilityTag>,
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//     ) -> FileApplicabilityPath {
//         match self.match_applicability(&features, config_name, parent_group, child_configurations) {
//             true => FileApplicabilityPath::Excluded(
//                 self.3
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//                 self.1
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//             false => FileApplicabilityPath::Included(
//                 self.3
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//                 self.1
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//         }
//     }
// }

// impl ParsePaths for ApplicabilitySyntaxTagNot {
//     fn parse_path(
//         &self,
//         features: Vec<ApplicabilityTag>,
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//     ) -> FileApplicabilityPath {
//         match self.match_applicability(&features, config_name, parent_group, child_configurations) {
//             true => FileApplicabilityPath::Included(
//                 self.3
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//                 self.1
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//             false => FileApplicabilityPath::Excluded(
//                 self.3
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//                 self.1
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//         }
//     }

//     fn parse_path_else(
//         &self,
//         features: Vec<ApplicabilityTag>,
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//     ) -> FileApplicabilityPath {
//         match self.match_applicability(&features, config_name, parent_group, child_configurations) {
//             true => FileApplicabilityPath::Excluded(
//                 self.1
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//                 self.3
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//             false => FileApplicabilityPath::Included(
//                 self.1
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//                 self.3
//                     .iter()
//                     .cloned()
//                     .map(|syntax_tag| {
//                         String::from(syntax_tag.substitute(substitutes).parse_path(
//                             features.clone(),
//                             config_name,
//                             substitutes,
//                             parent_group,
//                             child_configurations,
//                         ))
//                     })
//                     .collect::<Vec<String>>()
//                     .join(""),
//             ),
//         }
//     }
// }
