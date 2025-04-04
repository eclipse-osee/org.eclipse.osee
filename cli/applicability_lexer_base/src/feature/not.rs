use nom::{
    bytes::{tag, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait FeatureNot {
    fn feature_not<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.feature_not_tag())
    }
    fn feature_not_tag<'x>(&self) -> &'x str {
        "Feature Not"
    }
    fn take_until_feature_not<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.feature_not_tag())
    }
}
