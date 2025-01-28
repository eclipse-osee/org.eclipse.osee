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
    combinator::{map, peek},
    error::ParseError,
    multi::many_till,
    sequence::tuple,
    AsChar, IResult, InputIter, InputLength, InputTake, Parser, Slice,
};
use utility_def::{
    lex_and_def, lex_carriage_return_def, lex_end_brace_def, lex_end_comment_multi_line,
    lex_end_comment_single_line, lex_end_paren_def, lex_eof, lex_multi_line_comment_character,
    lex_not_def, lex_or_def, lex_space_def, lex_start_brace_def, lex_start_comment_multi_line,
    lex_start_comment_single_line, lex_start_paren_def, lex_start_single_line_comment,
    lex_unix_new_line_def,
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

    StartBraceParse = many_till(Space,StartBrace)
    FeatureParse = tuple((Feature, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    FeatureNotParse = tuple((FeatureNot, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    FeatureCaseParse = tuple((FeatureCase, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    ConfigurationParse = tuple((Configuration, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    ConfigurationNotParse = tuple((ConfigurationNot, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    ConfigurationCaseParse = tuple((ConfigurationCase, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    ConfigurationGroupParse = tuple((ConfigurationGroup, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    ConfigurationGroupNotParse = tuple((ConfigurationGroupNot, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    ConfigurationGroupCaseParse = tuple((ConfigurationGroupCase, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
    SubstitutionParse = tuple((Substitution, StartBraceParse, many_till(alt((Space,UnixNewLine,Not,And,Or,Tag(String))),EndBrace)))
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
    StartBrace,
    EndBrace,
    // the following should only be tokenized following a StartBrace and preceding an EndBrace
    StartParen,
    EndParen,
    Not,
    And,
    Or,
    Tag(String),
}

trait LexerConfigurable {
    type I;
    type E;
    type O1;
    type O2;
    type O3;
    type O4;
    type O5;
    type O6;
    type O7;
    type O8;
    type O9;
    type O10;
    type O11;
    type O12;
    type O13;
    type O14;
    type O15;
    type O16;
    type O17;
    type O18;
    type O19;
    type O20;
    type O21;
    type O22;
    type O23;
    type O24;
    type O25;
    type O26;
    type O27;
    type O28;
    type O29;
    type O30;
    type O31;
    type O32;
    type O33;
    type O34;
    type O35;
}
pub trait LexerConfig<T: LexerConfigurable> {
    fn feature_base(&self) -> impl Parser<T::I, T::O1, T::E>;
    fn feature_not(&self) -> impl Parser<T::I, T::O2, T::E>;
    fn feature_switch(&self) -> impl Parser<T::I, T::O3, T::E>;
    fn feature_case(&self) -> impl Parser<T::I, T::O4, T::E>;
    fn feature_else(&self) -> impl Parser<T::I, T::O5, T::E>;
    fn feature_end(&self) -> impl Parser<T::I, T::O6, T::E>;
    fn config_base(&self) -> impl Parser<T::I, T::O7, T::E>;
    fn config_not(&self) -> impl Parser<T::I, T::O8, T::E>;
    fn config_switch(&self) -> impl Parser<T::I, T::O9, T::E>;
    fn config_case(&self) -> impl Parser<T::I, T::O10, T::E>;
    fn config_else(&self) -> impl Parser<T::I, T::O11, T::E>;
    fn config_end(&self) -> impl Parser<T::I, T::O12, T::E>;
    fn group_base(&self) -> impl Parser<T::I, T::O13, T::E>;
    fn group_not(&self) -> impl Parser<T::I, T::O14, T::E>;
    fn group_switch(&self) -> impl Parser<T::I, T::O15, T::E>;
    fn group_case(&self) -> impl Parser<T::I, T::O16, T::E>;
    fn group_else(&self) -> impl Parser<T::I, T::O17, T::E>;
    fn group_end(&self) -> impl Parser<T::I, T::O18, T::E>;
    fn space(&self) -> impl Parser<T::I, T::O19, T::E>;
    fn unix_new_line(&self) -> impl Parser<T::I, T::O20, T::E>;
    fn carriage_new_line(&self) -> impl Parser<T::I, T::O21, T::E>;
    fn start_brace(&self) -> impl Parser<T::I, T::O22, T::E>;
    fn end_brace(&self) -> impl Parser<T::I, T::O23, T::E>;
    fn not(&self) -> impl Parser<T::I, T::O24, T::E>;
    fn and(&self) -> impl Parser<T::I, T::O25, T::E>;
    fn or(&self) -> impl Parser<T::I, T::O26, T::E>;
    fn start_comment_single_line(&self) -> impl Parser<T::I, T::O27, T::E>;
    fn end_comment_single_line(&self) -> impl Parser<T::I, T::O28, T::E>;
    fn start_comment_multi_line(&self) -> impl Parser<T::I, T::O29, T::E>;
    fn end_comment_multi_line(&self) -> impl Parser<T::I, T::O30, T::E>;
    fn multi_line_comment_character(&self) -> impl Parser<T::I, T::O31, T::E>;
    fn single_line_comment(&self) -> impl Parser<T::I, T::O32, T::E>;
    fn eof(&self) -> impl Parser<T::I, T::O33, T::E>;
    fn start_paren(&self) -> impl Parser<T::I, T::O34, T::E>;
    fn end_paren(&self) -> impl Parser<T::I, T::O35, T::E>;
}

pub struct MarkdownConfig {}

impl MarkdownConfig {
    fn new() -> Self {
        Self {}
    }
}
impl<T: LexerConfigurable> LexerConfig<T> for MarkdownConfig
where
    T::I: Clone
        + InputLength
        + InputIter
        + Slice<RangeFrom<usize>>
        + InputTake
        + nom::UnspecializedInput,
    <T::I as InputIter>::Item: AsChar,
{
    fn feature_base(&self) -> impl Parser<T::I, T::O1, T::E> {
        tag("Feature")
    }

    fn feature_not(&self) -> impl Parser<T::I, T::O2, T::E> {
        todo!()
    }

    fn feature_switch(&self) -> impl Parser<T::I, T::O3, T::E> {
        todo!()
    }

    fn feature_case(&self) -> impl Parser<T::I, T::O4, T::E> {
        todo!()
    }

    fn feature_else(&self) -> impl Parser<T::I, T::O5, T::E> {
        todo!()
    }

    fn feature_end(&self) -> impl Parser<T::I, T::O6, T::E> {
        todo!()
    }

    fn config_base(&self) -> impl Parser<T::I, T::O7, T::E> {
        todo!()
    }

    fn config_not(&self) -> impl Parser<T::I, T::O8, T::E> {
        todo!()
    }

    fn config_switch(&self) -> impl Parser<T::I, T::O9, T::E> {
        todo!()
    }

    fn config_case(&self) -> impl Parser<T::I, T::O10, T::E> {
        todo!()
    }

    fn config_else(&self) -> impl Parser<T::I, T::O11, T::E> {
        todo!()
    }

    fn config_end(&self) -> impl Parser<T::I, T::O12, T::E> {
        todo!()
    }

    fn group_base(&self) -> impl Parser<T::I, T::O13, T::E> {
        todo!()
    }

    fn group_not(&self) -> impl Parser<T::I, T::O14, T::E> {
        todo!()
    }

    fn group_switch(&self) -> impl Parser<T::I, T::O15, T::E> {
        todo!()
    }

    fn group_case(&self) -> impl Parser<T::I, T::O16, T::E> {
        todo!()
    }

    fn group_else(&self) -> impl Parser<T::I, T::O17, T::E> {
        todo!()
    }

    fn group_end(&self) -> impl Parser<T::I, T::O18, T::E> {
        todo!()
    }

    fn space(&self) -> impl Parser<T::I, T::O19, T::E> {
        todo!()
    }

    fn unix_new_line(&self) -> impl Parser<T::I, T::O20, T::E> {
        todo!()
    }

    fn carriage_new_line(&self) -> impl Parser<T::I, T::O21, T::E> {
        todo!()
    }

    fn start_brace(&self) -> impl Parser<T::I, T::O22, T::E> {
        todo!()
    }

    fn end_brace(&self) -> impl Parser<T::I, T::O23, T::E> {
        todo!()
    }

    fn not(&self) -> impl Parser<T::I, T::O24, T::E> {
        todo!()
    }

    fn and(&self) -> impl Parser<T::I, T::O25, T::E> {
        todo!()
    }

    fn or(&self) -> impl Parser<T::I, T::O26, T::E> {
        todo!()
    }

    fn start_comment_single_line(&self) -> impl Parser<T::I, T::O27, T::E> {
        todo!()
    }

    fn end_comment_single_line(&self) -> impl Parser<T::I, T::O28, T::E> {
        todo!()
    }

    fn start_comment_multi_line(&self) -> impl Parser<T::I, T::O29, T::E> {
        todo!()
    }

    fn end_comment_multi_line(&self) -> impl Parser<T::I, T::O30, T::E> {
        todo!()
    }

    fn multi_line_comment_character(&self) -> impl Parser<T::I, T::O31, T::E> {
        todo!()
    }

    fn single_line_comment(&self) -> impl Parser<T::I, T::O32, T::E> {
        todo!()
    }

    fn eof(&self) -> impl Parser<T::I, T::O33, T::E> {
        todo!()
    }

    fn start_paren(&self) -> impl Parser<T::I, T::O34, T::E> {
        todo!()
    }

    fn end_paren(&self) -> impl Parser<T::I, T::O35, T::E> {
        todo!()
    }
}
// pub struct LexerConfig<
//     I,
//     O1,
//     O2,
//     O3,
//     O4,
//     O5,
//     O6,
//     O7,
//     O8,
//     O9,
//     O10,
//     O11,
//     O12,
//     O13,
//     O14,
//     O15,
//     O16,
//     O17,
//     O18,
//     O19,
//     O20,
//     O21,
//     O22,
//     O23,
//     O24,
//     O25,
//     O26,
//     O27,
//     O28,
//     O29,
//     O30,
//     O31,
//     O32,
//     O33,
//     O34,
//     O35,
//     E,
//     P1,
//     P2,
//     P3,
//     P4,
//     P5,
//     P6,
//     P7,
//     P8,
//     P9,
//     P10,
//     P11,
//     P12,
//     P13,
//     P14,
//     P15,
//     P16,
//     P17,
//     P18,
//     P19,
//     P20,
//     P21,
//     P22,
//     P23,
//     P24,
//     P25,
//     P26,
//     P27,
//     P28,
//     P29,
//     P30,
//     P31,
//     P32,
//     P33,
//     P34,
//     P35,
//     U1,
//     U2,
//     U3,
//     U4,
//     U5,
//     U6,
//     U7,
//     U8,
//     U9,
//     U10,
//     U11,
//     U12,
//     U13,
//     U14,
//     U15,
//     U16,
//     U17,
//     U18,
//     U19,
//     U20,
//     U21,
//     U22,
//     U23,
//     U24,
//     U25,
//     U26,
//     U27,
//     U28,
//     U29,
//     U30,
//     U31,
//     U32,
//     U33,
//     U34,
//     U35,
// > where
//     I: Clone + InputLength + InputIter + Slice<RangeFrom<usize>>,
//     <I as InputIter>::Item: AsChar,
//     E: ParseError<I>,
//     U1: Parser<I, O1, E>,
//     U2: Parser<I, O2, E>,
//     U3: Parser<I, O3, E>,
//     U4: Parser<I, O4, E>,
//     U5: Parser<I, O5, E>,
//     U6: Parser<I, O6, E>,
//     U7: Parser<I, O7, E>,
//     U8: Parser<I, O8, E>,
//     U9: Parser<I, O9, E>,
//     U10: Parser<I, O10, E>,
//     U11: Parser<I, O11, E>,
//     U12: Parser<I, O12, E>,
//     U13: Parser<I, O13, E>,
//     U14: Parser<I, O14, E>,
//     U15: Parser<I, O15, E>,
//     U16: Parser<I, O16, E>,
//     U17: Parser<I, O17, E>,
//     U18: Parser<I, O18, E>,
//     U19: Parser<I, O19, E>,
//     U20: Parser<I, O20, E>,
//     U21: Parser<I, O21, E>,
//     U22: Parser<I, O22, E>,
//     U23: Parser<I, O23, E>,
//     U24: Parser<I, O24, E>,
//     U25: Parser<I, O25, E>,
//     U26: Parser<I, O26, E>,
//     U27: Parser<I, O27, E>,
//     U28: Parser<I, O28, E>,
//     U29: Parser<I, O29, E>,
//     U30: Parser<I, O30, E>,
//     U31: Parser<I, O31, E>,
//     U32: Parser<I, O32, E>,
//     U33: Parser<I, O33, E>,
//     U34: Parser<I, O34, E>,
//     U35: Parser<I, O35, E>,
//     P1: Copy + Fn() -> U1,
//     P2: Copy + Fn() -> U2,
//     P3: Copy + Fn() -> U3,
//     P4: Copy + Fn() -> U4,
//     P5: Copy + Fn() -> U5,
//     P6: Copy + Fn() -> U6,
//     P7: Copy + Fn() -> U7,
//     P8: Copy + Fn() -> U8,
//     P9: Copy + Fn() -> U9,
//     P10: Copy + Fn() -> U10,
//     P11: Copy + Fn() -> U11,
//     P12: Copy + Fn() -> U12,
//     P13: Copy + Fn() -> U13,
//     P14: Copy + Fn() -> U14,
//     P15: Copy + Fn() -> U15,
//     P16: Copy + Fn() -> U16,
//     P17: Copy + Fn() -> U17,
//     P18: Copy + Fn() -> U18,
//     P19: Copy + Fn() -> U19,
//     P20: Copy + Fn() -> U20,
//     P21: Copy + Fn() -> U21,
//     P22: Copy + Fn() -> U22,
//     P23: Copy + Fn() -> U23,
//     P24: Copy + Fn() -> U24,
//     P25: Copy + Fn() -> U25,
//     P26: Copy + Fn() -> U26,
//     P27: Copy + Fn() -> U27,
//     P28: Copy + Fn() -> U28,
//     P29: Copy + Fn() -> U29,
//     P30: Copy + Fn() -> U30,
//     P31: Copy + Fn() -> U31,
//     P32: Copy + Fn() -> U32,
//     P33: Copy + Fn() -> U33,
//     P34: Copy + Fn() -> U34,
//     P35: Copy + Fn() -> U35,
// {
//     feature: ApplicabilityLexerConfig<
//         I,
//         O1,
//         O2,
//         O3,
//         O4,
//         O5,
//         O6,
//         E,
//         P1,
//         P2,
//         P3,
//         P4,
//         P5,
//         P6,
//         U1,
//         U2,
//         U3,
//         U4,
//         U5,
//         U6,
//     >,
//     configuration: ApplicabilityLexerConfig<
//         I,
//         O7,
//         O8,
//         O9,
//         O10,
//         O11,
//         O12,
//         E,
//         P7,
//         P8,
//         P9,
//         P10,
//         P11,
//         P12,
//         U7,
//         U8,
//         U9,
//         U10,
//         U11,
//         U12,
//     >,
//     configuration_group: ApplicabilityLexerConfig<
//         I,
//         O13,
//         O14,
//         O15,
//         O16,
//         O17,
//         O18,
//         E,
//         P13,
//         P14,
//         P15,
//         P16,
//         P17,
//         P18,
//         U13,
//         U14,
//         U15,
//         U16,
//         U17,
//         U18,
//     >,
//     _phantom_o1: PhantomData<O19>,
//     _phantom_o2: PhantomData<O20>,
//     _phantom_o3: PhantomData<O21>,
//     _phantom_o4: PhantomData<O22>,
//     _phantom_o5: PhantomData<O23>,
//     _phantom_o6: PhantomData<O24>,
//     _phantom_o7: PhantomData<O25>,
//     _phantom_o8: PhantomData<O26>,
//     _phantom_o9: PhantomData<O27>,
//     _phantom_o10: PhantomData<O28>,
//     _phantom_o11: PhantomData<O29>,
//     _phantom_o12: PhantomData<O30>,
//     _phantom_o13: PhantomData<O31>,
//     _phantom_o14: PhantomData<O32>,
//     _phantom_o15: PhantomData<O33>,
//     _phantom_o16: PhantomData<O34>,
//     _phantom_o17: PhantomData<O35>,
//     space: P19,
//     unix_new_line: P20,
//     carriage_new_line: P21,
//     start_brace: P22,
//     end_brace: P23,
//     not: P24,
//     and: P25,
//     or: P26,
//     start_comment_single_line: P27,
//     end_comment_single_line: P28,
//     start_comment_multi_line: P29,
//     end_comment_multi_line: P30,
//     multi_line_comment_character: P31,
//     single_line_comment: P32,
//     eof: P33,
//     start_paren: P34,
//     end_paren: P35,
// }

// pub struct ApplicabilityLexerConfig<
//     I,
//     O1,
//     O2,
//     O3,
//     O4,
//     O5,
//     O6,
//     E,
//     P1,
//     P2,
//     P3,
//     P4,
//     P5,
//     P6,
//     U1,
//     U2,
//     U3,
//     U4,
//     U5,
//     U6,
// > where
//     I: Clone + InputLength + InputIter + Slice<RangeFrom<usize>>,
//     <I as InputIter>::Item: AsChar,
//     U1: Parser<I, O1, E>,
//     U2: Parser<I, O2, E>,
//     U3: Parser<I, O3, E>,
//     U4: Parser<I, O4, E>,
//     U5: Parser<I, O5, E>,
//     U6: Parser<I, O6, E>,
//     P1: Copy + Fn() -> U1,
//     P2: Copy + Fn() -> U2,
//     P3: Copy + Fn() -> U3,
//     P4: Copy + Fn() -> U4,
//     P5: Copy + Fn() -> U5,
//     P6: Copy + Fn() -> U6,
// {
//     _phantom_i: PhantomData<I>,
//     _phantom_o1: PhantomData<O1>,
//     _phantom_o2: PhantomData<O2>,
//     _phantom_o3: PhantomData<O3>,
//     _phantom_o4: PhantomData<O4>,
//     _phantom_o5: PhantomData<O5>,
//     _phantom_o6: PhantomData<O6>,
//     _phantom_e: PhantomData<E>,
//     base: P1,
//     not: P2,
//     switch: P3,
//     case: P4,
//     applic_else: P5,
//     end: P6,
// }

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
    O34,
    O35,
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
    P34,
    P35,
    U1,
    U2,
    U3,
    U4,
    U5,
    U6,
    U7,
    U8,
    U9,
    U10,
    U11,
    U12,
    U13,
    U14,
    U15,
    U16,
    U17,
    U18,
    U19,
    U20,
    U21,
    U22,
    U23,
    U24,
    U25,
    U26,
    U27,
    U28,
    U29,
    U30,
    U31,
    U32,
    U33,
    U34,
    U35,
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
        O34,
        O35,
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
        P34,
        P35,
        U1,
        U2,
        U3,
        U4,
        U5,
        U6,
        U7,
        U8,
        U9,
        U10,
        U11,
        U12,
        U13,
        U14,
        U15,
        U16,
        U17,
        U18,
        U19,
        U20,
        U21,
        U22,
        U23,
        U24,
        U25,
        U26,
        U27,
        U28,
        U29,
        U30,
        U31,
        U32,
        U33,
        U34,
        U35,
    >,
) -> impl FnMut(I) -> Result<(I, Vec<LexerToken>), nom::Err<E>>
where
    I: Clone + InputLength + InputIter + Slice<RangeFrom<usize>>,
    <I as InputIter>::Item: AsChar,
    E: ParseError<I>,
    U1: Parser<I, O1, E>,
    U2: Parser<I, O2, E>,
    U3: Parser<I, O3, E>,
    U4: Parser<I, O4, E>,
    U5: Parser<I, O5, E>,
    U6: Parser<I, O6, E>,
    U7: Parser<I, O7, E>,
    U8: Parser<I, O8, E>,
    U9: Parser<I, O9, E>,
    U10: Parser<I, O10, E>,
    U11: Parser<I, O11, E>,
    U12: Parser<I, O12, E>,
    U13: Parser<I, O13, E>,
    U14: Parser<I, O14, E>,
    U15: Parser<I, O15, E>,
    U16: Parser<I, O16, E>,
    U17: Parser<I, O17, E>,
    U18: Parser<I, O18, E>,
    U19: Parser<I, O19, E>,
    U20: Parser<I, O20, E>,
    U21: Parser<I, O21, E>,
    U22: Parser<I, O22, E>,
    U23: Parser<I, O23, E>,
    U24: Parser<I, O24, E>,
    U25: Parser<I, O25, E>,
    U26: Parser<I, O26, E>,
    U27: Parser<I, O27, E>,
    U28: Parser<I, O28, E>,
    U29: Parser<I, O29, E>,
    U30: Parser<I, O30, E>,
    U31: Parser<I, O31, E>,
    U32: Parser<I, O32, E>,
    U33: Parser<I, O33, E>,
    U34: Parser<I, O34, E>,
    U35: Parser<I, O35, E>,
    P1: Copy + Fn() -> U1,
    P2: Copy + Fn() -> U2,
    P3: Copy + Fn() -> U3,
    P4: Copy + Fn() -> U4,
    P5: Copy + Fn() -> U5,
    P6: Copy + Fn() -> U6,
    P7: Copy + Fn() -> U7,
    P8: Copy + Fn() -> U8,
    P9: Copy + Fn() -> U9,
    P10: Copy + Fn() -> U10,
    P11: Copy + Fn() -> U11,
    P12: Copy + Fn() -> U12,
    P13: Copy + Fn() -> U13,
    P14: Copy + Fn() -> U14,
    P15: Copy + Fn() -> U15,
    P16: Copy + Fn() -> U16,
    P17: Copy + Fn() -> U17,
    P18: Copy + Fn() -> U18,
    P19: Copy + Fn() -> U19,
    P20: Copy + Fn() -> U20,
    P21: Copy + Fn() -> U21,
    P22: Copy + Fn() -> U22,
    P23: Copy + Fn() -> U23,
    P24: Copy + Fn() -> U24,
    P25: Copy + Fn() -> U25,
    P26: Copy + Fn() -> U26,
    P27: Copy + Fn() -> U27,
    P28: Copy + Fn() -> U28,
    P29: Copy + Fn() -> U29,
    P30: Copy + Fn() -> U30,
    P31: Copy + Fn() -> U31,
    P32: Copy + Fn() -> U32,
    P33: Copy + Fn() -> U33,
    P34: Copy + Fn() -> U34,
    P35: Copy + Fn() -> U35,
{
    let start_brace_parse = || {
        many_till(
            lex_space_def((config.space)()),
            lex_start_brace_def((config.start_brace)()),
        )
    };
    let base_tag_parse = || {
        map(
            many_till(
                anychar,
                peek(alt((
                    lex_space_def((config.space)()),
                    lex_carriage_return_def((config.carriage_new_line)()),
                    lex_unix_new_line_def((config.unix_new_line)()),
                    lex_start_paren_def((config.start_paren)()),
                    lex_end_paren_def((config.end_paren)()),
                    lex_not_def((config.not)()),
                    lex_and_def((config.and)()),
                    lex_or_def((config.or)()),
                    lex_end_brace_def((config.end_brace)()),
                ))),
            ),
            |(results, _)| {
                let res = results.into_iter().clone().collect::<String>();
                LexerToken::Tag(res)
            },
        )
    };
    let feature_parse = || {
        map(
            tuple((
                lex_feature_def((config.feature.base)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_feature_not_def((config.feature.not)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_feature_case_def((config.feature.case)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_config_def((config.configuration.base)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_config_not_def((config.configuration.not)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_config_case_def((config.configuration.case)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_config_group_def((config.configuration_group.base)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_config_group_not_def((config.configuration_group.not)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_config_group_case_def((config.configuration_group.case)()),
                start_brace_parse(),
                many_till(
                    alt((
                        lex_space_def((config.space)()),
                        lex_carriage_return_def((config.carriage_new_line)()),
                        lex_unix_new_line_def((config.unix_new_line)()),
                        lex_start_paren_def((config.start_paren)()),
                        lex_end_paren_def((config.end_paren)()),
                        lex_not_def((config.not)()),
                        lex_and_def((config.and)()),
                        lex_or_def((config.or)()),
                        base_tag_parse(),
                    )),
                    lex_end_brace_def((config.end_brace)()),
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
                lex_start_comment_single_line((config.start_comment_single_line)()),
                map(
                    many_till(
                        alt((
                            map(lex_space_def((config.space)()), |x| vec![x]),
                            map(lex_unix_new_line_def((config.unix_new_line)()), |x| vec![x]),
                            feature_not_parse(),
                            map(lex_feature_switch_def((config.feature.switch)()), |x| {
                                vec![x]
                            }),
                            feature_case_parse(),
                            map(lex_feature_else_def((config.feature.applic_else)()), |x| {
                                vec![x]
                            }),
                            feature_parse(),
                            map(lex_end_feature_def((config.feature.end)()), |x| vec![x]),
                            config_not_parse(),
                            map(
                                lex_config_switch_def((config.configuration.switch)()),
                                |x| vec![x],
                            ),
                            config_case_parse(),
                            map(
                                lex_config_else_def((config.configuration.applic_else)()),
                                |x| vec![x],
                            ),
                            config_parse(),
                            map(lex_end_config_def((config.configuration.end)()), |x| {
                                vec![x]
                            }),
                            config_group_not_parse(),
                            map(
                                lex_config_group_switch_def((config.configuration_group.switch)()),
                                |x| vec![x],
                            ),
                            config_group_case_parse(),
                            map(
                                lex_config_group_else_def(
                                    (config.configuration_group.applic_else)(),
                                ),
                                |x| vec![x],
                            ),
                            config_group_parse(),
                            map(
                                lex_end_config_group_def((config.configuration_group.end)()),
                                |x| vec![x],
                            ),
                        )),
                        lex_end_comment_single_line((config.end_comment_single_line)()),
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
                lex_start_comment_multi_line((config.start_comment_multi_line)()),
                map(
                    many_till(
                        alt((
                            map(lex_space_def((config.space)()), |x| vec![x]),
                            map(lex_unix_new_line_def((config.unix_new_line)()), |x| vec![x]),
                            map(
                                lex_multi_line_comment_character((config
                                    .multi_line_comment_character)(
                                )),
                                |x| vec![x],
                            ),
                            feature_not_parse(),
                            map(lex_feature_switch_def((config.feature.switch)()), |x| {
                                vec![x]
                            }),
                            feature_case_parse(),
                            map(lex_feature_else_def((config.feature.applic_else)()), |x| {
                                vec![x]
                            }),
                            feature_parse(),
                            map(lex_end_feature_def((config.feature.end)()), |x| vec![x]),
                            config_not_parse(),
                            map(
                                lex_config_switch_def((config.configuration.switch)()),
                                |x| vec![x],
                            ),
                            config_case_parse(),
                            map(
                                lex_config_else_def((config.configuration.applic_else)()),
                                |x| vec![x],
                            ),
                            config_parse(),
                            map(lex_end_config_def((config.configuration.end)()), |x| {
                                vec![x]
                            }),
                            config_group_not_parse(),
                            map(
                                lex_config_group_switch_def((config.configuration_group.switch)()),
                                |x| vec![x],
                            ),
                            config_group_case_parse(),
                            map(
                                lex_config_group_else_def(
                                    (config.configuration_group.applic_else)(),
                                ),
                                |x| vec![x],
                            ),
                            config_group_parse(),
                            map(
                                lex_end_config_group_def((config.configuration_group.end)()),
                                |x| vec![x],
                            ),
                        )),
                        lex_end_comment_multi_line((config.end_comment_multi_line)()),
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
                lex_start_single_line_comment((config.single_line_comment)()),
                map(
                    many_till(
                        alt((
                            map(lex_space_def((config.space)()), |x| vec![x]),
                            feature_not_parse(),
                            map(lex_feature_switch_def((config.feature.switch)()), |x| {
                                vec![x]
                            }),
                            feature_case_parse(),
                            map(lex_feature_else_def((config.feature.applic_else)()), |x| {
                                vec![x]
                            }),
                            feature_parse(),
                            map(lex_end_feature_def((config.feature.end)()), |x| vec![x]),
                            config_not_parse(),
                            map(
                                lex_config_switch_def((config.configuration.switch)()),
                                |x| vec![x],
                            ),
                            config_case_parse(),
                            map(
                                lex_config_else_def((config.configuration.applic_else)()),
                                |x| vec![x],
                            ),
                            config_parse(),
                            map(lex_end_config_def((config.configuration.end)()), |x| {
                                vec![x]
                            }),
                            config_group_not_parse(),
                            map(
                                lex_config_group_switch_def((config.configuration_group.switch)()),
                                |x| vec![x],
                            ),
                            config_group_case_parse(),
                            map(
                                lex_config_group_else_def(
                                    (config.configuration_group.applic_else)(),
                                ),
                                |x| vec![x],
                            ),
                            config_group_parse(),
                            map(
                                lex_end_config_group_def((config.configuration_group.end)()),
                                |x| vec![x],
                            ),
                        )),
                        //TODO: convert to make carriage return have to be followed by a new line
                        alt((
                            lex_unix_new_line_def((config.unix_new_line)()),
                            lex_carriage_return_def((config.carriage_new_line)()),
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
    let eof = || map(lex_eof((config.eof)()), |_| vec![LexerToken::Eof]);
    let text_parser = map(
        many_till(anychar, alt((comments(), eof()))),
        |(results, mut comment): (Vec<char>, Vec<LexerToken>)| {
            let res = results.iter().clone().collect::<String>();
            comment.insert(0, LexerToken::Text(res));
            comment
        },
    );
    let parser = map(
        many_till(alt((comments(), text_parser)), lex_eof((config.eof)())),
        |(res, eof): (Vec<Vec<LexerToken>>, LexerToken)| {
            let mut flattened = res.into_iter().flatten().collect::<Vec<LexerToken>>();
            match flattened.last() {
                Some(i) => {
                    if *i != LexerToken::Eof {
                        flattened.push(eof);
                    }
                }
                None => panic!("failed to tokenize text document"),
            }
            flattened
        },
    );
    parser
}

#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use nom::{
        bytes::complete::tag,
        character::complete::{char, newline, space1},
        combinator::{eof, fail},
    };

    use crate::{lex_applicability, ApplicabilityLexerConfig, LexerConfig, LexerToken};

    #[test]
    fn basic_text() {
        let config = LexerConfig {
            feature: ApplicabilityLexerConfig {
                _phantom_i: PhantomData::<&str>,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("Feature"),
                not: || tag("Feature Not"),
                switch: || tag("Feature Switch"),
                case: || tag("Feature Case"),
                applic_else: || tag("Feature Else"),
                end: || tag("End Feature"),
            },
            configuration: ApplicabilityLexerConfig {
                _phantom_i: PhantomData::<&str>,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("Configuration"),
                not: || tag("Configuration Not"),
                switch: || tag("Configuration Switch"),
                case: || tag("Configuration Case"),
                applic_else: || tag("Configuration Else"),
                end: || tag("End Configuration"),
            },
            configuration_group: ApplicabilityLexerConfig {
                _phantom_i: PhantomData::<&str>,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("ConfigurationGroup"),
                not: || tag("ConfigurationGroup Not"),
                switch: || tag("ConfigurationGroup Switch"),
                case: || tag("ConfigurationGroup Case"),
                applic_else: || tag("ConfigurationGroup Else"),
                end: || tag("End ConfigurationGroup"),
            },
            _phantom_o1: PhantomData,
            _phantom_o2: PhantomData,
            _phantom_o3: PhantomData,
            _phantom_o4: PhantomData,
            _phantom_o5: PhantomData,
            _phantom_o6: PhantomData,
            _phantom_o7: PhantomData,
            _phantom_o8: PhantomData,
            _phantom_o9: PhantomData,
            _phantom_o10: PhantomData,
            _phantom_o11: PhantomData::<LexerToken>,
            _phantom_o12: PhantomData::<LexerToken>,
            _phantom_o13: PhantomData::<LexerToken>,
            _phantom_o14: PhantomData::<LexerToken>,
            _phantom_o15: PhantomData,
            _phantom_o16: PhantomData,
            _phantom_o17: PhantomData,
            space: || space1,
            unix_new_line: || newline,
            carriage_new_line: || char('\r'),
            start_brace: || tag("["),
            end_brace: || tag("]"),
            start_paren: || tag("("),
            end_paren: || tag(")"),
            not: || tag("!"),
            and: || tag("&"),
            or: || tag("|"),
            start_comment_single_line: || tag("``"),
            end_comment_single_line: || tag("``"),
            start_comment_multi_line: || fail,
            end_comment_multi_line: || fail,
            multi_line_comment_character: || fail,
            single_line_comment: || fail,
            eof: || eof,
        };
        let mut tokenizer = lex_applicability(config);

        assert_eq!(
            tokenizer("some text"),
            Ok((
                "",
                vec![LexerToken::Text("some text".to_string()), LexerToken::Eof]
            ))
        )
    }

    #[test]
    fn feature_tag() {
        let config = LexerConfig {
            feature: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("Feature"),
                not: || tag("Feature Not"),
                switch: || tag("Feature Switch"),
                case: || tag("Feature Case"),
                applic_else: || tag("Feature Else"),
                end: || tag("End Feature"),
            },
            configuration: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("Configuration"),
                not: || tag("Configuration Not"),
                switch: || tag("Configuration Switch"),
                case: || tag("Configuration Case"),
                applic_else: || tag("Configuration Else"),
                end: || tag("End Configuration"),
            },
            configuration_group: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("ConfigurationGroup"),
                not: || tag("ConfigurationGroup Not"),
                switch: || tag("ConfigurationGroup Switch"),
                case: || tag("ConfigurationGroup Case"),
                applic_else: || tag("ConfigurationGroup Else"),
                end: || tag("End ConfigurationGroup"),
            },
            _phantom_o1: PhantomData,
            _phantom_o2: PhantomData,
            _phantom_o3: PhantomData,
            _phantom_o4: PhantomData,
            _phantom_o5: PhantomData,
            _phantom_o6: PhantomData,
            _phantom_o7: PhantomData,
            _phantom_o8: PhantomData,
            _phantom_o9: PhantomData,
            _phantom_o10: PhantomData,
            _phantom_o11: PhantomData::<LexerToken>,
            _phantom_o12: PhantomData::<LexerToken>,
            _phantom_o13: PhantomData::<LexerToken>,
            _phantom_o14: PhantomData::<LexerToken>,
            _phantom_o15: PhantomData,
            _phantom_o16: PhantomData,
            _phantom_o17: PhantomData,
            space: || space1,
            unix_new_line: || newline,
            carriage_new_line: || char('\r'),
            start_brace: || tag("["),
            end_brace: || tag("]"),
            start_paren: || tag("("),
            end_paren: || tag(")"),
            not: || tag("!"),
            and: || tag("&"),
            or: || tag("|"),
            start_comment_single_line: || tag("``"),
            end_comment_single_line: || tag("``"),
            start_comment_multi_line: || fail,
            end_comment_multi_line: || fail,
            multi_line_comment_character: || fail,
            single_line_comment: || fail,
            eof: || eof,
        };
        let mut tokenizer = lex_applicability(config);
        assert_eq!(
            tokenizer("``Feature[FEATURE_1]`` some text ``End Feature``"),
            Ok((
                "",
                vec![
                    LexerToken::StartCommentSingleLine,
                    LexerToken::Feature,
                    LexerToken::StartBrace,
                    LexerToken::Tag("FEATURE_1".to_string()),
                    LexerToken::EndBrace,
                    LexerToken::EndCommentSingleLine,
                    LexerToken::Text(" some text ".to_string()),
                    LexerToken::StartCommentSingleLine,
                    LexerToken::EndFeature,
                    LexerToken::EndCommentSingleLine,
                    LexerToken::Eof
                ]
            ))
        )
    }

    #[test]
    fn feature_and_feature_tag() {
        let config = LexerConfig {
            feature: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("Feature"),
                not: || tag("Feature Not"),
                switch: || tag("Feature Switch"),
                case: || tag("Feature Case"),
                applic_else: || tag("Feature Else"),
                end: || tag("End Feature"),
            },
            configuration: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("Configuration"),
                not: || tag("Configuration Not"),
                switch: || tag("Configuration Switch"),
                case: || tag("Configuration Case"),
                applic_else: || tag("Configuration Else"),
                end: || tag("End Configuration"),
            },
            configuration_group: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("ConfigurationGroup"),
                not: || tag("ConfigurationGroup Not"),
                switch: || tag("ConfigurationGroup Switch"),
                case: || tag("ConfigurationGroup Case"),
                applic_else: || tag("ConfigurationGroup Else"),
                end: || tag("End ConfigurationGroup"),
            },
            _phantom_o1: PhantomData,
            _phantom_o2: PhantomData,
            _phantom_o3: PhantomData,
            _phantom_o4: PhantomData,
            _phantom_o5: PhantomData,
            _phantom_o6: PhantomData,
            _phantom_o7: PhantomData,
            _phantom_o8: PhantomData,
            _phantom_o9: PhantomData,
            _phantom_o10: PhantomData,
            _phantom_o11: PhantomData::<LexerToken>,
            _phantom_o12: PhantomData::<LexerToken>,
            _phantom_o13: PhantomData::<LexerToken>,
            _phantom_o14: PhantomData::<LexerToken>,
            _phantom_o15: PhantomData,
            _phantom_o16: PhantomData,
            _phantom_o17: PhantomData,
            space: || space1,
            unix_new_line: || newline,
            carriage_new_line: || char('\r'),
            start_brace: || tag("["),
            end_brace: || tag("]"),
            start_paren: || tag("("),
            end_paren: || tag(")"),
            not: || tag("!"),
            and: || tag("&"),
            or: || tag("|"),
            start_comment_single_line: || tag("``"),
            end_comment_single_line: || tag("``"),
            start_comment_multi_line: || fail,
            end_comment_multi_line: || fail,
            multi_line_comment_character: || fail,
            single_line_comment: || fail,
            eof: || eof,
        };
        let mut tokenizer = lex_applicability(config);
        assert_eq!(
            tokenizer("``Feature[FEATURE_1 & FEATURE_2]`` some text ``End Feature``"),
            Ok((
                "",
                vec![
                    LexerToken::StartCommentSingleLine,
                    LexerToken::Feature,
                    LexerToken::StartBrace,
                    LexerToken::Tag("FEATURE_1".to_string()),
                    LexerToken::Space,
                    LexerToken::And,
                    LexerToken::Space,
                    LexerToken::Tag("FEATURE_2".to_string()),
                    LexerToken::EndBrace,
                    LexerToken::EndCommentSingleLine,
                    LexerToken::Text(" some text ".to_string()),
                    LexerToken::StartCommentSingleLine,
                    LexerToken::EndFeature,
                    LexerToken::EndCommentSingleLine,
                    LexerToken::Eof
                ]
            ))
        )
    }

    #[test]
    fn feature_or_feature_tag() {
        let config = LexerConfig {
            feature: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("Feature"),
                not: || tag("Feature Not"),
                switch: || tag("Feature Switch"),
                case: || tag("Feature Case"),
                applic_else: || tag("Feature Else"),
                end: || tag("End Feature"),
            },
            configuration: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("Configuration"),
                not: || tag("Configuration Not"),
                switch: || tag("Configuration Switch"),
                case: || tag("Configuration Case"),
                applic_else: || tag("Configuration Else"),
                end: || tag("End Configuration"),
            },
            configuration_group: ApplicabilityLexerConfig {
                _phantom_i: PhantomData,
                _phantom_o1: PhantomData,
                _phantom_o2: PhantomData,
                _phantom_o3: PhantomData,
                _phantom_o4: PhantomData,
                _phantom_o5: PhantomData,
                _phantom_o6: PhantomData,
                _phantom_e: PhantomData::<nom::error::Error<_>>,
                base: || tag("ConfigurationGroup"),
                not: || tag("ConfigurationGroup Not"),
                switch: || tag("ConfigurationGroup Switch"),
                case: || tag("ConfigurationGroup Case"),
                applic_else: || tag("ConfigurationGroup Else"),
                end: || tag("End ConfigurationGroup"),
            },
            _phantom_o1: PhantomData,
            _phantom_o2: PhantomData,
            _phantom_o3: PhantomData,
            _phantom_o4: PhantomData,
            _phantom_o5: PhantomData,
            _phantom_o6: PhantomData,
            _phantom_o7: PhantomData,
            _phantom_o8: PhantomData,
            _phantom_o9: PhantomData,
            _phantom_o10: PhantomData,
            _phantom_o11: PhantomData::<LexerToken>,
            _phantom_o12: PhantomData::<LexerToken>,
            _phantom_o13: PhantomData::<LexerToken>,
            _phantom_o14: PhantomData::<LexerToken>,
            _phantom_o15: PhantomData,
            _phantom_o16: PhantomData,
            _phantom_o17: PhantomData,
            space: || space1,
            unix_new_line: || newline,
            carriage_new_line: || char('\r'),
            start_brace: || tag("["),
            end_brace: || tag("]"),
            start_paren: || tag("("),
            end_paren: || tag(")"),
            not: || tag("!"),
            and: || tag("&"),
            or: || tag("|"),
            start_comment_single_line: || tag("``"),
            end_comment_single_line: || tag("``"),
            start_comment_multi_line: || fail,
            end_comment_multi_line: || fail,
            multi_line_comment_character: || fail,
            single_line_comment: || fail,
            eof: || eof,
        };
        let mut tokenizer = lex_applicability(config);
        assert_eq!(
            tokenizer("``Feature[FEATURE_1 | FEATURE_2]`` some text ``End Feature``"),
            Ok((
                "",
                vec![
                    LexerToken::StartCommentSingleLine,
                    LexerToken::Feature,
                    LexerToken::StartBrace,
                    LexerToken::Tag("FEATURE_1".to_string()),
                    LexerToken::Space,
                    LexerToken::Or,
                    LexerToken::Space,
                    LexerToken::Tag("FEATURE_2".to_string()),
                    LexerToken::EndBrace,
                    LexerToken::EndCommentSingleLine,
                    LexerToken::Text(" some text ".to_string()),
                    LexerToken::StartCommentSingleLine,
                    LexerToken::EndFeature,
                    LexerToken::EndCommentSingleLine,
                    LexerToken::Eof
                ]
            ))
        )
    }
}
