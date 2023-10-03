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
    combinator::opt,
    sequence::{preceded, terminated, tuple},
    IResult,
};

use super::end::end_tag_parser;

pub fn end_config_group_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, &str> {
    terminated(
        preceded(
            tag(custom_start_comment_syntax),
            preceded(multispace0, tag("End ConfigurationGroup")),
        ),
        tuple((multispace0, opt(end_tag_parser(custom_end_comment_syntax)))),
    )
}

pub fn start_config_group_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, &str> {
    preceded(
        tag(custom_start_comment_syntax),
        preceded(
            multispace0,
            terminated(tag("ConfigurationGroup"), preceded(multispace0, tag("["))),
        ),
    )
}
pub fn else_config_group_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, &str> {
    terminated(
        preceded(
            tag(custom_start_comment_syntax),
            preceded(multispace0, tag("ConfigurationGroup Else")),
        ),
        tuple((multispace0, opt(end_tag_parser(custom_end_comment_syntax)))),
    )
}

pub fn not_config_group_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, &str> {
    preceded(
        tag(custom_start_comment_syntax),
        preceded(
            multispace0,
            terminated(
                tag("ConfigurationGroup Not"),
                preceded(multispace0, tag("[")),
            ),
        ),
    )
}
