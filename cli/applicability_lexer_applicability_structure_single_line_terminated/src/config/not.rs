use nom::{AsChar, Compare, FindSubstring, Input, Parser, error::ParseError, multi::many0};

use crate::utils::tag_terminated::TagTerminated;
use applicability_lexer_applicability_structure_base::{
    config::not::LexConfigurationNot,
    delimiters::{space::LexSpace, tab::LexTab},
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait ConfigNotSingleLineTerminated {
    fn config_not_terminated<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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

impl<T> ConfigNotSingleLineTerminated for T
where
    T: TagTerminated + LexConfigurationNot + LexSpace + LexTab,
{
    fn config_not_terminated<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
        let tag = self.terminated_tag();
        let config_not_tag = self
            .lex_config_not()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        config_not_tag
    }
}
