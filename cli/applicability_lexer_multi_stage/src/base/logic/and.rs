use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait And {
    fn is_and<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn and<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.and_tag())
    }
    fn take_till_and<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_and::<I>(x))
    }
    fn and_tag<'x>(&self) -> &'x str {
        "&"
    }
    fn take_until_and<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.and_tag())
    }
}
