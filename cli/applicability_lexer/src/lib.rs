use std::ops::RangeFrom;

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
    bytes::tag,
    character::complete::{anychar, char, newline, space1},
    combinator::{eof, fail, map, peek},
    error::ParseError,
    multi::many_till,
    sequence::tuple,
    AsChar, Compare, Input, Parser,
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

pub trait LexerConfig<I: Input, E: ParseError<I>> {
    fn feature_base(&self) -> impl Parser<I>;
    fn feature_not(&self) -> impl Parser<I>;
    fn feature_switch(&self) -> impl Parser<I>;
    fn feature_case(&self) -> impl Parser<I>;
    fn feature_else(&self) -> impl Parser<I>;
    fn feature_end(&self) -> impl Parser<I>;
    fn config_base(&self) -> impl Parser<I>;
    fn config_not(&self) -> impl Parser<I>;
    fn config_switch(&self) -> impl Parser<I>;
    fn config_case(&self) -> impl Parser<I>;
    fn config_else(&self) -> impl Parser<I>;
    fn config_end(&self) -> impl Parser<I>;
    fn group_base(&self) -> impl Parser<I>;
    fn group_not(&self) -> impl Parser<I>;
    fn group_switch(&self) -> impl Parser<I>;
    fn group_case(&self) -> impl Parser<I>;
    fn group_else(&self) -> impl Parser<I>;
    fn group_end(&self) -> impl Parser<I>;
    fn space(&self) -> impl Parser<I>;
    fn unix_new_line(&self) -> impl Parser<I>;
    fn carriage_new_line(&self) -> impl Parser<I>;
    fn start_brace(&self) -> impl Parser<I>;
    fn end_brace(&self) -> impl Parser<I>;
    fn not(&self) -> impl Parser<I>;
    fn and(&self) -> impl Parser<I>;
    fn or(&self) -> impl Parser<I>;
    fn start_comment_single_line(&self) -> impl Parser<I>;
    fn end_comment_single_line(&self) -> impl Parser<I>;
    fn start_comment_multi_line(&self) -> impl Parser<I>;
    fn end_comment_multi_line(&self) -> impl Parser<I>;
    fn multi_line_comment_character(&self) -> impl Parser<I>;
    fn single_line_comment(&self) -> impl Parser<I>;
    fn eof(&self) -> impl Parser<I>;
    fn start_paren(&self) -> impl Parser<I>;
    fn end_paren(&self) -> impl Parser<I>;
}

pub struct MarkdownConfig<
    'a,
    'b,
    'c,
    'd,
    'e,
    'f,
    'g,
    'h,
    'i,
    'j,
    'k,
    'l,
    'm,
    'n,
    'o,
    'p,
    'q,
    'r,
    's,
    't,
    'u,
> {
    document: &'a str,
    feature_base: &'b str,
    feature_not: &'c str,
    feature_switch: &'d str,
    feature_case: &'e str,
    feature_else: &'f str,
    feature_end: &'g str,
    config_base: &'h str,
    config_not: &'i str,
    config_switch: &'j str,
    config_case: &'k str,
    config_else: &'l str,
    config_end: &'m str,
    group_base: &'n str,
    group_not: &'o str,
    group_switch: &'p str,
    group_case: &'q str,
    group_else: &'r str,
    group_end: &'s str,
    start_comment_single_line: &'t str,
    end_comment_single_line: &'u str,
}

pub struct MarkdownDocumentConfig {}

impl MarkdownDocumentConfig {
    pub fn new() -> Self {
        Self {}
    }
}

pub trait FeatureBase {
    fn feature_base<'x, I, E>(&self) -> impl Parser<I, Error = E>
    where
        I: Input + Compare<&'x str>,
        E: ParseError<I>,
    {
        tag("Feature")
    }
}

impl FeatureBase for MarkdownDocumentConfig {}

pub trait FeatureNot {
    fn feature_not<'x, I, E>(&self) -> impl Parser<I, Error = E>
    where
        I: Input + Compare<&'x str>,
        E: ParseError<I>,
    {
        tag("Feature Not")
    }
}

impl FeatureNot for MarkdownDocumentConfig {}

pub trait FeatureSwitch {
    fn feature_switch<'x, I, E>(&self) -> impl Parser<I, Error = E>
    where
        I: Input + Compare<&'x str>,
        E: ParseError<I>,
    {
        tag("Feature Switch")
    }
}

impl FeatureSwitch for MarkdownDocumentConfig {}

pub trait FeatureCase<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn feature_case(&self) -> impl Parser<I, Error = E> {
        tag("Feature Case")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> FeatureCase<'x, I, E>
    for MarkdownDocumentConfig
{
}

//Configuration
pub trait ConfigBase<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn config_base(&self) -> impl Parser<I, Error = E> {
        tag("Configuration")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> ConfigBase<'x, I, E>
    for MarkdownDocumentConfig
{
}

pub trait ConfigNot<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn config_not(&self) -> impl Parser<I, Error = E> {
        tag("Configuration Not")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> ConfigNot<'x, I, E>
    for MarkdownDocumentConfig
{
}

pub trait ConfigSwitch<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn config_switch(&self) -> impl Parser<I, Error = E> {
        tag("Configuration Switch")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> ConfigSwitch<'x, I, E>
    for MarkdownDocumentConfig
{
}

pub trait ConfigCase<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn config_case(&self) -> impl Parser<I, Error = E> {
        tag("Configuration Case")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> ConfigCase<'x, I, E>
    for MarkdownDocumentConfig
{
}

//Configuration Group
pub trait GroupBase<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn group_base(&self) -> impl Parser<I, Error = E> {
        tag("ConfigurationGroup")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> GroupBase<'x, I, E>
    for MarkdownDocumentConfig
{
}

pub trait GroupNot<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn group_not(&self) -> impl Parser<I, Error = E> {
        tag("ConfigurationGroup Not")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> GroupNot<'x, I, E>
    for MarkdownDocumentConfig
{
}

pub trait GroupSwitch<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn group_switch(&self) -> impl Parser<I, Error = E> {
        tag("ConfigurationGroup Switch")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> GroupSwitch<'x, I, E>
    for MarkdownDocumentConfig
{
}

pub trait GroupCase<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn group_case(&self) -> impl Parser<I, Error = E> {
        tag("ConfigurationGroup Case")
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> GroupCase<'x, I, E>
    for MarkdownDocumentConfig
{
}

// Base Capabilities
pub trait Space<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn space(&self) -> impl Parser<I, Error = E> {
        space1
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> Space<'x, I, E> for MarkdownDocumentConfig where
    I::Item: AsChar
{
}

pub trait NewLine<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn newline(&self) -> impl Parser<I, Error = E> {
        newline
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> NewLine<'x, I, E> for MarkdownDocumentConfig where
    I::Item: AsChar
{
}

pub trait CarriageReturn<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn carriage_return(&self) -> impl Parser<I, Error = E> {
        char('\r')
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> CarriageReturn<'x, I, E>
    for MarkdownDocumentConfig
where
    I::Item: AsChar,
{
}

pub trait StartBrace<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn start_brace(&self) -> impl Parser<I, Error = E> {
        char('[')
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> StartBrace<'x, I, E>
    for MarkdownDocumentConfig
where
    I::Item: AsChar,
{
}

pub trait EndBrace<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn end_brace(&self) -> impl Parser<I, Error = E> {
        char(']')
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> EndBrace<'x, I, E>
    for MarkdownDocumentConfig
where
    I::Item: AsChar,
{
}

pub trait StartParen<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn start_paren(&self) -> impl Parser<I, Error = E> {
        char('(')
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> StartParen<'x, I, E>
    for MarkdownDocumentConfig
where
    I::Item: AsChar,
{
}

pub trait EndParen<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn end_paren(&self) -> impl Parser<I, Error = E> {
        char(')')
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> EndParen<'x, I, E>
    for MarkdownDocumentConfig
where
    I::Item: AsChar,
{
}

pub trait Not<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn not(&self) -> impl Parser<I, Error = E> {
        char('!')
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> Not<'x, I, E> for MarkdownDocumentConfig where
    I::Item: AsChar
{
}

pub trait And<'x, I: Input + Compare<&'x str>, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn and(&self) -> impl Parser<I, Error = E> {
        char('&')
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> And<'x, I, E> for MarkdownDocumentConfig where
    I::Item: AsChar
{
}

pub trait Or<I: Input, E: ParseError<I>>
where
    I::Item: AsChar,
{
    fn or(&self) -> impl Parser<I, Error = E> {
        char('|')
    }
}

impl<'x, I: Input + Compare<&'x str>, E: ParseError<I>> Or<I, E> for MarkdownDocumentConfig where
    I::Item: AsChar
{
}

pub trait Eof {
    fn eof<I, E>(&self) -> impl Parser<I, Error = E>
    where
        I: Input,
        E: ParseError<I>,
    {
        eof
    }
}

impl Eof for MarkdownDocumentConfig {}

// LEXER IMPLEMENTATION
pub trait LexApplicability<'x, I: Input + Compare<&'x str>, E: ParseError<I>> {
    fn lex_applicability(&self) -> impl Parser<I, Error = E>;
}

impl<'a, I: Input + Compare<&'a str>, E: ParseError<I>, T> LexApplicability<'a, I, E> for T
where
    I::Item: AsChar,
    E: ParseError<I>,
    T: FeatureBase + FeatureNot + FeatureSwitch,
{
    fn lex_applicability(&self) -> impl Parser<I, Error = E> {
        self.feature_base()
    }
}
// pub trait LexApplicability {
//     fn lex_applicability<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         E: ParseError<I>;
// }

// impl<T> LexApplicability for T
// where
//     T: FeatureBase + FeatureNot + FeatureSwitch,
// {
//     fn lex_applicability<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         E: ParseError<I>,
//     {
//         self.feature_base()
//     }
// }

// impl<'a>
//     MarkdownConfig<
//         'a,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//         '_,
//     >
// {
//     fn new() -> Self {
//         Self {
//             document: "",
//             feature_base: "Feature",
//             feature_not: "Feature Not",
//             feature_switch: "Feature Switch",
//             feature_case: "Feature Case",
//             feature_else: "Feature Else",
//             feature_end: "End Feature",
//             config_base: "Configuration",
//             config_not: "Configuration Not",
//             config_switch: "Configuration Switch",
//             config_case: "Configuration Case",
//             config_else: "Configuration Else",
//             config_end: "End Configuration",
//             group_base: "ConfigurationGroup",
//             group_not: "ConfigurationGroup Not",
//             group_switch: "ConfigurationGroup Switch",
//             group_case: "ConfigurationGroup Case",
//             group_else: "ConfigurationGroup Else",
//             group_end: "End ConfigurationGroup",
//             start_comment_single_line: "``",
//             end_comment_single_line: "``",
//         }
//     }
//     fn set_document(mut self, doc: &'a str) {
//         self.document = doc;
//     }
// }

// impl<'a, 'b, 'c, 'd, 'e, 'f, 'g, 'h, 'i, 'j, 'k, 'l, 'm, 'n, 'o, 'p, 'q, 'r, 's, 't, 'u, I, E>
//     LexerConfig<I, E>
//     for MarkdownConfig<
//         'a,
//         'b,
//         'c,
//         'd,
//         'e,
//         'f,
//         'g,
//         'h,
//         'i,
//         'j,
//         'k,
//         'l,
//         'm,
//         'n,
//         'o,
//         'p,
//         'q,
//         'r,
//         's,
//         't,
//         'u,
//     >
// where
//     I: Input + Compare<&'b str>,
//     <I as nom::Input>::Item: nom::AsChar,
//     E: ParseError<I>,
//     // 'a: 'b,
//     // 'a: 'c,
//     // 'a: 'd,
//     // 'a: 'e,
//     // 'a: 'f,
//     // 'a: 'g,
//     // 'a: 'h,
//     // 'a: 'i,
//     // 'a: 'j,
//     // 'a: 'k,
//     // 'a: 'l,
//     // 'a: 'm,
//     // 'a: 'n,
//     // 'a: 'o,
//     // 'a: 'p,
//     // 'a: 'q,
//     // 'a: 'r,
//     // 'a: 's,
//     // 'a: 't,
//     // 'a: 'u,
//     // 'b: 'a,
//     // 'c: 'a,
//     // 'd: 'a,
//     // 'e: 'a,
//     // 'f: 'a,
//     // 'g: 'a,
//     // 'h: 'a,
//     // 'i: 'a,
//     // 'j: 'a,
//     // 'k: 'a,
//     // 'l: 'a,
//     // 'm: 'a,
//     // 'n: 'a,
//     // 'o: 'a,
//     // 'p: 'a,
//     // 'q: 'a,
//     // 'r: 'a,
//     // 's: 'a,
//     // 't: 'a,
//     // 'u: 'a,
// {
//     fn feature_base(&self) -> impl Parser<I, Error = E> {
//         tag(self.feature_base)
//     }

//     fn feature_not(&self) -> impl Parser<I, Error = E> {
//         tag(self.feature_not)
//     }

//     fn feature_switch(&self) -> impl Parser<I, Error = E> {
//         tag(self.feature_switch)
//     }

//     fn feature_case(&self) -> impl Parser<I, Error = E> {
//         tag(self.feature_case)
//     }

//     fn feature_else(&self) -> impl Parser<I, Error = E> {
//         tag(self.feature_else)
//     }

//     fn feature_end(&self) -> impl Parser<I, Error = E> {
//         tag(self.feature_end)
//     }

//     fn config_base(&self) -> impl Parser<I, Error = E> {
//         tag(self.config_base)
//     }

//     fn config_not(&self) -> impl Parser<I, Error = E> {
//         tag(self.config_not)
//     }

//     fn config_switch(&self) -> impl Parser<I, Error = E> {
//         tag(self.config_switch)
//     }

//     fn config_case(&self) -> impl Parser<I, Error = E> {
//         tag(self.config_case)
//     }

//     fn config_else(&self) -> impl Parser<I, Error = E> {
//         tag(self.config_else)
//     }

//     fn config_end(&self) -> impl Parser<I, Error = E> {
//         tag(self.config_end)
//     }

//     fn group_base(&self) -> impl Parser<I, Error = E> {
//         tag(self.group_base)
//     }

//     fn group_not(&self) -> impl Parser<I, Error = E> {
//         tag(self.group_not)
//     }

//     fn group_switch(&self) -> impl Parser<I, Error = E> {
//         tag(self.group_switch)
//     }

//     fn group_case(&self) -> impl Parser<I, Error = E> {
//         tag(self.group_case)
//     }

//     fn group_else(&self) -> impl Parser<I, Error = E> {
//         tag(self.group_else)
//     }

//     fn group_end(&self) -> impl Parser<I, Error = E> {
//         tag(self.group_end)
//     }

//     fn space(&self) -> impl Parser<I, Error = E> {
//         space1
//     }

//     fn unix_new_line(&self) -> impl Parser<I, Error = E> {
//         newline
//     }

//     fn carriage_new_line(&self) -> impl Parser<I, Error = E> {
//         char('\r')
//     }

//     fn start_brace(&self) -> impl Parser<I, Error = E> {
//         char('[')
//     }

//     fn end_brace(&self) -> impl Parser<I, Error = E> {
//         char(']')
//     }

//     fn not(&self) -> impl Parser<I, Error = E> {
//         char('!')
//     }

//     fn and(&self) -> impl Parser<I, Error = E> {
//         char('&')
//     }

//     fn or(&self) -> impl Parser<I, Error = E> {
//         char('|')
//     }

//     fn start_comment_single_line(&self) -> impl Parser<I, Error = E> {
//         tag(self.start_comment_single_line)
//     }

//     fn end_comment_single_line(&self) -> impl Parser<I, Error = E> {
//         tag(self.end_comment_single_line)
//     }

//     fn start_comment_multi_line(&self) -> impl Parser<I, Error = E> {
//         fail
//     }

//     fn end_comment_multi_line(&self) -> impl Parser<I, Error = E> {
//         fail
//     }

//     fn multi_line_comment_character(&self) -> impl Parser<I, Error = E> {
//         fail
//     }

//     fn single_line_comment(&self) -> impl Parser<I, Error = E> {
//         fail
//     }

//     fn eof(&self) -> impl Parser<I, Error = E> {
//         eof
//     }

//     fn start_paren(&self) -> impl Parser<I, Error = E> {
//         char('(')
//     }

//     fn end_paren(&self) -> impl Parser<I, Error = E> {
//         char(')')
//     }
// }
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

// pub fn lex_applicability<
//     'a,
//     I,
//     E,
//     T: LexerConfigurable<I = I>,
//     U: LexerError<E = E>,
//     L: LexerConfig<T, U> + 'static,
// >(
//     config: &L,
// ) -> impl FnMut(I) -> Result<(I, Vec<LexerToken>), nom::Err<E>> + use<'_, I, E, T, L, U>
// // &'a impl Parser<I, Vec<LexerToken>, E>
// // impl FnMut(I) -> Result<(I, Vec<LexerToken>), nom::Err<E>>
// where
//     I: Clone
//         + InputLength
//         + InputIter<Item = char>
//         + Slice<RangeFrom<usize>>
//         + InputTake
//         + Compare<T::O2>,
//     E: ParseError<I>,
// {
//     let space_0_0 = config.space();
//     let start_brace_0_0 = config.start_brace();
//     let start_brace_parse_0_0 = || {
//         many_till(
//             lex_space_def(space_0_0),
//             lex_start_brace_def(start_brace_0_0),
//         )
//     };
//     let space_1_0 = config.space();
//     let start_brace_1_0 = config.start_brace();
//     let start_brace_parse_1_0 = || {
//         many_till(
//             lex_space_def(space_1_0),
//             lex_start_brace_def(start_brace_1_0),
//         )
//     };
//     let space_2_0 = config.space();
//     let start_brace_2_0 = config.start_brace();
//     let start_brace_parse_2_0 = || {
//         many_till(
//             lex_space_def(space_2_0),
//             lex_start_brace_def(start_brace_2_0),
//         )
//     };
//     let space_3_0 = config.space();
//     let start_brace_3_0 = config.start_brace();
//     let start_brace_parse_3_0 = || {
//         many_till(
//             lex_space_def(space_3_0),
//             lex_start_brace_def(start_brace_3_0),
//         )
//     };
//     let space_4_0 = config.space();
//     let start_brace_4_0 = config.start_brace();
//     let start_brace_parse_4_0 = || {
//         many_till(
//             lex_space_def(space_4_0),
//             lex_start_brace_def(start_brace_4_0),
//         )
//     };
//     let space_5_0 = config.space();
//     let start_brace_5_0 = config.start_brace();
//     let start_brace_parse_5_0 = || {
//         many_till(
//             lex_space_def(space_5_0),
//             lex_start_brace_def(start_brace_5_0),
//         )
//     };
//     let space_6_0 = config.space();
//     let start_brace_6_0 = config.start_brace();
//     let start_brace_parse_6_0 = || {
//         many_till(
//             lex_space_def(space_6_0),
//             lex_start_brace_def(start_brace_6_0),
//         )
//     };
//     let space_7_0 = config.space();
//     let start_brace_7_0 = config.start_brace();
//     let start_brace_parse_7_0 = || {
//         many_till(
//             lex_space_def(space_7_0),
//             lex_start_brace_def(start_brace_7_0),
//         )
//     };
//     let space_8_0 = config.space();
//     let start_brace_8_0 = config.start_brace();
//     let start_brace_parse_8_0 = || {
//         many_till(
//             lex_space_def(space_8_0),
//             lex_start_brace_def(start_brace_8_0),
//         )
//     };
//     // let p = move |c: L| {
//     let space_base_0_0 = config.space();
//     let carriage_new_line_base_0_0 = config.carriage_new_line();
//     let unix_new_line_base_0_0 = config.unix_new_line();
//     let start_paren_base_0_0 = config.start_paren();
//     let end_paren_base_0_0 = config.end_paren();
//     let not_base_0_0 = config.not();
//     let and_base_0_0 = config.and();
//     let or_base_0_0 = config.or();
//     let end_brace_base_0_0 = config.end_brace();
//     let base_tag_parse_0_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_0_0),
//                     lex_carriage_return_def(carriage_new_line_base_0_0),
//                     lex_unix_new_line_def(unix_new_line_base_0_0),
//                     lex_start_paren_def(start_paren_base_0_0),
//                     lex_end_paren_def(end_paren_base_0_0),
//                     lex_not_def(not_base_0_0),
//                     lex_and_def(and_base_0_0),
//                     lex_or_def(or_base_0_0),
//                     lex_end_brace_def(end_brace_base_0_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_1_0 = config.space();
//     let carriage_new_line_base_1_0 = config.carriage_new_line();
//     let unix_new_line_base_1_0 = config.unix_new_line();
//     let start_paren_base_1_0 = config.start_paren();
//     let end_paren_base_1_0 = config.end_paren();
//     let not_base_1_0 = config.not();
//     let and_base_1_0 = config.and();
//     let or_base_1_0 = config.or();
//     let end_brace_base_1_0 = config.end_brace();
//     let base_tag_parse_1_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_1_0),
//                     lex_carriage_return_def(carriage_new_line_base_1_0),
//                     lex_unix_new_line_def(unix_new_line_base_1_0),
//                     lex_start_paren_def(start_paren_base_1_0),
//                     lex_end_paren_def(end_paren_base_1_0),
//                     lex_not_def(not_base_1_0),
//                     lex_and_def(and_base_1_0),
//                     lex_or_def(or_base_1_0),
//                     lex_end_brace_def(end_brace_base_1_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_2_0 = config.space();
//     let carriage_new_line_base_2_0 = config.carriage_new_line();
//     let unix_new_line_base_2_0 = config.unix_new_line();
//     let start_paren_base_2_0 = config.start_paren();
//     let end_paren_base_2_0 = config.end_paren();
//     let not_base_2_0 = config.not();
//     let and_base_2_0 = config.and();
//     let or_base_2_0 = config.or();
//     let end_brace_base_2_0 = config.end_brace();
//     let base_tag_parse_2_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_2_0),
//                     lex_carriage_return_def(carriage_new_line_base_2_0),
//                     lex_unix_new_line_def(unix_new_line_base_2_0),
//                     lex_start_paren_def(start_paren_base_2_0),
//                     lex_end_paren_def(end_paren_base_2_0),
//                     lex_not_def(not_base_2_0),
//                     lex_and_def(and_base_2_0),
//                     lex_or_def(or_base_2_0),
//                     lex_end_brace_def(end_brace_base_2_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_3_0 = config.space();
//     let carriage_new_line_base_3_0 = config.carriage_new_line();
//     let unix_new_line_base_3_0 = config.unix_new_line();
//     let start_paren_base_3_0 = config.start_paren();
//     let end_paren_base_3_0 = config.end_paren();
//     let not_base_3_0 = config.not();
//     let and_base_3_0 = config.and();
//     let or_base_3_0 = config.or();
//     let end_brace_base_3_0 = config.end_brace();
//     let base_tag_parse_3_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_3_0),
//                     lex_carriage_return_def(carriage_new_line_base_3_0),
//                     lex_unix_new_line_def(unix_new_line_base_3_0),
//                     lex_start_paren_def(start_paren_base_3_0),
//                     lex_end_paren_def(end_paren_base_3_0),
//                     lex_not_def(not_base_3_0),
//                     lex_and_def(and_base_3_0),
//                     lex_or_def(or_base_3_0),
//                     lex_end_brace_def(end_brace_base_3_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_4_0 = config.space();
//     let carriage_new_line_base_4_0 = config.carriage_new_line();
//     let unix_new_line_base_4_0 = config.unix_new_line();
//     let start_paren_base_4_0 = config.start_paren();
//     let end_paren_base_4_0 = config.end_paren();
//     let not_base_4_0 = config.not();
//     let and_base_4_0 = config.and();
//     let or_base_4_0 = config.or();
//     let end_brace_base_4_0 = config.end_brace();
//     let base_tag_parse_4_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_4_0),
//                     lex_carriage_return_def(carriage_new_line_base_4_0),
//                     lex_unix_new_line_def(unix_new_line_base_4_0),
//                     lex_start_paren_def(start_paren_base_4_0),
//                     lex_end_paren_def(end_paren_base_4_0),
//                     lex_not_def(not_base_4_0),
//                     lex_and_def(and_base_4_0),
//                     lex_or_def(or_base_4_0),
//                     lex_end_brace_def(end_brace_base_4_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_5_0 = config.space();
//     let carriage_new_line_base_5_0 = config.carriage_new_line();
//     let unix_new_line_base_5_0 = config.unix_new_line();
//     let start_paren_base_5_0 = config.start_paren();
//     let end_paren_base_5_0 = config.end_paren();
//     let not_base_5_0 = config.not();
//     let and_base_5_0 = config.and();
//     let or_base_5_0 = config.or();
//     let end_brace_base_5_0 = config.end_brace();
//     let base_tag_parse_5_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_5_0),
//                     lex_carriage_return_def(carriage_new_line_base_5_0),
//                     lex_unix_new_line_def(unix_new_line_base_5_0),
//                     lex_start_paren_def(start_paren_base_5_0),
//                     lex_end_paren_def(end_paren_base_5_0),
//                     lex_not_def(not_base_5_0),
//                     lex_and_def(and_base_5_0),
//                     lex_or_def(or_base_5_0),
//                     lex_end_brace_def(end_brace_base_5_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_6_0 = config.space();
//     let carriage_new_line_base_6_0 = config.carriage_new_line();
//     let unix_new_line_base_6_0 = config.unix_new_line();
//     let start_paren_base_6_0 = config.start_paren();
//     let end_paren_base_6_0 = config.end_paren();
//     let not_base_6_0 = config.not();
//     let and_base_6_0 = config.and();
//     let or_base_6_0 = config.or();
//     let end_brace_base_6_0 = config.end_brace();
//     let base_tag_parse_6_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_6_0),
//                     lex_carriage_return_def(carriage_new_line_base_6_0),
//                     lex_unix_new_line_def(unix_new_line_base_6_0),
//                     lex_start_paren_def(start_paren_base_6_0),
//                     lex_end_paren_def(end_paren_base_6_0),
//                     lex_not_def(not_base_6_0),
//                     lex_and_def(and_base_6_0),
//                     lex_or_def(or_base_6_0),
//                     lex_end_brace_def(end_brace_base_6_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_7_0 = config.space();
//     let carriage_new_line_base_7_0 = config.carriage_new_line();
//     let unix_new_line_base_7_0 = config.unix_new_line();
//     let start_paren_base_7_0 = config.start_paren();
//     let end_paren_base_7_0 = config.end_paren();
//     let not_base_7_0 = config.not();
//     let and_base_7_0 = config.and();
//     let or_base_7_0 = config.or();
//     let end_brace_base_7_0 = config.end_brace();
//     let base_tag_parse_7_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_7_0),
//                     lex_carriage_return_def(carriage_new_line_base_7_0),
//                     lex_unix_new_line_def(unix_new_line_base_7_0),
//                     lex_start_paren_def(start_paren_base_7_0),
//                     lex_end_paren_def(end_paren_base_7_0),
//                     lex_not_def(not_base_7_0),
//                     lex_and_def(and_base_7_0),
//                     lex_or_def(or_base_7_0),
//                     lex_end_brace_def(end_brace_base_7_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_8_0 = config.space();
//     let carriage_new_line_base_8_0 = config.carriage_new_line();
//     let unix_new_line_base_8_0 = config.unix_new_line();
//     let start_paren_base_8_0 = config.start_paren();
//     let end_paren_base_8_0 = config.end_paren();
//     let not_base_8_0 = config.not();
//     let and_base_8_0 = config.and();
//     let or_base_8_0 = config.or();
//     let end_brace_base_8_0 = config.end_brace();
//     let base_tag_parse_8_0 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_8_0),
//                     lex_carriage_return_def(carriage_new_line_base_8_0),
//                     lex_unix_new_line_def(unix_new_line_base_8_0),
//                     lex_start_paren_def(start_paren_base_8_0),
//                     lex_end_paren_def(end_paren_base_8_0),
//                     lex_not_def(not_base_8_0),
//                     lex_and_def(and_base_8_0),
//                     lex_or_def(or_base_8_0),
//                     lex_end_brace_def(end_brace_base_8_0),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let feature_parse_0 = || {
//         map(
//             tuple((
//                 lex_feature_def(config.feature_base()),
//                 start_brace_parse_0_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_0_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_not_parse_0 = || {
//         map(
//             tuple((
//                 lex_feature_not_def(config.feature_not()),
//                 start_brace_parse_1_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_1_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_case_parse_0 = || {
//         map(
//             tuple((
//                 lex_feature_case_def(config.feature_case()),
//                 start_brace_parse_2_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_2_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_parse_0 = || {
//         map(
//             tuple((
//                 lex_config_def(config.config_base()),
//                 start_brace_parse_3_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_3_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_not_parse_0 = || {
//         map(
//             tuple((
//                 lex_config_not_def(config.config_not()),
//                 start_brace_parse_4_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_4_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_case_parse_0 = || {
//         map(
//             tuple((
//                 lex_config_case_def(config.config_case()),
//                 start_brace_parse_5_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_5_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };

//     let config_group_parse_0 = || {
//         map(
//             tuple((
//                 lex_config_group_def(config.group_base()),
//                 start_brace_parse_6_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_6_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_not_parse_0 = || {
//         map(
//             tuple((
//                 lex_config_group_not_def(config.group_not()),
//                 start_brace_parse_7_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_7_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_case_parse_0 = || {
//         map(
//             tuple((
//                 lex_config_group_case_def(config.group_case()),
//                 start_brace_parse_8_0(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_8_0(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let space_0_1 = config.space();
//     let start_brace_0_1 = config.start_brace();
//     let start_brace_parse_0_1 = || {
//         many_till(
//             lex_space_def(space_0_1),
//             lex_start_brace_def(start_brace_0_1),
//         )
//     };
//     let space_1_1 = config.space();
//     let start_brace_1_1 = config.start_brace();
//     let start_brace_parse_1_1 = || {
//         many_till(
//             lex_space_def(space_1_1),
//             lex_start_brace_def(start_brace_1_1),
//         )
//     };
//     let space_2_1 = config.space();
//     let start_brace_2_1 = config.start_brace();
//     let start_brace_parse_2_1 = || {
//         many_till(
//             lex_space_def(space_2_1),
//             lex_start_brace_def(start_brace_2_1),
//         )
//     };
//     let space_3_1 = config.space();
//     let start_brace_3_1 = config.start_brace();
//     let start_brace_parse_3_1 = || {
//         many_till(
//             lex_space_def(space_3_1),
//             lex_start_brace_def(start_brace_3_1),
//         )
//     };
//     let space_4_1 = config.space();
//     let start_brace_4_1 = config.start_brace();
//     let start_brace_parse_4_1 = || {
//         many_till(
//             lex_space_def(space_4_1),
//             lex_start_brace_def(start_brace_4_1),
//         )
//     };
//     let space_5_1 = config.space();
//     let start_brace_5_1 = config.start_brace();
//     let start_brace_parse_5_1 = || {
//         many_till(
//             lex_space_def(space_5_1),
//             lex_start_brace_def(start_brace_5_1),
//         )
//     };
//     let space_6_1 = config.space();
//     let start_brace_6_1 = config.start_brace();
//     let start_brace_parse_6_1 = || {
//         many_till(
//             lex_space_def(space_6_1),
//             lex_start_brace_def(start_brace_6_1),
//         )
//     };
//     let space_7_1 = config.space();
//     let start_brace_7_1 = config.start_brace();
//     let start_brace_parse_7_1 = || {
//         many_till(
//             lex_space_def(space_7_1),
//             lex_start_brace_def(start_brace_7_1),
//         )
//     };
//     let space_8_1 = config.space();
//     let start_brace_8_1 = config.start_brace();
//     let start_brace_parse_8_1 = || {
//         many_till(
//             lex_space_def(space_8_1),
//             lex_start_brace_def(start_brace_8_1),
//         )
//     };
//     // let p = move |c: L| {
//     let space_base_0_1 = config.space();
//     let carriage_new_line_base_0_1 = config.carriage_new_line();
//     let unix_new_line_base_0_1 = config.unix_new_line();
//     let start_paren_base_0_1 = config.start_paren();
//     let end_paren_base_0_1 = config.end_paren();
//     let not_base_0_1 = config.not();
//     let and_base_0_1 = config.and();
//     let or_base_0_1 = config.or();
//     let end_brace_base_0_1 = config.end_brace();
//     let base_tag_parse_0_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_0_1),
//                     lex_carriage_return_def(carriage_new_line_base_0_1),
//                     lex_unix_new_line_def(unix_new_line_base_0_1),
//                     lex_start_paren_def(start_paren_base_0_1),
//                     lex_end_paren_def(end_paren_base_0_1),
//                     lex_not_def(not_base_0_1),
//                     lex_and_def(and_base_0_1),
//                     lex_or_def(or_base_0_1),
//                     lex_end_brace_def(end_brace_base_0_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_1_1 = config.space();
//     let carriage_new_line_base_1_1 = config.carriage_new_line();
//     let unix_new_line_base_1_1 = config.unix_new_line();
//     let start_paren_base_1_1 = config.start_paren();
//     let end_paren_base_1_1 = config.end_paren();
//     let not_base_1_1 = config.not();
//     let and_base_1_1 = config.and();
//     let or_base_1_1 = config.or();
//     let end_brace_base_1_1 = config.end_brace();
//     let base_tag_parse_1_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_1_1),
//                     lex_carriage_return_def(carriage_new_line_base_1_1),
//                     lex_unix_new_line_def(unix_new_line_base_1_1),
//                     lex_start_paren_def(start_paren_base_1_1),
//                     lex_end_paren_def(end_paren_base_1_1),
//                     lex_not_def(not_base_1_1),
//                     lex_and_def(and_base_1_1),
//                     lex_or_def(or_base_1_1),
//                     lex_end_brace_def(end_brace_base_1_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_2_1 = config.space();
//     let carriage_new_line_base_2_1 = config.carriage_new_line();
//     let unix_new_line_base_2_1 = config.unix_new_line();
//     let start_paren_base_2_1 = config.start_paren();
//     let end_paren_base_2_1 = config.end_paren();
//     let not_base_2_1 = config.not();
//     let and_base_2_1 = config.and();
//     let or_base_2_1 = config.or();
//     let end_brace_base_2_1 = config.end_brace();
//     let base_tag_parse_2_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_2_1),
//                     lex_carriage_return_def(carriage_new_line_base_2_1),
//                     lex_unix_new_line_def(unix_new_line_base_2_1),
//                     lex_start_paren_def(start_paren_base_2_1),
//                     lex_end_paren_def(end_paren_base_2_1),
//                     lex_not_def(not_base_2_1),
//                     lex_and_def(and_base_2_1),
//                     lex_or_def(or_base_2_1),
//                     lex_end_brace_def(end_brace_base_2_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_3_1 = config.space();
//     let carriage_new_line_base_3_1 = config.carriage_new_line();
//     let unix_new_line_base_3_1 = config.unix_new_line();
//     let start_paren_base_3_1 = config.start_paren();
//     let end_paren_base_3_1 = config.end_paren();
//     let not_base_3_1 = config.not();
//     let and_base_3_1 = config.and();
//     let or_base_3_1 = config.or();
//     let end_brace_base_3_1 = config.end_brace();
//     let base_tag_parse_3_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_3_1),
//                     lex_carriage_return_def(carriage_new_line_base_3_1),
//                     lex_unix_new_line_def(unix_new_line_base_3_1),
//                     lex_start_paren_def(start_paren_base_3_1),
//                     lex_end_paren_def(end_paren_base_3_1),
//                     lex_not_def(not_base_3_1),
//                     lex_and_def(and_base_3_1),
//                     lex_or_def(or_base_3_1),
//                     lex_end_brace_def(end_brace_base_3_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_4_1 = config.space();
//     let carriage_new_line_base_4_1 = config.carriage_new_line();
//     let unix_new_line_base_4_1 = config.unix_new_line();
//     let start_paren_base_4_1 = config.start_paren();
//     let end_paren_base_4_1 = config.end_paren();
//     let not_base_4_1 = config.not();
//     let and_base_4_1 = config.and();
//     let or_base_4_1 = config.or();
//     let end_brace_base_4_1 = config.end_brace();
//     let base_tag_parse_4_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_4_1),
//                     lex_carriage_return_def(carriage_new_line_base_4_1),
//                     lex_unix_new_line_def(unix_new_line_base_4_1),
//                     lex_start_paren_def(start_paren_base_4_1),
//                     lex_end_paren_def(end_paren_base_4_1),
//                     lex_not_def(not_base_4_1),
//                     lex_and_def(and_base_4_1),
//                     lex_or_def(or_base_4_1),
//                     lex_end_brace_def(end_brace_base_4_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_5_1 = config.space();
//     let carriage_new_line_base_5_1 = config.carriage_new_line();
//     let unix_new_line_base_5_1 = config.unix_new_line();
//     let start_paren_base_5_1 = config.start_paren();
//     let end_paren_base_5_1 = config.end_paren();
//     let not_base_5_1 = config.not();
//     let and_base_5_1 = config.and();
//     let or_base_5_1 = config.or();
//     let end_brace_base_5_1 = config.end_brace();
//     let base_tag_parse_5_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_5_1),
//                     lex_carriage_return_def(carriage_new_line_base_5_1),
//                     lex_unix_new_line_def(unix_new_line_base_5_1),
//                     lex_start_paren_def(start_paren_base_5_1),
//                     lex_end_paren_def(end_paren_base_5_1),
//                     lex_not_def(not_base_5_1),
//                     lex_and_def(and_base_5_1),
//                     lex_or_def(or_base_5_1),
//                     lex_end_brace_def(end_brace_base_5_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_6_1 = config.space();
//     let carriage_new_line_base_6_1 = config.carriage_new_line();
//     let unix_new_line_base_6_1 = config.unix_new_line();
//     let start_paren_base_6_1 = config.start_paren();
//     let end_paren_base_6_1 = config.end_paren();
//     let not_base_6_1 = config.not();
//     let and_base_6_1 = config.and();
//     let or_base_6_1 = config.or();
//     let end_brace_base_6_1 = config.end_brace();
//     let base_tag_parse_6_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_6_1),
//                     lex_carriage_return_def(carriage_new_line_base_6_1),
//                     lex_unix_new_line_def(unix_new_line_base_6_1),
//                     lex_start_paren_def(start_paren_base_6_1),
//                     lex_end_paren_def(end_paren_base_6_1),
//                     lex_not_def(not_base_6_1),
//                     lex_and_def(and_base_6_1),
//                     lex_or_def(or_base_6_1),
//                     lex_end_brace_def(end_brace_base_6_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_7_1 = config.space();
//     let carriage_new_line_base_7_1 = config.carriage_new_line();
//     let unix_new_line_base_7_1 = config.unix_new_line();
//     let start_paren_base_7_1 = config.start_paren();
//     let end_paren_base_7_1 = config.end_paren();
//     let not_base_7_1 = config.not();
//     let and_base_7_1 = config.and();
//     let or_base_7_1 = config.or();
//     let end_brace_base_7_1 = config.end_brace();
//     let base_tag_parse_7_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_7_1),
//                     lex_carriage_return_def(carriage_new_line_base_7_1),
//                     lex_unix_new_line_def(unix_new_line_base_7_1),
//                     lex_start_paren_def(start_paren_base_7_1),
//                     lex_end_paren_def(end_paren_base_7_1),
//                     lex_not_def(not_base_7_1),
//                     lex_and_def(and_base_7_1),
//                     lex_or_def(or_base_7_1),
//                     lex_end_brace_def(end_brace_base_7_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_8_1 = config.space();
//     let carriage_new_line_base_8_1 = config.carriage_new_line();
//     let unix_new_line_base_8_1 = config.unix_new_line();
//     let start_paren_base_8_1 = config.start_paren();
//     let end_paren_base_8_1 = config.end_paren();
//     let not_base_8_1 = config.not();
//     let and_base_8_1 = config.and();
//     let or_base_8_1 = config.or();
//     let end_brace_base_8_1 = config.end_brace();
//     let base_tag_parse_8_1 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_8_1),
//                     lex_carriage_return_def(carriage_new_line_base_8_1),
//                     lex_unix_new_line_def(unix_new_line_base_8_1),
//                     lex_start_paren_def(start_paren_base_8_1),
//                     lex_end_paren_def(end_paren_base_8_1),
//                     lex_not_def(not_base_8_1),
//                     lex_and_def(and_base_8_1),
//                     lex_or_def(or_base_8_1),
//                     lex_end_brace_def(end_brace_base_8_1),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let feature_parse_1 = || {
//         map(
//             tuple((
//                 lex_feature_def(config.feature_base()),
//                 start_brace_parse_0_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_0_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_not_parse_1 = || {
//         map(
//             tuple((
//                 lex_feature_not_def(config.feature_not()),
//                 start_brace_parse_1_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_1_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_case_parse_1 = || {
//         map(
//             tuple((
//                 lex_feature_case_def(config.feature_case()),
//                 start_brace_parse_2_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_2_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_parse_1 = || {
//         map(
//             tuple((
//                 lex_config_def(config.config_base()),
//                 start_brace_parse_3_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_3_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_not_parse_1 = || {
//         map(
//             tuple((
//                 lex_config_not_def(config.config_not()),
//                 start_brace_parse_4_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_4_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_case_parse_1 = || {
//         map(
//             tuple((
//                 lex_config_case_def(config.config_case()),
//                 start_brace_parse_5_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_5_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };

//     let config_group_parse_1 = || {
//         map(
//             tuple((
//                 lex_config_group_def(config.group_base()),
//                 start_brace_parse_6_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_6_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_not_parse_1 = || {
//         map(
//             tuple((
//                 lex_config_group_not_def(config.group_not()),
//                 start_brace_parse_7_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_7_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_case_parse_1 = || {
//         map(
//             tuple((
//                 lex_config_group_case_def(config.group_case()),
//                 start_brace_parse_8_1(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_8_1(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let space_0_2 = config.space();
//     let start_brace_0_2 = config.start_brace();
//     let start_brace_parse_0_2 = || {
//         many_till(
//             lex_space_def(space_0_2),
//             lex_start_brace_def(start_brace_0_2),
//         )
//     };
//     let space_1_2 = config.space();
//     let start_brace_1_2 = config.start_brace();
//     let start_brace_parse_1_2 = || {
//         many_till(
//             lex_space_def(space_1_2),
//             lex_start_brace_def(start_brace_1_2),
//         )
//     };
//     let space_2_2 = config.space();
//     let start_brace_2_2 = config.start_brace();
//     let start_brace_parse_2_2 = || {
//         many_till(
//             lex_space_def(space_2_2),
//             lex_start_brace_def(start_brace_2_2),
//         )
//     };
//     let space_3_2 = config.space();
//     let start_brace_3_2 = config.start_brace();
//     let start_brace_parse_3_2 = || {
//         many_till(
//             lex_space_def(space_3_2),
//             lex_start_brace_def(start_brace_3_2),
//         )
//     };
//     let space_4_2 = config.space();
//     let start_brace_4_2 = config.start_brace();
//     let start_brace_parse_4_2 = || {
//         many_till(
//             lex_space_def(space_4_2),
//             lex_start_brace_def(start_brace_4_2),
//         )
//     };
//     let space_5_2 = config.space();
//     let start_brace_5_2 = config.start_brace();
//     let start_brace_parse_5_2 = || {
//         many_till(
//             lex_space_def(space_5_2),
//             lex_start_brace_def(start_brace_5_2),
//         )
//     };
//     let space_6_2 = config.space();
//     let start_brace_6_2 = config.start_brace();
//     let start_brace_parse_6_2 = || {
//         many_till(
//             lex_space_def(space_6_2),
//             lex_start_brace_def(start_brace_6_2),
//         )
//     };
//     let space_7_2 = config.space();
//     let start_brace_7_2 = config.start_brace();
//     let start_brace_parse_7_2 = || {
//         many_till(
//             lex_space_def(space_7_2),
//             lex_start_brace_def(start_brace_7_2),
//         )
//     };
//     let space_8_2 = config.space();
//     let start_brace_8_2 = config.start_brace();
//     let start_brace_parse_8_2 = || {
//         many_till(
//             lex_space_def(space_8_2),
//             lex_start_brace_def(start_brace_8_2),
//         )
//     };
//     // let p = move |c: L| {
//     let space_base_0_2 = config.space();
//     let carriage_new_line_base_0_2 = config.carriage_new_line();
//     let unix_new_line_base_0_2 = config.unix_new_line();
//     let start_paren_base_0_2 = config.start_paren();
//     let end_paren_base_0_2 = config.end_paren();
//     let not_base_0_2 = config.not();
//     let and_base_0_2 = config.and();
//     let or_base_0_2 = config.or();
//     let end_brace_base_0_2 = config.end_brace();
//     let base_tag_parse_0_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_0_2),
//                     lex_carriage_return_def(carriage_new_line_base_0_2),
//                     lex_unix_new_line_def(unix_new_line_base_0_2),
//                     lex_start_paren_def(start_paren_base_0_2),
//                     lex_end_paren_def(end_paren_base_0_2),
//                     lex_not_def(not_base_0_2),
//                     lex_and_def(and_base_0_2),
//                     lex_or_def(or_base_0_2),
//                     lex_end_brace_def(end_brace_base_0_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_1_2 = config.space();
//     let carriage_new_line_base_1_2 = config.carriage_new_line();
//     let unix_new_line_base_1_2 = config.unix_new_line();
//     let start_paren_base_1_2 = config.start_paren();
//     let end_paren_base_1_2 = config.end_paren();
//     let not_base_1_2 = config.not();
//     let and_base_1_2 = config.and();
//     let or_base_1_2 = config.or();
//     let end_brace_base_1_2 = config.end_brace();
//     let base_tag_parse_1_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_1_2),
//                     lex_carriage_return_def(carriage_new_line_base_1_2),
//                     lex_unix_new_line_def(unix_new_line_base_1_2),
//                     lex_start_paren_def(start_paren_base_1_2),
//                     lex_end_paren_def(end_paren_base_1_2),
//                     lex_not_def(not_base_1_2),
//                     lex_and_def(and_base_1_2),
//                     lex_or_def(or_base_1_2),
//                     lex_end_brace_def(end_brace_base_1_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_2_2 = config.space();
//     let carriage_new_line_base_2_2 = config.carriage_new_line();
//     let unix_new_line_base_2_2 = config.unix_new_line();
//     let start_paren_base_2_2 = config.start_paren();
//     let end_paren_base_2_2 = config.end_paren();
//     let not_base_2_2 = config.not();
//     let and_base_2_2 = config.and();
//     let or_base_2_2 = config.or();
//     let end_brace_base_2_2 = config.end_brace();
//     let base_tag_parse_2_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_2_2),
//                     lex_carriage_return_def(carriage_new_line_base_2_2),
//                     lex_unix_new_line_def(unix_new_line_base_2_2),
//                     lex_start_paren_def(start_paren_base_2_2),
//                     lex_end_paren_def(end_paren_base_2_2),
//                     lex_not_def(not_base_2_2),
//                     lex_and_def(and_base_2_2),
//                     lex_or_def(or_base_2_2),
//                     lex_end_brace_def(end_brace_base_2_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_3_2 = config.space();
//     let carriage_new_line_base_3_2 = config.carriage_new_line();
//     let unix_new_line_base_3_2 = config.unix_new_line();
//     let start_paren_base_3_2 = config.start_paren();
//     let end_paren_base_3_2 = config.end_paren();
//     let not_base_3_2 = config.not();
//     let and_base_3_2 = config.and();
//     let or_base_3_2 = config.or();
//     let end_brace_base_3_2 = config.end_brace();
//     let base_tag_parse_3_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_3_2),
//                     lex_carriage_return_def(carriage_new_line_base_3_2),
//                     lex_unix_new_line_def(unix_new_line_base_3_2),
//                     lex_start_paren_def(start_paren_base_3_2),
//                     lex_end_paren_def(end_paren_base_3_2),
//                     lex_not_def(not_base_3_2),
//                     lex_and_def(and_base_3_2),
//                     lex_or_def(or_base_3_2),
//                     lex_end_brace_def(end_brace_base_3_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_4_2 = config.space();
//     let carriage_new_line_base_4_2 = config.carriage_new_line();
//     let unix_new_line_base_4_2 = config.unix_new_line();
//     let start_paren_base_4_2 = config.start_paren();
//     let end_paren_base_4_2 = config.end_paren();
//     let not_base_4_2 = config.not();
//     let and_base_4_2 = config.and();
//     let or_base_4_2 = config.or();
//     let end_brace_base_4_2 = config.end_brace();
//     let base_tag_parse_4_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_4_2),
//                     lex_carriage_return_def(carriage_new_line_base_4_2),
//                     lex_unix_new_line_def(unix_new_line_base_4_2),
//                     lex_start_paren_def(start_paren_base_4_2),
//                     lex_end_paren_def(end_paren_base_4_2),
//                     lex_not_def(not_base_4_2),
//                     lex_and_def(and_base_4_2),
//                     lex_or_def(or_base_4_2),
//                     lex_end_brace_def(end_brace_base_4_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_5_2 = config.space();
//     let carriage_new_line_base_5_2 = config.carriage_new_line();
//     let unix_new_line_base_5_2 = config.unix_new_line();
//     let start_paren_base_5_2 = config.start_paren();
//     let end_paren_base_5_2 = config.end_paren();
//     let not_base_5_2 = config.not();
//     let and_base_5_2 = config.and();
//     let or_base_5_2 = config.or();
//     let end_brace_base_5_2 = config.end_brace();
//     let base_tag_parse_5_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_5_2),
//                     lex_carriage_return_def(carriage_new_line_base_5_2),
//                     lex_unix_new_line_def(unix_new_line_base_5_2),
//                     lex_start_paren_def(start_paren_base_5_2),
//                     lex_end_paren_def(end_paren_base_5_2),
//                     lex_not_def(not_base_5_2),
//                     lex_and_def(and_base_5_2),
//                     lex_or_def(or_base_5_2),
//                     lex_end_brace_def(end_brace_base_5_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_6_2 = config.space();
//     let carriage_new_line_base_6_2 = config.carriage_new_line();
//     let unix_new_line_base_6_2 = config.unix_new_line();
//     let start_paren_base_6_2 = config.start_paren();
//     let end_paren_base_6_2 = config.end_paren();
//     let not_base_6_2 = config.not();
//     let and_base_6_2 = config.and();
//     let or_base_6_2 = config.or();
//     let end_brace_base_6_2 = config.end_brace();
//     let base_tag_parse_6_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_6_2),
//                     lex_carriage_return_def(carriage_new_line_base_6_2),
//                     lex_unix_new_line_def(unix_new_line_base_6_2),
//                     lex_start_paren_def(start_paren_base_6_2),
//                     lex_end_paren_def(end_paren_base_6_2),
//                     lex_not_def(not_base_6_2),
//                     lex_and_def(and_base_6_2),
//                     lex_or_def(or_base_6_2),
//                     lex_end_brace_def(end_brace_base_6_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_7_2 = config.space();
//     let carriage_new_line_base_7_2 = config.carriage_new_line();
//     let unix_new_line_base_7_2 = config.unix_new_line();
//     let start_paren_base_7_2 = config.start_paren();
//     let end_paren_base_7_2 = config.end_paren();
//     let not_base_7_2 = config.not();
//     let and_base_7_2 = config.and();
//     let or_base_7_2 = config.or();
//     let end_brace_base_7_2 = config.end_brace();
//     let base_tag_parse_7_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_7_2),
//                     lex_carriage_return_def(carriage_new_line_base_7_2),
//                     lex_unix_new_line_def(unix_new_line_base_7_2),
//                     lex_start_paren_def(start_paren_base_7_2),
//                     lex_end_paren_def(end_paren_base_7_2),
//                     lex_not_def(not_base_7_2),
//                     lex_and_def(and_base_7_2),
//                     lex_or_def(or_base_7_2),
//                     lex_end_brace_def(end_brace_base_7_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_8_2 = config.space();
//     let carriage_new_line_base_8_2 = config.carriage_new_line();
//     let unix_new_line_base_8_2 = config.unix_new_line();
//     let start_paren_base_8_2 = config.start_paren();
//     let end_paren_base_8_2 = config.end_paren();
//     let not_base_8_2 = config.not();
//     let and_base_8_2 = config.and();
//     let or_base_8_2 = config.or();
//     let end_brace_base_8_2 = config.end_brace();
//     let base_tag_parse_8_2 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_8_2),
//                     lex_carriage_return_def(carriage_new_line_base_8_2),
//                     lex_unix_new_line_def(unix_new_line_base_8_2),
//                     lex_start_paren_def(start_paren_base_8_2),
//                     lex_end_paren_def(end_paren_base_8_2),
//                     lex_not_def(not_base_8_2),
//                     lex_and_def(and_base_8_2),
//                     lex_or_def(or_base_8_2),
//                     lex_end_brace_def(end_brace_base_8_2),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let feature_parse_2 = || {
//         map(
//             tuple((
//                 lex_feature_def(config.feature_base()),
//                 start_brace_parse_0_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_0_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_not_parse_2 = || {
//         map(
//             tuple((
//                 lex_feature_not_def(config.feature_not()),
//                 start_brace_parse_1_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_1_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_case_parse_2 = || {
//         map(
//             tuple((
//                 lex_feature_case_def(config.feature_case()),
//                 start_brace_parse_2_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_2_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_parse_2 = || {
//         map(
//             tuple((
//                 lex_config_def(config.config_base()),
//                 start_brace_parse_3_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_3_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_not_parse_2 = || {
//         map(
//             tuple((
//                 lex_config_not_def(config.config_not()),
//                 start_brace_parse_4_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_4_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_case_parse_2 = || {
//         map(
//             tuple((
//                 lex_config_case_def(config.config_case()),
//                 start_brace_parse_5_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_5_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };

//     let config_group_parse_2 = || {
//         map(
//             tuple((
//                 lex_config_group_def(config.group_base()),
//                 start_brace_parse_6_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_6_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_not_parse_2 = || {
//         map(
//             tuple((
//                 lex_config_group_not_def(config.group_not()),
//                 start_brace_parse_7_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_7_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_case_parse_2 = || {
//         map(
//             tuple((
//                 lex_config_group_case_def(config.group_case()),
//                 start_brace_parse_8_2(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_8_2(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let space_0_4 = config.space();
//     let start_brace_0_4 = config.start_brace();
//     let start_brace_parse_0_4 = || {
//         many_till(
//             lex_space_def(space_0_4),
//             lex_start_brace_def(start_brace_0_4),
//         )
//     };
//     let space_1_4 = config.space();
//     let start_brace_1_4 = config.start_brace();
//     let start_brace_parse_1_4 = || {
//         many_till(
//             lex_space_def(space_1_4),
//             lex_start_brace_def(start_brace_1_4),
//         )
//     };
//     let space_2_4 = config.space();
//     let start_brace_2_4 = config.start_brace();
//     let start_brace_parse_2_4 = || {
//         many_till(
//             lex_space_def(space_2_4),
//             lex_start_brace_def(start_brace_2_4),
//         )
//     };
//     let space_3_4 = config.space();
//     let start_brace_3_4 = config.start_brace();
//     let start_brace_parse_3_4 = || {
//         many_till(
//             lex_space_def(space_3_4),
//             lex_start_brace_def(start_brace_3_4),
//         )
//     };
//     let space_4_4 = config.space();
//     let start_brace_4_4 = config.start_brace();
//     let start_brace_parse_4_4 = || {
//         many_till(
//             lex_space_def(space_4_4),
//             lex_start_brace_def(start_brace_4_4),
//         )
//     };
//     let space_5_4 = config.space();
//     let start_brace_5_4 = config.start_brace();
//     let start_brace_parse_5_4 = || {
//         many_till(
//             lex_space_def(space_5_4),
//             lex_start_brace_def(start_brace_5_4),
//         )
//     };
//     let space_6_4 = config.space();
//     let start_brace_6_4 = config.start_brace();
//     let start_brace_parse_6_4 = || {
//         many_till(
//             lex_space_def(space_6_4),
//             lex_start_brace_def(start_brace_6_4),
//         )
//     };
//     let space_7_4 = config.space();
//     let start_brace_7_4 = config.start_brace();
//     let start_brace_parse_7_4 = || {
//         many_till(
//             lex_space_def(space_7_4),
//             lex_start_brace_def(start_brace_7_4),
//         )
//     };
//     let space_8_4 = config.space();
//     let start_brace_8_4 = config.start_brace();
//     let start_brace_parse_8_4 = || {
//         many_till(
//             lex_space_def(space_8_4),
//             lex_start_brace_def(start_brace_8_4),
//         )
//     };
//     // let p = move |c: L| {
//     let space_base_0_4 = config.space();
//     let carriage_new_line_base_0_4 = config.carriage_new_line();
//     let unix_new_line_base_0_4 = config.unix_new_line();
//     let start_paren_base_0_4 = config.start_paren();
//     let end_paren_base_0_4 = config.end_paren();
//     let not_base_0_4 = config.not();
//     let and_base_0_4 = config.and();
//     let or_base_0_4 = config.or();
//     let end_brace_base_0_4 = config.end_brace();
//     let base_tag_parse_0_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_0_4),
//                     lex_carriage_return_def(carriage_new_line_base_0_4),
//                     lex_unix_new_line_def(unix_new_line_base_0_4),
//                     lex_start_paren_def(start_paren_base_0_4),
//                     lex_end_paren_def(end_paren_base_0_4),
//                     lex_not_def(not_base_0_4),
//                     lex_and_def(and_base_0_4),
//                     lex_or_def(or_base_0_4),
//                     lex_end_brace_def(end_brace_base_0_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_1_4 = config.space();
//     let carriage_new_line_base_1_4 = config.carriage_new_line();
//     let unix_new_line_base_1_4 = config.unix_new_line();
//     let start_paren_base_1_4 = config.start_paren();
//     let end_paren_base_1_4 = config.end_paren();
//     let not_base_1_4 = config.not();
//     let and_base_1_4 = config.and();
//     let or_base_1_4 = config.or();
//     let end_brace_base_1_4 = config.end_brace();
//     let base_tag_parse_1_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_1_4),
//                     lex_carriage_return_def(carriage_new_line_base_1_4),
//                     lex_unix_new_line_def(unix_new_line_base_1_4),
//                     lex_start_paren_def(start_paren_base_1_4),
//                     lex_end_paren_def(end_paren_base_1_4),
//                     lex_not_def(not_base_1_4),
//                     lex_and_def(and_base_1_4),
//                     lex_or_def(or_base_1_4),
//                     lex_end_brace_def(end_brace_base_1_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_2_4 = config.space();
//     let carriage_new_line_base_2_4 = config.carriage_new_line();
//     let unix_new_line_base_2_4 = config.unix_new_line();
//     let start_paren_base_2_4 = config.start_paren();
//     let end_paren_base_2_4 = config.end_paren();
//     let not_base_2_4 = config.not();
//     let and_base_2_4 = config.and();
//     let or_base_2_4 = config.or();
//     let end_brace_base_2_4 = config.end_brace();
//     let base_tag_parse_2_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_2_4),
//                     lex_carriage_return_def(carriage_new_line_base_2_4),
//                     lex_unix_new_line_def(unix_new_line_base_2_4),
//                     lex_start_paren_def(start_paren_base_2_4),
//                     lex_end_paren_def(end_paren_base_2_4),
//                     lex_not_def(not_base_2_4),
//                     lex_and_def(and_base_2_4),
//                     lex_or_def(or_base_2_4),
//                     lex_end_brace_def(end_brace_base_2_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_3_4 = config.space();
//     let carriage_new_line_base_3_4 = config.carriage_new_line();
//     let unix_new_line_base_3_4 = config.unix_new_line();
//     let start_paren_base_3_4 = config.start_paren();
//     let end_paren_base_3_4 = config.end_paren();
//     let not_base_3_4 = config.not();
//     let and_base_3_4 = config.and();
//     let or_base_3_4 = config.or();
//     let end_brace_base_3_4 = config.end_brace();
//     let base_tag_parse_3_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_3_4),
//                     lex_carriage_return_def(carriage_new_line_base_3_4),
//                     lex_unix_new_line_def(unix_new_line_base_3_4),
//                     lex_start_paren_def(start_paren_base_3_4),
//                     lex_end_paren_def(end_paren_base_3_4),
//                     lex_not_def(not_base_3_4),
//                     lex_and_def(and_base_3_4),
//                     lex_or_def(or_base_3_4),
//                     lex_end_brace_def(end_brace_base_3_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_4_4 = config.space();
//     let carriage_new_line_base_4_4 = config.carriage_new_line();
//     let unix_new_line_base_4_4 = config.unix_new_line();
//     let start_paren_base_4_4 = config.start_paren();
//     let end_paren_base_4_4 = config.end_paren();
//     let not_base_4_4 = config.not();
//     let and_base_4_4 = config.and();
//     let or_base_4_4 = config.or();
//     let end_brace_base_4_4 = config.end_brace();
//     let base_tag_parse_4_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_4_4),
//                     lex_carriage_return_def(carriage_new_line_base_4_4),
//                     lex_unix_new_line_def(unix_new_line_base_4_4),
//                     lex_start_paren_def(start_paren_base_4_4),
//                     lex_end_paren_def(end_paren_base_4_4),
//                     lex_not_def(not_base_4_4),
//                     lex_and_def(and_base_4_4),
//                     lex_or_def(or_base_4_4),
//                     lex_end_brace_def(end_brace_base_4_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_5_4 = config.space();
//     let carriage_new_line_base_5_4 = config.carriage_new_line();
//     let unix_new_line_base_5_4 = config.unix_new_line();
//     let start_paren_base_5_4 = config.start_paren();
//     let end_paren_base_5_4 = config.end_paren();
//     let not_base_5_4 = config.not();
//     let and_base_5_4 = config.and();
//     let or_base_5_4 = config.or();
//     let end_brace_base_5_4 = config.end_brace();
//     let base_tag_parse_5_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_5_4),
//                     lex_carriage_return_def(carriage_new_line_base_5_4),
//                     lex_unix_new_line_def(unix_new_line_base_5_4),
//                     lex_start_paren_def(start_paren_base_5_4),
//                     lex_end_paren_def(end_paren_base_5_4),
//                     lex_not_def(not_base_5_4),
//                     lex_and_def(and_base_5_4),
//                     lex_or_def(or_base_5_4),
//                     lex_end_brace_def(end_brace_base_5_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_6_4 = config.space();
//     let carriage_new_line_base_6_4 = config.carriage_new_line();
//     let unix_new_line_base_6_4 = config.unix_new_line();
//     let start_paren_base_6_4 = config.start_paren();
//     let end_paren_base_6_4 = config.end_paren();
//     let not_base_6_4 = config.not();
//     let and_base_6_4 = config.and();
//     let or_base_6_4 = config.or();
//     let end_brace_base_6_4 = config.end_brace();
//     let base_tag_parse_6_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_6_4),
//                     lex_carriage_return_def(carriage_new_line_base_6_4),
//                     lex_unix_new_line_def(unix_new_line_base_6_4),
//                     lex_start_paren_def(start_paren_base_6_4),
//                     lex_end_paren_def(end_paren_base_6_4),
//                     lex_not_def(not_base_6_4),
//                     lex_and_def(and_base_6_4),
//                     lex_or_def(or_base_6_4),
//                     lex_end_brace_def(end_brace_base_6_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_7_4 = config.space();
//     let carriage_new_line_base_7_4 = config.carriage_new_line();
//     let unix_new_line_base_7_4 = config.unix_new_line();
//     let start_paren_base_7_4 = config.start_paren();
//     let end_paren_base_7_4 = config.end_paren();
//     let not_base_7_4 = config.not();
//     let and_base_7_4 = config.and();
//     let or_base_7_4 = config.or();
//     let end_brace_base_7_4 = config.end_brace();
//     let base_tag_parse_7_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_7_4),
//                     lex_carriage_return_def(carriage_new_line_base_7_4),
//                     lex_unix_new_line_def(unix_new_line_base_7_4),
//                     lex_start_paren_def(start_paren_base_7_4),
//                     lex_end_paren_def(end_paren_base_7_4),
//                     lex_not_def(not_base_7_4),
//                     lex_and_def(and_base_7_4),
//                     lex_or_def(or_base_7_4),
//                     lex_end_brace_def(end_brace_base_7_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_8_4 = config.space();
//     let carriage_new_line_base_8_4 = config.carriage_new_line();
//     let unix_new_line_base_8_4 = config.unix_new_line();
//     let start_paren_base_8_4 = config.start_paren();
//     let end_paren_base_8_4 = config.end_paren();
//     let not_base_8_4 = config.not();
//     let and_base_8_4 = config.and();
//     let or_base_8_4 = config.or();
//     let end_brace_base_8_4 = config.end_brace();
//     let base_tag_parse_8_4 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_8_4),
//                     lex_carriage_return_def(carriage_new_line_base_8_4),
//                     lex_unix_new_line_def(unix_new_line_base_8_4),
//                     lex_start_paren_def(start_paren_base_8_4),
//                     lex_end_paren_def(end_paren_base_8_4),
//                     lex_not_def(not_base_8_4),
//                     lex_and_def(and_base_8_4),
//                     lex_or_def(or_base_8_4),
//                     lex_end_brace_def(end_brace_base_8_4),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let feature_parse_4 = || {
//         map(
//             tuple((
//                 lex_feature_def(config.feature_base()),
//                 start_brace_parse_0_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_0_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_not_parse_4 = || {
//         map(
//             tuple((
//                 lex_feature_not_def(config.feature_not()),
//                 start_brace_parse_1_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_1_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_case_parse_4 = || {
//         map(
//             tuple((
//                 lex_feature_case_def(config.feature_case()),
//                 start_brace_parse_2_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_2_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_parse_4 = || {
//         map(
//             tuple((
//                 lex_config_def(config.config_base()),
//                 start_brace_parse_3_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_3_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_not_parse_4 = || {
//         map(
//             tuple((
//                 lex_config_not_def(config.config_not()),
//                 start_brace_parse_4_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_4_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_case_parse_4 = || {
//         map(
//             tuple((
//                 lex_config_case_def(config.config_case()),
//                 start_brace_parse_5_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_5_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };

//     let config_group_parse_4 = || {
//         map(
//             tuple((
//                 lex_config_group_def(config.group_base()),
//                 start_brace_parse_6_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_6_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_not_parse_4 = || {
//         map(
//             tuple((
//                 lex_config_group_not_def(config.group_not()),
//                 start_brace_parse_7_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_7_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_case_parse_4 = || {
//         map(
//             tuple((
//                 lex_config_group_case_def(config.group_case()),
//                 start_brace_parse_8_4(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_8_4(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let space_0_5 = config.space();
//     let start_brace_0_5 = config.start_brace();
//     let start_brace_parse_0_5 = || {
//         many_till(
//             lex_space_def(space_0_5),
//             lex_start_brace_def(start_brace_0_5),
//         )
//     };
//     let space_1_5 = config.space();
//     let start_brace_1_5 = config.start_brace();
//     let start_brace_parse_1_5 = || {
//         many_till(
//             lex_space_def(space_1_5),
//             lex_start_brace_def(start_brace_1_5),
//         )
//     };
//     let space_2_5 = config.space();
//     let start_brace_2_5 = config.start_brace();
//     let start_brace_parse_2_5 = || {
//         many_till(
//             lex_space_def(space_2_5),
//             lex_start_brace_def(start_brace_2_5),
//         )
//     };
//     let space_3_5 = config.space();
//     let start_brace_3_5 = config.start_brace();
//     let start_brace_parse_3_5 = || {
//         many_till(
//             lex_space_def(space_3_5),
//             lex_start_brace_def(start_brace_3_5),
//         )
//     };
//     let space_4_5 = config.space();
//     let start_brace_4_5 = config.start_brace();
//     let start_brace_parse_4_5 = || {
//         many_till(
//             lex_space_def(space_4_5),
//             lex_start_brace_def(start_brace_4_5),
//         )
//     };
//     let space_5_5 = config.space();
//     let start_brace_5_5 = config.start_brace();
//     let start_brace_parse_5_5 = || {
//         many_till(
//             lex_space_def(space_5_5),
//             lex_start_brace_def(start_brace_5_5),
//         )
//     };
//     let space_6_5 = config.space();
//     let start_brace_6_5 = config.start_brace();
//     let start_brace_parse_6_5 = || {
//         many_till(
//             lex_space_def(space_6_5),
//             lex_start_brace_def(start_brace_6_5),
//         )
//     };
//     let space_7_5 = config.space();
//     let start_brace_7_5 = config.start_brace();
//     let start_brace_parse_7_5 = || {
//         many_till(
//             lex_space_def(space_7_5),
//             lex_start_brace_def(start_brace_7_5),
//         )
//     };
//     let space_8_5 = config.space();
//     let start_brace_8_5 = config.start_brace();
//     let start_brace_parse_8_5 = || {
//         many_till(
//             lex_space_def(space_8_5),
//             lex_start_brace_def(start_brace_8_5),
//         )
//     };
//     // let p = move |c: L| {
//     let space_base_0_5 = config.space();
//     let carriage_new_line_base_0_5 = config.carriage_new_line();
//     let unix_new_line_base_0_5 = config.unix_new_line();
//     let start_paren_base_0_5 = config.start_paren();
//     let end_paren_base_0_5 = config.end_paren();
//     let not_base_0_5 = config.not();
//     let and_base_0_5 = config.and();
//     let or_base_0_5 = config.or();
//     let end_brace_base_0_5 = config.end_brace();
//     let base_tag_parse_0_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_0_5),
//                     lex_carriage_return_def(carriage_new_line_base_0_5),
//                     lex_unix_new_line_def(unix_new_line_base_0_5),
//                     lex_start_paren_def(start_paren_base_0_5),
//                     lex_end_paren_def(end_paren_base_0_5),
//                     lex_not_def(not_base_0_5),
//                     lex_and_def(and_base_0_5),
//                     lex_or_def(or_base_0_5),
//                     lex_end_brace_def(end_brace_base_0_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_1_5 = config.space();
//     let carriage_new_line_base_1_5 = config.carriage_new_line();
//     let unix_new_line_base_1_5 = config.unix_new_line();
//     let start_paren_base_1_5 = config.start_paren();
//     let end_paren_base_1_5 = config.end_paren();
//     let not_base_1_5 = config.not();
//     let and_base_1_5 = config.and();
//     let or_base_1_5 = config.or();
//     let end_brace_base_1_5 = config.end_brace();
//     let base_tag_parse_1_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_1_5),
//                     lex_carriage_return_def(carriage_new_line_base_1_5),
//                     lex_unix_new_line_def(unix_new_line_base_1_5),
//                     lex_start_paren_def(start_paren_base_1_5),
//                     lex_end_paren_def(end_paren_base_1_5),
//                     lex_not_def(not_base_1_5),
//                     lex_and_def(and_base_1_5),
//                     lex_or_def(or_base_1_5),
//                     lex_end_brace_def(end_brace_base_1_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_2_5 = config.space();
//     let carriage_new_line_base_2_5 = config.carriage_new_line();
//     let unix_new_line_base_2_5 = config.unix_new_line();
//     let start_paren_base_2_5 = config.start_paren();
//     let end_paren_base_2_5 = config.end_paren();
//     let not_base_2_5 = config.not();
//     let and_base_2_5 = config.and();
//     let or_base_2_5 = config.or();
//     let end_brace_base_2_5 = config.end_brace();
//     let base_tag_parse_2_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_2_5),
//                     lex_carriage_return_def(carriage_new_line_base_2_5),
//                     lex_unix_new_line_def(unix_new_line_base_2_5),
//                     lex_start_paren_def(start_paren_base_2_5),
//                     lex_end_paren_def(end_paren_base_2_5),
//                     lex_not_def(not_base_2_5),
//                     lex_and_def(and_base_2_5),
//                     lex_or_def(or_base_2_5),
//                     lex_end_brace_def(end_brace_base_2_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_3_5 = config.space();
//     let carriage_new_line_base_3_5 = config.carriage_new_line();
//     let unix_new_line_base_3_5 = config.unix_new_line();
//     let start_paren_base_3_5 = config.start_paren();
//     let end_paren_base_3_5 = config.end_paren();
//     let not_base_3_5 = config.not();
//     let and_base_3_5 = config.and();
//     let or_base_3_5 = config.or();
//     let end_brace_base_3_5 = config.end_brace();
//     let base_tag_parse_3_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_3_5),
//                     lex_carriage_return_def(carriage_new_line_base_3_5),
//                     lex_unix_new_line_def(unix_new_line_base_3_5),
//                     lex_start_paren_def(start_paren_base_3_5),
//                     lex_end_paren_def(end_paren_base_3_5),
//                     lex_not_def(not_base_3_5),
//                     lex_and_def(and_base_3_5),
//                     lex_or_def(or_base_3_5),
//                     lex_end_brace_def(end_brace_base_3_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_4_5 = config.space();
//     let carriage_new_line_base_4_5 = config.carriage_new_line();
//     let unix_new_line_base_4_5 = config.unix_new_line();
//     let start_paren_base_4_5 = config.start_paren();
//     let end_paren_base_4_5 = config.end_paren();
//     let not_base_4_5 = config.not();
//     let and_base_4_5 = config.and();
//     let or_base_4_5 = config.or();
//     let end_brace_base_4_5 = config.end_brace();
//     let base_tag_parse_4_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_4_5),
//                     lex_carriage_return_def(carriage_new_line_base_4_5),
//                     lex_unix_new_line_def(unix_new_line_base_4_5),
//                     lex_start_paren_def(start_paren_base_4_5),
//                     lex_end_paren_def(end_paren_base_4_5),
//                     lex_not_def(not_base_4_5),
//                     lex_and_def(and_base_4_5),
//                     lex_or_def(or_base_4_5),
//                     lex_end_brace_def(end_brace_base_4_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_5_5 = config.space();
//     let carriage_new_line_base_5_5 = config.carriage_new_line();
//     let unix_new_line_base_5_5 = config.unix_new_line();
//     let start_paren_base_5_5 = config.start_paren();
//     let end_paren_base_5_5 = config.end_paren();
//     let not_base_5_5 = config.not();
//     let and_base_5_5 = config.and();
//     let or_base_5_5 = config.or();
//     let end_brace_base_5_5 = config.end_brace();
//     let base_tag_parse_5_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_5_5),
//                     lex_carriage_return_def(carriage_new_line_base_5_5),
//                     lex_unix_new_line_def(unix_new_line_base_5_5),
//                     lex_start_paren_def(start_paren_base_5_5),
//                     lex_end_paren_def(end_paren_base_5_5),
//                     lex_not_def(not_base_5_5),
//                     lex_and_def(and_base_5_5),
//                     lex_or_def(or_base_5_5),
//                     lex_end_brace_def(end_brace_base_5_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_6_5 = config.space();
//     let carriage_new_line_base_6_5 = config.carriage_new_line();
//     let unix_new_line_base_6_5 = config.unix_new_line();
//     let start_paren_base_6_5 = config.start_paren();
//     let end_paren_base_6_5 = config.end_paren();
//     let not_base_6_5 = config.not();
//     let and_base_6_5 = config.and();
//     let or_base_6_5 = config.or();
//     let end_brace_base_6_5 = config.end_brace();
//     let base_tag_parse_6_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_6_5),
//                     lex_carriage_return_def(carriage_new_line_base_6_5),
//                     lex_unix_new_line_def(unix_new_line_base_6_5),
//                     lex_start_paren_def(start_paren_base_6_5),
//                     lex_end_paren_def(end_paren_base_6_5),
//                     lex_not_def(not_base_6_5),
//                     lex_and_def(and_base_6_5),
//                     lex_or_def(or_base_6_5),
//                     lex_end_brace_def(end_brace_base_6_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_7_5 = config.space();
//     let carriage_new_line_base_7_5 = config.carriage_new_line();
//     let unix_new_line_base_7_5 = config.unix_new_line();
//     let start_paren_base_7_5 = config.start_paren();
//     let end_paren_base_7_5 = config.end_paren();
//     let not_base_7_5 = config.not();
//     let and_base_7_5 = config.and();
//     let or_base_7_5 = config.or();
//     let end_brace_base_7_5 = config.end_brace();
//     let base_tag_parse_7_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_7_5),
//                     lex_carriage_return_def(carriage_new_line_base_7_5),
//                     lex_unix_new_line_def(unix_new_line_base_7_5),
//                     lex_start_paren_def(start_paren_base_7_5),
//                     lex_end_paren_def(end_paren_base_7_5),
//                     lex_not_def(not_base_7_5),
//                     lex_and_def(and_base_7_5),
//                     lex_or_def(or_base_7_5),
//                     lex_end_brace_def(end_brace_base_7_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_8_5 = config.space();
//     let carriage_new_line_base_8_5 = config.carriage_new_line();
//     let unix_new_line_base_8_5 = config.unix_new_line();
//     let start_paren_base_8_5 = config.start_paren();
//     let end_paren_base_8_5 = config.end_paren();
//     let not_base_8_5 = config.not();
//     let and_base_8_5 = config.and();
//     let or_base_8_5 = config.or();
//     let end_brace_base_8_5 = config.end_brace();
//     let base_tag_parse_8_5 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_8_5),
//                     lex_carriage_return_def(carriage_new_line_base_8_5),
//                     lex_unix_new_line_def(unix_new_line_base_8_5),
//                     lex_start_paren_def(start_paren_base_8_5),
//                     lex_end_paren_def(end_paren_base_8_5),
//                     lex_not_def(not_base_8_5),
//                     lex_and_def(and_base_8_5),
//                     lex_or_def(or_base_8_5),
//                     lex_end_brace_def(end_brace_base_8_5),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let feature_parse_5 = || {
//         map(
//             tuple((
//                 lex_feature_def(config.feature_base()),
//                 start_brace_parse_0_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_0_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_not_parse_5 = || {
//         map(
//             tuple((
//                 lex_feature_not_def(config.feature_not()),
//                 start_brace_parse_1_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_1_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_case_parse_5 = || {
//         map(
//             tuple((
//                 lex_feature_case_def(config.feature_case()),
//                 start_brace_parse_2_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_2_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_parse_5 = || {
//         map(
//             tuple((
//                 lex_config_def(config.config_base()),
//                 start_brace_parse_3_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_3_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_not_parse_5 = || {
//         map(
//             tuple((
//                 lex_config_not_def(config.config_not()),
//                 start_brace_parse_4_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_4_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_case_parse_5 = || {
//         map(
//             tuple((
//                 lex_config_case_def(config.config_case()),
//                 start_brace_parse_5_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_5_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };

//     let config_group_parse_5 = || {
//         map(
//             tuple((
//                 lex_config_group_def(config.group_base()),
//                 start_brace_parse_6_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_6_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_not_parse_5 = || {
//         map(
//             tuple((
//                 lex_config_group_not_def(config.group_not()),
//                 start_brace_parse_7_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_7_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_case_parse_5 = || {
//         map(
//             tuple((
//                 lex_config_group_case_def(config.group_case()),
//                 start_brace_parse_8_5(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_8_5(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let space_0_6 = config.space();
//     let start_brace_0_6 = config.start_brace();
//     let start_brace_parse_0_6 = || {
//         many_till(
//             lex_space_def(space_0_6),
//             lex_start_brace_def(start_brace_0_6),
//         )
//     };
//     let space_1_6 = config.space();
//     let start_brace_1_6 = config.start_brace();
//     let start_brace_parse_1_6 = || {
//         many_till(
//             lex_space_def(space_1_6),
//             lex_start_brace_def(start_brace_1_6),
//         )
//     };
//     let space_2_6 = config.space();
//     let start_brace_2_6 = config.start_brace();
//     let start_brace_parse_2_6 = || {
//         many_till(
//             lex_space_def(space_2_6),
//             lex_start_brace_def(start_brace_2_6),
//         )
//     };
//     let space_3_6 = config.space();
//     let start_brace_3_6 = config.start_brace();
//     let start_brace_parse_3_6 = || {
//         many_till(
//             lex_space_def(space_3_6),
//             lex_start_brace_def(start_brace_3_6),
//         )
//     };
//     let space_4_6 = config.space();
//     let start_brace_4_6 = config.start_brace();
//     let start_brace_parse_4_6 = || {
//         many_till(
//             lex_space_def(space_4_6),
//             lex_start_brace_def(start_brace_4_6),
//         )
//     };
//     let space_5_6 = config.space();
//     let start_brace_5_6 = config.start_brace();
//     let start_brace_parse_5_6 = || {
//         many_till(
//             lex_space_def(space_5_6),
//             lex_start_brace_def(start_brace_5_6),
//         )
//     };
//     let space_6_6 = config.space();
//     let start_brace_6_6 = config.start_brace();
//     let start_brace_parse_6_6 = || {
//         many_till(
//             lex_space_def(space_6_6),
//             lex_start_brace_def(start_brace_6_6),
//         )
//     };
//     let space_7_6 = config.space();
//     let start_brace_7_6 = config.start_brace();
//     let start_brace_parse_7_6 = || {
//         many_till(
//             lex_space_def(space_7_6),
//             lex_start_brace_def(start_brace_7_6),
//         )
//     };
//     let space_8_6 = config.space();
//     let start_brace_8_6 = config.start_brace();
//     let start_brace_parse_8_6 = || {
//         many_till(
//             lex_space_def(space_8_6),
//             lex_start_brace_def(start_brace_8_6),
//         )
//     };
//     // let p = move |c: L| {
//     let space_base_0_6 = config.space();
//     let carriage_new_line_base_0_6 = config.carriage_new_line();
//     let unix_new_line_base_0_6 = config.unix_new_line();
//     let start_paren_base_0_6 = config.start_paren();
//     let end_paren_base_0_6 = config.end_paren();
//     let not_base_0_6 = config.not();
//     let and_base_0_6 = config.and();
//     let or_base_0_6 = config.or();
//     let end_brace_base_0_6 = config.end_brace();
//     let base_tag_parse_0_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_0_6),
//                     lex_carriage_return_def(carriage_new_line_base_0_6),
//                     lex_unix_new_line_def(unix_new_line_base_0_6),
//                     lex_start_paren_def(start_paren_base_0_6),
//                     lex_end_paren_def(end_paren_base_0_6),
//                     lex_not_def(not_base_0_6),
//                     lex_and_def(and_base_0_6),
//                     lex_or_def(or_base_0_6),
//                     lex_end_brace_def(end_brace_base_0_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_1_6 = config.space();
//     let carriage_new_line_base_1_6 = config.carriage_new_line();
//     let unix_new_line_base_1_6 = config.unix_new_line();
//     let start_paren_base_1_6 = config.start_paren();
//     let end_paren_base_1_6 = config.end_paren();
//     let not_base_1_6 = config.not();
//     let and_base_1_6 = config.and();
//     let or_base_1_6 = config.or();
//     let end_brace_base_1_6 = config.end_brace();
//     let base_tag_parse_1_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_1_6),
//                     lex_carriage_return_def(carriage_new_line_base_1_6),
//                     lex_unix_new_line_def(unix_new_line_base_1_6),
//                     lex_start_paren_def(start_paren_base_1_6),
//                     lex_end_paren_def(end_paren_base_1_6),
//                     lex_not_def(not_base_1_6),
//                     lex_and_def(and_base_1_6),
//                     lex_or_def(or_base_1_6),
//                     lex_end_brace_def(end_brace_base_1_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_2_6 = config.space();
//     let carriage_new_line_base_2_6 = config.carriage_new_line();
//     let unix_new_line_base_2_6 = config.unix_new_line();
//     let start_paren_base_2_6 = config.start_paren();
//     let end_paren_base_2_6 = config.end_paren();
//     let not_base_2_6 = config.not();
//     let and_base_2_6 = config.and();
//     let or_base_2_6 = config.or();
//     let end_brace_base_2_6 = config.end_brace();
//     let base_tag_parse_2_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_2_6),
//                     lex_carriage_return_def(carriage_new_line_base_2_6),
//                     lex_unix_new_line_def(unix_new_line_base_2_6),
//                     lex_start_paren_def(start_paren_base_2_6),
//                     lex_end_paren_def(end_paren_base_2_6),
//                     lex_not_def(not_base_2_6),
//                     lex_and_def(and_base_2_6),
//                     lex_or_def(or_base_2_6),
//                     lex_end_brace_def(end_brace_base_2_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_3_6 = config.space();
//     let carriage_new_line_base_3_6 = config.carriage_new_line();
//     let unix_new_line_base_3_6 = config.unix_new_line();
//     let start_paren_base_3_6 = config.start_paren();
//     let end_paren_base_3_6 = config.end_paren();
//     let not_base_3_6 = config.not();
//     let and_base_3_6 = config.and();
//     let or_base_3_6 = config.or();
//     let end_brace_base_3_6 = config.end_brace();
//     let base_tag_parse_3_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_3_6),
//                     lex_carriage_return_def(carriage_new_line_base_3_6),
//                     lex_unix_new_line_def(unix_new_line_base_3_6),
//                     lex_start_paren_def(start_paren_base_3_6),
//                     lex_end_paren_def(end_paren_base_3_6),
//                     lex_not_def(not_base_3_6),
//                     lex_and_def(and_base_3_6),
//                     lex_or_def(or_base_3_6),
//                     lex_end_brace_def(end_brace_base_3_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_4_6 = config.space();
//     let carriage_new_line_base_4_6 = config.carriage_new_line();
//     let unix_new_line_base_4_6 = config.unix_new_line();
//     let start_paren_base_4_6 = config.start_paren();
//     let end_paren_base_4_6 = config.end_paren();
//     let not_base_4_6 = config.not();
//     let and_base_4_6 = config.and();
//     let or_base_4_6 = config.or();
//     let end_brace_base_4_6 = config.end_brace();
//     let base_tag_parse_4_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_4_6),
//                     lex_carriage_return_def(carriage_new_line_base_4_6),
//                     lex_unix_new_line_def(unix_new_line_base_4_6),
//                     lex_start_paren_def(start_paren_base_4_6),
//                     lex_end_paren_def(end_paren_base_4_6),
//                     lex_not_def(not_base_4_6),
//                     lex_and_def(and_base_4_6),
//                     lex_or_def(or_base_4_6),
//                     lex_end_brace_def(end_brace_base_4_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_5_6 = config.space();
//     let carriage_new_line_base_5_6 = config.carriage_new_line();
//     let unix_new_line_base_5_6 = config.unix_new_line();
//     let start_paren_base_5_6 = config.start_paren();
//     let end_paren_base_5_6 = config.end_paren();
//     let not_base_5_6 = config.not();
//     let and_base_5_6 = config.and();
//     let or_base_5_6 = config.or();
//     let end_brace_base_5_6 = config.end_brace();
//     let base_tag_parse_5_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_5_6),
//                     lex_carriage_return_def(carriage_new_line_base_5_6),
//                     lex_unix_new_line_def(unix_new_line_base_5_6),
//                     lex_start_paren_def(start_paren_base_5_6),
//                     lex_end_paren_def(end_paren_base_5_6),
//                     lex_not_def(not_base_5_6),
//                     lex_and_def(and_base_5_6),
//                     lex_or_def(or_base_5_6),
//                     lex_end_brace_def(end_brace_base_5_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_6_6 = config.space();
//     let carriage_new_line_base_6_6 = config.carriage_new_line();
//     let unix_new_line_base_6_6 = config.unix_new_line();
//     let start_paren_base_6_6 = config.start_paren();
//     let end_paren_base_6_6 = config.end_paren();
//     let not_base_6_6 = config.not();
//     let and_base_6_6 = config.and();
//     let or_base_6_6 = config.or();
//     let end_brace_base_6_6 = config.end_brace();
//     let base_tag_parse_6_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_6_6),
//                     lex_carriage_return_def(carriage_new_line_base_6_6),
//                     lex_unix_new_line_def(unix_new_line_base_6_6),
//                     lex_start_paren_def(start_paren_base_6_6),
//                     lex_end_paren_def(end_paren_base_6_6),
//                     lex_not_def(not_base_6_6),
//                     lex_and_def(and_base_6_6),
//                     lex_or_def(or_base_6_6),
//                     lex_end_brace_def(end_brace_base_6_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_7_6 = config.space();
//     let carriage_new_line_base_7_6 = config.carriage_new_line();
//     let unix_new_line_base_7_6 = config.unix_new_line();
//     let start_paren_base_7_6 = config.start_paren();
//     let end_paren_base_7_6 = config.end_paren();
//     let not_base_7_6 = config.not();
//     let and_base_7_6 = config.and();
//     let or_base_7_6 = config.or();
//     let end_brace_base_7_6 = config.end_brace();
//     let base_tag_parse_7_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_7_6),
//                     lex_carriage_return_def(carriage_new_line_base_7_6),
//                     lex_unix_new_line_def(unix_new_line_base_7_6),
//                     lex_start_paren_def(start_paren_base_7_6),
//                     lex_end_paren_def(end_paren_base_7_6),
//                     lex_not_def(not_base_7_6),
//                     lex_and_def(and_base_7_6),
//                     lex_or_def(or_base_7_6),
//                     lex_end_brace_def(end_brace_base_7_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let space_base_8_6 = config.space();
//     let carriage_new_line_base_8_6 = config.carriage_new_line();
//     let unix_new_line_base_8_6 = config.unix_new_line();
//     let start_paren_base_8_6 = config.start_paren();
//     let end_paren_base_8_6 = config.end_paren();
//     let not_base_8_6 = config.not();
//     let and_base_8_6 = config.and();
//     let or_base_8_6 = config.or();
//     let end_brace_base_8_6 = config.end_brace();
//     let base_tag_parse_8_6 = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def(space_base_8_6),
//                     lex_carriage_return_def(carriage_new_line_base_8_6),
//                     lex_unix_new_line_def(unix_new_line_base_8_6),
//                     lex_start_paren_def(start_paren_base_8_6),
//                     lex_end_paren_def(end_paren_base_8_6),
//                     lex_not_def(not_base_8_6),
//                     lex_and_def(and_base_8_6),
//                     lex_or_def(or_base_8_6),
//                     lex_end_brace_def(end_brace_base_8_6),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let feature_parse_6 = || {
//         map(
//             tuple((
//                 lex_feature_def(config.feature_base()),
//                 start_brace_parse_0_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_0_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_not_parse_6 = || {
//         map(
//             tuple((
//                 lex_feature_not_def(config.feature_not()),
//                 start_brace_parse_1_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_1_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_case_parse_6 = || {
//         map(
//             tuple((
//                 lex_feature_case_def(config.feature_case()),
//                 start_brace_parse_2_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_2_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_parse_6 = || {
//         map(
//             tuple((
//                 lex_config_def(config.config_base()),
//                 start_brace_parse_3_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_3_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_not_parse_6 = || {
//         map(
//             tuple((
//                 lex_config_not_def(config.config_not()),
//                 start_brace_parse_4_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_4_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_case_parse_6 = || {
//         map(
//             tuple((
//                 lex_config_case_def(config.config_case()),
//                 start_brace_parse_5_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_5_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };

//     let config_group_parse_6 = || {
//         map(
//             tuple((
//                 lex_config_group_def(config.group_base()),
//                 start_brace_parse_6_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_6_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_not_parse_6 = || {
//         map(
//             tuple((
//                 lex_config_group_not_def(config.group_not()),
//                 start_brace_parse_7_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_7_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_case_parse_6 = || {
//         map(
//             tuple((
//                 lex_config_group_case_def(config.group_case()),
//                 start_brace_parse_8_6(),
//                 many_till(
//                     alt((
//                         lex_space_def(config.space()),
//                         lex_carriage_return_def(config.carriage_new_line()),
//                         lex_unix_new_line_def(config.unix_new_line()),
//                         lex_start_paren_def(config.start_paren()),
//                         lex_end_paren_def(config.end_paren()),
//                         lex_not_def(config.not()),
//                         lex_and_def(config.and()),
//                         lex_or_def(config.or()),
//                         base_tag_parse_8_6(),
//                     )),
//                     lex_end_brace_def(config.end_brace()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let start_end_single_line_comment_parser_0 = || {
//         map(
//             tuple((
//                 lex_start_comment_single_line(config.start_comment_single_line()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def(config.space()), |x| vec![x]),
//                             map(lex_unix_new_line_def(config.unix_new_line()), |x| vec![x]),
//                             feature_not_parse_0(),
//                             map(lex_feature_switch_def(config.feature_switch()), |x| vec![x]),
//                             feature_case_parse_0(),
//                             map(lex_feature_else_def(config.feature_else()), |x| vec![x]),
//                             feature_parse_0(),
//                             map(lex_end_feature_def(config.feature_end()), |x| vec![x]),
//                             config_not_parse_0(),
//                             map(lex_config_switch_def(config.config_switch()), |x| vec![x]),
//                             config_case_parse_0(),
//                             map(lex_config_else_def(config.config_else()), |x| vec![x]),
//                             config_parse_0(),
//                             map(lex_end_config_def(config.config_end()), |x| vec![x]),
//                             config_group_not_parse_0(),
//                             map(lex_config_group_switch_def(config.group_switch()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_case_parse_0(),
//                             map(lex_config_group_else_def(config.group_else()), |x| vec![x]),
//                             config_group_parse_0(),
//                             map(lex_end_config_group_def(config.group_end()), |x| vec![x]),
//                         )),
//                         lex_end_comment_single_line(config.end_comment_single_line()),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };

//     let multi_line_comment_parser_0 = || {
//         map(
//             tuple((
//                 lex_start_comment_multi_line(config.start_comment_multi_line()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def(config.space()), |x| vec![x]),
//                             map(lex_unix_new_line_def(config.unix_new_line()), |x| vec![x]),
//                             map(
//                                 lex_multi_line_comment_character(
//                                     config.multi_line_comment_character(),
//                                 ),
//                                 |x| vec![x],
//                             ),
//                             feature_not_parse_1(),
//                             map(lex_feature_switch_def(config.feature_switch()), |x| vec![x]),
//                             feature_case_parse_1(),
//                             map(lex_feature_else_def(config.feature_else()), |x| vec![x]),
//                             feature_parse_1(),
//                             map(lex_end_feature_def(config.feature_end()), |x| vec![x]),
//                             config_not_parse_1(),
//                             map(lex_config_switch_def(config.config_switch()), |x| vec![x]),
//                             config_case_parse_1(),
//                             map(lex_config_else_def(config.config_else()), |x| vec![x]),
//                             config_parse_1(),
//                             map(lex_end_config_def(config.config_end()), |x| vec![x]),
//                             config_group_not_parse_1(),
//                             map(lex_config_group_switch_def(config.group_switch()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_case_parse_1(),
//                             map(lex_config_group_else_def(config.group_else()), |x| vec![x]),
//                             config_group_parse_1(),
//                             map(lex_end_config_group_def(config.group_end()), |x| vec![x]),
//                         )),
//                         lex_end_comment_multi_line(config.end_comment_multi_line()),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };
//     let single_line_comment_parser_0 = || {
//         map(
//             tuple((
//                 lex_start_single_line_comment(config.single_line_comment()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def(config.space()), |x| vec![x]),
//                             feature_not_parse_2(),
//                             map(lex_feature_switch_def(config.feature_switch()), |x| vec![x]),
//                             feature_case_parse_2(),
//                             map(lex_feature_else_def(config.feature_else()), |x| vec![x]),
//                             feature_parse_2(),
//                             map(lex_end_feature_def(config.feature_end()), |x| vec![x]),
//                             config_not_parse_2(),
//                             map(lex_config_switch_def(config.config_switch()), |x| vec![x]),
//                             config_case_parse_2(),
//                             map(lex_config_else_def(config.config_else()), |x| vec![x]),
//                             config_parse_2(),
//                             map(lex_end_config_def(config.config_end()), |x| vec![x]),
//                             config_group_not_parse_2(),
//                             map(lex_config_group_switch_def(config.group_switch()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_case_parse_2(),
//                             map(lex_config_group_else_def(config.group_else()), |x| vec![x]),
//                             config_group_parse_2(),
//                             map(lex_end_config_group_def(config.group_end()), |x| vec![x]),
//                         )),
//                         //TODO: convert to make carriage return have to be followed by a new line
//                         alt((
//                             lex_unix_new_line_def(config.unix_new_line()),
//                             lex_carriage_return_def(config.carriage_new_line()),
//                         )),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };
//     let start_end_single_line_comment_parser_1 = || {
//         map(
//             tuple((
//                 lex_start_comment_single_line(config.start_comment_single_line()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def(config.space()), |x| vec![x]),
//                             map(lex_unix_new_line_def(config.unix_new_line()), |x| vec![x]),
//                             feature_not_parse_4(),
//                             map(lex_feature_switch_def(config.feature_switch()), |x| vec![x]),
//                             feature_case_parse_4(),
//                             map(lex_feature_else_def(config.feature_else()), |x| vec![x]),
//                             feature_parse_4(),
//                             map(lex_end_feature_def(config.feature_end()), |x| vec![x]),
//                             config_not_parse_4(),
//                             map(lex_config_switch_def(config.config_switch()), |x| vec![x]),
//                             config_case_parse_4(),
//                             map(lex_config_else_def(config.config_else()), |x| vec![x]),
//                             config_parse_4(),
//                             map(lex_end_config_def(config.config_end()), |x| vec![x]),
//                             config_group_not_parse_4(),
//                             map(lex_config_group_switch_def(config.group_switch()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_case_parse_4(),
//                             map(lex_config_group_else_def(config.group_else()), |x| vec![x]),
//                             config_group_parse_4(),
//                             map(lex_end_config_group_def(config.group_end()), |x| vec![x]),
//                         )),
//                         lex_end_comment_single_line(config.end_comment_single_line()),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };

//     let multi_line_comment_parser_1 = || {
//         map(
//             tuple((
//                 lex_start_comment_multi_line(config.start_comment_multi_line()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def(config.space()), |x| vec![x]),
//                             map(lex_unix_new_line_def(config.unix_new_line()), |x| vec![x]),
//                             map(
//                                 lex_multi_line_comment_character(
//                                     config.multi_line_comment_character(),
//                                 ),
//                                 |x| vec![x],
//                             ),
//                             feature_not_parse_5(),
//                             map(lex_feature_switch_def(config.feature_switch()), |x| vec![x]),
//                             feature_case_parse_5(),
//                             map(lex_feature_else_def(config.feature_else()), |x| vec![x]),
//                             feature_parse_5(),
//                             map(lex_end_feature_def(config.feature_end()), |x| vec![x]),
//                             config_not_parse_5(),
//                             map(lex_config_switch_def(config.config_switch()), |x| vec![x]),
//                             config_case_parse_5(),
//                             map(lex_config_else_def(config.config_else()), |x| vec![x]),
//                             config_parse_5(),
//                             map(lex_end_config_def(config.config_end()), |x| vec![x]),
//                             config_group_not_parse_5(),
//                             map(lex_config_group_switch_def(config.group_switch()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_case_parse_5(),
//                             map(lex_config_group_else_def(config.group_else()), |x| vec![x]),
//                             config_group_parse_5(),
//                             map(lex_end_config_group_def(config.group_end()), |x| vec![x]),
//                         )),
//                         lex_end_comment_multi_line(config.end_comment_multi_line()),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };
//     let single_line_comment_parser_1 = || {
//         map(
//             tuple((
//                 lex_start_single_line_comment(config.single_line_comment()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def(config.space()), |x| vec![x]),
//                             feature_not_parse_6(),
//                             map(lex_feature_switch_def(config.feature_switch()), |x| vec![x]),
//                             feature_case_parse_6(),
//                             map(lex_feature_else_def(config.feature_else()), |x| vec![x]),
//                             feature_parse_6(),
//                             map(lex_end_feature_def(config.feature_end()), |x| vec![x]),
//                             config_not_parse_6(),
//                             map(lex_config_switch_def(config.config_switch()), |x| vec![x]),
//                             config_case_parse_6(),
//                             map(lex_config_else_def(config.config_else()), |x| vec![x]),
//                             config_parse_6(),
//                             map(lex_end_config_def(config.config_end()), |x| vec![x]),
//                             config_group_not_parse_6(),
//                             map(lex_config_group_switch_def(config.group_switch()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_case_parse_6(),
//                             map(lex_config_group_else_def(config.group_else()), |x| vec![x]),
//                             config_group_parse_6(),
//                             map(lex_end_config_group_def(config.group_end()), |x| vec![x]),
//                         )),
//                         //TODO: convert to make carriage return have to be followed by a new line
//                         alt((
//                             lex_unix_new_line_def(config.unix_new_line()),
//                             lex_carriage_return_def(config.carriage_new_line()),
//                         )),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };
//     let comments_0 = || {
//         alt((
//             start_end_single_line_comment_parser_0(),
//             multi_line_comment_parser_0(),
//             single_line_comment_parser_0(),
//         ))
//     };
//     let comments_1 = || {
//         alt((
//             start_end_single_line_comment_parser_1(),
//             multi_line_comment_parser_1(),
//             single_line_comment_parser_1(),
//         ))
//     };
//     let eof = || map(lex_eof(config.eof()), |_| vec![LexerToken::Eof]);
//     let text_parser = map(
//         many_till(anychar, alt((comments_0(), eof()))),
//         |(results, mut comment): (Vec<char>, Vec<LexerToken>)| {
//             let res = results.iter().clone().collect::<String>();
//             comment.insert(0, LexerToken::Text(res));
//             comment
//         },
//     );
//     let parser = map(
//         many_till(alt((comments_1(), text_parser)), lex_eof(config.eof())),
//         |(res, eof): (Vec<Vec<LexerToken>>, LexerToken)| {
//             let mut flattened = res.into_iter().flatten().collect::<Vec<LexerToken>>();
//             match flattened.last() {
//                 Some(i) => {
//                     if *i != LexerToken::Eof {
//                         flattened.push(eof);
//                     }
//                 }
//                 None => panic!("failed to tokenize text document"),
//             }
//             flattened
//         },
//     );
//     parser
//     // };
//     // p(config)
// }
// pub fn lex_applicability<
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
// >(
//     config: LexerConfig<
//         I,
//         O1,
//         O2,
//         O3,
//         O4,
//         O5,
//         O6,
//         O7,
//         O8,
//         O9,
//         O10,
//         O11,
//         O12,
//         O13,
//         O14,
//         O15,
//         O16,
//         O17,
//         O18,
//         O19,
//         O20,
//         O21,
//         O22,
//         O23,
//         O24,
//         O25,
//         O26,
//         O27,
//         O28,
//         O29,
//         O30,
//         O31,
//         O32,
//         O33,
//         O34,
//         O35,
//         E,
//         P1,
//         P2,
//         P3,
//         P4,
//         P5,
//         P6,
//         P7,
//         P8,
//         P9,
//         P10,
//         P11,
//         P12,
//         P13,
//         P14,
//         P15,
//         P16,
//         P17,
//         P18,
//         P19,
//         P20,
//         P21,
//         P22,
//         P23,
//         P24,
//         P25,
//         P26,
//         P27,
//         P28,
//         P29,
//         P30,
//         P31,
//         P32,
//         P33,
//         P34,
//         P35,
//         U1,
//         U2,
//         U3,
//         U4,
//         U5,
//         U6,
//         U7,
//         U8,
//         U9,
//         U10,
//         U11,
//         U12,
//         U13,
//         U14,
//         U15,
//         U16,
//         U17,
//         U18,
//         U19,
//         U20,
//         U21,
//         U22,
//         U23,
//         U24,
//         U25,
//         U26,
//         U27,
//         U28,
//         U29,
//         U30,
//         U31,
//         U32,
//         U33,
//         U34,
//         U35,
//     >,
// ) -> impl FnMut(I) -> Result<(I, Vec<LexerToken>), nom::Err<E>>
// where
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
//     let start_brace_parse = || {
//         many_till(
//             lex_space_def((config.space)()),
//             lex_start_brace_def((config.start_brace)()),
//         )
//     };
//     let base_tag_parse = || {
//         map(
//             many_till(
//                 anychar,
//                 peek(alt((
//                     lex_space_def((config.space)()),
//                     lex_carriage_return_def((config.carriage_new_line)()),
//                     lex_unix_new_line_def((config.unix_new_line)()),
//                     lex_start_paren_def((config.start_paren)()),
//                     lex_end_paren_def((config.end_paren)()),
//                     lex_not_def((config.not)()),
//                     lex_and_def((config.and)()),
//                     lex_or_def((config.or)()),
//                     lex_end_brace_def((config.end_brace)()),
//                 ))),
//             ),
//             |(results, _)| {
//                 let res = results.into_iter().clone().collect::<String>();
//                 LexerToken::Tag(res)
//             },
//         )
//     };
//     let feature_parse = || {
//         map(
//             tuple((
//                 lex_feature_def((config.feature.base)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_not_parse = || {
//         map(
//             tuple((
//                 lex_feature_not_def((config.feature.not)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let feature_case_parse = || {
//         map(
//             tuple((
//                 lex_feature_case_def((config.feature.case)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(feature, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_parse = || {
//         map(
//             tuple((
//                 lex_config_def((config.configuration.base)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_not_parse = || {
//         map(
//             tuple((
//                 lex_config_not_def((config.configuration.not)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_case_parse = || {
//         map(
//             tuple((
//                 lex_config_case_def((config.configuration.case)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(config, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, config);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };

//     let config_group_parse = || {
//         map(
//             tuple((
//                 lex_config_group_def((config.configuration_group.base)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_not_parse = || {
//         map(
//             tuple((
//                 lex_config_group_not_def((config.configuration_group.not)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let config_group_case_parse = || {
//         map(
//             tuple((
//                 lex_config_group_case_def((config.configuration_group.case)()),
//                 start_brace_parse(),
//                 many_till(
//                     alt((
//                         lex_space_def((config.space)()),
//                         lex_carriage_return_def((config.carriage_new_line)()),
//                         lex_unix_new_line_def((config.unix_new_line)()),
//                         lex_start_paren_def((config.start_paren)()),
//                         lex_end_paren_def((config.end_paren)()),
//                         lex_not_def((config.not)()),
//                         lex_and_def((config.and)()),
//                         lex_or_def((config.or)()),
//                         base_tag_parse(),
//                     )),
//                     lex_end_brace_def((config.end_brace)()),
//                 ),
//             )),
//             |(group, (mut spaces, start), (mut inner, end))| {
//                 spaces.insert(0, group);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     };
//     let start_end_single_line_comment_parser = || {
//         map(
//             tuple((
//                 lex_start_comment_single_line((config.start_comment_single_line)()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def((config.space)()), |x| vec![x]),
//                             map(lex_unix_new_line_def((config.unix_new_line)()), |x| vec![x]),
//                             feature_not_parse(),
//                             map(lex_feature_switch_def((config.feature.switch)()), |x| {
//                                 vec![x]
//                             }),
//                             feature_case_parse(),
//                             map(lex_feature_else_def((config.feature.applic_else)()), |x| {
//                                 vec![x]
//                             }),
//                             feature_parse(),
//                             map(lex_end_feature_def((config.feature.end)()), |x| vec![x]),
//                             config_not_parse(),
//                             map(
//                                 lex_config_switch_def((config.configuration.switch)()),
//                                 |x| vec![x],
//                             ),
//                             config_case_parse(),
//                             map(
//                                 lex_config_else_def((config.configuration.applic_else)()),
//                                 |x| vec![x],
//                             ),
//                             config_parse(),
//                             map(lex_end_config_def((config.configuration.end)()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_not_parse(),
//                             map(
//                                 lex_config_group_switch_def((config.configuration_group.switch)()),
//                                 |x| vec![x],
//                             ),
//                             config_group_case_parse(),
//                             map(
//                                 lex_config_group_else_def(
//                                     (config.configuration_group.applic_else)(),
//                                 ),
//                                 |x| vec![x],
//                             ),
//                             config_group_parse(),
//                             map(
//                                 lex_end_config_group_def((config.configuration_group.end)()),
//                                 |x| vec![x],
//                             ),
//                         )),
//                         lex_end_comment_single_line((config.end_comment_single_line)()),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };

//     let multi_line_comment_parser = || {
//         map(
//             tuple((
//                 lex_start_comment_multi_line((config.start_comment_multi_line)()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def((config.space)()), |x| vec![x]),
//                             map(lex_unix_new_line_def((config.unix_new_line)()), |x| vec![x]),
//                             map(
//                                 lex_multi_line_comment_character((config
//                                     .multi_line_comment_character)(
//                                 )),
//                                 |x| vec![x],
//                             ),
//                             feature_not_parse(),
//                             map(lex_feature_switch_def((config.feature.switch)()), |x| {
//                                 vec![x]
//                             }),
//                             feature_case_parse(),
//                             map(lex_feature_else_def((config.feature.applic_else)()), |x| {
//                                 vec![x]
//                             }),
//                             feature_parse(),
//                             map(lex_end_feature_def((config.feature.end)()), |x| vec![x]),
//                             config_not_parse(),
//                             map(
//                                 lex_config_switch_def((config.configuration.switch)()),
//                                 |x| vec![x],
//                             ),
//                             config_case_parse(),
//                             map(
//                                 lex_config_else_def((config.configuration.applic_else)()),
//                                 |x| vec![x],
//                             ),
//                             config_parse(),
//                             map(lex_end_config_def((config.configuration.end)()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_not_parse(),
//                             map(
//                                 lex_config_group_switch_def((config.configuration_group.switch)()),
//                                 |x| vec![x],
//                             ),
//                             config_group_case_parse(),
//                             map(
//                                 lex_config_group_else_def(
//                                     (config.configuration_group.applic_else)(),
//                                 ),
//                                 |x| vec![x],
//                             ),
//                             config_group_parse(),
//                             map(
//                                 lex_end_config_group_def((config.configuration_group.end)()),
//                                 |x| vec![x],
//                             ),
//                         )),
//                         lex_end_comment_multi_line((config.end_comment_multi_line)()),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };
//     let single_line_comment_parser = || {
//         map(
//             tuple((
//                 lex_start_single_line_comment((config.single_line_comment)()),
//                 map(
//                     many_till(
//                         alt((
//                             map(lex_space_def((config.space)()), |x| vec![x]),
//                             feature_not_parse(),
//                             map(lex_feature_switch_def((config.feature.switch)()), |x| {
//                                 vec![x]
//                             }),
//                             feature_case_parse(),
//                             map(lex_feature_else_def((config.feature.applic_else)()), |x| {
//                                 vec![x]
//                             }),
//                             feature_parse(),
//                             map(lex_end_feature_def((config.feature.end)()), |x| vec![x]),
//                             config_not_parse(),
//                             map(
//                                 lex_config_switch_def((config.configuration.switch)()),
//                                 |x| vec![x],
//                             ),
//                             config_case_parse(),
//                             map(
//                                 lex_config_else_def((config.configuration.applic_else)()),
//                                 |x| vec![x],
//                             ),
//                             config_parse(),
//                             map(lex_end_config_def((config.configuration.end)()), |x| {
//                                 vec![x]
//                             }),
//                             config_group_not_parse(),
//                             map(
//                                 lex_config_group_switch_def((config.configuration_group.switch)()),
//                                 |x| vec![x],
//                             ),
//                             config_group_case_parse(),
//                             map(
//                                 lex_config_group_else_def(
//                                     (config.configuration_group.applic_else)(),
//                                 ),
//                                 |x| vec![x],
//                             ),
//                             config_group_parse(),
//                             map(
//                                 lex_end_config_group_def((config.configuration_group.end)()),
//                                 |x| vec![x],
//                             ),
//                         )),
//                         //TODO: convert to make carriage return have to be followed by a new line
//                         alt((
//                             lex_unix_new_line_def((config.unix_new_line)()),
//                             lex_carriage_return_def((config.carriage_new_line)()),
//                         )),
//                     ),
//                     |(list, end)| {
//                         let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                         flattened.push(end);
//                         flattened
//                     },
//                 ),
//             )),
//             |(start, mut list)| {
//                 list.insert(0, start);
//                 list
//             },
//         )
//     };
//     let comments = || {
//         alt((
//             start_end_single_line_comment_parser(),
//             multi_line_comment_parser(),
//             single_line_comment_parser(),
//         ))
//     };
//     let eof = || map(lex_eof((config.eof)()), |_| vec![LexerToken::Eof]);
//     let text_parser = map(
//         many_till(anychar, alt((comments(), eof()))),
//         |(results, mut comment): (Vec<char>, Vec<LexerToken>)| {
//             let res = results.iter().clone().collect::<String>();
//             comment.insert(0, LexerToken::Text(res));
//             comment
//         },
//     );
//     let parser = map(
//         many_till(alt((comments(), text_parser)), lex_eof((config.eof)())),
//         |(res, eof): (Vec<Vec<LexerToken>>, LexerToken)| {
//             let mut flattened = res.into_iter().flatten().collect::<Vec<LexerToken>>();
//             match flattened.last() {
//                 Some(i) => {
//                     if *i != LexerToken::Eof {
//                         flattened.push(eof);
//                     }
//                 }
//                 None => panic!("failed to tokenize text document"),
//             }
//             flattened
//         },
//     );
//     parser
// }

#[cfg(test)]
mod tests {

    #[test]
    fn basic_text() {
        // let config = LexerConfig {
        //     feature: ApplicabilityLexerConfig {
        //         _phantom_i: PhantomData::<&str>,
        //         _phantom_o1: PhantomData,
        //         _phantom_o2: PhantomData,
        //         _phantom_o3: PhantomData,
        //         _phantom_o4: PhantomData,
        //         _phantom_o5: PhantomData,
        //         _phantom_o6: PhantomData,
        //         _phantom_e: PhantomData::<nom::error::Error<_>>,
        //         base: || tag("Feature"),
        //         not: || tag("Feature Not"),
        //         switch: || tag("Feature Switch"),
        //         case: || tag("Feature Case"),
        //         applic_else: || tag("Feature Else"),
        //         end: || tag("End Feature"),
        //     },
        //     configuration: ApplicabilityLexerConfig {
        //         _phantom_i: PhantomData::<&str>,
        //         _phantom_o1: PhantomData,
        //         _phantom_o2: PhantomData,
        //         _phantom_o3: PhantomData,
        //         _phantom_o4: PhantomData,
        //         _phantom_o5: PhantomData,
        //         _phantom_o6: PhantomData,
        //         _phantom_e: PhantomData::<nom::error::Error<_>>,
        //         base: || tag("Configuration"),
        //         not: || tag("Configuration Not"),
        //         switch: || tag("Configuration Switch"),
        //         case: || tag("Configuration Case"),
        //         applic_else: || tag("Configuration Else"),
        //         end: || tag("End Configuration"),
        //     },
        //     configuration_group: ApplicabilityLexerConfig {
        //         _phantom_i: PhantomData::<&str>,
        //         _phantom_o1: PhantomData,
        //         _phantom_o2: PhantomData,
        //         _phantom_o3: PhantomData,
        //         _phantom_o4: PhantomData,
        //         _phantom_o5: PhantomData,
        //         _phantom_o6: PhantomData,
        //         _phantom_e: PhantomData::<nom::error::Error<_>>,
        //         base: || tag("ConfigurationGroup"),
        //         not: || tag("ConfigurationGroup Not"),
        //         switch: || tag("ConfigurationGroup Switch"),
        //         case: || tag("ConfigurationGroup Case"),
        //         applic_else: || tag("ConfigurationGroup Else"),
        //         end: || tag("End ConfigurationGroup"),
        //     },
        //     _phantom_o1: PhantomData,
        //     _phantom_o2: PhantomData,
        //     _phantom_o3: PhantomData,
        //     _phantom_o4: PhantomData,
        //     _phantom_o5: PhantomData,
        //     _phantom_o6: PhantomData,
        //     _phantom_o7: PhantomData,
        //     _phantom_o8: PhantomData,
        //     _phantom_o9: PhantomData,
        //     _phantom_o10: PhantomData,
        //     _phantom_o11: PhantomData::<LexerToken>,
        //     _phantom_o12: PhantomData::<LexerToken>,
        //     _phantom_o13: PhantomData::<LexerToken>,
        //     _phantom_o14: PhantomData::<LexerToken>,
        //     _phantom_o15: PhantomData,
        //     _phantom_o16: PhantomData,
        //     _phantom_o17: PhantomData,
        //     space: || space1,
        //     unix_new_line: || newline,
        //     carriage_new_line: || char('\r'),
        //     start_brace: || tag("["),
        //     end_brace: || tag("]"),
        //     start_paren: || tag("("),
        //     end_paren: || tag(")"),
        //     not: || tag("!"),
        //     and: || tag("&"),
        //     or: || tag("|"),
        //     start_comment_single_line: || tag("``"),
        //     end_comment_single_line: || tag("``"),
        //     start_comment_multi_line: || fail,
        //     end_comment_multi_line: || fail,
        //     multi_line_comment_character: || fail,
        //     single_line_comment: || fail,
        //     eof: || eof,
        // };
        // let config = MarkdownDocumentConfig::new();
        // config.document = "some text";
        // let mut tokenizer = lex_applicability(&config);

        // assert_eq!(
        //     tokenizer(config.document),
        //     Ok((
        //         "",
        //         vec![LexerToken::Text("some text".to_string()), LexerToken::Eof]
        //     ))
        // )
    }

    // #[test]
    // fn feature_tag() {
    //     let config = LexerConfig {
    //         feature: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("Feature"),
    //             not: || tag("Feature Not"),
    //             switch: || tag("Feature Switch"),
    //             case: || tag("Feature Case"),
    //             applic_else: || tag("Feature Else"),
    //             end: || tag("End Feature"),
    //         },
    //         configuration: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("Configuration"),
    //             not: || tag("Configuration Not"),
    //             switch: || tag("Configuration Switch"),
    //             case: || tag("Configuration Case"),
    //             applic_else: || tag("Configuration Else"),
    //             end: || tag("End Configuration"),
    //         },
    //         configuration_group: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("ConfigurationGroup"),
    //             not: || tag("ConfigurationGroup Not"),
    //             switch: || tag("ConfigurationGroup Switch"),
    //             case: || tag("ConfigurationGroup Case"),
    //             applic_else: || tag("ConfigurationGroup Else"),
    //             end: || tag("End ConfigurationGroup"),
    //         },
    //         _phantom_o1: PhantomData,
    //         _phantom_o2: PhantomData,
    //         _phantom_o3: PhantomData,
    //         _phantom_o4: PhantomData,
    //         _phantom_o5: PhantomData,
    //         _phantom_o6: PhantomData,
    //         _phantom_o7: PhantomData,
    //         _phantom_o8: PhantomData,
    //         _phantom_o9: PhantomData,
    //         _phantom_o10: PhantomData,
    //         _phantom_o11: PhantomData::<LexerToken>,
    //         _phantom_o12: PhantomData::<LexerToken>,
    //         _phantom_o13: PhantomData::<LexerToken>,
    //         _phantom_o14: PhantomData::<LexerToken>,
    //         _phantom_o15: PhantomData,
    //         _phantom_o16: PhantomData,
    //         _phantom_o17: PhantomData,
    //         space: || space1,
    //         unix_new_line: || newline,
    //         carriage_new_line: || char('\r'),
    //         start_brace: || tag("["),
    //         end_brace: || tag("]"),
    //         start_paren: || tag("("),
    //         end_paren: || tag(")"),
    //         not: || tag("!"),
    //         and: || tag("&"),
    //         or: || tag("|"),
    //         start_comment_single_line: || tag("``"),
    //         end_comment_single_line: || tag("``"),
    //         start_comment_multi_line: || fail,
    //         end_comment_multi_line: || fail,
    //         multi_line_comment_character: || fail,
    //         single_line_comment: || fail,
    //         eof: || eof,
    //     };
    //     let mut tokenizer = lex_applicability(config);
    //     assert_eq!(
    //         tokenizer("``Feature[FEATURE_1]`` some text ``End Feature``"),
    //         Ok((
    //             "",
    //             vec![
    //                 LexerToken::StartCommentSingleLine,
    //                 LexerToken::Feature,
    //                 LexerToken::StartBrace,
    //                 LexerToken::Tag("FEATURE_1".to_string()),
    //                 LexerToken::EndBrace,
    //                 LexerToken::EndCommentSingleLine,
    //                 LexerToken::Text(" some text ".to_string()),
    //                 LexerToken::StartCommentSingleLine,
    //                 LexerToken::EndFeature,
    //                 LexerToken::EndCommentSingleLine,
    //                 LexerToken::Eof
    //             ]
    //         ))
    //     )
    // }

    // #[test]
    // fn feature_and_feature_tag() {
    //     let config = LexerConfig {
    //         feature: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("Feature"),
    //             not: || tag("Feature Not"),
    //             switch: || tag("Feature Switch"),
    //             case: || tag("Feature Case"),
    //             applic_else: || tag("Feature Else"),
    //             end: || tag("End Feature"),
    //         },
    //         configuration: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("Configuration"),
    //             not: || tag("Configuration Not"),
    //             switch: || tag("Configuration Switch"),
    //             case: || tag("Configuration Case"),
    //             applic_else: || tag("Configuration Else"),
    //             end: || tag("End Configuration"),
    //         },
    //         configuration_group: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("ConfigurationGroup"),
    //             not: || tag("ConfigurationGroup Not"),
    //             switch: || tag("ConfigurationGroup Switch"),
    //             case: || tag("ConfigurationGroup Case"),
    //             applic_else: || tag("ConfigurationGroup Else"),
    //             end: || tag("End ConfigurationGroup"),
    //         },
    //         _phantom_o1: PhantomData,
    //         _phantom_o2: PhantomData,
    //         _phantom_o3: PhantomData,
    //         _phantom_o4: PhantomData,
    //         _phantom_o5: PhantomData,
    //         _phantom_o6: PhantomData,
    //         _phantom_o7: PhantomData,
    //         _phantom_o8: PhantomData,
    //         _phantom_o9: PhantomData,
    //         _phantom_o10: PhantomData,
    //         _phantom_o11: PhantomData::<LexerToken>,
    //         _phantom_o12: PhantomData::<LexerToken>,
    //         _phantom_o13: PhantomData::<LexerToken>,
    //         _phantom_o14: PhantomData::<LexerToken>,
    //         _phantom_o15: PhantomData,
    //         _phantom_o16: PhantomData,
    //         _phantom_o17: PhantomData,
    //         space: || space1,
    //         unix_new_line: || newline,
    //         carriage_new_line: || char('\r'),
    //         start_brace: || tag("["),
    //         end_brace: || tag("]"),
    //         start_paren: || tag("("),
    //         end_paren: || tag(")"),
    //         not: || tag("!"),
    //         and: || tag("&"),
    //         or: || tag("|"),
    //         start_comment_single_line: || tag("``"),
    //         end_comment_single_line: || tag("``"),
    //         start_comment_multi_line: || fail,
    //         end_comment_multi_line: || fail,
    //         multi_line_comment_character: || fail,
    //         single_line_comment: || fail,
    //         eof: || eof,
    //     };
    //     let mut tokenizer = lex_applicability(config);
    //     assert_eq!(
    //         tokenizer("``Feature[FEATURE_1 & FEATURE_2]`` some text ``End Feature``"),
    //         Ok((
    //             "",
    //             vec![
    //                 LexerToken::StartCommentSingleLine,
    //                 LexerToken::Feature,
    //                 LexerToken::StartBrace,
    //                 LexerToken::Tag("FEATURE_1".to_string()),
    //                 LexerToken::Space,
    //                 LexerToken::And,
    //                 LexerToken::Space,
    //                 LexerToken::Tag("FEATURE_2".to_string()),
    //                 LexerToken::EndBrace,
    //                 LexerToken::EndCommentSingleLine,
    //                 LexerToken::Text(" some text ".to_string()),
    //                 LexerToken::StartCommentSingleLine,
    //                 LexerToken::EndFeature,
    //                 LexerToken::EndCommentSingleLine,
    //                 LexerToken::Eof
    //             ]
    //         ))
    //     )
    // }

    // #[test]
    // fn feature_or_feature_tag() {
    //     let config = LexerConfig {
    //         feature: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("Feature"),
    //             not: || tag("Feature Not"),
    //             switch: || tag("Feature Switch"),
    //             case: || tag("Feature Case"),
    //             applic_else: || tag("Feature Else"),
    //             end: || tag("End Feature"),
    //         },
    //         configuration: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("Configuration"),
    //             not: || tag("Configuration Not"),
    //             switch: || tag("Configuration Switch"),
    //             case: || tag("Configuration Case"),
    //             applic_else: || tag("Configuration Else"),
    //             end: || tag("End Configuration"),
    //         },
    //         configuration_group: ApplicabilityLexerConfig {
    //             _phantom_i: PhantomData,
    //             _phantom_o1: PhantomData,
    //             _phantom_o2: PhantomData,
    //             _phantom_o3: PhantomData,
    //             _phantom_o4: PhantomData,
    //             _phantom_o5: PhantomData,
    //             _phantom_o6: PhantomData,
    //             _phantom_e: PhantomData::<nom::error::Error<_>>,
    //             base: || tag("ConfigurationGroup"),
    //             not: || tag("ConfigurationGroup Not"),
    //             switch: || tag("ConfigurationGroup Switch"),
    //             case: || tag("ConfigurationGroup Case"),
    //             applic_else: || tag("ConfigurationGroup Else"),
    //             end: || tag("End ConfigurationGroup"),
    //         },
    //         _phantom_o1: PhantomData,
    //         _phantom_o2: PhantomData,
    //         _phantom_o3: PhantomData,
    //         _phantom_o4: PhantomData,
    //         _phantom_o5: PhantomData,
    //         _phantom_o6: PhantomData,
    //         _phantom_o7: PhantomData,
    //         _phantom_o8: PhantomData,
    //         _phantom_o9: PhantomData,
    //         _phantom_o10: PhantomData,
    //         _phantom_o11: PhantomData::<LexerToken>,
    //         _phantom_o12: PhantomData::<LexerToken>,
    //         _phantom_o13: PhantomData::<LexerToken>,
    //         _phantom_o14: PhantomData::<LexerToken>,
    //         _phantom_o15: PhantomData,
    //         _phantom_o16: PhantomData,
    //         _phantom_o17: PhantomData,
    //         space: || space1,
    //         unix_new_line: || newline,
    //         carriage_new_line: || char('\r'),
    //         start_brace: || tag("["),
    //         end_brace: || tag("]"),
    //         start_paren: || tag("("),
    //         end_paren: || tag(")"),
    //         not: || tag("!"),
    //         and: || tag("&"),
    //         or: || tag("|"),
    //         start_comment_single_line: || tag("``"),
    //         end_comment_single_line: || tag("``"),
    //         start_comment_multi_line: || fail,
    //         end_comment_multi_line: || fail,
    //         multi_line_comment_character: || fail,
    //         single_line_comment: || fail,
    //         eof: || eof,
    //     };
    //     let mut tokenizer = lex_applicability(config);
    //     assert_eq!(
    //         tokenizer("``Feature[FEATURE_1 | FEATURE_2]`` some text ``End Feature``"),
    //         Ok((
    //             "",
    //             vec![
    //                 LexerToken::StartCommentSingleLine,
    //                 LexerToken::Feature,
    //                 LexerToken::StartBrace,
    //                 LexerToken::Tag("FEATURE_1".to_string()),
    //                 LexerToken::Space,
    //                 LexerToken::Or,
    //                 LexerToken::Space,
    //                 LexerToken::Tag("FEATURE_2".to_string()),
    //                 LexerToken::EndBrace,
    //                 LexerToken::EndCommentSingleLine,
    //                 LexerToken::Text(" some text ".to_string()),
    //                 LexerToken::StartCommentSingleLine,
    //                 LexerToken::EndFeature,
    //                 LexerToken::EndCommentSingleLine,
    //                 LexerToken::Eof
    //             ]
    //         ))
    //     )
    // }
}
