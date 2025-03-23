use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        line_terminations::new_line::NewLine,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexNewLine {
    fn lex_new_line<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_new_line_tag<'x>(&self) -> &'x str;
}

impl<T> LexNewLine for T
where
    T: NewLine,
{
    fn lex_new_line<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(Parser::into::<I, _>(self.new_line()))
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::UnixNewLine(start, end)
            })
    }

    fn lex_new_line_tag<'x>(&self) -> &'x str {
        self.new_line_tag()
    }
}
