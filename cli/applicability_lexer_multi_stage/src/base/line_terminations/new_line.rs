use nom::{error::ParseError, AsChar, Compare, Input, Parser};

pub trait NewLine {
    fn is_new_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn new_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming new_line into LexerToken
}
