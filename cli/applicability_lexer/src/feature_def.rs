use nom::{combinator::value, error::ParseError, Err, Parser};

use crate::LexerToken;

pub fn lex_feature_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::Feature, inner)
}
pub fn lex_feature_not_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::FeatureNot, inner)
}
pub fn lex_feature_switch_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::FeatureSwitch, inner)
}
pub fn lex_feature_case_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::FeatureCase, inner)
}
pub fn lex_feature_else_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::FeatureElse, inner)
}
pub fn lex_end_feature_def<I, F>(
    inner: F,
) -> impl Parser<I, Output = LexerToken, Error = <F as Parser<I>>::Error>
where
    F: Parser<I>,
{
    value(LexerToken::EndFeature, inner)
}
// #[cfg(test)]
// mod lex_feature_test {

//     use nom::{
//         branch::alt,
//         bytes::complete::tag,
//         error::{Error, ErrorKind, ParseError},
//         Err, Parser,
//     };

//     use super::{
//         lex_end_feature_def, lex_feature_case_def, lex_feature_def, lex_feature_else_def,
//         lex_feature_not_def, lex_feature_switch_def, LexerToken,
//     };

//     #[test]
//     fn test_feature() {
//         let inner = tag("Feature");
//         let mut parser = lex_feature_def(inner);
//         assert_eq!(
//             parser.parse_complete("Feature"),
//             Ok(("", LexerToken::Feature))
//         );
//         assert_eq!(
//             parser.parse_complete("Not the word Feature"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word Feature",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_feature2() {
//         let inner = alt((tag("Feature2"), tag("Feature")));
//         let mut parser = lex_feature_def(inner);
//         assert_eq!(parser("Feature"), Ok(("", LexerToken::Feature)));
//         assert_eq!(parser("Feature2"), Ok(("", LexerToken::Feature)));
//         assert_eq!(
//             parser("Not the word Feature"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word Feature",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_feature_not() {
//         let mut parser = lex_feature_not_def(tag("Feature Not"));
//         assert_eq!(parser("Feature Not"), Ok(("", LexerToken::FeatureNot)));
//         assert_eq!(
//             parser("Not the word Feature"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word Feature",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_feature_switch() {
//         let mut parser = lex_feature_switch_def(tag("Feature Switch"));
//         assert_eq!(
//             parser("Feature Switch"),
//             Ok(("", LexerToken::FeatureSwitch))
//         );
//         assert_eq!(
//             parser("Not the word Feature"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word Feature",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_feature_case() {
//         let mut parser = lex_feature_case_def(tag("Feature Case"));
//         assert_eq!(parser("Feature Case"), Ok(("", LexerToken::FeatureCase)));
//         assert_eq!(
//             parser("Not the word Feature"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word Feature",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_feature_else() {
//         let mut parser = lex_feature_else_def(tag("Feature Else"));
//         assert_eq!(parser("Feature Else"), Ok(("", LexerToken::FeatureElse)));
//         assert_eq!(
//             parser("Not the word Feature"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word Feature",
//                 ErrorKind::Tag
//             )))
//         );
//     }

//     #[test]
//     fn test_end_feature() {
//         let mut parser = lex_end_feature_def(tag("End Feature"));
//         assert_eq!(parser("End Feature"), Ok(("", LexerToken::EndFeature)));
//         assert_eq!(
//             parser("Not the word Feature"),
//             Err(Err::Error(Error::from_error_kind(
//                 "Not the word Feature",
//                 ErrorKind::Tag
//             )))
//         );
//     }
// }
