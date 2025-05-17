use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use applicability_lexer_applicability_structure_base::{
        config::else_if::LexConfigurationElseIf,
        delimiters::{space::LexSpace, tab::LexTab},
    };
use crate::utils::tag_terminated::TagTerminated;
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait ConfigElseIfSingleLineTerminated {
    fn config_else_if_terminated<I, E>(
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

impl<T> ConfigElseIfSingleLineTerminated for T
where
    T: TagTerminated + LexConfigurationElseIf + LexSpace + LexTab,
{
    fn config_else_if_terminated<I, E>(
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
        let tag = self.terminated_tag();
        
        self
            .lex_config_else_if()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            })
    }
}
