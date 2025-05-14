use applicability_document_schema::StringOrByteArray;
use applicability_lexer_applicability_structure::lexer::find_applicability_structure_for_document_structure;
use applicability_lexer_applicability_structure_code_block::CodeBlock;
use applicability_lexer_applicability_structure_multi_line::MultiLine;
use applicability_lexer_applicability_structure_single_line_non_terminated::SingleLineNonTerminated;
use applicability_lexer_applicability_structure_single_line_terminated::SingleLineTerminated;
use applicability_lexer_base::{
    applicability_structure::LexerToken,
    document_structure::{DocumentStructureError, DocumentStructureToken},
    position::TokenPosition,
};
use applicability_lexer_document_structure::document_structure_parser::IdentifyComments;
use nom::{AsBytes, AsChar, Compare, FindSubstring, Input, Offset, Parser};
use nom_locate::LocatedSpan;
use rayon::prelude::*;

#[inline(always)]
pub fn tokenize_comments<'a, 'b, T, I1>(
    doc: &T,
    input: LocatedSpan<I1, TokenPosition>,
) -> Vec<LexerToken<LocatedSpan<I1, TokenPosition>>>
where
    T: IdentifyComments
        + SingleLineTerminated
        + SingleLineNonTerminated
        + MultiLine
        + CodeBlock
        + Sync,
    I1: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default
        + 'a,
    StringOrByteArray<'b>: From<I1>,
    'a: 'b,
    <I1 as Input>::Item: AsChar,
{
    let results = tokenize_comments_parser(doc).parse_complete(input);
    match results {
        Ok((_, output)) => output,
        Err(_) => vec![],
    }
}

type TokenizeInputType<I1> = LocatedSpan<I1, TokenPosition>;
type TokenizeOutputType<I1> = Vec<LexerToken<LocatedSpan<I1, TokenPosition>>>;
type TokenizeErrorType<I1> = DocumentStructureError<LocatedSpan<I1, TokenPosition>>;
#[tracing::instrument(name = "Tokenizing comments", skip_all)]
#[inline(always)]
pub fn tokenize_comments_parser<'a, 'b, T, I1>(
    doc: &T,
) -> impl Parser<TokenizeInputType<I1>, Output = TokenizeOutputType<I1>, Error = TokenizeErrorType<I1>>
where
    T: IdentifyComments
        + SingleLineTerminated
        + SingleLineNonTerminated
        + MultiLine
        + CodeBlock
        + Sync,
    I1: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default
        + 'a,
    StringOrByteArray<'b>: From<I1>,
    'a: 'b,
    <I1 as Input>::Item: AsChar,
{
    TokenizeCommentsParser { config: doc }
}
struct TokenizeCommentsParser<'a, T>
where
    T: IdentifyComments
        + SingleLineTerminated
        + SingleLineNonTerminated
        + MultiLine
        + CodeBlock
        + Sync,
{
    config: &'a T,
}
impl<'a, 'b, I1, T> Parser<LocatedSpan<I1, TokenPosition>> for TokenizeCommentsParser<'_, T>
where
    T: IdentifyComments
        + SingleLineTerminated
        + SingleLineNonTerminated
        + MultiLine
        + CodeBlock
        + Sync,
    I1: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Default
        + 'a,
    StringOrByteArray<'b>: From<I1>,
    'a: 'b,
    <I1 as Input>::Item: AsChar,
{
    type Output = Vec<LexerToken<LocatedSpan<I1, TokenPosition>>>;

    type Error = DocumentStructureError<LocatedSpan<I1, TokenPosition>>;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: LocatedSpan<I1, TokenPosition>,
    ) -> nom::PResult<OM, LocatedSpan<I1, TokenPosition>, Self::Output, Self::Error> {
        let mut parser = self.config.identify_comments().map(|o| {
            o.into_par_iter()
                .flat_map(
                    |comment: DocumentStructureToken<LocatedSpan<I1, TokenPosition>>| {
                        let res = find_applicability_structure_for_document_structure(
                            self.config,
                            comment,
                        )
                        .unwrap_or_default();
                        res.into_par_iter()
                    },
                )
                .collect()
        });
        parser.process::<OM>(input)
    }
}
