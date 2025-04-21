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
use nom::{
    IResult,
    bytes::complete::tag,
    character::complete::multispace0,
    combinator::{map, map_parser},
    sequence::{preceded, terminated, tuple},
};

use crate::counter::count_new_lines;

use crate::{ApplicabilityParserSyntaxTag, tag_parser::applicability_tag};
pub fn parse_substitution<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let start = map(
        preceded(
            tag(custom_start_comment_syntax),
            tuple((
                map_parser(multispace0, count_new_lines()),
                preceded(
                    tag("Eval"),
                    terminated(map_parser(multispace0, count_new_lines()), tag("[")),
                ),
            )),
        ),
        |(first, second)| first + second,
    );
    let right_brace = tag("]");
    let inner = applicability_tag(
        start,
        terminated(right_brace, tag(custom_end_comment_syntax)),
    );
    map(inner, |tokens| {
        ApplicabilityParserSyntaxTag::Substitution(tokens)
    })
}
pub fn parse_substitution_as_u8<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, u8> {
    move |input: &str| {
        let inner = parse_substitution(custom_start_comment_syntax, custom_end_comment_syntax);
        // anything not a Substitution Tag should be considered an error returning from parse_substitution
        map(inner, |token| match token {
            ApplicabilityParserSyntaxTag::Text(_) => 0,
            ApplicabilityParserSyntaxTag::Tag(_) => 0,
            ApplicabilityParserSyntaxTag::TagNot(_) => 0,
            ApplicabilityParserSyntaxTag::Substitution(t) => t
                .iter()
                .map(|x| match x {
                    applicability_parser_types::applic_tokens::ApplicTokens::NoTag(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::Not(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::And(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::NotAnd(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::Or(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::NotOr(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::NestedAnd(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::NestedNotAnd(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::NestedOr(t) => {
                        t.1.unwrap_or_default()
                    }
                    applicability_parser_types::applic_tokens::ApplicTokens::NestedNotOr(t) => {
                        t.1.unwrap_or_default()
                    }
                })
                .sum(),
            ApplicabilityParserSyntaxTag::SubstitutionNot(_) => 0,
        })(input)
    }
}
