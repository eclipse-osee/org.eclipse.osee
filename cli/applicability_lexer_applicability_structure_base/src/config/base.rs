use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    config::base::ConfigurationBase,
    utils::locatable::{position, Locatable},
    position::Position
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
            |((start, _), end): ((Position, _), Position)| LexerToken::Configuration((start, end)),
        )
    }

    fn lex_config_base_tag<'x>(&self) -> &'x str {
        self.config_base_tag()
    }
}
