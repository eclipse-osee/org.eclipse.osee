use applicability_lexer_base::{
    comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineNonTerminated},
    line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
    utils::locatable::Locatable,
};
use nom::{AsChar, Compare, Input, Mode, Parser};
use std::result::Result::Err;

use crate::error::FirstStageError;

use super::token::FirstStageToken;

pub trait IdentifySingleLineNonTerminatedComment {
    fn identify_comment_single_line_non_terminated<I>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<I>, Error = FirstStageError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync,
        <I as Input>::Item: AsChar;
}

impl<T> IdentifySingleLineNonTerminatedComment for T
where
    T: StartCommentSingleLineNonTerminated
        + EndCommentSingleLineTerminated
        + CarriageReturn
        + NewLine
        + Eof,
{
    fn identify_comment_single_line_non_terminated<I>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<I>, Error = FirstStageError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync,
        <I as Input>::Item: AsChar,
    {
        // let start = self
        //     .start_comment_single_line_non_terminated()
        //     .and(take_till(|x| {
        //         self.is_carriage_return::<I>(x) || self.is_new_line::<I>(x)
        //     }))
        //     .and(not(self.end_comment_single_line()))
        //     .map(|((start, text), _): ((I, I), ())| (start, text));
        // let windows_new_line = self
        //     .carriage_return()
        //     .and(self.new_line())
        //     .map(|x| (Some(x.0), Some(x.1)));
        // let unix_new_line = self.new_line().map(|x| (None, Some(x)));
        // let eof_termination = self.eof().map(|_| (None, None));
        // let end = windows_new_line.or(unix_new_line).or(eof_termination);
        // let parser = position().and(start.and(end)).and(position());
        // let p = parser.map(
        //     |((start_pos, (start, end)), end_pos): (
        //         ((usize, u32), ((I, I), (Option<I>, Option<I>))),
        //         (usize, u32),
        //     )| {
        //         let mut builder = start.0.new_builder();
        //         start.0.extend_into(&mut builder);
        //         start.1.extend_into(&mut builder);
        //         if let Some(x) = end.0 {
        //             let transform: I = x.into();
        //             transform.extend_into(&mut builder)
        //         }
        //         if let Some(x) = end.1 {
        //             let transform: I = x.into();
        //             transform.extend_into(&mut builder)
        //         }

        //         // start is &[u8] or vec![char], end is Option<char>, char
        //         // chars implements .as_str() on its own, but &[u8] doesn't
        //         FirstStageToken::SingleLineComment(builder, start_pos, end_pos)
        //     },
        // );
        // p
        SingleLineNonTerminatedCommentParser { doc: self }
    }
}

struct SingleLineNonTerminatedCommentParser<'single_line_parser, T> {
    doc: &'single_line_parser T,
}

impl<I: Input + Send + Sync, T> Parser<I> for SingleLineNonTerminatedCommentParser<'_, T>
where
    I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync,
    <I as Input>::Item: AsChar,
    T: StartCommentSingleLineNonTerminated
        + EndCommentSingleLineTerminated
        + CarriageReturn
        + NewLine
        + Eof,
{
    type Output = FirstStageToken<I>;
    type Error = FirstStageError<I>;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        if !(self
            .doc
            .has_start_comment_single_line_non_terminated_support()
            && self.doc.has_end_comment_single_line_terminated_support())
        {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                FirstStageError::Unsupported
            })));
        }
        let mut start_comment_ending_position = 0;
        let mut input_iter = input.iter_elements();
        for i in 0..self
            .doc
            .start_comment_single_line_non_terminated_tag()
            .chars()
            .count()
        {
            match input_iter.next() {
                Some(x) => {
                    let is_present = self
                        .doc
                        .is_start_comment_single_line_non_terminated_predicate::<I>(x, i);
                    if !is_present {
                        return Err(nom::Err::Error(OM::Error::bind(|| {
                            FirstStageError::MissingOrIncorrectStartComment
                        })));
                    }
                    start_comment_ending_position += 1;
                }
                None => {
                    return Err(nom::Err::Error(OM::Error::bind(|| {
                        FirstStageError::MissingOrIncorrectStartComment
                    })))
                }
            }
        }
        let post_start_input = input.take_from(start_comment_ending_position);
        let carriage_return_search = input.position(|x| self.doc.is_carriage_return::<I>(x));
        let new_line_search = input.position(|x| self.doc.is_new_line::<I>(x));
        let end_comment_search =
            post_start_input.position(|x| self.doc.is_end_comment_single_line::<I>(x));
        let eof_search = input.input_len();
        let new_line_position = match (carriage_return_search, new_line_search) {
            (None, None) => None,
            (None, Some(nl)) => Some(nl),
            (Some(_), None) => None,
            (Some(cr), Some(nl)) => {
                if cr + 1 == nl {
                    Some(nl)
                } else {
                    None
                }
            }
        };
        if new_line_position.is_none() && carriage_return_search.is_some() {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                FirstStageError::IncorrectSequence
            })));
        }
        let end_comment_position = match (new_line_position, end_comment_search) {
            (None, None) => None,
            (None, Some(_)) => None,
            // Some(endc + start_comment_ending_position),
            (Some(nl), None) => Some(nl),
            (Some(nl), Some(endc)) => {
                if nl + 1 == endc + start_comment_ending_position {
                    // this needs to be an error
                    // Some(endc + start_comment_ending_position)
                    None
                } else {
                    Some(nl)
                }
            }
        };
        if end_comment_position.is_none() && end_comment_search.is_some() {
            return Err(nom::Err::Error(OM::Error::bind(|| {
                FirstStageError::IncorrectSequence
            })));
        }
        let position_to_take = if let Some(x) = end_comment_position {
            if x + 1 == eof_search {
                eof_search
            } else {
                x + 1
            }
        } else {
            eof_search
        };
        let remaining_input = input.take_from(position_to_take);
        let remaining_input_position = remaining_input.get_position();
        Ok((
            remaining_input,
            OM::Output::bind(|| {
                let start_pos = input.get_position();
                let resulting_input = input.take(position_to_take);
                FirstStageToken::SingleLineComment(
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

    use super::IdentifySingleLineNonTerminatedComment;
    use crate::{error::FirstStageError, token::FirstStageToken};
    use applicability_lexer_base::{
        comment::single_line::{
            EndCommentSingleLineTerminated, StartCommentSingleLineNonTerminated,
        },
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::MissingOrIncorrectStartComment));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_windows_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\r\n");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 2, "", ()) },
            FirstStageToken::SingleLineComment(
                LocatedSpan::new("``Some text\r\n"),
                (0, 1),
                (13, 2),
            ),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_eof() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(11, 1, "", ()) },
            FirstStageToken::SingleLineComment(LocatedSpan::new("``Some text"), (0, 1), (11, 1)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_broken_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\r");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::IncorrectSequence));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment_unix_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\n");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(12, 2, "", ()) },
            FirstStageToken::SingleLineComment(LocatedSpan::new("``Some text\n"), (0, 1), (12, 2)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_windows_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\r\nOther text");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 2, "Other text", ()) },
            FirstStageToken::SingleLineComment(
                LocatedSpan::new("``Some text\r\n"),
                (0, 1),
                (13, 2),
            ),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_unix_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\nOther text");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(12, 2, "Other text", ()) },
            FirstStageToken::SingleLineComment(LocatedSpan::new("``Some text\n"), (0, 1), (12, 2)),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_trailing_text_broken_newline() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\rOther text");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::IncorrectSequence));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment_preceding_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_non_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("Other text``Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::MissingOrIncorrectStartComment));
        assert_eq!(parser.parse_complete(input), result)
    }
}
