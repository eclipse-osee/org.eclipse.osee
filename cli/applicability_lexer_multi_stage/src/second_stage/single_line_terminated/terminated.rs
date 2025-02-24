use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
    second_stage::token::LexerToken,
};

use super::{
    config::tag::ConfigTagSingleLineTerminated,
    config_group::tag::ConfigGroupTagSingleLineTerminated,
    feature::tag::FeatureTagSingleLineTerminated, utils::loose_text::LooseTextTerminated,
};

pub trait SingleLineTerminated {
    fn get_single_line_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}
impl<T> SingleLineTerminated for T
where
    T: StartCommentSingleLine
        + EndCommentSingleLine
        + FeatureTagSingleLineTerminated
        + ConfigTagSingleLineTerminated
        + ConfigGroupTagSingleLineTerminated
        + LooseTextTerminated,
{
    fn get_single_line_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start = self
            .start_comment_single_line()
            .map(|_| LexerToken::StartCommentSingleLine);

        let applic_tag = self
            .feature_tag_terminated()
            .or(self.config_tag_terminated())
            .or(self.config_group_tag_terminated());
        let inner_select = applic_tag.or(self.loose_text_terminated());
        let inner = many0(inner_select)
            .map(|x| x.into_iter().flatten().collect::<Vec<LexerToken<String>>>());
        let end = self
            .end_comment_single_line()
            .map(|_| LexerToken::EndCommentSingleLine);
        let parse_comment = start.and(inner).and(end).map(|((start, tag), end)| {
            let mut results = vec![start];
            results.extend(tag.into_iter());
            results.push(end);
            results
        });
        parse_comment
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::SingleLineTerminated;
    use crate::{
        base::{
            comment::{
                multi_line::{EndCommentMultiLine, StartCommentMultiLine},
                single_line::{EndCommentSingleLine, StartCommentSingleLine},
            },
            delimiters::{
                brace::{EndBrace, StartBrace},
                paren::{EndParen, StartParen},
                space::Space,
                tab::Tab,
            },
            line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
            logic::{and::And, not::Not, or::Or},
        },
        default::DefaultApplicabilityLexer,
        second_stage::{
            single_line_terminated::{
                config::tag::ConfigTagSingleLineTerminated,
                config_group::tag::ConfigGroupTagSingleLineTerminated,
                feature::tag::FeatureTagSingleLineTerminated,
                utils::{loose_text::LooseTextTerminated, tag_terminated::TagTerminated},
            },
            token::LexerToken,
        },
    };

    use nom::{
        character::char,
        combinator::eof,
        error::{Error, ParseError},
        AsChar, Compare, IResult, Input, Parser,
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
    impl<'a> CarriageReturn for TestStruct<'a> {
        fn is_carriage_return<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '\r'
        }

        fn carriage_return<'x, I, E>(
            &self,
        ) -> impl Parser<I, Output = Self::CarriageReturnOutput, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
            Self::CarriageReturnOutput: AsChar,
        {
            char('\r')
        }

        type CarriageReturnOutput = char;
    }
    impl<'a> NewLine for TestStruct<'a> {
        type NewlineOutput = char;

        fn is_new_line<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char() == '\n'
        }

        fn new_line<'x, I, E>(&self) -> impl Parser<I, Output = Self::NewlineOutput, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
        {
            char('\n')
        }
    }

    impl<'a> Eof for TestStruct<'a> {
        fn is_eof<I>(&self, input: I::Item) -> bool
        where
            I: Input,
            I::Item: AsChar,
        {
            input.as_char().len() == 0
        }

        fn eof<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I>,
        {
            eof
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
    // impl<'a> StartBrace for TestStruct<'a> {}
    // impl<'a> EndBrace for TestStruct<'a> {}
    // impl<'a> StartParen for TestStruct<'a> {}
    // impl<'a> EndParen for TestStruct<'a> {}
    // impl<'a> Space for TestStruct<'a> {}
    // impl<'a> Tab for TestStruct<'a> {}
    // impl<'a> Not for TestStruct<'a> {}
    // impl<'a> And for TestStruct<'a> {}
    // impl<'a> Or for TestStruct<'a> {}
    impl<'a> DefaultApplicabilityLexer for TestStruct<'a> {
        fn is_default() -> bool {
            true
        }
    }
    #[test]
    fn basic_test() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![]));
        assert_eq!(parser.parse_complete(input), result)
    }
}
