use nom::{bytes::take_until, error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

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
            .map(|x| x.into_iter().flatten().collect::<Vec<LexerToken<String>>>()).and(take_until(self.end_comment_single_line_tag())).map(|(mut list, remaining): (Vec<LexerToken<String>>, I)|{
                if remaining.input_len() > 0 {
                    list.push(LexerToken::Text(remaining.into()));

                }
                list
            }
        );
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
        let mut parser = config.get_single_line_terminated();
        let input: &str = "";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "``Some text``";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![LexerToken::StartCommentSingleLine, LexerToken::Text("Some text".to_string()), LexerToken::EndCommentSingleLine]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_feature_tag() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "``Some text Feature[ABCD & !(EFG | IJK)]``";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![LexerToken::StartCommentSingleLine, LexerToken::Text("Some text ".to_string()), LexerToken::Feature, LexerToken::StartBrace, LexerToken::Tag("ABCD".to_string()), LexerToken::Space, LexerToken::And, LexerToken::Space, LexerToken::Not, LexerToken::StartParen, LexerToken::Tag("EFG".to_string()), LexerToken::Space, LexerToken::Or,LexerToken::Space, LexerToken::Tag("IJK".to_string()), LexerToken::EndParen, LexerToken::EndBrace, LexerToken::EndCommentSingleLine]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn complex_feature_tag_with_default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "``Feature[ABCD & !(EFG | IJK)] Some text``";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![LexerToken::StartCommentSingleLine,  LexerToken::Feature, LexerToken::StartBrace, LexerToken::Tag("ABCD".to_string()), LexerToken::Space, LexerToken::And, LexerToken::Space, LexerToken::Not, LexerToken::StartParen, LexerToken::Tag("EFG".to_string()), LexerToken::Space, LexerToken::Or,LexerToken::Space, LexerToken::Tag("IJK".to_string()), LexerToken::EndParen, LexerToken::EndBrace, LexerToken::Text(" Some text".to_string()), LexerToken::EndCommentSingleLine]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_feature_tag_with_default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "``Some text Feature[ABCD & !(EFG | IJK)] Some text``";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![LexerToken::StartCommentSingleLine,LexerToken::Text("Some text ".into()),  LexerToken::Feature, LexerToken::StartBrace, LexerToken::Tag("ABCD".to_string()), LexerToken::Space, LexerToken::And, LexerToken::Space, LexerToken::Not, LexerToken::StartParen, LexerToken::Tag("EFG".to_string()), LexerToken::Space, LexerToken::Or,LexerToken::Space, LexerToken::Tag("IJK".to_string()), LexerToken::EndParen, LexerToken::EndBrace, LexerToken::Text(" Some text".to_string()), LexerToken::EndCommentSingleLine]));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn default_single_line_comment_with_complex_feature_tag_with_default_single_line_comment_with_complex_not_configuration_tag() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "``Some text Feature[ABCD & !(EFG | IJK)] Some text Configuration[!ABCD & !(EFG | IJK)]``";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![LexerToken::StartCommentSingleLine,LexerToken::Text("Some text ".into()),  LexerToken::Feature, LexerToken::StartBrace, LexerToken::Tag("ABCD".to_string()), LexerToken::Space, LexerToken::And, LexerToken::Space, LexerToken::Not, LexerToken::StartParen, LexerToken::Tag("EFG".to_string()), LexerToken::Space, LexerToken::Or,LexerToken::Space, LexerToken::Tag("IJK".to_string()), LexerToken::EndParen, LexerToken::EndBrace, LexerToken::Text(" Some text ".to_string()),LexerToken::Configuration, LexerToken::StartBrace,LexerToken::Not, LexerToken::Tag("ABCD".to_string()), LexerToken::Space, LexerToken::And, LexerToken::Space, LexerToken::Not, LexerToken::StartParen, LexerToken::Tag("EFG".to_string()), LexerToken::Space, LexerToken::Or,LexerToken::Space, LexerToken::Tag("IJK".to_string()), LexerToken::EndParen, LexerToken::EndBrace, LexerToken::EndCommentSingleLine]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_not_configuration_tag() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "``Some text Configuration[!ABCD & !(EFG | IJK)]``";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![LexerToken::StartCommentSingleLine, LexerToken::Text("Some text ".to_string()), LexerToken::Configuration, LexerToken::StartBrace,LexerToken::Not, LexerToken::Tag("ABCD".to_string()), LexerToken::Space, LexerToken::And, LexerToken::Space, LexerToken::Not, LexerToken::StartParen, LexerToken::Tag("EFG".to_string()), LexerToken::Space, LexerToken::Or,LexerToken::Space, LexerToken::Tag("IJK".to_string()), LexerToken::EndParen, LexerToken::EndBrace, LexerToken::EndCommentSingleLine]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_configuration_group_switch() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "``Some text ConfigurationGroup Switch``";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![LexerToken::StartCommentSingleLine, LexerToken::Text("Some text ".to_string()), LexerToken::ConfigurationGroupSwitch,  LexerToken::EndCommentSingleLine]));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_configuration_group_switch_with_typo() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: &str = "``Some text Configuration Group Switch``";
        let result: IResult<&str, Vec<LexerToken<String>>, Error<&str>> = Ok(("", vec![LexerToken::StartCommentSingleLine, LexerToken::Text("Some text ".to_string()), LexerToken::Text("Configuration Group Switch".to_string()), LexerToken::EndCommentSingleLine]));
        assert_eq!(parser.parse_complete(input), result)
    }

}
