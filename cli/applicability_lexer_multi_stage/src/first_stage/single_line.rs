use std::{iter::Chain, ops::Add, slice::Iter};

use nom::{
    bytes::take_till, character::multispace0, error::ParseError, AsChar, Compare, Input, Parser,
};

use crate::base::{
    comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
    custom_string_traits::CustomToString,
};

use super::token::FirstStageToken;

pub trait IdentifySingleLineComment {
    fn identify_comment_single_line<'x, I, O, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str>,
        O: CustomToString + FromIterator<I::Item>,
        // O1: FromIterator<I::Item>,
        // + Add<I, Output = Self::OutputType>,
        // <O1 as Add<I>>::Output
        // O2: Add<O1>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifySingleLineComment for T
where
    T: StartCommentSingleLine + EndCommentSingleLine,
{
    fn identify_comment_single_line<'x, I, O, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        // From<<I as Add<I>>::Output>
        // Add<I>
        I: Input + Compare<&'x str>,
        O: CustomToString + FromIterator<I::Item>,
        // O1: FromIterator<I::Item>,
        // + Add<I, Output = O2>,
        // O2: Add<O1>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_single_line()
            .and(take_till(|x| self.is_end_comment_single_line::<I>(x)))
            .map(|(start, text): (I, I)| {
                let start_iter: I::Iter = start.iter_elements();
                let text_iter: I::Iter = text.iter_elements();
                let iter = start_iter.chain(text_iter);
                // let result = iter.collect::<O1>();
                // result
                iter
            });
        let end = self
            .end_comment_single_line()
            .and(multispace0())
            .map(|(end, spaces): (I, I)| {
                let end_iter: I::Iter = end.iter_elements();
                let spaces_iter: I::Iter = spaces.iter_elements();
                let iter = end_iter.chain(spaces_iter);
                // let result = iter.collect::<O1>();
                // result
                iter
            });
        let parser = start.and(end);
        let p = parser.map(
            |(start, end): (
                Chain<<I as Input>::Iter, <I as Input>::Iter>,
                Chain<<I as Input>::Iter, <I as Input>::Iter>,
            )| {
                //String + String = String;
                //reality String + &str = String
                // let result: <O1 as Add<I>>::Output = start + end.into();
                // let result: Self::OutputType = start + end.into();
                // Chain<
                //     std::iter::Chain<<I as Input>::Iter, <I as Input>::Iter>,
                //     Chain<<I as Input>::Iter, <I as Input>::Iter>,
                // >
                let result_vec: O = start.chain(end).collect::<O>();
                let result = result_vec.custom_to_string();
                // result_vec.custom_to_string()

                // let result = result_iter.collect();
                // let result = conversion_fn(result_iter);
                // start is &[u8] or vec![char], same with end
                // chars implements .as_str() on its own, but &[u8] doesn't
                // let start_iter: I::Iter = start.iter_elements();
                // let end_iter: I::Iter = end.iter_elements();
                // let iter = start_iter.chain(end_iter);
                // let result = iter.collect::<O>();
                FirstStageToken::SingleLineTerminatedComment(result)
            },
        );
        p
    }
}

#[cfg(test)]
mod tests {
    use std::{char, marker::PhantomData};

    use super::IdentifySingleLineComment;
    use crate::{
        base::comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
        first_stage::token::FirstStageToken,
    };

    use nom::{
        bytes::tag,
        character::char,
        error::{Error, ErrorKind, ParseError},
        AsChar, Compare, Err, IResult, Input, Parser,
    };

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl<'a> StartCommentSingleLine for TestStruct<'a> {
        fn is_start_comment_single_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            false
        }

        fn start_comment_single_line<'x, I, E>(&self) -> impl nom::Parser<I, Output = I, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
        {
            tag("``")
        }
    }
    impl<'a> EndCommentSingleLine for TestStruct<'a> {
        fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            false
        }

        fn end_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
        where
            I: Input + Compare<&'x str>,
            // O: FromIterator<I::Item>,
            // O: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
        {
            tag("``")
        }
    }

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line::<_, Vec<char>, _>();
        let input: &str = "";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comment_single_line::<_, Vec<char>, _>();
        let input: &str = "``Some text``";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "",
            FirstStageToken::SingleLineTerminatedComment("``Some text``".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
