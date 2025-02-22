use std::iter::Chain;

use nom::{
    character::multispace0, error::ParseError, AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::base::{
    comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
    line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    utils::take_first::take_until_first3,
};

use super::token::FirstStageToken;

pub trait IdentifySingleLineTerminatedComment {
    fn identify_comment_single_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        String: FromIterator<<I as Input>::Item>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifySingleLineTerminatedComment for T
where
    T: StartCommentSingleLine + EndCommentSingleLine + CarriageReturn + NewLine,
{
    fn identify_comment_single_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        String: FromIterator<<I as Input>::Item>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_single_line()
            .and(take_until_first3(
                self.carriage_return_tag(),
                self.new_line_tag(),
                self.end_comment_single_line_tag(),
            ))
            .map(|(start, text): (I, I)| {
                let start_iter: I::Iter = start.iter_elements();
                let text_iter: I::Iter = text.iter_elements();
                let iter = start_iter.chain(text_iter);
                iter
            });
        // note: we are taking every character until a newline or end comment,
        // but we only parse the end comment here to trigger a "fail" so that it avoids this branch
        // immediately
        let end = self
            .end_comment_single_line()
            .and(multispace0())
            .map(|(end, spaces): (I, I)| {
                let end_iter: I::Iter = end.iter_elements();
                let spaces_iter: I::Iter = spaces.iter_elements();
                let iter = end_iter.chain(spaces_iter);
                iter
            });
        let parser = start.and(end);
        let p = parser.map(
            |(start, end): (
                Chain<<I as Input>::Iter, <I as Input>::Iter>,
                Chain<<I as Input>::Iter, <I as Input>::Iter>,
            )| {
                let result: String = start.chain(end).collect();

                // start is &[u8] or vec![char], same with end
                // chars implements .as_str() on its own, but &[u8] doesn't
                FirstStageToken::SingleLineTerminatedComment(result)
            },
        );
        p
    }
}

#[cfg(test)]
mod tests {
    use std::{char, marker::PhantomData};

    use super::IdentifySingleLineTerminatedComment;
    use crate::{
        base::{
            comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
            line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
        },
        first_stage::token::FirstStageToken,
    };

    use nom::{
        character::char,
        combinator::eof,
        error::{Error, ErrorKind, ParseError},
        AsChar, Compare, Err, IResult, Input, Parser,
    };

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl<'a> StartCommentSingleLine for TestStruct<'a> {
        fn is_start_comment_single_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '`'
        }

        fn start_comment_single_line_tag<'x>(&self) -> &'x str {
            "``"
        }
    }
    impl<'a> EndCommentSingleLine for TestStruct<'a> {
        fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '`'
        }

        fn end_comment_single_line_tag<'x>(&self) -> &'x str {
            "``"
        }
    }
    impl<'a> CarriageReturn for TestStruct<'a> {
        fn is_carriage_return<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '\r'
        }

        fn carriage_return<'x, I, E>(
            &self,
        ) -> impl Parser<I, Output = Self::CarriageReturnOutput, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
            Self::CarriageReturnOutput: AsChar,
        {
            char('\r')
        }

        type CarriageReturnOutput = char;
    }
    impl<'a> NewLine for TestStruct<'a> {
        type NewlineOutput = char;

        fn is_new_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '\n'
        }

        fn new_line<'x, I, E>(&self) -> impl Parser<I, Output = Self::NewlineOutput, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
        {
            char('\n')
        }
    }

    impl<'a> Eof for TestStruct<'a> {
        fn is_eof<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char().len() == 0
        }

        fn eof<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
        {
            eof
        }
    }

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: &str = "";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_partial_end_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: &str = "``Some text`";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Err(Err::Error(
            Error::from_error_kind("Some text`", ErrorKind::TakeUntil),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_carriage_return_inline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: &str = "``Some\r\n text`";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Err(Err::Error(
            Error::from_error_kind("\r\n text`", ErrorKind::Tag),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_new_line_inline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: &str = "``Some\n text`";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Err(Err::Error(
            Error::from_error_kind("\n text`", ErrorKind::Tag),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: &str = "``Some text``";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "",
            FirstStageToken::SingleLineTerminatedComment("``Some text``".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: &str = "``Some text``Other text";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "Other text",
            FirstStageToken::SingleLineTerminatedComment("``Some text``".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: &str = "Other text``Some text``";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
}
