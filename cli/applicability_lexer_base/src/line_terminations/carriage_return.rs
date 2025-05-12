use memchr::memmem;
use nom::{
    bytes::{take_till, take_until},
    error::ParseError,
    AsBytes, AsChar, Compare, FindSubstring, Input, Parser,
};


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
    fn carriage_return_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + AsBytes,
    {
        let finder = memmem::Finder::new(self.carriage_return_tag());
        finder.find(input.as_bytes())
    }
}
