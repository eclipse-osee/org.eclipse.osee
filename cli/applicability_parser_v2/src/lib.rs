use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_applicability_structure::{
    multi_line::multi_line_lexer::MultiLine,
    single_line_non_terminated::non_terminated::SingleLineNonTerminated,
    single_line_terminated::terminated::SingleLineTerminated,
};
use applicability_lexer_base::applicability_structure::LexerToken;
use applicability_lexer_chunker::chunk;
use applicability_lexer_document_structure::document_structure_parser::IdentifyComments;
use applicability_lexer_multi_stage_lexer::lexer::tokenize_comments;
use applicability_tokens_to_ast::{transform_tokens, tree::ApplicabilityExprKind};
use nom::{AsBytes, AsChar, Compare, FindSubstring, Input, Offset};
use nom_locate::LocatedSpan;
use rayon::iter::{IntoParallelIterator, ParallelIterator};

type ParseApplicabilityInput<I> = LocatedSpan<I, ((usize, u32), (usize, u32))>;

pub fn parse_applicability<I, T>(
    input: ParseApplicabilityInput<I>,
    doc: &T,
) -> Vec<ApplicabilityExprKind<I>>
where
    I: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default,
    <I as Input>::Item: AsChar,
    ApplicabilityTag<I, String>: From<I>,
    T: IdentifyComments + SingleLineTerminated + SingleLineNonTerminated + MultiLine + Sync,
{
    let tokens = tokenize_comments(doc, input)
        .into_iter()
        .map(Into::<LexerToken<I>>::into)
        .collect::<Vec<LexerToken<I>>>();
    let chunks = chunk(tokens);
    chunks
        .into_par_iter()
        .map(|chunk| transform_tokens(chunk))
        .collect()
}

#[cfg(test)]
mod tests {}
