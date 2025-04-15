use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    line_terminations::carriage_return::CarriageReturn,
    position::Position,
    utils::locatable::{position, Locatable},
};

pub trait LexCarriageReturn {
    fn lex_carriage_return<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_carriage_return_tag<'x>(&self) -> &'x str;
}

impl<T> LexCarriageReturn for T
where
    T: CarriageReturn,
{
    fn lex_carriage_return<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(Parser::into::<I, _>(self.carriage_return()))
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| {
                LexerToken::CarriageReturn((start, end))
            })
    }

    fn lex_carriage_return_tag<'x>(&self) -> &'x str {
        self.carriage_return_tag()
    }
}
