use applicability_document_schema::StringOrByteArray;
use applicability_lexer_applicability_structure_multi_line::MultiLine;
use applicability_lexer_applicability_structure_single_line_non_terminated::SingleLineNonTerminated;
use applicability_lexer_applicability_structure_single_line_terminated::SingleLineTerminated;
use applicability_lexer_base::{
    applicability_structure::LexerToken,
    document_structure::{DocumentStructureError, DocumentStructureToken},
    position::TokenPosition,
};
use applicability_lexer_document_structure::document_structure_parser::IdentifyComments;
use nom::{AsBytes, AsChar, Compare, FindSubstring, Input, Offset, Parser, error::Error};
use nom_locate::LocatedSpan;
use rayon::prelude::*;

use crate::CodeBlock;

#[inline(always)]
pub fn tokenize_others<T, I1>(
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
        + Default,
    <I1 as Input>::Item: AsChar,
    for<'x> StringOrByteArray<'x>: From<I1>,
{
    let results = tokenize_others_parser(doc).parse_complete(input);
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
pub fn tokenize_others_parser<'a, 'b, T, I1>(
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
    <I1 as Input>::Item: AsChar,
    StringOrByteArray<'b>: From<I1>,
    'a: 'b,
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

#[allow(clippy::extra_unused_lifetimes)]
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
    <I1 as Input>::Item: AsChar,
    StringOrByteArray<'b>: From<I1>,
    'a: 'b,
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
                        let res = match comment {
                            DocumentStructureToken::SingleLineComment(content, _, _) => match self.config
                            .get_single_line_non_terminated::<LocatedSpan<I1, TokenPosition>, Error<LocatedSpan<I1, TokenPosition>>>(false).parse_complete(content){
                                Ok(x) => Ok(x.1),
                                Err(error) => Err(error),
                            },
                            DocumentStructureToken::SingleLineTerminatedComment(content, _, _) => match self.config
                            .get_single_line_terminated::<LocatedSpan<I1, TokenPosition>, Error<LocatedSpan<I1, TokenPosition>>>().parse_complete(content){
                                Ok(x) => Ok(x.1),
                                Err(error) => Err(error),
                            },
                            DocumentStructureToken::MultiLineComment(content, _, _) => match self.config
                            .get_multi_line::<LocatedSpan<I1,TokenPosition>, Error<LocatedSpan<I1,TokenPosition>>>().parse_complete(content){
                                Ok(x) => Ok(x.1),
                                Err(error) => Err(error),
                            },
                            DocumentStructureToken::CodeBlock(content, _, _)=> match self.config.get_code_block::<I1, Error<LocatedSpan<I1,TokenPosition>>>().parse_complete(content){
                                Ok(x) => Ok(x.1),
                                Err(error) => Err(error),
                            },
                            DocumentStructureToken::Text(content, start, end) => Ok(vec![LexerToken::Text(
                                content,
                                (start,
                                end,)
                            )])
                        }.unwrap_or_default();
                        res.into_par_iter()
                    },
                )
                .collect()
        });
        parser.process::<OM>(input)
    }
}
