use nom::{error::ParseError, AsChar, Compare, Input, Parser};

pub trait StartCommentSingleLine {
    fn is_start_comment_single_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn start_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        // O: FromIterator<I::Item>,
        I::Item: AsChar,
        E: ParseError<I>;
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming start_comment_single_line into LexerToken
}

pub trait EndCommentSingleLine {
    fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn end_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        // O: FromIterator<I::Item>,
        // O: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}
