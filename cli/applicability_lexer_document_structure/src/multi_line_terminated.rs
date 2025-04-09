use nom::{AsBytes, AsChar, Compare, Input, Mode, Parser};

use applicability_lexer_base::{
    comment::multi_line::{EndCommentMultiLine, StartCommentMultiLine},
    document_structure::{DocumentStructureError, DocumentStructureToken},
    line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    utils::locatable::Locatable,
};

pub trait IdentifyMultiLineTerminatedComment {
    fn identify_comment_multi_line_terminated<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = DocumentStructureError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
        <I as Input>::Item: AsChar;
}

impl<T> IdentifyMultiLineTerminatedComment for T
where
    T: StartCommentMultiLine + EndCommentMultiLine + CarriageReturn + NewLine,
{
    fn identify_comment_multi_line_terminated<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = DocumentStructureError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
        <I as Input>::Item: AsChar,
    {
        // let start = self
        //     .start_comment_multi_line()
        //     .and(self.take_until_end_comment_multi_line());
        // let end = self.end_comment_multi_line().and(multispace0());
        // let parser = position().and(start.and(end)).and(position());
        // let p = parser.map(
        //     |((start_pos, (start, end)), end_pos): (
        //         ((usize, u32), ((I, I), (I, I))),
        //         (usize, u32),
        //     )| {
        //         let mut builder = start.0.new_builder();
        //         start.0.extend_into(&mut builder);
        //         start.1.extend_into(&mut builder);
        //         end.0.extend_into(&mut builder);
        //         end.1.extend_into(&mut builder);

        //         // start is &[u8] or vec![char], same with end
        //         // chars implements .as_str() on its own, but &[u8] doesn't
        //         DocumentStructureToken::MultiLineComment(builder, start_pos, end_pos)
        //     },
        // );
        // p
        MultiLineCommentParser { doc: self }
    }
}
struct MultiLineCommentParser<'single_line_parser, T> {
    doc: &'single_line_parser T,
}

impl<I, T> Parser<I> for MultiLineCommentParser<'_, T>
where
    I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
    <I as Input>::Item: AsChar,
    T: StartCommentMultiLine + EndCommentMultiLine + CarriageReturn + NewLine,
{
    type Output = DocumentStructureToken<I>;
    type Error = DocumentStructureError<I>;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        if !(self.doc.has_start_comment_multi_line_support()
            && self.doc.has_end_comment_multi_line_support())
        {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::Unsupported
            })));
        }
        let start_comment = self
            .doc
            .start_comment_multi_line_position(&input.as_bytes());
        if start_comment.unwrap_or(1) > 0 {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::MissingOrIncorrectStartComment
            })));
        }
        let start_comment_unwrapped = start_comment.unwrap();
        let start_comment_ending_position =
            start_comment_unwrapped + self.doc.start_comment_multi_line_tag().len();
        let post_start_input = input.take_from(start_comment_ending_position);
        let end_comment_search = self
            .doc
            .end_comment_multi_line_position(&post_start_input.as_bytes());
        if end_comment_search.is_none() {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                DocumentStructureError::MissingOrIncorrectEndComment
            })));
        }
        let end_comment = end_comment_search.unwrap();
        let end_comment_position = end_comment + self.doc.end_comment_multi_line_tag().len();
        let post_end_input_for_search = post_start_input.take_from(end_comment);
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
                DocumentStructureToken::MultiLineComment(
                    resulting_input,
                    start_pos,
                    remaining_input_position,
                )
            }),
        ))
        // let mut start_comment_ending_position = 0;
        // let mut input_iter = input.iter_elements();
        // for i in 0..self.doc.start_comment_multi_line_tag().chars().count() {
        //     match input_iter.next() {
        //         Some(x) => {
        //             let is_present = self.doc.is_start_comment_multi_line_predicate::<I>(x, i);
        //             if !is_present {
        //                 return Err(nom::Err::Error(OM::Error::bind(|| {
        //                     DocumentStructureError::MissingOrIncorrectStartComment
        //                 })));
        //             }
        //             start_comment_ending_position += 1;
        //         }
        //         None => {
        //             return Err(nom::Err::Error(OM::Error::bind(|| {
        //                 DocumentStructureError::MissingOrIncorrectStartComment
        //             })))
        //         }
        //     }
        // }
        // let post_start_input = input.take_from(start_comment_ending_position);

        // //iteratively search for an end comment in the text until there is no text left or an exact match is found
        // let mut found = false;
        // let mut search_index = 0;
        // // let mut base_input = post_start_input.take_from(search_index);
        // while !found && search_index < post_start_input.input_len() {
        //     let base_input = post_start_input.take_from(search_index);
        //     let end_comment_search =
        //         base_input.position(|x| self.doc.is_end_comment_multi_line::<I>(x));
        //     if let Some(end_comment_position) = end_comment_search {
        //         let end_input = base_input.take_from(end_comment_position);
        //         let mut end_iter = end_input.iter_elements();
        //         let mut is_present = false;
        //         let mut idx = 0;
        //         while idx < self.doc.end_comment_multi_line_tag().chars().count() - 1 {
        //             match end_iter.next() {
        //                 Some(x) => {
        //                     is_present = self.doc.is_end_comment_multi_line_predicate::<I>(x, idx);
        //                     if !is_present {
        //                         break;
        //                     }
        //                 }
        //                 None => {
        //                     is_present = false;
        //                     break;
        //                 }
        //             }
        //             idx += 1;
        //         }
        //         if is_present {
        //             search_index += end_comment_position
        //                 + self.doc.end_comment_multi_line_tag().chars().count();
        //             found = true;
        //         } else {
        //             search_index += end_comment_position + idx;
        //         }
        //     } else {
        //         search_index += 1;
        //         break;
        //     }
        // }
        // if !found {
        //     return Err(nom::Err::Error(OM::Error::bind(|| {
        //         DocumentStructureError::MissingOrIncorrectEndComment
        //     })));
        // }
        // let mut current_position = search_index;
        // if current_position < post_start_input.input_len() {
        //     let mut search_input = post_start_input.take_from(current_position);
        //     let mut predicate = match search_input.position(|character| {
        //         self.doc.is_carriage_return::<I>(character) || self.doc.is_new_line::<I>(character)
        //     }) {
        //         None => (false, 0),
        //         Some(y) => (y == 1, y),
        //     };
        //     while predicate.0 && current_position < post_start_input.input_len() {
        //         search_input = search_input.take_from(predicate.1);
        //         current_position += 1;
        //         predicate = match search_input.position(|character| {
        //             self.doc.is_carriage_return::<I>(character)
        //                 || self.doc.is_new_line::<I>(character)
        //         }) {
        //             None => (false, 0),
        //             Some(y) => (y == 1, y),
        //         };
        //     }
        // }
        // let final_position = start_comment_ending_position + current_position;
        // let remaining_input = input.take_from(final_position);
        // let remaining_input_position = remaining_input.get_position();
        // Ok((
        //     remaining_input,
        //     OM::Output::bind(|| {
        //         let start_pos = input.get_position();
        //         let resulting_input = input.take(final_position);
        //         DocumentStructureToken::MultiLineComment(
        //             resulting_input,
        //             start_pos,
        //             remaining_input_position,
        //         )
        //     }),
        // ))
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifyMultiLineTerminatedComment;
    use applicability_lexer_base::{
        comment::multi_line::{EndCommentMultiLine, StartCommentMultiLine},
        document_structure::{DocumentStructureError, DocumentStructureToken},
        line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    };

    use nom::{bytes::tag, error::ParseError, AsChar, Compare, Err, IResult, Input, Parser};
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
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
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("/*Some text*/");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            DocumentStructureToken::MultiLineComment(
                LocatedSpan::new("/*Some text*/"),
                (0, 1),
                (13, 1),
            ),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_multiline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new(
            "/*
            * 
            * 
            * Some text
            */",
        );
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(71, 5, "", ()) },
            DocumentStructureToken::MultiLineComment(
                LocatedSpan::new(
                    "/*
            * 
            * 
            * Some text
            */",
                ),
                (0, 1),
                (71, 5),
            ),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_with_new_lines() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("/*\r\nSome text\r\n\n*/");
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(18, 4, "", ()) },
            DocumentStructureToken::MultiLineComment(
                LocatedSpan::new("/*\r\nSome text\r\n\n*/"),
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
        let result: IResult<
            LocatedSpan<&str>,
            DocumentStructureToken<LocatedSpan<&str>>,
            DocumentStructureError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "Other text", ()) },
            DocumentStructureToken::MultiLineComment(
                LocatedSpan::new("/*Some text*/"),
                (0, 1),
                (13, 1),
            ),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_multi_line_terminated();
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
