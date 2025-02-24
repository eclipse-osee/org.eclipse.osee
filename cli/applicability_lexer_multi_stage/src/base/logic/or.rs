use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::default::DefaultApplicabilityLexer;

pub trait Or {
    fn is_or<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '|'
    }
    fn or<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.or_tag())
    }
    fn take_till_or<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_or::<I>(x))
    }
    fn or_tag<'x>(&self) -> &'x str {
        " "
    }
    fn take_until_or<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.or_tag())
    }
}
impl<T> Or for T where T: DefaultApplicabilityLexer {}
