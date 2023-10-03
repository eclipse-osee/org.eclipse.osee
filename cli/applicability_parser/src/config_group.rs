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
use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};
use nom::{
    branch::alt,
    character::complete::line_ending,
    combinator::{map, opt},
    multi::many0,
    sequence::tuple,
    IResult,
};

use crate::applicability_parser_syntax_tag::{
    ApplicabilitySyntaxTag, ApplicabilitySyntaxTagAnd, ApplicabilitySyntaxTagNot,
    ApplicabilitySyntaxTagNotAnd, ApplicabilitySyntaxTagNotOr, ApplicabilitySyntaxTagOr,
};

use super::{
    applicability_parser_syntax_tag::{ApplicabilityParserSyntaxTag, TagVariants},
    config_group_text::{
        else_config_group_text_parser, end_config_group_text_parser, not_config_group_text_parser,
        start_config_group_text_parser,
    },
    end::end_tag_parser,
    next::next_inner,
    tag_parser::applicability_tag,
    tag_parser::TokenSplit,
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
            opt(end_config_group_parser(
                custom_start_comment_syntax,
                custom_end_comment_syntax,
            )),
            opt(line_ending),
        )),
        |s: (Option<&str>, Option<&str>)| (s.1.unwrap_or(""), vec![], s),
    )
}

fn config_group_tag_parser<'a>(
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<TokenSplit>> {
    applicability_tag(starting_parser, end_tag_parser(custom_end_comment_syntax))
}
fn config_group_contents_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
) -> impl FnMut(
    &'a str,
) -> IResult<
    &'a str,
    (
        Vec<TokenSplit>,
        Option<&str>,
        Vec<ApplicabilityParserSyntaxTag>,
    ),
> {
    let tag_parser = config_group_tag_parser(starting_parser, custom_end_comment_syntax);
    let content_parser =
        parse_config_group_inner(custom_start_comment_syntax, custom_end_comment_syntax);
    tuple((tag_parser, opt(line_ending), content_parser))
}
///
/// "Configuration Group Else"
/// Configuration Group Inner
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
        opt(end_config_group_parser(
            custom_start_comment_syntax,
            custom_end_comment_syntax,
        )),
        opt(line_ending),
    ));
    tuple((
        else_config_group_parser(custom_start_comment_syntax, custom_end_comment_syntax),
        parse_config_group_inner(custom_start_comment_syntax, custom_end_comment_syntax),
        //note the following parser is equivalent to parse_end...just not using parse_end to not cause type explosion
        end_parser,
    ))
}
fn config_group_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
) -> impl FnMut(
    &'a str,
) -> IResult<
    &'a str,
    (
        (
            Vec<TokenSplit>,
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
        config_group_contents_parser(
            custom_start_comment_syntax,
            custom_end_comment_syntax,
            starting_parser,
        ),
        parse_else_or_end,
    ))
}

pub fn parse_config_group<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let combined_parser = config_group_parser(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
        start_config_group_text_parser(custom_start_comment_syntax),
    );
    map(
        combined_parser,
        |(
            (tokens, _ln1, contents),
            (_potential_else, else_contents, (_potential_end1, _potential_end2)),
        )| {
            let config_group_type = match tokens.first().cloned() {
                Some(token) => match token {
                    TokenSplit::And(_) => TagVariants::And,
                    TokenSplit::Or(_) => TagVariants::Or,
                    TokenSplit::AndOr(_) => TagVariants::Normal,
                },
                None => TagVariants::Normal,
            };
            let tag_list = tokens
                .iter()
                .map(|tag| match tag {
                    TokenSplit::And(tag_content)
                    | TokenSplit::Or(tag_content)
                    | TokenSplit::AndOr(tag_content) => tag_content.to_string().into(),
                })
                .collect::<Vec<ApplicabilityTag>>();
            match config_group_type {
                TagVariants::And => {
                    ApplicabilityParserSyntaxTag::TagAnd(ApplicabilitySyntaxTagAnd(
                        tag_list,
                        contents,
                        ApplicabilityTagTypes::ConfigurationGroup,
                        else_contents,
                    ))
                }
                TagVariants::Or => ApplicabilityParserSyntaxTag::TagOr(ApplicabilitySyntaxTagOr(
                    tag_list,
                    contents,
                    ApplicabilityTagTypes::ConfigurationGroup,
                    else_contents,
                )),
                TagVariants::Normal => ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                    tag_list,
                    contents,
                    ApplicabilityTagTypes::ConfigurationGroup,
                    else_contents,
                )),
            }
        },
    )
}
#[cfg(test)]
mod parse_config_group_tests {
    use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};

    use crate::applicability_parser_syntax_tag::{
        ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag, ApplicabilitySyntaxTagAnd,
        ApplicabilitySyntaxTagOr,
    };

    use super::parse_config_group;

    #[test]
    fn simple_config_group() {
        let mut parser = parse_config_group("``", "``");
        assert_eq!(
            parser(
                "`` ConfigurationGroup[SOMETHING] \n Some Text Here \n`` End ConfigurationGroup ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                    vec![ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "INCLUDED".to_string()
                    }],
                    vec![ApplicabilityParserSyntaxTag::Text(
                        "Some Text Here \n".to_string()
                    ),],
                    ApplicabilityTagTypes::ConfigurationGroup,
                    vec![]
                ))
            ))
        )
    }
    #[test]
    fn anded_config_group() {
        let mut parser = parse_config_group("``", "``");
        assert_eq!(
            parser("`` ConfigurationGroup[SOMETHING & SOMETHING ELSE] \n Some Text Here \n`` End ConfigurationGroup ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagAnd(ApplicabilitySyntaxTagAnd(
                    vec![ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "INCLUDED".to_string()
                    }, ApplicabilityTag {
                        tag: "SOMETHING ELSE".to_string(),
                        value: "INCLUDED".to_string()
                    }],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::ConfigurationGroup,
                    vec![]
                ))
            ))
        )
    }

    #[test]
    fn ored_config_group() {
        let mut parser = parse_config_group("``", "``");
        assert_eq!(
            parser("`` ConfigurationGroup[SOMETHING | SOMETHING ELSE] \n Some Text Here \n`` End ConfigurationGroup ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagOr(ApplicabilitySyntaxTagOr(
                    vec![ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "INCLUDED".to_string()
                    },ApplicabilityTag {
                        tag: "SOMETHING ELSE".to_string(),
                        value: "INCLUDED".to_string()
                    }],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::ConfigurationGroup,
                    vec![]
                ))
            ))
        )
    }
}
pub fn parse_config_group_not<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let combined_parser = config_group_parser(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
        not_config_group_text_parser(custom_start_comment_syntax),
    );
    map(
        combined_parser,
        |(
            (tokens, _ln1, contents),
            (_potential_else, else_contents, (_potential_end1, _potential_end2)),
        )| {
            let config_group_type = match tokens.first().cloned() {
                Some(token) => match token {
                    TokenSplit::And(_) => TagVariants::And,
                    TokenSplit::Or(_) => TagVariants::Or,
                    TokenSplit::AndOr(_) => TagVariants::Normal,
                },
                None => TagVariants::Normal,
            };
            let tag_list = tokens
                .iter()
                .map(|tag| match tag {
                    TokenSplit::And(tag_content)
                    | TokenSplit::Or(tag_content)
                    | TokenSplit::AndOr(tag_content) => tag_content.to_string().into(),
                })
                .collect::<Vec<ApplicabilityTag>>();
            match config_group_type {
                TagVariants::And => {
                    ApplicabilityParserSyntaxTag::TagNotAnd(ApplicabilitySyntaxTagNotAnd(
                        tag_list,
                        contents,
                        ApplicabilityTagTypes::ConfigurationGroup,
                        else_contents,
                    ))
                }
                TagVariants::Or => {
                    ApplicabilityParserSyntaxTag::TagNotOr(ApplicabilitySyntaxTagNotOr(
                        tag_list,
                        contents,
                        ApplicabilityTagTypes::ConfigurationGroup,
                        else_contents,
                    ))
                }
                TagVariants::Normal => {
                    ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                        tag_list,
                        contents,
                        ApplicabilityTagTypes::ConfigurationGroup,
                        else_contents,
                    ))
                }
            }
        },
    )
}
#[cfg(test)]
mod parse_config_group_not_tests {
    use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};

    use crate::applicability_parser_syntax_tag::{
        ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTagNot, ApplicabilitySyntaxTagNotAnd,
        ApplicabilitySyntaxTagNotOr,
    };

    use super::parse_config_group_not;

    #[test]
    fn simple_config_group() {
        let mut parser = parse_config_group_not("``", "``");
        assert_eq!(
            parser("`` ConfigurationGroup Not[SOMETHING] \n Some Text Here \n`` End ConfigurationGroup ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                    vec![ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "INCLUDED".to_string()
                    }],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::ConfigurationGroup,
                    vec![]
                ))
            ))
        )
    }
    #[test]
    fn anded_config_group() {
        let mut parser = parse_config_group_not("``", "``");
        assert_eq!(
            parser(
                "`` ConfigurationGroup Not[SOMETHING & SOMETHING ELSE] \n Some Text Here \n`` End ConfigurationGroup ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNotAnd(ApplicabilitySyntaxTagNotAnd(
                    vec![ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "INCLUDED".to_string()
                    }, ApplicabilityTag {
                        tag: "SOMETHING ELSE".to_string(),
                        value: "INCLUDED".to_string()
                    }],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::ConfigurationGroup,
                    vec![]
                ))
            ))
        )
    }

    #[test]
    fn ored_config_group() {
        let mut parser = parse_config_group_not("``", "``");
        assert_eq!(
            parser(
                "`` ConfigurationGroup Not[SOMETHING | SOMETHING ELSE] \n Some Text Here \n`` End ConfigurationGroup ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNotOr(ApplicabilitySyntaxTagNotOr(
                    vec![ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "INCLUDED".to_string()
                    }, ApplicabilityTag {
                        tag: "SOMETHING ELSE".to_string(),
                        value: "INCLUDED".to_string()
                    }],
                    vec![ApplicabilityParserSyntaxTag::Text("Some Text Here \n".to_string()),],
                    ApplicabilityTagTypes::ConfigurationGroup,
                    vec![]
                ))
            ))
        )
    }
}

fn end_config_group_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, &str> {
    end_config_group_text_parser(custom_start_comment_syntax, custom_end_comment_syntax)
}

fn else_config_group_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, &str> {
    else_config_group_text_parser(custom_start_comment_syntax, custom_end_comment_syntax)
}

fn parse_config_group_inner<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicabilityParserSyntaxTag>> {
    many0(next_inner(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
    ))
}
