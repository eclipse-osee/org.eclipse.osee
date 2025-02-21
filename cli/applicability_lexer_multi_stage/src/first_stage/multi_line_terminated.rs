use std::iter::Chain;

use nom::{
    character::multispace0, error::ParseError, AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::base::{
    comment::multi_line::{EndCommentMultiLine, StartCommentMultiLine},
    custom_string_traits::CustomToString,
};

use super::token::FirstStageToken;

pub trait IdentifyMultiLineTerminatedComment {
    fn identify_comment_multi_line_terminated<'x, I, O, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        O: CustomToString + FromIterator<I::Item>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifyMultiLineTerminatedComment for T
where
    T: StartCommentMultiLine + EndCommentMultiLine,
{
    fn identify_comment_multi_line_terminated<'x, I, O, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        O: CustomToString + FromIterator<I::Item>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_multi_line()
            .and(self.take_until_end_comment_multi_line())
            .map(|(start, text): (I, I)| {
                let start_iter: I::Iter = start.iter_elements();
                let text_iter: I::Iter = text.iter_elements();
                let iter = start_iter.chain(text_iter);
                iter
            });
        let end = self
            .end_comment_multi_line()
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
                let result_vec: O = start.chain(end).collect::<O>();
                let result = result_vec.custom_to_string();

                // start is &[u8] or vec![char], same with end
                // chars implements .as_str() on its own, but &[u8] doesn't
                FirstStageToken::MultiLineComment(result)
            },
        );
        p
    }
}

#[cfg(test)]
mod tests {
    use std::{char, marker::PhantomData};

    use super::IdentifyMultiLineTerminatedComment;
    use crate::{
        base::comment::multi_line::{EndCommentMultiLine, StartCommentMultiLine},
        first_stage::token::FirstStageToken,
    };

    use nom::{
        error::{Error, ErrorKind, ParseError},
        AsChar, Err, IResult, Input, Parser,
    };

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl<'a> StartCommentMultiLine for TestStruct<'a> {
        fn is_start_comment_multi_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '/'
        }

        fn start_comment_multi_line_tag<'x>(&self) -> &'x str {
            "/*"
        }
    }
    impl<'a> EndCommentMultiLine for TestStruct<'a> {
        fn is_end_comment_multi_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '/'
        }

        fn end_comment_multi_line_tag<'x>(&self) -> &'x str {
            "*/"
        }
    }

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated::<_, Vec<char>, _>();
        let input: &str = "";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated::<_, Vec<char>, _>();
        let input: &str = "/*Some text*/";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "",
            FirstStageToken::MultiLineComment("/*Some text*/".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_with_new_lines() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated::<_, Vec<char>, _>();
        let input: &str = "/*\r\nSome text\r\n\n*/";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "",
            FirstStageToken::MultiLineComment("/*\r\nSome text\r\n\n*/".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated::<_, Vec<char>, _>();
        let input: &str = "/*Some text*/Other text";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "Other text",
            FirstStageToken::MultiLineComment("/*Some text*/".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated::<_, Vec<char>, _>();
        let input: &str = "Other text/*Some text*/";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
}
