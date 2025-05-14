use applicability_document_schema::StringOrByteArray;
use applicability_lexer_applicability_structure_code_block::CodeBlock;
use applicability_lexer_applicability_structure_multi_line::MultiLine;
use applicability_lexer_applicability_structure_single_line_non_terminated::SingleLineNonTerminated;
use applicability_lexer_applicability_structure_single_line_terminated::SingleLineTerminated;
use applicability_lexer_base::{
    applicability_structure::LexerToken, document_structure::DocumentStructureToken,
    position::TokenPosition,
};
use nom::{AsBytes, AsChar, Compare, Err, FindSubstring, Input, Offset, Parser, error::Error};
use nom_locate::LocatedSpan;

type LexerResult<I> = Result<
    Vec<LexerToken<LocatedSpan<I, TokenPosition>>>,
    Err<Error<LocatedSpan<I, TokenPosition>>>,
>;
pub fn find_applicability_structure_for_document_structure<'a, 'b, I, T>(
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
        + Sync
        + Default
        + 'a,
    <I as Input>::Item: AsChar,
    StringOrByteArray<'b>: From<I>,
    'a: 'b,
    T: SingleLineTerminated + SingleLineNonTerminated + MultiLine + CodeBlock + Sync,
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
        DocumentStructureToken::CodeBlock(content, _, _)=> match doc_config.get_code_block().parse_complete(content){
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
