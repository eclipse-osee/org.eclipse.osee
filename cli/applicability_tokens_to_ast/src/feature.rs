use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{
    applicability_structure::LexerToken,
    position::{Position, TokenPosition},
};
use nom::Input;

use crate::{
    flatten_ast::{ApplicabilityNode, FlattenApplicabilityAst, HasContents, PositionNode},
    flatten_ast_state_machine::FlattenStateMachine,
};

pub fn remove_unnecessary_comments_feature<I, Iter, X>(
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
    let mut feature_node = ApplicabilityNode::new(token_position);
    transformer.skip_spaces_and_tabs_and_cr_and_nl();
    feature_node.tag = transformer.process_tags();
    if let LexerToken::EndBrace(x) = transformer.current_token {
        feature_node.set_end_position(x.1);
    }
    transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
    root_node.push(FlattenApplicabilityAst::Feature(feature_node));
}
pub fn remove_unnecessary_comments_feature_not<I, Iter, X>(
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
    let mut feature_node = ApplicabilityNode::new(token_position);
    transformer.skip_spaces_and_tabs_and_cr_and_nl();
    feature_node.tag = transformer.process_tags();
    if let LexerToken::EndBrace(x) = transformer.current_token {
        feature_node.set_end_position(x.1);
    }
    transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
    root_node.push(FlattenApplicabilityAst::FeatureNot(feature_node));
}
pub fn remove_unnecessary_comments_feature_case<I, Iter, X>(
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
    let mut feature_node = ApplicabilityNode::new(token_position);
    transformer.skip_spaces_and_tabs_and_cr_and_nl();
    feature_node.tag = transformer.process_tags();
    if let LexerToken::EndBrace(x) = transformer.current_token {
        feature_node.set_end_position(x.1);
    }
    transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
    root_node.push(FlattenApplicabilityAst::FeatureCase(feature_node));
}
pub fn remove_unnecessary_comments_feature_elsif<I, Iter, X>(
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
    let mut feature_node = ApplicabilityNode::new(token_position);
    transformer.skip_spaces_and_tabs_and_cr_and_nl();
    feature_node.tag = transformer.process_tags();
    if let LexerToken::EndBrace(x) = transformer.current_token {
        feature_node.set_end_position(x.1);
    }
    transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
    root_node.push(FlattenApplicabilityAst::FeatureElseIf(feature_node));
}
pub fn remove_unnecessary_comments_feature_else<I, Iter, X>(
    transformer: &mut FlattenStateMachine<I, Iter>,
    token_position: TokenPosition,
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
    let mut feature_node = PositionNode::new(token_position.0);
    feature_node.set_end_position(token_position.1);
    transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
    root_node.push(FlattenApplicabilityAst::FeatureElse(feature_node));
}
pub fn remove_unnecessary_comments_feature_switch<I, Iter, X>(
    transformer: &mut FlattenStateMachine<I, Iter>,
    token_position: TokenPosition,
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
    let mut feature_node = PositionNode::new(token_position.0);
    feature_node.set_end_position(token_position.1);
    transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
    root_node.push(FlattenApplicabilityAst::FeatureSwitch(feature_node));
}
pub fn remove_unnecessary_comments_feature_end<I, Iter, X>(
    transformer: &mut FlattenStateMachine<I, Iter>,
    token_position: TokenPosition,
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
    let mut feature_node = PositionNode::new(token_position.0);
    feature_node.set_end_position(token_position.1);
    transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
    root_node.push(FlattenApplicabilityAst::EndFeature(feature_node));
}
