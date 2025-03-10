use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait StartCommentMultiLine {
    fn is_start_comment_multi_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn has_start_comment_multi_line_support(&self) -> bool;
    fn start_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.start_comment_multi_line_tag())
    }
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming start_comment_multi_line into LexerToken
    fn take_till_start_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_start_comment_multi_line::<I>(x))
    }
    fn start_comment_multi_line_tag<'x>(&self) -> &'x str;
    fn take_until_start_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.start_comment_multi_line_tag())
    }
}

pub trait EndCommentMultiLine {
    fn is_end_comment_multi_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn has_end_comment_multi_line_support(&self) -> bool;
    fn end_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.end_comment_multi_line_tag())
    }
    fn take_till_end_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_end_comment_multi_line::<I>(x))
    }
    fn end_comment_multi_line_tag<'x>(&self) -> &'x str;
    fn take_until_end_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.end_comment_multi_line_tag())
    }
}
