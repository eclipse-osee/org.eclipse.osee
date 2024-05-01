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

use super::{
    end::end_tag_parser,
    feature_text::{
        else_feature_text_parser, end_feature_text_parser, not_feature_text_parser,
        start_feature_text_parser,
    },
    next::next_inner,
    tag_parser::applicability_tag,
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
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicTokens>> {
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
        Vec<ApplicTokens>,
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
            ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                tokens,
                contents,
                ApplicabilityTagTypes::Feature,
                else_contents,
            ))
        },
    )
}

#[cfg(test)]
mod parse_feature_tests {
    use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};
    use applicability_parser_types::{
        applic_tokens::{
            ApplicTokens, ApplicabilityAndTag, ApplicabilityNoTag, ApplicabilityOrTag,
        },
        applicability_parser_syntax_tag::{ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTag},
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
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "Included".to_string()
                    }))],
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
            parser("`` Feature[SOMETHING & SOMETHING_ELSE] \n Some Text Here \n`` End Feature ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                    vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "Included".to_string()
                        })),
                        ApplicTokens::And(ApplicabilityAndTag(ApplicabilityTag {
                            tag: "SOMETHING_ELSE".to_string(),
                            value: "Included".to_string()
                        }))
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
            parser("`` Feature[SOMETHING | SOMETHING_ELSE] \n Some Text Here \n`` End Feature ``"),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::Tag(ApplicabilitySyntaxTag(
                    vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "Included".to_string()
                        })),
                        ApplicTokens::Or(ApplicabilityOrTag(ApplicabilityTag {
                            tag: "SOMETHING_ELSE".to_string(),
                            value: "Included".to_string()
                        }))
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
            ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                tokens,
                contents,
                ApplicabilityTagTypes::Feature,
                else_contents,
            ))
        },
    )
}
#[cfg(test)]
mod parse_feature_not_tests {
    use applicability::applic_tag::{ApplicabilityTag, ApplicabilityTagTypes};
    use applicability_parser_types::{
        applic_tokens::{
            ApplicTokens, ApplicabilityAndTag, ApplicabilityNoTag, ApplicabilityOrTag,
        },
        applicability_parser_syntax_tag::{
            ApplicabilityParserSyntaxTag, ApplicabilitySyntaxTagNot,
        },
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
                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                        tag: "SOMETHING".to_string(),
                        value: "Included".to_string()
                    }))],
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
                "`` Feature Not[SOMETHING & SOMETHING_ELSE] \n Some Text Here \n`` End Feature ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                    vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "Included".to_string()
                        })),
                        ApplicTokens::And(ApplicabilityAndTag(ApplicabilityTag {
                            tag: "SOMETHING_ELSE".to_string(),
                            value: "Included".to_string()
                        }))
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
                "`` Feature Not[SOMETHING | SOMETHING_ELSE] \n Some Text Here \n`` End Feature ``"
            ),
            Ok((
                "",
                ApplicabilityParserSyntaxTag::TagNot(ApplicabilitySyntaxTagNot(
                    vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag {
                            tag: "SOMETHING".to_string(),
                            value: "Included".to_string()
                        })),
                        ApplicTokens::Or(ApplicabilityOrTag(ApplicabilityTag {
                            tag: "SOMETHING_ELSE".to_string(),
                            value: "Included".to_string()
                        }))
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
