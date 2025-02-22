use nom::{
    combinator::rest, error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input,
    Parser,
};

use super::{
    first_stage_text::IdentifyFirstStageText,
    multi_line_terminated::IdentifyMultiLineTerminatedComment,
    single_line_non_terminated::IdentifySingleLineNonTerminatedComment,
    single_line_terminated::IdentifySingleLineTerminatedComment, token::FirstStageToken,
};

pub trait IdentifyComments {
    fn identify_comments<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<FirstStageToken<String>>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString,
        String: FromIterator<<I as Input>::Item>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> IdentifyComments for T
where
    T: IdentifyFirstStageText
        + IdentifyMultiLineTerminatedComment
        + IdentifySingleLineNonTerminatedComment
        + IdentifySingleLineTerminatedComment,
{
    fn identify_comments<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<FirstStageToken<String>>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString,
        String: FromIterator<<I as Input>::Item>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let inner_parser = self
            .identify_comment_single_line_terminated()
            .or(self.identify_comment_multi_line_terminated())
            .or(self.identify_comment_single_line_non_terminated())
            .or(self.identify_first_stage_text());
        let parser = many0(inner_parser)
            .and(rest.map(|x: I| FirstStageToken::Text(x.to_string())))
            .map(|(mut list, remaining)| {
                list.push(remaining);
                list
            });
        parser
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifyComments;
    use crate::{
        base::{
            comment::{
                multi_line::{EndCommentMultiLine, StartCommentMultiLine},
                single_line::{EndCommentSingleLine, StartCommentSingleLine},
            },
            line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
        },
        first_stage::token::FirstStageToken,
    };

    use nom::{
        character::char,
        combinator::eof,
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> =
            Ok(("", vec![FirstStageToken::Text("".to_string())]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_text_with_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string``Some text``";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::SingleLineTerminatedComment("``Some text``".to_string()),
                FirstStageToken::Text("".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_not_terminated() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string``Some text";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::SingleLineComment("``Some text".to_string()),
                FirstStageToken::Text("".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_not_terminated_cr_nl() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string``Some text\r\n";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::SingleLineComment("``Some text\r\n".to_string()),
                FirstStageToken::Text("".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string``Some text``More text";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::SingleLineTerminatedComment("``Some text``".to_string()),
                FirstStageToken::Text("More text".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_not_terminated_cr_nl_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string``Some text\r\nMore text";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::SingleLineComment("``Some text\r\n".to_string()),
                FirstStageToken::Text("More text".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string/*\r\nSome text*/";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::MultiLineComment("/*\r\nSome text*/".to_string()),
                FirstStageToken::Text("".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment_and_text() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string/*\r\nSome text*/More text";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::MultiLineComment("/*\r\nSome text*/".to_string()),
                FirstStageToken::Text("More text".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment_and_single_line() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string/*\r\nSome text*/``More text``";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::MultiLineComment("/*\r\nSome text*/".to_string()),
                FirstStageToken::SingleLineTerminatedComment("``More text``".to_string()),
                FirstStageToken::Text("".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_single_line_comment_and_multi_line() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "Random string``More text``/*\r\nSome text*/";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::Text("Random string".to_string()),
                FirstStageToken::SingleLineTerminatedComment("``More text``".to_string()),
                FirstStageToken::MultiLineComment("/*\r\nSome text*/".to_string()),
                FirstStageToken::Text("".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_comments();
        let input: &str = "``Some text``";
        let result: IResult<&str, Vec<FirstStageToken<String>>, Error<&str>> = Ok((
            "",
            vec![
                FirstStageToken::SingleLineTerminatedComment("``Some text``".to_string()),
                FirstStageToken::Text("".to_string()),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
