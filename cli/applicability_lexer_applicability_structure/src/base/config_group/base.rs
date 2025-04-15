use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    config_group::base::ConfigurationGroupBase,
    position::Position,
    utils::locatable::{position, Locatable},
};

pub trait LexConfigurationGroupBase {
    fn lex_config_group_base<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_base_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupBase for T
where
    T: ConfigurationGroupBase,
{
    fn lex_config_group_base<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.config_group_base())
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| {
                LexerToken::ConfigurationGroup((start, end))
            })
    }

    fn lex_config_group_base_tag<'x>(&self) -> &'x str {
        self.config_group_base_tag()
    }
}
