use nom::{
    bytes::{take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait CarriageReturn {
    type CarriageReturnOutput;
    fn is_carriage_return<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn carriage_return<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = Self::CarriageReturnOutput, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
        Self::CarriageReturnOutput: AsChar;
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
