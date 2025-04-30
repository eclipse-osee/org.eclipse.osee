use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::applicability_structure::LexerToken;
use feature::{process_feature, process_feature_not, process_feature_switch};
use latch::LatchedValue;
use nom::Input;
use state_machine::StateMachine;
use tree::{ApplicabilityExprContainer, ApplicabilityExprKind, Text};

mod config;
mod config_group;
mod feature;
// mod flatten_ast;
// mod flatten_ast_state_machine;
// mod multi_line;
// mod non_terminated;
mod latch;
mod state_machine;
mod substitution;
mod tree;
// mod terminated;

// use std::fmt::Debug;

// use applicability::applic_tag::ApplicabilityTag;
// use applicability_lexer_base::applicability_structure::LexerToken;
// use flatten_ast::{FlattenApplicabilityAst, HasContents, HeadNode, TextNode};
// use flatten_ast_state_machine::FlattenStateMachine;
// use multi_line::remove_unnecessary_comments_multi_line_comment;
// use nom::Input;
// use non_terminated::remove_unnecessary_comments_non_terminated_comment;
// use terminated::remove_unnecessary_comments_terminated_comment;
// // /*
// // Some docs:
// // ``
// // Feature Switch
// // ``
// // ``
// // Feature Case[SOMETHING]
// // ``
// // Some text here
// // `` Feature Case[SOMETHING_ELSE] ``
// // Other text here
// // `` End Feature ``
// // ``Feature[OTHER_VALUE]``
// // Some text
// // `` End Feature ``
// // Switch should have
// //     height: 10 lines
// //     contents: len 3
// // Case Something should have
// //     height: 4 lines
// //     contents: "Some text here"
// // Case Something Else should have
// //     height: 2 lines
// //     contents: "Other text here"
// // Other value should have
// //     height: 3 lines
// //     contents: "Some text"
// // */
// pub fn flatten_to_end<I, Iter>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
// ) -> FlattenApplicabilityAst<I>
// where
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
// {
//     let mut head = HeadNode { contents: vec![] };
//     while transformer.next().is_some() {
//         match &transformer.current_token {
//             // LexerToken::StartCommentSingleLineTerminated(position) => {
//             //     remove_unnecessary_comments_terminated_comment(
//             //         transformer,
//             //         position.0,
//             //         Some(&mut head),
//             //     );
//             // }
//             // LexerToken::SingleLineCommentCharacter(position) => {
//             //     remove_unnecessary_comments_non_terminated_comment(
//             //         transformer,
//             //         position.0,
//             //         Some(&mut head),
//             //     );
//             // }
//             // LexerToken::StartCommentMultiLine(position) => {
//             //     remove_unnecessary_comments_multi_line_comment(
//             //         transformer,
//             //         position.0,
//             //         Some(&mut head),
//             //     );
//             // }
//             LexerToken::Text(content, position) => {
//                 head.push(FlattenApplicabilityAst::Text(TextNode {
//                     content: content.clone(),
//                     start_position: position.0,
//                     end_position: position.1,
//                 }));
//             }
//             _ => {}
//         }
//     }
//     //TODO: make sure this doesn't introduce any bugs
//     match &transformer.current_token {
//         // LexerToken::StartCommentSingleLineTerminated(position) => {
//         //     remove_unnecessary_comments_terminated_comment(
//         //         transformer,
//         //         position.0,
//         //         Some(&mut head),
//         //     );
//         // }
//         // LexerToken::SingleLineCommentCharacter(position) => {
//         //     remove_unnecessary_comments_non_terminated_comment(
//         //         transformer,
//         //         position.0,
//         //         Some(&mut head),
//         //     );
//         // }
//         // LexerToken::StartCommentMultiLine(position) => {
//         //     remove_unnecessary_comments_multi_line_comment(
//         //         transformer,
//         //         position.0,
//         //         Some(&mut head),
//         //     );
//         // }
//         LexerToken::Text(content, position) => {
//             head.push(FlattenApplicabilityAst::Text(TextNode {
//                 content: content.clone(),
//                 start_position: position.0,
//                 end_position: position.1,
//             }));
//         }
//         _ => {}
//     };
//     FlattenApplicabilityAst::Head(head)
// }
// #[cfg(test)]
// mod tests {
//     use applicability::applic_tag::ApplicabilityTag;
//     use applicability_lexer_base::applicability_structure::LexerToken;
//     use applicability_lexer_config_markdown::ApplicabiltyMarkdownLexerConfig;
//     use applicability_lexer_multi_stage_lexer::lexer::tokenize_comments;
//     use applicability_parser_types::applic_tokens::{ApplicTokens, ApplicabilityNoTag};
//     use nom_locate::LocatedSpan;

//     use crate::{
//         FlattenStateMachine,
//         flatten_ast::{
//             ApplicabilityNode, FlattenApplicabilityAst, HeadNode, PositionNode, SubstitutionNode,
//             TextNode,
//         },
//         flatten_to_end,
//     };

//     #[test]
//     fn base_comment_test() {
//         let doc = ApplicabiltyMarkdownLexerConfig::new();
//         let input = LocatedSpan::new_extra("``Test Text``", ((0, 0), (0, 0)));
//         let token_stream = tokenize_comments(&doc, input);
//         let mut parser =
//             FlattenStateMachine::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
//         let head = flatten_to_end(&mut parser);
//         let results = FlattenApplicabilityAst::Head(HeadNode {
//             contents: vec![FlattenApplicabilityAst::Text(TextNode {
//                 content: "``Test Text``",
//                 start_position: (0, 1),
//                 end_position: (13, 1),
//             })],
//         });
//         assert_eq!(head, results);
//     }

//     #[test]
//     fn feature_with_else_test() {
//         let doc = ApplicabiltyMarkdownLexerConfig::new();
//         let input = LocatedSpan::new_extra(
//             "``Random text Feature[ABCD] Other Random text``Text``Feature Else If[BCD]``Some text``Feature Else``Other text``End Feature``",
//             ((0, 0), (0, 0)),
//         );
//         let token_stream = tokenize_comments(&doc, input);
//         let mut parser =
//             FlattenStateMachine::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
//         let head = flatten_to_end(&mut parser);
//         let results = FlattenApplicabilityAst::Head(HeadNode {
//             contents: vec![
//                 FlattenApplicabilityAst::Feature(ApplicabilityNode {
//                     start_position: (14, 1),
//                     end_position: (47, 1),
//                     tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
//                         ApplicabilityTag {
//                             tag: "ABCD",
//                             value: "Included".to_string(),
//                         },
//                         None,
//                     ))],
//                 }),
//                 FlattenApplicabilityAst::Text(TextNode {
//                     start_position: (47, 1),
//                     end_position: (51, 1),
//                     content: "Text",
//                 }),
//                 FlattenApplicabilityAst::FeatureElseIf(ApplicabilityNode {
//                     start_position: (53, 1),
//                     end_position: (75, 1),
//                     tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
//                         ApplicabilityTag {
//                             tag: "BCD",
//                             value: "Included".to_string(),
//                         },
//                         None,
//                     ))],
//                 }),
//                 FlattenApplicabilityAst::Text(TextNode {
//                     start_position: (75, 1),
//                     end_position: (84, 1),
//                     content: "Some text",
//                 }),
//                 FlattenApplicabilityAst::FeatureElse(PositionNode {
//                     start_position: (86, 1),
//                     end_position: (100, 1),
//                 }),
//                 FlattenApplicabilityAst::Text(TextNode {
//                     start_position: (100, 1),
//                     end_position: (110, 1),
//                     content: "Other text",
//                 }),
//                 FlattenApplicabilityAst::EndFeature(PositionNode {
//                     start_position: (112, 1),
//                     end_position: (125, 1),
//                 }),
//             ],
//         });
//         assert_eq!(head, results);
//     }

//     #[test]
//     fn substitution_test() {
//         let doc = ApplicabiltyMarkdownLexerConfig::new();
//         let input = LocatedSpan::new_extra("``Eval[ABCD]``", ((0, 0), (0, 0)));
//         let token_stream = tokenize_comments(&doc, input);
//         let mut parser =
//             FlattenStateMachine::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
//         let head = flatten_to_end(&mut parser);
//         let results = FlattenApplicabilityAst::Head(HeadNode {
//             contents: vec![FlattenApplicabilityAst::Substitution(SubstitutionNode {
//                 start_position: (2, 1),
//                 end_position: (14, 1),
//                 tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
//                     ApplicabilityTag {
//                         tag: "ABCD",
//                         value: "Included".to_string(),
//                     },
//                     None,
//                 ))],
//             })],
//         });
//         assert_eq!(head, results);
//     }
// }
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
                    start_position: LatchedValue::new(position.0),
                    end_position: LatchedValue::new(position.1),
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
            LexerToken::Configuration(_) => todo!(),
            LexerToken::ConfigurationNot(_) => todo!(),
            LexerToken::ConfigurationSwitch(_) => todo!(),
            LexerToken::ConfigurationCase(_) => todo!(),
            LexerToken::ConfigurationElse(_) => todo!(),
            LexerToken::ConfigurationElseIf(_) => todo!(),
            LexerToken::EndConfiguration(_) => todo!(),
            LexerToken::ConfigurationGroup(_) => todo!(),
            LexerToken::ConfigurationGroupNot(_) => todo!(),
            LexerToken::ConfigurationGroupSwitch(_) => todo!(),
            LexerToken::ConfigurationGroupCase(_) => todo!(),
            LexerToken::ConfigurationGroupElse(_) => todo!(),
            LexerToken::ConfigurationGroupElseIf(_) => todo!(),
            LexerToken::EndConfigurationGroup(_) => todo!(),
            LexerToken::Substitution(_) => todo!(),
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
        }
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
            latch::LatchedValue,
            process_tokens,
            state_machine::StateMachine,
            tree::{
                ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition,
                ApplicabilityExprKind, ApplicabilityExprTag, ApplicabilityKind::Feature, Text,
            },
        };

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
            let expected = ApplicabilityExprKind::None(ApplicabilityExprContainer {
                contents: vec![
                    ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                        contents: vec![
                            ApplicabilityExprKind::Tag(ApplicabilityExprTag {
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
                                        start_position: LatchedValue {
                                            previous_value: (18, 1),
                                            current_value: (18, 1),
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (32, 1),
                                            current_value: (32, 1),
                                        },
                                    }),
                                    ApplicabilityExprKind::TagContainer(
                                        ApplicabilityExprContainerWithPosition {
                                            contents: vec![ApplicabilityExprKind::Tag(
                                                ApplicabilityExprTag {
                                                    tag: vec![ApplicTokens::NoTag(
                                                        ApplicabilityNoTag(
                                                            ApplicabilityTag {
                                                                tag: "APPLIC_2",
                                                                value: "Included".to_string(),
                                                            },
                                                            None,
                                                        ),
                                                    )],
                                                    kind: Feature,
                                                    contents: vec![ApplicabilityExprKind::Text(
                                                        Text {
                                                            text: "Nested text here",
                                                            start_position: LatchedValue {
                                                                previous_value: (49, 1),
                                                                current_value: (49, 1),
                                                            },
                                                            end_position: LatchedValue {
                                                                previous_value: (65, 1),
                                                                current_value: (65, 1),
                                                            },
                                                        },
                                                    )],
                                                    start_position: LatchedValue {
                                                        previous_value: (32, 1),
                                                        current_value: (32, 1),
                                                    },
                                                    end_position: LatchedValue {
                                                        previous_value: (39, 1),
                                                        current_value: (76, 1),
                                                    },
                                                },
                                            )],
                                            start_position: LatchedValue {
                                                previous_value: (32, 1),
                                                current_value: (32, 1),
                                            },
                                            end_position: LatchedValue {
                                                previous_value: (0, 0),
                                                current_value: (76, 1),
                                            },
                                        },
                                    ),
                                ],
                                start_position: LatchedValue {
                                    previous_value: (0, 1),
                                    current_value: (0, 1),
                                },
                                end_position: LatchedValue {
                                    previous_value: (7, 1),
                                    current_value: (76, 1),
                                },
                            }),
                            ApplicabilityExprKind::Tag(ApplicabilityExprTag {
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
                                    start_position: LatchedValue {
                                        previous_value: (88, 1),
                                        current_value: (88, 1),
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (108, 1),
                                        current_value: (108, 1),
                                    },
                                })],
                                start_position: LatchedValue {
                                    previous_value: (76, 1),
                                    current_value: (76, 1),
                                },
                                end_position: LatchedValue {
                                    previous_value: (88, 1),
                                    current_value: (119, 1),
                                },
                            }),
                        ],
                        start_position: LatchedValue {
                            previous_value: (0, 1),
                            current_value: (0, 1),
                        },
                        end_position: LatchedValue {
                            previous_value: (0, 0),
                            current_value: (119, 1),
                        },
                    }),
                    ApplicabilityExprKind::TagContainer(ApplicabilityExprContainerWithPosition {
                        contents: vec![
                            ApplicabilityExprKind::Tag(ApplicabilityExprTag {
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
                                    start_position: LatchedValue {
                                        previous_value: (156, 1),
                                        current_value: (156, 1),
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (161, 1),
                                        current_value: (161, 1),
                                    },
                                })],
                                start_position: LatchedValue {
                                    previous_value: (132, 1),
                                    current_value: (132, 1),
                                },
                                end_position: LatchedValue {
                                    previous_value: (143, 1),
                                    current_value: (161, 1),
                                },
                            }),
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
                                        text: "case2",
                                        start_position: LatchedValue {
                                            previous_value: (182, 1),
                                            current_value: (182, 1),
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (187, 1),
                                            current_value: (187, 1),
                                        },
                                    }),
                                    ApplicabilityExprKind::TagContainer(
                                        ApplicabilityExprContainerWithPosition {
                                            contents: vec![ApplicabilityExprKind::Tag(
                                                ApplicabilityExprTag {
                                                    tag: vec![
                                                        ApplicTokens::NoTag(ApplicabilityNoTag(
                                                            ApplicabilityTag {
                                                                tag: "APPLIC_3",
                                                                value: "Included".to_string(),
                                                            },
                                                            None,
                                                        )),
                                                        ApplicTokens::NestedNotAnd(
                                                            ApplicabilityNestedNotAndTag(
                                                                vec![
                                                                    ApplicTokens::NoTag(
                                                                        ApplicabilityNoTag(
                                                                            ApplicabilityTag {
                                                                                tag: "APPLIC_4",
                                                                                value: "Included"
                                                                                    .to_string(),
                                                                            },
                                                                            None,
                                                                        ),
                                                                    ),
                                                                    ApplicTokens::Or(
                                                                        ApplicabilityOrTag(
                                                                            ApplicabilityTag {
                                                                                tag: "APPLIC_5",
                                                                                value: "Included"
                                                                                    .to_string(),
                                                                            },
                                                                            None,
                                                                        ),
                                                                    ),
                                                                ],
                                                                None,
                                                            ),
                                                        ),
                                                    ],
                                                    kind: Feature,
                                                    contents: vec![ApplicabilityExprKind::Text(
                                                        Text {
                                                            text: "feature1incase",
                                                            start_position: LatchedValue {
                                                                previous_value: (229, 1),
                                                                current_value: (229, 1),
                                                            },
                                                            end_position: LatchedValue {
                                                                previous_value: (243, 1),
                                                                current_value: (243, 1),
                                                            },
                                                        },
                                                    )],
                                                    start_position: LatchedValue {
                                                        previous_value: (187, 1),
                                                        current_value: (187, 1),
                                                    },
                                                    end_position: LatchedValue {
                                                        previous_value: (194, 1),
                                                        current_value: (244, 1),
                                                    },
                                                },
                                            )],
                                            start_position: LatchedValue {
                                                previous_value: (187, 1),
                                                current_value: (187, 1),
                                            },
                                            end_position: LatchedValue {
                                                previous_value: (0, 0),
                                                current_value: (244, 1),
                                            },
                                        },
                                    ),
                                ],
                                start_position: LatchedValue {
                                    previous_value: (161, 1),
                                    current_value: (161, 1),
                                },
                                end_position: LatchedValue {
                                    previous_value: (172, 1),
                                    current_value: (245, 1),
                                },
                            }),
                        ],
                        start_position: LatchedValue {
                            previous_value: (119, 1),
                            current_value: (119, 1),
                        },
                        end_position: LatchedValue {
                            previous_value: (0, 0),
                            current_value: (245, 1),
                        },
                    }),
                ],
            });
            assert_eq!(result, expected)
        }
    }
    mod config {}
    mod config_group {}
}
