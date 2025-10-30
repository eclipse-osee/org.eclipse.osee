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
use applicability_lexer_base::applicability_structure::LexerToken;
use applicability_parser_errors::AstTransformError;
use config::{process_config, process_config_not, process_config_switch};
use config_group::{process_config_group, process_config_group_not, process_config_group_switch};
use feature::{process_feature, process_feature_not, process_feature_switch};
use nom::{AsBytes, Input, Offset};
use state_machine::StateMachine;
use std::fmt::Debug;
use substitution::process_substitution;
use tracing::error;
use tree::{ApplicabilityExprContainer, ApplicabilityExprKind, Text};
use updatable::UpdatableValue;

mod config;
mod config_group;
mod feature;

mod state_machine;
mod substitution;
pub mod tree;
pub mod updatable;

pub fn transform_tokens<I>(
    input: Vec<LexerToken<I>>,
) -> Result<ApplicabilityExprKind<I>, AstTransformError>
where
    I: Input + Send + Sync + Default + AsBytes + Offset + Debug,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut sm = StateMachine::new(input.into_iter());
    process_tokens(&mut sm)
}

pub fn process_tokens<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
) -> Result<ApplicabilityExprKind<I>, AstTransformError>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default + Debug,
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
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureNot(position) => {
                let node_to_add = process_feature_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureSwitch(position) => {
                let node_to_add = process_feature_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureCase(_) => {
                error!("Unexpected feature case, you may be missing a feature switch tag.")
            }
            LexerToken::FeatureElse(_) => {
                error!("Unexpected feature else, you may be missing a feature tag.")
            }
            LexerToken::FeatureElseIf(_) => {
                error!("Unexpected feature else if, you may be missing a feature tag.")
            }
            LexerToken::EndFeature(_) => {}
            LexerToken::Configuration(position) => {
                let node_to_add = process_config(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationNot(position) => {
                let node_to_add = process_config_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationSwitch(position) => {
                let node_to_add = process_config_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationCase(_) => {
                error!(
                    "Unexpected configuration case, you may be missing a configuration switch tag."
                )
            }
            LexerToken::ConfigurationElse(_) => {
                error!("Unexpected configuration else, you may be missing a configuration tag.")
            }
            LexerToken::ConfigurationElseIf(_) => {
                error!("Unexpected configuration else if, you may be missing a configuration tag.")
            }
            LexerToken::EndConfiguration(_) => {}
            LexerToken::ConfigurationGroup(position) => {
                let node_to_add = process_config_group(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupNot(position) => {
                let node_to_add = process_config_group_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                let node_to_add = process_config_group_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupCase(_) => error!(
                "Unexpected configuration group case, you may be missing a configuration group switch tag."
            ),
            LexerToken::ConfigurationGroupElse(_) => error!(
                "Unexpected configuration group else, you may be missing a configuration group tag."
            ),
            LexerToken::ConfigurationGroupElseIf(_) => error!(
                "Unexpected configuration group else if, you may be missing a configuration group tag."
            ),
            LexerToken::EndConfigurationGroup(_) => {}
            LexerToken::Substitution(position) => {
                let node_to_add = process_substitution(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
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
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::FeatureNot(position) => {
            let node_to_add = process_feature_not(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::FeatureSwitch(position) => {
            let node_to_add = process_feature_switch(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::FeatureCase(_) => {
            error!("Unexpected feature case, you may be missing a feature switch tag.")
        }
        LexerToken::FeatureElse(_) => {
            error!("Unexpected feature else, you may be missing a feature tag.")
        }
        LexerToken::FeatureElseIf(_) => {
            error!("Unexpected feature else if, you may be missing a feature tag.")
        }
        LexerToken::EndFeature(_) => {}
        LexerToken::Configuration(position) => {
            let node_to_add = process_config(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::ConfigurationNot(position) => {
            let node_to_add = process_config_not(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::ConfigurationSwitch(position) => {
            let node_to_add = process_config_switch(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::ConfigurationCase(_) => {
            error!("Unexpected configuration case, you may be missing a configuration switch tag.")
        }
        LexerToken::ConfigurationElse(_) => {
            error!("Unexpected configuration else, you may be missing a configuration tag.")
        }
        LexerToken::ConfigurationElseIf(_) => {
            error!("Unexpected configuration else if, you may be missing a configuration tag.")
        }
        LexerToken::EndConfiguration(_) => {}
        LexerToken::ConfigurationGroup(position) => {
            let node_to_add = process_config_group(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::ConfigurationGroupNot(position) => {
            let node_to_add = process_config_group_not(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::ConfigurationGroupSwitch(position) => {
            let node_to_add = process_config_group_switch(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
        }
        LexerToken::ConfigurationGroupCase(_) => error!(
            "Unexpected configuration group case, you may be missing a configuration group switch tag."
        ),
        LexerToken::ConfigurationGroupElse(_) => error!(
            "Unexpected configuration group else, you may be missing a configuration group tag."
        ),
        LexerToken::ConfigurationGroupElseIf(_) => error!(
            "Unexpected configuration group else if, you may be missing a configuration group tag."
        ),
        LexerToken::EndConfigurationGroup(_) => {}
        LexerToken::Substitution(position) => {
            let node_to_add = process_substitution(transformer, position);
            match node_to_add {
                Ok(successful_node) => {
                    container.contents.push(successful_node);
                }
                Err(e) => return Err(e),
            };
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
    Ok(ApplicabilityExprKind::None(container))
}

#[cfg(test)]
mod tests {
    mod base {}
    mod all {}
    mod feature {
        use applicability::applic_tag::ApplicabilityTag;
        use applicability_lexer_base::applicability_structure::LexerToken;
        use applicability_parser_types::applic_tokens::{
            ApplicTokens::{self, NestedNotOr},
            ApplicabilityNestedNotAndTag, ApplicabilityNestedNotOrTag, ApplicabilityNoTag,
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
            let input = vec![LexerToken::Text("abcd", ((0, 0, 0), (4, 0, 0)))];
            let mut sm = StateMachine::new(input.into_iter());
            let result = process_tokens(&mut sm);
            let expected = ApplicabilityExprKind::None(ApplicabilityExprContainer {
                contents: vec![ApplicabilityExprKind::Text(Text {
                    text: "abcd",
                    start_position: UpdatableValue::new((0, 0, 0)),
                    end_position: UpdatableValue::new((4, 0, 0)),
                })],
            });
            assert_eq!(result, Ok(expected))
        }

        #[test]
        fn test_basic_substitution() {
            let input = vec![
                LexerToken::Substitution(((0, 0, 0), (4, 0, 0))),
                LexerToken::StartBrace(((4, 0, 0), (5, 0, 0))),
                LexerToken::Tag("APPLIC_1", ((5, 0, 0), (13, 0, 0))),
                LexerToken::EndBrace(((13, 0, 0), (14, 0, 0))),
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
                        start_position: UpdatableValue::new((0, 0, 0)),
                        end_position: UpdatableValue::new((14, 0, 0)),
                    },
                )],
            });
            assert_eq!(result, Ok(expected))
        }
        #[test]
        fn test_feature_else_with_substitution() {
            let input = vec![
                LexerToken::Feature(((0, 1, 0), (7, 1, 0))),
                LexerToken::StartBrace(((7, 1, 0), (8, 1, 0))),
                LexerToken::Tag("Feature1", ((8, 1, 0), (17, 1, 0))),
                LexerToken::EndBrace(((17, 1, 0), (18, 1, 0))),
                LexerToken::Text("Some text related to feature 1.", ((17, 1, 0), (49, 1, 0))),
                LexerToken::Feature(((49, 1, 0), (56, 1, 0))),
                LexerToken::StartBrace(((56, 1, 0), (57, 1, 0))),
                LexerToken::Tag("Feature2", ((57, 1, 0), (66, 1, 0))),
                LexerToken::EndBrace(((66, 1, 0), (67, 1, 0))),
                LexerToken::Text("Some text related to feature 2.", ((66, 1, 0), (98, 1, 0))),
                LexerToken::FeatureElse(((98, 1, 0), (124, 1, 0))),
                LexerToken::Substitution(((124, 1, 0), (130, 1, 0))),
                LexerToken::StartBrace(((130, 1, 0), (131, 1, 0))),
                LexerToken::Tag("SomeSubstitutionTerm", ((131, 1, 0), (139, 1, 0))),
                LexerToken::EndBrace(((139, 1, 0), (144, 1, 0))),
                LexerToken::EndFeature(((144, 1, 0), (161, 1, 0))),
                LexerToken::Text(
                    "Some additional text related to feature 1.",
                    ((161, 1, 0), (226, 1, 0)),
                ),
                LexerToken::EndFeature(((226, 1, 0), (241, 1, 0))),
            ];
            let mut sm = StateMachine::new(input.into_iter());
            let result = process_tokens(&mut sm);
            let feature2_substitution_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                        ApplicabilityTag {
                            tag: "Feature2",
                            value: "Included".to_string(),
                        },
                        None,
                    ))],
                    None,
                ))],
                kind: Feature,
                contents: vec![ApplicabilityExprKind::Substitution(
                    ApplicabilityExprSubstitution {
                        tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                            ApplicabilityTag {
                                tag: "SomeSubstitutionTerm",
                                value: "Included".to_string(),
                            },
                            None,
                        ))],
                        start_position: UpdatableValue {
                            previous_value: (124, 1, 0),
                            current_value: (124, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (144, 1, 0),
                            current_value: (144, 1, 0),
                        },
                    },
                )],
                start_position: UpdatableValue {
                    previous_value: (98, 1, 0),
                    current_value: (98, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (124, 1, 0),
                    current_value: (161, 1, 0),
                },
            });

            let feature2_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    ApplicabilityTag {
                        tag: "Feature2",
                        value: "Included".to_string(),
                    },
                    None,
                ))],
                kind: Feature,
                contents: vec![ApplicabilityExprKind::Text(Text {
                    text: "Some text related to feature 2.",
                    start_position: UpdatableValue {
                        previous_value: (66, 1, 0),
                        current_value: (66, 1, 0),
                    },
                    end_position: UpdatableValue {
                        previous_value: (98, 1, 0),
                        current_value: (98, 1, 0),
                    },
                })],
                start_position: UpdatableValue {
                    previous_value: (49, 1, 0),
                    current_value: (49, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (56, 1, 0),
                    current_value: (98, 1, 0),
                },
            });

            let feature1_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    ApplicabilityTag {
                        tag: "Feature1",
                        value: "Included".to_string(),
                    },
                    None,
                ))],
                kind: Feature,
                contents: vec![
                    ApplicabilityExprKind::Text(Text {
                        text: "Some text related to feature 1.",
                        start_position: UpdatableValue {
                            previous_value: (17, 1, 0),
                            current_value: (17, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (49, 1, 0),
                            current_value: (49, 1, 0),
                        },
                    }),
                    ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                        contents: vec![feature2_expected, feature2_substitution_expected],
                        start_position: UpdatableValue {
                            previous_value: (49, 1, 0),
                            current_value: (49, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (0, 0, 0),
                            current_value: (161, 1, 0),
                        },
                    }),
                    ApplicabilityExprKind::Text(Text {
                        text: "Some additional text related to feature 1.",
                        start_position: UpdatableValue {
                            previous_value: (161, 1, 0),
                            current_value: (161, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (226, 1, 0),
                            current_value: (226, 1, 0),
                        },
                    }),
                ],
                start_position: UpdatableValue {
                    previous_value: (0, 1, 0),
                    current_value: (0, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (7, 1, 0),
                    current_value: (241, 1, 0),
                },
            });

            let expected = ApplicabilityExprKind::None(ApplicabilityExprContainer {
                contents: vec![ApplicabilityExprKind::TagContainer(
                    ApplicabilityExprContainerWithPosition {
                        contents: vec![feature1_expected],
                        start_position: UpdatableValue {
                            previous_value: (0, 1, 0),
                            current_value: (0, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (0, 0, 0),
                            current_value: (241, 1, 0),
                        },
                    },
                )],
            });
            assert_eq!(result, Ok(expected));
        }
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
                LexerToken::FeatureSwitch(((119, 1, 0), (132, 1, 0))),
                LexerToken::FeatureCase(((132, 1, 0), (143, 1, 0))),
                LexerToken::Space(((143, 1, 0), (144, 1, 0))),
                LexerToken::StartBrace(((144, 1, 0), (145, 1, 0))),
                LexerToken::Space(((145, 1, 0), (146, 1, 0))),
                LexerToken::Tag("APPLIC_1", ((146, 1, 0), (154, 1, 0))),
                LexerToken::Space(((154, 1, 0), (155, 1, 0))),
                LexerToken::EndBrace(((155, 1, 0), (156, 1, 0))),
                LexerToken::Text("case1", ((156, 1, 0), (161, 1, 0))),
                LexerToken::FeatureCase(((161, 1, 0), (172, 1, 0))),
                LexerToken::StartBrace(((172, 1, 0), (173, 1, 0))),
                LexerToken::Tag("APPLIC_2", ((173, 1, 0), (181, 1, 0))),
                LexerToken::EndBrace(((181, 1, 0), (182, 1, 0))),
                LexerToken::Text("case2", ((182, 1, 0), (187, 1, 0))),
                LexerToken::Feature(((187, 1, 0), (194, 1, 0))),
                LexerToken::StartBrace(((194, 1, 0), (195, 1, 0))),
                LexerToken::Tag("APPLIC_3", ((195, 1, 0), (203, 1, 0))),
                LexerToken::Space(((203, 1, 0), (204, 1, 0))),
                LexerToken::And(((204, 1, 0), (205, 1, 0))),
                LexerToken::Space(((205, 1, 0), (206, 1, 0))),
                LexerToken::Space(((206, 1, 0), (207, 1, 0))),
                LexerToken::Not(((208, 1, 0), (209, 1, 0))),
                LexerToken::StartParen(((209, 1, 0), (210, 1, 0))),
                LexerToken::Tag("APPLIC_4", ((210, 1, 0), (218, 1, 0))),
                LexerToken::Or(((218, 1, 0), (219, 1, 0))),
                LexerToken::Tag("APPLIC_5", ((219, 1, 0), (227, 1, 0))),
                LexerToken::EndParen(((227, 1, 0), (228, 1, 0))),
                LexerToken::EndBrace(((228, 1, 0), (229, 1, 0))),
                LexerToken::Text("feature1incase", ((229, 1, 0), (243, 1, 0))),
                LexerToken::EndFeature(((243, 1, 0), (244, 1, 0))),
                LexerToken::EndFeature(((244, 1, 0), (245, 1, 0))),
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
                        previous_value: (229, 1, 0),
                        current_value: (229, 1, 0),
                    },
                    end_position: UpdatableValue {
                        previous_value: (243, 1, 0),
                        current_value: (243, 1, 0),
                    },
                })],
                start_position: UpdatableValue {
                    previous_value: (187, 1, 0),
                    current_value: (187, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (194, 1, 0),
                    current_value: (244, 1, 0),
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
                        previous_value: (156, 1, 0),
                        current_value: (156, 1, 0),
                    },
                    end_position: UpdatableValue {
                        previous_value: (161, 1, 0),
                        current_value: (161, 1, 0),
                    },
                })],
                start_position: UpdatableValue {
                    previous_value: (132, 1, 0),
                    current_value: (132, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (143, 1, 0),
                    current_value: (161, 1, 0),
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
                            previous_value: (182, 1, 0),
                            current_value: (182, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (187, 1, 0),
                            current_value: (187, 1, 0),
                        },
                    }),
                    ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                        contents: vec![feature_in_case_expected],
                        start_position: UpdatableValue {
                            previous_value: (187, 1, 0),
                            current_value: (187, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (0, 0, 0),
                            current_value: (244, 1, 0),
                        },
                    }),
                ],
                start_position: UpdatableValue {
                    previous_value: (161, 1, 0),
                    current_value: (161, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (172, 1, 0),
                    current_value: (245, 1, 0),
                },
            });
            let feature_switch_expected =
                ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                    contents: vec![feature_case_1_expected, feature_case_2_expected],
                    start_position: UpdatableValue {
                        previous_value: (119, 1, 0),
                        current_value: (119, 1, 0),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (245, 1, 0),
                    },
                });
            let feature_else_expected = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                tag: vec![NestedNotOr(ApplicabilityNestedNotOrTag(
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                        ApplicabilityTag {
                            tag: "APPLIC_1",
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
                        previous_value: (88, 1, 0),
                        current_value: (88, 1, 0),
                    },
                    end_position: UpdatableValue {
                        previous_value: (108, 1, 0),
                        current_value: (108, 1, 0),
                    },
                })],
                start_position: UpdatableValue {
                    previous_value: (76, 1, 0),
                    current_value: (76, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (88, 1, 0),
                    current_value: (119, 1, 0),
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
                                previous_value: (49, 1, 0),
                                current_value: (49, 1, 0),
                            },
                            end_position: UpdatableValue {
                                previous_value: (65, 1, 0),
                                current_value: (65, 1, 0),
                            },
                        })],
                        start_position: UpdatableValue {
                            previous_value: (32, 1, 0),
                            current_value: (32, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (39, 1, 0),
                            current_value: (76, 1, 0),
                        },
                    })],
                    start_position: UpdatableValue {
                        previous_value: (32, 1, 0),
                        current_value: (32, 1, 0),
                    },
                    end_position: UpdatableValue {
                        previous_value: (0, 0, 0),
                        current_value: (76, 1, 0),
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
                    previous_value: (0, 1, 0),
                    current_value: (0, 1, 0),
                },
                end_position: UpdatableValue {
                    previous_value: (7, 1, 0),
                    current_value: (76, 1, 0),
                },
            });
            let expected = ApplicabilityExprKind::None(ApplicabilityExprContainer {
                contents: vec![
                    ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                        contents: vec![feature_expected, feature_else_expected],
                        start_position: UpdatableValue {
                            previous_value: (0, 1, 0),
                            current_value: (0, 1, 0),
                        },
                        end_position: UpdatableValue {
                            previous_value: (0, 0, 0),
                            current_value: (119, 1, 0),
                        },
                    }),
                    feature_switch_expected,
                ],
            });
            assert_eq!(result, Ok(expected))
        }
    }
    mod config {}
    mod config_group {}
}
