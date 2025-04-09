use applicability_lexer_base::{
    applicability_structure::LexerToken, document_structure::DocumentStructureToken,
};
use nom::{
    error::{Error, ErrorKind, ParseError},
    AsBytes, AsChar, Compare, Err, FindSubstring, Input, Offset, Parser,
};
use nom_locate::LocatedSpan;

use crate::{
    multi_line::multi_line::MultiLine,
    single_line_non_terminated::non_terminated::SingleLineNonTerminated,
    single_line_terminated::terminated::SingleLineTerminated,
};

pub fn find_applicability_structure_for_document_structure<I, T>(
    doc_config: &T,
    input: DocumentStructureToken<LocatedSpan<I, ((usize, u32), (usize, u32))>>,
) -> Result<
    Vec<LexerToken<LocatedSpan<I, ((usize, u32), (usize, u32))>>>,
    Err<Error<LocatedSpan<I, ((usize, u32), (usize, u32))>>>,
>
where
    I: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync,
    <I as Input>::Item: AsChar,
    T: SingleLineTerminated + SingleLineNonTerminated + MultiLine + Sync,
{
    match input {
        DocumentStructureToken::SingleLineComment(content, start, _) => match doc_config
        .get_single_line_non_terminated::<LocatedSpan<I, ((usize, u32), (usize, u32))>, Error<LocatedSpan<I, ((usize, u32), (usize, u32))>>>().parse_complete(content){
            Ok(x) => Ok(increment_offsets(x.1, start)),
            Err(error) => Err(error),
        },
        DocumentStructureToken::SingleLineTerminatedComment(content, start, _) => match doc_config
        .get_single_line_terminated::<LocatedSpan<I, ((usize, u32), (usize, u32))>, Error<LocatedSpan<I, ((usize, u32), (usize, u32))>>>().parse_complete(content){
            Ok(x) => Ok(increment_offsets(x.1, start)),
            Err(error) => Err(error),
        },
        DocumentStructureToken::MultiLineComment(content, start, _) => match doc_config
        .get_multi_line::<LocatedSpan<I,((usize, u32), (usize, u32))>, Error<LocatedSpan<I,((usize, u32), (usize, u32))>>>().parse_complete(content){
            Ok(x) => Ok(increment_offsets(x.1, start)),
            Err(error) => Err(error),
        },
        DocumentStructureToken::Text(content, start, end) => Ok(increment_offsets(vec![LexerToken::Text(
            content,
            start,
            end,
        )],start))
    }
}

fn increment_offsets<I>(
    input: Vec<LexerToken<LocatedSpan<I, ((usize, u32), (usize, u32))>>>,
    start: (usize, u32),
) -> Vec<LexerToken<LocatedSpan<I, ((usize, u32), (usize, u32))>>>
where
    I: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync,
    <I as Input>::Item: AsChar,
{
    input
        .into_iter()
        .map(|x| {
            let mut y = x.clone();
            if start.1 > 1 {
                y = x.increment_line_number(start.1);
            }
            y = y.increment_offset(start.0);

            y
        })
        .collect::<Vec<LexerToken<LocatedSpan<I, ((usize, u32), (usize, u32))>>>>()
}
