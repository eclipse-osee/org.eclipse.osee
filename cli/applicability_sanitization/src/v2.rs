use std::fmt::Debug;

use applicability::{
    applic_tag::{ApplicabilityTag, ApplicabilityTagTypes},
    substitution::Substitution,
};
use applicability_match::MatchApplicability;
use applicability_parser_types::applic_tokens::{ApplicTokens, GetApplicabilityTag, MatchToken};
use applicability_tokens_to_ast::tree::{
    ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition, ApplicabilityExprKind,
    ApplicabilityExprSubstitution, ApplicabilityExprTag, Text,
};

pub trait SanitizeApplicabilityV2<X1> {
    fn sanitize(
        &self,
        features: &[ApplicabilityTag<X1>],
        config_name: &X1,
        substitutes: &[Substitution<X1, X1>],
        parent_group: Option<&X1>,
        child_configurations: Option<&[X1]>,
        is_inverted: Option<bool>,
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
    ) -> Option<I> {
        //find the first tag that evaluates to true
        let tag_to_evaluate = self.contents.iter().find(|tag| match tag {
            ApplicabilityExprKind::None(_applicability_expr_container) => false, //should never hit
            ApplicabilityExprKind::Text(_text) => false,                         //should never hit
            ApplicabilityExprKind::TagContainer(_applicability_expr_container_with_position) => {
                false //should never come here? 
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => {
                // applicability_expr_tag
                // .match_applicability(features, config_name, parent_group, child_configurations)
                applicability_expr_tag.match_applicability(
                    features,
                    config_name,
                    parent_group,
                    child_configurations,
                )
            }
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => !applicability_expr_tag
                .match_applicability(features, config_name, parent_group, child_configurations),
            ApplicabilityExprKind::Substitution(_applicability_expr_substitution) => false,
        });
        if let Some(found_tag) = tag_to_evaluate {
            // found_tag.sanitize()
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
                    ),
                ApplicabilityExprKind::TagNot(applicability_expr_tag) => applicability_expr_tag
                    .sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        Some(true),
                    ),
                ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                    applicability_expr_substitution.sanitize(
                        features,
                        config_name,
                        substitutes,
                        parent_group,
                        child_configurations,
                        is_inverted,
                    )
                }
            };
        }
        None
    }
}

impl<I: Send + Sync> SanitizeApplicabilityV2<I> for ApplicabilityExprTag<I>
where
    I: PartialEq
        + Debug
        //        + ExtendInto
        + Default
        + Extend<I>
        + Clone,
    //<I as ExtendInto>::Extender: Default + Extend<<I as ExtendInto>::Extender> + Clone,
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
    I: PartialEq
        + Debug
        //        + ExtendInto
        + Default
        + Extend<I>
        + Clone,
    //<I as ExtendInto>::Extender: Default + Extend<<I as ExtendInto>::Extender> + Clone,
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
    I: PartialEq
        + Debug
        //        + ExtendInto
        + Default
        + Extend<I>
        + Clone,
    //<I as ExtendInto>::Extender: Default + Extend<<I as ExtendInto>::Extender> + Clone,
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
                )
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => applicability_expr_tag.sanitize(
                features,
                config_name,
                substitutes,
                parent_group,
                child_configurations,
                Some(true),
            ),
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => applicability_expr_tag
                .sanitize(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    Some(true),
                ),
            ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                applicability_expr_substitution.sanitize(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    Some(true),
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
    I: PartialEq
        + Debug
        //        + ExtendInto
        + Default
        + Extend<I>
        + Clone,
    //<I as ExtendInto>::Extender: Default + Extend<<I as ExtendInto>::Extender> + Clone,
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
                )
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => applicability_expr_tag.sanitize(
                features,
                config_name,
                substitutes,
                parent_group,
                child_configurations,
                Some(true),
            ),
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => applicability_expr_tag
                .sanitize(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    Some(true),
                ),
            ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                applicability_expr_substitution.sanitize(
                    features,
                    config_name,
                    substitutes,
                    parent_group,
                    child_configurations,
                    Some(true),
                )
            }
        }
    }
}
// impl<I: Send + Sync> SanitizeApplicabilityV2 for ApplicabilityExprKind<I> {
//     fn sanitize(
//         &self,
//         features: &[ApplicabilityTag],
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//         is_inverted: Option<bool>,
//     ) {
//         match self {
//             ApplicabilityExprKind::None(applicability_expr_container) => {
//                 applicability_expr_container.sanitize(
//                     features,
//                     config_name,
//                     substitutes,
//                     parent_group,
//                     child_configurations,
//                     is_inverted,
//                 )
//             }
//             ApplicabilityExprKind::Text(text) => todo!(),
//             ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
//                 todo!()
//             }
//             ApplicabilityExprKind::Tag(applicability_expr_tag)
//             | ApplicabilityExprKind::TagNot(applicability_expr_tag) => todo!(),
//             ApplicabilityExprKind::Substitution(applicability_expr_substitution) => todo!(),
//         }
//     }
// }
// impl<I: Send + Sync> SanitizeApplicabilityV2 for ApplicabilityExprContainer<I> {
//     fn sanitize(
//         &self,
//         features: &[ApplicabilityTag],
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//         is_inverted: Option<bool>,
//     ) {
//         self.contents
//             .par_iter()
//             .map(|content| {
//                 content.sanitize(
//                     features,
//                     config_name,
//                     substitutes,
//                     parent_group,
//                     child_configurations,
//                     is_inverted,
//                 )
//             })
//             .collect::<Vec<_>>();
//     }
// }

// impl<I: Send + Sync> SanitizeApplicabilityV2 for Text<I> {
//     fn sanitize(
//         &self,
//         features: &[ApplicabilityTag],
//         config_name: &str,
//         substitutes: &[Substitution],
//         parent_group: Option<&str>,
//         child_configurations: Option<&[&str]>,
//         is_inverted: Option<bool>,
//     ) {
//         // self.text;
//     }
// }
