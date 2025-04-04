use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        config_group::switch::ConfigurationGroupSwitch,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexConfigurationGroupSwitch {
    fn lex_config_group_switch<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_switch_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupSwitch for T
where
    T: ConfigurationGroupSwitch,
{
    fn lex_config_group_switch<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.config_group_switch())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationGroupSwitch(start, end)
            })
    }

    fn lex_config_group_switch_tag<'x>(&self) -> &'x str {
        self.config_group_switch_tag()
    }
}
