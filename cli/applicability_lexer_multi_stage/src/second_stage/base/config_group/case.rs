use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::second_stage::token::LexerToken;
use applicability_lexer_base::{
    config_group::case::ConfigurationGroupCase,
    utils::locatable::{position, Locatable},
};

pub trait LexConfigurationGroupCase {
    fn lex_config_group_case<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_case_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupCase for T
where
    T: ConfigurationGroupCase,
{
    fn lex_config_group_case<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.config_group_case())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationGroupCase(start, end)
            })
    }

    fn lex_config_group_case_tag<'x>(&self) -> &'x str {
        self.config_group_case_tag()
    }
}
