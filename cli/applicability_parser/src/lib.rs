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
use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag;
use nom::{
    branch::permutation,
    character::complete::anychar,
    combinator::{eof, map},
    multi::{many0, many_till},
    IResult,
};
#[cfg(feature = "serde")]
// #[macro_use] uncomment if you use Deserialize or Serialize on a struct in applicability lib
extern crate serde;
use self::next::next;

mod config;
mod config_group;
mod config_group_text;
mod config_text;
mod counter;
mod end;
mod feature;
mod feature_text;
mod next;
mod substitution;
mod tag_parser;

fn get_remaining_text(input: &str) -> IResult<&str, ApplicabilityParserSyntaxTag> {
    let characters = many_till(anychar, eof);
    let text = map(characters, |(results, _): (Vec<char>, &str)| {
        results.iter().clone().collect::<String>()
    });
    let mut parser = map(text, |remaining_text| {
        ApplicabilityParserSyntaxTag::Text(remaining_text.to_string())
    });
    parser(input)
}

///
/// Main entry point for parsing a chunk of text for applicabilities.
///
/// Requires the start and end comment syntaxes for the given file.
/// An example for markdown would be (``, ``)
///
/// # Examples:
/// ``` rust
/// use applicability_parser::parse_applicability;
/// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilitySyntaxTag;
/// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag;
/// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Tag;
/// use applicability_parser_types::applicability_parser_syntax_tag::ApplicabilityParserSyntaxTag::Text;
/// use applicability::applic_tag::ApplicabilityTagTypes::Configuration;
/// use applicability::applic_tag::ApplicabilityTagTypes::Feature;
/// use applicability::applic_tag::ApplicabilityTag;
/// use applicability_parser_types::applic_tokens::{ApplicabilityNoTag,ApplicTokens};
///
/// assert_eq!(parse_applicability("Some other text
/// ``Feature[SOMETHING] ``
/// Some text here  
/// ``End Feature`` More text
/// ``Configuration [SOME_CONFIGURATION]``
/// configuration text
/// ``End Configuration``","``","``"),Ok(("",vec![Text("Some other text\n".to_string()), Tag(ApplicabilitySyntaxTag([ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag { tag: "SOMETHING".to_string(), value: "Included".to_string() },0))].to_vec(), vec![Text("Some text here  \n".to_string())], Feature, [].to_vec(), 1,0,0)), Text(" More text\n".to_string()), Tag(ApplicabilitySyntaxTag([ApplicTokens::NoTag(ApplicabilityNoTag(ApplicabilityTag { tag: "SOME_CONFIGURATION".to_string(), value: "Included".to_string() },0))].to_vec(), vec![Text("configuration text\n".to_string())], Configuration, [].to_vec(), 1,0,1)), Text("".to_string())])));
/// ```
pub fn parse_applicability<'a>(
    input: &'a str,
    custom_start_comment_syntax: &'a str,
    custom_end_comment_syntax: &'a str,
) -> IResult<&'a str, Vec<ApplicabilityParserSyntaxTag>> {
    let mut parser = map(
        permutation((
            many0(next(custom_start_comment_syntax, custom_end_comment_syntax)),
            get_remaining_text,
        )),
        |(mut parsed_results, remaining)| {
            parsed_results.push(remaining);
            parsed_results
        },
    );
    parser(input)
}
