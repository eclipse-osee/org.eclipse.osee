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
use applicability::{applic_tag::ApplicabilityTag, substitution::Substitution};
use applicability_match::MatchApplicability;
use applicability_parser_types::applicability_parser_syntax_tag::{
    ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag, ApplicabilitySyntaxTagNot,
};
use applicability_substitution::SubstituteApplicability;

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
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
    ) -> ApplicabilityParserSyntaxTag;
}
impl SanitizeApplicability for ApplicabilityParserSyntaxTag {
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
    ) -> ApplicabilityParserSyntaxTag {
        match &self {
            ApplicabilityParserSyntaxTag::Text(t) => ApplicabilityParserSyntaxTag::Text(t.clone()),
            ApplicabilityParserSyntaxTag::Tag(t) => t.clone().sanitize(
                features,
                config_name,
                substitutes,
                parent_group,
                child_configurations,
            ),
            ApplicabilityParserSyntaxTag::TagNot(t) => t.clone().sanitize(
                features,
                config_name,
                substitutes,
                parent_group,
                child_configurations,
            ),
            //Note: Originally these 3 would do a panic!("Content did not get fully substituted."),
            //but it is probably a more scalable solution to just accept substituted tags that don't exist as their text content in order to be more compatible as a code parser.
            ApplicabilityParserSyntaxTag::Substitution(t) => ApplicabilityParserSyntaxTag::Text(
                t.iter()
                    .cloned()
                    .map(|tag| tag.into())
                    .collect::<Vec<String>>()
                    .join(""),
            ),
            //this one is left intentionally unimplemented, future growth if needed, these paths don't exist yet.
            ApplicabilityParserSyntaxTag::SubstitutionNot(_) => todo!(),
        }
    }
}

impl SanitizeApplicability for ApplicabilitySyntaxTag {
    ///
    /// Sanitizes a standard ApplicabilityTag
    ///
    /// # Examples
    /// ``` rust
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilitySyntaxTag;
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Text;
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Tag;
    /// use applicability_parser_types::applicability_parser_syntax_tag::LineEnding;
    /// use applicability_parser_types::applic_tokens::{ApplicabilityNoTag,ApplicTokens};
    /// use applicability::applic_tag::ApplicabilityTagTypes::Feature;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Configuration;
    /// use applicability_sanitization::SanitizeApplicability;
    /// use applicability_substitution::SubstituteApplicability;
    /// use applicability::applic_tag::ApplicabilityTag;
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("Engine 5 A2543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("Product A".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("Engine 5 B5543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("No Configuration".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     }))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///     LineEnding::NoLineEndings
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"Excluded".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     }))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///     LineEnding::NoLineEndings
    ///             ))
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///    ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"Excluded".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     }))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![
    ///                 Text("JHU Controller".to_string())
    ///                 ],
    ///             LineEnding::NoLineEndings
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"Included".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("JHU Controller".to_string()));
    /// ```
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
    ) -> ApplicabilityParserSyntaxTag {
        match self.match_applicability(&features, config_name, parent_group, child_configurations) {
            true => ApplicabilityParserSyntaxTag::Text(
                self.1
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(
                                features.clone(),
                                config_name,
                                substitutes,
                                parent_group,
                                child_configurations,
                            )
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
                            .sanitize(
                                features.clone(),
                                config_name,
                                substitutes,
                                parent_group,
                                child_configurations,
                            )
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
    /// use applicability_parser_types::applicability_parser_syntax_tag::{ApplicabilitySyntaxTagNot,ApplicabilitySyntaxTag};
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Text;
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::TagNot;
    /// use applicability_parser_types::applic_tokens::{ApplicabilityNotTag,ApplicabilityNoTag,ApplicTokens};
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Tag;
    /// use applicability_parser_types::applicability_parser_syntax_tag::LineEnding;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Feature;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Configuration;
    /// use applicability::applic_tag::ApplicabilityTag;
    /// use applicability_sanitization::SanitizeApplicability;
    /// use applicability_substitution::SubstituteApplicability;
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("Engine 5 A2543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("Product A".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("Engine 5 B5543".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("No Configuration".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Included".to_string()
    ///                     }))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             LineEnding::NoLineEndings
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"B5543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"Excluded".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Included".to_string()
    ///                     }))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             LineEnding::NoLineEndings
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"Excluded".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_B",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("No JHU Controller".to_string()));
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             }))
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     }))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![
    ///                 Text("JHU Controller".to_string())
    ///                 ],
    ///             LineEnding::NoLineEndings
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     LineEnding::NoLineEndings
    /// ).sanitize(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"B5543".to_string()
    ///         },
    ///     ApplicabilityTag{
    ///         tag:"JHU_CONTROLLER".to_string(),
    ///         value:"Excluded".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None
    /// ),
    /// Text("JHU Controller".to_string()));
    ///
    /// assert_eq!(
    ///     ApplicabilitySyntaxTagNot(
    ///         vec![
    ///             ApplicTokens::NoTag(
    ///                 ApplicabilityNoTag(
    ///                     ApplicabilityTag{
    ///                         tag: "PRODUCT_C".to_string(),
    ///                         value: "Included".to_string()
    ///                     }
    ///                 ),
    ///             ),
    ///         ],
    ///         vec![
    ///             Text("Vivamus congue ornare ipsum quiz vehicula.\r\n".to_string()),
    ///             Tag(
    ///                 ApplicabilitySyntaxTag(
    ///                     vec![
    ///                         ApplicTokens::NoTag(
    ///                             ApplicabilityNoTag(
    ///                                 ApplicabilityTag{
    ///                                     tag:"ROBOT_ARM_LIGHT".to_string(),
    ///                                     value:"EXCLUDED".to_string()
    ///                                 }
    ///                             ),
    ///                         ),
    ///                     ],
    ///                     vec![
    ///                         Text("Ut non mauris et nisi bibendum iaculis porta eu ipsum.\r\n".to_string())
    ///                     ],
    ///                     Feature,
    ///                     vec![],
    ///                     LineEnding::NoLineEndings
    ///                 ),
    ///             ),
    ///         ],
    ///         Configuration,
    ///         vec![
    ///             Text("\r\nMauris ut tortor id ipsum pharetra ultrices. \r\n".to_string())
    ///         ],
    ///         LineEnding::NoLineEndings
    ///         
    ///     ).sanitize(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"JHU_CONTROLLER".to_string(),
    ///             value:"Excluded".to_string()
    ///             }
    ///         ],
    ///     "PRODUCT_A",
    ///     vec![].as_slice(),
    ///     None,
    ///     None
    /// ),
    /// Text("\r\nMauris ut tortor id ipsum pharetra ultrices. \r\n".to_string()));
    ///
    /// assert_eq!(
    ///     ApplicabilitySyntaxTagNot(
    ///         vec![
    ///             ApplicTokens::NoTag(
    ///                 ApplicabilityNoTag(
    ///                     ApplicabilityTag{
    ///                         tag: "PRODUCT_C".to_string(),
    ///                         value: "Included".to_string()
    ///                     }
    ///                 ),
    ///             ),
    ///         ],
    ///         vec![
    ///             Text("Vivamus congue ornare ipsum quiz vehicula.\r\n".to_string()),
    ///             Tag(
    ///                 ApplicabilitySyntaxTag(
    ///                     vec![
    ///                         ApplicTokens::NoTag(
    ///                             ApplicabilityNoTag(
    ///                                 ApplicabilityTag{
    ///                                     tag:"ROBOT_ARM_LIGHT".to_string(),
    ///                                     value:"EXCLUDED".to_string()
    ///                                 }
    ///                             ),
    ///                         ),
    ///                     ],
    ///                     vec![
    ///                         Text("Ut non mauris et nisi bibendum iaculis porta eu ipsum.\r\n".to_string())
    ///                     ],
    ///                     Feature,
    ///                     vec![],
    ///                     LineEnding::NoLineEndings
    ///                 ),
    ///             ),
    ///         ],
    ///         Configuration,
    ///         vec![
    ///             Text("\r\nMauris ut tortor id ipsum pharetra ultrices. \r\n".to_string())
    ///         ],
    ///         LineEnding::NoLineEndings
    ///         
    ///     ).sanitize(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"JHU_CONTROLLER".to_string(),
    ///             value:"Excluded".to_string()
    ///             }
    ///         ],
    ///     "PRODUCT_C",
    ///     vec![].as_slice(),
    ///     None,
    ///     None
    /// ),
    /// Text("Vivamus congue ornare ipsum quiz vehicula.\r\n".to_string()));
    ///
    /// assert_eq!(
    ///     ApplicabilitySyntaxTagNot(
    ///         vec![
    ///             ApplicTokens::NoTag(
    ///                 ApplicabilityNoTag(
    ///                     ApplicabilityTag{
    ///                         tag: "PRODUCT_C".to_string(),
    ///                         value: "Included".to_string()
    ///                     }
    ///                 ),
    ///             ),
    ///         ],
    ///         vec![
    ///             Text("Vivamus congue ornare ipsum quiz vehicula.\r\n".to_string()),
    ///             Tag(
    ///                 ApplicabilitySyntaxTag(
    ///                     vec![
    ///                         ApplicTokens::NoTag(
    ///                             ApplicabilityNoTag(
    ///                                 ApplicabilityTag{
    ///                                     tag:"ROBOT_ARM_LIGHT".to_string(),
    ///                                     value:"EXCLUDED".to_string()
    ///                                 }
    ///                             ),
    ///                         ),
    ///                     ],
    ///                     vec![
    ///                         Text("Ut non mauris et nisi bibendum iaculis porta eu ipsum.\r\n".to_string())
    ///                     ],
    ///                     Feature,
    ///                     vec![],
    ///                     LineEnding::NoLineEndings
    ///                 ),
    ///             ),
    ///         ],
    ///         Configuration,
    ///         vec![
    ///             Text("\r\nMauris ut tortor id ipsum pharetra ultrices. \r\n".to_string())
    ///         ],
    ///         LineEnding::NoLineEndings
    ///         
    ///     ).sanitize(
    ///     vec![
    ///         ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"JHU_CONTROLLER".to_string(),
    ///             value:"Excluded".to_string()
    ///             },
    ///         ApplicabilityTag{
    ///             tag:"ROBOT_ARM_LIGHT".to_string(),
    ///             value:"EXCLUDED".to_string()
    ///             }
    ///         ],
    ///     "PRODUCT_C",
    ///     vec![].as_slice(),
    ///     None,
    ///     None
    /// ),
    /// Text("Vivamus congue ornare ipsum quiz vehicula.\r\nUt non mauris et nisi bibendum iaculis porta eu ipsum.\r\n".to_string()));
    /// ```
    fn sanitize(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
    ) -> ApplicabilityParserSyntaxTag {
        match self.match_applicability(&features, config_name, parent_group, child_configurations) {
            true => ApplicabilityParserSyntaxTag::Text(
                self.3
                    .iter()
                    .cloned()
                    .map(|syntax_tag| {
                        syntax_tag
                            .substitute(substitutes)
                            .sanitize(
                                features.clone(),
                                config_name,
                                substitutes,
                                parent_group,
                                child_configurations,
                            )
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
                            .sanitize(
                                features.clone(),
                                config_name,
                                substitutes,
                                parent_group,
                                child_configurations,
                            )
                            .into()
                    })
                    .collect::<Vec<String>>()
                    .join(""),
            ),
        }
    }
}
