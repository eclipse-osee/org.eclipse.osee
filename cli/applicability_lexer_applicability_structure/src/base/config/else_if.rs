use nom::{error::ParseError, AsChar, Compare, Input, Parser};


use applicability_lexer_base::{
    applicability_structure::LexerToken, config::else_if::ConfigurationElseIf, utils::locatable::{position, Locatable}
};

pub trait LexConfigurationElseIf {
    fn lex_config_else_if<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_else_if_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationElseIf for T
where
    T: ConfigurationElseIf,
{
    fn lex_config_else_if<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_else_if()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationElseIf(start, end)
            },
        )
    }

    fn lex_config_else_if_tag<'x>(&self) -> &'x str {
        self.config_else_if_tag()
    }
}
