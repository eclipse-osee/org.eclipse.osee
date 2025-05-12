use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::{
        config_group::else_if::LexConfigurationGroupElseIf,
        delimiters::{space::LexSpace, tab::LexTab},
    },
    single_line_non_terminated::utils::tag_non_terminated::TagNonTerminated,
    
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait ConfigGroupElseIfSingleLineNonTerminated {
    fn config_group_else_if_non_terminated<I, E>(
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

impl<T> ConfigGroupElseIfSingleLineNonTerminated for T
where
    T: TagNonTerminated + LexConfigurationGroupElseIf + LexSpace + LexTab,
{
    fn config_group_else_if_non_terminated<I, E>(
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
        let config_group_else_if_tag = self
            .lex_config_group_else_if()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        config_group_else_if_tag
    }
}
