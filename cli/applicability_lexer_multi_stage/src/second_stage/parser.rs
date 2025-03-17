use nom::{
    error::ParseError, AsBytes, AsChar, Compare, ExtendInto, FindSubstring, IResult, Input, Offset,
    Parser,
};
use nom_locate::LocatedSpan;
use rayon::prelude::*;

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
            + Offset,
        X: Into<I>,
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
            + Offset,
        X: Into<I>,
        <I as ExtendInto>::Extender: AsBytes,
        FirstStageToken<<I as ExtendInto>::Extender>: Sync,
        <I as Input>::Item: AsChar + AsBytes,
        E: ParseError<I>,
    {
        comments
            .into_iter()
            .map(|comment| match comment {
                FirstStageToken::SingleLineComment(content, start, end) => {
                    self.get_single_line_non_terminated()
                        .parse_complete(LocatedSpan::new_extra(content.into(), (start, end)))
                        .unwrap_or((LocatedSpan::new_extra("".into(), (start, end)), vec![]))
                        .1
                }
                FirstStageToken::SingleLineTerminatedComment(content, start, end) => {
                    self.get_single_line_terminated()
                        .parse_complete(LocatedSpan::new_extra(content.into(), (start, end)))
                        .unwrap_or((LocatedSpan::new_extra("".into(), (start, end)), vec![]))
                        .1
                }
                FirstStageToken::MultiLineComment(content, start, end) => {
                    self.get_multi_line()
                        .parse_complete(LocatedSpan::new_extra(content.into(), (start, end)))
                        .unwrap_or((LocatedSpan::new_extra("".into(), (start, end)), vec![]))
                        .1
                }
                FirstStageToken::Text(content, start, end) => {
                    Ok((
                        LocatedSpan::new_extra("".into(), (start, end)),
                        vec![LexerToken::Text(
                            LocatedSpan::new_extra(content.into(), (start, end)),
                            start,
                            end,
                        )],
                    ))
                    .unwrap_or((LocatedSpan::new_extra("".into(), (start, end)), vec![]))
                    .1
                }
            })
            .collect()
    }
}
