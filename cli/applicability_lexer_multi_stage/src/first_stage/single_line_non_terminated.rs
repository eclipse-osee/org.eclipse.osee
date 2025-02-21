use std::{iter::Chain, ops::Add};

use nom::{
    bytes::take_till, character::multispace0, error::ParseError, AsChar, Compare, Input, Parser,
};

use crate::base::{
    comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
    custom_string_traits::CustomToString,
    line_terminations::{
        carriage_return::{self, CarriageReturn},
        new_line::NewLine,
    },
};

use super::token::FirstStageToken;

pub trait IdentifySingleLineNonTerminatedComment {
    type CommentOutput1;
    type CommentOutput2;
    type Output;
    fn identify_comment_single_line_non_terminated<'x, I, O, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<Self::Output>, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        O: CustomToString + FromIterator<I::Item>,

        //Note for myself: This bound has to be here: Into<Output<Add<>> for CommentOutput1/2
        //
        // Self::Output: Add<Self::CommentOutput1, Output = Self::Output>,
        // Self::Output: Add<Self::CommentOutput2, Output = Self::Output>,
        // + Add<Self::CommentOutput1, Output = O>+Add<Self::CommentOutput2, Output = O>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifySingleLineNonTerminatedComment for T
where
    T: StartCommentSingleLine + CarriageReturn + NewLine,
    T::NewlineOutput: AsChar,
    T::CarriageReturnOutput: AsChar,
{
    type CommentOutput1 = T::CarriageReturnOutput;
    type CommentOutput2 = T::NewlineOutput;
    type Output = String;
    fn identify_comment_single_line_non_terminated<'x, I, O, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<Self::Output>, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        O: CustomToString + FromIterator<I::Item>,
        // Self::Output: Add<Self::CommentOutput1, Output = Self::Output>,
        // Self::Output: Add<Self::CommentOutput2, Output = Self::Output>,
        // + Add<Self::CommentOutput1, Output = O>+ Add<Self::CommentOutput2, Output = O>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_single_line()
            .and(take_till(|x| {
                self.is_carriage_return::<I>(x) || self.is_new_line::<I>(x)
            }))
            .map(|(start, text): (I, I)| {
                let start_iter: I::Iter = start.iter_elements();
                let text_iter: I::Iter = text.iter_elements();
                let iter = start_iter.chain(text_iter);
                iter
            });
        let windows_new_line = self
            .carriage_return()
            .and(self.new_line())
            .map(|x| (Some(x.0), x.1));
        let unix_new_line = self.new_line().map(|x| (None, x));
        let end = windows_new_line.or(unix_new_line);
        let parser = start.and(end);
        let p = parser.map(
            |(start, end): (
                Chain<<I as Input>::Iter, <I as Input>::Iter>,
                (Option<Self::CommentOutput1>, Self::CommentOutput2),
            )| {
                let result_vec: O = start.collect::<O>();
                let mut result: Self::Output = result_vec.custom_to_string();
                if let Some(x) = end.0 {
                    result.push(x.as_char());
                }
                result.push(end.1.as_char());

                // start is &[u8] or vec![char], end is Option<char>, char
                // chars implements .as_str() on its own, but &[u8] doesn't
                FirstStageToken::SingleLineComment(result)
            },
        );
        p
    }
}

#[cfg(test)]
mod tests {
    use std::{char, marker::PhantomData};

    use super::IdentifySingleLineNonTerminatedComment;
    use crate::{
        base::{
            comment::single_line::StartCommentSingleLine,
            line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
        },
        first_stage::token::FirstStageToken,
    };

    use nom::{
        bytes::tag,
        character::char,
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

        fn start_comment_single_line<'x, I, E>(&self) -> impl nom::Parser<I, Output = I, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
        {
            tag("``")
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_windows_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "``Some text\r\n";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "",
            FirstStageToken::SingleLineComment("``Some text\r\n".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_broken_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "``Some text\r";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind("\r", ErrorKind::Char)));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_unix_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "``Some text\n";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "",
            FirstStageToken::SingleLineComment("``Some text\n".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_windows_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "``Some text\r\nOther text";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "Other text",
            FirstStageToken::SingleLineComment("``Some text\r\n".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_unix_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "``Some text\nOther text";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "Other text",
            FirstStageToken::SingleLineComment("``Some text\n".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_broken_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "``Some text\rOther text";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Err(Err::Error(
            Error::from_error_kind("\rOther text", ErrorKind::Char),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "Other text``Some text``";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
}
