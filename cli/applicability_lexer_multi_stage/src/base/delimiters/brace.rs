use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait StartBrace {
    fn is_start_brace<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn start_brace<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.start_brace_tag())
    }
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming start_brace into LexerToken
    fn take_till_start_brace<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_start_brace::<I>(x))
    }
    fn start_brace_tag<'x>(&self) -> &'x str {
        "["
    }
    fn take_until_start_brace<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.start_brace_tag())
    }
}

pub trait EndBrace {
    fn is_end_brace<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn end_brace<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.end_brace_tag())
    }
    fn take_till_end_brace<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_end_brace::<I>(x))
    }
    fn end_brace_tag<'x>(&self) -> &'x str {
        "]"
    }
    fn take_until_end_brace<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.end_brace_tag())
    }
}
