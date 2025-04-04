// use nom::{combinator::value, Parser};

// use crate::final_stage::LexerToken;

// pub fn lex_config_def<I, F>(
//     inner: F,
// ) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
// where
//     F: Parser<I>,
// {
//     value(LexerToken::Configuration, inner)
// }
// pub fn lex_config_not_def<I, F>(
//     inner: F,
// ) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
// where
//     F: Parser<I>,
// {
//     value(LexerToken::ConfigurationNot, inner)
// }
// pub fn lex_config_switch_def<I, F>(
//     inner: F,
// ) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
// where
//     F: Parser<I>,
// {
//     value(LexerToken::ConfigurationSwitch, inner)
// }
// pub fn lex_config_case_def<I, F>(
//     inner: F,
// ) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
// where
//     F: Parser<I>,
// {
//     value(LexerToken::ConfigurationCase, inner)
// }
// pub fn lex_config_else_def<I, F>(
//     inner: F,
// ) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
// where
//     F: Parser<I>,
// {
//     value(LexerToken::ConfigurationElse, inner)
// }
// pub fn lex_end_config_def<I, F>(
//     inner: F,
// ) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
// where
//     F: Parser<I>,
// {
//     value(LexerToken::EndConfiguration, inner)
// }
// // #[cfg(test)]
// // mod lex_config_test {

// //     use nom::{
// //         branch::alt,
// //         bytes::complete::tag,
// //         error::{Error, ErrorKind, ParseError},
// //         Err,
// //     };

// //     use super::{
// //         lex_config_case_def, lex_config_def, lex_config_else_def, lex_config_not_def,
// //         lex_config_switch_def, lex_end_config_def, LexerToken,
// //     };

// //     #[test]
// //     fn test_config() {
// //         let inner = tag("Configuration");
// //         let mut parser = lex_config_def(inner);
// //         assert_eq!(parser("Configuration"), Ok(("", LexerToken::Configuration)));
// //         assert_eq!(
// //             parser("Not the word Configuration"),
// //             Err(Err::Error(Error::from_error_kind(
// //                 "Not the word Configuration",
// //                 ErrorKind::Tag
// //             )))
// //         );
// //     }

// //     #[test]
// //     fn test_config2() {
// //         let inner = alt((tag("Configuration2"), tag("Configuration")));
// //         let mut parser = lex_config_def(inner);
// //         assert_eq!(parser("Configuration"), Ok(("", LexerToken::Configuration)));
// //         assert_eq!(
// //             parser("Configuration2"),
// //             Ok(("", LexerToken::Configuration))
// //         );
// //         assert_eq!(
// //             parser("Not the word Configuration"),
// //             Err(Err::Error(Error::from_error_kind(
// //                 "Not the word Configuration",
// //                 ErrorKind::Tag
// //             )))
// //         );
// //     }

// //     #[test]
// //     fn test_config_not() {
// //         let mut parser = lex_config_not_def(tag("Configuration Not"));
// //         assert_eq!(
// //             parser("Configuration Not"),
// //             Ok(("", LexerToken::ConfigurationNot))
// //         );
// //         assert_eq!(
// //             parser("Not the word Configuration"),
// //             Err(Err::Error(Error::from_error_kind(
// //                 "Not the word Configuration",
// //                 ErrorKind::Tag
// //             )))
// //         );
// //     }

// //     #[test]
// //     fn test_config_switch() {
// //         let mut parser = lex_config_switch_def(tag("Configuration Switch"));
// //         assert_eq!(
// //             parser("Configuration Switch"),
// //             Ok(("", LexerToken::ConfigurationSwitch))
// //         );
// //         assert_eq!(
// //             parser("Not the word Configuration"),
// //             Err(Err::Error(Error::from_error_kind(
// //                 "Not the word Configuration",
// //                 ErrorKind::Tag
// //             )))
// //         );
// //     }

// //     #[test]
// //     fn test_config_case() {
// //         let mut parser = lex_config_case_def(tag("Configuration Case"));
// //         assert_eq!(
// //             parser("Configuration Case"),
// //             Ok(("", LexerToken::ConfigurationCase))
// //         );
// //         assert_eq!(
// //             parser("Not the word Configuration"),
// //             Err(Err::Error(Error::from_error_kind(
// //                 "Not the word Configuration",
// //                 ErrorKind::Tag
// //             )))
// //         );
// //     }

// //     #[test]
// //     fn test_config_else() {
// //         let mut parser = lex_config_else_def(tag("Configuration Else"));
// //         assert_eq!(
// //             parser("Configuration Else"),
// //             Ok(("", LexerToken::ConfigurationElse))
// //         );
// //         assert_eq!(
// //             parser("Not the word Configuration"),
// //             Err(Err::Error(Error::from_error_kind(
// //                 "Not the word Configuration",
// //                 ErrorKind::Tag
// //             )))
// //         );
// //     }

// //     #[test]
// //     fn test_end_config() {
// //         let mut parser = lex_end_config_def(tag("End Configuration"));
// //         assert_eq!(
// //             parser("End Configuration"),
// //             Ok(("", LexerToken::EndConfiguration))
// //         );
// //         assert_eq!(
// //             parser("Not the word Configuration"),
// //             Err(Err::Error(Error::from_error_kind(
// //                 "Not the word Configuration",
// //                 ErrorKind::Tag
// //             )))
// //         );
// //     }
// // }
