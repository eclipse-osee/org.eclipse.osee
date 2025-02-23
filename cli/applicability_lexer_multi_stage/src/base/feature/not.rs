use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait FeatureNot {
    fn is_feature_not<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn feature_not<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.feature_not_tag())
    }
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming feature_not into LexerToken
    fn take_till_feature_not<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_feature_not::<I>(x))
    }
    fn feature_not_tag<'x>(&self) -> &'x str{
        "Feature Not"
    }
    fn take_until_feature_not<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.feature_not_tag())
    }
}