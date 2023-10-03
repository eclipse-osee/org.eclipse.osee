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
    character::complete::char,
    combinator::map,
    sequence::{delimited, tuple},
    IResult,
};

use crate::{
    applicability_parser_syntax_tag::{self, TagVariants},
    tag_parser::{substitution_tag, TokenSplit},
    ApplicabilityParserSyntaxTag,
};
pub fn parse_substitution<'a>(
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let equals = char('=');
    let left_brace = char('[');
    let start = tuple((equals, left_brace));
    let right_brace = char(']');
    let inner = delimited(start, substitution_tag(), right_brace);
    map(inner, |tokens| {
        let substitution_type = match tokens.first().cloned() {
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
                | TokenSplit::AndOr(tag_content) => tag_content.to_string(),
            })
            .collect::<Vec<String>>();
        match substitution_type {
            TagVariants::And => ApplicabilityParserSyntaxTag::SubstitutionAnd(
                applicability_parser_syntax_tag::SubstitutionSyntaxTagAnd(tag_list),
            ),
            TagVariants::Or => ApplicabilityParserSyntaxTag::SubstitutionOr(
                applicability_parser_syntax_tag::SubstitutionSyntaxTagOr(tag_list),
            ),
            TagVariants::Normal => match tag_list.first() {
                Some(tag) => ApplicabilityParserSyntaxTag::Substitution(tag.to_owned()),
                None => ApplicabilityParserSyntaxTag::Text("".to_string()),
            },
        }
    })
}
pub fn parse_substitution_as_str<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, &'a str> {
    move |input: &str| {
        let equals = char('=');
        let left_brace = char('[');
        let start = tuple((equals, left_brace));
        let right_brace = char(']');
        let inner = delimited(start, substitution_tag(), right_brace);
        map(inner, |_tokens| input)(input)
    }
}
