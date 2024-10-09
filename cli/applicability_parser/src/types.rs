/*********************************************************************
 * Copyright (c) 2025 Boeing
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
use applicability_parser_types::{
    applic_tokens::ApplicTokens, applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag,
};
use nom::IResult;

pub type ElseInner<'a> = (
    u8,
    Vec<ApplicabilityParserSyntaxTag>,
    (Option<u8>, Option<&'a str>),
);
pub type ElseResult<'a> = IResult<&'a str, ElseInner<'a>>;

pub type ContentsInner<'a> = (
    Vec<ApplicTokens>,
    Option<&'a str>,
    Vec<ApplicabilityParserSyntaxTag>,
);
pub type ContentsResult<'a> = IResult<&'a str, ContentsInner<'a>>;

pub type EndInner<'a> = (
    u8,
    Vec<ApplicabilityParserSyntaxTag>,
    (Option<u8>, Option<&'a str>),
);
pub type EndResult<'a> = IResult<&'a str, EndInner<'a>>;
pub type ParserInner<'a> = (ContentsInner<'a>, ElseInner<'a>);
pub type ParserResult<'a> = IResult<&'a str, ParserInner<'a>>;
