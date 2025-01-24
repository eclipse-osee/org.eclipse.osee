use std::{marker::PhantomData, ops::RangeFrom};

use config_def::{
    lex_config_case_def, lex_config_def, lex_config_else_def, lex_config_not_def,
    lex_config_switch_def, lex_end_config_def,
};
use config_group_def::{
    lex_config_group_case_def, lex_config_group_def, lex_config_group_else_def,
    lex_config_group_not_def, lex_config_group_switch_def, lex_end_config_group_def,
};
use feature_def::{
    lex_end_feature_def, lex_feature_case_def, lex_feature_def, lex_feature_else_def,
    lex_feature_not_def, lex_feature_switch_def,
};
use nom::{
    branch::alt,
    bytes::complete::tag,
    character::complete::anychar,
    combinator::{eof, map, value},
    error::ParseError,
    multi::many_till,
    sequence::tuple,
    AsChar, IResult, InputIter, InputLength, Parser, Slice,
};
use utility_def::{
    lex_and_def, lex_carriage_return_def, lex_end_comment_multi_line, lex_end_comment_single_line,
    lex_end_paren_def, lex_eof, lex_multi_line_comment_character, lex_not_def, lex_or_def,
    lex_space_def, lex_start_comment_multi_line, lex_start_comment_single_line,
    lex_start_paren_def, lex_start_single_line_comment, lex_unix_new_line_def,
};

mod config_def;
mod config_group_def;
mod feature_def;
mod utility_def;

/**
*
* the alts in the comment need to be many0'd or take_until'd
* StartCommentSingleLine should take until EndCommentSingleLine
* StartCommentMultiLine should take until EndCommentMultiLine
* SingleLineCommentCharacter should take until UnixNewLine
* Theoretical prototype:

    StartParenParse = many_till(Space,StartParen)
    FeatureParse = tuple((Feature, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    FeatureNotParse = tuple((FeatureNot, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    FeatureCaseParse = tuple((FeatureCase, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationParse = tuple((Configuration, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationNotParse = tuple((ConfigurationNot, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationCaseParse = tuple((ConfigurationCase, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationGroupParse = tuple((ConfigurationGroup, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationGroupNotParse = tuple((ConfigurationGroupNot, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    ConfigurationGroupCaseParse = tuple((ConfigurationGroupCase, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
    SubstitutionParse = tuple((Substitution, StartParenParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndParen)))
* results = alt((
* tuple((StartCommentSingleLine,
many_till(
* alt((
   FeatureParse,
   FeatureNotParse,
   FeatureSwitch,
   FeatureCaseParse,
   FeatureElse,
   EndFeature,
   ConfigurationParse,
   ConfigurationNotParse,
   ConfigurationSwitch,
   ConfigurationCaseParse,
   ConfigurationElse,
   EndConfiguration,
   ConfigurationGroupParse,
   ConfigurationGroupNotParse,
   ConfigurationGroupSwitch,
   ConfigurationGroupCaseParse,
   ConfigurationGroupElse,
   EndConfigurationGroup,
   SubstitutionParse,
   Space,
   UnixNewLine,
* ))
, EndCommentSingleLine)
)),
* tuple((
* StartCommentMultiLine,
many_till(
* alt((
* MultilineCommentCharacter,
   FeatureParse,
   FeatureNotParse,
   FeatureSwitch,
   FeatureCaseParse,
   FeatureElse,
   EndFeature,
   ConfigurationParse,
   ConfigurationNotParse,
   ConfigurationSwitch,
   ConfigurationCaseParse,
   ConfigurationElse,
   EndConfiguration,
   ConfigurationGroupParse,
   ConfigurationGroupNotParse,
   ConfigurationGroupSwitch,
   ConfigurationGroupCaseParse,
   ConfigurationGroupElse,
   EndConfigurationGroup,
   SubstitutionParse,
   Space,
   UnixNewLine,
* )), EndCommentMultiLine)
)),
* tuple((
* SingleLineCommentCharacter,
many_till(
* alt((
   FeatureParse,
   FeatureNotParse,
   FeatureSwitch,
   FeatureCaseParse,
   FeatureElse,
   EndFeature,
   ConfigurationParse,
   ConfigurationNotParse,
   ConfigurationSwitch,
   ConfigurationCaseParse,
   ConfigurationElse,
   EndConfiguration,
   ConfigurationGroupParse,
   ConfigurationGroupNotParse,
   ConfigurationGroupSwitch,
   ConfigurationGroupCaseParse,
   ConfigurationGroupElse,
   EndConfigurationGroup,
   SubstitutionParse,
   Space,
   * ))
   , UnixNewLine)
* ))
* Nothing,
* Illegal,
* Identity,
* Eof,
* ))
*/

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum LexerToken {
    #[default]
    Nothing,
    Illegal,
    Identity,
    Text(String),
    Eof,
    StartCommentSingleLine,
    StartCommentMultiLine,
    SingleLineCommentCharacter,
    // the following should only be tokenized inside a comment(i.e. not normal Text(String))
    EndCommentSingleLine,
    EndCommentMultiLine,
    MultilineCommentCharacter,
    Feature,
    FeatureNot,
    FeatureSwitch,
    FeatureCase,
    FeatureElse,
    EndFeature,
    Configuration,
    ConfigurationNot,
    ConfigurationSwitch,
    ConfigurationCase,
    ConfigurationElse,
    EndConfiguration,
    ConfigurationGroup,
    ConfigurationGroupNot,
    ConfigurationGroupSwitch,
    ConfigurationGroupCase,
    ConfigurationGroupElse,
    EndConfigurationGroup,
    Substitution,
    Space,
    CarriageReturn,
    UnixNewLine,
    //the following should only be tokenized following one of the Feature|Configuration|ConfigurationGroup Base|Not|Switch|Case
    StartParen,
    EndParen,
    // the following should only be tokenized following a StartParen and preceding an EndParen
    Not,
    And,
    Or,
    Tag(String),
}
pub struct LexerConfig<
    I,
    O1,
    O2,
    O3,
    O4,
    O5,
    O6,
    O7,
    O8,
    O9,
    O10,
    O11,
    O12,
    O13,
    O14,
    O15,
    O16,
    O17,
    O18,
    O19,
    O20,
    O21,
    O22,
    O23,
    O24,
    O25,
    O26,
    O27,
    O28,
    O29,
    O30,
    O31,
    O32,
    O33,
    E,
    P1,
    P2,
    P3,
    P4,
    P5,
    P6,
    P7,
    P8,
    P9,
    P10,
    P11,
    P12,
    P13,
    P14,
    P15,
    P16,
    P17,
    P18,
    P19,
    P20,
    P21,
    P22,
    P23,
    P24,
    P25,
    P26,
    P27,
    P28,
    P29,
    P30,
    P31,
    P32,
    P33,
> where
    I: Clone + InputLength + InputIter + AsChar + Slice<RangeFrom<usize>>,
    <I as InputIter>::Item: AsChar,
    E: ParseError<I>,
    P1: Parser<I, O1, E> + Copy,
    P2: Parser<I, O2, E> + Copy,
    P3: Parser<I, O3, E> + Copy,
    P4: Parser<I, O4, E> + Copy,
    P5: Parser<I, O5, E> + Copy,
    P6: Parser<I, O6, E> + Copy,
    P7: Parser<I, O7, E> + Copy,
    P8: Parser<I, O8, E> + Copy,
    P9: Parser<I, O9, E> + Copy,
    P10: Parser<I, O10, E> + Copy,
    P11: Parser<I, O11, E> + Copy,
    P12: Parser<I, O12, E> + Copy,
    P13: Parser<I, O13, E> + Copy,
    P14: Parser<I, O14, E> + Copy,
    P15: Parser<I, O15, E> + Copy,
    P16: Parser<I, O16, E> + Copy,
    P17: Parser<I, O17, E> + Copy,
    P18: Parser<I, O18, E> + Copy,
    P19: Parser<I, O19, E> + Copy,
    P20: Parser<I, O20, E> + Copy,
    P21: Parser<I, O21, E> + Copy,
    P22: Parser<I, O22, E> + Copy,
    P23: Parser<I, O23, E> + Copy,
    P24: Parser<I, O24, E> + Copy,
    P25: Parser<I, O25, E> + Copy,
    P26: Parser<I, O26, E> + Copy,
    P27: Parser<I, O27, E> + Copy,
    P28: Parser<I, O28, E> + Copy,
    P29: Parser<I, O29, E> + Copy,
    P30: Parser<I, O30, E> + Copy,
    P31: Parser<I, O31, E> + Copy,
    P32: Parser<I, O32, E> + Copy,
    P33: Parser<I, O33, E> + Copy,
{
    feature: ApplicabilityLexerConfig<I, O1, O2, O3, O4, O5, O6, E, P1, P2, P3, P4, P5, P6>,
    configuration:
        ApplicabilityLexerConfig<I, O7, O8, O9, O10, O11, O12, E, P7, P8, P9, P10, P11, P12>,
    configuration_group:
        ApplicabilityLexerConfig<I, O13, O14, O15, O16, O17, O18, E, P13, P14, P15, P16, P17, P18>,
    phantom_o1: PhantomData<O19>,
    phantom_o2: PhantomData<O20>,
    phantom_o3: PhantomData<O21>,
    phantom_o4: PhantomData<O22>,
    phantom_o5: PhantomData<O23>,
    phantom_o6: PhantomData<O24>,
    phantom_o7: PhantomData<O25>,
    phantom_o8: PhantomData<O26>,
    phantom_o9: PhantomData<O27>,
    phantom_o10: PhantomData<O28>,
    phantom_o11: PhantomData<O29>,
    phantom_o12: PhantomData<O30>,
    phantom_o13: PhantomData<O31>,
    phantom_o14: PhantomData<O32>,
    phantom_o15: PhantomData<O33>,
    space: P19,
    unix_new_line: P20,
    carriage_new_line: P21,
    start_paren: P22,
    end_paren: P23,
    not: P24,
    and: P25,
    or: P26,
    start_comment_single_line: P27,
    end_comment_single_line: P28,
    start_comment_multi_line: P29,
    end_comment_multi_line: P30,
    multi_line_comment_character: P31,
    single_line_comment: P32,
    eof: P33,
}

pub struct ApplicabilityLexerConfig<I, O1, O2, O3, O4, O5, O6, E, P1, P2, P3, P4, P5, P6>
where
    I: Clone + InputLength + InputIter + AsChar + Slice<RangeFrom<usize>>,
    <I as InputIter>::Item: AsChar,
    P1: Parser<I, O1, E>,
    P2: Parser<I, O2, E>,
    P3: Parser<I, O3, E>,
    P4: Parser<I, O4, E>,
    P5: Parser<I, O5, E>,
    P6: Parser<I, O6, E>,
{
    phantom_i: PhantomData<I>,
    phantom_o1: PhantomData<O1>,
    phantom_o2: PhantomData<O2>,
    phantom_o3: PhantomData<O3>,
    phantom_o4: PhantomData<O4>,
    phantom_o5: PhantomData<O5>,
    phantom_o6: PhantomData<O6>,
    phantom_e: PhantomData<E>,
    base: P1,
    not: P2,
    switch: P3,
    case: P4,
    applic_else: P5,
    end: P6,
}

pub fn lex_applicability<
    I,
    O1,
    O2,
    O3,
    O4,
    O5,
    O6,
    O7,
    O8,
    O9,
    O10,
    O11,
    O12,
    O13,
    O14,
    O15,
    O16,
    O17,
    O18,
    O19,
    O20,
    O21,
    O22,
    O23,
    O24,
    O25,
    O26,
    O27,
    O28,
    O29,
    O30,
    O31,
    O32,
    O33,
    E,
    P1,
    P2,
    P3,
    P4,
    P5,
    P6,
    P7,
    P8,
    P9,
    P10,
    P11,
    P12,
    P13,
    P14,
    P15,
    P16,
    P17,
    P18,
    P19,
    P20,
    P21,
    P22,
    P23,
    P24,
    P25,
    P26,
    P27,
    P28,
    P29,
    P30,
    P31,
    P32,
    P33,
>(
    config: LexerConfig<
        I,
        O1,
        O2,
        O3,
        O4,
        O5,
        O6,
        O7,
        O8,
        O9,
        O10,
        O11,
        O12,
        O13,
        O14,
        O15,
        O16,
        O17,
        O18,
        O19,
        O20,
        O21,
        O22,
        O23,
        O24,
        O25,
        O26,
        O27,
        O28,
        O29,
        O30,
        O31,
        O32,
        O33,
        E,
        P1,
        P2,
        P3,
        P4,
        P5,
        P6,
        P7,
        P8,
        P9,
        P10,
        P11,
        P12,
        P13,
        P14,
        P15,
        P16,
        P17,
        P18,
        P19,
        P20,
        P21,
        P22,
        P23,
        P24,
        P25,
        P26,
        P27,
        P28,
        P29,
        P30,
        P31,
        P32,
        P33,
    >,
) where
    I: Clone + InputLength + InputIter + AsChar + Slice<RangeFrom<usize>>,
    <I as InputIter>::Item: AsChar,
    E: ParseError<I>,
    P1: Parser<I, O1, E> + Copy,
    P2: Parser<I, O2, E> + Copy,
    P3: Parser<I, O3, E> + Copy,
    P4: Parser<I, O4, E> + Copy,
    P5: Parser<I, O5, E> + Copy,
    P6: Parser<I, O6, E> + Copy,
    P7: Parser<I, O7, E> + Copy,
    P8: Parser<I, O8, E> + Copy,
    P9: Parser<I, O9, E> + Copy,
    P10: Parser<I, O10, E> + Copy,
    P11: Parser<I, O11, E> + Copy,
    P12: Parser<I, O12, E> + Copy,
    P13: Parser<I, O13, E> + Copy,
    P14: Parser<I, O14, E> + Copy,
    P15: Parser<I, O15, E> + Copy,
    P16: Parser<I, O16, E> + Copy,
    P17: Parser<I, O17, E> + Copy,
    P18: Parser<I, O18, E> + Copy,
    P19: Parser<I, O19, E> + Copy,
    P20: Parser<I, O20, E> + Copy,
    P21: Parser<I, O21, E> + Copy,
    P22: Parser<I, O22, E> + Copy,
    P23: Parser<I, O23, E> + Copy,
    P24: Parser<I, O24, E> + Copy,
    P25: Parser<I, O25, E> + Copy,
    P26: Parser<I, O26, E> + Copy,
    P27: Parser<I, O27, E> + Copy,
    P28: Parser<I, O28, E> + Copy,
    P29: Parser<I, O29, E> + Copy,
    P30: Parser<I, O30, E> + Copy,
    P31: Parser<I, O31, E> + Copy,
    P32: Parser<I, O32, E> + Copy,
    P33: Parser<I, O33, E> + Copy,
{
    let start_paren_parse = || {
        many_till(
            lex_space_def(config.space),
            lex_start_paren_def(config.start_paren),
        )
    };
    let feature_parse = || {
        map(
            tuple((
                lex_feature_def(config.feature.base),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(feature, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, feature);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };
    let feature_not_parse = || {
        map(
            tuple((
                lex_feature_not_def(config.feature.not),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(feature, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, feature);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };
    let feature_case_parse = || {
        map(
            tuple((
                lex_feature_case_def(config.feature.case),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(feature, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, feature);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };
    let config_parse = || {
        map(
            tuple((
                lex_config_def(config.configuration.base),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(config, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, config);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };
    let config_not_parse = || {
        map(
            tuple((
                lex_config_not_def(config.configuration.not),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(config, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, config);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };
    let config_case_parse = || {
        map(
            tuple((
                lex_config_case_def(config.configuration.case),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(config, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, config);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };

    let config_group_parse = || {
        map(
            tuple((
                lex_config_group_def(config.configuration_group.base),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(group, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, group);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };
    let config_group_not_parse = || {
        map(
            tuple((
                lex_config_group_not_def(config.configuration_group.not),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(group, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, group);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };
    let config_group_case_parse = || {
        map(
            tuple((
                lex_config_group_case_def(config.configuration_group.case),
                start_paren_parse(),
                many_till(
                    alt((
                        lex_space_def(config.space),
                        lex_carriage_return_def(config.carriage_new_line),
                        lex_unix_new_line_def(config.unix_new_line),
                        lex_not_def(config.not),
                        lex_and_def(config.and),
                        lex_or_def(config.or), //insert tag parse here
                    )),
                    lex_end_paren_def(config.end_paren),
                ),
            )),
            |(group, (mut spaces, start), (mut inner, end))| {
                spaces.insert(0, group);
                spaces.push(start);
                inner.push(end);
                spaces.append(&mut inner);
                spaces
            },
        )
    };
    let start_end_single_line_comment_parser = || {
        map(
            tuple((
                lex_start_comment_single_line(config.start_comment_single_line),
                map(
                    many_till(
                        alt((
                            map(lex_space_def(config.space), |x| vec![x]),
                            map(lex_unix_new_line_def(config.unix_new_line), |x| vec![x]),
                            feature_not_parse(),
                            map(lex_feature_switch_def(config.feature.switch), |x| vec![x]),
                            feature_case_parse(),
                            map(lex_feature_else_def(config.feature.applic_else), |x| {
                                vec![x]
                            }),
                            feature_parse(),
                            map(lex_end_feature_def(config.feature.end), |x| vec![x]),
                            config_not_parse(),
                            map(lex_config_switch_def(config.configuration.switch), |x| {
                                vec![x]
                            }),
                            config_case_parse(),
                            map(lex_config_else_def(config.configuration.applic_else), |x| {
                                vec![x]
                            }),
                            config_parse(),
                            map(lex_end_config_def(config.configuration.end), |x| vec![x]),
                            config_group_not_parse(),
                            map(
                                lex_config_group_switch_def(config.configuration_group.switch),
                                |x| vec![x],
                            ),
                            config_group_case_parse(),
                            map(
                                lex_config_group_else_def(config.configuration_group.applic_else),
                                |x| vec![x],
                            ),
                            config_group_parse(),
                            map(
                                lex_end_config_group_def(config.configuration_group.end),
                                |x| vec![x],
                            ),
                        )),
                        lex_end_comment_single_line(config.end_comment_single_line),
                    ),
                    |(list, end)| {
                        let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
                        flattened.push(end);
                        flattened
                    },
                ),
            )),
            |(start, mut list)| {
                list.insert(0, start);
                list
            },
        )
    };

    let multi_line_comment_parser = || {
        map(
            tuple((
                lex_start_comment_multi_line(config.start_comment_multi_line),
                map(
                    many_till(
                        alt((
                            map(lex_space_def(config.space), |x| vec![x]),
                            map(lex_unix_new_line_def(config.unix_new_line), |x| vec![x]),
                            map(
                                lex_multi_line_comment_character(
                                    config.multi_line_comment_character,
                                ),
                                |x| vec![x],
                            ),
                            feature_not_parse(),
                            map(lex_feature_switch_def(config.feature.switch), |x| vec![x]),
                            feature_case_parse(),
                            map(lex_feature_else_def(config.feature.applic_else), |x| {
                                vec![x]
                            }),
                            feature_parse(),
                            map(lex_end_feature_def(config.feature.end), |x| vec![x]),
                            config_not_parse(),
                            map(lex_config_switch_def(config.configuration.switch), |x| {
                                vec![x]
                            }),
                            config_case_parse(),
                            map(lex_config_else_def(config.configuration.applic_else), |x| {
                                vec![x]
                            }),
                            config_parse(),
                            map(lex_end_config_def(config.configuration.end), |x| vec![x]),
                            config_group_not_parse(),
                            map(
                                lex_config_group_switch_def(config.configuration_group.switch),
                                |x| vec![x],
                            ),
                            config_group_case_parse(),
                            map(
                                lex_config_group_else_def(config.configuration_group.applic_else),
                                |x| vec![x],
                            ),
                            config_group_parse(),
                            map(
                                lex_end_config_group_def(config.configuration_group.end),
                                |x| vec![x],
                            ),
                        )),
                        lex_end_comment_multi_line(config.end_comment_multi_line),
                    ),
                    |(list, end)| {
                        let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
                        flattened.push(end);
                        flattened
                    },
                ),
            )),
            |(start, mut list)| {
                list.insert(0, start);
                list
            },
        )
    };
    let single_line_comment_parser = || {
        map(
            tuple((
                lex_start_single_line_comment(config.single_line_comment),
                map(
                    many_till(
                        alt((
                            map(lex_space_def(config.space), |x| vec![x]),
                            feature_not_parse(),
                            map(lex_feature_switch_def(config.feature.switch), |x| vec![x]),
                            feature_case_parse(),
                            map(lex_feature_else_def(config.feature.applic_else), |x| {
                                vec![x]
                            }),
                            feature_parse(),
                            map(lex_end_feature_def(config.feature.end), |x| vec![x]),
                            config_not_parse(),
                            map(lex_config_switch_def(config.configuration.switch), |x| {
                                vec![x]
                            }),
                            config_case_parse(),
                            map(lex_config_else_def(config.configuration.applic_else), |x| {
                                vec![x]
                            }),
                            config_parse(),
                            map(lex_end_config_def(config.configuration.end), |x| vec![x]),
                            config_group_not_parse(),
                            map(
                                lex_config_group_switch_def(config.configuration_group.switch),
                                |x| vec![x],
                            ),
                            config_group_case_parse(),
                            map(
                                lex_config_group_else_def(config.configuration_group.applic_else),
                                |x| vec![x],
                            ),
                            config_group_parse(),
                            map(
                                lex_end_config_group_def(config.configuration_group.end),
                                |x| vec![x],
                            ),
                        )),
                        //TODO: convert to make carriage return have to be followed by a new line
                        alt((
                            lex_unix_new_line_def(config.unix_new_line),
                            lex_carriage_return_def(config.carriage_new_line),
                        )),
                    ),
                    |(list, end)| {
                        let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
                        flattened.push(end);
                        flattened
                    },
                ),
            )),
            |(start, mut list)| {
                list.insert(0, start);
                list
            },
        )
    };
    let comments = || {
        alt((
            start_end_single_line_comment_parser(),
            multi_line_comment_parser(),
            single_line_comment_parser(),
        ))
    };
    let text_parser = map(
        many_till(anychar, comments()),
        |(results, mut comment): (Vec<char>, Vec<LexerToken>)| {
            let res = results.iter().clone().collect::<String>();
            comment.insert(0, LexerToken::Text(res));
            comment
        },
    );
    let parser = map(
        many_till(alt((comments(), text_parser)), lex_eof(config.eof)),
        |(res, eof): (Vec<Vec<LexerToken>>, LexerToken)| {
            let mut flattened = res.into_iter().flatten().collect::<Vec<LexerToken>>();
            flattened.push(eof);
            flattened
        },
    );
}
