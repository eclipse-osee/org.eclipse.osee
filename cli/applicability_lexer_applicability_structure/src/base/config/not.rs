use nom::{error::ParseError, AsChar, Compare, Input, Parser};


use applicability_lexer_base::{
    applicability_structure::LexerToken, config::not::ConfigurationNot, utils::locatable::{position, Locatable}
};

pub trait LexConfigurationNot {
    fn lex_config_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_not_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationNot for T
where
    T: ConfigurationNot,
{
    fn lex_config_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_not()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationNot(start, end)
            },
        )
    }

    fn lex_config_not_tag<'x>(&self) -> &'x str {
        self.config_not_tag()
    }
}
