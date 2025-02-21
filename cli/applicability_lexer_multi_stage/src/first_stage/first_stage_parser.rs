// use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

// use crate::base::custom_string_traits::CustomToString;

// use super::{
//     first_stage_text::IdentifyFirstStageText,
//     multi_line_terminated::IdentifyMultiLineTerminatedComment,
//     single_line_non_terminated::IdentifySingleLineNonTerminatedComment,
//     single_line_terminated::IdentifySingleLineTerminatedComment, token::FirstStageToken,
// };

// pub trait IdentifyComments {
//     fn identify_comments<'x, I, O, E>(
//         &self,
//     ) -> impl Parser<I, Output = Vec<FirstStageToken<String>>, Error = E>
//     where
//         I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString,
//         O: CustomToString + FromIterator<I::Item>,
//         I::Item: AsChar,
//         E: ParseError<I>;
// }

// impl<T> IdentifyComments for T
// where
//     T: IdentifyFirstStageText
//         + IdentifyMultiLineTerminatedComment
//         + IdentifySingleLineNonTerminatedComment
//         + IdentifySingleLineTerminatedComment,
// {
//     fn identify_comments<'x, I, O, E>(
//         &self,
//     ) -> impl Parser<I, Output = Vec<FirstStageToken<String>>, Error = E>
//     where
//         I: Input + Compare<&'x str> + FindSubstring<&'x str> + ToString,
//         O: CustomToString + FromIterator<I::Item>,
//         I::Item: AsChar,
//         E: ParseError<I>,
//     {
//         let inner_parser = self
//             .identify_comment_single_line_terminated::<_, Vec<char>, _>()
//             .or(self.identify_comment_multi_line_terminated::<_, Vec<char>, _>())
//             .or(self.identify_comment_single_line_non_terminated::<_, Vec<char>, _>())
//             .or(self.identify_first_stage_text());
//         let parser = many0(inner_parser);
//         parser
//     }
// }
