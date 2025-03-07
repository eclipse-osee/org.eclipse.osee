use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        feature::end::FeatureEnd,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexFeatureEnd {
    fn lex_feature_end<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_end_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureEnd for T
where
    T: FeatureEnd,
{
    fn lex_feature_end<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.feature_end()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::EndFeature(start, end)
            },
        )
    }

    fn lex_feature_end_tag<'x>(&self) -> &'x str {
        self.feature_end_tag()
    }
}
