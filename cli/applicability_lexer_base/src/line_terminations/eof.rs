use nom::{combinator::eof, error::ParseError, AsChar, Compare, Input, Parser};

// use crate::default::DefaultApplicabilityLexer;

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
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming carriage_return into LexerToken
}
// impl<T> Eof for T
// where
//     T: DefaultApplicabilityLexer,
// {
//     fn is_eof<I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar,
//     {
//         input.as_char().len() == 0
//     }

//     fn eof<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         eof
//     }
// }
