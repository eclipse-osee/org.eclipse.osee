use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        substitution::Substitution,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexSubstitution {
    fn lex_substitution<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_substitution_tag<'x>(&self) -> &'x str;
}

impl<T> LexSubstitution for T
where
    T: Substitution,
{
    fn lex_substitution<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.substitution()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::Substitution(start, end)
            },
        )
    }

    fn lex_substitution_tag<'x>(&self) -> &'x str {
        self.substitution_tag()
    }
}
