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
use applicability::applic_tag::ApplicabilityTagTypes;
use applicability_parser_types::{
    applic_tokens::ApplicTokens,
    applicability_parser_syntax_tag::{
        ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag, ApplicabilitySyntaxTagNot,
    },
};
use nom::{
    branch::alt,
    character::complete::line_ending,
    combinator::{map, opt},
    multi::many0,
    sequence::tuple,
    IResult,
};

use crate::tag_parser::applicability_tag;

use super::{
    config_text::{
        else_config_text_parser, end_config_text_parser, not_config_text_parser,
        start_config_text_parser,
    },
    end::end_tag_parser,
    next::next_inner,
};
///
/// Parse End:
/// End Configuration
/// Line Ending
fn parse_end<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(
    &'a str,
) -> IResult<
    &str,
    (
        &str,
        Vec<ApplicabilityParserSyntaxTag>,
        (Option<&str>, Option<&str>),
    ),
> {
    map(
        tuple((
            opt(end_config_parser(
                custom_start_comment_syntax,
                custom_end_comment_syntax,
            )),
            opt(line_ending),
        )),
        |s: (Option<&str>, Option<&str>)| (s.1.unwrap_or(""), vec![], s),
    )
}
fn config_tag_parser<'a>(
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicTokens>> {
    applicability_tag(starting_parser, end_tag_parser(custom_end_comment_syntax))
}
fn config_contents_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
) -> impl FnMut(
    &'a str,
) -> IResult<
    &'a str,
    (
        Vec<ApplicTokens>,
        Option<&str>,
        Vec<ApplicabilityParserSyntaxTag>,
    ),
> {
    let tag_parser = config_tag_parser(starting_parser, custom_end_comment_syntax);
    let content_parser = parse_config_inner(custom_start_comment_syntax, custom_end_comment_syntax);
    tuple((tag_parser, opt(line_ending), content_parser))
}
///
/// "Configuration Else"
/// Configuration Inner
/// Parse End
/// alt((Parse Else, Parse End))
fn else_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(
    &'a str,
) -> IResult<
    &'a str,
    (
        &str,
        Vec<ApplicabilityParserSyntaxTag>,
        (Option<&str>, Option<&str>),
    ),
> {
    let end_parser = tuple((
        opt(end_config_parser(
            custom_start_comment_syntax,
            custom_end_comment_syntax,
        )),
        opt(line_ending),
    ));
    tuple((
        else_config_parser(custom_start_comment_syntax, custom_end_comment_syntax),
        parse_config_inner(custom_start_comment_syntax, custom_end_comment_syntax),
        //note the following parser is equivalent to parse_end...just not using parse_end to not cause type explosion
        end_parser,
    ))
}
fn config_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
) -> impl FnMut(
    &'a str,
) -> IResult<
    &'a str,
    (
        (
            Vec<ApplicTokens>,
            Option<&str>,
            Vec<ApplicabilityParserSyntaxTag>,
        ),
        (
            &'a str,
            Vec<ApplicabilityParserSyntaxTag>,
            (Option<&str>, Option<&str>),
        ),
    ),
> {
    let parse_else_or_end = alt((
        else_parser(custom_start_comment_syntax, custom_end_comment_syntax),
        parse_end(custom_start_comment_syntax, custom_end_comment_syntax),
    ));

    tuple((
        config_contents_parser(
            custom_start_comment_syntax,
            custom_end_comment_syntax,
            starting_parser,
        ),
        parse_else_or_end,
    ))
}
pub fn parse_config<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let combined_parser = config_parser(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
        start_config_text_parser(custom_start_comment_syntax),
    );
    map(
        combined_parser,
        |(
            (tokens, _ln1, contents),
            (_potential_else, else_contents, (_potential_end1, _potential_end2)),
        )| {
            ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                tokens,
                contents,
                ApplicabilityTagTypes::Configuration,
                else_contents,
            ))
        },
    )
}
#[cfg(test)]
mod parse_config_tests {
    use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};
    use applicability_parser_types::{
        applic_tokens::{
            ApplicTokens, ApplicabilityAndTag, ApplicabilityNoTag, ApplicabilityOrTag,
        },
        applicability_parser_syntax_tag::{ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag},
    };

    use super::parse_config;

    #[test]
    fn simple_config() {
        let mut parser = parse_config("``", "``");
        assert_eq!(
            parser("`` Configuration[SOMETHING] \n Some Text Here \n`` End Configuration ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "Included".to_string()
                    }))],
                    vec![ApplicabilityParserSyntaxTag::Text(
                        "Some Text Here \n".to_string()
                    ),],
                    ApplicabilityTagTypes::Configuration,
                    vec![]
                ))
            ))
        )
    }
    #[test]
    fn anded_config() {
        let mut parser = parse_config("``", "``");
        assert_eq!(
            parser("`` Configuration[SOMETHING & SOMETHING_ELSE] \n Some Text Here \n`` End Configuration ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "Included".to_string()
                    })),
                    ApplicTokens::And(ApplicabilityAndTag(ApplicabilityTag {
                        tag: "SOMETHING_ELSE".to_string(),
                        value: "Included".to_string()
                    }))],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::Configuration,
                    vec![]
                ))
            ))
        )
    }

    #[test]
    fn ored_config() {
        let mut parser = parse_config("``", "``");
        assert_eq!(
            parser("`` Configuration[SOMETHING | SOMETHING_ELSE] \n Some Text Here \n`` End Configuration ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "Included".to_string()
                    })),
                    ApplicTokens::Or(ApplicabilityOrTag(ApplicabilityTag {
                        tag: "SOMETHING_ELSE".to_string(),
                        value: "Included".to_string()
                    }))],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::Configuration,
                    vec![]
                ))
            ))
        )
    }
}
pub fn parse_config_not<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let combined_parser = config_parser(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
        not_config_text_parser(custom_start_comment_syntax),
    );
    map(
        combined_parser,
        |(
            (tokens, _ln1, contents),
            (_potential_else, else_contents, (_potential_end1, _potential_end2)),
        )| {
            ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                tokens,
                contents,
                ApplicabilityTagTypes::Configuration,
                else_contents,
            ))
        },
    )
}
#[cfg(test)]
mod parse_config_not_tests {
    use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};
    use applicability_parser_types::{
        applic_tokens::{
            ApplicTokens, ApplicabilityAndTag, ApplicabilityNoTag, ApplicabilityOrTag,
        },
        applicability_parser_syntax_tag::{
            ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTagNot,
        },
    };

    use super::parse_config_not;

    #[test]
    fn simple_config() {
        let mut parser = parse_config_not("``", "``");
        assert_eq!(
            parser("`` Configuration Not[SOMETHING] \n Some Text Here \n`` End Configuration ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "Included".to_string()
                    }))],
                    vec![ApplicabilityParserSyntaxTag::Text(
                        "Some Text Here \n".to_string()
                    ),],
                    ApplicabilityTagTypes::Configuration,
                    vec![]
                ))
            ))
        )
    }
    #[test]
    fn anded_config() {
        let mut parser = parse_config_not("``", "``");
        assert_eq!(
            parser(
                "`` Configuration Not[SOMETHING & SOMETHING_ELSE] \n Some Text Here \n`` End Configuration ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "Included".to_string()
                    })),
                    ApplicTokens::And(ApplicabilityAndTag(ApplicabilityTag {
                        tag: "SOMETHING_ELSE".to_string(),
                        value: "Included".to_string()
                    }))],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::Configuration,
                    vec![]
                ))
            ))
        )
    }

    #[test]
    fn ored_config() {
        let mut parser = parse_config_not("``", "``");
        assert_eq!(
            parser(
                "`` Configuration Not[SOMETHING | SOMETHING_ELSE] \n Some Text Here \n`` End Configuration ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "Included".to_string()
                    })),
                    ApplicTokens::Or(ApplicabilityOrTag(ApplicabilityTag {
                        tag: "SOMETHING_ELSE".to_string(),
                        value: "Included".to_string()
                    }))],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::Configuration,
                    vec![]
                ))
            ))
        )
    }
}
fn end_config_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, &str> {
    end_config_text_parser(custom_start_comment_syntax, custom_end_comment_syntax)
}

fn else_config_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, &str> {
    else_config_text_parser(custom_start_comment_syntax, custom_end_comment_syntax)
}

fn parse_config_inner<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicabilityParserSyntaxTag>> {
    many0(next_inner(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
    ))
}
