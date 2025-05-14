use nom::{AsChar, Compare, FindSubstring, Input, Parser, error::ParseError, multi::many0};

use crate::utils::tag_non_terminated::TagNonTerminated;
use applicability_lexer_applicability_structure_base::{
    config::else_if::LexConfigurationElseIf,
    delimiters::{space::LexSpace, tab::LexTab},
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait ConfigElseIfSingleLineNonTerminated {
    fn config_else_if_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigElseIfSingleLineNonTerminated for T
where
    T: TagNonTerminated + LexConfigurationElseIf + LexSpace + LexTab,
{
    fn config_else_if_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let tag = self.non_terminated_tag();
        let config_else_if_tag = self
            .lex_config_else_if()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        config_else_if_tag
    }
}
