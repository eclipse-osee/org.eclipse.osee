use nom::{error::ParseError, AsChar, Compare, Input, Parser};


use applicability_lexer_base::{
    applicability_structure::LexerToken, delimiters::space::Space, utils::locatable::{position, Locatable}
};

pub trait LexSpace {
    fn lex_space<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_space_tag<'x>(&self) -> &'x str;
}

impl<T> LexSpace for T
where
    T: Space,
{
    fn lex_space<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.space()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| LexerToken::Space(start, end),
        )
    }

    fn lex_space_tag<'x>(&self) -> &'x str {
        self.space_tag()
    }
}
