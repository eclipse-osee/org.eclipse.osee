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

#[cfg(windows)]
const LINE_ENDING: &str = "\r\n";
#[cfg(not(windows))]
const LINE_ENDING: &str = "\n";

///
/// Trait for turning a tree of applicability-parsed text into it's resulting text.
///
/// Unlike [SanitizeApplicability](applicability_sanitization::SanitizeApplicability), this trait's functions will
/// return the text as a string, and with the same number of lines as the input source.
/// The start tag, else tag, end tag, and any non-matching applicabilities will be replaced
/// by a comment using the comment syntax specified.
pub trait SanitizeApplicabilityWithLinePreservation {
    /// Turns featurized text into it's corresponding text
    ///
    /// [substitute](applicability_substitution::SubstituteApplicability::substitute) should be called before sanitize_with_line_preservation
    /// as subsitution is not handled by the first level unnesting of applicability.
    ///
    /// # Arguments
    ///
    /// * `features` - The features that are valid for a given product line configuration.
    /// * `config_name` - Name of the product line configuration/configuration group that the document is being processed for.
    /// * `substitutes` - The substitutions that are valid for a given product line configuration.
    /// * `parent_group` - Optional configuration group that has ownership over the current product line configuration.
    ///   By default, most product line configurations do not belong to a group.
    ///   A Configuration Group should not have a parent_group.
    /// * `child_configurations` - The configurations that belong to this configuration group.
    ///   Configurations should not have this defined.
    /// * `custom_start_comment_syntax` - Syntax that should be used to start a comment in the desired file format.
    /// * `custom_end_comment_syntax` - Syntax that should be used to end a comment in the desired file format.
    ///
    ///
    /// # Examples
    /// ``` rust
    ///
    /// #[cfg(windows)]
    /// const LINE_ENDING: &str = "\r\n";
    /// #[cfg(not(windows))]
    /// const LINE_ENDING: &str = "\n";
    ///
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilitySyntaxTag;
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Text;
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Tag;
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::TagNot;
    /// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilitySyntaxTagNot;
    /// use applicability_parser_types::applicability_parser_syntax_tag::LineEnding;
    /// use applicability_parser_types::applic_tokens::{ApplicabilityNoTag,ApplicabilityNotTag,ApplicTokens};
    /// use applicability::applic_tag::ApplicabilityTagTypes::Feature;
    /// use applicability::applic_tag::ApplicabilityTagTypes::Configuration;
    /// use applicability_sanitization_preserve_line_numbers::SanitizeApplicabilityWithLinePreservation;
    /// use applicability_substitution::SubstituteApplicability;
    /// use applicability::applic_tag::ApplicabilityTag;
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "Engine 5 A2543".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"Engine 5 A2543````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"Engine 5 A2543````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     5,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING+"Engine 5 A2543````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "Product A".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"Product A````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"Product A````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// LINE_ENDING.to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     3,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// LINE_ENDING.to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     3,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "Engine 5 B5543".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"Engine 5 B5543````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"Engine 5 B5543````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     1,
    ///     1,
    ///     3
    /// ).sanitize_with_line_preservation(
    /// vec![],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"Engine 5 B5543````"+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "No Configuration".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"No Configuration````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"No Configuration````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             0,
    ///             0,
    ///             0
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "No JHU Controller".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             0,
    ///             0,
    ///             0
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"No JHU Controller````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             0,
    ///             0,
    ///             0
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"No JHU Controller````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTag(
    ///     vec![
    ///         ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Tag(ApplicabilitySyntaxTag(
    ///             vec![
    ///                 ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             1,
    ///             1,
    ///             1
    ///             ))
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"No JHU Controller````"+LINE_ENDING+"````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"````````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "Engine 5 A2543".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"Engine 5 A2543````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "Product A".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"Product A````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "Engine 5 B5543".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"Engine 5 B5543"+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("Engine 5 B5543".to_string())
    ///         ],
    ///     Feature,
    ///     vec![
    ///         Text("Engine 5 A2543".to_string())
    ///         ],
    ///     1,
    ///     1,
    ///     1
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"B5543".to_string()
    ///             }
    /// ],
    /// "PRODUCT_A",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"Engine 5 B5543"+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"PRODUCT_A".to_string(),
    ///             value:"Included".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         Text("No Configuration".to_string())
    ///         ],
    ///     Configuration,
    ///     vec![
    ///         Text("Product A".to_string())
    ///         ],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
    /// vec![
    ///     ApplicabilityTag{
    ///         tag:"ENGINE_5".to_string(),
    ///         value:"A2543".to_string()
    ///         }
    ///     ],
    /// "",
    /// vec![].as_slice(),
    /// None,
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "No Configuration".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Included".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             0,
    ///             0,
    ///             0
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "No JHU Controller".to_string());
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Included".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             1,
    ///             0,
    ///             1
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"No JHU Controller````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Included".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             0,
    ///             0,
    ///             0
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"No JHU Controller````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Included".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![],
    ///             1,
    ///             0,
    ///             1
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     1,
    ///     0,
    ///     1
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "````".to_string()+LINE_ENDING+"````"+LINE_ENDING+"No JHU Controller````"+LINE_ENDING+"````"+LINE_ENDING);
    ///
    /// assert_eq!(
    /// ApplicabilitySyntaxTagNot(
    ///     vec![
    ///         ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///             tag:"ENGINE_5".to_string(),
    ///             value:"A2543".to_string()
    ///             },Some(0)))
    ///         ],
    ///     vec![
    ///         TagNot(ApplicabilitySyntaxTagNot(
    ///             vec![
    ///                 ApplicTokens::Not(ApplicabilityNotTag(ApplicabilityTag{
    ///                     tag:"JHU_CONTROLLER".to_string(),
    ///                     value:"Excluded".to_string()
    ///                     },Some(0)))
    ///                 ],
    ///             vec![
    ///                 Text("No JHU Controller".to_string())
    ///                 ],
    ///             Feature,
    ///             vec![
    ///                 Text("JHU Controller".to_string())
    ///                 ],
    ///             0,
    ///             0,
    ///             0
    ///             ))
    ///         ],
    ///     Feature,
    ///     vec![],
    ///     0,
    ///     0,
    ///     0
    /// ).sanitize_with_line_preservation(
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
    /// None,
    /// "``",
    /// "``"
    /// ),
    /// "JHU Controller".to_string());
    /// ```
    #[allow(clippy::too_many_arguments)]
    fn sanitize_with_line_preservation<'a>(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        custom_start_comment_syntax: &'a str,
        custom_end_comment_syntax: &'a str,
    ) -> String;
}
impl SanitizeApplicabilityWithLinePreservation for ApplicabilityParserSyntaxTag {
    fn sanitize_with_line_preservation<'a>(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        custom_start_comment_syntax: &'a str,
        custom_end_comment_syntax: &'a str,
    ) -> String {
        match &self {
            ApplicabilityParserSyntaxTag::Text(t) => t.clone(),
            ApplicabilityParserSyntaxTag::Tag(t) => t.clone().sanitize_with_line_preservation(
                features,
                config_name,
                substitutes,
                parent_group,
                child_configurations,
                custom_start_comment_syntax,
                custom_end_comment_syntax,
            ),
            ApplicabilityParserSyntaxTag::TagNot(t) => t.clone().sanitize_with_line_preservation(
                features,
                config_name,
                substitutes,
                parent_group,
                child_configurations,
                custom_start_comment_syntax,
                custom_end_comment_syntax,
            ),
            //Note: Originally these 3 would do a panic!("Content did not get fully substituted."),
            //but it is probably a more scalable solution to just accept substituted tags that don't exist as their text content in order to be more compatible as a code parser.
            ApplicabilityParserSyntaxTag::Substitution(t) => t
                .iter()
                .cloned()
                .map(|tag| tag.into())
                .collect::<Vec<String>>()
                .join(""),

            //this one is left intentionally unimplemented, future growth if needed, these paths don't exist yet.
            ApplicabilityParserSyntaxTag::SubstitutionNot(_) => todo!(),
        }
    }
}

impl SanitizeApplicabilityWithLinePreservation for ApplicabilitySyntaxTag {
    fn sanitize_with_line_preservation<'a>(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        custom_start_comment_syntax: &'a str,
        custom_end_comment_syntax: &'a str,
    ) -> String {
        let start_line_number = self.4;
        let comment =
            custom_start_comment_syntax.to_owned() + custom_end_comment_syntax + LINE_ENDING;
        let start_comment = comment.repeat(start_line_number.into());
        let else_line_number = self.5;
        let else_comment = comment.repeat(else_line_number.into());
        let end_line_number = self.6;
        let end_comment = comment.repeat(end_line_number.into());
        let comment_with_no_ln = custom_start_comment_syntax.to_owned() + custom_end_comment_syntax;
        let true_value = self
            .1
            .iter()
            .cloned()
            .map(|syntax_tag| {
                syntax_tag
                    .substitute(substitutes)
                    .sanitize_with_line_preservation(
                        features.clone(),
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        custom_start_comment_syntax,
                        custom_end_comment_syntax,
                    )
            })
            .collect::<Vec<String>>()
            .join("");
        let else_value = self
            .3
            .iter()
            .cloned()
            .map(|syntax_tag| {
                syntax_tag
                    .substitute(substitutes)
                    .sanitize_with_line_preservation(
                        features.clone(),
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        custom_start_comment_syntax,
                        custom_end_comment_syntax,
                    )
            })
            .collect::<Vec<String>>()
            .join("");
        let true_value_size = true_value.lines().count();
        let true_comment = match (
            true_value_size,
            true_value.contains(LINE_ENDING),
            start_line_number,
        ) {
            (1, false, _) => "".to_string(),
            _ => comment.repeat(true_value_size),
        };
        let else_value_size = else_value.lines().count();
        let else_value_comment = match (
            else_value_size,
            else_value.contains(LINE_ENDING),
            else_line_number,
        ) {
            (1, false, 0) => "".to_string(),
            (1, false, _) => comment_with_no_ln.repeat(else_value_size),
            _ => comment.repeat(else_value_size),
        };
        let resulting_value = match self.match_applicability(
            &features,
            config_name,
            parent_group,
            child_configurations,
        ) {
            true => {
                start_comment
                    + true_value.as_str()
                    + else_comment.as_str()
                    + else_value_comment.as_str()
                    + end_comment.as_str()
            }
            false => {
                start_comment
                    + true_comment.as_str()
                    + else_comment.as_str()
                    + else_value.as_str()
                    + end_comment.as_str()
            }
        };
        match resulting_value.len() {
            0 => LINE_ENDING.to_string(),
            _ => resulting_value,
        }
    }
}

impl SanitizeApplicabilityWithLinePreservation for ApplicabilitySyntaxTagNot {
    fn sanitize_with_line_preservation<'a>(
        &self,
        features: Vec<ApplicabilityTag>,
        config_name: &str,
        substitutes: &[Substitution],
        parent_group: Option<&str>,
        child_configurations: Option<&[&str]>,
        custom_start_comment_syntax: &'a str,
        custom_end_comment_syntax: &'a str,
    ) -> String {
        let start_line_number = self.4;
        let comment =
            custom_start_comment_syntax.to_owned() + custom_end_comment_syntax + LINE_ENDING;
        let start_comment = comment.repeat(start_line_number.into());
        let else_line_number = self.5;
        let else_comment = comment.repeat(else_line_number.into());
        let end_line_number = self.6;
        let end_comment = comment.repeat(end_line_number.into());
        let comment_with_no_ln = custom_start_comment_syntax.to_owned() + custom_end_comment_syntax;
        let true_value = self
            .3
            .iter()
            .cloned()
            .map(|syntax_tag| {
                syntax_tag
                    .substitute(substitutes)
                    .sanitize_with_line_preservation(
                        features.clone(),
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        custom_start_comment_syntax,
                        custom_end_comment_syntax,
                    )
            })
            .collect::<Vec<String>>()
            .join("");
        let else_value = self
            .1
            .iter()
            .cloned()
            .map(|syntax_tag| {
                syntax_tag
                    .substitute(substitutes)
                    .sanitize_with_line_preservation(
                        features.clone(),
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        custom_start_comment_syntax,
                        custom_end_comment_syntax,
                    )
            })
            .collect::<Vec<String>>()
            .join("");
        let true_value_size = true_value.lines().count();
        let true_comment = match (
            true_value_size,
            true_value.contains(LINE_ENDING),
            start_line_number,
        ) {
            (1, false, _) => "".to_string(),
            _ => comment.repeat(true_value_size),
        };
        let else_value_size = else_value.lines().count();
        let else_value_comment = match (
            else_value_size,
            else_value.contains(LINE_ENDING),
            else_line_number,
        ) {
            (1, false, 0) => "".to_string(),
            (1, false, _) => comment_with_no_ln.repeat(else_value_size),
            _ => comment.repeat(else_value_size),
        };
        let resulting_value = match self.match_applicability(
            &features,
            config_name,
            parent_group,
            child_configurations,
        ) {
            true => {
                start_comment
                    + true_value.as_str()
                    + else_comment.as_str()
                    + else_value_comment.as_str()
                    + end_comment.as_str()
            }
            false => {
                start_comment
                    + true_comment.as_str()
                    + else_comment.as_str()
                    + else_value.as_str()
                    + end_comment.as_str()
            }
        };
        match resulting_value.len() {
            0 => LINE_ENDING.to_string(),
            _ => resulting_value,
        }
    }
}
