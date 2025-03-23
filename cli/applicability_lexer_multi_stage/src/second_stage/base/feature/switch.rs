use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        feature::switch::FeatureSwitch,
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexFeatureSwitch {
    fn lex_feature_switch<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_switch_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureSwitch for T
where
    T: FeatureSwitch,
{
    fn lex_feature_switch<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.feature_switch()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::FeatureSwitch(start, end)
            },
        )
    }

    fn lex_feature_switch_tag<'x>(&self) -> &'x str {
        self.feature_switch_tag()
    }
}
