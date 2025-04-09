use std::cmp;

use nom::{error::ErrorKind, AsBytes, AsChar, Input, Mode, Parser};

use applicability_lexer_base::{
    comment::{
        multi_line::StartCommentMultiLine,
        single_line::{StartCommentSingleLineNonTerminated, StartCommentSingleLineTerminated},
    },
    document_structure::{DocumentStructureError, DocumentStructureToken},
    utils::locatable::Locatable,
};

pub trait IdentifyDocumentStructureText {
    fn identify_document_structure_text<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = DocumentStructureError<I>>
    where
        I: Input + Send + Sync + AsBytes + Locatable,
        <I as Input>::Item: AsChar;
}
impl<T> IdentifyDocumentStructureText for T
where
    T: StartCommentSingleLineTerminated
        + StartCommentMultiLine
        + StartCommentSingleLineNonTerminated,
{
    #[inline(always)]
    fn identify_document_structure_text<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = DocumentStructureError<I>>
    where
        I: Input + Send + Sync + AsBytes + Locatable,
        <I as Input>::Item: AsChar,
    {
        IdentifyDocumentStructureTextLexer { doc: self }
    }
}

struct IdentifyDocumentStructureTextLexer<'single_line_parser, T> {
    doc: &'single_line_parser T,
}

impl<I: Input + Send + Sync + AsBytes, T> Parser<I> for IdentifyDocumentStructureTextLexer<'_, T>
where
    I: Input + Send + Sync + AsBytes + Locatable,
    <I as Input>::Item: AsChar,
    T: StartCommentSingleLineTerminated
        + StartCommentMultiLine
        + StartCommentSingleLineNonTerminated,
{
    type Output = DocumentStructureToken<I>;
    type Error = DocumentStructureError<I>;

    #[inline(always)]
    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        let parse_to_position: Option<usize> = match (
            self.doc.has_start_comment_multi_line_support(),
            self.doc.has_start_comment_single_line_terminated_support(),
            self.doc
                .has_start_comment_single_line_non_terminated_support(),
        ) {
            (true, true, true) => {
                let multi_line = self.doc.start_comment_multi_line_position(&input);
                let terminated = self
                    .doc
                    .start_comment_single_line_terminated_position(&input);
                let non_terminated = self
                    .doc
                    .start_comment_single_line_non_terminated_position(&input);
                match (multi_line, terminated, non_terminated) {
                    (None, None, None) => None,
                    (None, None, Some(nt)) => Some(nt),
                    (None, Some(t), None) => Some(t),
                    (None, Some(t), Some(nt)) => Some(cmp::min(t, nt)),
                    (Some(ml), None, None) => Some(ml),
                    (Some(ml), None, Some(nt)) => Some(cmp::min(ml, nt)),
                    (Some(ml), Some(t), None) => Some(cmp::min(ml, t)),
                    (Some(ml), Some(t), Some(nt)) => Some(cmp::min(cmp::min(ml, t), nt)),
                }
            }
            (true, true, false) => {
                let multi_line = self.doc.start_comment_multi_line_position(&input);
                let terminated = self
                    .doc
                    .start_comment_single_line_terminated_position(&input);
                match (multi_line, terminated) {
                    (None, None) => None,
                    (None, Some(t)) => Some(t),
                    (Some(ml), None) => Some(ml),
                    (Some(ml), Some(t)) => Some(cmp::min(ml, t)),
                }
            }
            (true, false, true) => {
                let multi_line = self.doc.start_comment_multi_line_position(&input);
                let non_terminated = self
                    .doc
                    .start_comment_single_line_non_terminated_position(&input);
                match (multi_line, non_terminated) {
                    (None, None) => None,
                    (None, Some(nt)) => Some(nt),
                    (Some(ml), None) => Some(ml),
                    (Some(ml), Some(nt)) => Some(cmp::min(ml, nt)),
                }
            }
            (true, false, false) => self.doc.start_comment_multi_line_position(&input),
            (false, true, true) => {
                let terminated = self
                    .doc
                    .start_comment_single_line_terminated_position(&input);
                let non_terminated = self
                    .doc
                    .start_comment_single_line_non_terminated_position(&input);
                match (terminated, non_terminated) {
                    (None, None) => None,
                    (None, Some(nt)) => Some(nt),
                    (Some(t), None) => Some(t),
                    (Some(t), Some(nt)) => Some(cmp::min(t, nt)),
                }
            }
            (false, true, false) => self
                .doc
                .start_comment_single_line_terminated_position(&input),
            (false, false, true) => self
                .doc
                .start_comment_single_line_non_terminated_position(&input),
            (false, false, false) => None,
        };
        if parse_to_position.is_none() {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::Nom(input, ErrorKind::TakeUntil)
            })));
        }
        let position_to_take = parse_to_position.unwrap();
        let remaining_input = input.take_from(position_to_take);
        let remaining_input_position = remaining_input.get_position();
        Ok((
            remaining_input,
            OM::Output::bind(|| {
                let start_pos = input.get_position();
                let resulting_input = input.take(position_to_take);
                DocumentStructureToken::Text(resulting_input, start_pos, remaining_input_position)
            }),
        ))
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifyDocumentStructureText;
    use applicability_lexer_base::{
        comment::{
            multi_line::StartCommentMultiLine,
            single_line::{StartCommentSingleLineNonTerminated, StartCommentSingleLineTerminated},
        },
        document_structure::{DocumentStructureError, DocumentStructureToken},
    };

    use nom::{error::ErrorKind, AsChar, Err, IResult, Input, Parser};
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
    impl StartCommentSingleLineNonTerminated for TestStruct<'_> {
        fn is_start_comment_single_line_non_terminated<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '/'
        }

        fn start_comment_single_line_non_terminated_tag<'x>(&self) -> &'x str {
            "//"
        }

        fn has_start_comment_single_line_non_terminated_support(&self) -> bool {
            true
        }
    }

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_document_structure_text();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Err(Err::Error(DocumentStructureError::Nom(
            input,
            ErrorKind::TakeUntil,
        )));
        // Err(Err::Error(Error::from_error_kind(
        //     input,
        //     ErrorKind::TakeUntil,
        // )));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_text_with_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_document_structure_text();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "``Some text``", ()) },
            DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_document_structure_text();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string/*\r\nSome text*/");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "/*\r\nSome text*/", ()) },
            DocumentStructureToken::Text(LocatedSpan::new("Random string"), (0, 1), (13, 1)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
