use nom::{error::ParseError, AsChar, Compare, Input, Parser};


use applicability_lexer_base::{
    applicability_structure::LexerToken, config::case::ConfigurationCase, utils::locatable::{position, Locatable}
};

pub trait LexConfigurationCase {
    fn lex_config_case<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_case_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationCase for T
where
    T: ConfigurationCase,
{
    fn lex_config_case<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_case()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::ConfigurationCase(start, end)
            },
        )
    }

    fn lex_config_case_tag<'x>(&self) -> &'x str {
        self.config_case_tag()
    }
}
