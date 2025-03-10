use nom::{
    bytes::{tag, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::default::DefaultApplicabilityLexer;

pub trait FeatureSwitch {
    fn feature_switch<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.feature_switch_tag())
    }
    fn feature_switch_tag<'x>(&self) -> &'x str {
        "Feature Switch"
    }
    fn take_until_feature_switch<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.feature_switch_tag())
    }
}
impl<T> FeatureSwitch for T where T: DefaultApplicabilityLexer {}
