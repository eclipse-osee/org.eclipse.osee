// use std::cmp;

use std::default;

use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::Position};
// use nom::{AsBytes, Input, Offset};
// use nom_locate::LocatedSpan;
// use thiserror::Error;
trait HasContents<I> {
    fn push(&mut self, value: ApplicabilityAst<I>);
}

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct HeadNode<I> {
    contents: Vec<ApplicabilityAst<I>>,
}

impl<I> HeadNode<I> {
    fn push(&mut self, value: ApplicabilityAst<I>) {
        self.contents.push(value);
    }
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct TextNode<I> {
    content: I,
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct CommentNode<I> {
    start_position: Position,
    end_position: Position,
    contents: Vec<ApplicabilityAst<I>>,
}
impl<I> CommentNode<I> {
    fn new(position: Position) -> Self {
        CommentNode {
            start_position: position,
            end_position: (0, 0),
            contents: vec![],
        }
    }
    fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}
impl<I> HasContents<I> for CommentNode<I> {
    fn push(&mut self, value: ApplicabilityAst<I>) {
        self.contents.push(value);
    }
}

impl<I> Default for CommentNode<I> {
    fn default() -> Self {
        Self::new((0, 0))
    }
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct ApplicabilityNode<I> {
    contents: Vec<ApplicabilityAst<I>>,
}
impl<I> ApplicabilityNode<I> {
    fn push(&mut self, value: ApplicabilityAst<I>) {
        self.contents.push(value);
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct SubstitutionNode<I> {
    start_position: Position,
    end_position: Position,
    tag: I,
}

impl<I> SubstitutionNode<I>
where
    I: Default,
{
    fn new(position: Position) -> Self {
        SubstitutionNode {
            start_position: position,
            end_position: (0, 0),
            tag: I::default(),
        }
    }
}
impl<I> SubstitutionNode<I> {
    fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}
impl<I> Default for SubstitutionNode<I>
where
    I: Default,
{
    fn default() -> Self {
        Self::new((0, 0))
    }
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum ApplicabilityAst<I> {
    #[default]
    NoToken,
    Head(HeadNode<I>),
    Comment(CommentNode<I>),
    Text(TextNode<I>),
    Applicability(ApplicabilityNode<I>),
    Substitution(SubstitutionNode<I>),
}
// #[derive(Debug, PartialEq, Error)]
// enum ApplicabilityAstTransformError {
//     #[error("Failed to find node to append to")]
//     FailedToFindNodeToAppend,
// }

// impl<I> ApplicabilityAst<I> {
//     fn push(
//         self,
//         value: ApplicabilityAst<I>,
//     ) -> Result<ApplicabilityAst<I>, ApplicabilityAstTransformError> {
//         match self {
//             ApplicabilityAst::NoToken => {
//                 Err(ApplicabilityAstTransformError::FailedToFindNodeToAppend)
//             }
//             ApplicabilityAst::Head(mut head_node) => {
//                 head_node.push(value);
//                 Ok(value)
//             }
//             ApplicabilityAst::Text(_) => {
//                 Err(ApplicabilityAstTransformError::FailedToFindNodeToAppend)
//             }
//             ApplicabilityAst::Applicability(mut applicability_node) => {
//                 applicability_node.push(value);
//                 Ok(value)
//             }
//             ApplicabilityAst::Substitution(_) => {
//                 Err(ApplicabilityAstTransformError::FailedToFindNodeToAppend)
//             }
//             ApplicabilityAst::Comment(mut comment_node) => {
//                 comment_node.push(value);
//                 Ok(value)
//             }
//         }
//     }
// }

// struct CurrentState<'a, 'b, 'c, I> {
//     is_in_comment: bool,
//     comment_size: u32,
//     previous_token: &'a ApplicabilityAst<LocatedSpan<I, TokenPosition>>,
//     current_token: &'b ApplicabilityAst<LocatedSpan<I, TokenPosition>>,
//     parent_token: &'c ApplicabilityAst<LocatedSpan<I, TokenPosition>>,
// }

use applicability_parser_types::applic_tokens::{
    ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedAndTag, ApplicabilityNestedNotAndTag,
    ApplicabilityNestedNotOrTag, ApplicabilityNestedOrTag, ApplicabilityNoTag,
    ApplicabilityNotAndTag, ApplicabilityNotOrTag, ApplicabilityNotTag, ApplicabilityOrTag,
};
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
use nom::Input;

struct TokensToAst<I, Iter>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    current_token: LexerToken<I>,
    next_token: Option<LexerToken<I>>,
    iterator: Iter,
}
impl<I, Iter> TokensToAst<I, Iter>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    pub fn new(tokens: Iter) -> Self {
        let mut transformer: TokensToAst<I, Iter> = TokensToAst {
            current_token: LexerToken::<I>::Illegal,
            next_token: Some(LexerToken::<I>::Illegal),
            iterator: tokens,
        };
        transformer.next();
        transformer.next();
        transformer
    }

    fn next(&mut self) -> Option<LexerToken<I>> {
        if let Some(next_token) = &self.next_token {
            self.current_token = next_token.clone();
            self.next_token = self.iterator.next();
            return self.next_token.clone();
        }
        None
    }

    ///
    /// This fn is entered upon receipt of LexerToken::StartCommentSingleLineTerminated
    /// It will exit upon end of stream or LexerToken::EndCommentSingleLineTerminated
    /// It will get the tokens:
    /// LexerToken::StartCommentSingleLineTerminated
    /// tokens between Start and End
    /// LexerToken::EndCommentSingleLineTerminated
    fn parse_terminated_comment<X>(&mut self, token_position: Position, root: Option<&mut X>)
    where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut comment_node = CommentNode::new(token_position);
        let mut next_value = self.next();
        while !matches!(
            self.current_token,
            LexerToken::EndCommentSingleLineTerminated(_)
        ) && next_value.is_some()
        {
            match self.current_token {
                LexerToken::StartCommentSingleLineTerminated(position) => {
                    self.parse_terminated_comment(position.0, Some(&mut comment_node));
                }
                LexerToken::Substitution(position) => {
                    self.parse_substitution(position.0, Some(&mut comment_node));
                }
                _ => {}
            }
            next_value = self.next();
        }
        if let LexerToken::EndCommentSingleLineTerminated(x) = self.current_token {
            comment_node.set_end_position(x.1);
        }
        //always ensure once we get to end comment single line terminated to move to the "next" token so it's ready to parse again
        //this is also necessary to recursive calls to this function still end up working
        self.next();
        root_node.push(ApplicabilityAst::Comment(comment_node));
    }

    fn skip_spaces_and_tabs(&mut self) {
        let mut next_value = self.next();
        while matches!(
            self.current_token,
            LexerToken::Space(_) | LexerToken::Tab(_)
        ) && next_value.is_some()
        {
            next_value = self.next();
        }
    }
    fn skip_spaces_and_tabs_and_cr_and_nl(&mut self) {
        let mut next_value = self.next();
        while matches!(
            self.current_token,
            LexerToken::Space(_)
                | LexerToken::Tab(_)
                | LexerToken::CarriageReturn(_)
                | LexerToken::UnixNewLine(_)
        ) && next_value.is_some()
        {
            next_value = self.next();
        }
    }

    fn parse_substitution<X>(&mut self, token_position: Position, root: Option<&mut X>)
    where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut substitution_node = SubstitutionNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        // let mut next_value = self.next();
        // while !matches!(self.current_token, LexerToken::EndBrace(_)) && next_value.is_some() {
        //     self.skip_spaces_and_tabs_and_cr_and_nl();
        //     //TODO: capture the tag contents
        //     next_value = self.next();
        // }
        // if current_token = [, parse_tags, else error
        if let LexerToken::EndBrace(x) = self.current_token {
            substitution_node.set_end_position(x.1);
        }
        self.next();
        root_node.push(ApplicabilityAst::Substitution(substitution_node));
    }

    // [
    // (optional !) -> Not tag
    //
    // parse is finished when we reach ]
    fn parse_tags(&mut self) -> Vec<ApplicTokens<I>> {
        //move past [
        self.next();
        self.skip_spaces_and_tabs_and_cr_and_nl_if_not_tag();
        self.parse_tags_inner(TagState::Default, |token| {
            matches!(token, LexerToken::EndBrace(_))
        })
    }

    fn skip_spaces_and_tabs_and_cr_and_nl_if_not_tag(&mut self) {
        if !matches!(
            &mut self.current_token,
            LexerToken::Not(_)
                | LexerToken::And(_)
                | LexerToken::Or(_)
                | LexerToken::Tag(_, _)
                | LexerToken::StartParen(_)
        ) {
            self.skip_spaces_and_tabs_and_cr_and_nl();
        }
    }
    fn parse_tags_inner(
        &mut self,
        mut state: TagState,
        terminating_fn: impl Fn(&LexerToken<I>) -> bool,
    ) -> Vec<ApplicTokens<I>> {
        //parse not first
        //parse spaces
        // while terminating_fn(self.current_token) == false
        // if type = Tag, turn into ApplicabilityTag
        // if type = And treat as And
        // if type = OR treat as OR
        // if type = NOT and next = OR treat as NotOr
        // if type = NOT and next = AND treat as NotAnd
        let mut results = vec![];
        while !terminating_fn(&self.current_token) {
            self.skip_spaces_and_tabs_and_cr_and_nl_if_not_tag();
            match state {
                TagState::Default => {
                    if matches!(self.current_token, LexerToken::Not(_)) {
                        state = TagState::IsInNot;
                    } else if matches!(self.current_token, LexerToken::And(_)) {
                        state = TagState::IsInAnd;
                    } else if matches!(self.current_token, LexerToken::Or(_)) {
                        state = TagState::IsInOr;
                    } else {
                        //add a normal tag
                        if let LexerToken::Tag(value, _) = &self.current_token {
                            results.push(ApplicTokens::NoTag(ApplicabilityNoTag(
                                value.clone().into(),
                                None,
                            )));
                        }
                    }
                }
                TagState::IsInNot => {
                    if matches!(self.current_token, LexerToken::And(_)) {
                        state = TagState::IsInNotAnd;
                    }
                    if matches!(self.current_token, LexerToken::Or(_)) {
                        state = TagState::IsInNotOr;
                    }
                    if matches!(self.current_token, LexerToken::Tag(_, _)) {
                        if let LexerToken::Tag(value, _) = &self.current_token {
                            results.push(ApplicTokens::Not(ApplicabilityNotTag(
                                value.clone().into(),
                                None,
                            )));
                        }
                    }
                }
                TagState::IsInNotAnd => {
                    if matches!(self.current_token, LexerToken::StartParen(_)) {
                        //nested notAnd
                        self.next();
                        let contents = self.parse_tags_inner(TagState::Default, |token| {
                            matches!(token, LexerToken::EndParen(_))
                        });
                        results.push(ApplicTokens::NestedNotAnd(ApplicabilityNestedNotAndTag(
                            contents, None,
                        )));
                    } else if let LexerToken::Tag(value, _) = &self.current_token {
                        results.push(ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                            value.clone().into(),
                            None,
                        )));
                    }
                }
                TagState::IsInNotOr => {
                    if matches!(self.current_token, LexerToken::StartParen(_)) {
                        //nested notOr
                        self.next();
                        let contents = self.parse_tags_inner(TagState::Default, |token| {
                            matches!(token, LexerToken::EndParen(_))
                        });
                        results.push(ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(
                            contents, None,
                        )));
                    } else if let LexerToken::Tag(value, _) = &self.current_token {
                        results.push(ApplicTokens::NotOr(ApplicabilityNotOrTag(
                            value.clone().into(),
                            None,
                        )));
                    }
                }
                TagState::IsInAnd => {
                    if matches!(self.current_token, LexerToken::StartParen(_)) {
                        //nested And
                        self.next();
                        let contents = self.parse_tags_inner(TagState::Default, |token| {
                            matches!(token, LexerToken::EndParen(_))
                        });
                        results.push(ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(
                            contents, None,
                        )));
                    } else if let LexerToken::Tag(value, _) = &self.current_token {
                        results.push(ApplicTokens::And(ApplicabilityAndTag(
                            value.clone().into(),
                            None,
                        )));
                    }
                }
                TagState::IsInOr => {
                    if matches!(self.current_token, LexerToken::StartParen(_)) {
                        //nested Or
                        self.next();
                        let contents = self.parse_tags_inner(TagState::Default, |token| {
                            matches!(token, LexerToken::EndParen(_))
                        });
                        results.push(ApplicTokens::NestedOr(ApplicabilityNestedOrTag(
                            contents, None,
                        )));
                    } else if let LexerToken::Tag(value, _) = &self.current_token {
                        results.push(ApplicTokens::Or(ApplicabilityOrTag(
                            value.clone().into(),
                            None,
                        )));
                    }
                }
            }
            self.next();
        }
        results
    }
}

enum TagState {
    Default,
    IsInNot,
    IsInNotAnd,
    IsInNotOr,
    IsInAnd,
    IsInOr,
}

#[cfg(test)]
mod tests {
    use applicability_lexer_base::applicability_structure::LexerToken;
    use applicability_lexer_config_markdown::ApplicabiltyMarkdownLexerConfig;
    use applicability_lexer_multi_stage_lexer::lexer::tokenize_comments;
    use nom_locate::LocatedSpan;

    use crate::TokensToAst;

    fn test() {
        let doc = ApplicabiltyMarkdownLexerConfig::new();
        let input = LocatedSpan::new_extra("``Test Document``", ((0, 0), (0, 0)));
        let token_stream = tokenize_comments(&doc, input);
        TokensToAst::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
    }
}
