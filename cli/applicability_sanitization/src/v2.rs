/*********************************************************************
 * Copyright (c) 2025 Boeing
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
use std::fmt::Debug;

use applicability::{
    applic_tag::{ApplicabilityTag, ApplicabilityTagTypes},
    substitution::Substitution,
};
use applicability_match::{MatchApplicability, MatchApplicabilityError};
use applicability_parser_types::applic_tokens::{ApplicTokens, GetApplicabilityTag, MatchToken};
use applicability_tokens_to_ast::tree::{
    ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition, ApplicabilityExprKind,
    ApplicabilityExprSubstitution, ApplicabilityExprTag,
};
use feature_definition::FeatureDefinition;
use thiserror::Error;

#[derive(Debug, Clone)]
pub struct SanitizeApplicabilityExternalError<X> {
    pub errors: Vec<SanitizeApplicabilityInternalError<X>>,
}
#[derive(Debug, Error, Clone, PartialEq)]
pub enum SanitizeApplicabilityInternalError<X> {
    #[error("None")]
    None,
    #[error("Invalid Data Condition")]
    InvalidDataCondition,
    #[error("Invalid Iterator Condition")]
    InvalidIteratorCondition,
    #[error("Missing Applicability")]
    MissingApplicability,
    #[error("Missing Substitution: {0} on line: {1} at column {2}")]
    MissingSubstitution(X, u32, usize),
    #[error("No Tag Found")]
    NoTagFound,
    #[error("Feature Tag does not exist in the PLE Model: {0} on line: {1} at column {2}")]
    FeatureTagDoesNotExist(X, u32, usize),
    #[error(
        "Feature Value does not exist does not exist in the PLE Model: {0} on line: {1} at column {2}"
    )]
    FeatureValueDoesNotExist(String, u32, usize),
}
pub enum SanitizeApplicabilityError {
    None,
    InvalidCondition,
    MissingApplicability,
    MissingSubstitution,
    NoTagFound,
}

impl<I1> From<MatchApplicabilityError<I1>> for SanitizeApplicabilityInternalError<I1> {
    fn from(value: MatchApplicabilityError<I1>) -> Self {
        match value {
            MatchApplicabilityError::FeatureTagDoesNotExist(i) => {
                Self::FeatureTagDoesNotExist(i, 0, 0)
            }
            MatchApplicabilityError::FeatureValueDoesNotExist(i) => {
                Self::FeatureValueDoesNotExist(i, 0, 0)
            }
        }
    }
}
impl<I1> SanitizeApplicabilityInternalError<I1> {
    fn from_match_applicability_error(
        value: MatchApplicabilityError<I1>,
        line: u32,
        column: usize,
    ) -> Self {
        match value {
            MatchApplicabilityError::FeatureTagDoesNotExist(i) => {
                Self::FeatureTagDoesNotExist(i, line, column)
            }
            MatchApplicabilityError::FeatureValueDoesNotExist(i) => {
                Self::FeatureValueDoesNotExist(i, line, column)
            }
        }
    }
}
pub trait SanitizeApplicabilityV2<X1> {
    #[allow(clippy::too_many_arguments)]
    fn sanitize(
        &self,
        features: &[ApplicabilityTag<X1>],
        config_name: &X1,
        substitutes: &[Substitution<X1, X1>],
        parent_group: Option<&X1>,
        child_configurations: Option<&[X1]>,
        is_inverted: Option<bool>,
        ple_model: &[FeatureDefinition<X1>],
    ) -> Result<X1, SanitizeApplicabilityExternalError<X1>>;
}

impl<I: Send + Sync> SanitizeApplicabilityV2<I> for ApplicabilityExprContainerWithPosition<I>
where
    I: PartialEq + Debug + Default + Extend<I> + Clone,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn sanitize(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        is_inverted: Option<bool>,
        ple_model: &[FeatureDefinition<I>],
    ) -> Result<I, SanitizeApplicabilityExternalError<I>> {
        //find the first tag that evaluates to true
        let validity_map = self.contents.iter().map(|tag| {
            (
                tag,
                match tag {
                    ApplicabilityExprKind::None(_applicability_expr_container) => Ok(false), //should never hit
                    ApplicabilityExprKind::Text(_text) => Ok(false), //should never hit
                    ApplicabilityExprKind::TagContainer(
                        _applicability_expr_container_with_position,
                    ) => {
                        Ok(false) //should never come here? 
                    }
                    ApplicabilityExprKind::Tag(applicability_expr_tag) => applicability_expr_tag
                        .match_applicability(
                            features,
                            config_name,
                            parent_group,
                            child_configurations,
                            ple_model,
                        ),
                    ApplicabilityExprKind::TagNot(applicability_expr_tag) => applicability_expr_tag
                        .match_applicability(
                            features,
                            config_name,
                            parent_group,
                            child_configurations,
                            ple_model,
                        )
                        .map(|x| !x),
                    ApplicabilityExprKind::Substitution(_applicability_expr_substitution) => {
                        Ok(false)
                    }
                },
            )
        });
        let (successful_results, failed_results): (Vec<_>, Vec<_>) =
            validity_map.partition(|(_, result)| result.is_ok());
        if !failed_results.is_empty() {
            let unmapped_results = failed_results.into_iter().map(|(_, result)| result);
            let accumulated_failures = unmapped_results.fold(
                SanitizeApplicabilityExternalError { errors: vec![] },
                |mut acc, item| match item {
                    Ok(_) => {
                        acc.errors
                            .push(SanitizeApplicabilityInternalError::InvalidIteratorCondition);
                        acc
                    }
                    Err(e) => {
                        acc.errors.push(
                            SanitizeApplicabilityInternalError::from_match_applicability_error(
                                e,
                                self.start_position.current_value.1,
                                self.start_position.current_value.2,
                            ),
                        );
                        acc
                    }
                },
            );
            return Err(accumulated_failures);
        }
        let mut filtered_results = successful_results
            .into_iter()
            .filter(|(_, result)| result.clone().is_ok_and(|x| x))
            .map(|(tag, _)| tag);
        let tag_to_evaluate = filtered_results.next();
        if let Some(found_tag) = tag_to_evaluate {
            return match found_tag {
                ApplicabilityExprKind::None(_applicability_expr_container) => {
                    Err(SanitizeApplicabilityExternalError {
                        errors: vec![SanitizeApplicabilityInternalError::None],
                    })
                }
                ApplicabilityExprKind::Text(text) => Ok(text.text.clone()),
                ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                    applicability_expr_container_with_position.sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        is_inverted,
                        ple_model,
                    )
                }
                ApplicabilityExprKind::Tag(applicability_expr_tag) => applicability_expr_tag
                    .sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(false),
                        ple_model,
                    ),
                ApplicabilityExprKind::TagNot(applicability_expr_tag) => applicability_expr_tag
                    .sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(true),
                        ple_model,
                    ),
                ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                    applicability_expr_substitution.sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        is_inverted,
                        ple_model,
                    )
                }
            };
        }
        Err(SanitizeApplicabilityExternalError {
            errors: vec![SanitizeApplicabilityInternalError::NoTagFound],
        })
    }
}

impl<I: Send + Sync> SanitizeApplicabilityV2<I> for ApplicabilityExprTag<I>
where
    I: PartialEq + Debug + Default + Extend<I> + Clone,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn sanitize(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        is_inverted: Option<bool>,
        ple_model: &[FeatureDefinition<I>],
    ) -> Result<I, SanitizeApplicabilityExternalError<I>> {
        //at this point, the tag should be validated through ApplicabiltyExprContainerWithPosition
        // all we need to do is sanitize each piece of contents and return it
        let (successful_results, failed_results): (Vec<_>, Vec<_>) = self
            .contents
            .iter()
            .map(|c| {
                match c {
                    ApplicabilityExprKind::None(_applicability_expr_container) => {
                        Err(SanitizeApplicabilityExternalError {
                            errors: vec![SanitizeApplicabilityInternalError::None],
                        })
                    }
                    ApplicabilityExprKind::Text(text) => Ok(text.text.clone()),
                    ApplicabilityExprKind::TagContainer(
                        applicability_expr_container_with_position,
                    ) => applicability_expr_container_with_position.sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        is_inverted,
                        ple_model,
                    ),

                    ApplicabilityExprKind::Tag(_applicability_expr_tag) => {
                        Err(SanitizeApplicabilityExternalError {
                            errors: vec![SanitizeApplicabilityInternalError::InvalidDataCondition],
                        })
                    } //invalid condition
                    ApplicabilityExprKind::TagNot(_applicability_expr_tag) => {
                        Err(SanitizeApplicabilityExternalError {
                            errors: vec![SanitizeApplicabilityInternalError::InvalidDataCondition],
                        })
                    } //invalid condition
                    ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                        applicability_expr_substitution.sanitize(
                            features,
                            config_name,
                            substitutes,
                            parent_group,
                            child_configurations,
                            is_inverted,
                            ple_model,
                        )
                    }
                }
            })
            .partition(|value| value.is_ok());
        let actual_failed = failed_results
            .into_iter()
            .filter(|x| match x {
                Ok(_) => false,
                Err(e) => !e
                    .errors
                    .iter()
                    .filter(|e1| {
                        matches!(
                            e1,
                            SanitizeApplicabilityInternalError::MissingApplicability
                                | SanitizeApplicabilityInternalError::MissingSubstitution(_, _, _)
                                | SanitizeApplicabilityInternalError::InvalidDataCondition
                                | SanitizeApplicabilityInternalError::None
                        )
                    })
                    .collect::<Vec<_>>()
                    .is_empty(),
            })
            .collect::<Vec<_>>();
        let mut acc = I::default();
        if !actual_failed.is_empty() {
            let accumulated_failures =
                actual_failed
                    .into_iter()
                    .reduce(|acc, item| match (acc, item) {
                        (Ok(_), Ok(_)) => Err(SanitizeApplicabilityExternalError {
                            errors: vec![
                                SanitizeApplicabilityInternalError::InvalidIteratorCondition,
                            ],
                        }),
                        (Ok(_), Err(e2)) => Err(e2),
                        (Err(e1), Ok(_)) => Err(e1),
                        (Err(mut e1), Err(e2)) => {
                            e1.errors.extend(e2.errors);
                            Err(e1)
                        }
                    });
            if accumulated_failures.is_none() {
                return Err(SanitizeApplicabilityExternalError { errors: vec![] });
            }
            return accumulated_failures.unwrap();
        }
        acc.extend(successful_results.into_iter().filter_map(|x| x.ok()));
        Ok(acc)
    }
}
impl<I: Send + Sync> SanitizeApplicabilityV2<I> for ApplicabilityExprSubstitution<I>
where
    I: PartialEq + Debug + Default + Extend<I> + Clone,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn sanitize(
        &self,
        _features: &[ApplicabilityTag<I>],
        _config_name: &I,
        substitutes: &[Substitution<I, I>],
        _parent_group: Option<&I>,
        _child_configurations: Option<&[I]>,
        _is_inverted: Option<bool>,
        ple_model: &[FeatureDefinition<I>],
    ) -> Result<I, SanitizeApplicabilityExternalError<I>> {
        let (successful_tokens, failed_tokens): (Vec<_>, Vec<_>) =
            self.tag.clone().into_iter().partition(|token| {
                token
                    .match_token(
                        substitutes,
                        &I::default(),
                        None,
                        None,
                        false,
                        &ApplicabilityTagTypes::Configuration,
                        ple_model,
                    )
                    .is_ok_and(|x| x)
            });
        if !failed_tokens.is_empty() {
            return Err(SanitizeApplicabilityExternalError {
                errors: vec![SanitizeApplicabilityInternalError::MissingSubstitution(
                    match failed_tokens.first().unwrap() {
                        ApplicTokens::NoTag(applicability_tag) => applicability_tag.0.tag.clone(),
                        ApplicTokens::Not(applicability_tag) => applicability_tag.0.tag.clone(),
                        ApplicTokens::And(applicability_tag) => applicability_tag.0.tag.clone(),
                        ApplicTokens::NotAnd(applicability_tag) => applicability_tag.0.tag.clone(),
                        ApplicTokens::Or(applicability_tag) => applicability_tag.0.tag.clone(),
                        ApplicTokens::NotOr(applicability_tag) => applicability_tag.0.tag.clone(),
                        ApplicTokens::NestedAnd(_) => I::default(),
                        ApplicTokens::NestedNotAnd(_) => I::default(),
                        ApplicTokens::NestedOr(_) => I::default(),
                        ApplicTokens::NestedNotOr(_) => I::default(),
                    },
                    self.start_position.current_value.1,
                    self.start_position.current_value.2,
                )],
            });
        }
        let iter = successful_tokens.into_iter().flat_map(|token| {
            substitutes
                .iter()
                .filter(|&substitute| substitute.match_text == token.get_tag())
                .cloned()
                .map(|substitute| substitute.substitute)
                .collect::<Vec<I>>()
        });
        let mut acc = I::default();
        acc.extend(iter);
        Ok(acc)
    }
}
impl<I: Send + Sync> SanitizeApplicabilityV2<I> for ApplicabilityExprContainer<I>
where
    I: PartialEq + Debug + Default + Extend<I> + Clone,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn sanitize(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        is_inverted: Option<bool>,
        ple_model: &[FeatureDefinition<I>],
    ) -> Result<I, SanitizeApplicabilityExternalError<I>> {
        let (successful_results, failed_results): (Vec<_>, Vec<_>) = self
            .contents
            .iter()
            .map(|c| match c {
                ApplicabilityExprKind::None(applicability_expr_container) => {
                    applicability_expr_container.sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        is_inverted,
                        ple_model,
                    )
                }
                ApplicabilityExprKind::Text(text) => Ok(text.text.clone()),
                ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                    applicability_expr_container_with_position.sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        is_inverted,
                        ple_model,
                    )
                }
                ApplicabilityExprKind::Tag(applicability_expr_tag) => applicability_expr_tag
                    .sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(true),
                        ple_model,
                    ),
                ApplicabilityExprKind::TagNot(applicability_expr_tag) => applicability_expr_tag
                    .sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(true),
                        ple_model,
                    ),
                ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                    applicability_expr_substitution.sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(true),
                        ple_model,
                    )
                }
            })
            .partition(|value| value.is_ok());
        let actual_failed = failed_results
            .into_iter()
            .filter(|x| match x {
                Ok(_) => false,
                Err(e) => !e
                    .errors
                    .iter()
                    .filter(|e1| {
                        matches!(
                            e1,
                            SanitizeApplicabilityInternalError::MissingApplicability
                                | SanitizeApplicabilityInternalError::MissingSubstitution(_, _, _)
                                | SanitizeApplicabilityInternalError::FeatureTagDoesNotExist(
                                    _,
                                    _,
                                    _
                                )
                                | SanitizeApplicabilityInternalError::FeatureValueDoesNotExist(
                                    _,
                                    _,
                                    _
                                )
                                | SanitizeApplicabilityInternalError::InvalidDataCondition
                                | SanitizeApplicabilityInternalError::None
                        )
                    })
                    .collect::<Vec<_>>()
                    .is_empty(),
            })
            .collect::<Vec<_>>();
        let mut acc = I::default();
        if !actual_failed.is_empty() {
            let accumulated_failures =
                actual_failed
                    .into_iter()
                    .reduce(|acc, item| match (acc, item) {
                        (Ok(_), Ok(_)) => Err(SanitizeApplicabilityExternalError {
                            errors: vec![
                                SanitizeApplicabilityInternalError::InvalidIteratorCondition,
                            ],
                        }),
                        (Ok(_), Err(e2)) => Err(e2),
                        (Err(e1), Ok(_)) => Err(e1),
                        (Err(mut e1), Err(e2)) => {
                            e1.errors.extend(e2.errors);
                            Err(e1)
                        }
                    });
            if accumulated_failures.is_none() {
                return Err(SanitizeApplicabilityExternalError { errors: vec![] });
            }
            return accumulated_failures.unwrap();
        }
        acc.extend(successful_results.into_iter().filter_map(|x| x.ok()));
        Ok(acc)
    }
}

impl<I: Send + Sync> SanitizeApplicabilityV2<I> for ApplicabilityExprKind<I>
where
    I: PartialEq + Debug + Default + Extend<I> + Clone,
    ApplicTokens<I>:
        MatchToken<Substitution<I, I>, TagType = I> + MatchToken<ApplicabilityTag<I>, TagType = I>,
{
    fn sanitize(
        &self,
        features: &[ApplicabilityTag<I>],
        config_name: &I,
        substitutes: &[Substitution<I, I>],
        parent_group: Option<&I>,
        child_configurations: Option<&[I]>,
        is_inverted: Option<bool>,
        ple_model: &[FeatureDefinition<I>],
    ) -> Result<I, SanitizeApplicabilityExternalError<I>> {
        match self {
            ApplicabilityExprKind::None(applicability_expr_container) => {
                applicability_expr_container.sanitize(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    is_inverted,
                    ple_model,
                )
            }
            ApplicabilityExprKind::Text(text) => Ok(text.text.clone()),
            ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                applicability_expr_container_with_position.sanitize(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    is_inverted,
                    ple_model,
                )
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => applicability_expr_tag.sanitize(
                features,
                config_name,
                substitutes,
                parent_group,
                child_configurations,
                Some(true),
                ple_model,
            ),
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => applicability_expr_tag
                .sanitize(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    Some(true),
                    ple_model,
                ),
            ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                applicability_expr_substitution.sanitize(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    Some(true),
                    ple_model,
                )
            }
        }
    }
}
