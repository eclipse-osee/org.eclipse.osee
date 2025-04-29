// use applicability::applic_tag::ApplicabilityTag;
// use applicability_lexer_base::{applicability_structure::LexerToken, position::Position};
// use nom::Input;

// use crate::{
//     flatten_ast::{FlattenApplicabilityAst, HasContents, SubstitutionNode},
//     flatten_ast_state_machine::FlattenStateMachine,
// };

// pub fn remove_unnecessary_comments_substitution<I, Iter, X>(
//     transformer: &mut FlattenStateMachine<I, Iter>,
//     token_position: Position,
//     root: Option<&mut X>,
// ) where
//     Iter: Iterator<Item = LexerToken<I>>,
//     I: Input + Send + Sync + Default,
//     ApplicabilityTag<I, String>: From<I>,
//     X: HasContents<I> + Default,
// {
//     let root_node = match root {
//         Some(root) => root,
//         None => &mut X::default(),
//     };
//     let mut substitution_node = SubstitutionNode::new(token_position);
//     transformer.skip_spaces_and_tabs_and_cr_and_nl();
//     substitution_node.tag = transformer.process_tags();
//     if let LexerToken::EndBrace(x) = transformer.current_token {
//         substitution_node.set_end_position(x.1);
//     }
//     transformer.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
//     root_node.push(FlattenApplicabilityAst::Substitution(substitution_node));
// }
