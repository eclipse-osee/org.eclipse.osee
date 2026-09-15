/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
use nom::{
    AsBytes, AsChar, Compare, FindSubstring, Input, Offset, Parser,
    combinator::{recognize, rest, verify},
    error::ParseError,
    multi::many0,
};

use applicability_lexer_applicability_structure_base::line_terminations::{
    carriage_return::LexCarriageReturn, new_line::LexNewLine,
};
use applicability_lexer_base::{
    applicability_structure::{LexerToken, update_end_position, update_start_position},
    comment::single_line::StartCommentSingleLineNonTerminated,
    line_terminations::eof::Eof,
    position::Position,
    utils::{
        locatable::{Locatable, position},
        success_no_value::success_no_value,
        take_first::take_until_first2,
    },
};

use super::{
    config::tag::ConfigTagSingleLineNonTerminated,
    config_group::tag::ConfigGroupTagSingleLineNonTerminated,
    feature::tag::FeatureTagSingleLineNonTerminated,
    substitution::base::SubstitutionSingleLineNonTerminated,
    utils::loose_text::LooseTextNonTerminated,
};
type NonTerminatedAcc<I> = (((Vec<LexerToken<I>>, Position), I), Position);
pub trait SingleLineNonTerminated {
    fn get_single_line_non_terminated<I, E>(
        &self,
        preserve_comment: bool,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync
            + Offset
            + AsBytes,
        I::Item: AsChar,
        E: ParseError<I>;
}
impl<T> SingleLineNonTerminated for T
where
    T: StartCommentSingleLineNonTerminated
        + FeatureTagSingleLineNonTerminated
        + ConfigTagSingleLineNonTerminated
        + ConfigGroupTagSingleLineNonTerminated
        + SubstitutionSingleLineNonTerminated
        + LooseTextNonTerminated
        + LexCarriageReturn
        + LexNewLine
        + Eof,
{
    fn get_single_line_non_terminated<I, E>(
        &self,
        preserve_comment: bool,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync
            + Offset
            + AsBytes,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start = position()
            .and(self.start_comment_single_line_non_terminated())
            .and(position())
            .map(move |((start, input), end): ((Position, _), Position)| {
                if preserve_comment {
                    return LexerToken::<I>::SingleLineCommentCharacter(input, (start, end));
                }
                LexerToken::TextToDiscard(input, (start, end))
            });

        let applic_tag = self
            .feature_tag_non_terminated()
            .or(self.config_group_tag_non_terminated())
            .or(self.config_tag_non_terminated())
            .or(self.substitution_non_terminated());
        let inner_select = applic_tag.or(self.loose_text_non_terminated());
        let inner = many0(inner_select)
            .map(|x| x.into_iter().flatten().collect::<Vec<LexerToken<I>>>())
            .and(position())
            .and(
                take_until_first2(self.lex_carriage_return_tag(), self.lex_new_line_tag())
                    .or(rest)
                    .or(success_no_value()),
            )
            .and(position())
            .map(
                |(((mut list, start), remaining), end): NonTerminatedAcc<I>| {
                    if remaining.input_len() > 0 {
                        list.push(LexerToken::TextToDiscard(remaining, (start, end)));
                    }
                    list
                },
            );
        let windows_new_line = self
            .lex_carriage_return()
            .and(self.lex_new_line())
            .map(|x| (Some(x.0), Some(x.1)));
        let unix_new_line = self.lex_new_line().map(|x| (None, Some(x)));
        let eof_termination = self.eof().map(|_| (None, None));
        let end = windows_new_line
            .or(unix_new_line)
            .or(eof_termination)
            .map(|endings| {
                let mut results = vec![];
                if let Some(x) = endings.0 {
                    results.push(x);
                }
                if let Some(x) = endings.1 {
                    results.push(x);
                }
                results
            });

        let content = start.and(inner).and(end).map(|((start, mut tag), end)| {
            let mut start_idx = 0;
            let mut end_idx = 0;
            let start_pos = tag.iter().position(|result| {
                !matches!(
                    result,
                    LexerToken::Text(_, _)
                        | LexerToken::TextToDiscard(_, _)
                        | LexerToken::SingleLineCommentCharacter(_, _)
                )
            });
            let end_pos = tag.iter().rposition(|result| {
                !matches!(
                    result,
                    LexerToken::Text(_, _)
                        | LexerToken::TextToDiscard(_, _)
                        | LexerToken::SingleLineCommentCharacter(_, _)
                )
            });
            if let Some(x) = start_pos {
                start_idx = x;
            }
            if let Some(x) = end_pos {
                end_idx = x;
            }
            if let Some::<LexerToken<I>>(end_tag) = end.last().cloned() {
                let position_to_update = end_tag.get_end_position();
                if tag.len() > end_idx {
                    tag[end_idx] = update_end_position(tag[end_idx].clone(), position_to_update);
                }
            }
            if tag.len() > start_idx {
                tag[start_idx] =
                    update_start_position(tag[start_idx].clone(), start.get_start_position());
            }
            tag.insert(0, start);
            tag
        });
        let start2 = position()
            .and(self.start_comment_single_line_non_terminated())
            .and(position())
            .map(|((start, input), end): ((Position, _), Position)| {
                LexerToken::<I>::SingleLineCommentCharacter(input, (start, end))
            });

        let applic_tag2 = self
            .feature_tag_non_terminated()
            .or(self.config_tag_non_terminated())
            .or(self.config_group_tag_non_terminated())
            .or(self.substitution_non_terminated());
        let inner_select2 = applic_tag2.or(self.loose_text_non_terminated());
        let inner2 = many0(inner_select2)
            .map(|x| x.into_iter().flatten().collect::<Vec<LexerToken<I>>>())
            .and(position())
            .and(
                take_until_first2(self.lex_carriage_return_tag(), self.lex_new_line_tag())
                    .or(rest)
                    .or(success_no_value()),
            )
            .and(position())
            .map(
                |(((mut list, start), remaining), end): NonTerminatedAcc<I>| {
                    if remaining.input_len() > 0 {
                        list.push(LexerToken::TextToDiscard(remaining, (start, end)));
                    }
                    list
                },
            );
        let windows_new_line2 = self
            .lex_carriage_return()
            .and(self.lex_new_line())
            .map(|x| (Some(x.0), Some(x.1)));
        let unix_new_line2 = self.lex_new_line().map(|x| (None, Some(x)));
        let eof_termination2 = self.eof().map(|_| (None, None));
        let end2 = windows_new_line2
            .or(unix_new_line2)
            .or(eof_termination2)
            .map(|endings| {
                let mut results = vec![];
                if let Some(x) = endings.0 {
                    results.push(x);
                }
                if let Some(x) = endings.1 {
                    results.push(x);
                }
                results
            });

        let content2 = start2.and(inner2).and(end2).map(|((start, mut tag), end)| {
            let mut start_idx = 0;
            let mut end_idx = 0;
            let start_pos = tag.iter().position(|result| {
                !matches!(
                    result,
                    LexerToken::Text(_, _)
                        | LexerToken::TextToDiscard(_, _)
                        | LexerToken::SingleLineCommentCharacter(_, _)
                )
            });
            let end_pos = tag.iter().rposition(|result| {
                !matches!(
                    result,
                    LexerToken::Text(_, _)
                        | LexerToken::TextToDiscard(_, _)
                        | LexerToken::SingleLineCommentCharacter(_, _)
                )
            });
            if let Some(x) = start_pos {
                start_idx = x;
            }
            if let Some(x) = end_pos {
                end_idx = x;
            }
            if let Some::<LexerToken<I>>(end_tag) = end.last().cloned() {
                let position_to_update = end_tag.get_end_position();
                if tag.len() > end_idx {
                    tag[end_idx] = update_end_position(tag[end_idx].clone(), position_to_update);
                }
            }
            if tag.len() > start_idx {
                tag[start_idx] =
                    update_start_position(tag[start_idx].clone(), start.get_start_position());
            }
            tag.insert(0, start);
            tag
        });
        verify(content, |x: &Vec<LexerToken<I>>| {
            !x.iter()
                .filter(|result| {
                    !matches!(
                        result,
                        LexerToken::Text(_, _)
                            | LexerToken::TextToDiscard(_, _)
                            | LexerToken::SingleLineCommentCharacter(_, _)
                    )
                })
                .collect::<Vec<_>>()
                .is_empty()
        })
        .or(recognize(content2).and_then(
            position()
                .and(rest)
                .and(position())
                .map(|((start, text), end)| vec![LexerToken::Text(text, (start, end))]),
        ))
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use applicability_lexer_applicability_structure_test_utils::ResultType;

    use super::SingleLineNonTerminated;

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
    use pretty_assertions::assert_eq;

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
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> = LocatedSpan::new("");
        let result: ResultType<&str> =
            Err(Err::Error(Error::from_error_kind(input, ErrorKind::Tag)));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(12, 2, "", ()) },
            vec![LexerToken::Text(
                unsafe { LocatedSpan::new_from_raw_offset(0, 1, "``Some text\n", ()) },
                ((0, 1, 1), (12, 2, 1)),
            )],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_substitution() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> = LocatedSpan::new("``Eval[ABCD]\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(13, 2, "", ()) },
            vec![
                // LexerToken::SingleLineCommentCharacter(((0, 1), (2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(0, 1, "``", ()) },
                    ((0, 1, 1), (2, 1, 3)),
                ),
                LexerToken::Substitution(((0, 1, 1), (6, 1, 7))),
                LexerToken::StartBrace(((6, 1, 7), (7, 1, 8))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(7, 1, "ABCD", ()) },
                    ((7, 1, 8), (11, 1, 12)),
                ),
                LexerToken::EndBrace(((11, 1, 12), (13, 2, 1))),
                // LexerToken::UnixNewLine(((12, 1), (13, 2))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_feature_tag() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> =
            LocatedSpan::new("``Some text Feature[ABCD & !(EFG | IJK)]\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(41, 2, "", ()) },
            vec![
                // LexerToken::SingleLineCommentCharacter(((0, 1), (2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(0, 1, "``", ()) },
                    ((0, 1, 1), (2, 1, 3)),
                ),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    ((2, 1, 3), (12, 1, 13)),
                ),
                LexerToken::Feature(((0, 1, 1), (19, 1, 20))),
                LexerToken::StartBrace(((19, 1, 20), (20, 1, 21))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(20, 1, "ABCD", ()) },
                    ((20, 1, 21), (24, 1, 25)),
                ),
                LexerToken::Space(((24, 1, 25), (25, 1, 26))),
                LexerToken::And(((25, 1, 26), (26, 1, 27))),
                LexerToken::Space(((26, 1, 27), (27, 1, 28))),
                LexerToken::Not(((27, 1, 28), (28, 1, 29))),
                LexerToken::StartParen(((28, 1, 29), (29, 1, 30))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(29, 1, "EFG", ()) },
                    ((29, 1, 30), (32, 1, 33)),
                ),
                LexerToken::Space(((32, 1, 33), (33, 1, 34))),
                LexerToken::Or(((33, 1, 34), (34, 1, 35))),
                LexerToken::Space(((34, 1, 35), (35, 1, 36))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(35, 1, "IJK", ()) },
                    ((35, 1, 36), (38, 1, 39)),
                ),
                LexerToken::EndParen(((38, 1, 39), (39, 1, 40))),
                LexerToken::EndBrace(((39, 1, 40), (41, 2, 1))),
                // LexerToken::UnixNewLine(((40, 1), (41, 2))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn complex_feature_tag_with_default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> =
            LocatedSpan::new("``Feature[ABCD & !(EFG | IJK)] Some text\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(41, 2, "", ()) },
            vec![
                // LexerToken::SingleLineCommentCharacter(((0, 1), (2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(0, 1, "``", ()) },
                    ((0, 1, 1), (2, 1, 3)),
                ),
                LexerToken::Feature(((0, 1, 1), (9, 1, 10))),
                LexerToken::StartBrace(((9, 1, 10), (10, 1, 11))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(10, 1, "ABCD", ()) },
                    ((10, 1, 11), (14, 1, 15)),
                ),
                LexerToken::Space(((14, 1, 15), (15, 1, 16))),
                LexerToken::And(((15, 1, 16), (16, 1, 17))),
                LexerToken::Space(((16, 1, 17), (17, 1, 18))),
                LexerToken::Not(((17, 1, 18), (18, 1, 19))),
                LexerToken::StartParen(((18, 1, 19), (19, 1, 20))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(19, 1, "EFG", ()) },
                    ((19, 1, 20), (22, 1, 23)),
                ),
                LexerToken::Space(((22, 1, 23), (23, 1, 24))),
                LexerToken::Or(((23, 1, 24), (24, 1, 25))),
                LexerToken::Space(((24, 1, 25), (25, 1, 26))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(25, 1, "IJK", ()) },
                    ((25, 1, 26), (28, 1, 29)),
                ),
                LexerToken::EndParen(((28, 1, 29), (29, 1, 30))),
                LexerToken::EndBrace(((29, 1, 30), (41, 2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(30, 1, " Some text", ()) },
                    ((30, 1, 31), (40, 1, 41)),
                ),
                // LexerToken::UnixNewLine(((40, 1), (41, 2))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_feature_tag_with_default_single_line_comment() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> =
            LocatedSpan::new("``Some text Feature[ABCD & !(EFG | IJK)] Some text\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(51, 2, "", ()) },
            vec![
                // LexerToken::SingleLineCommentCharacter(((0, 1), (2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(0, 1, "``", ()) },
                    ((0, 1, 1), (2, 1, 3)),
                ),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    ((2, 1, 3), (12, 1, 13)),
                ),
                LexerToken::Feature(((0, 1, 1), (19, 1, 20))),
                LexerToken::StartBrace(((19, 1, 20), (20, 1, 21))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(20, 1, "ABCD", ()) },
                    ((20, 1, 21), (24, 1, 25)),
                ),
                LexerToken::Space(((24, 1, 25), (25, 1, 26))),
                LexerToken::And(((25, 1, 26), (26, 1, 27))),
                LexerToken::Space(((26, 1, 27), (27, 1, 28))),
                LexerToken::Not(((27, 1, 28), (28, 1, 29))),
                LexerToken::StartParen(((28, 1, 29), (29, 1, 30))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(29, 1, "EFG", ()) },
                    ((29, 1, 30), (32, 1, 33)),
                ),
                LexerToken::Space(((32, 1, 33), (33, 1, 34))),
                LexerToken::Or(((33, 1, 34), (34, 1, 35))),
                LexerToken::Space(((34, 1, 35), (35, 1, 36))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(35, 1, "IJK", ()) },
                    ((35, 1, 36), (38, 1, 39)),
                ),
                LexerToken::EndParen(((38, 1, 39), (39, 1, 40))),
                LexerToken::EndBrace(((39, 1, 40), (51, 2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(40, 1, " Some text", ()) },
                    ((40, 1, 41), (50, 1, 51)),
                ),
                // LexerToken::UnixNewLine(((50, 1), (51, 2))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
    #[test]
    fn default_single_line_comment_with_complex_feature_tag_with_default_single_line_comment_with_complex_not_configuration_tag()
     {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> = LocatedSpan::new(
            "``Some text Feature[ABCD & !(EFG | IJK)] Some text Configuration[!ABCD & !(EFG | IJK)]\n",
        );
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(87, 2, "", ()) },
            vec![
                // LexerToken::SingleLineCommentCharacter(((0, 1), (2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(0, 1, "``", ()) },
                    ((0, 1, 1), (2, 1, 3)),
                ),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    ((2, 1, 3), (12, 1, 13)),
                ),
                LexerToken::Feature(((0, 1, 1), (19, 1, 20))),
                LexerToken::StartBrace(((19, 1, 20), (20, 1, 21))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(20, 1, "ABCD", ()) },
                    ((20, 1, 21), (24, 1, 25)),
                ),
                LexerToken::Space(((24, 1, 25), (25, 1, 26))),
                LexerToken::And(((25, 1, 26), (26, 1, 27))),
                LexerToken::Space(((26, 1, 27), (27, 1, 28))),
                LexerToken::Not(((27, 1, 28), (28, 1, 29))),
                LexerToken::StartParen(((28, 1, 29), (29, 1, 30))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(29, 1, "EFG", ()) },
                    ((29, 1, 30), (32, 1, 33)),
                ),
                LexerToken::Space(((32, 1, 33), (33, 1, 34))),
                LexerToken::Or(((33, 1, 34), (34, 1, 35))),
                LexerToken::Space(((34, 1, 35), (35, 1, 36))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(35, 1, "IJK", ()) },
                    ((35, 1, 36), (38, 1, 39)),
                ),
                LexerToken::EndParen(((38, 1, 39), (39, 1, 40))),
                LexerToken::EndBrace(((39, 1, 40), (40, 1, 41))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(40, 1, " Some text ", ()) },
                    ((40, 1, 41), (51, 1, 52)),
                ),
                LexerToken::Configuration(((51, 1, 52), (64, 1, 65))),
                LexerToken::StartBrace(((64, 1, 65), (65, 1, 66))),
                LexerToken::Not(((65, 1, 66), (66, 1, 67))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(66, 1, "ABCD", ()) },
                    ((66, 1, 67), (70, 1, 71)),
                ),
                LexerToken::Space(((70, 1, 71), (71, 1, 72))),
                LexerToken::And(((71, 1, 72), (72, 1, 73))),
                LexerToken::Space(((72, 1, 73), (73, 1, 74))),
                LexerToken::Not(((73, 1, 74), (74, 1, 75))),
                LexerToken::StartParen(((74, 1, 75), (75, 1, 76))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(75, 1, "EFG", ()) },
                    ((75, 1, 76), (78, 1, 79)),
                ),
                LexerToken::Space(((78, 1, 79), (79, 1, 80))),
                LexerToken::Or(((79, 1, 80), (80, 1, 81))),
                LexerToken::Space(((80, 1, 81), (81, 1, 82))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(81, 1, "IJK", ()) },
                    ((81, 1, 82), (84, 1, 85)),
                ),
                LexerToken::EndParen(((84, 1, 85), (85, 1, 86))),
                LexerToken::EndBrace(((85, 1, 86), (87, 2, 1))),
                // LexerToken::UnixNewLine(((86, 1), (87, 2))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_complex_not_configuration_tag() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> =
            LocatedSpan::new("``Some text Configuration[!ABCD & !(EFG | IJK)]\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(48, 2, "", ()) },
            vec![
                // LexerToken::SingleLineCommentCharacter(((0, 1), (2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(0, 1, "``", ()) },
                    ((0, 1, 1), (2, 1, 3)),
                ),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    ((2, 1, 3), (12, 1, 13)),
                ),
                LexerToken::Configuration(((0, 1, 1), (25, 1, 26))),
                LexerToken::StartBrace(((25, 1, 26), (26, 1, 27))),
                LexerToken::Not(((26, 1, 27), (27, 1, 28))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(27, 1, "ABCD", ()) },
                    ((27, 1, 28), (31, 1, 32)),
                ),
                LexerToken::Space(((31, 1, 32), (32, 1, 33))),
                LexerToken::And(((32, 1, 33), (33, 1, 34))),
                LexerToken::Space(((33, 1, 34), (34, 1, 35))),
                LexerToken::Not(((34, 1, 35), (35, 1, 36))),
                LexerToken::StartParen(((35, 1, 36), (36, 1, 37))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(36, 1, "EFG", ()) },
                    ((36, 1, 37), (39, 1, 40)),
                ),
                LexerToken::Space(((39, 1, 40), (40, 1, 41))),
                LexerToken::Or(((40, 1, 41), (41, 1, 42))),
                LexerToken::Space(((41, 1, 42), (42, 1, 43))),
                LexerToken::Tag(
                    unsafe { LocatedSpan::new_from_raw_offset(42, 1, "IJK", ()) },
                    ((42, 1, 43), (45, 1, 46)),
                ),
                LexerToken::EndParen(((45, 1, 46), (46, 1, 47))),
                LexerToken::EndBrace(((46, 1, 47), (48, 2, 1))),
                // LexerToken::UnixNewLine(((47, 1), (48, 2))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_configuration_group_switch() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text ConfigurationGroup Switch\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(38, 2, "", ()) },
            vec![
                // LexerToken::SingleLineCommentCharacter(((0, 1), (2, 1))),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(0, 1, "``", ()) },
                    ((0, 1, 1), (2, 1, 3)),
                ),
                LexerToken::TextToDiscard(
                    unsafe { LocatedSpan::new_from_raw_offset(2, 1, "Some text ", ()) },
                    ((2, 1, 3), (12, 1, 13)),
                ),
                LexerToken::ConfigurationGroupSwitch(((0, 1, 1), (38, 2, 1))),
                // LexerToken::UnixNewLine(((37, 1), (38, 2))),
            ],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }

    #[test]
    fn default_single_line_comment_with_configuration_group_switch_with_typo() {
        let config = TestStruct { _ph: PhantomData };
        let mut parser = config.get_single_line_non_terminated(false);
        let input: LocatedSpan<&str> = LocatedSpan::new("``Some text Configuration Group Switch\n");
        let result: ResultType<&str> = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(39, 2, "", ()) },
            vec![LexerToken::Text(
                unsafe {
                    LocatedSpan::new_from_raw_offset(
                        0,
                        1,
                        "``Some text Configuration Group Switch\n",
                        (),
                    )
                },
                ((0, 1, 1), (39, 2, 1)),
            )],
        ));
        assert_eq!(parser.parse_complete(input), result)
    }
}
