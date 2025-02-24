use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::default::DefaultApplicabilityLexer;

pub trait StartParen {
    fn is_start_paren<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '('
    }
    fn start_paren<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.start_paren_tag())
    }
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming start_paren into LexerToken
    fn take_till_start_paren<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_start_paren::<I>(x))
    }
    fn start_paren_tag<'x>(&self) -> &'x str {
        "("
    }
    fn take_until_start_paren<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.start_paren_tag())
    }
}

pub trait EndParen {
    fn is_end_paren<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == ')'
    }
    fn end_paren<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.end_paren_tag())
    }
    fn take_till_end_paren<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_end_paren::<I>(x))
    }
    fn end_paren_tag<'x>(&self) -> &'x str {
        ")"
    }
    fn take_until_end_paren<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.end_paren_tag())
    }
}
impl<T> StartParen for T where T: DefaultApplicabilityLexer {}
impl<T> EndParen for T where T: DefaultApplicabilityLexer {}
