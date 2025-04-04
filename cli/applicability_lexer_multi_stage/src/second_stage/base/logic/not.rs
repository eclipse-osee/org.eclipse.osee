use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        logic::not::Not,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexNot {
    fn lex_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_not_tag<'x>(&self) -> &'x str;
}

impl<T> LexNot for T
where
    T: Not,
{
    fn lex_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.not())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| LexerToken::Not(start, end))
    }

    fn lex_not_tag<'x>(&self) -> &'x str {
        self.not_tag()
    }
}
