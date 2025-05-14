use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    logic::or::Or,
    utils::locatable::{position, Locatable},
    position::Position
};

pub trait LexOr {
    fn lex_or<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_or_tag<'x>(&self) -> &'x str;
}

impl<T> LexOr for T
where
    T: Or,
{
    fn lex_or<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.or())
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| LexerToken::Or((start, end)))
    }

    fn lex_or_tag<'x>(&self) -> &'x str {
        self.or_tag()
    }
}
