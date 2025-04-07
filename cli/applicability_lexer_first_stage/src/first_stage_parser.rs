use nom::{
    combinator::rest, error::ParseError, multi::many0, AsChar, Compare, ExtendInto, FindSubstring,
    Input, Parser,
};

use applicability_lexer_base::utils::{
    has_length::HasLength,
    locatable::{position, Locatable},
};

use super::{
    first_stage_text::IdentifyFirstStageText,
    multi_line_terminated::IdentifyMultiLineTerminatedComment,
    single_line_non_terminated::IdentifySingleLineNonTerminatedComment,
    single_line_terminated::IdentifySingleLineTerminatedComment, token::FirstStageToken,
};

pub trait IdentifyComments {
    fn identify_comments<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<FirstStageToken<<I as ExtendInto>::Extender>>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString + Locatable + ExtendInto,
        <I as ExtendInto>::Extender: HasLength + Send + Sync,
        <I as Input>::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifyComments for T
where
    T: IdentifyFirstStageText
        + IdentifyMultiLineTerminatedComment
        + IdentifySingleLineNonTerminatedComment
        + IdentifySingleLineTerminatedComment,
{
    fn identify_comments<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<FirstStageToken<<I as ExtendInto>::Extender>>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString + Locatable + ExtendInto,
        <I as ExtendInto>::Extender: HasLength + Send + Sync,
        <I as Input>::Item: AsChar,
        E: ParseError<I>,
    {
        let inner_parser = self
            // .identify_comment_single_line_terminated()
            // .identify_comment_multi_line_terminated()
            // .or(self.identify_comment_single_line_non_terminated())
            .identify_first_stage_text();
        let parser = many0(inner_parser)
            .and(position().and(rest).and(position()).map(
                |((start, x), end): (((usize, u32), I), (usize, u32))| {
                    let mut builder = x.new_builder();
                    x.extend_into(&mut builder);
                    FirstStageToken::Text(builder, start, end)
                },
            ))
            .map(|(mut list, remaining)| {
                if remaining.get_inner().len() > 0 {
                    list.push(remaining);
                }
                list
            });
        parser
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifyComments;
    use crate::token::FirstStageToken;
    use applicability_lexer_base::{
        comment::{
            multi_line::{EndCommentMultiLine, StartCommentMultiLine},
            single_line::{
                EndCommentSingleLineTerminated, StartCommentSingleLineNonTerminated,
                StartCommentSingleLineTerminated,
            },
        },
        line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
    };

    use nom::{
        bytes::tag,
        combinator::eof,
        error::{Error, ParseError},
        AsChar, Compare, IResult, Input, Parser,
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

        fn has_start_comment_multi_line_support(&self) -> bool {
            true
        }
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

        fn has_end_comment_multi_line_support(&self) -> bool {
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((LocatedSpan::new(""), vec![]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            vec![FirstStageToken::Text(
                "Random string".to_string(),
                (0, 1),
                (13, 1),
            )],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_text_with_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(26, 1, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::SingleLineTerminatedComment(
                    "``Some text``".to_string(),
                    (13, 1),
                    (26, 1),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_not_terminated() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(24, 1, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::SingleLineComment("``Some text".to_string(), (13, 1), (24, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_not_terminated_cr_nl() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text\r\n");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(26, 2, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::SingleLineComment("``Some text\r\n".to_string(), (13, 1), (26, 2)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text``More text");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(35, 1, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::SingleLineTerminatedComment(
                    "``Some text``".to_string(),
                    (13, 1),
                    (26, 1),
                ),
                FirstStageToken::Text("More text".to_string(), (26, 1), (35, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_not_terminated_cr_nl_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text\r\nMore text");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(35, 2, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::SingleLineComment("``Some text\r\n".to_string(), (13, 1), (26, 2)),
                FirstStageToken::Text("More text".to_string(), (26, 2), (35, 2)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string/*\r\nSome text*/");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(28, 2, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::MultiLineComment(
                    "/*\r\nSome text*/".to_string(),
                    (13, 1),
                    (28, 2),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string/*\r\nSome text*/More text");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(37, 2, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::MultiLineComment(
                    "/*\r\nSome text*/".to_string(),
                    (13, 1),
                    (28, 2),
                ),
                FirstStageToken::Text("More text".to_string(), (28, 2), (37, 2)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment_and_single_line() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> =
            LocatedSpan::new("Random string/*\r\nSome text*/``More text``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(41, 2, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::MultiLineComment(
                    "/*\r\nSome text*/".to_string(),
                    (13, 1),
                    (28, 2),
                ),
                FirstStageToken::SingleLineTerminatedComment(
                    "``More text``".to_string(),
                    (28, 2),
                    (41, 2),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_and_multi_line() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> =
            LocatedSpan::new("Random string``More text``/*\r\nSome text*/");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(41, 2, "", ()) },
            vec![
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
                FirstStageToken::SingleLineTerminatedComment(
                    "``More text``".to_string(),
                    (13, 1),
                    (26, 1),
                ),
                FirstStageToken::MultiLineComment(
                    "/*\r\nSome text*/".to_string(),
                    (26, 1),
                    (41, 2),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            vec![FirstStageToken::SingleLineTerminatedComment(
                "``Some text``".to_string(),
                (0, 1),
                (13, 1),
            )],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_single_line_comment_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``More text");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(22, 1, "", ()) },
            vec![
                FirstStageToken::SingleLineTerminatedComment(
                    "``Some text``".to_string(),
                    (0, 1),
                    (13, 1),
                ),
                FirstStageToken::Text("More text".to_string(), (13, 1), (22, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_single_line_comment_and_multi_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``/*More text*/");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<FirstStageToken<String>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(26, 1, "", ()) },
            vec![
                FirstStageToken::SingleLineTerminatedComment(
                    "``Some text``".to_string(),
                    (0, 1),
                    (13, 1),
                ),
                FirstStageToken::MultiLineComment("/*More text*/".to_string(), (13, 1), (26, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
