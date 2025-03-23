use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        config_group::applic_else::ConfigurationGroupElse,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexConfigurationGroupElse {
    fn lex_config_group_else<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_else_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupElse for T
where
    T: ConfigurationGroupElse,
{
    fn lex_config_group_else<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.config_group_else())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationGroupElse(start, end)
            })
    }

    fn lex_config_group_else_tag<'x>(&self) -> &'x str {
        self.config_group_else_tag()
    }
}
