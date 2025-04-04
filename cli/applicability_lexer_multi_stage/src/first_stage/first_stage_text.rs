use nom::{
    bytes::take_until, error::ParseError, AsChar, Compare, ExtendInto, FindSubstring, Input, Parser,
};

use crate::base::{
    comment::{
        multi_line::StartCommentMultiLine,
        single_line::{StartCommentSingleLineNonTerminated, StartCommentSingleLineTerminated},
    },
    utils::{
        cond_with_failure::cond_with_failure,
        locatable::{position, Locatable},
        take_first::{take_until_first2, take_until_first3},
    },
};

use super::token::FirstStageToken;
pub trait IdentifyFirstStageText {
    fn identify_first_stage_text<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<<I as ExtendInto>::Extender>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString + Locatable + ExtendInto,
        <I as ExtendInto>::Extender: Send + Sync,
        <I as Input>::Item: AsChar,
        E: ParseError<I>;
}
impl<T> IdentifyFirstStageText for T
where
    T: StartCommentSingleLineTerminated
        + StartCommentMultiLine
        + StartCommentSingleLineNonTerminated,
{
    fn identify_first_stage_text<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<<I as ExtendInto>::Extender>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString + Locatable + ExtendInto,
        <I as ExtendInto>::Extender: Send + Sync,
        <I as Input>::Item: AsChar,
        E: ParseError<I>,
    {
        // parse until you hit one of the special characters
        let cond1 = cond_with_failure(
            self.has_start_comment_multi_line_support()
                && self.has_start_comment_single_line_terminated_support()
                && self.has_start_comment_single_line_non_terminated_support(),
            take_until_first3(
                self.start_comment_single_line_terminated_tag(),
                self.start_comment_multi_line_tag(),
                self.start_comment_single_line_non_terminated_tag(),
            ),
        );
        let cond2 = cond_with_failure(
            self.has_start_comment_multi_line_support()
                && self.has_start_comment_single_line_terminated_support(),
            take_until_first2(
                self.start_comment_single_line_terminated_tag(),
                self.start_comment_multi_line_tag(),
            ),
        );
        let cond3 = cond_with_failure(
            self.has_start_comment_multi_line_support()
                && self.has_start_comment_single_line_non_terminated_support(),
            take_until_first2(
                self.start_comment_multi_line_tag(),
                self.start_comment_single_line_non_terminated_tag(),
            ),
        );
        let cond4 = cond_with_failure(
            self.has_start_comment_single_line_non_terminated_support()
                && self.has_start_comment_single_line_terminated_support(),
            take_until_first2(
                self.start_comment_single_line_terminated_tag(),
                self.start_comment_single_line_non_terminated_tag(),
            ),
        );
        let cond5 = cond_with_failure(
            self.has_start_comment_multi_line_support(),
            take_until(self.start_comment_multi_line_tag()),
        );
        let cond6 = cond_with_failure(
            self.has_start_comment_single_line_terminated_support(),
            take_until(self.start_comment_single_line_terminated_tag()),
        );
        let cond7 = cond_with_failure(
            self.has_start_comment_single_line_non_terminated_support(),
            take_until(self.start_comment_single_line_non_terminated_tag()),
        );
        let cond = cond1
            .or(cond2)
            .or(cond3)
            .or(cond4)
            .or(cond5)
            .or(cond6)
            .or(cond7);
        position().and(cond).and(position()).map(
            |((start, x), end): (((usize, u32), I), (usize, u32))| {
                let mut builder = x.new_builder();
                x.extend_into(&mut builder);
                FirstStageToken::Text(builder, start, end)
            },
        )
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifyFirstStageText;
    use crate::{
        base::comment::{
            multi_line::StartCommentMultiLine,
            single_line::{StartCommentSingleLineNonTerminated, StartCommentSingleLineTerminated},
        },
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
    impl<'a> StartCommentSingleLineNonTerminated for TestStruct<'a> {
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
        let mut parser = config.identify_first_stage_text();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Err(Err::Error(Error::from_error_kind(
                input,
                ErrorKind::TakeUntil,
            )));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_text_with_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_first_stage_text();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string``Some text``");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(13, 1, "``Some text``", ()) },
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_first_stage_text();
        let input: LocatedSpan<&str> = LocatedSpan::new("Random string/*\r\nSome text*/");
        let result: IResult<LocatedSpan<&str>, FirstStageToken<String>, Error<LocatedSpan<&str>>> =
            Ok((
                unsafe { LocatedSpan::new_from_raw_offset(13, 1, "/*\r\nSome text*/", ()) },
                FirstStageToken::Text("Random string".to_string(), (0, 1), (13, 1)),
            ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
