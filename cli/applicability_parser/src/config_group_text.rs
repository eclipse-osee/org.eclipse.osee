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
    combinator::{map, map_parser, opt},
    sequence::{preceded, terminated, tuple},
    IResult,
};

use crate::counter::count_new_lines;

use super::end::end_tag_parser;

pub fn end_config_group_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, u8> {
    map(tuple((
        preceded(
            tag(custom_start_comment_syntax),
            terminated(map_parser(multispace0,count_new_lines()), tag("End ConfigurationGroup")),
        ),
        tuple((map_parser(multispace0,count_new_lines()), opt(end_tag_parser(custom_end_comment_syntax)))),
    )),|(first,(second, _remaining_string))|{first+second})
}

pub fn start_config_group_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, u8> {
    map(preceded(
        tag(custom_start_comment_syntax),
        tuple((
            map_parser(multispace0,count_new_lines()),
            preceded(tag("ConfigurationGroup"), terminated(map_parser(multispace0,count_new_lines()), tag("["))),
        )),
    ),|(first,second)|{first+second})
}
pub fn else_config_group_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, u8> {
    map(tuple((
        preceded(
            tag(custom_start_comment_syntax),
            terminated(map_parser(multispace0,count_new_lines()), tag("ConfigurationGroup Else")),
        ),
        terminated(map_parser(multispace0,count_new_lines()), opt(end_tag_parser(custom_end_comment_syntax))),
    )),|(first,second)|{first+second})
}

pub fn not_config_group_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, u8> {
    map(preceded(
        tag(custom_start_comment_syntax),
        tuple((
            map_parser(multispace0,count_new_lines()),
            preceded(
                tag("ConfigurationGroup Not"),
                terminated(map_parser(multispace0,count_new_lines()), tag("[")),
            ),
        ),
    )),|(first,second)|{first+second})
}
