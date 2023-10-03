/*********************************************************************
 * Copyright (c) 2024 Boeing
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
use crate::substitution::{parse_substitution, parse_substitution_as_str};

use super::{
    config::{parse_config, parse_config_not},
    config_group::{parse_config_group, parse_config_group_not},
    config_group_text::{
        else_config_group_text_parser, end_config_group_text_parser, not_config_group_text_parser,
        start_config_group_text_parser,
    },
    config_text::{
        else_config_text_parser, end_config_text_parser, not_config_text_parser,
        start_config_text_parser,
    },
    feature::{parse_feature, parse_feature_not},
    feature_text::{
        else_feature_text_parser, end_feature_text_parser, not_feature_text_parser,
        start_feature_text_parser,
    },
    ApplicabilityParserSyntaxTag,
};
use nom::{
    branch::alt,
    character::complete::anychar,
    combinator::{map, not, peek},
    multi::many_till,
    sequence::{preceded, tuple},
    IResult,
};

///
/// Gets the next top level element to look for i.e. feature, config, config group, feature not, config not, config group not, or loose text
pub fn next<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    move |input: &str| {
        let start_feature = start_feature_text_parser(custom_start_comment_syntax);
        let start_feature_not = not_feature_text_parser(custom_start_comment_syntax);
        let start_config = start_config_text_parser(custom_start_comment_syntax);
        let start_config_not = not_config_text_parser(custom_start_comment_syntax);
        let start_config_group = start_config_group_text_parser(custom_start_comment_syntax);
        let start_config_group_not = not_config_group_text_parser(custom_start_comment_syntax);

        let inner_parser = alt((
            start_feature,
            start_feature_not,
            start_config,
            start_config_not,
            start_config_group,
            start_config_group_not,
            parse_substitution_as_str(),
        ));
        let remaining_chars = many_till(anychar, peek(inner_parser));
        let remaining = map(remaining_chars, |(parsed_characters, _)| {
            ApplicabilityParserSyntaxTag::Text(parsed_characters.into_iter().collect::<String>())
        });
        alt((
            parse_substitution(),
            parse_feature_not(custom_start_comment_syntax, custom_end_comment_syntax),
            parse_feature(custom_start_comment_syntax, custom_end_comment_syntax),
            parse_config_not(custom_start_comment_syntax, custom_end_comment_syntax),
            parse_config(custom_start_comment_syntax, custom_end_comment_syntax),
            parse_config_group_not(custom_start_comment_syntax, custom_end_comment_syntax),
            parse_config_group(custom_start_comment_syntax, custom_end_comment_syntax),
            remaining,
        ))(input)
    }
}

fn error_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    move |input: &str| {
        let mut err_inner_parser = map(
            tuple((
                not(end_feature_text_parser(
                    custom_start_comment_syntax,
                    custom_end_comment_syntax,
                )),
                not(end_config_text_parser(
                    custom_start_comment_syntax,
                    custom_end_comment_syntax,
                )),
                not(end_config_group_text_parser(
                    custom_start_comment_syntax,
                    custom_end_comment_syntax,
                )),
                not(else_feature_text_parser(
                    custom_start_comment_syntax,
                    custom_end_comment_syntax,
                )),
                not(else_config_text_parser(
                    custom_start_comment_syntax,
                    custom_end_comment_syntax,
                )),
                not(else_config_group_text_parser(
                    custom_start_comment_syntax,
                    custom_end_comment_syntax,
                )),
            )),
            |_| ApplicabilityParserSyntaxTag::Text("".to_string()),
        );
        err_inner_parser(input)
    }
}
///
/// Gets the next inner element to look for i.e. feature, config, config group, feature not, config not, config group not, or loose text
/// This differs from next() by erroring when End Feature|Configuration|ConfigurationGroup or (Feature|Configuration|Configuration Group) Else is the next token
/// It will also parse until the next end/else token, whereas next() will parse until EOF if any of these tokens are encountered as the next token.
pub fn next_inner<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    move |input: &str| {
        //parse a nested feature
        let start_feature = start_feature_text_parser(custom_start_comment_syntax);
        let start_feature_not = not_feature_text_parser(custom_start_comment_syntax);
        //parse a new config
        let start_config = start_config_text_parser(custom_start_comment_syntax);
        let start_config_not = not_config_text_parser(custom_start_comment_syntax);

        //parse a new config group
        let start_config_group = start_config_group_text_parser(custom_start_comment_syntax);
        let start_config_group_not = not_config_group_text_parser(custom_start_comment_syntax);
        // these three should be an indicator to grab loose remaining text
        // should return ElementType::Tag
        let end_feature =
            end_feature_text_parser(custom_start_comment_syntax, custom_end_comment_syntax);
        let end_config =
            end_config_text_parser(custom_start_comment_syntax, custom_end_comment_syntax);
        let end_config_group =
            end_config_group_text_parser(custom_start_comment_syntax, custom_end_comment_syntax);
        // these two should be a break for the parser to return to parse_feature,parse_config,or parse_config_group.
        let feature_else =
            else_feature_text_parser(custom_start_comment_syntax, custom_end_comment_syntax);
        let config_else =
            else_config_text_parser(custom_start_comment_syntax, custom_end_comment_syntax);
        let config_group_else =
            else_config_group_text_parser(custom_start_comment_syntax, custom_end_comment_syntax);

        //this is the next type to parse...

        let inner_parser = alt((
            start_feature,
            start_feature_not,
            start_config,
            start_config_not,
            start_config_group,
            start_config_group_not,
            end_feature,
            end_config,
            end_config_group,
            feature_else,
            config_else,
            config_group_else,
            parse_substitution_as_str(),
        ));
        let remaining_chars = many_till(anychar, peek(inner_parser));
        let remaining = map(remaining_chars, |(parsed_characters, _)| {
            ApplicabilityParserSyntaxTag::Text(parsed_characters.into_iter().collect::<String>())
        });
        //if any of these succeed, error out of the next loop
        let mut parser = preceded(
            error_parser(custom_start_comment_syntax, custom_end_comment_syntax),
            alt((
                parse_substitution(),
                parse_feature_not(custom_start_comment_syntax, custom_end_comment_syntax),
                parse_feature(custom_start_comment_syntax, custom_end_comment_syntax),
                parse_config_not(custom_start_comment_syntax, custom_end_comment_syntax),
                parse_config(custom_start_comment_syntax, custom_end_comment_syntax),
                parse_config_group_not(custom_start_comment_syntax, custom_end_comment_syntax),
                parse_config_group(custom_start_comment_syntax, custom_end_comment_syntax),
                remaining,
            )),
        );
        parser(input)
    }
}

#[cfg(test)]
mod tests {
    mod next_inner_tests {

        use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};
        use nom::{
            error::{Error, ErrorKind, ParseError},
            Err,
        };

        use crate::{
            applicability_parser_syntax_tag::{
                ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag, ApplicabilitySyntaxTagNot,
            },
            next::next_inner,
        };
        #[test]
        fn happy_path_feature_not() {
            let mut parser = next_inner("``", "``");
            assert_eq!(
                parser("`` Feature Not[SOMETHING] ``\n Some Text Here \n`` End Feature ``"),
                Ok((
                    "",
                    ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                        vec![ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "INCLUDED".to_string()
                        }],
                        vec![ApplicabilityParserSyntaxTag::Text(
                            " Some Text Here \n".to_string()
                        )],
                        ApplicabilityTagTypes::Feature,
                        vec![]
                    ))
                ))
            )
        }

        #[test]
        fn happy_path_configuration_not() {
            let mut parser = next_inner("``", "``");
            assert_eq!(
                parser(
                    "`` Configuration Not[SOMETHING] ``\n Some Text Here \n`` End Configuration ``"
                ),
                Ok((
                    "",
                    ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                        vec![ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "INCLUDED".to_string()
                        }],
                        vec![ApplicabilityParserSyntaxTag::Text(
                            " Some Text Here \n".to_string()
                        )],
                        ApplicabilityTagTypes::Configuration,
                        vec![]
                    ))
                ))
            )
        }

        #[test]
        fn bail_out_imm_end_feature() {
            let mut parser = next_inner("``", "``");
            assert_eq!(
                parser("`` End Feature ``"),
                Err(Err::Error(Error::from_error_kind(
                    "`` End Feature ``",
                    ErrorKind::Not
                )))
            )
        }

        #[test]
        fn bail_out_end_feature() {
            let mut parser = next_inner("``", "``");
            assert_eq!(
                parser("`` Feature[SOMETHING] ``\n Some Text Here \n`` End Feature ``"),
                Ok((
                    "",
                    ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                        vec![ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "INCLUDED".to_string()
                        }],
                        vec![ApplicabilityParserSyntaxTag::Text(
                            " Some Text Here \n".to_string()
                        )],
                        ApplicabilityTagTypes::Feature,
                        vec![]
                    )),
                ))
            )
        }
        #[test]
        fn bail_out_imm_end_config() {
            let mut parser = next_inner("``", "``");
            assert_eq!(
                parser("`` End Configuration ``"),
                Err(Err::Error(Error::from_error_kind(
                    "`` End Configuration ``",
                    ErrorKind::Not
                )))
            )
        }

        #[test]
        fn bail_out_end_config() {
            let mut parser = next_inner("``", "``");
            assert_eq!(
                parser("`` Configuration[SOMETHING] ``\n Some Text Here \n`` End Configuration ``"),
                Ok((
                    "",
                    ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                        vec![ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "INCLUDED".to_string()
                        }],
                        vec![ApplicabilityParserSyntaxTag::Text(
                            " Some Text Here \n".to_string()
                        )],
                        ApplicabilityTagTypes::Configuration,
                        vec![]
                    )),
                ))
            )
        }
    }
    mod error_parser_tests {
        use nom::{
            error::{Error, ErrorKind, ParseError},
            Err,
        };

        use crate::{next::error_parser, ApplicabilityParserSyntaxTag};

        #[test]
        fn bail_out_end_feature() {
            let mut parser = error_parser("``", "``");
            assert_eq!(
                parser("`` End Feature ``"),
                Err(Err::Error(Error::from_error_kind(
                    "`` End Feature ``",
                    ErrorKind::Not
                )))
            )
        }
        #[test]
        fn bail_out_end_config() {
            let mut parser = error_parser("``", "``");
            assert_eq!(
                parser("`` End Configuration ``"),
                Err(Err::Error(Error::from_error_kind(
                    "`` End Configuration ``",
                    ErrorKind::Not
                )))
            )
        }

        #[test]
        fn success_start_feature() {
            let mut parser = error_parser("``", "``");
            assert_eq!(
                parser("`` Feature[SOMETHING] ``\nSome Text"),
                Ok((
                    "`` Feature[SOMETHING] ``\nSome Text",
                    ApplicabilityParserSyntaxTag::Text("".to_string())
                ))
            )
        }

        #[test]
        fn success_start_feature_and_end() {
            let mut parser = error_parser("``", "``");
            assert_eq!(
                parser("`` Feature[SOMETHING] ``\nSome Text\n`` End Feature ``"),
                Ok((
                    "`` Feature[SOMETHING] ``\nSome Text\n`` End Feature ``",
                    ApplicabilityParserSyntaxTag::Text("".to_string())
                ))
            )
        }

        #[test]
        fn success_start_config() {
            let mut parser = error_parser("``", "``");
            assert_eq!(
                parser("`` Configuration[SOMETHING] ``\nSome Text"),
                Ok((
                    "`` Configuration[SOMETHING] ``\nSome Text",
                    ApplicabilityParserSyntaxTag::Text("".to_string())
                ))
            )
        }

        #[test]
        fn success_start_config_and_end() {
            let mut parser = error_parser("``", "``");
            assert_eq!(
                parser("`` Configuration[SOMETHING] ``\nSome Text\n`` End Configuration ``"),
                Ok((
                    "`` Configuration[SOMETHING] ``\nSome Text\n`` End Configuration ``",
                    ApplicabilityParserSyntaxTag::Text("".to_string())
                ))
            )
        }
    }
}
