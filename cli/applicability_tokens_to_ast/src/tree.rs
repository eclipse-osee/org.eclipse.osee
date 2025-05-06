use std::fmt::Debug;

use applicability::applic_tag::ApplicabilityTagTypes;
use applicability_lexer_base::position::Position;
use applicability_match::MatchApplicability;
use applicability_parser_types::applic_tokens::{
    ApplicTokens, ApplicabilityNestedAndTag, MatchToken,
};

use crate::latch::LatchedValue;
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ApplicabilityKind {
    Feature,
    Configuration,
    ConfigurationGroup,
}

impl From<ApplicabilityTagTypes> for ApplicabilityKind {
    fn from(value: ApplicabilityTagTypes) -> Self {
        match value {
            ApplicabilityTagTypes::Feature => ApplicabilityKind::Feature,
            ApplicabilityTagTypes::Configuration => ApplicabilityKind::Configuration,
            ApplicabilityTagTypes::ConfigurationGroup => ApplicabilityKind::ConfigurationGroup,
        }
    }
}

impl From<ApplicabilityKind> for ApplicabilityTagTypes {
    fn from(val: ApplicabilityKind) -> Self {
        match val {
            ApplicabilityKind::Feature => ApplicabilityTagTypes::Feature,
            ApplicabilityKind::Configuration => ApplicabilityTagTypes::Configuration,
            ApplicabilityKind::ConfigurationGroup => ApplicabilityTagTypes::ConfigurationGroup,
        }
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ApplicabilityExprKind<Input> {
    None(ApplicabilityExprContainer<Input>),
    Text(Text<Input>),
    TagContainer(ApplicabilityExprContainerWithPosition<Input>),
    Tag(ApplicabilityExprTag<Input>),
    TagNot(ApplicabilityExprTag<Input>),
    Substitution(ApplicabilityExprSubstitution<Input>),
}
impl<Input> ApplicabilityExprKind<Input> {
    pub fn set_end_position(&mut self, position: Position) {
        match self {
            ApplicabilityExprKind::None(_applicability_expr_container) => {}
            ApplicabilityExprKind::Text(text) => text.end_position.next(position),
            ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                applicability_expr_container_with_position
                    .end_position
                    .next(position);
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => {
                applicability_expr_tag.set_end_position(position)
            }
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => {
                applicability_expr_tag.end_position.next(position)
            }
            ApplicabilityExprKind::Substitution(applicability_tag) => {
                applicability_tag.end_position.next(position);
            }
        }
    }
    pub fn has_end_position_changed(&self) -> bool {
        match self {
            ApplicabilityExprKind::None(_applicability_expr_container) => false,
            ApplicabilityExprKind::Text(text) => text.end_position.has_changed(),
            ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                applicability_expr_container_with_position
                    .end_position
                    .has_changed()
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => {
                applicability_expr_tag.end_position.has_changed()
            }
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => {
                applicability_expr_tag.end_position.has_changed()
            }
            ApplicabilityExprKind::Substitution(applicability_tag) => {
                applicability_tag.end_position.has_changed()
            }
        }
    }
}
impl From<ApplicabilityExprKind<&str>> for ApplicabilityExprKind<String> {
    fn from(value: ApplicabilityExprKind<&str>) -> Self {
        match value {
            ApplicabilityExprKind::None(applicability_expr_container) => {
                ApplicabilityExprKind::None(applicability_expr_container.into())
            }
            ApplicabilityExprKind::Text(text) => ApplicabilityExprKind::Text(text.into()),
            ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                ApplicabilityExprKind::TagContainer(
                    applicability_expr_container_with_position.into(),
                )
            }
            ApplicabilityExprKind::Tag(applicability_expr_tag) => {
                ApplicabilityExprKind::Tag(applicability_expr_tag.into())
            }
            ApplicabilityExprKind::TagNot(applicability_expr_tag) => {
                ApplicabilityExprKind::TagNot(applicability_expr_tag.into())
            }
            ApplicabilityExprKind::Substitution(applicability_expr_substitution) => {
                ApplicabilityExprKind::Substitution(applicability_expr_substitution.into())
            }
        }
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityExprTag<Input> {
    pub tag: Vec<ApplicTokens<Input>>,
    pub kind: ApplicabilityKind,
    pub contents: Vec<ApplicabilityExprKind<Input>>,
    pub start_position: LatchedValue<Position>,
    pub end_position: LatchedValue<Position>,
}
impl<Input> ApplicabilityExprTag<Input> {
    pub fn set_end_position(&mut self, position: Position) {
        self.end_position.next(position);
    }
    pub fn has_end_position_changed(&self) -> bool {
        self.end_position.has_changed()
    }
}

impl<Input> ApplicabilityExprTag<Input> {
    pub fn add_text(&mut self, text: Text<Input>) {
        self.add_expr(ApplicabilityExprKind::Text(text));
    }
    pub fn add_expr(&mut self, tag_to_insert: ApplicabilityExprKind<Input>) {
        self.contents.push(tag_to_insert);
    }
}

impl From<ApplicabilityExprTag<&str>> for ApplicabilityExprTag<String> {
    fn from(value: ApplicabilityExprTag<&str>) -> Self {
        Self {
            tag: value.tag.into_iter().map(Into::into).collect::<Vec<_>>(),
            kind: value.kind,
            contents: value
                .contents
                .into_iter()
                .map(Into::into)
                .collect::<Vec<_>>(),
            start_position: value.start_position,
            end_position: value.end_position,
        }
    }
}

// impl<'a, I, T> MatchApplicability<T> for ApplicabilityExprTag<&'a I>
// where
//     I: PartialEq + Debug,
//     ApplicTokens<&'a I>: MatchToken<T, TagType = &'a I>,
// {
//     type TagType = &'a I;
//     fn match_applicability(
//         &self,
//         match_list: &[T],
//         config_name: Self::TagType,
//         parent_group: Option<Self::TagType>,
//         child_configurations: Option<&[Self::TagType]>,
//     ) -> bool {
//         let mut found = false;
//         let tags = &self.tag;
//         for applic_tag in tags {
//             found = applic_tag.match_token(
//                 match_list,
//                 config_name,
//                 parent_group,
//                 child_configurations,
//                 found,
//                 &(self.kind.clone().into()),
//             )
//         }
//         found
//     }
// }
impl<I, T> MatchApplicability<T> for ApplicabilityExprTag<I>
where
    I: PartialEq + Debug + Clone,
    ApplicTokens<I>: MatchToken<T, TagType = I>,
{
    type TagType = I;
    fn match_applicability(
        &self,
        match_list: &[T],
        config_name: &Self::TagType,
        parent_group: Option<&Self::TagType>,
        child_configurations: Option<&[Self::TagType]>,
    ) -> bool {
        let mut found = false;
        let tags = &self.tag;
        for applic_tag in tags {
            found = applic_tag.match_token(
                match_list,
                config_name,
                parent_group,
                child_configurations,
                found,
                &(self.kind.clone().into()),
            )
        }
        found
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityExprSubstitution<Input> {
    pub tag: Vec<ApplicTokens<Input>>,
    pub start_position: LatchedValue<Position>,
    pub end_position: LatchedValue<Position>,
}

impl From<ApplicabilityExprSubstitution<&str>> for ApplicabilityExprSubstitution<String> {
    fn from(value: ApplicabilityExprSubstitution<&str>) -> Self {
        Self {
            tag: value.tag.into_iter().map(|x| x.into()).collect::<Vec<_>>(),
            start_position: value.start_position,
            end_position: value.end_position,
        }
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityExprContainer<Input> {
    pub contents: Vec<ApplicabilityExprKind<Input>>,
}

impl From<ApplicabilityExprContainer<&str>> for ApplicabilityExprContainer<String> {
    fn from(value: ApplicabilityExprContainer<&str>) -> Self {
        Self {
            contents: value
                .contents
                .into_iter()
                .map(Into::into)
                .collect::<Vec<_>>(),
        }
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityExprContainerWithPosition<Input> {
    pub contents: Vec<ApplicabilityExprKind<Input>>,
    pub start_position: LatchedValue<Position>,
    pub end_position: LatchedValue<Position>,
}
impl<Input> ApplicabilityExprContainerWithPosition<Input> {
    pub fn add_text_to_latest_tag(&mut self, text: Text<Input>) {
        self.add_expr_to_latest_tag(ApplicabilityExprKind::Text(text));
    }
    pub fn add_expr_to_latest_tag(&mut self, tag_to_insert: ApplicabilityExprKind<Input>) {
        if let Some(idx) = self.contents.iter().rposition(|expr| {
            matches!(
                expr,
                ApplicabilityExprKind::Tag(_) | ApplicabilityExprKind::TagNot(_)
            )
        }) {
            let mut tag = self.contents.remove(idx);
            match tag {
                ApplicabilityExprKind::Tag(ref mut tag_result)
                | ApplicabilityExprKind::TagNot(ref mut tag_result) => {
                    tag_result.contents.push(tag_to_insert);
                }
                _ => {}
            }
            self.contents.push(tag);
        }
    }
    pub fn add_expr(&mut self, tag_to_insert: ApplicabilityExprKind<Input>) {
        self.contents.push(tag_to_insert);
    }
    pub fn get_total_tags(&self) -> Vec<ApplicTokens<Input>>
    where
        Input: Clone,
    {
        self.contents
            .iter()
            .filter(|expr| {
                matches!(
                    expr,
                    ApplicabilityExprKind::Tag(_) | ApplicabilityExprKind::TagNot(_)
                )
            })
            .map(|tag| match tag {
                ApplicabilityExprKind::Tag(applicability_expr_tag) => {
                    applicability_expr_tag.tag.clone()
                }
                ApplicabilityExprKind::TagNot(applicability_expr_tag) => {
                    applicability_expr_tag.tag.clone()
                }
                ApplicabilityExprKind::None(_) => {
                    vec![]
                }
                ApplicabilityExprKind::Text(_) => vec![],
                ApplicabilityExprKind::TagContainer(applicability_expr_container_with_position) => {
                    applicability_expr_container_with_position.get_total_tags()
                }
                ApplicabilityExprKind::Substitution(_) => vec![], //substitution tags aren't valuable when trying to assess the total applicability
            })
            .filter(|x| !x.is_empty())
            .fold(vec![], |mut acc, element| {
                acc.push(ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(
                    element, None,
                )));
                acc
            })
    }
}

impl From<ApplicabilityExprContainerWithPosition<&str>>
    for ApplicabilityExprContainerWithPosition<String>
{
    fn from(value: ApplicabilityExprContainerWithPosition<&str>) -> Self {
        Self {
            contents: value
                .contents
                .into_iter()
                .map(Into::into)
                .collect::<Vec<_>>(),
            start_position: value.start_position,
            end_position: value.end_position,
        }
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Text<I> {
    pub text: I,
    pub start_position: LatchedValue<Position>,
    pub end_position: LatchedValue<Position>,
}

impl From<Text<&str>> for Text<String> {
    fn from(value: Text<&str>) -> Self {
        Self {
            text: value.text.to_string(),
            start_position: value.start_position,
            end_position: value.end_position,
        }
    }
}
impl<I, T> MatchApplicability<T> for Text<I> {
    type TagType = I;
    fn match_applicability(
        &self,
        _match_list: &[T],
        _config_name: &Self::TagType,
        _parent_group: Option<&Self::TagType>,
        _child_configurations: Option<&[Self::TagType]>,
    ) -> bool {
        true
    }
}

#[cfg(test)]
mod tests {

    mod total_tags {
        use applicability::applic_tag::ApplicabilityTag;
        use applicability_parser_types::applic_tokens::{
            ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedAndTag, ApplicabilityNoTag,
        };

        use crate::{
            latch::LatchedValue,
            tree::{
                ApplicabilityExprContainerWithPosition, ApplicabilityExprKind,
                ApplicabilityExprTag, ApplicabilityKind,
            },
        };

        #[test]
        fn test_total_tag_basic() {
            let container = ApplicabilityExprContainerWithPosition {
                contents: vec![
                    ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "APPLIC_1",
                                value: "Included".to_string(),
                            },
                            None,
                        ))],
                        kind: ApplicabilityKind::Feature,
                        contents: vec![],
                        start_position: LatchedValue::new((0, 0)),
                        end_position: LatchedValue::new((0, 0)),
                    }),
                    ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(
                            vec![
                                ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string(),
                                    },
                                    None,
                                )),
                                ApplicTokens::And(ApplicabilityAndTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_3",
                                        value: "Included".to_string(),
                                    },
                                    None,
                                )),
                            ],
                            None,
                        ))],
                        kind: ApplicabilityKind::Feature,
                        contents: vec![],
                        start_position: LatchedValue::new((0, 0)),
                        end_position: LatchedValue::new((0, 0)),
                    }),
                ],
                start_position: LatchedValue::new((0, 0)),
                end_position: LatchedValue::new((0, 0)),
            };
            assert_eq!(
                container.get_total_tags(),
                vec![
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(
                        vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "APPLIC_1",
                                value: "Included".to_string()
                            },
                            None
                        ))],
                        None
                    )),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(
                        vec![ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(
                            vec![
                                ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                )),
                                ApplicTokens::And(ApplicabilityAndTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_3",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))
                            ],
                            None
                        ))],
                        None
                    ))
                ]
            )
        }
    }
}
