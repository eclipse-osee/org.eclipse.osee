use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::default::DefaultApplicabilityLexer;

pub trait CarriageReturn {
    fn is_carriage_return<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn carriage_return<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
        O: From<I>;
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming carriage_return into LexerToken
    fn take_till_carriage_return<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_carriage_return::<I>(x))
    }
    fn carriage_return_tag<'x>(&self) -> &'x str {
        "\r"
    }
    fn take_until_carriage_return<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.carriage_return_tag())
    }
}
impl<T> CarriageReturn for T
where
    T: DefaultApplicabilityLexer,
{
    fn is_carriage_return<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '\r'
    }

    fn carriage_return<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
        O: From<I>,
    {
        tag("\r").map(|x: I| x.into())
    }
}
