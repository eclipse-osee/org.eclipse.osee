use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    config_group::else_if::ConfigurationGroupElseIf,
    position::Position,
    utils::locatable::{position, Locatable},
};

pub trait LexConfigurationGroupElseIf {
    fn lex_config_group_else_if<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_else_if_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupElseIf for T
where
    T: ConfigurationGroupElseIf,
{
    fn lex_config_group_else_if<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.config_group_else_if())
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| {
                LexerToken::ConfigurationGroupElseIf((start, end))
            })
    }

    fn lex_config_group_else_if_tag<'x>(&self) -> &'x str {
        self.config_group_else_if_tag()
    }
}
