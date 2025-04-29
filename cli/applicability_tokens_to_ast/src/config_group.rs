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
// pub fn remove_unnecessary_comments_configuration_group<I, Iter, X>(
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
//     let mut config_node = ApplicabilityNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     config_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         config_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::ConfigurationGroup(config_node));
// }
// pub fn remove_unnecessary_comments_configuration_group_not<I, Iter, X>(
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
//     let mut config_node = ApplicabilityNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     config_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         config_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::ConfigurationGroupNot(config_node));
// }
// pub fn remove_unnecessary_comments_configuration_group_case<I, Iter, X>(
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
//     let mut config_node = ApplicabilityNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     config_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         config_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::ConfigurationGroupCase(config_node));
// }
// pub fn remove_unnecessary_comments_configuration_group_elsif<I, Iter, X>(
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
//     let mut config_node = ApplicabilityNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     config_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         config_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::ConfigurationGroupElseIf(
//         config_node,
//     ));
// }
// pub fn remove_unnecessary_comments_configuration_group_else<I, Iter, X>(
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
//     let mut config_node = PositionNode::new(token_position.0);
//     config_node.set_end_position(token_position.1);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::ConfigurationGroupElse(config_node));
// }
// pub fn remove_unnecessary_comments_configuration_group_switch<I, Iter, X>(
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
//     let mut config_node = PositionNode::new(token_position.0);
//     config_node.set_end_position(token_position.1);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::ConfigurationGroupSwitch(
//         config_node,
//     ));
// }
// pub fn remove_unnecessary_comments_configuration_group_end<I, Iter, X>(
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
//     let mut config_node = PositionNode::new(token_position.0);
//     config_node.set_end_position(token_position.1);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::EndConfigurationGroup(config_node));
// }
