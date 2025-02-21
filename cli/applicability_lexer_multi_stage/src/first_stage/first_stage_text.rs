use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::base::comment::{
    multi_line::StartCommentMultiLine, single_line::StartCommentSingleLine,
};

use super::token::FirstStageToken;
pub trait IdentifyFirstStageText {
    fn identify_first_stage_text<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString,
        String: FromIterator<<I as Input>::Item>,
        I::Item: AsChar,
        E: ParseError<I>;
}
impl<T> IdentifyFirstStageText for T
where
    T: StartCommentSingleLine + StartCommentMultiLine,
{
    fn identify_first_stage_text<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = FirstStageToken<String>, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString,
        String: FromIterator<<I as Input>::Item>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        self.take_until_start_comment_single_line()
            .or(self.take_until_start_comment_multi_line())
            .map(|x: I| FirstStageToken::Text(x.to_string()))
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use super::IdentifyFirstStageText;
    use crate::{
        base::comment::{multi_line::StartCommentMultiLine, single_line::StartCommentSingleLine},
        first_stage::token::FirstStageToken,
    };

    use nom::{
        error::{Error, ErrorKind, ParseError},
        AsChar, Err, IResult, Input, Parser,
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

    #[test]
    fn parse_empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_first_stage_text();
        let input: &str = "";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Err(Err::Error(
            Error::from_error_kind(input, ErrorKind::TakeUntil),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn parse_text_with_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_first_stage_text();
        let input: &str = "Random string``Some text``";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "``Some text``",
            FirstStageToken::Text("Random string".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn parse_text_with_multi_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.identify_first_stage_text();
        let input: &str = "Random string/*\r\nSome text*/";
        let result: IResult<&str, FirstStageToken<String>, Error<&str>> = Ok((
            "/*\r\nSome text*/",
            FirstStageToken::Text("Random string".to_string()),
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
