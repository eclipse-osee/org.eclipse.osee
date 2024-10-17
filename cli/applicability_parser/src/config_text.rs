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
    combinator::{eof, map, map_parser, opt},
    sequence::{preceded, terminated, tuple},
    IResult,
};

use crate::counter::count_new_lines;

use super::end::end_tag_parser;

///
/// Returns a parser that will grab 0-n spaces, the word "End Configuration"
pub fn end_config_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, u8> {
    map(
        tuple((
            preceded(
                tag(custom_start_comment_syntax),
                terminated(
                    map_parser(multispace0, count_new_lines()),
                    tag("End Configuration"),
                ),
            ),
            tuple((
                map_parser(multispace0, count_new_lines()),
                preceded(opt(end_tag_parser(custom_end_comment_syntax)), opt(eof)),
            )),
        )),
        |(first, (second, eof))| match eof {
            Some(_) => first + second + 1,
            None => first + second,
        },
    )
}

///
/// Returns a parser that will grab 0-n spaces, the word "Configuration" 0-n spaces "["
pub fn start_config_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, u8> {
    map(
        preceded(
            tag(custom_start_comment_syntax),
            tuple((
                map_parser(multispace0, count_new_lines()),
                preceded(
                    tag("Configuration"),
                    terminated(map_parser(multispace0, count_new_lines()), tag("[")),
                ),
            )),
        ),
        |(first, second)| first + second,
    )
}
///
/// Returns a parser that will grab 0-n spaces, the word "Configuration Else"
pub fn else_config_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, u8> {
    map(
        tuple((
            preceded(
                tag(custom_start_comment_syntax),
                terminated(
                    map_parser(multispace0, count_new_lines()),
                    tag("Configuration Else"),
                ),
            ),
            tuple((
                terminated(
                    map_parser(multispace0, count_new_lines()),
                    opt(end_tag_parser(custom_end_comment_syntax)),
                ),
                map_parser(multispace0, count_new_lines()),
            )),
        )),
        |(first, (second, third))| first + second + third,
    )
}

///
/// Returns a parser that will grab 0-n spaces, the word "Configuration Not" 0-n spaces "["
pub fn not_config_text_parser<'a>(
    custom_start_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&'a str, u8> {
    map(
        preceded(
            tag(custom_start_comment_syntax),
            tuple((
                map_parser(multispace0, count_new_lines()),
                preceded(
                    tag("Configuration Not"),
                    terminated(map_parser(multispace0, count_new_lines()), tag("[")),
                ),
            )),
        ),
        |(first, second)| first + second,
    )
}
