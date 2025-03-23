use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        delimiters::tab::Tab,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexTab {
    fn lex_tab<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_tab_tag<'x>(&self) -> &'x str;
}

impl<T> LexTab for T
where
    T: Tab,
{
    fn lex_tab<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.tab())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| LexerToken::Tab(start, end))
    }

    fn lex_tab_tag<'x>(&self) -> &'x str {
        self.tab_tag()
    }
}
