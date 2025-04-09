use nom::{
    bytes::take_until, error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input,
    Parser,
};

use crate::applicability_structure::token::LexerToken;
use applicability_lexer_base::{
    comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineTerminated},
    utils::locatable::{position, Locatable},
};

use super::{
    config::tag::ConfigTagSingleLineTerminated,
    config_group::tag::ConfigGroupTagSingleLineTerminated,
    feature::tag::FeatureTagSingleLineTerminated,
    substitution::base::SubstitutionSingleLineTerminated, utils::loose_text::LooseTextTerminated,
};

pub trait SingleLineTerminated {
    fn get_single_line_terminated<I, E>(
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
impl<T> SingleLineTerminated for T
where
    T: StartCommentSingleLineTerminated
        + EndCommentSingleLineTerminated
        + FeatureTagSingleLineTerminated
        + ConfigTagSingleLineTerminated
        + ConfigGroupTagSingleLineTerminated
        + SubstitutionSingleLineTerminated
        + LooseTextTerminated,
{
    fn get_single_line_terminated<I, E>(
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
        let start = position()
            .and(self.start_comment_single_line_terminated())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::StartCommentSingleLineTerminated(start, end)
            });

        let applic_tag = self
            .feature_tag_terminated()
            .or(self.config_tag_terminated())
            .or(self.config_group_tag_terminated())
            .or(self.substitution_terminated());
        let inner_select = applic_tag.or(self.loose_text_terminated());
        let inner = many0(inner_select)
            .map(|x| x.into_iter().flatten().collect::<Vec<LexerToken<I>>>())
            .and(position())
            .and(take_until(self.end_comment_single_line_tag()))
            .and(position())
            .map(
                |(((mut list, start), remaining), end): (
                    ((Vec<LexerToken<I>>, (usize, u32)), I),
                    (usize, u32),
                )| {
                    if remaining.input_len() > 0 {
                        list.push(LexerToken::Text(remaining, start, end));
                    }
                    list
                },
            );
        let end = position()
            .and(self.end_comment_single_line())
            .and(position())
            .map(|((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::EndCommentSingleLineTerminated(start, end)
            });
        //TODO: fix we need to capture trailing new line characters
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
    use crate::applicability_structure::token::LexerToken;
    use applicability_lexer_base::{
        comment::multi_line::{EndCommentMultiLine, StartCommentMultiLine},
        comment::single_line::{EndCommentSingleLineTerminated, StartCommentSingleLineTerminated},
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
    impl<'a> StartCommentSingleLineTerminated for TestStruct<'a> {
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

    impl<'a> EndCommentSingleLineTerminated for TestStruct<'a> {
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
    impl<'a> DefaultApplicabilityLexer for TestStruct<'a> {
        fn is_default() -> bool {
            true
        }
    }
    #[test]
    fn empty_string() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text", ()) },
                    (2, 1),
                    (11, 1),
                ),
                LexerToken::EndCommentSingleLineTerminated((11, 1), (13, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_substitution() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Eval[ABCD]``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(14, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Substitution((2, 1), (6, 1)),
                LexerToken::StartBrace((6, 1), (7, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(7, 1, "ABCD", ()) },
                    (7, 1),
                    (11, 1),
                ),
                LexerToken::EndBrace((11, 1), (12, 1)),
                LexerToken::EndCommentSingleLineTerminated((12, 1), (14, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_feature_tag() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> =
            LocatedSpan::new("``Some text Feature[ABCD & !(EFG | IJK)]``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(42, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    (2, 1),
                    (12, 1),
                ),
                LexerToken::Feature((12, 1), (19, 1)),
                LexerToken::StartBrace((19, 1), (20, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(20, 1, "ABCD", ()) },
                    (20, 1),
                    (24, 1),
                ),
                LexerToken::Space((24, 1), (25, 1)),
                LexerToken::And((25, 1), (26, 1)),
                LexerToken::Space((26, 1), (27, 1)),
                LexerToken::Not((27, 1), (28, 1)),
                LexerToken::StartParen((28, 1), (29, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(29, 1, "EFG", ()) },
                    (29, 1),
                    (32, 1),
                ),
                LexerToken::Space((32, 1), (33, 1)),
                LexerToken::Or((33, 1), (34, 1)),
                LexerToken::Space((34, 1), (35, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(35, 1, "IJK", ()) },
                    (35, 1),
                    (38, 1),
                ),
                LexerToken::EndParen((38, 1), (39, 1)),
                LexerToken::EndBrace((39, 1), (40, 1)),
                LexerToken::EndCommentSingleLineTerminated((40, 1), (42, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn complex_feature_tag_with_default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> =
            LocatedSpan::new("``Feature[ABCD & !(EFG | IJK)] Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(42, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Feature((2, 1), (9, 1)),
                LexerToken::StartBrace((9, 1), (10, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(10, 1, "ABCD", ()) },
                    (10, 1),
                    (14, 1),
                ),
                LexerToken::Space((14, 1), (15, 1)),
                LexerToken::And((15, 1), (16, 1)),
                LexerToken::Space((16, 1), (17, 1)),
                LexerToken::Not((17, 1), (18, 1)),
                LexerToken::StartParen((18, 1), (19, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(19, 1, "EFG", ()) },
                    (19, 1),
                    (22, 1),
                ),
                LexerToken::Space((22, 1), (23, 1)),
                LexerToken::Or((23, 1), (24, 1)),
                LexerToken::Space((24, 1), (25, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(25, 1, "IJK", ()) },
                    (25, 1),
                    (28, 1),
                ),
                LexerToken::EndParen((28, 1), (29, 1)),
                LexerToken::EndBrace((29, 1), (30, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(30, 1, " Some text", ()) },
                    (30, 1),
                    (40, 1),
                ),
                LexerToken::EndCommentSingleLineTerminated((40, 1), (42, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_feature_tag_with_default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> =
            LocatedSpan::new("``Some text Feature[ABCD & !(EFG | IJK)] Some text``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(52, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    (2, 1),
                    (12, 1),
                ),
                LexerToken::Feature((12, 1), (19, 1)),
                LexerToken::StartBrace((19, 1), (20, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(20, 1, "ABCD", ()) },
                    (20, 1),
                    (24, 1),
                ),
                LexerToken::Space((24, 1), (25, 1)),
                LexerToken::And((25, 1), (26, 1)),
                LexerToken::Space((26, 1), (27, 1)),
                LexerToken::Not((27, 1), (28, 1)),
                LexerToken::StartParen((28, 1), (29, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(29, 1, "EFG", ()) },
                    (29, 1),
                    (32, 1),
                ),
                LexerToken::Space((32, 1), (33, 1)),
                LexerToken::Or((33, 1), (34, 1)),
                LexerToken::Space((34, 1), (35, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(35, 1, "IJK", ()) },
                    (35, 1),
                    (38, 1),
                ),
                LexerToken::EndParen((38, 1), (39, 1)),
                LexerToken::EndBrace((39, 1), (40, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(40, 1, " Some text", ()) },
                    (40, 1),
                    (50, 1),
                ),
                LexerToken::EndCommentSingleLineTerminated((50, 1), (52, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn default_single_line_comment_with_complex_feature_tag_with_default_single_line_comment_with_complex_not_configuration_tag(
    ) {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text Feature[ABCD & !(EFG | IJK)] Some text Configuration[!ABCD & !(EFG | IJK)]``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(88, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ".into(), ()) },
                    (2, 1),
                    (12, 1),
                ),
                LexerToken::Feature((12, 1), (19, 1)),
                LexerToken::StartBrace((19, 1), (20, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(20, 1, "ABCD", ()) },
                    (20, 1),
                    (24, 1),
                ),
                LexerToken::Space((24, 1), (25, 1)),
                LexerToken::And((25, 1), (26, 1)),
                LexerToken::Space((26, 1), (27, 1)),
                LexerToken::Not((27, 1), (28, 1)),
                LexerToken::StartParen((28, 1), (29, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(29, 1, "EFG", ()) },
                    (29, 1),
                    (32, 1),
                ),
                LexerToken::Space((32, 1), (33, 1)),
                LexerToken::Or((33, 1), (34, 1)),
                LexerToken::Space((34, 1), (35, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(35, 1, "IJK", ()) },
                    (35, 1),
                    (38, 1),
                ),
                LexerToken::EndParen((38, 1), (39, 1)),
                LexerToken::EndBrace((39, 1), (40, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(40, 1, " Some text ", ()) },
                    (40, 1),
                    (51, 1),
                ),
                LexerToken::Configuration((51, 1), (64, 1)),
                LexerToken::StartBrace((64, 1), (65, 1)),
                LexerToken::Not((65, 1), (66, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(66, 1, "ABCD", ()) },
                    (66, 1),
                    (70, 1),
                ),
                LexerToken::Space((70, 1), (71, 1)),
                LexerToken::And((71, 1), (72, 1)),
                LexerToken::Space((72, 1), (73, 1)),
                LexerToken::Not((73, 1), (74, 1)),
                LexerToken::StartParen((74, 1), (75, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(75, 1, "EFG", ()) },
                    (75, 1),
                    (78, 1),
                ),
                LexerToken::Space((78, 1), (79, 1)),
                LexerToken::Or((79, 1), (80, 1)),
                LexerToken::Space((80, 1), (81, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(81, 1, "IJK", ()) },
                    (81, 1),
                    (84, 1),
                ),
                LexerToken::EndParen((84, 1), (85, 1)),
                LexerToken::EndBrace((85, 1), (86, 1)),
                LexerToken::EndCommentSingleLineTerminated((86, 1), (88, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_not_configuration_tag() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> =
            LocatedSpan::new("``Some text Configuration[!ABCD & !(EFG | IJK)]``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(49, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    (2, 1),
                    (12, 1),
                ),
                LexerToken::Configuration((12, 1), (25, 1)),
                LexerToken::StartBrace((25, 1), (26, 1)),
                LexerToken::Not((26, 1), (27, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(27, 1, "ABCD", ()) },
                    (27, 1),
                    (31, 1),
                ),
                LexerToken::Space((31, 1), (32, 1)),
                LexerToken::And((32, 1), (33, 1)),
                LexerToken::Space((33, 1), (34, 1)),
                LexerToken::Not((34, 1), (35, 1)),
                LexerToken::StartParen((35, 1), (36, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(36, 1, "EFG", ()) },
                    (36, 1),
                    (39, 1),
                ),
                LexerToken::Space((39, 1), (40, 1)),
                LexerToken::Or((40, 1), (41, 1)),
                LexerToken::Space((41, 1), (42, 1)),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(42, 1, "IJK", ()) },
                    (42, 1),
                    (45, 1),
                ),
                LexerToken::EndParen((45, 1), (46, 1)),
                LexerToken::EndBrace((46, 1), (47, 1)),
                LexerToken::EndCommentSingleLineTerminated((47, 1), (49, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_configuration_group_switch() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text ConfigurationGroup Switch``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(39, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    (2, 1),
                    (12, 1),
                ),
                LexerToken::ConfigurationGroupSwitch((12, 1), (37, 1)),
                LexerToken::EndCommentSingleLineTerminated((37, 1), (39, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_configuration_group_switch_with_typo() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_terminated();
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text Configuration Group Switch``");
        let result: IResult<
            LocatedSpan<&str>,
            Vec<LexerToken<LocatedSpan<&str>>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(40, 1, "", ()) },
            vec![
                LexerToken::StartCommentSingleLineTerminated((0, 1), (2, 1)),
                LexerToken::Text(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    (2, 1),
                    (12, 1),
                ),
                LexerToken::Text(
                    unsafe {
                        LocatedSpan::new_from_raw_offset(12, 1, "Configuration Group Switch", ())
                    },
                    (12, 1),
                    (38, 1),
                ),
                LexerToken::EndCommentSingleLineTerminated((38, 1), (40, 1)),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
