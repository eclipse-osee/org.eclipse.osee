use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::second_stage::token::LexerToken;
use applicability_lexer_base::{
    config::end::ConfigurationEnd,
    utils::locatable::{position, Locatable},
};

pub trait LexConfigurationEnd {
    fn lex_config_end<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_end_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationEnd for T
where
    T: ConfigurationEnd,
{
    fn lex_config_end<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_end()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::EndConfiguration(start, end)
            },
        )
    }

    fn lex_config_end_tag<'x>(&self) -> &'x str {
        self.config_end_tag()
    }
}
