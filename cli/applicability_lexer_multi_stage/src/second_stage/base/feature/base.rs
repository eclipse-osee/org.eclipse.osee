use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        feature::base::FeatureBase,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexFeatureBase {
    fn lex_feature_base<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_base_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureBase for T
where
    T: FeatureBase,
{
    fn lex_feature_base<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.feature_base()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| LexerToken::Feature(start, end),
        )
    }

    fn lex_feature_base_tag<'x>(&self) -> &'x str {
        self.feature_base_tag()
    }
}
