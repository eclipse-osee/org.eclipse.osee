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
    branch::{alt, permutation},
    bytes::complete::tag,
    character::complete::multispace0,
    combinator::{map, opt},
    sequence::{delimited, terminated},
    IResult,
};

use crate::split::{
    split_by_and_no_spaces_allowed, split_by_and_or_no_spaces_allowed,
    split_by_or_no_spaces_allowed,
};

use super::split::{split_by_and, split_by_and_or, split_by_or};

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum TokenSplit {
    And(String),
    Or(String),
    AndOr(String),
}

pub fn applicability_tag<'a>(
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
    ending_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<TokenSplit>> {
    //get a vector of strings splitting on |, or &, or both until ]
    let p1 = map(split_by_and, |token_list| {
        token_list
            .into_iter()
            .map(|token| TokenSplit::And(token.to_string()))
            .collect::<Vec<TokenSplit>>()
    });
    let p2 = map(split_by_or, |token_list| {
        token_list
            .into_iter()
            .map(|token| TokenSplit::Or(token.to_string()))
            .collect::<Vec<TokenSplit>>()
    });
    let p3 = map(split_by_and_or, |token_list| {
        token_list
            .into_iter()
            .map(|token| TokenSplit::AndOr(token.to_string()))
            .collect::<Vec<TokenSplit>>()
    });
    let inner = alt((p1, p2, p3));
    terminated(
        delimited(starting_parser, inner, tag("]")),
        permutation((multispace0, opt(ending_parser))),
    )
}

pub fn substitution_tag<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, Vec<TokenSplit>> {
    //get a vector of strings splitting on |, or &, or both until ]
    let p1 = map(split_by_and_no_spaces_allowed, |token_list| {
        token_list
            .into_iter()
            .map(|token| TokenSplit::And(token.to_string()))
            .collect::<Vec<TokenSplit>>()
    });
    let p2 = map(split_by_or_no_spaces_allowed, |token_list| {
        token_list
            .into_iter()
            .map(|token| TokenSplit::Or(token.to_string()))
            .collect::<Vec<TokenSplit>>()
    });
    let p3 = map(split_by_and_or_no_spaces_allowed, |token_list| {
        token_list
            .into_iter()
            .map(|token| TokenSplit::AndOr(token.to_string()))
            .collect::<Vec<TokenSplit>>()
    });
    alt((p1, p2, p3))
}
