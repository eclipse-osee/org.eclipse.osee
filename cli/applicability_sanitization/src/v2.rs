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
use applicability_match::MatchApplicability;
use applicability_parser_types::applic_tokens::{ApplicTokens, GetApplicabilityTag, MatchToken};
use applicability_tokens_to_ast::tree::{
    ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition, ApplicabilityExprKind,
    ApplicabilityExprSubstitution, ApplicabilityExprTag,
};
use feature_definition::FeatureDefinition;

pub trait SanitizeApplicabilityV2<X1> {
    fn sanitize(
        &self,
        features: &[ApplicabilityTag<X1>],
        config_name: &X1,
        substitutes: &[Substitution<X1, X1>],
        parent_group: Option<&X1>,
        child_configurations: Option<&[X1]>,
        is_inverted: Option<bool>,
        ple_model: &[FeatureDefinition<X1>],
    ) -> Option<X1>;
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
    ) -> Option<I> {
        //find the first tag that evaluates to true
        let tag_to_evaluate = self.contents.iter().find(|tag| match tag {
            ApplicabilityExprKind::None(_applicability_expr_container) => false, //should never hit
            ApplicabilityExprKind::Text(_text) => false,                         //should never hit
            ApplicabilityExprKind::TagContainer(_applicability_expr_container_with_position) => {
                false //should never come here? 
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => applicability_expr_tag
                .match_applicability(
                    features,
                    config_name,
                    parent_group,
                    child_configurations,
                    ple_model,
                ),
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => !applicability_expr_tag
                .match_applicability(
                    features,
                    config_name,
                    parent_group,
                    child_configurations,
                    ple_model,
                ),
            ApplicabilityExprKind::Substitution(_applicability_expr_substitution) => false,
        });
        if let Some(found_tag) = tag_to_evaluate {
            return match found_tag {
                ApplicabilityExprKind::None(_applicability_expr_container) => None,
                ApplicabilityExprKind::Text(text) => Some(text.text.clone()),
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
        None
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
    ) -> Option<I> {
        //at this point, the tag should be validated through ApplicabiltyExprContainerWithPosition
        // all we need to do is sanitize each piece of contents and return it
        let iter = self.contents.iter().filter_map(|c| match c {
            ApplicabilityExprKind::None(_applicability_expr_container) => None,
            ApplicabilityExprKind::Text(text) => Some(text.text.clone()),
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

            ApplicabilityExprKind::Tag(_applicability_expr_tag) => None, //invalid condition
            ApplicabilityExprKind::TagNot(_applicability_expr_tag) => None, //invalid condition
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
        });
        let mut acc = I::default();
        acc.extend(iter);
        Some(acc)
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
    ) -> Option<I> {
        let iter = self
            .tag
            .clone()
            .into_iter()
            .filter(|token| {
                token.match_token(
                    substitutes,
                    &I::default(),
                    None,
                    None,
                    false,
                    &ApplicabilityTagTypes::Configuration,
                    ple_model,
                )
            })
            .flat_map(|token| {
                substitutes
                    .iter()
                    .filter(|&substitute| substitute.match_text == token.get_tag())
                    .cloned()
                    .map(|substitute| substitute.substitute)
                    .collect::<Vec<I>>()
            });
        let mut acc = I::default();
        acc.extend(iter);
        Some(acc)
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
    ) -> Option<I> {
        let iter = self.contents.iter().filter_map(|c| match c {
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
            ApplicabilityExprKind::Text(text) => Some(text.text.clone()),
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
        });
        let mut acc = I::default();
        acc.extend(iter);
        Some(acc)
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
    ) -> Option<I> {
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
            ApplicabilityExprKind::Text(text) => Some(text.text.clone()),
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
