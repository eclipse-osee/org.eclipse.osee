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
// use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
// use applicability_match::MatchApplicability;
// use applicability_parser_types::{
//     applic_tokens::GetSubstitutionValue,
//     applicability_parser_syntax_tag::{
//         ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag, ApplicabilitySyntaxTagNot,
//     },
// };
// use applicability_substitution::SubstituteApplicability;
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum FileApplicabilityPath {
    Included(String, String),
    Excluded(String, String),
    Text(String),
}

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
