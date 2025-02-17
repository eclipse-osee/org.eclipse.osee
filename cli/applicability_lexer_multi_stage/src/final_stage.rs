// use nom::{
//     bytes::take_till, combinator::map, error::ParseError, multi::many_till, AsChar, Compare, Input,
//     Parser,
// };

// use crate::{
//     config_def::{
//         lex_config_case_def, lex_config_def, lex_config_else_def, lex_config_not_def,
//         lex_config_switch_def, lex_end_config_def,
//     },
//     config_group_def::{
//         lex_config_group_case_def, lex_config_group_def, lex_config_group_else_def,
//         lex_config_group_not_def, lex_config_group_switch_def, lex_end_config_group_def,
//     },
//     feature_def::{
//         lex_end_feature_def, lex_feature_case_def, lex_feature_def, lex_feature_else_def,
//         lex_feature_not_def, lex_feature_switch_def,
//     },
//     traits::{
//         And, CarriageReturn, ConfigBase, ConfigCase, ConfigElse, ConfigNot, ConfigSwitch, EndBrace,
//         EndCommentMultiLine, EndCommentSingleLine, EndConfig, EndFeature, EndGroup, EndParen, Eof,
//         FeatureBase, FeatureCase, FeatureElse, FeatureNot, FeatureSwitch, GroupBase, GroupCase,
//         GroupElse, GroupNot, GroupSwitch, MultilineCommentCharacter, NewLine, Not, Or,
//         SingleLineComment, Space, StartBrace, StartCommentMultiLine, StartCommentSingleLine,
//         StartParen,
//     },
//     utility_def::{
//         lex_and_def, lex_carriage_return_def, lex_end_brace_def, lex_end_comment_single_line,
//         lex_end_paren_def, lex_not_def, lex_or_def, lex_space_def, lex_start_brace_def,
//         lex_start_comment_single_line, lex_start_paren_def, lex_unix_new_line_def,
//     },
// };

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
// // LEXER IMPLEMENTATION

// trait LexStartBrace {
//     fn start_brace_lexer<'x, I, E>(
//         &self,
//     ) -> impl Parser<I, Output = (Vec<LexerToken>, LexerToken), Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexStartBrace for T
// where
//     T: Space + StartBrace,
// {
//     fn start_brace_lexer<'x, I, E>(
//         &self,
//     ) -> impl Parser<I, Output = (Vec<LexerToken>, LexerToken), Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         many_till(
//             lex_space_def(self.space()),
//             lex_start_brace_def(self.start_brace()),
//         )
//     }
// }

// trait LexTagSpecialCharacters {
//     fn is_special_character<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar;
//     fn tag_special_character_lexer<'x, I, E>(
//         &self,
//     ) -> impl Parser<I, Output = LexerToken, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexTagSpecialCharacters for T
// where
//     T: Space + CarriageReturn + NewLine + StartParen + EndParen + Not + And + Or,
// {
//     fn tag_special_character_lexer<'x, I, E>(
//         &self,
//     ) -> impl Parser<I, Output = LexerToken, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         lex_space_def(self.space())
//             .or(lex_carriage_return_def(self.carriage_return()))
//             .or(lex_unix_new_line_def(self.newline()))
//             .or(lex_start_paren_def(self.start_paren()))
//             .or(lex_end_paren_def(self.end_paren()))
//             .or(lex_not_def(self.not()))
//             .or(lex_and_def(self.and()))
//             .or(lex_or_def(self.or()))
//     }

//     fn is_special_character<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar,
//     {
//         let result = self.is_space(input)
//             || self.is_carriage_return(input)
//             || self.is_new_line(input)
//             || self.is_start_paren(input)
//             || self.is_end_paren(input)
//             || self.is_not(input)
//             || self.is_and(input)
//             || self.is_or(input);
//         result
//     }
// }
// trait LexTagText {
//     fn tag_text_lexer<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexTagText for T
// where
//     T: LexTagSpecialCharacters + EndBrace,
// {
//     fn tag_text_lexer<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             take_till(|x: I::Item| self.is_special_character::<I>(x) || self.is_end_brace::<I>(x)),
//             |res: I| LexerToken::Tag(res.into()),
//         )
//     }
// }

// trait LexFeature {
//     fn lex_feature<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexFeature for T
// where
//     T: FeatureBase + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_feature<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_feature_def(self.feature_base())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }

// trait LexFeatureNot {
//     fn lex_feature_not<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexFeatureNot for T
// where
//     T: FeatureNot + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_feature_not<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_feature_not_def(self.feature_not())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }
// trait LexFeatureCase {
//     fn lex_feature_case<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexFeatureCase for T
// where
//     T: FeatureCase + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_feature_case<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_feature_case_def(self.feature_case())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }
// trait LexConfig {
//     fn lex_config<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexConfig for T
// where
//     T: ConfigBase + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_config<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_config_def(self.config_base())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }
// trait LexConfigNot {
//     fn lex_config_not<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexConfigNot for T
// where
//     T: ConfigNot + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_config_not<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_config_not_def(self.config_not())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }

// trait LexConfigCase {
//     fn lex_config_case<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }

// impl<T> LexConfigCase for T
// where
//     T: ConfigCase + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_config_case<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_config_case_def(self.config_case())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }

// trait LexGroup {
//     fn lex_group<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }

// impl<T> LexGroup for T
// where
//     T: GroupBase + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_group<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_config_group_def(self.group_base())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }

// trait LexGroupNot {
//     fn lex_group_not<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }
// impl<T> LexGroupNot for T
// where
//     T: GroupNot + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_group_not<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_config_group_not_def(self.group_not())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }

// trait LexGroupCase {
//     fn lex_group_case<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }

// impl<T> LexGroupCase for T
// where
//     T: GroupCase + LexStartBrace + LexTagSpecialCharacters + LexTagText + EndBrace,
// {
//     fn lex_group_case<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str> + Into<String>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         map(
//             lex_config_group_case_def(self.group_case())
//                 .and(self.start_brace_lexer())
//                 .and(many_till(
//                     self.tag_special_character_lexer().or(self.tag_text_lexer()),
//                     lex_end_brace_def(self.end_brace()),
//                 )),
//             |((feature, (mut spaces, start)), (mut inner, end))| {
//                 spaces.insert(0, feature);
//                 spaces.push(start);
//                 inner.push(end);
//                 spaces.append(&mut inner);
//                 spaces
//             },
//         )
//     }
// }

// trait LexCommentContents {
//     fn lex_comment_contents<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }

// impl<T> LexCommentContents for T
// where
//     T: Space
//         + LexFeatureNot
//         + FeatureSwitch
//         + LexFeatureCase
//         + FeatureElse
//         + LexFeature
//         + EndFeature
//         + LexConfigNot
//         + ConfigSwitch
//         + LexConfigCase
//         + ConfigElse
//         + LexConfig
//         + EndConfig
//         + LexGroupNot
//         + GroupSwitch
//         + LexGroupCase
//         + GroupElse
//         + LexGroup
//         + EndGroup,
// {
//     fn lex_comment_contents<'x, I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         lex_space_def(self.space())
//             .map(|x| vec![x])
//             .or(self.lex_feature_not())
//             .or(lex_feature_switch_def(self.feature_switch()).map(|x| vec![x]))
//             .or(self.lex_feature_case())
//             .or(lex_feature_else_def(self.feature_else()).map(|x| vec![x]))
//             .or(self.lex_feature())
//             .or(lex_end_feature_def(self.end_feature()).map(|x| vec![x]))
//             .or(self.lex_config_not())
//             .or(lex_config_switch_def(self.config_switch()).map(|x| vec![x]))
//             .or(self.lex_config_case())
//             .or(lex_config_else_def(self.config_else()).map(|x| vec![x]))
//             .or(self.lex_config())
//             .or(lex_end_config_def(self.end_config()).map(|x| vec![x]))
//             .or(self.lex_group_not())
//             .or(lex_config_group_switch_def(self.group_switch()).map(|x| vec![x]))
//             .or(self.lex_group_case())
//             .or(lex_config_group_else_def(self.group_else()).map(|x| vec![x]))
//             .or(self.lex_group())
//             .or(lex_end_config_group_def(self.end_group()).map(|x| vec![x]))
//     }
// }

// trait LexStartEndSingleLineComment {
//     fn start_end_single_line_comment<'x, I, E>(
//         &self,
//     ) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }

// impl<T> LexStartEndSingleLineComment for T
// where
//     T: StartCommentSingleLine + LexCommentContents + EndCommentSingleLine,
// {
//     fn start_end_single_line_comment<'x, I, E>(
//         &self,
//     ) -> impl Parser<I, Output = Vec<LexerToken>, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         lex_start_comment_single_line(self.start_comment_single_line())
//             .and(many_till(
//                 self.lex_comment_contents(),
//                 lex_end_comment_single_line(self.end_comment_single_line()),
//             ))
//             .map(|(start, (list, end))| {
//                 let mut flattened = list.into_iter().flatten().collect::<Vec<LexerToken>>();
//                 list.insert(0, start);
//                 flattened.push(end);
//                 flattened
//             })
//     }
// }

// pub trait LexApplicability {
//     fn lex_applicability<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }

// impl<T> LexApplicability for T
// where
//     T: FeatureBase
//         + FeatureElse
//         + FeatureNot
//         + FeatureSwitch
//         + FeatureCase
//         + EndFeature
//         + ConfigBase
//         + ConfigElse
//         + ConfigNot
//         + ConfigSwitch
//         + ConfigCase
//         + EndConfig
//         + GroupBase
//         + GroupElse
//         + GroupNot
//         + GroupSwitch
//         + GroupCase
//         + EndGroup
//         + StartBrace
//         + EndBrace
//         + StartParen
//         + EndParen
//         + Not
//         + And
//         + Or
//         + Eof
//         + CarriageReturn
//         + NewLine
//         + Space
//         + StartCommentSingleLine
//         + EndCommentSingleLine
//         + StartCommentMultiLine
//         + EndCommentMultiLine
//         + MultilineCommentCharacter
//         + SingleLineComment
//         + LexStartBrace
//         + LexTagSpecialCharacters
//         + LexTagText
//         + LexFeature
//         + LexFeatureNot
//         + LexFeatureCase
//         + LexConfig
//         + LexConfigNot
//         + LexConfigCase
//         + LexGroup
//         + LexGroupNot
//         + LexGroupCase
//         + LexCommentContents
//         + LexStartEndSingleLineComment,
// {
//     fn lex_applicability<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         let start_end_single_line_comment_parser = self.start_end_single_line_comment();
//         start_end_single_line_comment_parser
//     }
// }
