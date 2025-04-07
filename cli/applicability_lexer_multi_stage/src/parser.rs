use nom::{
    error::{Error, ParseError},
    AsBytes, AsChar, Compare, ExtendInto, FindSubstring, Input, Offset, Parser,
};
use nom_locate::LocatedSpan;
use rayon::prelude::*;
use std::fmt::{Debug, Display};
use tracing::error;

use crate::second_stage::{
    multi_line::multi_line::MultiLine,
    single_line_non_terminated::non_terminated::SingleLineNonTerminated,
    single_line_terminated::terminated::SingleLineTerminated, token::LexerToken,
};
use applicability_lexer_base::utils::{as_str::AsStr, has_length::HasLength};
use applicability_lexer_first_stage::{
    first_stage_parser::IdentifyComments, token::FirstStageToken,
};

pub fn tokenize_comments<T, I1, I2, E1, E2>(
    doc: &T,
    input: LocatedSpan<I1, ((usize, u32), (usize, u32))>,
) -> Vec<LexerToken<LocatedSpan<I1, ((usize, u32), (usize, u32))>>>
where
    T: IdentifyComments + SingleLineTerminated + SingleLineNonTerminated + MultiLine + Sync,
    I1: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Debug
        + Display,
    // <I1 as ExtendInto>::Extender: HasLength + Send + Sync + for<'x> AsStr<AsStrOutputType<'x> = I2>,
    // <I1 as ExtendInto>::Extender: 'static,
    <I1 as Input>::Item: AsChar,
    E1: ParseError<LocatedSpan<I1, ((usize, u32), (usize, u32))>>,
    E2: ParseError<LocatedSpan<I2, ((usize, u32), (usize, u32))>>,
    // I2: Input
    //     + for<'x> FindSubstring<&'x str>
    //     + for<'x> Compare<&'x str>
    //     + Send
    //     + Sync
    //     + Display
    //     + Debug
    //     + AsBytes
    //     + Offset,
    // I2::Item: AsChar,
{
    let results = match doc.identify_comments::<LocatedSpan<I1, ((usize, u32), (usize, u32))>>().parse_complete(input) {
        Ok((_i, o1)) => Ok(o1.into_par_iter().flat_map(|comment:FirstStageToken<LocatedSpan<I1, ((usize, u32), (usize, u32))>>| {
            let res = match comment {
                FirstStageToken::SingleLineComment(content, start, _end) => {
                    match doc
                        .get_single_line_non_terminated::<LocatedSpan<I1, ((usize, u32), (usize, u32))>, Error<LocatedSpan<I1, ((usize, u32), (usize, u32))>>>()
                        .parse_complete(content)
                    {
                        Ok((_i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<LocatedSpan<I1,((usize, u32), (usize, u32))>>>>(),
                        Err(e) => {
                            error!("Error parsing single line non terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::SingleLineTerminatedComment(content, start, _end) => {
                    match doc
                        .get_single_line_terminated::<LocatedSpan<I1, ((usize, u32), (usize, u32))>, Error<LocatedSpan<I1, ((usize, u32), (usize, u32))>>>()
                        .parse_complete(content)
                    {
                        Ok((_i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<LocatedSpan<I1,((usize, u32), (usize, u32))>>>>(),
                        Err(e) => {
                            error!("Error parsing single line terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::MultiLineComment(content, start, _end) => {
                    match doc
                        .get_multi_line::<LocatedSpan<I1,((usize, u32), (usize, u32))>, Error<LocatedSpan<I1,((usize, u32), (usize, u32))>>>()
                        .parse_complete(content)
                    {
                        Ok((_i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<LocatedSpan<I1,((usize, u32), (usize, u32))>>>>(),
                        Err(e) => {
                            error!("Error parsing single line non terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::Text(content, start, end) => vec![LexerToken::Text(
                    content,
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
                .collect::<Vec<LexerToken<LocatedSpan<I1, ((usize, u32), (usize, u32))>>>>(),
            };
            res.into_par_iter()
        }).collect()),
        Err(e) => Err(e),
    };
    results.unwrap_or(vec![])
}
