use nom::{
    bytes::{tag, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait FeatureCase {
    fn feature_case<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.feature_case_tag())
    }
    fn feature_case_tag<'x>(&self) -> &'x str {
        "Feature Case"
    }
    fn take_until_feature_case<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.feature_case_tag())
    }
}
