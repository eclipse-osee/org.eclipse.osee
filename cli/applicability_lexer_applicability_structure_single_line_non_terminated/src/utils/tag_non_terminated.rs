use nom::{
    AsChar, Compare, FindSubstring, Input, Parser, combinator::success, error::ParseError,
    multi::many0,
};

use applicability_lexer_applicability_structure_base::{
    delimiters::{
        brace::{LexEndBrace, LexStartBrace},
        paren::{LexEndParen, LexStartParen},
        space::LexSpace,
        tab::LexTab,
    },
    logic::{and::LexAnd, not::LexNot, or::LexOr},
};
use applicability_lexer_base::{
    applicability_structure::LexerToken,
    position::Position,
    utils::{
        locatable::{Locatable, position},
        take_first::take_until_first8,
    },
};

pub trait TagNonTerminated {
    fn non_terminated_tag<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
impl<T> TagNonTerminated for T
where
    T: LexStartBrace
        + LexEndBrace
        + LexStartParen
        + LexEndParen
        + LexAnd
        + LexOr
        + LexNot
        + LexSpace
        + LexTab,
{
    fn non_terminated_tag<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
        let start_brace = self.lex_start_brace();
        let end_brace = self.lex_end_brace();

        let start_paren = self.lex_start_paren();
        let end_paren = self.lex_end_paren();

        let and = self.lex_and();
        let or = self.lex_or();
        let not = self.lex_not();
        let tag_text = position()
            .and(take_until_first8(
                self.lex_space_tag(),
                self.lex_tab_tag(),
                self.lex_or_tag(),
                self.lex_not_tag(),
                self.lex_and_tag(),
                self.lex_start_paren_tag(),
                self.lex_end_paren_tag(),
                self.lex_end_brace_tag(),
            ))
            .and(position())
            .map(|((start, x), end): ((Position, I), Position)| LexerToken::Tag(x, (start, end)));
        
        start_brace
            .and(
                many0(
                    self.lex_space()
                        .or(self.lex_tab())
                        .or(and)
                        .or(or)
                        .or(not)
                        .or(start_paren)
                        .or(end_paren)
                        .or(tag_text),
                )
                .or(success(vec![])),
            )
            .and(end_brace)
            .map(|((start, mut t), end)| {
                t.insert(0, start);
                t.push(end);
                t
            })
    }
}

#[cfg(test)]
mod tests {
    use std::{marker::PhantomData, vec};

    use applicability_lexer_applicability_structure_test_utils::ResultType;

    use super::TagNonTerminated;

    use applicability_lexer_base::{
        applicability_structure::LexerToken,
        comment::{
            multi_line::{EndCommentMultiLine, StartCommentMultiLine},
            single_line::StartCommentSingleLineNonTerminated,
        },
        default::DefaultApplicabilityLexer,
    };

    use nom::{
        AsChar, Err, Input, Parser,
        error::{Error, ErrorKind, ParseError},
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
    impl StartCommentSingleLineNonTerminated for TestStruct<'_> {
        fn is_start_comment_single_line_non_terminated<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
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

    impl DefaultApplicabilityLexer for TestStruct<'_> {
        fn is_default() -> bool {
            true
        }
    }
    #[test]
    fn empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.non_terminated_tag();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: ResultType<&str> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn empty_brace() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.non_terminated_tag();
        let input: LocatedSpan<&str> = LocatedSpan::new("[]");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(2, 1, "", ()) },
            vec![
                LexerToken::StartBrace(((0, 1), (1, 1))),
                LexerToken::EndBrace(((1, 1), (2, 1))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn empty_brace_text_after() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.non_terminated_tag();
        let input: LocatedSpan<&str> = LocatedSpan::new("[] abcd");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(2, 1, " abcd", ()) },
            vec![
                LexerToken::StartBrace(((0, 1), (1, 1))),
                LexerToken::EndBrace(((1, 1), (2, 1))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn tag_text_after() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.non_terminated_tag();
        let input: LocatedSpan<&str> = LocatedSpan::new("[ABCD] abcd");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(6, 1, " abcd", ()) },
            vec![
                LexerToken::StartBrace(((0, 1), (1, 1))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(1, 1, "ABCD", ()) },
                    ((1, 1), (5, 1)),
                ),
                LexerToken::EndBrace(((5, 1), (6, 1))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
