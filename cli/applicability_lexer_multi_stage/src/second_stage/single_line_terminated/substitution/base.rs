use nom::{
    combinator::success, error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input,
    Parser,
};

use crate::{
    base::{
        delimiters::{space::Space, tab::Tab},
        substitution::Substitution,
    },
    second_stage::{
        single_line_terminated::utils::tag_terminated::TagTerminated, token::LexerToken,
    },
};

pub trait SubstitutionSingleLineTerminated {
    fn substitution_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> SubstitutionSingleLineTerminated for T
where
    T: TagTerminated + Substitution + Space + Tab,
{
    fn substitution_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let substitution_tag = self
            .substitution()
            .map(|_| LexerToken::Substitution)
            .and(
                many0(
                    self.space()
                        .map(|_| LexerToken::Space)
                        .or(self.tab().map(|_| LexerToken::Tab)),
                )
                .or(success(vec![])),
            )
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        substitution_tag
    }
}
#[cfg(test)]
mod tests {
    use std::{marker::PhantomData, vec};

    use super::SubstitutionSingleLineTerminated;
    use crate::{
        base::{
            comment::{
                multi_line::{EndCommentMultiLine, StartCommentMultiLine},
                single_line::{EndCommentSingleLine, StartCommentSingleLine},
            },
            line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
        },
        default::DefaultApplicabilityLexer,
        second_stage::token::LexerToken,
    };

    use nom::{
        error::{Error, ErrorKind, ParseError},
        AsChar, Compare, Err, IResult, Input, Parser,
    };

    struct TestStruct<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl<'a> StartCommentMultiLine for TestStruct<'a> {
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
    }
    impl<'a> StartCommentSingleLine for TestStruct<'a> {
        fn is_start_comment_single_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '`'
        }

        fn start_comment_single_line_tag<'x>(&self) -> &'x str {
            "``"
        }
    }
    impl<'a> EndCommentMultiLine for TestStruct<'a> {
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
    }

    impl<'a> EndCommentSingleLine for TestStruct<'a> {
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
    }
    impl<'a> DefaultApplicabilityLexer for TestStruct<'a> {
        fn is_default() -> bool {
            true
        }
    }
    #[test]
    fn empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: &str = "";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn empty_eval() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: &str = "Eval[]";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                LexerToken::Substitution,
                LexerToken::StartBrace,
                LexerToken::EndBrace,
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn empty_eval_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: &str = "Eval[] abcd";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok((
            " abcd",
            vec![
                LexerToken::Substitution,
                LexerToken::StartBrace,
                LexerToken::EndBrace,
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn eval_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: &str = "Eval[ABCD]";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                LexerToken::Substitution,
                LexerToken::StartBrace,
                LexerToken::Tag("ABCD".into()),
                LexerToken::EndBrace,
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn eval_text_after() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.substitution_terminated();
        let input: &str = "Eval[ABCD] abcd";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok((
            " abcd",
            vec![
                LexerToken::Substitution,
                LexerToken::StartBrace,
                LexerToken::Tag("ABCD".into()),
                LexerToken::EndBrace,
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
