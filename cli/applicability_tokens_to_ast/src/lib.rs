use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::applicability_structure::LexerToken;
use config::{process_config, process_config_not, process_config_switch};
use config_group::{process_config_group, process_config_group_not, process_config_group_switch};
use feature::{process_feature, process_feature_not, process_feature_switch};
use nom::{AsBytes, Input, Offset};
use state_machine::StateMachine;
use substitution::process_substitution;
use tree::{ApplicabilityExprContainer, ApplicabilityExprKind, Text};
use updatable::UpdatableValue;

mod config;
mod config_group;
mod feature;

mod state_machine;
mod substitution;
pub mod tree;
pub mod updatable;

pub fn transform_tokens<I>(input: Vec<LexerToken<I>>) -> ApplicabilityExprKind<I>
where
    I: Input + Send + Sync + Default + AsBytes + Offset,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut sm = StateMachine::new(input.into_iter());
    process_tokens(&mut sm)
}

pub fn process_tokens<I, Iter>(transformer: &mut StateMachine<I, Iter>) -> ApplicabilityExprKind<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut container = ApplicabilityExprContainer { contents: vec![] };
    while transformer.next().is_some() {
        let current_token = transformer.current_token.clone();
        match &current_token {
            LexerToken::Nothing => todo!(),
            LexerToken::Illegal => todo!(),
            LexerToken::Identity => todo!(),
            LexerToken::Text(content, position) => {
                let text = Text {
                    text: content.to_owned(),
                    start_position: UpdatableValue::new(position.0),
                    end_position: UpdatableValue::new(position.1),
                };
                container.contents.push(ApplicabilityExprKind::Text(text))
            }
            LexerToken::TextToDiscard(_, _) => todo!(),
            LexerToken::Feature(position) => {
                let node_to_add = process_feature(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::FeatureNot(position) => {
                let node_to_add = process_feature_not(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::FeatureSwitch(position) => {
                let node_to_add = process_feature_switch(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::FeatureCase(_) => todo!(),
            LexerToken::FeatureElse(_) => todo!(),
            LexerToken::FeatureElseIf(_) => todo!(),
            LexerToken::EndFeature(_) => todo!(),
            LexerToken::Configuration(position) => {
                let node_to_add = process_config(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::ConfigurationNot(position) => {
                let node_to_add = process_config_not(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::ConfigurationSwitch(position) => {
                let node_to_add = process_config_switch(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::ConfigurationCase(_) => todo!(),
            LexerToken::ConfigurationElse(_) => todo!(),
            LexerToken::ConfigurationElseIf(_) => todo!(),
            LexerToken::EndConfiguration(_) => todo!(),
            LexerToken::ConfigurationGroup(position) => {
                let node_to_add = process_config_group(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::ConfigurationGroupNot(position) => {
                let node_to_add = process_config_group_not(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                let node_to_add = process_config_group_switch(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::ConfigurationGroupCase(_) => todo!(),
            LexerToken::ConfigurationGroupElse(_) => todo!(),
            LexerToken::ConfigurationGroupElseIf(_) => todo!(),
            LexerToken::EndConfigurationGroup(_) => todo!(),
            LexerToken::Substitution(position) => {
                let node_to_add = process_substitution(transformer, position);
                container.contents.push(node_to_add);
            }
            LexerToken::Space(_) => todo!(),
            LexerToken::CarriageReturn(_) => todo!(),
            LexerToken::UnixNewLine(_) => todo!(),
            LexerToken::Tab(_) => todo!(),
            LexerToken::StartBrace(_) => todo!(),
            LexerToken::EndBrace(_) => todo!(),
            LexerToken::StartParen(_) => todo!(),
            LexerToken::EndParen(_) => todo!(),
            LexerToken::Not(_) => todo!(),
            LexerToken::And(_) => todo!(),
            LexerToken::Or(_) => todo!(),
            LexerToken::Tag(_, _) => todo!(),
            LexerToken::StartCommentMultiLine(_, _) => todo!(),
            LexerToken::EndCommentMultiLine(_, _) => todo!(),
            LexerToken::SingleLineCommentCharacter(_, _) => todo!(),
        }
    }
    let current_token = transformer.current_token.clone();
    match &current_token {
        LexerToken::Nothing => todo!(),
        LexerToken::Illegal => todo!(),
        LexerToken::Identity => todo!(),
        LexerToken::Text(content, position) => {
            let text = Text {
                text: content.to_owned(),
                start_position: UpdatableValue::new(position.0),
                end_position: UpdatableValue::new(position.1),
            };
            container.contents.push(ApplicabilityExprKind::Text(text))
        }
        LexerToken::TextToDiscard(_, _) => {}
        LexerToken::Feature(position) => {
            let node_to_add = process_feature(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::FeatureNot(position) => {
            let node_to_add = process_feature_not(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::FeatureSwitch(position) => {
            let node_to_add = process_feature_switch(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::FeatureCase(_) => todo!(),
        LexerToken::FeatureElse(_) => todo!(),
        LexerToken::FeatureElseIf(_) => todo!(),
        LexerToken::EndFeature(_) => {}
        LexerToken::Configuration(position) => {
            let node_to_add = process_config(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::ConfigurationNot(position) => {
            let node_to_add = process_config_not(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::ConfigurationSwitch(position) => {
            let node_to_add = process_config_switch(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::ConfigurationCase(_) => todo!(),
        LexerToken::ConfigurationElse(_) => todo!(),
        LexerToken::ConfigurationElseIf(_) => todo!(),
        LexerToken::EndConfiguration(_) => {}
        LexerToken::ConfigurationGroup(position) => {
            let node_to_add = process_config_group(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::ConfigurationGroupNot(position) => {
            let node_to_add = process_config_group_not(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::ConfigurationGroupSwitch(position) => {
            let node_to_add = process_config_group_switch(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::ConfigurationGroupCase(_) => todo!(),
        LexerToken::ConfigurationGroupElse(_) => todo!(),
        LexerToken::ConfigurationGroupElseIf(_) => todo!(),
        LexerToken::EndConfigurationGroup(_) => {}
        LexerToken::Substitution(position) => {
            let node_to_add = process_substitution(transformer, position);
            container.contents.push(node_to_add);
        }
        LexerToken::Space(_) => {}
        LexerToken::CarriageReturn(_) => {}
        LexerToken::UnixNewLine(_) => {}
        LexerToken::Tab(_) => {}
        LexerToken::StartBrace(_) => {}
        LexerToken::EndBrace(_) => {}
        LexerToken::StartParen(_) => {}
        LexerToken::EndParen(_) => {}
        LexerToken::Not(_) => {}
        LexerToken::And(_) => {}
        LexerToken::Or(_) => {}
        LexerToken::Tag(_, _) => {}
        LexerToken::StartCommentMultiLine(_, _) => todo!(),
        LexerToken::EndCommentMultiLine(_, _) => todo!(),
        LexerToken::SingleLineCommentCharacter(_, _) => todo!(),
    }
    ApplicabilityExprKind::None(container)
}

#[cfg(test)]
mod tests {
    mod base {}
    mod all {}
    mod feature {
        use applicability::applic_tag::ApplicabilityTag;
        use applicability_lexer_base::applicability_structure::LexerToken;
        use applicability_parser_types::applic_tokens::{
            ApplicTokens::{self, NestedAnd, NestedNotAnd},
            ApplicabilityNestedAndTag, ApplicabilityNestedNotAndTag, ApplicabilityNoTag,
            ApplicabilityOrTag,
        };
        use pretty_assertions::assert_eq;

        use crate::{
            process_tokens,
            state_machine::StateMachine,
            tree::{
                ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition,
                ApplicabilityExprKind, ApplicabilityExprSubstitution, ApplicabilityExprTag,
                ApplicabilityKind::Feature, Text,
            },
            updatable::UpdatableValue,
        };

        #[test]
        fn test_text() {
            let input = vec![LexerToken::Text("abcd", ((0, 0), (4, 0)))];
            let mut sm = StateMachine::new(input.into_iter());
            let result = process_tokens(&mut sm);
            let expected = ApplicabilityExprKind::None(ApplicabilityExprContainer {
                contents: vec![ApplicabilityExprKind::Text(Text {
                    text: "abcd",
                    start_position: UpdatableValue::new((0, 0)),
                    end_position: UpdatableValue::new((4, 0)),
                })],
            });
            assert_eq!(result, expected)
        }

        #[test]
        fn test_basic_substitution() {
            let input = vec![
                LexerToken::Substitution(((0, 0), (4, 0))),
                LexerToken::StartBrace(((4, 0), (5, 0))),
                LexerToken::Tag("APPLIC_1", ((5, 0), (13, 0))),
                LexerToken::EndBrace(((13, 0), (14, 0))),
            ];
            let mut sm = StateMachine::new(input.into_iter());
            let result = process_tokens(&mut sm);
            let expected = ApplicabilityExprKind::None(ApplicabilityExprContainer {
                contents: vec![ApplicabilityExprKind::Substitution(
                    ApplicabilityExprSubstitution {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "APPLIC_1",
                                value: "Included".to_string(),
                            },
                            None,
                        ))],
                        start_position: UpdatableValue::new((0, 0)),
                        end_position: UpdatableValue::new((14, 0)),
                    },
                )],
            });
            assert_eq!(result, expected)
        }
        #[test]
        fn test_feature_block() {
            let input = vec![
                LexerToken::Feature(((0, 1), (7, 1))),
                LexerToken::Space(((7, 1), (8, 1))),
                LexerToken::StartBrace(((8, 1), (9, 1))),
                LexerToken::Tag("APPLIC_1", ((9, 1), (17, 1))),
                LexerToken::EndBrace(((17, 1), (18, 1))),
                LexerToken::Text("Some text here", ((18, 1), (32, 1))),
                LexerToken::Feature(((32, 1), (39, 1))),
                LexerToken::StartBrace(((32, 1), (33, 1))),
                LexerToken::Tag("APPLIC_2", ((33, 1), (41, 1))),
                LexerToken::EndBrace(((41, 1), (42, 1))),
                LexerToken::Text("Nested text here", ((49, 1), (65, 1))),
                LexerToken::EndFeature(((65, 1), (76, 1))),
                LexerToken::FeatureElse(((76, 1), (88, 1))),
                LexerToken::Text("Some other text here", ((88, 1), (108, 1))),
                LexerToken::EndFeature(((108, 1), (119, 1))),
                LexerToken::FeatureSwitch(((119, 1), (132, 1))),
                LexerToken::FeatureCase(((132, 1), (143, 1))),
                LexerToken::Space(((143, 1), (144, 1))),
                LexerToken::StartBrace(((144, 1), (145, 1))),
                LexerToken::Space(((145, 1), (146, 1))),
                LexerToken::Tag("APPLIC_1", ((146, 1), (154, 1))),
                LexerToken::Space(((154, 1), (155, 1))),
                LexerToken::EndBrace(((155, 1), (156, 1))),
                LexerToken::Text("case1", ((156, 1), (161, 1))),
                LexerToken::FeatureCase(((161, 1), (172, 1))),
                LexerToken::StartBrace(((172, 1), (173, 1))),
                LexerToken::Tag("APPLIC_2", ((173, 1), (181, 1))),
                LexerToken::EndBrace(((181, 1), (182, 1))),
                LexerToken::Text("case2", ((182, 1), (187, 1))),
                LexerToken::Feature(((187, 1), (194, 1))),
                LexerToken::StartBrace(((194, 1), (195, 1))),
                LexerToken::Tag("APPLIC_3", ((195, 1), (203, 1))),
                LexerToken::Space(((203, 1), (204, 1))),
                LexerToken::And(((204, 1), (205, 1))),
                LexerToken::Space(((205, 1), (206, 1))),
                LexerToken::Space(((206, 1), (207, 1))),
                LexerToken::Not(((208, 1), (209, 1))),
                LexerToken::StartParen(((209, 1), (210, 1))),
                LexerToken::Tag("APPLIC_4", ((210, 1), (218, 1))),
                LexerToken::Or(((218, 1), (219, 1))),
                LexerToken::Tag("APPLIC_5", ((219, 1), (227, 1))),
                LexerToken::EndParen(((227, 1), (228, 1))),
                LexerToken::EndBrace(((228, 1), (229, 1))),
                LexerToken::Text("feature1incase", ((229, 1), (243, 1))),
                LexerToken::EndFeature(((243, 1), (244, 1))),
                LexerToken::EndFeature(((244, 1), (245, 1))),
            ];
            let mut sm = StateMachine::new(input.into_iter());
            let result = process_tokens(&mut sm);
            let feature_in_case_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag(
                        ApplicabilityTag {
                            tag: "APPLIC_3",
                            value: "Included".to_string(),
                        },
                        None,
                    )),
                    ApplicTokens::NestedNotAnd(ApplicabilityNestedNotAndTag(
                        vec![
                            ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "APPLIC_4",
                                    value: "Included".to_string(),
                                },
                                None,
                            )),
                            ApplicTokens::Or(ApplicabilityOrTag(
                                ApplicabilityTag {
                                    tag: "APPLIC_5",
                                    value: "Included".to_string(),
                                },
                                None,
                            )),
                        ],
                        None,
                    )),
                ],
                kind: Feature,
                contents: vec![ApplicabilityExprKind::Text(Text {
                    text: "feature1incase",
                    start_position: UpdatableValue {
                        previous_value: (229, 1),
                        current_value: (229, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (243, 1),
                        current_value: (243, 1),
                    },
                })],
                start_position: UpdatableValue {
                    previous_value: (187, 1),
                    current_value: (187, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (194, 1),
                    current_value: (244, 1),
                },
            });
            let feature_case_1_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    ApplicabilityTag {
                        tag: "APPLIC_1",
                        value: "Included".to_string(),
                    },
                    None,
                ))],
                kind: Feature,
                contents: vec![ApplicabilityExprKind::Text(Text {
                    text: "case1",
                    start_position: UpdatableValue {
                        previous_value: (156, 1),
                        current_value: (156, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (161, 1),
                        current_value: (161, 1),
                    },
                })],
                start_position: UpdatableValue {
                    previous_value: (132, 1),
                    current_value: (132, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (143, 1),
                    current_value: (161, 1),
                },
            });
            let feature_case_2_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    ApplicabilityTag {
                        tag: "APPLIC_2",
                        value: "Included".to_string(),
                    },
                    None,
                ))],
                kind: Feature,
                contents: vec![
                    ApplicabilityExprKind::Text(Text {
                        text: "case2",
                        start_position: UpdatableValue {
                            previous_value: (182, 1),
                            current_value: (182, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (187, 1),
                            current_value: (187, 1),
                        },
                    }),
                    ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                        contents: vec![feature_in_case_expected],
                        start_position: UpdatableValue {
                            previous_value: (187, 1),
                            current_value: (187, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (0, 0),
                            current_value: (244, 1),
                        },
                    }),
                ],
                start_position: UpdatableValue {
                    previous_value: (161, 1),
                    current_value: (161, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (172, 1),
                    current_value: (245, 1),
                },
            });
            let feature_switch_expected =
                ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                    contents: vec![feature_case_1_expected, feature_case_2_expected],
                    start_position: UpdatableValue {
                        previous_value: (119, 1),
                        current_value: (119, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0),
                        current_value: (245, 1),
                    },
                });
            let feature_else_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![NestedNotAnd(ApplicabilityNestedNotAndTag(
                    vec![NestedAnd(ApplicabilityNestedAndTag(
                        vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "APPLIC_1",
                                value: "Included".to_string(),
                            },
                            None,
                        ))],
                        None,
                    ))],
                    None,
                ))],
                kind: Feature,
                contents: vec![ApplicabilityExprKind::Text(Text {
                    text: "Some other text here",
                    start_position: UpdatableValue {
                        previous_value: (88, 1),
                        current_value: (88, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (108, 1),
                        current_value: (108, 1),
                    },
                })],
                start_position: UpdatableValue {
                    previous_value: (76, 1),
                    current_value: (76, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (88, 1),
                    current_value: (119, 1),
                },
            });
            let nested_feature_expected =
                ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                    contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "APPLIC_2",
                                value: "Included".to_string(),
                            },
                            None,
                        ))],
                        kind: Feature,
                        contents: vec![ApplicabilityExprKind::Text(Text {
                            text: "Nested text here",
                            start_position: UpdatableValue {
                                previous_value: (49, 1),
                                current_value: (49, 1),
                            },
                            end_position: UpdatableValue {
                                previous_value: (65, 1),
                                current_value: (65, 1),
                            },
                        })],
                        start_position: UpdatableValue {
                            previous_value: (32, 1),
                            current_value: (32, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (39, 1),
                            current_value: (76, 1),
                        },
                    })],
                    start_position: UpdatableValue {
                        previous_value: (32, 1),
                        current_value: (32, 1),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0),
                        current_value: (76, 1),
                    },
                });
            let feature_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    ApplicabilityTag {
                        tag: "APPLIC_1",
                        value: "Included".to_string(),
                    },
                    None,
                ))],
                kind: Feature,
                contents: vec![
                    ApplicabilityExprKind::Text(Text {
                        text: "Some text here",
                        start_position: UpdatableValue {
                            previous_value: (18, 1),
                            current_value: (18, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (32, 1),
                            current_value: (32, 1),
                        },
                    }),
                    nested_feature_expected,
                ],
                start_position: UpdatableValue {
                    previous_value: (0, 1),
                    current_value: (0, 1),
                },
                end_position: UpdatableValue {
                    previous_value: (7, 1),
                    current_value: (76, 1),
                },
            });
            let expected = ApplicabilityExprKind::None(ApplicabilityExprContainer {
                contents: vec![
                    ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                        contents: vec![feature_expected, feature_else_expected],
                        start_position: UpdatableValue {
                            previous_value: (0, 1),
                            current_value: (0, 1),
                        },
                        end_position: UpdatableValue {
                            previous_value: (0, 0),
                            current_value: (119, 1),
                        },
                    }),
                    feature_switch_expected,
                ],
            });
            assert_eq!(result, expected)
        }
    }
    mod config {}
    mod config_group {}
}
