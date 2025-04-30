use applicability_lexer_base::position::Position;
use applicability_parser_types::applic_tokens::{ApplicTokens, ApplicabilityNestedAndTag};

use crate::latch::LatchedValue;
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ApplicabilityKind {
    Feature,
    Configuration,
    ConfigurationGroup,
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub enum ApplicabilityExprKind<Input> {
    None(ApplicabilityExprContainer<Input>),
    Text(Text<Input>),
    TagContainer(ApplicabilityExprContainerWithPosition<Input>),
    Tag(ApplicabilityExprTag<Input>),
    TagNot(ApplicabilityExprTag<Input>),
    Substitution,
}
impl<Input> ApplicabilityExprKind<Input> {
    pub fn set_end_position(&mut self, position: Position) {
        match self {
            ApplicabilityExprKind::None(applicability_expr_container) => {}
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
            ApplicabilityExprKind::Substitution => todo!(),
        }
    }
    pub fn has_end_position_changed(&self) -> bool {
        match self {
            ApplicabilityExprKind::None(applicability_expr_container) => false,
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
            ApplicabilityExprKind::Substitution => false,
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
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityExprSubstitution<Input> {
    pub tag: Vec<ApplicTokens<Input>>,
    pub start_position: LatchedValue<Position>,
    pub end_position: LatchedValue<Position>,
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityExprContainer<Input> {
    pub contents: Vec<ApplicabilityExprKind<Input>>,
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
                ApplicabilityExprKind::Substitution => vec![],
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
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct Text<I> {
    pub text: I,
    pub start_position: LatchedValue<Position>,
    pub end_position: LatchedValue<Position>,
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
