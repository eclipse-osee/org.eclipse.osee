use nom::{error::ParseError, AsChar, Compare, Input, Parser};


pub trait Eof {
    fn is_eof<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn eof<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}
