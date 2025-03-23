use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        feature::not::FeatureNot,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexFeatureNot {
    fn lex_feature_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_not_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureNot for T
where
    T: FeatureNot,
{
    fn lex_feature_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.feature_not()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::FeatureNot(start, end)
            },
        )
    }

    fn lex_feature_not_tag<'x>(&self) -> &'x str {
        self.feature_not_tag()
    }
}
