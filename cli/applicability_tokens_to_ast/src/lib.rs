use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{
    applicability_structure::LexerToken,
    feature::switch::FeatureSwitch,
    position::{Position, TokenPosition},
};
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
    Configuration(ApplicabilityNode<I>),
    ConfigurationSwitch(PositionNode),
    ConfigurationNot(ApplicabilityNode<I>),
    ConfigurationCase(ApplicabilityNode<I>),
    ConfigurationElse(PositionNode),
    ConfigurationElseIf(ApplicabilityNode<I>),
    EndConfiguration(PositionNode),
    ConfigurationGroup(ApplicabilityNode<I>),
    ConfigurationGroupSwitch(PositionNode),
    ConfigurationGroupNot(ApplicabilityNode<I>),
    ConfigurationGroupCase(ApplicabilityNode<I>),
    ConfigurationGroupElse(PositionNode),
    ConfigurationGroupElseIf(ApplicabilityNode<I>),
    EndConfigurationGroup(PositionNode),
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
            | FlattenApplicabilityAst::FeatureElseIf(applicability_node)
            | FlattenApplicabilityAst::Configuration(applicability_node)
            | FlattenApplicabilityAst::ConfigurationNot(applicability_node)
            | FlattenApplicabilityAst::ConfigurationCase(applicability_node)
            | FlattenApplicabilityAst::ConfigurationElseIf(applicability_node)
            | FlattenApplicabilityAst::ConfigurationGroup(applicability_node)
            | FlattenApplicabilityAst::ConfigurationGroupNot(applicability_node)
            | FlattenApplicabilityAst::ConfigurationGroupCase(applicability_node)
            | FlattenApplicabilityAst::ConfigurationGroupElseIf(applicability_node) => {
                applicability_node.set_end_position(position)
            }
            FlattenApplicabilityAst::FeatureSwitch(position_node)
            | FlattenApplicabilityAst::FeatureElse(position_node)
            | FlattenApplicabilityAst::EndFeature(position_node)
            | FlattenApplicabilityAst::ConfigurationSwitch(position_node)
            | FlattenApplicabilityAst::ConfigurationElse(position_node)
            | FlattenApplicabilityAst::EndConfiguration(position_node)
            | FlattenApplicabilityAst::ConfigurationGroupSwitch(position_node)
            | FlattenApplicabilityAst::ConfigurationGroupElse(position_node)
            | FlattenApplicabilityAst::EndConfigurationGroup(position_node) => {
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
        //NOTE: we only want to roll forward once here since the first action of the parser is to roll forward by 1
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
    fn skip_spaces_and_tabs_and_cr_and_nl_if_is_space(&mut self) {
        if matches!(
            &mut self.current_token,
            LexerToken::Space(_)
                | LexerToken::Tab(_)
                | LexerToken::CarriageReturn(_)
                | LexerToken::UnixNewLine(_)
        ) {
            self.skip_spaces_and_tabs_and_cr_and_nl();
        }
    }
    fn process_tags(&mut self) -> Vec<ApplicTokens<I>> {
        //move past [
        self.next();
        self.skip_spaces_and_tabs_and_cr_and_nl_if_not_tag();
        self.process_tags_inner(InternalTagState::Default, |token| {
            matches!(token, LexerToken::EndBrace(_))
        })
    }

    fn process_tags_inner(
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
                                self.process_tags_inner(InternalTagState::Default, is_end_paren);
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
                                self.process_tags_inner(InternalTagState::Default, is_end_paren);
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
                                self.process_tags_inner(InternalTagState::Default, is_end_paren);
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
                                self.process_tags_inner(InternalTagState::Default, is_end_paren);
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
    fn parse(&mut self) -> FlattenApplicabilityAst<I> {
        let mut head = HeadNode { contents: vec![] };
        while self.next().is_some() {
            match &self.current_token {
                LexerToken::StartCommentSingleLineTerminated(position) => {
                    self.remove_unnecessary_comments_terminated_comment(
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
            // self.next();
        }
        FlattenApplicabilityAst::Head(head)
    }
    ///
    /// This fn is entered upon receipt of LexerToken::StartCommentSingleLineTerminated
    /// It will exit upon end of stream or LexerToken::EndCommentSingleLineTerminated
    /// It will get the tokens:
    /// LexerToken::StartCommentSingleLineTerminated
    /// tokens between Start and End
    /// LexerToken::EndCommentSingleLineTerminated
    fn remove_unnecessary_comments_terminated_comment<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut comment_node = CommentNode::new(token_position);
        while !matches!(
            self.current_token,
            LexerToken::EndCommentSingleLineTerminated(_)
        ) && self.next().is_some()
        {
            match &self.current_token {
                LexerToken::StartCommentSingleLineTerminated(position) => {
                    self.remove_unnecessary_comments_terminated_comment(
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
                    self.remove_unnecessary_comments_substitution(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::Feature(position) => {
                    self.remove_unnecessary_comments_feature(position.0, Some(&mut comment_node));
                }
                LexerToken::FeatureElseIf(position) => {
                    self.remove_unnecessary_comments_feature_elsif(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::FeatureCase(position) => {
                    self.remove_unnecessary_comments_feature_case(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::FeatureNot(position) => {
                    self.remove_unnecessary_comments_feature_not(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::FeatureElse(position) => {
                    self.remove_unnecessary_comments_feature_else(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }
                LexerToken::FeatureSwitch(position) => {
                    self.remove_unnecessary_comments_feature_switch(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }
                LexerToken::EndFeature(position) => {
                    self.remove_unnecessary_comments_feature_end(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }
                LexerToken::Configuration(position) => {
                    self.remove_unnecessary_comments_configuration(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationElseIf(position) => {
                    self.remove_unnecessary_comments_configuration_elsif(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationCase(position) => {
                    self.remove_unnecessary_comments_configuration_case(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationNot(position) => {
                    self.remove_unnecessary_comments_configuration_not(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationElse(position) => {
                    self.remove_unnecessary_comments_configuration_else(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationSwitch(position) => {
                    self.remove_unnecessary_comments_configuration_switch(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }
                LexerToken::EndConfiguration(position) => {
                    self.remove_unnecessary_comments_configuration_end(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }

                LexerToken::ConfigurationGroup(position) => {
                    self.remove_unnecessary_comments_configuration_group(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationGroupElseIf(position) => {
                    self.remove_unnecessary_comments_configuration_group_elsif(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationGroupCase(position) => {
                    self.remove_unnecessary_comments_configuration_group_case(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationGroupNot(position) => {
                    self.remove_unnecessary_comments_configuration_group_not(
                        position.0,
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationGroupElse(position) => {
                    self.remove_unnecessary_comments_configuration_group_else(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }
                LexerToken::ConfigurationGroupSwitch(position) => {
                    self.remove_unnecessary_comments_configuration_group_switch(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }
                LexerToken::EndConfigurationGroup(position) => {
                    self.remove_unnecessary_comments_configuration_group_end(
                        position.to_owned(),
                        Some(&mut comment_node),
                    );
                }

                _ => {}
            }
        }
        if let LexerToken::EndCommentSingleLineTerminated(x) = self.current_token {
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
    fn remove_unnecessary_comments_feature<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        feature_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            feature_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::Feature(feature_node));
    }
    fn remove_unnecessary_comments_feature_not<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        feature_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            feature_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::FeatureNot(feature_node));
    }
    fn remove_unnecessary_comments_feature_case<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        feature_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            feature_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::FeatureCase(feature_node));
    }
    fn remove_unnecessary_comments_feature_elsif<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        feature_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            feature_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::FeatureElseIf(feature_node));
    }
    fn remove_unnecessary_comments_feature_else<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = PositionNode::new(token_position.0);
        feature_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        if let LexerToken::EndBrace(x) = self.current_token {
            feature_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::FeatureElse(feature_node));
    }
    fn remove_unnecessary_comments_feature_switch<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = PositionNode::new(token_position.0);
        feature_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::FeatureSwitch(feature_node));
    }
    fn remove_unnecessary_comments_feature_end<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut feature_node = PositionNode::new(token_position.0);
        feature_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::EndFeature(feature_node));
    }

    fn remove_unnecessary_comments_configuration<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        config_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::Configuration(config_node));
    }
    fn remove_unnecessary_comments_configuration_not<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        config_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationNot(config_node));
    }
    fn remove_unnecessary_comments_configuration_case<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        config_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationCase(config_node));
    }
    fn remove_unnecessary_comments_configuration_elsif<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        config_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationElseIf(config_node));
    }
    fn remove_unnecessary_comments_configuration_else<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = PositionNode::new(token_position.0);
        config_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationElse(config_node));
    }
    fn remove_unnecessary_comments_configuration_switch<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = PositionNode::new(token_position.0);
        config_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationSwitch(config_node));
    }
    fn remove_unnecessary_comments_configuration_end<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = PositionNode::new(token_position.0);
        config_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::EndConfiguration(config_node));
    }

    fn remove_unnecessary_comments_configuration_group<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        config_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationGroup(config_node));
    }
    fn remove_unnecessary_comments_configuration_group_not<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        config_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationGroupNot(config_node));
    }
    fn remove_unnecessary_comments_configuration_group_case<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        config_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationGroupCase(config_node));
    }
    fn remove_unnecessary_comments_configuration_group_elsif<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = ApplicabilityNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        config_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationGroupElseIf(
            config_node,
        ));
    }
    fn remove_unnecessary_comments_configuration_group_else<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = PositionNode::new(token_position.0);
        config_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        if let LexerToken::EndBrace(x) = self.current_token {
            config_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationGroupElse(config_node));
    }
    fn remove_unnecessary_comments_configuration_group_switch<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = PositionNode::new(token_position.0);
        config_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::ConfigurationGroupSwitch(
            config_node,
        ));
    }
    fn remove_unnecessary_comments_configuration_group_end<X>(
        &mut self,
        token_position: TokenPosition,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut config_node = PositionNode::new(token_position.0);
        config_node.set_end_position(token_position.1);
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
        root_node.push(FlattenApplicabilityAst::EndConfigurationGroup(config_node));
    }

    fn remove_unnecessary_comments_substitution<X>(
        &mut self,
        token_position: Position,
        root: Option<&mut X>,
    ) where
        X: HasContents<I> + Default,
    {
        let root_node = match root {
            Some(root) => root,
            None => &mut X::default(),
        };
        let mut substitution_node = SubstitutionNode::new(token_position);
        self.skip_spaces_and_tabs_and_cr_and_nl();
        substitution_node.tag = self.process_tags();
        if let LexerToken::EndBrace(x) = self.current_token {
            substitution_node.set_end_position(x.1);
        }
        self.skip_spaces_and_tabs_and_cr_and_nl_if_is_space();
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
        ApplicabilityNode, CommentNode, FlattenApplicabilityAst, HeadNode, PositionNode,
        SubstitutionNode, TextNode, TokensToAst,
    };

    #[test]
    fn base_comment_test() {
        let doc = ApplicabiltyMarkdownLexerConfig::new();
        let input = LocatedSpan::new_extra("``Test Text``", ((0, 0), (0, 0)));
        let token_stream = tokenize_comments(&doc, input);
        let mut parser =
            TokensToAst::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
        let head = parser.parse();
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
            TokensToAst::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
        let head = parser.parse();
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
            TokensToAst::new(token_stream.into_iter().map(Into::<LexerToken<&str>>::into));
        let head = parser.parse();
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
