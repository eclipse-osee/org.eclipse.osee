use nom::{combinator::rest, multi::many0, AsBytes, AsChar, Compare, FindSubstring, Input, Parser};

use applicability_lexer_base::{
    document_structure::{DocumentStructureError, DocumentStructureToken},
    utils::locatable::{position, Locatable},
};

use super::{
    document_structure_text::IdentifyDocumentStructureText,
    multi_line_terminated::IdentifyMultiLineTerminatedComment,
    single_line_non_terminated::IdentifySingleLineNonTerminatedComment,
    single_line_terminated::IdentifySingleLineTerminatedComment,
};

pub trait IdentifyComments {
    fn identify_comments<I>(
        &self,
    ) -> impl Parser<I, Output = Vec<DocumentStructureToken<I>>, Error = DocumentStructureError<I>>
    where
        I: Input
            + for<'x> Compare<&'x str>
            + for<'x> FindSubstring<&'x str>
            + Locatable
            + Send
            + Sync
            + AsBytes,
        <I as Input>::Item: AsChar;
}

impl<T> IdentifyComments for T
where
    T: IdentifyDocumentStructureText
        + IdentifyMultiLineTerminatedComment
        + IdentifySingleLineNonTerminatedComment
        + IdentifySingleLineTerminatedComment,
{
    #[inline(always)]
    fn identify_comments<I>(
        &self,
    ) -> impl Parser<I, Output = Vec<DocumentStructureToken<I>>, Error = DocumentStructureError<I>>
    where
        I: Input
            + for<'x> Compare<&'x str>
            + for<'x> FindSubstring<&'x str>
            + Locatable
            + Send
            + Sync
            + AsBytes,
        <I as Input>::Item: AsChar,
    {
        let inner_parser = self
            .identify_comment_single_line_terminated()
            .or(self.identify_comment_multi_line_terminated())
            .or(self.identify_comment_single_line_non_terminated())
            .or(self.identify_document_structure_text());
        many0(inner_parser)
            .and(position().and(rest).and(position()).map(
                |((start, x), end): (((usize, u32), I), (usize, u32))| {
                    DocumentStructureToken::Text(x, start, end)
                },
            ))
            .map(|(mut list, remaining)| {
                if remaining.get_inner().input_len() > 0 {
                    list.push(remaining);
                }
                list
            })
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use crate::result_type::ResultType;

    use super::IdentifyComments;
    use applicability_lexer_base::{
        comment::{
            multi_line::{EndCommentMultiLine, StartCommentMultiLine},
            single_line::{
                EndCommentSingleLineTerminated, StartCommentSingleLineNonTerminated,
                StartCommentSingleLineTerminated,
            },
        },
        document_structure::DocumentStructureToken,
        line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
    };

    use nom::{bytes::tag, combinator::eof, error::ParseError, AsChar, Compare, Input, Parser};
    use nom_locate::LocatedSpan;

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl StartCommentMultiLine for TestStruct<'_> {
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
    impl StartCommentSingleLineTerminated for TestStruct<'_> {
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
    impl EndCommentMultiLine for TestStruct<'_> {
        fn is_end_comment_multi_line<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '*'
        }

        fn end_comment_multi_line_tag<'x>(&self) -> &'x str {
            "*/"
        }

        fn has_end_comment_multi_line_support(&self) -> bool {
            true
        }
    }
    impl CarriageReturn for TestStruct<'_> {
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
    impl NewLine for TestStruct<'_> {
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

    impl Eof for TestStruct<'_> {
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
    impl EndCommentSingleLineTerminated for TestStruct<'_> {
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
    impl StartCommentSingleLineNonTerminated for TestStruct<'_> {
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
        let result: ResultType<&str> = Ok((LocatedSpan::new(""), vec![]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            vec![DocumentStructureToken::Text(
                LocatedSpan::new("Random string"),
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
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(26, 1, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::SingleLineTerminatedComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "``Some text``", ()) },
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
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(24, 1, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::SingleLineComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "``Some text", ()) },
                    (13, 1),
                    (24, 1),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_not_terminated_cr_nl() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text\r\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(26, 2, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::SingleLineComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "``Some text\r\n", ()) },
                    (13, 1),
                    (26, 2),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text``More text");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(35, 1, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::SingleLineTerminatedComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "``Some text``", ()) },
                    (13, 1),
                    (26, 1),
                ),
                DocumentStructureToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(26, 1, "More text", ()) },
                    (26, 1),
                    (35, 1),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_not_terminated_cr_nl_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text\r\nMore text");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(35, 2, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::SingleLineComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "``Some text\r\n", ()) },
                    (13, 1),
                    (26, 2),
                ),
                DocumentStructureToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(26, 2, "More text", ()) },
                    (26, 2),
                    (35, 2),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string/*\r\nSome text*/");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(28, 2, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::MultiLineComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "/*\r\nSome text*/", ()) },
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
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(37, 2, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::MultiLineComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "/*\r\nSome text*/", ()) },
                    (13, 1),
                    (28, 2),
                ),
                DocumentStructureToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(28, 2, "More text", ()) },
                    (28, 2),
                    (37, 2),
                ),
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
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(41, 2, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::MultiLineComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "/*\r\nSome text*/", ()) },
                    (13, 1),
                    (28, 2),
                ),
                DocumentStructureToken::SingleLineTerminatedComment(
                    unsafe { LocatedSpan::new_from_raw_offset(28, 2, "``More text``", ()) },
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
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(41, 2, "", ()) },
            vec![
                DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
                DocumentStructureToken::SingleLineTerminatedComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "``More text``", ()) },
                    (13, 1),
                    (26, 1),
                ),
                DocumentStructureToken::MultiLineComment(
                    unsafe { LocatedSpan::new_from_raw_offset(26, 1, "/*\r\nSome text*/", ()) },
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
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            vec![DocumentStructureToken::SingleLineTerminatedComment(
                LocatedSpan::new("``Some text``"),
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
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(22, 1, "", ()) },
            vec![
                DocumentStructureToken::SingleLineTerminatedComment(
                    LocatedSpan::new("``Some text``"),
                    (0, 1),
                    (13, 1),
                ),
                DocumentStructureToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "More text", ()) },
                    (13, 1),
                    (22, 1),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_single_line_comment_and_multi_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``/*More text*/");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(26, 1, "", ()) },
            vec![
                DocumentStructureToken::SingleLineTerminatedComment(
                    LocatedSpan::new("``Some text``"),
                    (0, 1),
                    (13, 1),
                ),
                DocumentStructureToken::MultiLineComment(
                    unsafe { LocatedSpan::new_from_raw_offset(13, 1, "/*More text*/", ()) },
                    (13, 1),
                    (26, 1),
                ),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
