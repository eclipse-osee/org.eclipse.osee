use applicability_lexer_base::{
    applicability_structure::LexerToken, document_structure::DocumentStructureToken,
    position::TokenPosition,
};
use nom::{AsBytes, AsChar, Compare, Err, FindSubstring, Input, Offset, Parser, error::Error};
use nom_locate::LocatedSpan;

use crate::{
    multi_line::multi_line_lexer::MultiLine,
    single_line_non_terminated::non_terminated::SingleLineNonTerminated,
    single_line_terminated::terminated::SingleLineTerminated,
};

type LexerResult<I> = Result<
    Vec<LexerToken<LocatedSpan<I, TokenPosition>>>,
    Err<Error<LocatedSpan<I, TokenPosition>>>,
>;
pub fn find_applicability_structure_for_document_structure<I, T>(
    doc_config: &T,
    input: DocumentStructureToken<LocatedSpan<I, TokenPosition>>,
) -> LexerResult<I>
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
        DocumentStructureToken::SingleLineComment(content, _, _) => match doc_config
        .get_single_line_non_terminated::<LocatedSpan<I, TokenPosition>, Error<LocatedSpan<I, TokenPosition>>>(false).parse_complete(content){
            Ok(x) => Ok(x.1),
            Err(error) => Err(error),
        },
        DocumentStructureToken::SingleLineTerminatedComment(content, _, _) => match doc_config
        .get_single_line_terminated::<LocatedSpan<I, TokenPosition>, Error<LocatedSpan<I, TokenPosition>>>().parse_complete(content){
            Ok(x) => Ok(x.1),
            Err(error) => Err(error),
        },
        DocumentStructureToken::MultiLineComment(content, _, _) => match doc_config
        .get_multi_line::<LocatedSpan<I,TokenPosition>, Error<LocatedSpan<I,TokenPosition>>>().parse_complete(content){
            Ok(x) => Ok(x.1),
            Err(error) => Err(error),
        },
        DocumentStructureToken::Text(content, start, end) => Ok(vec![LexerToken::Text(
            content,
            (start,
            end,)
        )])
    }
}
