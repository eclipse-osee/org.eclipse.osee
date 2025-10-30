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
use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use applicability_parser_errors::AstTransformError;
use nom::Input;
use std::fmt::Debug;

use crate::{
    config::{process_config, process_config_not, process_config_switch},
    config_group::{process_config_group, process_config_group_not, process_config_group_switch},
    state_machine::StateMachine,
    substitution::process_substitution,
    tree::{
        ApplicabilityExprContainerWithPosition, ApplicabilityExprKind, ApplicabilityExprTag,
        ApplicabilityKind, Text,
    },
    updatable::UpdatableValue,
};

use super::{
    applic_else::process_feature_else, not::process_feature_not, switch::process_feature_switch,
};

pub(crate) fn process_feature<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> Result<ApplicabilityExprKind<I>, AstTransformError>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default + Debug,
    ApplicabilityTag<I, String>: From<I>,
{
    let tag = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
        tag: transformer.process_tags(),
        kind: ApplicabilityKind::Feature,
        contents: vec![],
        start_position: UpdatableValue::new(base_position.0),
        end_position: UpdatableValue::new(base_position.1),
    });
    let mut container = ApplicabilityExprContainerWithPosition {
        contents: vec![tag],
        start_position: UpdatableValue::new(base_position.0),
        end_position: UpdatableValue::new((0, 0, 0)),
    };
    while transformer.next().is_some()
        && !matches!(transformer.current_token, LexerToken::EndFeature(_))
    {
        let current_token = transformer.current_token.clone();
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
                    start_position: UpdatableValue::new(position.0),
                    end_position: UpdatableValue::new(position.1),
                };
                container.add_text_to_latest_tag(text);
            }
            LexerToken::TextToDiscard(_, _) => {
                //discard
            }
            LexerToken::Feature(position) => {
                let node_to_add = process_feature(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureNot(position) => {
                let node_to_add = process_feature_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureSwitch(position) => {
                let node_to_add = process_feature_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureCase(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedFeatureCase(*position));
            }
            LexerToken::FeatureElse(position) => {
                let tokens = container.get_total_tags();
                if !container.contents[0].has_end_position_changed() {
                    container.contents[0].set_end_position(position.0);
                }
                let node_to_add = process_feature_else(transformer, position, tokens);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
                break;
            }
            LexerToken::FeatureElseIf(position) => {
                let node_to_add = process_feature(transformer, position);
                if !container.contents[0].has_end_position_changed() {
                    container.contents[0].set_end_position(position.0);
                }
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::EndFeature(position) => {
                return Err(AstTransformError::UnexpectedEndFeature(*position));
            }
            LexerToken::Configuration(position) => {
                let node_to_add = process_config(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationNot(position) => {
                let node_to_add = process_config_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationSwitch(position) => {
                let node_to_add = process_config_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationCase(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationCase(*position));
            }
            LexerToken::ConfigurationElse(position) => {
                return Err(AstTransformError::UnexpectedConfigurationElse(*position));
            }
            LexerToken::ConfigurationElseIf(position) => {
                return Err(AstTransformError::UnexpectedConfigurationElseIf(*position));
            }
            LexerToken::EndConfiguration(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedEndConfiguration(*position));
            }
            LexerToken::ConfigurationGroup(position) => {
                let node_to_add = process_config_group(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupNot(position) => {
                let node_to_add = process_config_group_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                let node_to_add = process_config_group_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupCase(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroupCase(
                    *position,
                ));
            }
            LexerToken::ConfigurationGroupElse(position) => {
                return Err(AstTransformError::UnexpectedConfigurationGroupElse(
                    *position,
                ));
            }
            LexerToken::ConfigurationGroupElseIf(position) => {
                return Err(AstTransformError::UnexpectedConfigurationGroupElseIf(
                    *position,
                ));
            }
            LexerToken::EndConfigurationGroup(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedEndConfiguration(*position));
            }
            LexerToken::Substitution(position) => {
                let node_to_add = process_substitution(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.add_expr_to_latest_tag(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
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
            LexerToken::StartCommentMultiLine(_, _) => todo!(),
            LexerToken::EndCommentMultiLine(_, _) => todo!(),
            LexerToken::SingleLineCommentCharacter(_, _) => todo!(),
        }
    }
    if let LexerToken::EndFeature(x) = transformer.current_token {
        if !container.contents[0].has_end_position_changed() {
            container.contents[0].set_end_position(x.1);
        }
        container.end_position.next(x.1);
    } else {
        return Err(AstTransformError::MissingEndFeature((
            transformer.current_token.clone().get_start_position(),
            transformer.current_token.clone().get_end_position(),
        )));
    }
    Ok(ApplicabilityExprKind::TagContainer(container))
}

#[cfg(test)]
mod tests {
    use applicability::applic_tag::ApplicabilityTag;
    use applicability_lexer_base::applicability_structure::LexerToken;
    use applicability_parser_types::applic_tokens::{
        ApplicTokens::{self, NestedNotOr},
        ApplicabilityNestedNotOrTag, ApplicabilityNoTag,
    };

    use crate::{
        state_machine::StateMachine,
        tree::{
            ApplicabilityExprContainerWithPosition, ApplicabilityExprKind, ApplicabilityExprTag,
            ApplicabilityKind::Feature, Text,
        },
        updatable::UpdatableValue,
    };
    use pretty_assertions::assert_eq;

    use super::process_feature;

    #[test]
    fn test_feature_block() {
        let input = vec![
            LexerToken::Feature(((0, 1, 0), (7, 1, 0))),
            LexerToken::Space(((7, 1, 0), (8, 1, 0))),
            LexerToken::StartBrace(((8, 1, 0), (9, 1, 0))),
            LexerToken::Tag("APPLIC_1", ((9, 1, 0), (17, 1, 0))),
            LexerToken::EndBrace(((17, 1, 0), (18, 1, 0))),
            LexerToken::Text("Some text here", ((18, 1, 0), (32, 1, 0))),
            LexerToken::Feature(((32, 1, 0), (39, 1, 0))),
            LexerToken::StartBrace(((32, 1, 0), (33, 1, 0))),
            LexerToken::Tag("APPLIC_2", ((33, 1, 0), (41, 1, 0))),
            LexerToken::EndBrace(((41, 1, 0), (42, 1, 0))),
            LexerToken::Text("Nested text here", ((49, 1, 0), (65, 1, 0))),
            LexerToken::EndFeature(((65, 1, 0), (76, 1, 0))),
            LexerToken::FeatureElse(((76, 1, 0), (88, 1, 0))),
            LexerToken::Text("Some other text here", ((88, 1, 0), (108, 1, 0))),
            LexerToken::EndFeature(((108, 1, 0), (119, 1, 0))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        let result = process_feature(&mut sm, &((0, 0, 0), (0, 0, 0)));
        assert_eq!(
            result,
            Ok(ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
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
                                    start_position: UpdatableValue {
                                        previous_value: (18, 1, 0),
                                        current_value: (18, 1, 0)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (32, 1, 0),
                                        current_value: (32, 1, 0)
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
                                                    start_position: UpdatableValue {
                                                        previous_value: (49, 1, 0),
                                                        current_value: (49, 1, 0)
                                                    },
                                                    end_position: UpdatableValue {
                                                        previous_value: (65, 1, 0),
                                                        current_value: (65, 1, 0)
                                                    }
                                                })],
                                                start_position: UpdatableValue {
                                                    previous_value: (32, 1, 0),
                                                    current_value: (32, 1, 0)
                                                },
                                                end_position: UpdatableValue {
                                                    previous_value: (39, 1, 0),
                                                    current_value: (76, 1, 0)
                                                }
                                            }
                                        )],
                                        start_position: UpdatableValue {
                                            previous_value: (32, 1, 0),
                                            current_value: (32, 1, 0)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (0, 0, 0),
                                            current_value: (76, 1, 0)
                                        }
                                    }
                                )
                            ],
                            start_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (0, 0, 0)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (76, 1, 0)
                            }
                        }),
                        ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                            tag: vec![NestedNotOr(ApplicabilityNestedNotOrTag(
                                vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                None
                            ))],
                            kind: Feature,
                            contents: vec![ApplicabilityExprKind::Text(Text {
                                text: "Some other text here",
                                start_position: UpdatableValue {
                                    previous_value: (88, 1, 0),
                                    current_value: (88, 1, 0)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (108, 1, 0),
                                    current_value: (108, 1, 0)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (76, 1, 0),
                                current_value: (76, 1, 0)
                            },
                            end_position: UpdatableValue {
                                previous_value: (88, 1, 0),
                                current_value: (119, 1, 0)
                            }
                        })
                    ],
                    start_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (0, 0, 0)
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (119, 1, 0)
                    }
                }
            ))
        )
    }

    #[test]
    fn test_feature_block_with_embedded_switch() {
        let input = vec![
            LexerToken::Feature(((0, 1, 0), (7, 1, 0))),
            LexerToken::Space(((7, 1, 0), (8, 1, 0))),
            LexerToken::StartBrace(((8, 1, 0), (9, 1, 0))),
            LexerToken::Tag("APPLIC_1", ((9, 1, 0), (17, 1, 0))),
            LexerToken::EndBrace(((17, 1, 0), (18, 1, 0))),
            LexerToken::Text("Some text here", ((18, 1, 0), (32, 1, 0))),
            LexerToken::Feature(((32, 1, 0), (39, 1, 0))),
            LexerToken::StartBrace(((32, 1, 0), (33, 1, 0))),
            LexerToken::Tag("APPLIC_2", ((33, 1, 0), (41, 1, 0))),
            LexerToken::EndBrace(((41, 1, 0), (42, 1, 0))),
            LexerToken::Text("Nested text here", ((49, 1, 0), (65, 1, 0))),
            LexerToken::FeatureSwitch(((65, 1, 0), (79, 1, 0))),
            LexerToken::FeatureCase(((79, 1, 0), (91, 1, 0))),
            LexerToken::StartBrace(((91, 1, 0), (92, 1, 0))),
            LexerToken::Tag("APPLIC_3", ((92, 1, 0), (99, 1, 0))),
            LexerToken::EndBrace(((99, 1, 0), (100, 1, 0))),
            LexerToken::Text("abcd", ((100, 1, 0), (104, 1, 0))),
            LexerToken::FeatureCase(((104, 1, 0), (116, 1, 0))),
            LexerToken::StartBrace(((116, 1, 0), (117, 1, 0))),
            LexerToken::Tag("APPLIC_4", ((117, 1, 0), (124, 1, 0))),
            LexerToken::EndBrace(((124, 1, 0), (125, 1, 0))),
            LexerToken::Text("efg", ((125, 1, 0), (128, 1, 0))),
            LexerToken::EndFeature(((128, 1, 0), (139, 1, 0))),
            LexerToken::FeatureElse(((139, 1, 0), (151, 1, 0))),
            LexerToken::Text("Some other text here", ((151, 1, 0), (171, 1, 0))),
            LexerToken::EndFeature(((171, 1, 0), (182, 1, 0))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        let result = process_feature(&mut sm, &((0, 0, 0), (0, 0, 0)));
        let feature_else_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
            tag: vec![NestedNotOr(ApplicabilityNestedNotOrTag(
                vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    ApplicabilityTag {
                        tag: "APPLIC_2",
                        value: "Included".to_string(),
                    },
                    None,
                ))],
                None,
            ))],
            kind: Feature,
            contents: vec![ApplicabilityExprKind::Text(Text {
                text: "Some other text here",
                start_position: UpdatableValue {
                    previous_value: (151, 1, 0),
                    current_value: (151, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (171, 1, 0),
                    current_value: (171, 1, 0),
                },
            })],
            start_position: UpdatableValue {
                previous_value: (139, 1, 0),
                current_value: (139, 1, 0),
            },
            end_position: UpdatableValue {
                previous_value: (151, 1, 0),
                current_value: (182, 1, 0),
            },
        });
        let feature_case_1_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                ApplicabilityTag {
                    tag: "APPLIC_3",
                    value: "Included".to_string(),
                },
                None,
            ))],
            kind: Feature,
            contents: vec![ApplicabilityExprKind::Text(Text {
                text: "abcd",
                start_position: UpdatableValue {
                    previous_value: (100, 1, 0),
                    current_value: (100, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (104, 1, 0),
                    current_value: (104, 1, 0),
                },
            })],
            start_position: UpdatableValue {
                previous_value: (79, 1, 0),
                current_value: (79, 1, 0),
            },
            end_position: UpdatableValue {
                previous_value: (91, 1, 0),
                current_value: (104, 1, 0),
            },
        });
        let feature_case_2_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                ApplicabilityTag {
                    tag: "APPLIC_4",
                    value: "Included".to_string(),
                },
                None,
            ))],
            kind: Feature,
            contents: vec![ApplicabilityExprKind::Text(Text {
                text: "efg",
                start_position: UpdatableValue {
                    previous_value: (125, 1, 0),
                    current_value: (125, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (128, 1, 0),
                    current_value: (128, 1, 0),
                },
            })],
            start_position: UpdatableValue {
                previous_value: (104, 1, 0),
                current_value: (104, 1, 0),
            },
            end_position: UpdatableValue {
                previous_value: (116, 1, 0),
                current_value: (139, 1, 0),
            },
        });
        let feature_switch_expected =
            ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                contents: vec![feature_case_1_expected, feature_case_2_expected],
                start_position: UpdatableValue {
                    previous_value: (65, 1, 0),
                    current_value: (65, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (0, 0, 0),
                    current_value: (139, 1, 0),
                },
            });
        let nested_feature_expected =
            ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                contents: vec![
                    ApplicabilityExprKind::Tag(ApplicabilityExprTag {
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
                                text: "Nested text here",
                                start_position: UpdatableValue {
                                    previous_value: (49, 1, 0),
                                    current_value: (49, 1, 0),
                                },
                                end_position: UpdatableValue {
                                    previous_value: (65, 1, 0),
                                    current_value: (65, 1, 0),
                                },
                            }),
                            feature_switch_expected,
                        ],
                        start_position: UpdatableValue {
                            previous_value: (32, 1, 0),
                            current_value: (32, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (39, 1, 0),
                            current_value: (139, 1, 0),
                        },
                    }),
                    feature_else_expected,
                ],
                start_position: UpdatableValue {
                    previous_value: (32, 1, 0),
                    current_value: (32, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (0, 0, 0),
                    current_value: (182, 1, 0),
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
                        previous_value: (18, 1, 0),
                        current_value: (18, 1, 0),
                    },
                    end_position: UpdatableValue {
                        previous_value: (32, 1, 0),
                        current_value: (32, 1, 0),
                    },
                }),
                nested_feature_expected,
            ],
            start_position: UpdatableValue {
                previous_value: (0, 0, 0),
                current_value: (0, 0, 0),
            },
            end_position: UpdatableValue {
                previous_value: (0, 0, 0),
                current_value: (182, 1, 0),
            },
        });
        assert_eq!(
            result,
            Ok(ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
                    contents: vec![feature_expected,],
                    start_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (0, 0, 0)
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (182, 1, 0)
                    }
                }
            ))
        )
    }
    #[test]
    fn test_feature_block_with_arbitrary_spaces_between_feature_and_brace() {
        let input = vec![
            LexerToken::Feature(((0, 1, 0), (7, 1, 0))),
            LexerToken::Space(((7, 1, 0), (8, 1, 0))),
            LexerToken::Space(((8, 1, 0), (9, 1, 0))),
            LexerToken::Space(((9, 1, 0), (10, 1, 0))),
            LexerToken::StartBrace(((10, 1, 0), (11, 1, 0))),
            LexerToken::Tag("APPLIC_1", ((11, 1, 0), (19, 1, 0))),
            LexerToken::EndBrace(((19, 1, 0), (20, 1, 0))),
            LexerToken::Text("Some text here", ((20, 1, 0), (34, 1, 0))),
            LexerToken::Feature(((34, 1, 0), (41, 1, 0))),
            LexerToken::StartBrace(((34, 1, 0), (35, 1, 0))),
            LexerToken::Tag("APPLIC_2", ((35, 1, 0), (43, 1, 0))),
            LexerToken::EndBrace(((43, 1, 0), (44, 1, 0))),
            LexerToken::Text("Nested text here", ((51, 1, 0), (67, 1, 0))),
            LexerToken::EndFeature(((67, 1, 0), (78, 1, 0))),
            LexerToken::FeatureElse(((78, 1, 0), (90, 1, 0))),
            LexerToken::Text("Some other text here", ((90, 1, 0), (110, 1, 0))),
            LexerToken::EndFeature(((110, 1, 0), (121, 1, 0))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        let result = process_feature(&mut sm, &((0, 0, 0), (0, 0, 0)));
        assert_eq!(
            result,
            Ok(ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
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
                                    start_position: UpdatableValue {
                                        previous_value: (20, 1, 0),
                                        current_value: (20, 1, 0)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (34, 1, 0),
                                        current_value: (34, 1, 0)
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
                                                    start_position: UpdatableValue {
                                                        previous_value: (51, 1, 0),
                                                        current_value: (51, 1, 0)
                                                    },
                                                    end_position: UpdatableValue {
                                                        previous_value: (67, 1, 0),
                                                        current_value: (67, 1, 0)
                                                    }
                                                })],
                                                start_position: UpdatableValue {
                                                    previous_value: (34, 1, 0),
                                                    current_value: (34, 1, 0)
                                                },
                                                end_position: UpdatableValue {
                                                    previous_value: (41, 1, 0),
                                                    current_value: (78, 1, 0)
                                                }
                                            }
                                        )],
                                        start_position: UpdatableValue {
                                            previous_value: (34, 1, 0),
                                            current_value: (34, 1, 0)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (0, 0, 0),
                                            current_value: (78, 1, 0)
                                        }
                                    }
                                )
                            ],
                            start_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (0, 0, 0)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (78, 1, 0)
                            }
                        }),
                        ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                            tag: vec![NestedNotOr(ApplicabilityNestedNotOrTag(
                                vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                None
                            ))],
                            kind: Feature,
                            contents: vec![ApplicabilityExprKind::Text(Text {
                                text: "Some other text here",
                                start_position: UpdatableValue {
                                    previous_value: (90, 1, 0),
                                    current_value: (90, 1, 0)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (110, 1, 0),
                                    current_value: (110, 1, 0)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (78, 1, 0),
                                current_value: (78, 1, 0)
                            },
                            end_position: UpdatableValue {
                                previous_value: (90, 1, 0),
                                current_value: (121, 1, 0)
                            }
                        })
                    ],
                    start_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (0, 0, 0)
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (121, 1, 0)
                    }
                }
            ))
        )
    }
    #[test]
    fn test_feature_block_with_arbitrary_spaces_between_feature_and_brace_and_tag() {
        let input = vec![
            LexerToken::Feature(((0, 1, 0), (7, 1, 0))),
            LexerToken::Space(((7, 1, 0), (8, 1, 0))),
            LexerToken::Space(((8, 1, 0), (9, 1, 0))),
            LexerToken::Space(((9, 1, 0), (10, 1, 0))),
            LexerToken::StartBrace(((10, 1, 0), (11, 1, 0))),
            LexerToken::Space(((7, 1, 0), (8, 1, 0))),
            LexerToken::Space(((8, 1, 0), (9, 1, 0))),
            LexerToken::Space(((9, 1, 0), (10, 1, 0))),
            LexerToken::Tag("APPLIC_1", ((11, 1, 0), (19, 1, 0))),
            LexerToken::EndBrace(((19, 1, 0), (20, 1, 0))),
            LexerToken::Text("Some text here", ((20, 1, 0), (34, 1, 0))),
            LexerToken::Feature(((34, 1, 0), (41, 1, 0))),
            LexerToken::StartBrace(((34, 1, 0), (35, 1, 0))),
            LexerToken::Tag("APPLIC_2", ((35, 1, 0), (43, 1, 0))),
            LexerToken::EndBrace(((43, 1, 0), (44, 1, 0))),
            LexerToken::Text("Nested text here", ((51, 1, 0), (67, 1, 0))),
            LexerToken::EndFeature(((67, 1, 0), (78, 1, 0))),
            LexerToken::FeatureElse(((78, 1, 0), (90, 1, 0))),
            LexerToken::Text("Some other text here", ((90, 1, 0), (110, 1, 0))),
            LexerToken::EndFeature(((110, 1, 0), (121, 1, 0))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        let result = process_feature(&mut sm, &((0, 0, 0), (0, 0, 0)));
        assert_eq!(
            result,
            Ok(ApplicabilityExprKind::TagContainer(
                ApplicabilityExprContainerWithPosition {
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
                                    start_position: UpdatableValue {
                                        previous_value: (20, 1, 0),
                                        current_value: (20, 1, 0)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (34, 1, 0),
                                        current_value: (34, 1, 0)
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
                                                    start_position: UpdatableValue {
                                                        previous_value: (51, 1, 0),
                                                        current_value: (51, 1, 0)
                                                    },
                                                    end_position: UpdatableValue {
                                                        previous_value: (67, 1, 0),
                                                        current_value: (67, 1, 0)
                                                    }
                                                })],
                                                start_position: UpdatableValue {
                                                    previous_value: (34, 1, 0),
                                                    current_value: (34, 1, 0)
                                                },
                                                end_position: UpdatableValue {
                                                    previous_value: (41, 1, 0),
                                                    current_value: (78, 1, 0)
                                                }
                                            }
                                        )],
                                        start_position: UpdatableValue {
                                            previous_value: (34, 1, 0),
                                            current_value: (34, 1, 0)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (0, 0, 0),
                                            current_value: (78, 1, 0)
                                        }
                                    }
                                )
                            ],
                            start_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (0, 0, 0)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0, 0),
                                current_value: (78, 1, 0)
                            }
                        }),
                        ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                            tag: vec![NestedNotOr(ApplicabilityNestedNotOrTag(
                                vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                None
                            ))],
                            kind: Feature,
                            contents: vec![ApplicabilityExprKind::Text(Text {
                                text: "Some other text here",
                                start_position: UpdatableValue {
                                    previous_value: (90, 1, 0),
                                    current_value: (90, 1, 0)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (110, 1, 0),
                                    current_value: (110, 1, 0)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (78, 1, 0),
                                current_value: (78, 1, 0)
                            },
                            end_position: UpdatableValue {
                                previous_value: (90, 1, 0),
                                current_value: (121, 1, 0)
                            }
                        })
                    ],
                    start_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (0, 0, 0)
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (121, 1, 0)
                    }
                }
            ))
        )
    }
}
