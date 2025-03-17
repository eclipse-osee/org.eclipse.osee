use nom::{
    error::{Error, ParseError},
    AsBytes, AsChar, Compare, ExtendInto, FindSubstring, IResult, Input, Offset, Parser,
};
use nom_locate::LocatedSpan;
use rayon::prelude::*;
use std::fmt::Debug;

use crate::{base::utils::locatable::Locatable, first_stage::token::FirstStageToken};

use super::{
    multi_line::multi_line::MultiLine,
    single_line_non_terminated::non_terminated::SingleLineNonTerminated,
    single_line_terminated::terminated::SingleLineTerminated, token::LexerToken,
};

pub trait TokenizeComments {
    fn tokenize_comments<I, X, E>(&self, comments: Vec<FirstStageToken<X>>) -> Vec<LexerToken<I>>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + for<'x> From<&'x str>
            + Locatable
            + ExtendInto
            + AsBytes
            + Offset
            + Debug,
        X: Into<I> + Into<Vec<LexerToken<I>>>,
        <I as ExtendInto>::Extender: AsBytes,
        FirstStageToken<<I as ExtendInto>::Extender>: Sync,
        <I as Input>::Item: AsChar + AsBytes,
        E: ParseError<I>;
}

impl<T> TokenizeComments for T
where
    T: SingleLineTerminated + SingleLineNonTerminated + MultiLine,
{
    fn tokenize_comments<I, X, E>(&self, comments: Vec<FirstStageToken<X>>) -> Vec<LexerToken<I>>
    // impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + for<'x> From<&'x str>
            + Locatable
            + ExtendInto
            + AsBytes
            + Offset
            + Debug,
        X: Into<I> + Into<Vec<LexerToken<I>>>,
        <I as ExtendInto>::Extender: AsBytes,
        FirstStageToken<<I as ExtendInto>::Extender>: Sync,
        <I as Input>::Item: AsChar + AsBytes,
        E: ParseError<I>,
    {
        comments
            .into_iter()
            .flat_map(|comment| match comment {
                FirstStageToken::SingleLineComment(content, start, end) => {
                    self.get_single_line_non_terminated::<I, Error<I>>()
                        // .parse_complete(LocatedSpan::new_extra(
                        //     Into::<I>::into(content),
                        //     (start, end),
                        // ))
                        .parse_complete(Into::<I>::into(content))
                        .unwrap()
                        // .unwrap_or((LocatedSpan::new_extra("".into(), (start, end)), vec![]).into())
                        .1
                        .into_iter()
                        .map(|x| {
                            let mut y = x.clone();
                            if start.1 > 1 {
                                y = x.increment_line_number(start.1);
                            }
                            y = y.increment_offset(start.0);

                            y
                        })
                        .collect::<Vec<_>>()
                }
                FirstStageToken::SingleLineTerminatedComment(content, start, end) => {
                    self.get_single_line_terminated::<I, Error<I>>()
                        // .parse_complete(LocatedSpan::new_extra(
                        //     Into::<I>::into(content),
                        //     (start, end),
                        // ))
                        .parse_complete(Into::<I>::into(content))
                        .unwrap()
                        // .unwrap_or((LocatedSpan::new_extra("".into(), (start, end)), vec![]).into())
                        .1
                        .into_iter()
                        .map(|x| {
                            let mut y = x.clone();
                            if start.1 > 1 {
                                y = x.increment_line_number(start.1);
                            }
                            y = y.increment_offset(start.0);

                            y
                        })
                        .collect::<Vec<_>>()
                }
                FirstStageToken::MultiLineComment(content, start, end) => {
                    self.get_multi_line::<I, Error<I>>()
                        // .parse_complete(LocatedSpan::new_extra(
                        //     Into::<I>::into(content),
                        //     (start, end),
                        // ))
                        .parse_complete(Into::<I>::into(content))
                        .unwrap()
                        // .unwrap_or((LocatedSpan::new_extra("".into(), (start, end)), vec![]).into())
                        .1
                        .into_iter()
                        .map(|x| {
                            let mut y = x.clone();
                            if start.1 > 1 {
                                y = x.increment_line_number(start.1);
                            }
                            y = y.increment_offset(start.0);

                            y
                        })
                        .collect::<Vec<_>>()
                }
                FirstStageToken::Text(content, start, end) => {
                    // Ok((
                    //     LocatedSpan::new_extra(std::convert::Into::<I>::into(""), (start, end))
                    //         .into(),
                    //     vec![LexerToken::Text(
                    //         LocatedSpan::new_extra(content.into(), (start, end)).into(),
                    //         start,
                    //         end,
                    //     )],
                    // ))
                    // .unwrap_or((LocatedSpan::new_extra("".into(), (start, end)), vec![]).into())
                    // .1
                    // content.into()
                    std::convert::Into::<Vec<LexerToken<I>>>::into(content)
                        .into_iter()
                        .map(|x| {
                            let mut y = x.clone();
                            if start.1 > 1 {
                                y = x.increment_line_number(start.1);
                            }
                            y = y.increment_offset(start.0);

                            y
                        })
                        .collect::<Vec<_>>()
                }
            })
            .collect()
    }
}
