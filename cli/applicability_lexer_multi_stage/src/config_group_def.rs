use nom::{combinator::value, Parser};

use crate::final_stage::LexerToken;

pub fn lex_config_group_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::ConfigurationGroup, inner)
}
pub fn lex_config_group_not_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::ConfigurationGroupNot, inner)
}
pub fn lex_config_group_switch_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::ConfigurationGroupSwitch, inner)
}
pub fn lex_config_group_case_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::ConfigurationGroupCase, inner)
}
pub fn lex_config_group_else_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::ConfigurationGroupElse, inner)
}
pub fn lex_end_config_group_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::EndConfigurationGroup, inner)
}
// #[cfg(test)]
// mod lex_config_group_test {

//     use nom::{
//         branch::alt,
//         bytes::complete::tag,
//         error::{Error, ErrorKind, ParseError},
//         Err,
//     };

//     use super::{
//         lex_config_group_case_def, lex_config_group_def, lex_config_group_else_def,
//         lex_config_group_not_def, lex_config_group_switch_def, lex_end_config_group_def,
//         LexerToken,
//     };

//     #[test]
//     fn test_config_group() {
//         let inner = tag("ConfigurationGroup");
//         let mut parser = lex_config_group_def(inner);
//         assert_eq!(
//             parser("ConfigurationGroup"),
//             Ok(("", LexerToken::ConfigurationGroup))
//         );
//         assert_eq!(
//             parser("Not the word ConfigurationGroup"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word ConfigurationGroup",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_config_group2() {
//         let inner = alt((tag("ConfigurationGroup2"), tag("ConfigurationGroup")));
//         let mut parser = lex_config_group_def(inner);
//         assert_eq!(
//             parser("ConfigurationGroup"),
//             Ok(("", LexerToken::ConfigurationGroup))
//         );
//         assert_eq!(
//             parser("ConfigurationGroup2"),
//             Ok(("", LexerToken::ConfigurationGroup))
//         );
//         assert_eq!(
//             parser("Not the word ConfigurationGroup"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word ConfigurationGroup",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_config_group_not() {
//         let mut parser = lex_config_group_not_def(tag("ConfigurationGroup Not"));
//         assert_eq!(
//             parser("ConfigurationGroup Not"),
//             Ok(("", LexerToken::ConfigurationGroupNot))
//         );
//         assert_eq!(
//             parser("Not the word ConfigurationGroup"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word ConfigurationGroup",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_config_group_switch() {
//         let mut parser = lex_config_group_switch_def(tag("ConfigurationGroup Switch"));
//         assert_eq!(
//             parser("ConfigurationGroup Switch"),
//             Ok(("", LexerToken::ConfigurationGroupSwitch))
//         );
//         assert_eq!(
//             parser("Not the word ConfigurationGroup"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word ConfigurationGroup",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_config_group_case() {
//         let mut parser = lex_config_group_case_def(tag("ConfigurationGroup Case"));
//         assert_eq!(
//             parser("ConfigurationGroup Case"),
//             Ok(("", LexerToken::ConfigurationGroupCase))
//         );
//         assert_eq!(
//             parser("Not the word ConfigurationGroup"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word ConfigurationGroup",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_config_group_else() {
//         let mut parser = lex_config_group_else_def(tag("ConfigurationGroup Else"));
//         assert_eq!(
//             parser("ConfigurationGroup Else"),
//             Ok(("", LexerToken::ConfigurationGroupElse))
//         );
//         assert_eq!(
//             parser("Not the word ConfigurationGroup"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word ConfigurationGroup",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_end_config_group() {
//         let mut parser = lex_end_config_group_def(tag("End ConfigurationGroup"));
//         assert_eq!(
//             parser("End ConfigurationGroup"),
//             Ok(("", LexerToken::EndConfigurationGroup))
//         );
//         assert_eq!(
//             parser("Not the word ConfigurationGroup"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word ConfigurationGroup",
//                 ErrorKind::Tag
//             )))
//         );
//     }
// }
