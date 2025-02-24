use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::default::DefaultApplicabilityLexer;

pub trait FeatureBase {
    fn feature_base<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.feature_base_tag())
    }
    fn feature_base_tag<'x>(&self) -> &'x str {
        "Feature"
    }
    fn take_until_feature_base<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.feature_base_tag())
    }
}
impl<T> FeatureBase for T where T: DefaultApplicabilityLexer {}
