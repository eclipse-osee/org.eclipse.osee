use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

// use crate::default::DefaultApplicabilityLexer;

pub trait NewLine {
    fn is_new_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn new_line<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        O: From<I>,
        E: ParseError<I>;
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming new_line into LexerToken
    fn take_till_new_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_new_line::<I>(x))
    }
    fn new_line_tag<'x>(&self) -> &'x str {
        "\n"
    }
    fn take_until_new_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.new_line_tag())
    }
}
// impl<T> NewLine for T
// where
//     T: DefaultApplicabilityLexer,
// {
//     fn is_new_line<I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar,
//     {
//         input.as_char() == '\n'
//     }

//     fn new_line<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         O: From<I>,
//         E: ParseError<I>,
//     {
//         tag("\n").map(|x: I| x.into())
//     }
// }
