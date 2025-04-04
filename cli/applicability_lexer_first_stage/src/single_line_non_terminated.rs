use nom::{
    bytes::take_till, combinator::not, error::ParseError, AsChar, Compare, ExtendInto, Input,
    Parser,
};

use applicability_lexer_base::{
    comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineNonTerminated},
    line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
    utils::locatable::{position, Locatable},
};

use super::token::FirstStageToken;

pub trait IdentifySingleLineNonTerminatedComment {
    fn identify_comment_single_line_non_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<<I as ExtendInto>::Extender>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + ExtendInto,
        <I as ExtendInto>::Extender: Send + Sync,
        <I as Input>::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifySingleLineNonTerminatedComment for T
where
    T: StartCommentSingleLineNonTerminated
        + EndCommentSingleLineTerminated
        + CarriageReturn
        + NewLine
        + Eof,
{
    fn identify_comment_single_line_non_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<<I as ExtendInto>::Extender>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + ExtendInto,
        <I as ExtendInto>::Extender: Send + Sync,
        <I as Input>::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_single_line_non_terminated()
            .and(take_till(|x| {
                self.is_carriage_return::<I>(x) || self.is_new_line::<I>(x)
            }))
            .and(not(self.end_comment_single_line()))
            .map(|((start, text), _): ((I, I), ())| (start, text));
        let windows_new_line = self
            .carriage_return()
            .and(self.new_line())
            .map(|x| (Some(x.0), Some(x.1)));
        let unix_new_line = self.new_line().map(|x| (None, Some(x)));
        let eof_termination = self.eof().map(|_| (None, None));
        let end = windows_new_line.or(unix_new_line).or(eof_termination);
        let parser = position().and(start.and(end)).and(position());
        let p = parser.map(
            |((start_pos, (start, end)), end_pos): (
                ((usize, u32), ((I, I), (Option<I>, Option<I>))),
                (usize, u32),
            )| {
                let mut builder = start.0.new_builder();
                start.0.extend_into(&mut builder);
                start.1.extend_into(&mut builder);
                if let Some(x) = end.0 {
                    let transform: I = x.into();
                    transform.extend_into(&mut builder)
                }
                if let Some(x) = end.1 {
                    let transform: I = x.into();
                    transform.extend_into(&mut builder)
                }

                // start is &[u8] or vec![char], end is Option<char>, char
                // chars implements .as_str() on its own, but &[u8] doesn't
                FirstStageToken::SingleLineComment(builder, start_pos, end_pos)
            },
        );
        p
    }
}

#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifySingleLineNonTerminatedComment;
    use crate::token::FirstStageToken;
    use applicability_lexer_base::{
        comment::single_line::{
            EndCommentSingleLineTerminated, StartCommentSingleLineNonTerminated,
        },
        line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
    };

    use nom::{
        bytes::tag,
        combinator::eof,
        error::{Error, ErrorKind, ParseError},
        AsChar, Compare, Err, IResult, Input, Parser,
    };
    use nom_locate::LocatedSpan;

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl<'a> StartCommentSingleLineNonTerminated for TestStruct<'a> {
        fn is_start_comment_single_line_non_terminated<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '`'
        }

        fn start_comment_single_line_non_terminated_tag<'x>(&self) -> &'x str {
            "``"
        }

        fn has_start_comment_single_line_non_terminated_support(&self) -> bool {
            true
        }
    }
    impl<'a> CarriageReturn for TestStruct<'a> {
        fn is_carriage_return<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '\r'
        }

        fn carriage_return<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
            O: From<I>,
        {
            tag("\r").map(|x: I| x.into())
        }
    }
    impl<'a> NewLine for TestStruct<'a> {
        fn is_new_line<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '\n'
        }

        fn new_line<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            O: From<I>,
            E: ParseError<I>,
        {
            tag("\n").map(|x: I| x.into())
        }
    }

    impl<'a> Eof for TestStruct<'a> {
        fn is_eof<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char().len() == 0
        }

        fn eof<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
        where
            I: Input + Compare<&'x str>,
            <I as Input>::Item: AsChar,
            E: ParseError<I>,
        {
            eof
        }
    }
    impl<'a> EndCommentSingleLineTerminated for TestStruct<'a> {
        fn is_end_comment_single_line<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '`'
        }

        fn end_comment_single_line_tag<'x>(&self) -> &'x str {
            "``"
        }

        fn has_end_comment_single_line_terminated_support(&self) -> bool {
            true
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
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(13, 2, "", ()) },
                FirstStageToken::SingleLineComment("``Some text\r\n".to_string(), (0, 1), (13, 2)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_eof() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(11, 1, "", ()) },
                FirstStageToken::SingleLineComment("``Some text".to_string(), (0, 1), (11, 1)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_broken_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\r");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(
                unsafe { LocatedSpan::new_from_raw_offset(11, 1, "\r", ()) },
                ErrorKind::Eof,
            )));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_unix_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\n");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(12, 2, "", ()) },
                FirstStageToken::SingleLineComment("``Some text\n".to_string(), (0, 1), (12, 2)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_windows_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\r\nOther text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(13, 2, "Other text", ()) },
                FirstStageToken::SingleLineComment("``Some text\r\n".to_string(), (0, 1), (13, 2)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_unix_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\nOther text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(12, 2, "Other text", ()) },
                FirstStageToken::SingleLineComment("``Some text\n".to_string(), (0, 1), (12, 2)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_broken_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\rOther text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(
                unsafe { LocatedSpan::new_from_raw_offset(11, 1, "\rOther text", ()) },
                ErrorKind::Eof,
            )));
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
