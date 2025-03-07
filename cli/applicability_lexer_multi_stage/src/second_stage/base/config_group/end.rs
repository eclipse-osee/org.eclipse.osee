use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        config_group::end::ConfigurationGroupEnd,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexConfigurationGroupEnd {
    fn lex_config_group_end<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_end_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupEnd for T
where
    T: ConfigurationGroupEnd,
{
    fn lex_config_group_end<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_group_end()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::EndConfigurationGroup(start, end)
            },
        )
    }

    fn lex_config_group_end_tag<'x>(&self) -> &'x str {
        self.config_group_end_tag()
    }
}
