use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::applicability_structure::LexerToken;
mod config;
mod config_group;
mod feature;
mod flatten_ast;
mod flatten_ast_state_machine;
mod non_terminated;
mod substitution;
mod terminated;

use flatten_ast::{FlattenApplicabilityAst, HasContents, HeadNode, TextNode};
use flatten_ast_state_machine::FlattenStateMachine;
use nom::Input;
use non_terminated::remove_unnecessary_comments_non_terminated_comment;
use terminated::remove_unnecessary_comments_terminated_comment;
// /*
// Some docs:
// ``
// Feature Switch
// ``
// ``
// Feature Case[SOMETHING]
// ``
// Some text here
// `` Feature Case[SOMETHING_ELSE] ``
// Other text here
// `` End Feature ``
// ``Feature[OTHER_VALUE]``
// Some text
// `` End Feature ``
// Switch should have
//     height: 10 lines
//     contents: len 3
// Case Something should have
//     height: 4 lines
//     contents: "Some text here"
// Case Something Else should have
//     height: 2 lines
//     contents: "Other text here"
// Other value should have
//     height: 3 lines
//     contents: "Some text"
// */
pub fn flatten_to_end<I, Iter>(
    transformer: &mut FlattenStateMachine<I, Iter>,
) -> FlattenApplicabilityAst<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut head = HeadNode { contents: vec![] };
    while transformer.next().is_some() {
        match &transformer.current_token {
            LexerToken::StartCommentSingleLineTerminated(position) => {
                remove_unnecessary_comments_terminated_comment(
                    transformer,
                    position.0,
                    Some(&mut head),
                );
            }
            LexerToken::SingleLineCommentCharacter(position) => {
                remove_unnecessary_comments_non_terminated_comment(
                    transformer,
                    position.0,
                    Some(&mut head),
                );
            }
            LexerToken::Text(content, position) => {
                head.push(FlattenApplicabilityAst::Text(TextNode {
                    content: content.clone(),
                    start_position: position.0,
                    end_position: position.1,
                }));
            }
            _ => {}
        }
    }
    FlattenApplicabilityAst::Head(head)
}
#[cfg(test)]
mod tests {
    use applicability::applic_tag::ApplicabilityTag;
    use applicability_lexer_base::applicability_structure::LexerToken;
    use applicability_lexer_config_markdown::ApplicabiltyMarkdownLexerConfig;
    use applicability_lexer_multi_stage_lexer::lexer::tokenize_comments;
    use applicability_parser_types::applic_tokens::{ApplicTokens, ApplicabilityNoTag};
    use nom_locate::LocatedSpan;

    use crate::{
        FlattenStateMachine,
        flatten_ast::{
            ApplicabilityNode, CommentNode, FlattenApplicabilityAst, HeadNode, PositionNode,
            SubstitutionNode, TextNode,
        },
        flatten_to_end,
    };

    #[test]
    fn base_comment_test() {
        let doc = ApplicabiltyMarkdownLexerConfig::new();
        let input = LocatedSpan::new_extra("``Test Text``", ((0, 0), (0, 0)));
        let token_stream = tokenize_comments(&doc, input);
        let mut parser =
            FlattenStateMachine::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
        let head = flatten_to_end(&mut parser);
        let results = FlattenApplicabilityAst::Head(HeadNode {
            contents: vec![FlattenApplicabilityAst::Comment(CommentNode {
                start_position: (0, 1),
                end_position: (13, 1),
                contents: vec![FlattenApplicabilityAst::Text(TextNode {
                    content: "Test Text",
                    start_position: (2, 1),
                    end_position: (11, 1),
                })],
            })],
        });
        assert_eq!(head, results);
    }

    #[test]
    fn feature_with_else_test() {
        let doc = ApplicabiltyMarkdownLexerConfig::new();
        let input = LocatedSpan::new_extra(
            "``Random text Feature[ABCD] Other Random text``Text``Feature Else If[BCD]``Some text``Feature Else``Other text``End Feature``",
            ((0, 0), (0, 0)),
        );
        let token_stream = tokenize_comments(&doc, input);
        let mut parser =
            FlattenStateMachine::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
        let head = flatten_to_end(&mut parser);
        let results = FlattenApplicabilityAst::Head(HeadNode {
            contents: vec![
                FlattenApplicabilityAst::Feature(ApplicabilityNode {
                    start_position: (14, 1),
                    end_position: (47, 1),
                    tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                        ApplicabilityTag {
                            tag: "ABCD",
                            value: "Included".to_string(),
                        },
                        None,
                    ))],
                }),
                FlattenApplicabilityAst::Text(TextNode {
                    start_position: (47, 1),
                    end_position: (51, 1),
                    content: "Text",
                }),
                FlattenApplicabilityAst::FeatureElseIf(ApplicabilityNode {
                    start_position: (53, 1),
                    end_position: (75, 1),
                    tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                        ApplicabilityTag {
                            tag: "BCD",
                            value: "Included".to_string(),
                        },
                        None,
                    ))],
                }),
                FlattenApplicabilityAst::Text(TextNode {
                    start_position: (75, 1),
                    end_position: (84, 1),
                    content: "Some text",
                }),
                FlattenApplicabilityAst::FeatureElse(PositionNode {
                    start_position: (86, 1),
                    end_position: (100, 1),
                }),
                FlattenApplicabilityAst::Text(TextNode {
                    start_position: (100, 1),
                    end_position: (110, 1),
                    content: "Other text",
                }),
                FlattenApplicabilityAst::EndFeature(PositionNode {
                    start_position: (112, 1),
                    end_position: (125, 1),
                }),
            ],
        });
        assert_eq!(head, results);
    }

    #[test]
    fn substitution_test() {
        let doc = ApplicabiltyMarkdownLexerConfig::new();
        let input = LocatedSpan::new_extra("``Eval[ABCD]``", ((0, 0), (0, 0)));
        let token_stream = tokenize_comments(&doc, input);
        let mut parser =
            FlattenStateMachine::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
        let head = flatten_to_end(&mut parser);
        let results = FlattenApplicabilityAst::Head(HeadNode {
            contents: vec![FlattenApplicabilityAst::Substitution(SubstitutionNode {
                start_position: (2, 1),
                end_position: (14, 1),
                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    ApplicabilityTag {
                        tag: "ABCD",
                        value: "Included".to_string(),
                    },
                    None,
                ))],
            })],
        });
        assert_eq!(head, results);
    }
}
