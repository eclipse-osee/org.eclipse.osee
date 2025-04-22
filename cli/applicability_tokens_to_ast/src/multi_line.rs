use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::Position};
use nom::Input;

use crate::{
    config::{
        remove_unnecessary_comments_configuration, remove_unnecessary_comments_configuration_case,
        remove_unnecessary_comments_configuration_else,
        remove_unnecessary_comments_configuration_elsif,
        remove_unnecessary_comments_configuration_end,
        remove_unnecessary_comments_configuration_not,
        remove_unnecessary_comments_configuration_switch,
    },
    config_group::{
        remove_unnecessary_comments_configuration_group,
        remove_unnecessary_comments_configuration_group_case,
        remove_unnecessary_comments_configuration_group_else,
        remove_unnecessary_comments_configuration_group_elsif,
        remove_unnecessary_comments_configuration_group_end,
        remove_unnecessary_comments_configuration_group_not,
        remove_unnecessary_comments_configuration_group_switch,
    },
    feature::{
        remove_unnecessary_comments_feature, remove_unnecessary_comments_feature_case,
        remove_unnecessary_comments_feature_else, remove_unnecessary_comments_feature_elsif,
        remove_unnecessary_comments_feature_end, remove_unnecessary_comments_feature_not,
        remove_unnecessary_comments_feature_switch,
    },
    flatten_ast::{CommentNode, FlattenApplicabilityAst, HasContents, TextNode},
    flatten_ast_state_machine::FlattenStateMachine,
    non_terminated::remove_unnecessary_comments_non_terminated_comment,
    substitution::remove_unnecessary_comments_substitution,
    terminated::remove_unnecessary_comments_terminated_comment,
};

pub fn remove_unnecessary_comments_multi_line_comment<I, Iter, X>(
    transformer: &mut FlattenStateMachine<I, Iter>,
    token_position: Position,
    root: Option<&mut X>,
) where
    X: HasContents<I> + Default,
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    let root_node = match root {
        Some(root) => root,
        None => &mut X::default(),
    };
    let mut comment_node = CommentNode::new(token_position);
    while !matches!(
        transformer.current_token,
        LexerToken::EndCommentMultiLine(_)
    ) && transformer.next().is_some()
    {
        match &transformer.current_token {
            LexerToken::StartCommentSingleLineTerminated(position) => {
                remove_unnecessary_comments_terminated_comment(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::SingleLineCommentCharacter(position) => {
                remove_unnecessary_comments_non_terminated_comment(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::StartCommentMultiLine(position) => {
                remove_unnecessary_comments_multi_line_comment(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::Text(content, position) => {
                comment_node.push(FlattenApplicabilityAst::Text(TextNode {
                    content: content.clone(),
                    start_position: position.0,
                    end_position: position.1,
                }));
            }
            LexerToken::Substitution(position) => {
                remove_unnecessary_comments_substitution(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::Feature(position) => {
                remove_unnecessary_comments_feature(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::FeatureElseIf(position) => {
                remove_unnecessary_comments_feature_elsif(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::FeatureCase(position) => {
                remove_unnecessary_comments_feature_case(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::FeatureNot(position) => {
                remove_unnecessary_comments_feature_not(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::FeatureElse(position) => {
                remove_unnecessary_comments_feature_else(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }
            LexerToken::FeatureSwitch(position) => {
                remove_unnecessary_comments_feature_switch(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }
            LexerToken::EndFeature(position) => {
                remove_unnecessary_comments_feature_end(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }
            LexerToken::Configuration(position) => {
                remove_unnecessary_comments_configuration(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationElseIf(position) => {
                remove_unnecessary_comments_configuration_elsif(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationCase(position) => {
                remove_unnecessary_comments_configuration_case(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationNot(position) => {
                remove_unnecessary_comments_configuration_not(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationElse(position) => {
                remove_unnecessary_comments_configuration_else(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationSwitch(position) => {
                remove_unnecessary_comments_configuration_switch(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }
            LexerToken::EndConfiguration(position) => {
                remove_unnecessary_comments_configuration_end(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }

            LexerToken::ConfigurationGroup(position) => {
                remove_unnecessary_comments_configuration_group(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationGroupElseIf(position) => {
                remove_unnecessary_comments_configuration_group_elsif(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationGroupCase(position) => {
                remove_unnecessary_comments_configuration_group_case(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationGroupNot(position) => {
                remove_unnecessary_comments_configuration_group_not(
                    transformer,
                    position.0,
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationGroupElse(position) => {
                remove_unnecessary_comments_configuration_group_else(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                remove_unnecessary_comments_configuration_group_switch(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }
            LexerToken::EndConfigurationGroup(position) => {
                remove_unnecessary_comments_configuration_group_end(
                    transformer,
                    position.to_owned(),
                    Some(&mut comment_node),
                );
            }

            _ => {}
        }
    }
    if let LexerToken::EndCommentMultiLine(x) = transformer.current_token {
        comment_node.set_end_position(x.1);
    }
    //always ensure once we get to end comment single line terminated to move to the "next" token so it's ready to parse again
    //this is also necessary to recursive calls to this function still end up working
    let mut comment_iter = comment_node.contents.iter();
    if comment_iter.any(|c| {
        !matches!(
            c,
            FlattenApplicabilityAst::Text(_) | FlattenApplicabilityAst::Comment(_)
        )
    }) {
        let mut i = comment_node
            .contents
            .into_iter()
            .filter(|x| {
                !matches!(
                    x,
                    FlattenApplicabilityAst::Text(_) | FlattenApplicabilityAst::Comment(_)
                )
            })
            .peekable();
        while let Some(mut x) = i.next() {
            if i.peek().is_none() {
                x.set_end_position(comment_node.end_position);
            }
            root_node.push(x)
        }
    } else {
        root_node.push(FlattenApplicabilityAst::Comment(comment_node));
    }
}
