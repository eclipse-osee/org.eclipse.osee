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
use applicability_parser_types::applic_tokens::{
    ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedAndTag, ApplicabilityNestedNotAndTag,
    ApplicabilityNestedNotOrTag, ApplicabilityNestedOrTag, ApplicabilityNoTag,
    ApplicabilityNotAndTag, ApplicabilityNotOrTag, ApplicabilityNotTag, ApplicabilityOrTag,
};
use nom::{
    branch::{alt, permutation},
    bytes::complete::tag,
    character::complete::{anychar, multispace0, multispace1},
    combinator::{eof, map, opt, peek},
    error::{ErrorKind, ParseError},
    multi::many_till,
    sequence::{delimited, preceded, terminated, tuple},
    AsChar, IResult, InputTakeAtPosition,
};

fn parse_not_and<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, ApplicTokens> {
    map(
        preceded(tag("!"), parse_until_new_word),
        |parsed_characters| ApplicTokens::NotAnd(ApplicabilityNotAndTag(parsed_characters.into())),
    )
}

fn parse_not_and_nested<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, ApplicTokens> {
    map(
        preceded(
            tuple((tag("!"), multispace0, tag("("))),
            applicability_tag_inner(),
        ),
        |parsed_tokens| ApplicTokens::NestedNotAnd(ApplicabilityNestedNotAndTag(parsed_tokens)),
    )
}

fn parse_and_nested<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, ApplicTokens> {
    map(
        preceded(tag("("), applicability_tag_inner()),
        |parsed_tokens| ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(parsed_tokens)),
    )
}

fn parse_and<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, ApplicTokens> {
    map(parse_until_new_word, |parsed_characters| {
        ApplicTokens::And(ApplicabilityAndTag(parsed_characters.into()))
    })
}

fn parse_and_tokens<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicTokens>> {
    //3
    let parse_and = preceded(
        delimited(opt(multispace0), tag("&"), opt(multispace0)),
        alt((
            parse_not_and_nested(),
            parse_not_and(),
            parse_and_nested(),
            parse_and(),
        )),
    );
    map(
        many_till(parse_and, alt((tag("("), tag(")"), tag("]"), eof))),
        |(and_content, _)| and_content,
    )
}

#[cfg(test)]
mod parse_applic_or_tests {

    use applicability_parser_types::applic_tokens::{
        ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedNotOrTag, ApplicabilityNestedOrTag,
        ApplicabilityNoTag, ApplicabilityNotAndTag, ApplicabilityNotOrTag, ApplicabilityOrTag,
    };

    use super::{parse_not_or, parse_not_or_nested, parse_or, parse_or_nested, parse_or_tokens};

    #[test]
    fn simple_tag() {
        let mut parser = parse_or();
        assert_eq!(
            parser("JHU_CONTROLLER"),
            Ok((
                "",
                ApplicTokens::Or(ApplicabilityOrTag("JHU_CONTROLLER".to_string().into()))
            ))
        )
    }
    #[test]
    fn not_tag() {
        let mut parser = parse_not_or();
        assert_eq!(
            parser("!JHU_CONTROLLER"),
            Ok((
                "",
                ApplicTokens::NotOr(ApplicabilityNotOrTag("JHU_CONTROLLER".to_string().into()))
            ))
        )
    }
    #[test]
    fn nested_not_tag() {
        let mut parser = parse_not_or_nested();
        assert_eq!(
            parser("!(JHU_CONTROLLER)"),
            Ok((
                "",
                ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![ApplicTokens::NoTag(
                    ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())
                )]))
            ))
        )
    }

    #[test]
    fn nested_tag() {
        let mut parser = parse_or_nested();
        assert_eq!(
            parser("(JHU_CONTROLLER)"),
            Ok((
                "",
                ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![ApplicTokens::NoTag(
                    ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())
                )]))
            ))
        )
    }

    #[test]
    fn parse_tokens() {
        let mut parser = parse_or_tokens();
        assert_eq!(
            parser("| ENGINE_5=A2543)"),
            Ok((
                "",
                vec![ApplicTokens::Or(ApplicabilityOrTag(
                    "ENGINE_5=A2543".to_string().into()
                ))]
            ))
        )
    }
    #[test]
    fn nested_or_tag() {
        let mut parser = parse_or_nested();
        assert_eq!(
            parser("(JHU_CONTROLLER | ENGINE_5=A2543)"),
            Ok((
                "",
                ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::Or(ApplicabilityOrTag("ENGINE_5=A2543".to_string().into()))
                ]))
            ))
        )
    }

    #[test]
    fn nested_or_nested_tag() {
        let mut parser = parse_or_nested();
        assert_eq!(
            parser("(JHU_CONTROLLER | (ENGINE_5=A2543 | ROBOT_SPEAKER=SPKR_B))"),
            Ok((
                "",
                ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::Or(ApplicabilityOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]))
            ))
        )
    }

    #[test]
    fn nested_or_nested_and_tag() {
        let mut parser = parse_or_nested();
        assert_eq!(
            parser("(JHU_CONTROLLER | (ENGINE_5=A2543 & ROBOT_SPEAKER=SPKR_B))"),
            Ok((
                "",
                ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::And(ApplicabilityAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]))
            ))
        )
    }

    #[test]
    fn nested_not_or_tag() {
        let mut parser = parse_or_nested();
        assert_eq!(
            parser("(JHU_CONTROLLER | !ENGINE_5=A2543)"),
            Ok((
                "",
                ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NotOr(ApplicabilityNotOrTag("ENGINE_5=A2543".to_string().into()))
                ]))
            ))
        )
    }

    #[test]
    fn nested_and_tag() {
        let mut parser = parse_or_nested();
        assert_eq!(
            parser("(JHU_CONTROLLER & ENGINE_5=A2543)"),
            Ok((
                "",
                ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::And(ApplicabilityAndTag("ENGINE_5=A2543".to_string().into()))
                ]))
            ))
        )
    }

    #[test]
    fn nested_not_and_tag() {
        let mut parser = parse_or_nested();
        assert_eq!(
            parser("(JHU_CONTROLLER & !ENGINE_5=A2543)"),
            Ok((
                "",
                ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                        "ENGINE_5=A2543".to_string().into()
                    ))
                ]))
            ))
        )
    }
}
fn parse_not_or<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, ApplicTokens> {
    map(
        preceded(tag("!"), parse_until_new_word),
        |parsed_characters| ApplicTokens::NotOr(ApplicabilityNotOrTag(parsed_characters.into())),
    )
}

fn parse_not_or_nested<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, ApplicTokens> {
    map(
        preceded(
            tuple((tag("!"), multispace0, tag("("))),
            applicability_tag_inner(),
        ),
        |parsed_tokens| ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(parsed_tokens)),
    )
}

fn parse_or_nested<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, ApplicTokens> {
    map(
        preceded(tag("("), applicability_tag_inner()),
        |parsed_tokens| ApplicTokens::NestedOr(ApplicabilityNestedOrTag(parsed_tokens)),
    )
}

fn parse_or<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, ApplicTokens> {
    map(parse_until_new_word, |parsed_characters| {
        ApplicTokens::Or(ApplicabilityOrTag(parsed_characters.into()))
    })
}

fn parse_or_tokens<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicTokens>> {
    //4
    let parse_or = preceded(
        delimited(multispace0, tag("|"), multispace0),
        alt((
            parse_not_or_nested(),
            parse_not_or(),
            parse_or_nested(),
            parse_or(),
        )),
    );
    map(
        many_till(parse_or, alt((tag("("), tag(")"), tag("]"), eof))),
        |(or_content, _)| or_content,
    )
}

#[cfg(test)]
mod applic_tag_inner_tests {

    use applicability_parser_types::applic_tokens::{
        ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedAndTag, ApplicabilityNestedNotOrTag,
        ApplicabilityNestedOrTag, ApplicabilityNoTag, ApplicabilityNotAndTag,
        ApplicabilityNotOrTag, ApplicabilityNotTag, ApplicabilityOrTag,
    };

    use super::applicability_tag_inner;

    #[test]
    fn simple_tag() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER"),
            Ok((
                "",
                vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    "JHU_CONTROLLER".to_string().into()
                ))]
            ))
        )
    }

    #[test]
    fn not_tag() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER"),
            Ok((
                "",
                vec![ApplicTokens::Not(ApplicabilityNotTag(
                    "JHU_CONTROLLER".to_string().into()
                ))]
            ))
        )
    }

    #[test]
    fn simple_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER & ENGINE_5=A2543"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::And(ApplicabilityAndTag("ENGINE_5=A2543".to_string().into()))
                ]
            ))
        )
    }

    #[test]
    fn nested_and_with_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER & (ENGINE_5=A2543 & ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::And(ApplicabilityAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn nested_and_with_not_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER & (ENGINE_5=A2543 & !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn nested_and_with_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER & (ENGINE_5=A2543 | ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::Or(ApplicabilityOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn nested_and_with_not_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER & (ENGINE_5=A2543 | !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotOr(ApplicabilityNotOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn not_nested_and_with_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER & (ENGINE_5=A2543 & ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::And(ApplicabilityAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn not_nested_and_with_not_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER & (ENGINE_5=A2543 & !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn not_nested_and_with_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER & (ENGINE_5=A2543 | ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::Or(ApplicabilityOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn not_nested_and_with_not_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER & (ENGINE_5=A2543 | !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedAnd(ApplicabilityNestedAndTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotOr(ApplicabilityNotOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn simple_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | ENGINE_5=A2543"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::Or(ApplicabilityOrTag("ENGINE_5=A2543".to_string().into()))
                ]
            ))
        )
    }

    #[test]
    fn nested_or_with_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | (ENGINE_5=A2543 & ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::And(ApplicabilityAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn nested_or_with_not_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | (ENGINE_5=A2543 & !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn nested_or_with_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | (ENGINE_5=A2543 | ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::Or(ApplicabilityOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn nested_or_with_not_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | (ENGINE_5=A2543 | !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotOr(ApplicabilityNotOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn nested_not_or_with_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | !(ENGINE_5=A2543 & ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::And(ApplicabilityAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn nested_not_or_with_not_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | !(ENGINE_5=A2543 & !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn nested_not_or_with_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | !(ENGINE_5=A2543 | ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::Or(ApplicabilityOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn nested_not_or_with_not_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("JHU_CONTROLLER | !(ENGINE_5=A2543 | !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::NoTag(ApplicabilityNoTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotOr(ApplicabilityNotOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn not_nested_or_with_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER | (ENGINE_5=A2543 & ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::And(ApplicabilityAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn not_nested_or_with_not_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER | (ENGINE_5=A2543 & !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn not_nested_or_with_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER | (ENGINE_5=A2543 | ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::Or(ApplicabilityOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn not_nested_or_with_not_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER | (ENGINE_5=A2543 | !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedOr(ApplicabilityNestedOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotOr(ApplicabilityNotOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn not_nested_not_or_with_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER | !(ENGINE_5=A2543 & ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::And(ApplicabilityAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn not_nested_not_or_with_not_and() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER | !(ENGINE_5=A2543 & !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotAnd(ApplicabilityNotAndTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }

    #[test]
    fn not_nested_not_or_with_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER | !(ENGINE_5=A2543 | ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::Or(ApplicabilityOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
    #[test]
    fn not_nested_not_or_with_not_or() {
        let mut parser = applicability_tag_inner();
        assert_eq!(
            parser("!JHU_CONTROLLER | !(ENGINE_5=A2543 | !ROBOT_SPEAKER=SPKR_B)"),
            Ok((
                "",
                vec![
                    ApplicTokens::Not(ApplicabilityNotTag("JHU_CONTROLLER".to_string().into())),
                    ApplicTokens::NestedNotOr(ApplicabilityNestedNotOrTag(vec![
                        ApplicTokens::NoTag(ApplicabilityNoTag(
                            "ENGINE_5=A2543".to_string().into()
                        )),
                        ApplicTokens::NotOr(ApplicabilityNotOrTag(
                            "ROBOT_SPEAKER=SPKR_B".to_string().into()
                        ))
                    ]))
                ]
            ))
        )
    }
}
pub fn applicability_tag_inner<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicTokens>> {
    move |input: &'a str| {
        let attempt_starting_text = map(parse_until_new_word, |parsed_characters| {
            ApplicTokens::NoTag(ApplicabilityNoTag(parsed_characters.into()))
        });
        let parse_not_starting = map(
            preceded(tag("!"), parse_until_new_word),
            |parsed_characters| ApplicTokens::Not(ApplicabilityNotTag(parsed_characters.into())),
        );
        let starting_parse = alt((parse_not_starting, attempt_starting_text));
        let parse_and_or = alt((parse_and_tokens(), parse_or_tokens()));
        let parse_tags = map(
            tuple((starting_parse, parse_and_or)),
            |(first, mut rest): (ApplicTokens, Vec<ApplicTokens>)| {
                rest.insert(0, first);
                rest
            },
        );
        let mut parser = terminated(parse_tags, multispace0);
        parser(input)
    }
}

#[cfg(test)]
mod parse_applic_tags_tests {
    use applicability_parser_types::applic_tokens::{ApplicTokens, ApplicabilityNoTag};
    use nom::bytes::complete::tag;

    use super::applicability_tag;

    #[test]
    fn simple_tag() {
        let mut parser = applicability_tag(tag(""), tag(""));
        assert_eq!(
            parser("JHU_CONTROLLER]"),
            Ok((
                "",
                vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                    "JHU_CONTROLLER".to_string().into()
                ))]
            ))
        )
    }
}
pub fn applicability_tag<'a>(
    starting_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
    ending_parser: impl FnMut(&'a str) -> IResult<&str, &str>,
) -> impl FnMut(&'a str) -> IResult<&'a str, Vec<ApplicTokens>> {
    // theory of execution:
    // 1. parse ! or full text to start off until a space occurs
    // 2. parse &, | to decide the route the rest of the sequence should go, parse ] if neither is there, complete
    // 3. if & is parsed, all future tokens parsed should be AND or AND NOT
    // 4. if | is parsed, all future tokens parsed should be OR or OR NOT
    // 3a. continue to parse tokens as & until ( (special case) or ](end case)
    // 4a. continue to parse tokens as | until ( (special case) or ](end case)

    //1
    let attempt_starting_text = map(parse_until_new_word, |parsed_characters| {
        ApplicTokens::NoTag(ApplicabilityNoTag(parsed_characters.into()))
    });
    //1
    let parse_not_starting = map(
        preceded(tag("!"), parse_until_new_word),
        |parsed_characters| ApplicTokens::Not(ApplicabilityNotTag(parsed_characters.into())),
    );

    let starting_parse = alt((parse_not_starting, attempt_starting_text));

    let empty_return = map(peek(alt((eof, tag("]")))), |_| vec![]);
    let parse_and_or = alt((parse_and_tokens(), parse_or_tokens(), empty_return));
    let parse_tags = map(
        tuple((starting_parse, parse_and_or)),
        |(first, mut rest): (ApplicTokens, Vec<ApplicTokens>)| {
            rest.insert(0, first);
            rest
        },
    );
    let parse_start = preceded(starting_parser, parse_tags);
    terminated(parse_start, permutation((multispace0, opt(ending_parser))))
}

fn valid_feature_tag<T, E: ParseError<T>>(input: T) -> IResult<T, T, E>
where
    T: InputTakeAtPosition,
    <T as InputTakeAtPosition>::Item: AsChar,
{
    input.split_at_position1_complete(
        |item| {
            let character = item.as_char();
            character.is_uppercase()
                || character.is_lowercase()
                || character == '_'
                || character.is_numeric()
                || character == '='
                || character == ' '
        },
        ErrorKind::Fix,
    )
}
///
/// Parse the text until you hit the end character ] or until you hit the next set of tokens(multispace, "(")
fn parse_until_new_word(input: &str) -> IResult<&str, String> {
    terminated(
        map(
            many_till(
                anychar,
                peek(alt((
                    tag("]"),
                    tag("("),
                    tag(")"),
                    multispace1,
                    eof,
                    valid_feature_tag,
                ))),
            ),
            |(parsed_characters, _): (Vec<char>, &str)| {
                parsed_characters.into_iter().collect::<String>()
            },
        ),
        multispace0,
    )(input)
}
#[cfg(test)]
mod parse_applic_until_new_word_tests {

    use nom::combinator::consumed;

    use super::parse_until_new_word;

    #[test]
    fn with_space() {
        assert_eq!(
            parse_until_new_word("JHU_CONTROLLER "),
            Ok(("", "JHU_CONTROLLER".to_string()))
        )
    }

    #[test]
    fn with_square() {
        assert_eq!(
            parse_until_new_word("JHU_CONTROLLER]"),
            Ok(("]", "JHU_CONTROLLER".to_string()))
        )
    }

    #[test]
    fn with_new_tag() {
        assert_eq!(
            parse_until_new_word("JHU_CONTROLLER("),
            Ok(("(", "JHU_CONTROLLER".to_string()))
        )
    }
    #[test]
    fn with_eof() {
        assert_eq!(
            parse_until_new_word("JHU_CONTROLLER"),
            Ok(("", "JHU_CONTROLLER".to_string()))
        )
    }

    #[test]
    fn with_closing_paren() {
        assert_eq!(
            consumed(parse_until_new_word)("JHU_CONTROLLER)"),
            Ok((")", ("JHU_CONTROLLER", "JHU_CONTROLLER".to_string())))
        )
    }
}
