use nom::{
    character::multispace0, error::ParseError, AsChar, Compare, ExtendInto, FindSubstring, Input,
    Parser,
};

use crate::base::{
    comment::multi_line::{EndCommentMultiLine, StartCommentMultiLine},
    utils::locatable::{position, Locatable},
};

use super::token::FirstStageToken;

pub trait IdentifyMultiLineTerminatedComment {
    fn identify_comment_multi_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<<I as ExtendInto>::Extender>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + Locatable + ExtendInto,
        <I as Input>::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifyMultiLineTerminatedComment for T
where
    T: StartCommentMultiLine + EndCommentMultiLine,
{
    fn identify_comment_multi_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<<I as ExtendInto>::Extender>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + Locatable + ExtendInto,
        <I as Input>::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_multi_line()
            .and(self.take_until_end_comment_multi_line());
        let end = self.end_comment_multi_line().and(multispace0());
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
                FirstStageToken::MultiLineComment(builder, start_pos, end_pos)
            },
        );
        p
    }
}

#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifyMultiLineTerminatedComment;
    use crate::{
        base::comment::multi_line::{EndCommentMultiLine, StartCommentMultiLine},
        first_stage::token::FirstStageToken,
    };

    use nom::{
        error::{Error, ErrorKind, ParseError},
        AsChar, Err, IResult, Input, Parser,
    };
    use nom_locate::LocatedSpan;

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl<'a> StartCommentMultiLine for TestStruct<'a> {
        fn is_start_comment_multi_line<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '/'
        }

        fn start_comment_multi_line_tag<'x>(&self) -> &'x str {
            "/*"
        }
    }
    impl<'a> EndCommentMultiLine for TestStruct<'a> {
        fn is_end_comment_multi_line<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
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
        let mut parser = config.identify_comment_multi_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("/*Some text*/");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
                FirstStageToken::MultiLineComment("/*Some text*/".to_string(), (0, 1), (13, 1)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_with_new_lines() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("/*\r\nSome text\r\n\n*/");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(18, 4, "", ()) },
                FirstStageToken::MultiLineComment(
                    "/*\r\nSome text\r\n\n*/".to_string(),
                    (0, 1),
                    (18, 4),
                ),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("/*Some text*/Other text");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(13, 1, "Other text", ()) },
                FirstStageToken::MultiLineComment("/*Some text*/".to_string(), (0, 1), (13, 1)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("Other text/*Some text*/");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }
}
