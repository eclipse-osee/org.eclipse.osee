use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::second_stage::token::LexerToken;
use applicability_lexer_base::{
    config_group::not::ConfigurationGroupNot,
    utils::locatable::{position, Locatable},
};

pub trait LexConfigurationGroupNot {
    fn lex_config_group_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_not_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupNot for T
where
    T: ConfigurationGroupNot,
{
    fn lex_config_group_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_group_not()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationGroupNot(start, end)
            },
        )
    }

    fn lex_config_group_not_tag<'x>(&self) -> &'x str {
        self.config_group_not_tag()
    }
}
