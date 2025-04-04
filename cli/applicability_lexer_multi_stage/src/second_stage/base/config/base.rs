use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        config::base::ConfigurationBase,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexConfigurationBase {
    fn lex_config_base<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_base_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationBase for T
where
    T: ConfigurationBase,
{
    fn lex_config_base<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_base()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::Configuration(start, end)
            },
        )
    }

    fn lex_config_base_tag<'x>(&self) -> &'x str {
        self.config_base_tag()
    }
}
