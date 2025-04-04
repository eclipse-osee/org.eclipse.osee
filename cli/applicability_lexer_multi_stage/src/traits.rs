// use nom::{
//     bytes::tag,
//     character::{
//         char,
//         complete::{newline, space1},
//     },
//     combinator::eof,
//     error::ParseError,
//     AsChar, Compare, Input, Parser,
// };

// pub trait DefaultApplicabilityLexer {}

// pub trait FeatureBase {
//     fn feature_base<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>;
// }

// impl<T> FeatureBase for T
// where
//     T: DefaultApplicabilityLexer,
// {
//     fn feature_base<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Feature")
//     }
// }

// pub trait FeatureElse {
//     fn feature_else<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Feature Else")
//     }
// }

// impl<T> FeatureElse for T where T: DefaultApplicabilityLexer {}

// pub trait FeatureNot {
//     fn feature_not<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Feature Not")
//     }
// }

// impl<T> FeatureNot for T where T: DefaultApplicabilityLexer {}

// pub trait FeatureSwitch {
//     fn feature_switch<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Feature Switch")
//     }
// }

// impl<T> FeatureSwitch for T where T: DefaultApplicabilityLexer {}

// pub trait FeatureCase {
//     fn feature_case<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Feature Case")
//     }
// }

// impl<T> FeatureCase for T where T: DefaultApplicabilityLexer {}

// pub trait EndFeature {
//     fn end_feature<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("End Feature")
//     }
// }

// impl<T> EndFeature for T where T: DefaultApplicabilityLexer {}
// //Configuration
// pub trait ConfigBase {
//     fn config_base<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Configuration")
//     }
// }

// impl<T> ConfigBase for T where T: DefaultApplicabilityLexer {}

// pub trait ConfigElse {
//     fn config_else<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Configuration Else")
//     }
// }

// impl<T> ConfigElse for T where T: DefaultApplicabilityLexer {}

// pub trait ConfigNot {
//     fn config_not<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Configuration Not")
//     }
// }

// impl<T> ConfigNot for T where T: DefaultApplicabilityLexer {}

// pub trait ConfigSwitch {
//     fn config_switch<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Configuration Switch")
//     }
// }

// impl<T> ConfigSwitch for T where T: DefaultApplicabilityLexer {}

// pub trait ConfigCase {
//     fn config_case<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("Configuration Case")
//     }
// }

// impl<T> ConfigCase for T where T: DefaultApplicabilityLexer {}

// pub trait EndConfig {
//     fn end_config<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("End Configuration")
//     }
// }

// impl<T> EndConfig for T where T: DefaultApplicabilityLexer {}

// //Configuration Group
// pub trait GroupBase {
//     fn group_base<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("ConfigurationGroup")
//     }
// }

// impl<T> GroupBase for T where T: DefaultApplicabilityLexer {}

// pub trait GroupElse {
//     fn group_else<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("ConfigurationGroup Else")
//     }
// }

// impl<T> GroupElse for T where T: DefaultApplicabilityLexer {}

// pub trait GroupNot {
//     fn group_not<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("ConfigurationGroup Not")
//     }
// }

// impl<T> GroupNot for T where T: DefaultApplicabilityLexer {}

// pub trait GroupSwitch {
//     fn group_switch<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("ConfigurationGroup Switch")
//     }
// }

// impl<T> GroupSwitch for T where T: DefaultApplicabilityLexer {}

// pub trait GroupCase {
//     fn group_case<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("ConfigurationGroup Case")
//     }
// }

// impl<T> GroupCase for T where T: DefaultApplicabilityLexer {}

// pub trait EndGroup {
//     fn end_group<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>,
//     {
//         tag("End ConfigurationGroup")
//     }
// }

// impl<T> EndGroup for T where T: DefaultApplicabilityLexer {}

// // Base Capabilities
// pub trait Space {
//     fn is_space<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//     {
//         input.as_char() == ' '
//     }
//     fn space<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         space1
//     }
// }

// impl<T> Space for T where T: DefaultApplicabilityLexer {}

// pub trait NewLine {
//     fn is_new_line<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//     {
//         input.as_char() == '\n'
//     }
//     fn newline<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         newline
//     }
// }

// impl<T> NewLine for T where T: DefaultApplicabilityLexer {}

// pub trait CarriageReturn {
//     fn is_carriage_return<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//     {
//         input.as_char() == '\r'
//     }
//     fn carriage_return<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         char('\r')
//     }
// }

// impl<T> CarriageReturn for T where T: DefaultApplicabilityLexer {}

// pub trait StartBrace {
//     fn is_start_brace<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar,
//     {
//         input.as_char() == '['
//     }
//     fn start_brace<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         char('[')
//     }
// }

// impl<T> StartBrace for T where T: DefaultApplicabilityLexer {}

// pub trait EndBrace {
//     fn is_end_brace<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar,
//     {
//         input.as_char() == ']'
//     }
//     fn end_brace<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         char(']')
//     }
// }

// impl<T> EndBrace for T where T: DefaultApplicabilityLexer {}

// pub trait StartParen {
//     fn is_start_paren<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//     {
//         input.as_char() == '('
//     }
//     fn start_paren<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         char('(')
//     }
// }

// impl<T> StartParen for T where T: DefaultApplicabilityLexer {}

// pub trait EndParen {
//     fn is_end_paren<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//     {
//         input.as_char() == ')'
//     }
//     fn end_paren<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         char(')')
//     }
// }

// impl<T> EndParen for T where T: DefaultApplicabilityLexer {}

// pub trait Not {
//     fn is_not<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//     {
//         input.as_char() == '!'
//     }
//     fn not<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         char('!')
//     }
// }

// impl<T> Not for T where T: DefaultApplicabilityLexer {}

// pub trait And {
//     fn is_and<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//     {
//         input.as_char() == '&'
//     }
//     fn and<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         char('&')
//     }
// }

// impl<T> And for T where T: DefaultApplicabilityLexer {}

// pub trait Or {
//     fn is_or<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar,
//     {
//         input.as_char() == '|'
//     }
//     fn or<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         char('|')
//     }
// }

// impl<T> Or for T where T: DefaultApplicabilityLexer {}

// pub trait Eof {
//     fn eof<I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input,
//         E: ParseError<I>,
//     {
//         eof
//     }
// }

// impl<T> Eof for T where T: DefaultApplicabilityLexer {}

// // COMMENT SYNTAXES
// pub trait StartCommentSingleLineTerminated {
//     fn is_start_comment_single_line_terminated<I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar;
//     fn start_comment_single_line_terminated<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>;
// }

// // impl StartCommentSingleLineTerminated for MarkdownDocumentConfig {
// //     fn is_start_comment_single_line_terminated<'x, I>(&self, input: I) -> bool
// //     where
// //         I: Input + Compare<&'x str>,
// //     {
// //         match input.compare("``") {
// //             nom::CompareResult::Ok => true,
// //             //TODO figure out what to do here
// //             nom::CompareResult::Incomplete => false,
// //             nom::CompareResult::Error => false,
// //         }
// //     }
// //     fn start_comment_single_line_terminated<'x, I, E>(&self) -> impl Parser<I, Error = E>
// //     where
// //         I: Input + Compare<&'x str>,
// //         E: ParseError<I>,
// //     {
// //         tag("``")
// //     }
// // }

// pub trait EndCommentSingleLineTerminated {
//     fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar;
//     fn end_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>;
// }

// // impl EndCommentSingleLineTerminated for MarkdownDocumentConfig {
// //     fn is_end_comment_single_line<'x, I>(&self, input: I) -> bool
// //     where
// //         I: Input + Compare<&'x str>,
// //     {
// //         //TODO: look for how to add is_newline functionality here as well
// //         match input.compare("``") {
// //             nom::CompareResult::Ok => true,
// //             //TODO figure out what to do here
// //             nom::CompareResult::Incomplete => false,
// //             nom::CompareResult::Error => false,
// //         }
// //     }
// //     fn end_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Error = E>
// //     where
// //         I: Input + Compare<&'x str>,
// //         E: ParseError<I>,
// //     {
// //         tag("``")
// //     }
// // }

// pub trait StartCommentMultiLine {
//     fn is_start_comment_multi_line<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar;
//     fn start_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>;
// }

// // impl StartCommentMultiLine for MarkdownDocumentConfig {
// //     fn is_start_comment_multi_line<'x, I>(&self, input: I) -> bool
// //     where
// //         I: Input + Compare<&'x str>,
// //     {
// //         false
// //     }
// //     fn start_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Error = E>
// //     where
// //         I: Input + Compare<&'x str>,
// //         E: ParseError<I>,
// //     {
// //         fail::<I, PhantomData<LexerToken>, E>()
// //     }
// // }

// pub trait EndCommentMultiLine {
//     fn is_end_comment_multi_line<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar;
//     fn end_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>;
// }

// // impl EndCommentMultiLine for MarkdownDocumentConfig {
// //     fn is_end_comment_multi_line<'x, I>(&self, input: I) -> bool
// //     where
// //         I: Input + Compare<&'x str>,
// //     {
// //         false
// //     }
// //     fn end_comment_multi_line<'x, I, E>(&self) -> impl Parser<I, Error = E>
// //     where
// //         I: Input + Compare<&'x str>,
// //         E: ParseError<I>,
// //     {
// //         fail::<I, PhantomData<LexerToken>, E>()
// //     }
// // }

// pub trait MultilineCommentCharacter {
//     fn is_multi_line_comment_character<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar;
//     fn multi_line_comment_character<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>;
// }

// // impl MultilineCommentCharacter for MarkdownDocumentConfig {
// //     fn is_multi_line_comment_character<'x, I>(&self, input: I) -> bool
// //     where
// //         I: Input + Compare<&'x str>,
// //     {
// //         false
// //     }
// //     fn multi_line_comment_character<'x, I, E>(&self) -> impl Parser<I, Error = E>
// //     where
// //         I: Input + Compare<&'x str>,
// //         E: ParseError<I>,
// //     {
// //         fail::<I, PhantomData<LexerToken>, E>()
// //     }
// // }

// pub trait SingleLineComment {
//     fn is_single_line_comment<'x, I>(&self, input: I::Item) -> bool
//     where
//         I: Input + Compare<&'x str>,
//         I::Item: AsChar;
//     fn single_line_comment<'x, I, E>(&self) -> impl Parser<I, Error = E>
//     where
//         I: Input + Compare<&'x str>,
//         E: ParseError<I>;
// }

// // impl SingleLineComment for MarkdownDocumentConfig {
// //     fn is_single_line_comment<'x, I>(&self, input: I) -> bool
// //     where
// //         I: Input + Compare<&'x str>,
// //     {
// //         false
// //     }
// //     fn single_line_comment<'x, I, E>(&self) -> impl Parser<I, Error = E>
// //     where
// //         I: Input + Compare<&'x str>,
// //         E: ParseError<I>,
// //     {
// //         fail::<I, PhantomData<LexerToken>, E>()
// //     }
// // }
