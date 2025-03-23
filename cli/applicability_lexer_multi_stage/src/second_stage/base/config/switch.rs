use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        config::switch::ConfigurationSwitch,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexConfigurationSwitch {
    fn lex_config_switch<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_switch_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationSwitch for T
where
    T: ConfigurationSwitch,
{
    fn lex_config_switch<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_switch()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationSwitch(start, end)
            },
        )
    }

    fn lex_config_switch_tag<'x>(&self) -> &'x str {
        self.config_switch_tag()
    }
}
