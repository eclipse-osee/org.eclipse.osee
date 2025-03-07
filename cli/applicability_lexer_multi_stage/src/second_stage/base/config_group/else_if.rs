use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        config_group::else_if::ConfigurationGroupElseIf,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexConfigurationGroupElseIf {
    fn lex_config_group_else_if<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_else_if_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupElseIf for T
where
    T: ConfigurationGroupElseIf,
{
    fn lex_config_group_else_if<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_group_else_if()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationGroupElseIf(start, end)
            },
        )
    }

    fn lex_config_group_else_if_tag<'x>(&self) -> &'x str {
        self.config_group_else_if_tag()
    }
}
