use nom::{
    error::Error, AsBytes, AsChar, Compare, ExtendInto, FindSubstring, Input, Offset, Parser,
};
use nom_locate::LocatedSpan;
use rayon::prelude::*;
use std::fmt::{Debug, Display};
use tracing::error;

use crate::{
    base::utils::{as_str::AsStr, has_length::HasLength},
    first_stage::{first_stage_parser::IdentifyComments, token::FirstStageToken},
    second_stage::{
        multi_line::multi_line::MultiLine,
        single_line_non_terminated::non_terminated::SingleLineNonTerminated,
        single_line_terminated::terminated::SingleLineTerminated, token::LexerToken,
    },
};

// use super::{
//     multi_line::multi_line::MultiLine,
//     single_line_non_terminated::non_terminated::SingleLineNonTerminated,
//     single_line_terminated::terminated::SingleLineTerminated, token::LexerToken,
// };

pub fn tokenize_comments<T, I1, I2>(
    doc: &T,
    input: LocatedSpan<I1>,
) -> Vec<LexerToken<LocatedSpan<I2, _>>>
where
    T: IdentifyComments + SingleLineTerminated + SingleLineNonTerminated + MultiLine,
    I1: Input + for<'x> Compare<&'x str> + for<'x> FindSubstring<&'x str> + ToString + ExtendInto,
    <I1 as ExtendInto>::Extender: HasLength + Send + Sync + for<'x> AsStr<AsStrOutputType<'x> = I2>,
    <I1 as Input>::Item: AsChar,
    I2: Input
        + for<'x> FindSubstring<&'x str>
        + for<'x> Compare<&'x str>
        + Send
        + Sync
        + Display
        + Debug
        + AsBytes
        + Offset,
    I2::Item: AsChar,
{
    let results = match doc.identify_comments().parse_complete(input) {
        Ok((i2, o1)) => Ok(o1.into_iter().flat_map(|comment| {
            let res = match comment {
                FirstStageToken::SingleLineComment(content, start, end) => {
                    match doc
                        .get_single_line_non_terminated::<LocatedSpan<I2>, Error<LocatedSpan<I2>>>()
                        .parse_complete(LocatedSpan::new_extra(content.as_str(), (start, end)))
                    {
                        Ok((i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<I2, _>>>(),
                        Err(e) => {
                            error!("Error parsing single line non terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::SingleLineTerminatedComment(content, start, end) => {
                    match doc
                        .get_single_line_terminated::<LocatedSpan<I2>, Error<LocatedSpan<I2>>>()
                        .parse_complete(LocatedSpan::new_extra(content.as_str(), (start, end)))
                    {
                        Ok((i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<LocatedSpan<I2, _>>>>(),
                        Err(e) => {
                            error!("Error parsing single line terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::MultiLineComment(content, start, end) => {
                    match doc
                        .get_multi_line::<LocatedSpan<I2>, Error<LocatedSpan<I2>>>()
                        .parse_complete(LocatedSpan::new_extra(content.as_str(), (start, end)))
                    {
                        Ok((i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<LocatedSpan<I2, _>>>>(),
                        Err(e) => {
                            error!("Error parsing single line non terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::Text(content, start, end) => vec![LexerToken::Text(
                    LocatedSpan::new_extra(content.as_str(), (start, end)),
                    start,
                    end,
                )]
                .into_iter()
                .map(|x| {
                    let mut y = x.clone();
                    if start.1 > 1 {
                        y = x.increment_line_number(start.1);
                    }
                    y = y.increment_offset(start.0);

                    y
                })
                .collect::<Vec<LexerToken<LocatedSpan<I2, _>>>>(),
            };
            res.into_iter()
        })),
        Err(e) => Err(e),
    };
    results.unwrap_or(vec![])
}
