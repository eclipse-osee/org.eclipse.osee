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
    bytes::complete::take_until,
    combinator::{map, map_res},
    error::{Error, ErrorKind, ParseError},
    Err, IResult,
};

pub fn split_by_and_or(input: &str) -> IResult<&str, Vec<&str>> {
    let inner = take_until("]");
    let mut parser = map(inner, |tag_content: &str| {
        tag_content
            .split(&['&', '|'][..])
            .map(|split_tag| split_tag.trim())
            .collect()
    });
    parser(input)
}

pub fn split_by_and_or_no_spaces_allowed(input: &str) -> IResult<&str, Vec<&str>> {
    let inner_parser = map_res(take_until("]"), |tag_content: &str| {
        match !tag_content.contains(' ') {
            true => Ok(tag_content),
            false => Err(Err::Error(Error::from_error_kind(input, ErrorKind::IsNot))),
        }
    });
    let mut parser = map(inner_parser, |tag_content: &str| {
        tag_content
            .split(&['&', '|'][..])
            .map(|split_tag| split_tag.trim())
            .collect()
    });
    parser(input)
}

pub fn split_by_and(input: &str) -> IResult<&str, Vec<&str>> {
    let inner_parser = map_res(take_until("]"), |tag_content: &str| {
        match tag_content.contains('&') {
            true => Ok(tag_content),
            false => Err(Err::Error(Error::from_error_kind(input, ErrorKind::IsNot))),
        }
    });
    let mut parser = map(inner_parser, |tag_content: &str| {
        tag_content
            .split(&['&'][..])
            .map(|split_tag| split_tag.trim())
            .collect::<Vec<&str>>()
    });
    parser(input)
}

pub fn split_by_and_no_spaces_allowed(input: &str) -> IResult<&str, Vec<&str>> {
    let inner_parser = map_res(take_until("]"), |tag_content: &str| {
        match tag_content.contains('&') && !tag_content.contains(' ') {
            true => Ok(tag_content),
            false => Err(Err::Error(Error::from_error_kind(input, ErrorKind::IsNot))),
        }
    });
    let mut parser = map(inner_parser, |tag_content: &str| {
        tag_content
            .split(&['&'][..])
            .map(|split_tag| split_tag.trim())
            .collect::<Vec<&str>>()
    });
    parser(input)
}

pub fn split_by_or(input: &str) -> IResult<&str, Vec<&str>> {
    let inner_parser = map_res(take_until("]"), |tag_content: &str| {
        match tag_content.contains('|') {
            true => Ok(tag_content),
            false => Err(Err::Error(Error::from_error_kind(input, ErrorKind::IsNot))),
        }
    });
    let mut parser = map(inner_parser, |tag_content: &str| {
        tag_content
            .split(&['|'][..])
            .map(|split_tag| split_tag.trim())
            .collect()
    });
    parser(input)
}
pub fn split_by_or_no_spaces_allowed(input: &str) -> IResult<&str, Vec<&str>> {
    let inner = map_res(take_until("]"), |tag_content: &str| {
        match tag_content.contains('|') && !tag_content.contains(' ') {
            true => Ok(tag_content),
            false => Err(Err::Error(Error::from_error_kind(input, ErrorKind::IsNot))),
        }
    });
    let mut parser = map(inner, |tag_content: &str| {
        tag_content
            .split(&['|'][..])
            .map(|split_tag| split_tag.trim())
            .collect()
    });
    parser(input)
}
