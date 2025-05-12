use applicability_lexer_applicability_structure::{
    lexer::find_applicability_structure_for_document_structure,
    multi_line::multi_line_lexer::MultiLine,
    single_line_non_terminated::non_terminated::SingleLineNonTerminated,
    single_line_terminated::terminated::SingleLineTerminated,
};
use applicability_lexer_base::{
    applicability_structure::LexerToken, document_structure::DocumentStructureToken,
    position::TokenPosition,
};
use applicability_lexer_document_structure::document_structure_parser::IdentifyComments;
use nom::{AsBytes, AsChar, Compare, FindSubstring, Input, Offset, Parser};
use nom_locate::LocatedSpan;
use rayon::prelude::*;

#[tracing::instrument(name = "Tokenizing comments", skip_all)]
#[inline(always)]
pub fn tokenize_comments<T, I1>(
    doc: &T,
    input: LocatedSpan<I1, TokenPosition>,
) -> Vec<LexerToken<LocatedSpan<I1, TokenPosition>>>
where
    T: IdentifyComments + SingleLineTerminated + SingleLineNonTerminated + MultiLine + Sync,
    I1: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default,
    <I1 as Input>::Item: AsChar,
{
    let results = match doc
        .identify_comments::<LocatedSpan<I1, TokenPosition>>()
        .parse_complete(input)
    {
        Ok((_i, o1)) => Ok(o1
            .into_par_iter()
            .flat_map(
                |comment: DocumentStructureToken<LocatedSpan<I1, TokenPosition>>| {
                    let res = find_applicability_structure_for_document_structure(doc, comment)
                        .unwrap_or_default();
                    res.into_par_iter()
                },
            )
            .collect()),
        Err(e) => Err(e),
    };
    results.unwrap_or(vec![])
}
