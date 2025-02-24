use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::default::DefaultApplicabilityLexer;

pub trait FeatureElseIf {
    fn feature_else_if<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.feature_else_if_tag())
    }
    fn feature_else_if_tag<'x>(&self) -> &'x str {
        "Feature Else If"
    }
    fn take_until_feature_else_if<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.feature_else_if_tag())
    }
}
impl<T> FeatureElseIf for T where T: DefaultApplicabilityLexer {}
