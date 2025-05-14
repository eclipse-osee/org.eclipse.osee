use nom::{AsBytes, AsChar, Compare, Input, Mode, Parser};

use applicability_lexer_base::{
    comment::code_block::{EndCodeBlock, StartCodeBlock},
    document_structure::{DocumentStructureError, DocumentStructureToken},
    line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    utils::locatable::Locatable,
};

pub trait IdentifyCodeBlock {
    fn identify_code_block<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = DocumentStructureError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
        <I as Input>::Item: AsChar;
}

impl<T> IdentifyCodeBlock for T
where
    T: StartCodeBlock + EndCodeBlock + CarriageReturn + NewLine,
{
    fn identify_code_block<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = DocumentStructureError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
        <I as Input>::Item: AsChar,
    {
        CodeBlockParser { doc: self }
    }
}
struct CodeBlockParser<'single_line_parser, T> {
    doc: &'single_line_parser T,
}

impl<I, T> Parser<I> for CodeBlockParser<'_, T>
where
    I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
    <I as Input>::Item: AsChar,
    T: StartCodeBlock + EndCodeBlock + CarriageReturn + NewLine,
{
    type Output = DocumentStructureToken<I>;
    type Error = DocumentStructureError<I>;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        if !(self.doc.has_start_code_block_support() && self.doc.has_end_code_block_support()) {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::Unsupported
            })));
        }
        let start_code_block = self.doc.start_code_block_position(&input.as_bytes());
        if start_code_block.unwrap_or(1) > 0 {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::MissingOrIncorrectStartComment
            })));
        }
        let start_code_block_unwrapped = start_code_block.unwrap();
        let start_code_block_ending_position =
            start_code_block_unwrapped + self.doc.start_code_block_tag().len();
        let post_start_input = input.take_from(start_code_block_ending_position);
        let end_code_block_search = self
            .doc
            .end_code_block_position(&post_start_input.as_bytes());
        if end_code_block_search.is_none() {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::MissingOrIncorrectEndComment
            })));
        }
        let end_code_block = end_code_block_search.unwrap();
        let end_code_block_position = end_code_block + self.doc.end_code_block_tag().len();
        let post_end_input_for_search = post_start_input.take_from(end_code_block);
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
        let final_position =
            start_code_block_ending_position + end_code_block_position + last_new_lines;
        let remaining_input = input.take_from(final_position);
        let remaining_input_position = remaining_input.get_position();
        Ok((
            remaining_input,
            OM::Output::bind(|| {
                let start_pos = input.get_position();
                let resulting_input = input.take(final_position);
                DocumentStructureToken::CodeBlock(
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

    use super::IdentifyCodeBlock;
    use applicability_lexer_base::{
        comment::code_block::{EndCodeBlock, StartCodeBlock},
        document_structure::{DocumentStructureError, DocumentStructureToken},
        line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    };

    use nom::{AsChar, Compare, Err, IResult, Input, Parser, bytes::tag, error::ParseError};
    use nom_locate::LocatedSpan;

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl StartCodeBlock for TestStruct<'_> {
        fn is_start_code_block<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '/'
        }

        fn start_code_block_tag<'x>(&self) -> &'x str {
            "/*"
        }

        fn has_start_code_block_support(&self) -> bool {
            true
        }
    }
    impl EndCodeBlock for TestStruct<'_> {
        fn is_end_code_block<I>(&self, input: <I as Input>::Item) -> bool
        where
            I: Input,
            <I as Input>::Item: AsChar,
        {
            input.as_char() == '*'
        }

        fn end_code_block_tag<'x>(&self) -> &'x str {
            "*/"
        }

        fn has_end_code_block_support(&self) -> bool {
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_code_block();
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
    fn parse_code_block() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_code_block();
        let input: LocatedSpan<&str> = LocatedSpan::new("/*Some text*/");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            DocumentStructureToken::CodeBlock(LocatedSpan::new("/*Some text*/"), (0, 1), (13, 1)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_code_block_with_new_lines() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_code_block();
        let input: LocatedSpan<&str> = LocatedSpan::new("/*\r\nSome text\r\n\n*/");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(18, 4, "", ()) },
            DocumentStructureToken::CodeBlock(
                LocatedSpan::new("/*\r\nSome text\r\n\n*/"),
                (0, 1),
                (18, 4),
            ),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_code_block_trailing_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_code_block();
        let input: LocatedSpan<&str> = LocatedSpan::new("/*Some text*/Other text");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "Other text", ()) },
            DocumentStructureToken::CodeBlock(LocatedSpan::new("/*Some text*/"), (0, 1), (13, 1)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_code_block_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_code_block();
        let input: LocatedSpan<&str> = LocatedSpan::new("Other text/*Some text*/");
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
