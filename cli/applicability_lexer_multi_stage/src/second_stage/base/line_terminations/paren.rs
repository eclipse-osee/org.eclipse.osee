use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        delimiters::paren::{EndParen, StartParen},
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexStartParen {
    fn lex_start_paren<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_start_paren_tag<'x>(&self) -> &'x str;
}

impl<T> LexStartParen for T
where
    T: StartParen,
{
    fn lex_start_paren<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.start_paren()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::StartParen(start, end)
            },
        )
    }

    fn lex_start_paren_tag<'x>(&self) -> &'x str {
        self.start_paren_tag()
    }
}
pub trait LexEndParen {
    fn lex_end_paren<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_end_paren_tag<'x>(&self) -> &'x str;
}

impl<T> LexEndParen for T
where
    T: EndParen,
{
    fn lex_end_paren<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.end_paren()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| LexerToken::EndParen(start, end),
        )
    }

    fn lex_end_paren_tag<'x>(&self) -> &'x str {
        self.end_paren_tag()
    }
}
