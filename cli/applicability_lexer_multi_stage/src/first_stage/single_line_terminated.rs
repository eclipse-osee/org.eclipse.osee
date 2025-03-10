use nom::{
    character::multispace0, error::ParseError, AsChar, Compare, ExtendInto, FindSubstring, Input,
    Parser,
};

use crate::base::{
    comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineTerminated},
    line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    utils::{
        locatable::{position, Locatable},
        take_first::take_until_first3,
    },
};

use super::token::FirstStageToken;

pub trait IdentifySingleLineTerminatedComment {
    fn identify_comment_single_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<<I as ExtendInto>::Extender>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + Locatable + ExtendInto,
        <I as Input>::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifySingleLineTerminatedComment for T
where
    T: StartCommentSingleLineTerminated + EndCommentSingleLineTerminated + CarriageReturn + NewLine,
{
    fn identify_comment_single_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<<I as ExtendInto>::Extender>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + Locatable + ExtendInto,
        <I as Input>::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_single_line_terminated()
            .and(take_until_first3(
                self.carriage_return_tag(),
                self.new_line_tag(),
                self.end_comment_single_line_tag(),
            ))
            .map(|(start, text): (I, I)| (start, text));
        // note: we are taking every character until a newline or end comment,
        // but we only parse the end comment here to trigger a "fail" so that it avoids this branch
        // immediately
        let end = self
            .end_comment_single_line()
            .and(multispace0())
            .map(|(end, spaces): (I, I)| (end, spaces));
        let parser = position().and(start.and(end)).and(position());
        let p = parser.map(
            |((start_pos, (start, end)), end_pos): (
                ((usize, u32), ((I, I), (I, I))),
                (usize, u32),
            )| {
                let mut builder = start.0.new_builder();
                start.0.extend_into(&mut builder);
                start.1.extend_into(&mut builder);
                end.0.extend_into(&mut builder);
                end.1.extend_into(&mut builder);

                // start is &[u8] or vec![char], same with end
                // chars implements .as_str() on its own, but &[u8] doesn't
                FirstStageToken::SingleLineTerminatedComment(builder, start_pos, end_pos)
            },
        );
        p
    }
}

#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifySingleLineTerminatedComment;
    use crate::{
        base::{
            comment::single_line::{
                EndCommentSingleLineTerminated, StartCommentSingleLineTerminated,
            },
            line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
        },
        first_stage::token::FirstStageToken,
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
    impl<'a> StartCommentSingleLineTerminated for TestStruct<'a> {
        fn is_start_comment_single_line_terminated<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '`'
        }

        fn start_comment_single_line_terminated_tag<'x>(&self) -> &'x str {
            "``"
        }

        fn has_start_comment_single_line_terminated_support(&self) -> bool {
            true
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_partial_end_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text`");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(
                unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text`", ()) },
                ErrorKind::TakeUntil,
            )));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_carriage_return_inline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some\r\n text`");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(
                unsafe { LocatedSpan::new_from_raw_offset(6, 1, "\r\n text`", ()) },
                ErrorKind::Tag,
            )));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_new_line_inline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some\n text`");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(
                unsafe { LocatedSpan::new_from_raw_offset(6, 1, "\n text`", ()) },
                ErrorKind::Tag,
            )));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
                FirstStageToken::SingleLineTerminatedComment(
                    "``Some text``".to_string(),
                    (0, 1),
                    (13, 1),
                ),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``Other text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(13, 1, "Other text", ()) },
                FirstStageToken::SingleLineTerminatedComment(
                    "``Some text``".to_string(),
                    (0, 1),
                    (13, 1),
                ),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("Other text``Some text``");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
}
