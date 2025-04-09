use nom::{
    combinator::success, error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input,
    Parser,
};

use crate::{
    base::{
        delimiters::{
            brace::{LexEndBrace, LexStartBrace},
            paren::{LexEndParen, LexStartParen},
            space::LexSpace,
            tab::LexTab,
        },
        line_terminations::{carriage_return::LexCarriageReturn, new_line::LexNewLine},
        logic::{and::LexAnd, not::LexNot, or::LexOr},
    },
    
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::{
    locatable::{position, Locatable},
    take_first::take_until_first10,
}};

pub trait TagMultiLine {
    fn multi_line_tag<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
impl<T> TagMultiLine for T
where
    T: LexStartBrace
        + LexEndBrace
        + LexStartParen
        + LexEndParen
        + LexAnd
        + LexOr
        + LexNot
        + LexSpace
        + LexTab
        + LexCarriageReturn
        + LexNewLine,
{
    fn multi_line_tag<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
        let nl = self.lex_new_line();
        let cr = self.lex_carriage_return();
        let tag_text = position()
            .and(take_until_first10(
                self.lex_space_tag(),
                self.lex_tab_tag(),
                self.lex_or_tag(),
                self.lex_not_tag(),
                self.lex_and_tag(),
                self.lex_start_paren_tag(),
                self.lex_end_paren_tag(),
                self.lex_end_brace_tag(),
                self.lex_new_line_tag(),
                self.lex_carriage_return_tag(),
            ))
            .and(position())
            .map(|((start, x), end): (((usize, u32), I), (usize, u32))| {
                LexerToken::Tag(x.into(), start, end)
            });
        //TODO: verify many0 works instead of many_till
        let tag = start_brace
            .and(
                many0(
                    self.lex_space()
                        .or(self.lex_tab())
                        .or(and)
                        .or(or)
                        .or(not)
                        .or(start_paren)
                        .or(end_paren)
                        .or(nl)
                        .or(cr)
                        .or(tag_text),
                )
                .or(success(vec![])),
            )
            .and(end_brace)
            .map(|((start, mut t), end)| {
                t.insert(0, start);
                t.push(end);
                t
            });
        tag
    }
}

#[cfg(test)]
mod tests {
    use std::{marker::PhantomData, vec};

    use super::TagMultiLine;
    
    use applicability_lexer_base::applicability_structure::LexerToken;
    use applicability_lexer_base::comment::multi_line::{
        EndCommentMultiLine, StartCommentMultiLine,
    };
    use applicability_lexer_base::default::DefaultApplicabilityLexer;

    use nom::{
        error::{Error, ErrorKind, ParseError},
        AsChar, Err, IResult, Input, Parser,
    };
    use nom_locate::LocatedSpan;

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

        fn has_start_comment_multi_line_support(&self) -> bool {
            true
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

        fn has_end_comment_multi_line_support(&self) -> bool {
            true
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
        let mut parser = config.multi_line_tag();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn empty_brace() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.multi_line_tag();
        let input: LocatedSpan<&str> = LocatedSpan::new("[]");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(2, 1, "", ()) },
            vec![
                LexerToken::StartBrace((0, 1), (1, 1)),
                LexerToken::EndBrace((1, 1), (2, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn empty_brace_text_after() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.multi_line_tag();
        let input: LocatedSpan<&str> = LocatedSpan::new("[] abcd");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(2, 1, " abcd", ()) },
            vec![
                LexerToken::StartBrace((0, 1), (1, 1)),
                LexerToken::EndBrace((1, 1), (2, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn tag_text_after() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.multi_line_tag();
        let input: LocatedSpan<&str> = LocatedSpan::new("[ABCD] abcd");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(6, 1, " abcd", ()) },
            vec![
                LexerToken::StartBrace((0, 1), (1, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(1, 1, "ABCD", ()) },
                    (1, 1),
                    (5, 1),
                ),
                LexerToken::EndBrace((5, 1), (6, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
