use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::second_stage::token::LexerToken;
use applicability_lexer_base::{
    feature::else_if::FeatureElseIf,
    utils::locatable::{position, Locatable},
};

pub trait LexFeatureElseIf {
    fn lex_feature_else_if<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_else_if_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureElseIf for T
where
    T: FeatureElseIf,
{
    fn lex_feature_else_if<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.feature_else_if()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::FeatureElseIf(start, end)
            },
        )
    }

    fn lex_feature_else_if_tag<'x>(&self) -> &'x str {
        self.feature_else_if_tag()
    }
}
