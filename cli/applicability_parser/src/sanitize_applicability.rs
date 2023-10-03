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
        ApplicabilitySyntaxTag, ApplicabilitySyntaxTagAnd, ApplicabilitySyntaxTagNot,
        ApplicabilitySyntaxTagNotAnd, ApplicabilitySyntaxTagNotOr, ApplicabilitySyntaxTagOr,
    },
    match_applicability::MatchApplicability,
    substitute_applicability::{SubstituteApplicability, Substitution},
    ApplicabilityParserSyntaxTag,
};
use applicability::applic_tag::ApplicabilityTag;

///
/// Trait for turning a tree of applicability-parsed text into it's resulting text
pub trait SanitizeApplicability {
    /// Turns featurized text into it's corresponding text
    ///
    /// After calling .sanitize(), .into() should be used to turn into it's string, as the outer shell of the text is no longer useful.
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
    ) -> ApplicabilityParserSyntaxTag;
}
impl SanitizeApplicability for ApplicabilityParserSyntaxTag {
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
    ) -> ApplicabilityParserSyntaxTag {
        match &self {
            ApplicabilityParserSyntaxTag::Text(t) => ApplicabilityParserSyntaxTag::Text(t.clone()),
            ApplicabilityParserSyntaxTag::Tag(t) => t.sanitize(features, config_name, substitutes),
            ApplicabilityParserSyntaxTag::TagAnd(t) => {
                t.sanitize(features, config_name, substitutes)
            }
            ApplicabilityParserSyntaxTag::TagOr(t) => {
                t.sanitize(features, config_name, substitutes)
            }
            ApplicabilityParserSyntaxTag::TagNot(t) => {
                t.sanitize(features, config_name, substitutes)
            }
            ApplicabilityParserSyntaxTag::TagNotAnd(t) => {
                t.sanitize(features, config_name, substitutes)
            }
            ApplicabilityParserSyntaxTag::TagNotOr(t) => {
                t.sanitize(features, config_name, substitutes)
            }
            //Note: Originally these 3 would do a panic!("Content did not get fully substituted."),
            //but it is probably a more scalable solution to just accept substituted tags that don't exist as their text content in order to be more compatible as a code parser.
            ApplicabilityParserSyntaxTag::Substitution(t) => {
                ApplicabilityParserSyntaxTag::Text(t.to_owned())
            }
            ApplicabilityParserSyntaxTag::SubstitutionOr(t) => {
                ApplicabilityParserSyntaxTag::Text(t.0.join(""))
            }
            ApplicabilityParserSyntaxTag::SubstitutionAnd(t) => {
                ApplicabilityParserSyntaxTag::Text(t.0.join(""))
            }
            //these 3 left intentionally unimplemented, future growth if needed, these paths don't exist yet.
            ApplicabilityParserSyntaxTag::SubstitutionNot(_) => todo!(),
            ApplicabilityParserSyntaxTag::SubstitutionNotAnd(_) => todo!(),
            ApplicabilityParserSyntaxTag::SubstitutionNotOr(_) => todo!(),
        }
    }
}

impl SanitizeApplicability for ApplicabilitySyntaxTag {
    ///
    /// Sanitizes a standard ApplicabilityTag
    ///
    /// # Examples
    /// ``` rust
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilitySyntaxTag;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Feature;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Configuration;
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Text;
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Tag;
    /// use applicability_parser::sanitize_applicability::SanitizeApplicability;
    /// use applicability_parser::substitute_applicability::Substitution;
    /// use applicability::applic_tag::ApplicabilityTag;
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 A2543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("Product A".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ]
    /// ).sanitize(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 B5543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("No Configuration".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![
    ///                 Text("JHU Controller".to_string())
    ///                 ]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"INCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("JHU Controller".to_string()));
    /// ```
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
    ) -> ApplicabilityParserSyntaxTag {
        match self.match_applicability(&features, config_name) {
            true => ApplicabilityParserSyntaxTag::Text(
                self.1
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
            false => ApplicabilityParserSyntaxTag::Text(
                self.3
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
        }
    }
}

impl SanitizeApplicability for ApplicabilitySyntaxTagAnd {
    ///
    /// Sanitizes an or'd ApplicabilityTag
    ///
    /// # Examples
    /// ``` rust
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilitySyntaxTagAnd;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Feature;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Configuration;
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Text;
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::TagAnd;
    /// use applicability_parser::sanitize_applicability::SanitizeApplicability;
    /// use applicability_parser::substitute_applicability::Substitution;
    /// use applicability::applic_tag::ApplicabilityTag;
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"EXCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"EXCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },
    ///     ApplicabilityTag{
    ///         tag:"ROBOT_ARM_LIGHT".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 A2543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"EXCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ROBOT_ARM_LIGHT".to_string(),
    ///         value:"INCLUDED".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"EXCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"ROBOT_ARM_LIGHT".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("Product A".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ]
    /// ).sanitize(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 B5543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("No Configuration".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagAnd(ApplicabilitySyntaxTagAnd(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagAnd(ApplicabilitySyntaxTagAnd(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagAnd(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagAnd(ApplicabilitySyntaxTagAnd(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![
    ///                 Text("JHU Controller".to_string())
    ///                 ]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"INCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("JHU Controller".to_string()));
    /// ```
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
    ) -> ApplicabilityParserSyntaxTag {
        match self.match_applicability(&features, config_name) {
            true => ApplicabilityParserSyntaxTag::Text(
                self.1
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
            false => ApplicabilityParserSyntaxTag::Text(
                self.3
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
        }
    }
}
impl SanitizeApplicability for ApplicabilitySyntaxTagOr {
    ///
    /// Sanitizes an or'd ApplicabilityTag
    ///
    /// # Examples
    /// ``` rust
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilitySyntaxTagOr;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Feature;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Configuration;
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Text;
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::TagOr;
    /// use applicability_parser::sanitize_applicability::SanitizeApplicability;
    /// use applicability_parser::substitute_applicability::Substitution;
    /// use applicability::applic_tag::ApplicabilityTag;
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"EXCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 A2543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"EXCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ROBOT_ARM_LIGHT".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 A2543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("Product A".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"EXCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"ROBOT_ARM_LIGHT".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("Product A".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ]
    /// ).sanitize(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 B5543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("No Configuration".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagOr(ApplicabilitySyntaxTagOr(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagOr(ApplicabilitySyntaxTagOr(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagOr(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagOr(ApplicabilitySyntaxTagOr(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![
    ///                 Text("JHU Controller".to_string())
    ///                 ]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"INCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("JHU Controller".to_string()));
    /// ```
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
    ) -> ApplicabilityParserSyntaxTag {
        match self.match_applicability(&features, config_name) {
            true => ApplicabilityParserSyntaxTag::Text(
                self.1
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
            false => ApplicabilityParserSyntaxTag::Text(
                self.3
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
        }
    }
}

impl SanitizeApplicability for ApplicabilitySyntaxTagNot {
    ///
    /// Sanitizes a not'd ApplicabilityTag
    ///
    /// # Examples
    /// ``` rust
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilitySyntaxTagNot;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Feature;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Configuration;
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Text;
    /// use applicability_parser::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::TagNot;
    /// use applicability_parser::sanitize_applicability::SanitizeApplicability;
    /// use applicability_parser::substitute_applicability::Substitution;
    /// use applicability::applic_tag::ApplicabilityTag;
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 A2543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("Product A".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice()
    /// ),
    /// Text("Engine 5 B5543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("No Configuration".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"INCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"B5543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"INCLUDED".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"INCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_B",
    /// vec![].as_slice()
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"EXCLUDED".to_string()
    ///                     }
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![
    ///                 Text("JHU Controller".to_string())
    ///                 ]
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![]
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"B5543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"EXCLUDED".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice()
    /// ),
    /// Text("JHU Controller".to_string()));
    /// ```
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
    ) -> ApplicabilityParserSyntaxTag {
        match self.match_applicability(&features, config_name) {
            true => ApplicabilityParserSyntaxTag::Text(
                self.3
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
            false => ApplicabilityParserSyntaxTag::Text(
                self.1
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
        }
    }
}

impl SanitizeApplicability for ApplicabilitySyntaxTagNotAnd {
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
    ) -> ApplicabilityParserSyntaxTag {
        match self.match_applicability(&features, config_name) {
            true => ApplicabilityParserSyntaxTag::Text(
                self.3
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
            false => ApplicabilityParserSyntaxTag::Text(
                self.1
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
        }
    }
}

impl SanitizeApplicability for ApplicabilitySyntaxTagNotOr {
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
    ) -> ApplicabilityParserSyntaxTag {
        //if either of the features is found, use opposite
        match self.match_applicability(&features, config_name) {
            true => ApplicabilityParserSyntaxTag::Text(
                self.3
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
            false => ApplicabilityParserSyntaxTag::Text(
                self.1
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(features.clone(), config_name, substitutes)
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
        }
    }
}
