use std::iter::Chain;

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
    fn identify_comment_single_line_non_terminated<'x, I, O, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str>,
        O: CustomToString + FromIterator<I::Item>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifySingleLineNonTerminatedComment for T
where
    T: StartCommentSingleLine + CarriageReturn + NewLine,
{
    fn identify_comment_single_line_non_terminated<'x, I, O, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str>,
        O: CustomToString + FromIterator<I::Item>,
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
            .map(|x| (x.0, Some(x.1)));
        let unix_new_line = self.new_line().map(|x| (x, None));
        let end = windows_new_line.or(unix_new_line);
        let parser = start.and(end);
        let p = parser.map(
            |(start, end): (
                Chain<<I as Input>::Iter, <I as Input>::Iter>,
                (I, Option<I>),
            )| {
                let result_vec: O = start.collect::<O>();
                let result = result_vec.custom_to_string();

                // start is &[u8] or vec![char], same with end
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
        base::comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
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
    impl<'a> EndCommentSingleLine for TestStruct<'a> {
        fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '`'
        }

        fn end_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
        {
            tag("``")
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
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
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
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
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
        let mut parser = config.identify_comment_single_line_non_terminated::<_, Vec<char>, _>();
        let input: &str = "Other text``Some text``";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
}
