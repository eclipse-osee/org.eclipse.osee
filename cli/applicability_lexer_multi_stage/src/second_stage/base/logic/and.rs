use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        logic::and::And,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexAnd {
    fn lex_and<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_and_tag<'x>(&self) -> &'x str;
}

impl<T> LexAnd for T
where
    T: And,
{
    fn lex_and<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.and())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| LexerToken::And(start, end))
    }

    fn lex_and_tag<'x>(&self) -> &'x str {
        self.and_tag()
    }
}
