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
    end::end_tag_parser,
    feature_text::{
        else_feature_text_parser, end_feature_text_parser, not_feature_text_parser,
        start_feature_text_parser,
    },
    next::next_inner,
    tag_parser::{applicability_tag, TokenSplit},
};
///
/// Parse End:
/// End Feature
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
            opt(end_feature_parser(
                custom_start_comment_syntax,
                custom_end_comment_syntax,
            )),
            opt(line_ending),
        )),
        |s: (Option<&str>, Option<&str>)| (s.1.unwrap_or(""), vec![], s),
    )
}

fn feature_tag_parser<'a>(
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<TokenSplit>> {
    applicability_tag(starting_parser, end_tag_parser(custom_end_comment_syntax))
}
fn feature_contents_parser<'a>(
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
    let tag_parser = feature_tag_parser(starting_parser, custom_end_comment_syntax);
    let content_parser =
        parse_feature_inner(custom_start_comment_syntax, custom_end_comment_syntax);
    tuple((tag_parser, opt(line_ending), content_parser))
}
///
/// "Feature Else"
/// Feature Inner
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
        opt(end_feature_parser(
            custom_start_comment_syntax,
            custom_end_comment_syntax,
        )),
        opt(line_ending),
    ));
    tuple((
        else_feature_parser(custom_start_comment_syntax, custom_end_comment_syntax),
        parse_feature_inner(custom_start_comment_syntax, custom_end_comment_syntax),
        //note the following parser is equivalent to parse_end...just not using parse_end to not cause type explosion
        end_parser,
    ))
}
fn feature_parser<'a>(
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
        feature_contents_parser(
            custom_start_comment_syntax,
            custom_end_comment_syntax,
            starting_parser,
        ),
        parse_else_or_end,
    ))
}

pub fn parse_feature<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let combined_parser = feature_parser(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
        start_feature_text_parser(custom_start_comment_syntax),
    );
    map(
        combined_parser,
        |(
            (tokens, _ln1, contents),
            (_potential_else, else_contents, (_potential_end1, _potential_end2)),
        )| {
            let feature_type = match tokens.first().cloned() {
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
            match feature_type {
                TagVariants::And => {
                    ApplicabilityParserSyntaxTag::TagAnd(ApplicabilitySyntaxTagAnd(
                        tag_list,
                        contents,
                        ApplicabilityTagTypes::Feature,
                        else_contents,
                    ))
                }
                TagVariants::Or => ApplicabilityParserSyntaxTag::TagOr(ApplicabilitySyntaxTagOr(
                    tag_list,
                    contents,
                    ApplicabilityTagTypes::Feature,
                    else_contents,
                )),
                TagVariants::Normal => ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                    tag_list,
                    contents,
                    ApplicabilityTagTypes::Feature,
                    else_contents,
                )),
            }
        },
    )
}

#[cfg(test)]
mod parse_feature_tests {
    use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};

    use crate::applicability_parser_syntax_tag::{
        ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag, ApplicabilitySyntaxTagAnd,
        ApplicabilitySyntaxTagOr,
    };

    use super::parse_feature;

    #[test]
    fn simple_feature() {
        let mut parser = parse_feature("``", "``");
        assert_eq!(
            parser("`` Feature[SOMETHING] \n Some Text Here \n`` End Feature ``"),
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
                    ApplicabilityTagTypes::Feature,
                    vec![]
                ))
            ))
        )
    }
    #[test]
    fn anded_feature() {
        let mut parser = parse_feature("``", "``");
        assert_eq!(
            parser("`` Feature[SOMETHING & SOMETHING ELSE] \n Some Text Here \n`` End Feature ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagAnd(ApplicabilitySyntaxTagAnd(
                    vec![
                        ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "INCLUDED".to_string()
                        },
                        ApplicabilityTag {
                            tag: "SOMETHING ELSE".to_string(),
                            value: "INCLUDED".to_string()
                        }
                    ],
                    vec![ApplicabilityParserSyntaxTag::Text(
                        "Some Text Here \n".to_string()
                    ),],
                    ApplicabilityTagTypes::Feature,
                    vec![]
                ))
            ))
        )
    }

    #[test]
    fn ored_feature() {
        let mut parser = parse_feature("``", "``");
        assert_eq!(
            parser("`` Feature[SOMETHING | SOMETHING ELSE] \n Some Text Here \n`` End Feature ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagOr(ApplicabilitySyntaxTagOr(
                    vec![
                        ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "INCLUDED".to_string()
                        },
                        ApplicabilityTag {
                            tag: "SOMETHING ELSE".to_string(),
                            value: "INCLUDED".to_string()
                        }
                    ],
                    vec![ApplicabilityParserSyntaxTag::Text(
                        "Some Text Here \n".to_string()
                    ),],
                    ApplicabilityTagTypes::Feature,
                    vec![]
                ))
            ))
        )
    }
}

pub fn parse_feature_not<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let combined_parser = feature_parser(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
        not_feature_text_parser(custom_start_comment_syntax),
    );
    map(
        combined_parser,
        |(
            (tokens, _ln1, contents),
            (_potential_else, else_contents, (_potential_end1, _potential_end2)),
        )| {
            let feature_type = match tokens.first().cloned() {
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
            match feature_type {
                TagVariants::And => {
                    ApplicabilityParserSyntaxTag::TagNotAnd(ApplicabilitySyntaxTagNotAnd(
                        tag_list,
                        contents,
                        ApplicabilityTagTypes::Feature,
                        else_contents,
                    ))
                }
                TagVariants::Or => {
                    ApplicabilityParserSyntaxTag::TagNotOr(ApplicabilitySyntaxTagNotOr(
                        tag_list,
                        contents,
                        ApplicabilityTagTypes::Feature,
                        else_contents,
                    ))
                }
                TagVariants::Normal => {
                    ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                        tag_list,
                        contents,
                        ApplicabilityTagTypes::Feature,
                        else_contents,
                    ))
                }
            }
        },
    )
}
#[cfg(test)]
mod parse_feature_not_tests {
    use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};

    use crate::applicability_parser_syntax_tag::{
        ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTagNot, ApplicabilitySyntaxTagNotAnd,
        ApplicabilitySyntaxTagNotOr,
    };

    use super::parse_feature_not;

    #[test]
    fn simple_feature() {
        let mut parser = parse_feature_not("``", "``");
        assert_eq!(
            parser("`` Feature Not[SOMETHING] \n Some Text Here \n`` End Feature ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                    vec![ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "INCLUDED".to_string()
                    }],
                    vec![ApplicabilityParserSyntaxTag::Text(
                        "Some Text Here \n".to_string()
                    ),],
                    ApplicabilityTagTypes::Feature,
                    vec![]
                ))
            ))
        )
    }
    #[test]
    fn anded_feature() {
        let mut parser = parse_feature_not("``", "``");
        assert_eq!(
            parser(
                "`` Feature Not[SOMETHING & SOMETHING ELSE] \n Some Text Here \n`` End Feature ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNotAnd(ApplicabilitySyntaxTagNotAnd(
                    vec![
                        ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "INCLUDED".to_string()
                        },
                        ApplicabilityTag {
                            tag: "SOMETHING ELSE".to_string(),
                            value: "INCLUDED".to_string()
                        }
                    ],
                    vec![ApplicabilityParserSyntaxTag::Text(
                        "Some Text Here \n".to_string()
                    ),],
                    ApplicabilityTagTypes::Feature,
                    vec![]
                ))
            ))
        )
    }

    #[test]
    fn ored_feature() {
        let mut parser = parse_feature_not("``", "``");
        assert_eq!(
            parser(
                "`` Feature Not[SOMETHING | SOMETHING ELSE] \n Some Text Here \n`` End Feature ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNotOr(ApplicabilitySyntaxTagNotOr(
                    vec![
                        ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "INCLUDED".to_string()
                        },
                        ApplicabilityTag {
                            tag: "SOMETHING ELSE".to_string(),
                            value: "INCLUDED".to_string()
                        }
                    ],
                    vec![ApplicabilityParserSyntaxTag::Text(
                        "Some Text Here \n".to_string()
                    ),],
                    ApplicabilityTagTypes::Feature,
                    vec![]
                ))
            ))
        )
    }
}
fn end_feature_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, &str> {
    end_feature_text_parser(custom_start_comment_syntax, custom_end_comment_syntax)
}

fn else_feature_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, &str> {
    else_feature_text_parser(custom_start_comment_syntax, custom_end_comment_syntax)
}

fn parse_feature_inner<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicabilityParserSyntaxTag>> {
    many0(next_inner(
        custom_start_comment_syntax,
        custom_end_comment_syntax,
    ))
}
