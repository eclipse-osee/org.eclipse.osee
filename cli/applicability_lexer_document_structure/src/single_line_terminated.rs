use nom::{AsBytes, AsChar, Compare, Input, Mode, Parser};

use applicability_lexer_base::{
    comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineTerminated},
    document_structure::{DocumentStructureError, DocumentStructureToken},
    line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    utils::locatable::Locatable,
};

pub trait IdentifySingleLineTerminatedComment {
    fn identify_comment_single_line_terminated<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = DocumentStructureError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
        <I as Input>::Item: AsChar;
}

impl<T> IdentifySingleLineTerminatedComment for T
where
    T: StartCommentSingleLineTerminated + EndCommentSingleLineTerminated + CarriageReturn + NewLine,
{
    fn identify_comment_single_line_terminated<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = DocumentStructureError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
        <I as Input>::Item: AsChar,
    {
        SingleLineTerminatedCommentParser { doc: self }
    }
}
struct SingleLineTerminatedCommentParser<'single_line_parser, T> {
    doc: &'single_line_parser T,
}

impl<I, T> Parser<I> for SingleLineTerminatedCommentParser<'_, T>
where
    I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
    <I as Input>::Item: AsChar,
    T: StartCommentSingleLineTerminated + EndCommentSingleLineTerminated + CarriageReturn + NewLine,
{
    type Output = DocumentStructureToken<I>;
    type Error = DocumentStructureError<I>;

    #[inline(always)]
    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        let start_comment = self
            .doc
            .start_comment_single_line_terminated_position(&input.as_bytes());
        if start_comment.unwrap_or(1) > 0 {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::MissingOrIncorrectStartComment
            })));
        }
        let start_comment_unwrapped = start_comment.unwrap();
        let start_comment_ending_position =
            start_comment_unwrapped + self.doc.start_comment_single_line_terminated_tag().len();
        let post_start_input = input.take_from(start_comment_ending_position);
        let end_comment_search = self
            .doc
            .end_comment_single_line_position(&post_start_input.as_bytes());
        if end_comment_search.is_none() {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::MissingOrIncorrectEndComment
            })));
        }
        let end_comment = end_comment_search.unwrap();
        //this will take from input[0]...end_comment_search so we can find new lines and carriage returns within
        let end_input_for_search = post_start_input.take(end_comment);
        let carriage_return_search = self.doc.carriage_return_position(&end_input_for_search);
        let new_line_search = self.doc.new_line_position(&end_input_for_search);
        if carriage_return_search.is_some() || new_line_search.is_some() {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::IncorrectSequence
            })));
        }

        let end_comment_position = end_comment + self.doc.end_comment_single_line_tag().len();
        let post_end_input_for_search = input.take_from(end_comment);
        let cr_nl = post_end_input_for_search.compare(
            ("".to_string() + self.doc.carriage_return_tag() + self.doc.new_line_tag()).as_str(),
        );
        let nl = post_end_input_for_search.compare(self.doc.new_line_tag());
        let last_new_lines = match (cr_nl, nl) {
            (nom::CompareResult::Ok, nom::CompareResult::Ok) => 2,
            (nom::CompareResult::Ok, nom::CompareResult::Incomplete) => 2,
            (nom::CompareResult::Ok, nom::CompareResult::Error) => 2,
            (nom::CompareResult::Incomplete, nom::CompareResult::Ok) => 1,
            (nom::CompareResult::Incomplete, nom::CompareResult::Incomplete) => 0,
            (nom::CompareResult::Incomplete, nom::CompareResult::Error) => 0,
            (nom::CompareResult::Error, nom::CompareResult::Ok) => 1,
            (nom::CompareResult::Error, nom::CompareResult::Incomplete) => 0,
            (nom::CompareResult::Error, nom::CompareResult::Error) => 0,
        };
        let final_position = start_comment_ending_position + end_comment_position + last_new_lines;
        let remaining_input = input.take_from(final_position);
        let remaining_input_position = remaining_input.get_position();
        Ok((
            remaining_input,
            OM::Output::bind(|| {
                let start_pos = input.get_position();
                let resulting_input = input.take(final_position);
                DocumentStructureToken::SingleLineTerminatedComment(
                    resulting_input,
                    start_pos,
                    remaining_input_position,
                )
            }),
        ))
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifySingleLineTerminatedComment;
    use applicability_lexer_base::{
        comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineTerminated},
        document_structure::{DocumentStructureError, DocumentStructureToken},
        line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
    };

    use nom::{
        bytes::tag, combinator::eof, error::ParseError, AsChar, Compare, Err, IResult, Input,
        Parser,
    };
    use nom_locate::LocatedSpan;

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Err(Err::Error(
            DocumentStructureError::MissingOrIncorrectStartComment,
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_partial_end_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text`");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Err(Err::Error(
            DocumentStructureError::MissingOrIncorrectEndComment,
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_carriage_return_inline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some\r\n text``");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Err(Err::Error(DocumentStructureError::IncorrectSequence));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_new_line_inline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some\n text``");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Err(Err::Error(DocumentStructureError::IncorrectSequence));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            DocumentStructureToken::SingleLineTerminatedComment(
                LocatedSpan::new("``Some text``"),
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
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "Other text", ()) },
            DocumentStructureToken::SingleLineTerminatedComment(
                LocatedSpan::new("``Some text``"),
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
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Err(Err::Error(
            DocumentStructureError::MissingOrIncorrectStartComment,
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
