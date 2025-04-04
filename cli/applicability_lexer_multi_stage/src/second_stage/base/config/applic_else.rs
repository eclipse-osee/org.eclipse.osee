use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        config::applic_else::ConfigurationElse,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexConfigurationElse {
    fn lex_config_else<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_else_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationElse for T
where
    T: ConfigurationElse,
{
    fn lex_config_else<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_else()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationElse(start, end)
            },
        )
    }

    fn lex_config_else_tag<'x>(&self) -> &'x str {
        self.config_else_tag()
    }
}
