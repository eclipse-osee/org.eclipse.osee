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
    bytes::complete::tag,
    character::complete::multispace0,
    combinator::map,
    sequence::{preceded, terminated},
    IResult,
};

use crate::{tag_parser::applicability_tag, ApplicabilityParserSyntaxTag};
pub fn parse_substitution<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, ApplicabilityParserSyntaxTag> {
    let start = preceded(
        tag(custom_start_comment_syntax),
        preceded(
            multispace0,
            terminated(tag("Eval"), preceded(multispace0, tag("["))),
        ),
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
pub fn parse_substitution_as_str<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, &'a str> {
    move |input: &str| {
        let inner = parse_substitution(custom_start_comment_syntax, custom_end_comment_syntax);
        map(inner, |_tokens| input)(input)
    }
}
