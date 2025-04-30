use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use applicability_parser_types::applic_tokens::{ApplicTokens, ApplicabilityNestedNotAndTag};
use nom::Input;
use std::fmt::Debug;
use tracing::error;

use crate::{
    latch::LatchedValue,
    state_machine::StateMachine,
    tree::{
        ApplicabilityExprContainerWithPosition, ApplicabilityExprKind, ApplicabilityExprTag,
        ApplicabilityKind, Text,
    },
};

use super::{
    applic_else::process_feature_else, not::process_feature_not, switch::process_feature_switch,
};

pub(crate) fn process_feature<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> ApplicabilityExprKind<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default + Debug,
    ApplicabilityTag<I, String>: From<I>,
{
    let tag = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
        tag: transformer.process_tags(),
        kind: ApplicabilityKind::Feature,
        contents: vec![],
        start_position: LatchedValue::new(base_position.0),
        end_position: LatchedValue::new(base_position.1),
    });
    let mut container = ApplicabilityExprContainerWithPosition {
        contents: vec![tag],
        start_position: LatchedValue::new(base_position.0),
        end_position: LatchedValue::new((0, 0)),
    };
    while transformer.next().is_some()
        && !matches!(transformer.current_token, LexerToken::EndFeature(_))
    {
        let current_token = transformer.current_token.clone();
        println!("{:#?}", current_token);
        match &current_token {
            LexerToken::Nothing => {
                //discard
            }
            LexerToken::Illegal => {
                //discard
            }
            LexerToken::Identity => {
                //discard
            }
            LexerToken::Text(content, position) => {
                let text = Text {
                    text: content.to_owned(),
                    start_position: LatchedValue::new(position.0),
                    end_position: LatchedValue::new(position.1),
                };
                container.add_text_to_latest_tag(text);
            }
            LexerToken::TextToDiscard(_, _) => {
                //discard
            }
            LexerToken::Feature(position) => {
                let node_to_add = process_feature(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::FeatureNot(position) => {
                let node_to_add = process_feature_not(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::FeatureSwitch(position) => {
                let node_to_add = process_feature_switch(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::FeatureCase(position) => {
                //throw an error here
                error!(
                    "Feature Case found at {:#?} to {:#?} in Feature at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureElse(position) => {
                let tokens = container.get_total_tags();
                if !container.contents[0].has_end_position_changed() {
                    container.contents[0].set_end_position(position.0);
                }
                println!("process_feature process_feature_else");
                let node_to_add = process_feature_else(
                    transformer,
                    position,
                    vec![ApplicTokens::NestedNotAnd(ApplicabilityNestedNotAndTag(
                        tokens, None,
                    ))],
                );
                container.add_expr(node_to_add);
            }
            LexerToken::FeatureElseIf(position) => {
                let node_to_add = process_feature(transformer, position);
                if !container.contents[0].has_end_position_changed() {
                    container.contents[0].set_end_position(position.0);
                }
                container.add_expr(node_to_add);
            }
            LexerToken::EndFeature(position) => {
                error!(
                    "End Feature found at {:#?} to {:#?} in Feature at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Configuration(_) => todo!(),
            LexerToken::ConfigurationNot(_) => todo!(),
            LexerToken::ConfigurationSwitch(_) => todo!(),
            LexerToken::ConfigurationCase(position) => {
                //throw an error here
                error!(
                    "Configuration Case found at {:#?} to {:#?} in Feature at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationElse(_) => todo!(),
            LexerToken::ConfigurationElseIf(_) => todo!(),
            LexerToken::EndConfiguration(position) => {
                //throw an error here
                error!(
                    "End Configuration found at {:#?} to {:#?} in Feature at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroup(_) => todo!(),
            LexerToken::ConfigurationGroupNot(_) => todo!(),
            LexerToken::ConfigurationGroupSwitch(_) => todo!(),
            LexerToken::ConfigurationGroupCase(position) => {
                //throw an error here
                error!(
                    "Configuration Group Case found at {:#?} to {:#?} in Feature at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupElse(_) => todo!(),
            LexerToken::ConfigurationGroupElseIf(_) => todo!(),
            LexerToken::EndConfigurationGroup(position) => {
                //throw an error here
                error!(
                    "End Configuration found at {:#?} to {:#?} in Feature at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Substitution(_) => todo!(),
            LexerToken::Space(_) => {
                //discard
            }
            LexerToken::CarriageReturn(_) => {
                //discard TODO: remove
            }
            LexerToken::UnixNewLine(_) => {
                //discard TODO: remove
            }
            LexerToken::Tab(_) => {
                //discard
            }
            LexerToken::StartBrace(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::EndBrace(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::StartParen(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::EndParen(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::Not(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::And(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::Or(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::Tag(_, _) => {
                //discard it's an error if it gets here
            }
        }
    }
    if let LexerToken::EndFeature(x) = transformer.current_token {
        if !container.contents[0].has_end_position_changed() {
            container.contents[0].set_end_position(x.1);
        }
        container.end_position.next(x.1);
    }
    ApplicabilityExprKind::TagContainer(container)
}

#[cfg(test)]
mod tests {
    use applicability::applic_tag::ApplicabilityTag;
    use applicability_lexer_base::applicability_structure::LexerToken;
    use applicability_parser_types::applic_tokens::{
        ApplicTokens::{self, NestedAnd, NestedNotAnd},
        ApplicabilityNestedAndTag, ApplicabilityNestedNotAndTag, ApplicabilityNoTag,
    };

    use crate::{
        latch::LatchedValue,
        state_machine::StateMachine,
        tree::{
            ApplicabilityExprContainerWithPosition, ApplicabilityExprKind, ApplicabilityExprTag,
            ApplicabilityKind::Feature, Text,
        },
    };
    use pretty_assertions::assert_eq;

    use super::process_feature;

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
        ];
        let mut sm = StateMachine::new(input.into_iter());
        let result = process_feature(&mut sm, &((0, 0), (0, 0)));
        assert_eq!(
            result,
            ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                contents: vec![
                    ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "APPLIC_1",
                                value: "Included".to_string()
                            },
                            None
                        ))],
                        kind: Feature,
                        contents: vec![
                            ApplicabilityExprKind::Text(Text {
                                text: "Some text here",
                                start_position: LatchedValue {
                                    previous_value: (18, 1),
                                    current_value: (18, 1)
                                },
                                end_position: LatchedValue {
                                    previous_value: (32, 1),
                                    current_value: (32, 1)
                                }
                            }),
                            ApplicabilityExprKind::TagContainer(
                                ApplicabilityExprContainerWithPosition {
                                    contents: vec![ApplicabilityExprKind::Tag(
                                        ApplicabilityExprTag {
                                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                                ApplicabilityTag {
                                                    tag: "APPLIC_2",
                                                    value: "Included".to_string()
                                                },
                                                None
                                            ))],
                                            kind: Feature,
                                            contents: vec![ApplicabilityExprKind::Text(Text {
                                                text: "Nested text here",
                                                start_position: LatchedValue {
                                                    previous_value: (49, 1),
                                                    current_value: (49, 1)
                                                },
                                                end_position: LatchedValue {
                                                    previous_value: (65, 1),
                                                    current_value: (65, 1)
                                                }
                                            })],
                                            start_position: LatchedValue {
                                                previous_value: (32, 1),
                                                current_value: (32, 1)
                                            },
                                            end_position: LatchedValue {
                                                previous_value: (39, 1),
                                                current_value: (76, 1)
                                            }
                                        }
                                    )],
                                    start_position: LatchedValue {
                                        previous_value: (32, 1),
                                        current_value: (32, 1)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (0, 0),
                                        current_value: (76, 1)
                                    }
                                }
                            )
                        ],
                        start_position: LatchedValue {
                            previous_value: (0, 0),
                            current_value: (0, 0)
                        },
                        end_position: LatchedValue {
                            previous_value: (0, 0),
                            current_value: (76, 1)
                        }
                    }),
                    ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                        tag: vec![NestedNotAnd(ApplicabilityNestedNotAndTag(
                            vec![NestedAnd(ApplicabilityNestedAndTag(
                                vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                None
                            ))],
                            None
                        ))],
                        kind: Feature,
                        contents: vec![ApplicabilityExprKind::Text(Text {
                            text: "Some other text here",
                            start_position: LatchedValue {
                                previous_value: (88, 1),
                                current_value: (88, 1)
                            },
                            end_position: LatchedValue {
                                previous_value: (108, 1),
                                current_value: (108, 1)
                            }
                        })],
                        start_position: LatchedValue {
                            previous_value: (76, 1),
                            current_value: (76, 1)
                        },
                        end_position: LatchedValue {
                            previous_value: (88, 1),
                            current_value: (119, 1)
                        }
                    })
                ],
                start_position: LatchedValue {
                    previous_value: (0, 0),
                    current_value: (0, 0)
                },
                end_position: LatchedValue {
                    previous_value: (0, 0),
                    current_value: (119, 1)
                }
            })
        )
    }

    #[test]
    fn test_feature_block_with_embedded_switch() {
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
            LexerToken::FeatureSwitch(((65, 1), (79, 1))),
            LexerToken::FeatureCase(((79, 1), (91, 1))),
            LexerToken::StartBrace(((91, 1), (92, 1))),
            LexerToken::Tag("APPLIC_3", ((92, 1), (99, 1))),
            LexerToken::EndBrace(((99, 1), (100, 1))),
            LexerToken::Text("abcd", ((100, 1), (104, 1))),
            LexerToken::FeatureCase(((104, 1), (116, 1))),
            LexerToken::StartBrace(((116, 1), (117, 1))),
            LexerToken::Tag("APPLIC_4", ((117, 1), (124, 1))),
            LexerToken::EndBrace(((124, 1), (125, 1))),
            LexerToken::Text("efg", ((125, 1), (128, 1))),
            LexerToken::EndFeature(((128, 1), (139, 1))),
            LexerToken::FeatureElse(((139, 1), (151, 1))),
            LexerToken::Text("Some other text here", ((151, 1), (171, 1))),
            LexerToken::EndFeature(((171, 1), (182, 1))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        let result = process_feature(&mut sm, &((0, 0), (0, 0)));
        assert_eq!(
            result,
            ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                    tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                        ApplicabilityTag {
                            tag: "APPLIC_1",
                            value: "Included".to_string()
                        },
                        None
                    ))],
                    kind: Feature,
                    contents: vec![
                        ApplicabilityExprKind::Text(Text {
                            text: "Some text here",
                            start_position: LatchedValue {
                                previous_value: (18, 1),
                                current_value: (18, 1)
                            },
                            end_position: LatchedValue {
                                previous_value: (32, 1),
                                current_value: (32, 1)
                            }
                        }),
                        ApplicabilityExprKind::TagContainer(
                            ApplicabilityExprContainerWithPosition {
                                contents: vec![
                                    ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                            ApplicabilityTag {
                                                tag: "APPLIC_2",
                                                value: "Included".to_string()
                                            },
                                            None
                                        ))],
                                        kind: Feature,
                                        contents: vec![
                                            ApplicabilityExprKind::Text(Text {
                                                text: "Nested text here",
                                                start_position: LatchedValue {
                                                    previous_value: (49, 1),
                                                    current_value: (49, 1)
                                                },
                                                end_position: LatchedValue {
                                                    previous_value: (65, 1),
                                                    current_value: (65, 1)
                                                }
                                            }),
                                            ApplicabilityExprKind::TagContainer(
                                                ApplicabilityExprContainerWithPosition {
                                                    contents: vec![
                                                        ApplicabilityExprKind::Tag(
                                                            ApplicabilityExprTag {
                                                                tag: vec![ApplicTokens::NoTag(
                                                                    ApplicabilityNoTag(
                                                                        ApplicabilityTag {
                                                                            tag: "APPLIC_3",
                                                                            value: "Included"
                                                                                .to_string()
                                                                        },
                                                                        None
                                                                    )
                                                                )],
                                                                kind: Feature,
                                                                contents: vec![
                                                                ApplicabilityExprKind::Text(Text {
                                                                    text: "abcd",
                                                                    start_position: LatchedValue {
                                                                        previous_value: (100, 1),
                                                                        current_value: (100, 1)
                                                                    },
                                                                    end_position: LatchedValue {
                                                                        previous_value: (104, 1),
                                                                        current_value: (104, 1)
                                                                    }
                                                                })
                                                            ],
                                                                start_position: LatchedValue {
                                                                    previous_value: (79, 1),
                                                                    current_value: (79, 1)
                                                                },
                                                                end_position: LatchedValue {
                                                                    previous_value: (91, 1),
                                                                    current_value: (104, 1)
                                                                }
                                                            }
                                                        ),
                                                        ApplicabilityExprKind::Tag(
                                                            ApplicabilityExprTag {
                                                                tag: vec![ApplicTokens::NoTag(
                                                                    ApplicabilityNoTag(
                                                                        ApplicabilityTag {
                                                                            tag: "APPLIC_4",
                                                                            value: "Included"
                                                                                .to_string()
                                                                        },
                                                                        None
                                                                    )
                                                                )],
                                                                kind: Feature,
                                                                contents: vec![
                                                                ApplicabilityExprKind::Text(Text {
                                                                    text: "efg",
                                                                    start_position: LatchedValue {
                                                                        previous_value: (125, 1),
                                                                        current_value: (125, 1)
                                                                    },
                                                                    end_position: LatchedValue {
                                                                        previous_value: (128, 1),
                                                                        current_value: (128, 1)
                                                                    }
                                                                })
                                                            ],
                                                                start_position: LatchedValue {
                                                                    previous_value: (104, 1),
                                                                    current_value: (104, 1)
                                                                },
                                                                end_position: LatchedValue {
                                                                    previous_value: (116, 1),
                                                                    current_value: (139, 1)
                                                                }
                                                            }
                                                        )
                                                    ],
                                                    start_position: LatchedValue {
                                                        previous_value: (65, 1),
                                                        current_value: (65, 1)
                                                    },
                                                    end_position: LatchedValue {
                                                        previous_value: (0, 0),
                                                        current_value: (139, 1)
                                                    }
                                                }
                                            )
                                        ],
                                        start_position: LatchedValue {
                                            previous_value: (32, 1),
                                            current_value: (32, 1)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (39, 1),
                                            current_value: (139, 1)
                                        }
                                    }),
                                    ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                        tag: vec![NestedNotAnd(ApplicabilityNestedNotAndTag(
                                            vec![NestedAnd(ApplicabilityNestedAndTag(
                                                vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                                    ApplicabilityTag {
                                                        tag: "APPLIC_2",
                                                        value: "Included".to_string(),
                                                    },
                                                    None,
                                                ),),],
                                                None,
                                            ),),],
                                            None,
                                        ),),],
                                        kind: Feature,
                                        contents: vec![ApplicabilityExprKind::Text(Text {
                                            text: "Some other text here",
                                            start_position: LatchedValue {
                                                previous_value: (151, 1,),
                                                current_value: (151, 1,),
                                            },
                                            end_position: LatchedValue {
                                                previous_value: (171, 1,),
                                                current_value: (171, 1,),
                                            },
                                        },),],
                                        start_position: LatchedValue {
                                            previous_value: (139, 1,),
                                            current_value: (139, 1,),
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (151, 1,),
                                            current_value: (182, 1,),
                                        },
                                    },),
                                ],
                                start_position: LatchedValue {
                                    previous_value: (32, 1),
                                    current_value: (32, 1)
                                },
                                end_position: LatchedValue {
                                    previous_value: (0, 0),
                                    current_value: (182, 1)
                                }
                            }
                        )
                    ],
                    start_position: LatchedValue {
                        previous_value: (0, 0),
                        current_value: (0, 0)
                    },
                    end_position: LatchedValue {
                        previous_value: (0, 0),
                        current_value: (182, 1)
                    }
                }),],
                start_position: LatchedValue {
                    previous_value: (0, 0),
                    current_value: (0, 0)
                },
                end_position: LatchedValue {
                    previous_value: (0, 0),
                    current_value: (182, 1)
                }
            })
        )
    }
}
