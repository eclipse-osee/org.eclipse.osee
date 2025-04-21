// use std::cmp;

use std::default;

use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{
    applicability_structure::LexerToken, feature::switch::FeatureSwitch, position::Position,
};
// use nom::{AsBytes, Input, Offset};
// use nom_locate::LocatedSpan;
// use thiserror::Error;
trait HasContents<I> {
    fn push(&mut self, value: FlattenApplicabilityAst<I>);
}

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct HeadNode<I> {
    contents: Vec<FlattenApplicabilityAst<I>>,
}

impl<I> HasContents<I> for HeadNode<I> {
    fn push(&mut self, value: FlattenApplicabilityAst<I>) {
        self.contents.push(value);
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct TextNode<I> {
    start_position: Position,
    end_position: Position,
    content: I,
}
impl<I> TextNode<I> {
    fn new(position: Position, content: I) -> Self {
        TextNode {
            start_position: position,
            end_position: (0, 0),
            content: content,
        }
    }
    fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct CommentNode<I> {
    start_position: Position,
    end_position: Position,
    contents: Vec<FlattenApplicabilityAst<I>>,
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
    fn push(&mut self, value: FlattenApplicabilityAst<I>) {
        self.contents.push(value);
    }
}

impl<I> Default for CommentNode<I> {
    fn default() -> Self {
        Self::new((0, 0))
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNode<I> {
    start_position: Position,
    end_position: Position,
    tag: Vec<ApplicTokens<I>>,
}
impl<I> ApplicabilityNode<I> {
    fn new(position: Position) -> Self {
        ApplicabilityNode {
            start_position: position,
            end_position: (0, 0),
            tag: vec![],
        }
    }
    fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}

impl<I> Default for ApplicabilityNode<I> {
    fn default() -> Self {
        Self::new((0, 0))
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct SubstitutionNode<I> {
    start_position: Position,
    end_position: Position,
    tag: Vec<ApplicTokens<I>>,
}

impl<I> SubstitutionNode<I> {
    fn new(position: Position) -> Self {
        SubstitutionNode {
            start_position: position,
            end_position: (0, 0),
            tag: vec![],
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

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct PositionNode {
    start_position: Position,
    end_position: Position,
}

impl PositionNode {
    fn new(position: Position) -> Self {
        PositionNode {
            start_position: position,
            end_position: (0, 0),
        }
    }
    fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum FlattenApplicabilityAst<I> {
    #[default]
    NoToken,
    Head(HeadNode<I>),
    Comment(CommentNode<I>),
    Text(TextNode<I>),
    Feature(ApplicabilityNode<I>),
    FeatureSwitch(PositionNode),
    FeatureNot(ApplicabilityNode<I>),
    FeatureCase(ApplicabilityNode<I>),
    FeatureElse(PositionNode),
    FeatureElseIf(ApplicabilityNode<I>),
    EndFeature(PositionNode),
    Applicability(ApplicabilityNode<I>),
    Substitution(SubstitutionNode<I>),
}

impl<I> FlattenApplicabilityAst<I> {
    fn set_end_position(&mut self, position: Position) {
        match self {
            FlattenApplicabilityAst::NoToken => {}
            FlattenApplicabilityAst::Head(head_node) => {}
            FlattenApplicabilityAst::Comment(comment_node) => {
                comment_node.set_end_position(position);
            }
            FlattenApplicabilityAst::Text(text_node) => {}
            FlattenApplicabilityAst::Feature(applicability_node)
            | FlattenApplicabilityAst::FeatureNot(applicability_node)
            | FlattenApplicabilityAst::FeatureCase(applicability_node)
            | FlattenApplicabilityAst::FeatureElseIf(applicability_node) => {
                applicability_node.set_end_position(position)
            }
            FlattenApplicabilityAst::FeatureSwitch(position_node)
            | FlattenApplicabilityAst::FeatureElse(position_node)
            | FlattenApplicabilityAst::EndFeature(position_node) => {
                position_node.set_end_position(position);
            }
            FlattenApplicabilityAst::Applicability(applicability_node) => {
                applicability_node.set_end_position(position)
            }
            FlattenApplicabilityAst::Substitution(substitution_node) => {
                substitution_node.set_end_position(position)
            }
        }
    }
}

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
    fn parse_tags(&mut self) -> Vec<ApplicTokens<I>> {
        //move past [
        self.next();
        self.skip_spaces_and_tabs_and_cr_and_nl_if_not_tag();
        self.parse_tags_inner(InternalTagState::Default, |token| {
            matches!(token, LexerToken::EndBrace(_))
        })
    }

    fn parse_tags_inner(
        &mut self,
        mut state: InternalTagState,
        terminating_fn: impl Fn(&LexerToken<I>) -> bool,
    ) -> Vec<ApplicTokens<I>> {
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
                InternalTagState::Default => {
                    if matches!(self.current_token, LexerToken::Not(_)) {
                        state = InternalTagState::IsInNot;
                    } else if matches!(self.current_token, LexerToken::And(_)) {
                        state = InternalTagState::IsInAnd;
                    } else if matches!(self.current_token, LexerToken::Or(_)) {
                        state = InternalTagState::IsInOr;
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
                InternalTagState::IsInNot => {
                    if matches!(self.current_token, LexerToken::Not(_)) {
                        state = InternalTagState::Default;
                    } else if matches!(self.current_token, LexerToken::And(_)) {
                        state = InternalTagState::IsInNotAnd;
                    }
                    if matches!(self.current_token, LexerToken::Or(_)) {
                        state = InternalTagState::IsInNotOr;
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
                InternalTagState::IsInNotAnd => {
                    if matches!(self.current_token, LexerToken::Not(_)) {
                        state = InternalTagState::IsInAnd;
                    } else if matches!(self.current_token, LexerToken::StartParen(_)) {
                        //nested notAnd
                        if self.next().is_some() {
                            let contents =
                                self.parse_tags_inner(InternalTagState::Default, is_end_paren);
                            results.push(ApplicTokens::NestedNotAnd(ApplicabilityNestedNotAndTag(
                                contents, None,
                            )));
                        }
                    } else if let LexerToken::Tag(value, _) = &self.current_token {
                        results.push(ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                            value.clone().into(),
                            None,
                        )));
                    }
                }
                InternalTagState::IsInNotOr => {
                    if matches!(self.current_token, LexerToken::Not(_)) {
                        state = InternalTagState::IsInOr;
                    } else if matches!(self.current_token, LexerToken::StartParen(_)) {
                        //nested notOr
                        if self.next().is_some() {
                            let contents =
                                self.parse_tags_inner(InternalTagState::Default, is_end_paren);
                            results.push(ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(
                                contents, None,
                            )));
                        }
                    } else if let LexerToken::Tag(value, _) = &self.current_token {
                        results.push(ApplicTokens::NotOr(ApplicabilityNotOrTag(
                            value.clone().into(),
                            None,
                        )));
                    }
                }
                InternalTagState::IsInAnd => {
                    if matches!(self.current_token, LexerToken::Not(_)) {
                        state = InternalTagState::IsInNotAnd;
                    } else if matches!(self.current_token, LexerToken::StartParen(_)) {
                        //nested And
                        if self.next().is_some() {
                            let contents =
                                self.parse_tags_inner(InternalTagState::Default, is_end_paren);
                            results.push(ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(
                                contents, None,
                            )));
                        }
                    } else if let LexerToken::Tag(value, _) = &self.current_token {
                        results.push(ApplicTokens::And(ApplicabilityAndTag(
                            value.clone().into(),
                            None,
                        )));
                    }
                }
                InternalTagState::IsInOr => {
                    if matches!(self.current_token, LexerToken::Not(_)) {
                        state = InternalTagState::IsInNotOr;
                    } else if matches!(self.current_token, LexerToken::StartParen(_)) {
                        //nested Or
                        if self.next().is_some() {
                            let contents =
                                self.parse_tags_inner(InternalTagState::Default, is_end_paren);
                            results.push(ApplicTokens::NestedOr(ApplicabilityNestedOrTag(
                                contents, None,
                            )));
                        }
                    } else if let LexerToken::Tag(value, _) = &self.current_token {
                        results.push(ApplicTokens::Or(ApplicabilityOrTag(
                            value.clone().into(),
                            None,
                        )));
                    }
                }
            }
            if self.next().is_none() {
                break;
            }
        }
        results
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
            match &self.current_token {
                LexerToken::StartCommentSingleLineTerminated(position) => {
                    self.parse_terminated_comment(position.0, Some(&mut comment_node));
                }
                LexerToken::Text(content, position) => {
                    comment_node.push(FlattenApplicabilityAst::Text(TextNode {
                        content: content.clone(),
                        start_position: position.0,
                        end_position: position.1,
                    }));
                }
                LexerToken::Substitution(position) => {
                    self.parse_substitution(position.0, Some(&mut comment_node));
                }
                LexerToken::Feature(position) => {
                    self.parse_feature(position.0, Some(&mut comment_node));
                }
                LexerToken::FeatureNot(position) => {
                    self.parse_feature_not(position.0, Some(&mut comment_node));
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
        let mut comment_iter = comment_node.contents.iter();
        if comment_iter.any(|c| {
            !matches!(
                c,
                FlattenApplicabilityAst::Text(_) | FlattenApplicabilityAst::Comment(_)
            )
        }) {
            let mut i = comment_node.contents.into_iter().peekable();
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
    fn parse_feature<X>(&mut self, token_position: Position, root: Option<&mut X>)
    where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        feature_node.tag = self.parse_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            feature_node.set_end_position(x.1);
        }
        self.next();
        root_node.push(FlattenApplicabilityAst::Feature(feature_node));
    }
    fn parse_feature_not<X>(&mut self, token_position: Position, root: Option<&mut X>)
    where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        feature_node.tag = self.parse_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            feature_node.set_end_position(x.1);
        }
        self.next();
        root_node.push(FlattenApplicabilityAst::FeatureNot(feature_node));
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
        substitution_node.tag = self.parse_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            substitution_node.set_end_position(x.1);
        }
        self.next();
        root_node.push(FlattenApplicabilityAst::Substitution(substitution_node));
    }
}

fn is_end_paren<I: Input + Send + Sync>(token: &LexerToken<I>) -> bool {
    matches!(token, LexerToken::EndParen(_))
}
enum InternalTagState {
    Default,
    IsInNot,
    IsInNotAnd,
    IsInNotOr,
    IsInAnd,
    IsInOr,
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
        CommentNode, FlattenApplicabilityAst, HeadNode, SubstitutionNode, TextNode, TokensToAst,
    };

    #[test]
    fn base_comment_test() {
        let doc = ApplicabiltyMarkdownLexerConfig::new();
        let input = LocatedSpan::new_extra("``Test Text``", ((0, 0), (0, 0)));
        let token_stream = tokenize_comments(&doc, input);
        let mut parser =
            TokensToAst::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
        let mut head = HeadNode { contents: vec![] };
        parser.parse_terminated_comment((0, 0), Some(&mut head));
        let results = HeadNode {
            contents: vec![FlattenApplicabilityAst::Comment(CommentNode {
                start_position: (0, 0),
                end_position: (13, 1),
                contents: vec![FlattenApplicabilityAst::Text(TextNode {
                    content: "Test Text",
                    start_position: (2, 1),
                    end_position: (11, 1),
                })],
            })],
        };
        assert_eq!(head, results);
    }

    #[test]
    fn substitution_test() {
        let doc = ApplicabiltyMarkdownLexerConfig::new();
        let input = LocatedSpan::new_extra("``Eval[ABCD]``", ((0, 0), (0, 0)));
        let token_stream = tokenize_comments(&doc, input);
        let mut parser =
            TokensToAst::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
        let mut head = HeadNode { contents: vec![] };
        parser.parse_terminated_comment((0, 0), Some(&mut head));
        let results = HeadNode {
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
        };
        assert_eq!(head, results);
    }
}
