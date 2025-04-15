use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    delimiters::tab::Tab,
    utils::locatable::{position, Locatable},
    position::Position
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
            .map(|((start, _), end): ((Position, _), Position)| LexerToken::Tab((start, end)))
    }

    fn lex_tab_tag<'x>(&self) -> &'x str {
        self.tab_tag()
    }
}
