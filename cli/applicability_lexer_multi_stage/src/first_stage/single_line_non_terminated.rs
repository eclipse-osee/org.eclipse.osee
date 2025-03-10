use std::iter::Chain;

use nom::{bytes::take_till, combinator::not, error::ParseError, AsChar, Compare, Input, Parser};

use crate::base::{
    comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
    line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine}, utils::locatable::{position, Locatable},
};

use super::token::FirstStageToken;

pub trait IdentifySingleLineNonTerminatedComment {
    type CommentOutput1;
    type CommentOutput2;
    type Output;
    fn identify_comment_single_line_non_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str>+Locatable,
        I::Item: AsChar,
        String: FromIterator<<I as Input>::Item>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifySingleLineNonTerminatedComment for T
where
    T: StartCommentSingleLine + EndCommentSingleLine + CarriageReturn + NewLine + Eof,
    T::NewlineOutput: AsChar,
    T::CarriageReturnOutput: AsChar,
{
    type CommentOutput1 = T::CarriageReturnOutput;
    type CommentOutput2 = T::NewlineOutput;
    type Output = String;
    fn identify_comment_single_line_non_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str>+Locatable,
        I::Item: AsChar,
        String: FromIterator<<I as Input>::Item>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_single_line()
            .and(take_till(|x| {
                self.is_carriage_return::<I>(x) || self.is_new_line::<I>(x)
            }))
            .and(not(self.end_comment_single_line()))
            .map(|((start, text), _): ((I, I), ())| {
                let start_iter: I::Iter = start.iter_elements();
                let text_iter: I::Iter = text.iter_elements();
                let iter = start_iter.chain(text_iter);
                iter
            });
        let windows_new_line = self
            .carriage_return()
            .and(self.new_line())
            .map(|x| (Some(x.0), Some(x.1)));
        let unix_new_line = self.new_line().map(|x| (None, Some(x)));
        let eof_termination = self.eof().map(|_| (None, None));
        let end = windows_new_line.or(unix_new_line).or(eof_termination);
        let parser = position().and(start.and(end)).and(position());
        let p = parser.map(
            
            |((start_pos,(start,end)),end_pos)
            :(
                (
                    (usize, u32), 
                    (Chain<<I as Input>::Iter, <I as Input>::Iter>, 
                    (Option<Self::CommentOutput1>, Option<Self::CommentOutput2>)
                )
            )
            , (usize, u32)
        )|
            {
                let mut result: String = start.collect();
                // let mut result: Self::Output = result_vec.custom_to_string();
                if let Some(x) = end.0 {
                    result.push(x.as_char());
                }
                if let Some(x) = end.1 {
                    result.push(x.as_char());
                }

                // start is &[u8] or vec![char], end is Option<char>, char
                // chars implements .as_str() on its own, but &[u8] doesn't
                FirstStageToken::SingleLineComment(result,start_pos,end_pos)
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
    use nom_locate::LocatedSpan;

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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_windows_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\r\n");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> = Ok((
            unsafe{LocatedSpan::new_from_raw_offset(13,2,"",())},
            FirstStageToken::SingleLineComment("``Some text\r\n".to_string(),(0,1),(13,2)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_eof() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> = Ok((
            unsafe{LocatedSpan::new_from_raw_offset(11,1,"",())},
            FirstStageToken::SingleLineComment("``Some text".to_string(),(0,1),(11,1)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_broken_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\r");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(unsafe{LocatedSpan::new_from_raw_offset(11,1,"\r",())}, ErrorKind::Eof)));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_unix_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\n");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> = Ok((
            unsafe{LocatedSpan::new_from_raw_offset(12,2,"",())},
            FirstStageToken::SingleLineComment("``Some text\n".to_string(),(0,1),(12,2)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_windows_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\r\nOther text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> = Ok((
            unsafe{LocatedSpan::new_from_raw_offset(13,2,"Other text",())},
            FirstStageToken::SingleLineComment("``Some text\r\n".to_string(),(0,1),(13,2)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_unix_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\nOther text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> = Ok((
            unsafe{LocatedSpan::new_from_raw_offset(12,2,"Other text",())},
            FirstStageToken::SingleLineComment("``Some text\n".to_string(),(0,1),(12,2)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_broken_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\rOther text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> = Err(Err::Error(
            Error::from_error_kind(unsafe{LocatedSpan::new_from_raw_offset(11,1,"\rOther text",())}, ErrorKind::Eof),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("Other text``Some text``");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
}
