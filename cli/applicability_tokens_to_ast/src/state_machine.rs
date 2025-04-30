use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::applicability_structure::LexerToken;
use applicability_parser_types::applic_tokens::{
    ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedAndTag, ApplicabilityNestedNotAndTag,
    ApplicabilityNestedNotOrTag, ApplicabilityNestedOrTag, ApplicabilityNoTag,
    ApplicabilityNotAndTag, ApplicabilityNotOrTag, ApplicabilityNotTag, ApplicabilityOrTag,
};
use nom::Input;

pub struct StateMachine<I, Iter>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    pub current_token: LexerToken<I>,
    pub next_token: Option<LexerToken<I>>,
    pub iterator: Iter,
}
impl<I, Iter> StateMachine<I, Iter>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    pub fn new(tokens: Iter) -> Self {
        let mut transformer: StateMachine<I, Iter> = StateMachine {
            current_token: LexerToken::<I>::Illegal,
            next_token: Some(LexerToken::<I>::Illegal),
            iterator: tokens,
        };
        transformer.next();
        //NOTE: we only want to roll forward once here since the first action of the parser is to roll forward by 1
        transformer
    }

    pub fn next(&mut self) -> Option<LexerToken<I>> {
        if let Some(next_token) = &self.next_token {
            self.current_token = next_token.clone();
            self.next_token = self.iterator.next();
            return self.next_token.clone();
        }
        None
    }
    pub fn skip_spaces_and_tabs(&mut self) {
        let mut next_value = self.next();
        while matches!(
            self.current_token,
            LexerToken::Space(_) | LexerToken::Tab(_)
        ) && next_value.is_some()
        {
            next_value = self.next();
        }
    }
    pub fn skip_spaces_and_tabs_and_cr_and_nl(&mut self) {
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
    pub fn skip_spaces_and_tabs_and_cr_and_nl_if_not_tag(&mut self) {
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
    pub fn skip_spaces_and_tabs_and_cr_and_nl_if_is_space(&mut self) {
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

    fn skip_until_start_brace(&mut self) {
        if !matches!(&self.current_token, LexerToken::StartBrace(_)) {
            let mut next_value = self.next();
            while !matches!(self.current_token, LexerToken::StartBrace(_)) && next_value.is_some() {
                next_value = self.next();
            }
        }
    }
    pub fn process_tags(&mut self) -> Vec<ApplicTokens<I>> {
        //move past [
        self.skip_until_start_brace();
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
            if terminating_fn(&self.current_token) {
                break;
            }
            match state {
                InternalTagState::Default => {
                    if matches!(self.current_token, LexerToken::Not(_)) {
                        state = InternalTagState::IsInNot;
                    } else if matches!(self.current_token, LexerToken::And(_)) {
                        state = InternalTagState::IsInAnd;
                    } else if matches!(self.current_token, LexerToken::Or(_)) {
                        state = InternalTagState::IsInOr;
                    } else if matches!(self.current_token, LexerToken::Tag(_, _)) {
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
}
enum InternalTagState {
    Default,
    IsInNot,
    IsInNotAnd,
    IsInNotOr,
    IsInAnd,
    IsInOr,
}
fn is_end_paren<I: Input + Send + Sync>(token: &LexerToken<I>) -> bool {
    matches!(token, LexerToken::EndParen(_))
}

#[cfg(test)]
mod tests {
    use applicability_lexer_base::applicability_structure::LexerToken;
    use pretty_assertions::assert_eq;

    use super::StateMachine;

    #[test]
    fn basic_test() {
        let input = vec![
            LexerToken::StartBrace(((8, 1), (9, 1))),
            LexerToken::Tag("APPLIC_1", ((9, 1), (17, 1))),
            LexerToken::EndBrace(((17, 1), (18, 1))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        sm.process_tags();
        assert_eq!(sm.current_token, LexerToken::EndBrace(((17, 1), (18, 1))))
    }
    #[test]
    fn test_start_with_arbitrary_tag_and_has_space() {
        let input = vec![
            LexerToken::FeatureCase(((132, 1), (143, 1))),
            LexerToken::Space(((143, 1), (144, 1))),
            LexerToken::StartBrace(((144, 1), (145, 1))),
            LexerToken::Space(((145, 1), (146, 1))),
            LexerToken::Tag("APPLIC_1", ((146, 1), (154, 1))),
            LexerToken::Space(((154, 1), (155, 1))),
            LexerToken::EndBrace(((155, 1), (156, 1))),
            LexerToken::Text("case1", ((156, 1), (161, 1))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        sm.process_tags();
        assert_eq!(sm.current_token, LexerToken::EndBrace(((155, 1), (156, 1))))
    }

    #[test]
    fn test_start_with_arbitrary_tag_without_starting_space() {
        let input = vec![
            LexerToken::FeatureCase(((132, 1), (143, 1))),
            LexerToken::StartBrace(((144, 1), (145, 1))),
            LexerToken::Space(((145, 1), (146, 1))),
            LexerToken::Tag("APPLIC_1", ((146, 1), (154, 1))),
            LexerToken::Space(((154, 1), (155, 1))),
            LexerToken::EndBrace(((155, 1), (156, 1))),
            LexerToken::Text("case1", ((156, 1), (161, 1))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        sm.process_tags();
        assert_eq!(sm.current_token, LexerToken::EndBrace(((155, 1), (156, 1))))
    }

    #[test]
    fn test_start_with_arbitrary_tag_without_space_after_brace() {
        let input = vec![
            LexerToken::FeatureCase(((132, 1), (143, 1))),
            LexerToken::Space(((143, 1), (144, 1))),
            LexerToken::StartBrace(((144, 1), (145, 1))),
            LexerToken::Tag("APPLIC_1", ((146, 1), (154, 1))),
            LexerToken::Space(((154, 1), (155, 1))),
            LexerToken::EndBrace(((155, 1), (156, 1))),
            LexerToken::Text("case1", ((156, 1), (161, 1))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        sm.process_tags();
        assert_eq!(sm.current_token, LexerToken::EndBrace(((155, 1), (156, 1))))
    }

    #[test]
    fn test_start_with_arbitrary_tag_without_space_after_tag() {
        let input = vec![
            LexerToken::FeatureCase(((132, 1), (143, 1))),
            LexerToken::Space(((143, 1), (144, 1))),
            LexerToken::StartBrace(((144, 1), (145, 1))),
            LexerToken::Space(((145, 1), (146, 1))),
            LexerToken::Tag("APPLIC_1", ((146, 1), (154, 1))),
            LexerToken::EndBrace(((155, 1), (156, 1))),
            LexerToken::Text("case1", ((156, 1), (161, 1))),
        ];
        let mut sm = StateMachine::new(input.into_iter());
        sm.process_tags();
        assert_eq!(sm.current_token, LexerToken::EndBrace(((155, 1), (156, 1))))
    }
}
