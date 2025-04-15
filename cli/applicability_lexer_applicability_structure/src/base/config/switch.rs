use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    config::switch::ConfigurationSwitch,
    position::Position,
    utils::locatable::{position, Locatable},
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
            |((start, _), end): ((Position, _), Position)| {
                LexerToken::ConfigurationSwitch((start, end))
            },
        )
    }

    fn lex_config_switch_tag<'x>(&self) -> &'x str {
        self.config_switch_tag()
    }
}
