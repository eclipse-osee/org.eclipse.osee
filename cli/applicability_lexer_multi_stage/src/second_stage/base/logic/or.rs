use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        logic::or::Or,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexOr {
    fn lex_or<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
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
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.or())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| LexerToken::Or(start, end))
    }

    fn lex_or_tag<'x>(&self) -> &'x str {
        self.or_tag()
    }
}
