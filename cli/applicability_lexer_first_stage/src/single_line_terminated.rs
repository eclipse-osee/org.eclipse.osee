use nom::{AsChar, Compare, Input, Mode, Parser};

use applicability_lexer_base::{
    comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineTerminated},
    line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    utils::locatable::Locatable,
};

use crate::error::FirstStageError;

use super::token::FirstStageToken;

pub trait IdentifySingleLineTerminatedComment {
    fn identify_comment_single_line_terminated<I>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<I>, Error = FirstStageError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync,
        <I as Input>::Item: AsChar;
}

impl<T> IdentifySingleLineTerminatedComment for T
where
    T: StartCommentSingleLineTerminated + EndCommentSingleLineTerminated + CarriageReturn + NewLine,
{
    fn identify_comment_single_line_terminated<I>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<I>, Error = FirstStageError<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync,
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
    I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync,
    <I as Input>::Item: AsChar,
    T: StartCommentSingleLineTerminated + EndCommentSingleLineTerminated + CarriageReturn + NewLine,
{
    type Output = FirstStageToken<I>;
    type Error = FirstStageError<I>;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        let mut start_comment_ending_position = 0;
        let mut input_iter = input.iter_elements();
        for i in 0..self
            .doc
            .start_comment_single_line_terminated_tag()
            .chars()
            .count()
        {
            match input_iter.next() {
                Some(x) => {
                    let is_present = self
                        .doc
                        .is_start_comment_single_line_terminated_predicate::<I>(x, i);
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
        let post_start_input = input.take_from(start_comment_ending_position + 1);
        let carriage_return_search = input.position(|x| self.doc.is_carriage_return::<I>(x));
        let new_line_search = input.position(|x| self.doc.is_new_line::<I>(x));
        let end_comment_search =
            post_start_input.position(|x| self.doc.is_end_comment_single_line::<I>(x));
        if let Some(mut end_comment_position) = end_comment_search {
            //search for the first position where the character is not a space, new line or carriage return after the end comment
            let end_input = post_start_input.take_from(end_comment_position);
            let mut end_iter = end_input.iter_elements();
            for i in 0..self.doc.end_comment_single_line_tag().chars().count() {
                match end_iter.next() {
                    Some(x) => {
                        let is_present = self.doc.is_end_comment_single_line_predicate::<I>(x, i);
                        if !is_present {
                            return Err(nom::Err::Error(OM::Error::bind(|| {
                                FirstStageError::MissingOrIncorrectEndComment
                            })));
                        }
                        end_comment_position += 1;
                    }
                    None => {
                        return Err(nom::Err::Error(OM::Error::bind(|| {
                            FirstStageError::MissingOrIncorrectEndComment
                        })))
                    }
                }
            }
            let mut current_position = end_comment_position + 1;
            if current_position < post_start_input.input_len() {
                let mut search_input = post_start_input.take_from(current_position);
                let mut predicate = match search_input.position(|character| {
                    self.doc.is_carriage_return::<I>(character)
                        || self.doc.is_new_line::<I>(character)
                }) {
                    None => (false, 0),
                    Some(y) => (y == 1, y),
                };
                while predicate.0 {
                    search_input = search_input.take_from(predicate.1);
                    current_position += 1;
                    predicate = match search_input.position(|character| {
                        self.doc.is_carriage_return::<I>(character)
                            || self.doc.is_new_line::<I>(character)
                    }) {
                        None => (false, 0),
                        Some(y) => (y == 1, y),
                    };
                }
            }
            let final_position = start_comment_ending_position + current_position;
            let remaining_input = input.take_from(final_position);
            let remaining_input_position = remaining_input.get_position();
            let result = Ok((
                remaining_input,
                OM::Output::bind(|| {
                    let start_pos = input.get_position();
                    let resulting_input = input.take(final_position);
                    FirstStageToken::SingleLineTerminatedComment(
                        resulting_input,
                        start_pos,
                        remaining_input_position,
                    )
                }),
            ));
            match (carriage_return_search, new_line_search) {
                (None, None) => result, //success
                (None, Some(nl)) => {
                    if nl + 1 <= end_comment_position {
                        return Err(nom::Err::Error(OM::Error::bind(|| {
                            FirstStageError::IncorrectSequence
                        })));
                    }
                    result
                } //error if new_line_search < end_comment
                (Some(_), None) => Err(nom::Err::Error(OM::Error::bind(|| {
                    FirstStageError::IncorrectSequence
                }))), //error
                (Some(cr), Some(nl)) => {
                    if cr + 1 <= end_comment_position || nl + 1 <= end_comment_position {
                        return Err(nom::Err::Error(OM::Error::bind(|| {
                            FirstStageError::IncorrectSequence
                        })));
                    }
                    result
                } // if carriage return or new_line occur before end_comment, error
            }
        } else {
            Err(nom::Err::Error(OM::Error::bind(|| {
                FirstStageError::MissingOrIncorrectEndComment
            })))
        }
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifySingleLineTerminatedComment;
    use crate::{error::FirstStageError, token::FirstStageToken};
    use applicability_lexer_base::{
        comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineTerminated},
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
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::MissingOrIncorrectStartComment));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_partial_end_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text`");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::MissingOrIncorrectEndComment));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_carriage_return_inline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some\r\n text``");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::IncorrectSequence));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_new_line_inline_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some\n text``");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::IncorrectSequence));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            FirstStageToken::SingleLineTerminatedComment(
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
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "Other text", ()) },
            FirstStageToken::SingleLineTerminatedComment(
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
            FirstStageToken<LocatedSpan<&str>>,
            FirstStageError<LocatedSpan<&str>>,
        > = Err(Err::Error(FirstStageError::MissingOrIncorrectStartComment));
        assert_eq!(parser.parse_complete(input), result)
    }
}
