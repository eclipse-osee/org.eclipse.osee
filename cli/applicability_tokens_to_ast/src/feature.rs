// use applicability::applic_tag::ApplicabilityTag;
// use applicability_lexer_base::{
//     applicability_structure::LexerToken,
//     position::{Position, TokenPosition},
// };
// use nom::Input;

// use crate::{
//     flatten_ast::{ApplicabilityNode, FlattenApplicabilityAst, HasContents, PositionNode},
//     flatten_ast_state_machine::FlattenStateMachine,
// };

// pub fn remove_unnecessary_comments_feature<I, Iter, X>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
//     token_position: Position,
//     root: Option<&mut X>,
// ) where
//     X: HasContents<I> + Default,
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
// {
//     let root_node = match root {
//         Some(root) => root,
//         None => &mut X::default(),
//     };
//     let mut feature_node = ApplicabilityNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     feature_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         feature_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::Feature(feature_node));
// }
// pub fn remove_unnecessary_comments_feature_not<I, Iter, X>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
//     token_position: Position,
//     root: Option<&mut X>,
// ) where
//     X: HasContents<I> + Default,
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
// {
//     let root_node = match root {
//         Some(root) => root,
//         None => &mut X::default(),
//     };
//     let mut feature_node = ApplicabilityNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     feature_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         feature_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::FeatureNot(feature_node));
// }
// pub fn remove_unnecessary_comments_feature_case<I, Iter, X>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
//     token_position: Position,
//     root: Option<&mut X>,
// ) where
//     X: HasContents<I> + Default,
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
// {
//     let root_node = match root {
//         Some(root) => root,
//         None => &mut X::default(),
//     };
//     let mut feature_node = ApplicabilityNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     feature_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         feature_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::FeatureCase(feature_node));
// }
// pub fn remove_unnecessary_comments_feature_elsif<I, Iter, X>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
//     token_position: Position,
//     root: Option<&mut X>,
// ) where
//     X: HasContents<I> + Default,
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
// {
//     let root_node = match root {
//         Some(root) => root,
//         None => &mut X::default(),
//     };
//     let mut feature_node = ApplicabilityNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     feature_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         feature_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::FeatureElseIf(feature_node));
// }
// pub fn remove_unnecessary_comments_feature_else<I, Iter, X>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
//     token_position: TokenPosition,
//     root: Option<&mut X>,
// ) where
//     X: HasContents<I> + Default,
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
// {
//     let root_node = match root {
//         Some(root) => root,
//         None => &mut X::default(),
//     };
//     let mut feature_node = PositionNode::new(token_position.0);
//     feature_node.set_end_position(token_position.1);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::FeatureElse(feature_node));
// }
// pub fn remove_unnecessary_comments_feature_switch<I, Iter, X>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
//     token_position: TokenPosition,
//     root: Option<&mut X>,
// ) where
//     X: HasContents<I> + Default,
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
// {
//     let root_node = match root {
//         Some(root) => root,
//         None => &mut X::default(),
//     };
//     let mut feature_node = PositionNode::new(token_position.0);
//     feature_node.set_end_position(token_position.1);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::FeatureSwitch(feature_node));
// }
// pub fn remove_unnecessary_comments_feature_end<I, Iter, X>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
//     token_position: TokenPosition,
//     root: Option<&mut X>,
// ) where
//     X: HasContents<I> + Default,
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
// {
//     let root_node = match root {
//         Some(root) => root,
//         None => &mut X::default(),
//     };
//     let mut feature_node = PositionNode::new(token_position.0);
//     feature_node.set_end_position(token_position.1);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::EndFeature(feature_node));
// }
use crate::{
    state_machine::StateMachine,
    tree::{
        ApplicabilityExprContainerWithPosition, ApplicabilityExprKind, ApplicabilityExprTag,
        ApplicabilityKind,
    },
};
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use nom::Input;

use applicability::applic_tag::ApplicabilityTag;
pub fn process_feature<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> ApplicabilityExprKind<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut tag = ApplicabilityExprKind::Tag(ApplicabilityExprTag {
        tag: transformer.process_tags(),
        kind: ApplicabilityKind::Feature,
        contents: vec![],
        start_position: base_position.0,
        end_position: base_position.1,
    });
    let mut container = ApplicabilityExprContainerWithPosition {
        contents: vec![tag],
        start_position: base_position.0,
        end_position: (0, 0),
    };
    while !matches!(transformer.current_token, LexerToken::EndFeature(_))
        && transformer.next().is_some()
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
            LexerToken::Text(_, _) => {}
            LexerToken::TextToDiscard(_, _) => {
                //discard
            }
            LexerToken::Feature(position) => {
                let node_to_add = process_feature(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::FeatureNot(_) => todo!(),
            LexerToken::FeatureSwitch(_) => todo!(),
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

    ApplicabilityExprKind::TagContainer(container)
}
