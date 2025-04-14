use nom::{
    combinator::success, error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input,
    Parser,
};

use crate::{
    base::{
        delimiters::{space::LexSpace, tab::LexTab},
        substitution::LexSubstitution,
    },
    single_line_terminated::utils::tag_terminated::TagTerminated,
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait SubstitutionSingleLineTerminated {
    fn substitution_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> SubstitutionSingleLineTerminated for T
where
    T: TagTerminated + LexSubstitution + LexSpace + LexTab,
{
    fn substitution_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let substitution_tag = self
            .lex_substitution()
            .and(many0(self.lex_space().or(self.lex_tab())).or(success(vec![])))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        substitution_tag
    }
}
#[cfg(test)]
mod tests {
    use std::{marker::PhantomData, vec};

    use super::SubstitutionSingleLineTerminated;

    use applicability_lexer_base::{
        applicability_structure::LexerToken,
        comment::{
            multi_line::{EndCommentMultiLine, StartCommentMultiLine},
            single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineTerminated},
        },
        default::DefaultApplicabilityLexer,
    };

    use nom::{
        error::{Error, ErrorKind, ParseError},
        AsChar, Err, IResult, Input, Parser,
    };
    use nom_locate::LocatedSpan;

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl StartCommentMultiLine for TestStruct<'_> {
        fn is_start_comment_multi_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
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
        fn is_start_comment_single_line_terminated<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
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
        fn is_end_comment_multi_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
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

    impl EndCommentSingleLineTerminated for TestStruct<'_> {
        fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
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
    impl DefaultApplicabilityLexer for TestStruct<'_> {
        fn is_default() -> bool {
            true
        }
    }
    #[test]
    fn empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn empty_eval() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("Eval[]");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(6, 1, "", ()) },
            vec![
                LexerToken::Substitution((0, 1), (4, 1)),
                LexerToken::StartBrace((4, 1), (5, 1)),
                LexerToken::EndBrace((5, 1), (6, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn empty_eval_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("Eval[] abcd");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(6, 1, " abcd", ()) },
            vec![
                LexerToken::Substitution((0, 1), (4, 1)),
                LexerToken::StartBrace((4, 1), (5, 1)),
                LexerToken::EndBrace((5, 1), (6, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn eval_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("Eval[ABCD]");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(10, 1, "", ()) },
            vec![
                LexerToken::Substitution((0, 1), (4, 1)),
                LexerToken::StartBrace((4, 1), (5, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(5, 1, "ABCD", ()) },
                    (5, 1),
                    (9, 1),
                ),
                LexerToken::EndBrace((9, 1), (10, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn eval_text_after() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("Eval[ABCD] abcd");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(10, 1, " abcd", ()) },
            vec![
                LexerToken::Substitution((0, 1), (4, 1)),
                LexerToken::StartBrace((4, 1), (5, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(5, 1, "ABCD", ()) },
                    (5, 1),
                    (9, 1),
                ),
                LexerToken::EndBrace((9, 1), (10, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
