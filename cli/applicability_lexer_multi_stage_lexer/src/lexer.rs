/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
use applicability_document_schema::StringOrByteArray;
use applicability_lexer_applicability_structure::lexer::find_applicability_structure_for_document_structure;
use applicability_lexer_applicability_structure_code_block::CodeBlock;
use applicability_lexer_applicability_structure_multi_line::MultiLine;
use applicability_lexer_applicability_structure_single_line_non_terminated::SingleLineNonTerminated;
use applicability_lexer_applicability_structure_single_line_terminated::SingleLineTerminated;
use applicability_lexer_base::{
    applicability_structure::LexerToken, document_structure::DocumentStructureToken,
    position::TokenPosition,
};
use applicability_lexer_document_structure::document_structure_parser::IdentifyComments;
use applicability_parser_errors::ApplicabilityParserInternalErrorWithNomInputs;
use itertools::Itertools;
use nom::{AsBytes, AsChar, Compare, Err, FindSubstring, Input, Offset, Parser, error::Error};
use nom_locate::LocatedSpan;
//TODO: remove this clippy lint allow, we need to fix rayon not working properly here
#[allow(unused_imports)]
use rayon::prelude::*;

type TokenizeCommentsError<I1> = ApplicabilityParserInternalErrorWithNomInputs<LocatedSpan<I1, TokenPosition>>;
type TokenizeCommentsInput<I1> = LocatedSpan<I1, TokenPosition>;
type TokenizeCommentsResult<I1> = Vec<LexerToken<LocatedSpan<I1, TokenPosition>>>;

#[inline(always)]
pub fn tokenize_comments<'a, 'b, T, I1>(
    doc: &T,
    input: LocatedSpan<I1, TokenPosition>,
) -> Result<(TokenizeCommentsInput<I1>, TokenizeCommentsResult<I1>), TokenizeCommentsError<I1>>
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
    match tokenize_comments_parser(doc).parse_complete(input) {
        Ok(res) => Ok(res),
        Result::Err(e) => match e {
            Err::Incomplete(needed) => match needed {
                nom::Needed::Unknown => Err(ApplicabilityParserInternalErrorWithNomInputs::NeedsUnknownMoreData),
                nom::Needed::Size(non_zero) => {
                    Err(ApplicabilityParserInternalErrorWithNomInputs::NeedsMoreData(non_zero.into()))
                }
            },
            Err::Error(e) => Err(e),
            Err::Failure(e) => Err(e),
        },
    }
}

type TokenizeInputType<I1> = LocatedSpan<I1, TokenPosition>;
type TokenizeOutputType<I1> = Vec<LexerToken<LocatedSpan<I1, TokenPosition>>>;
type TokenizeErrorType<I1> =
    ApplicabilityParserInternalErrorWithNomInputs<LocatedSpan<I1, TokenPosition>, Error<LocatedSpan<I1, TokenPosition>>>;
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
    StringOrByteArray<'b>: From<I1>,
    'a: 'b,
    <I1 as Input>::Item: AsChar,
{
    type Output = Vec<LexerToken<LocatedSpan<I1, TokenPosition>>>;

    type Error = ApplicabilityParserInternalErrorWithNomInputs<
        LocatedSpan<I1, TokenPosition>,
        Error<LocatedSpan<I1, TokenPosition>>,
    >;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: LocatedSpan<I1, TokenPosition>,
    ) -> nom::PResult<OM, LocatedSpan<I1, TokenPosition>, Self::Output, Self::Error> {
        let mut parser = self
            .config
            .identify_comments()
            .map_res(|o| {
                let res: Result<
                    TokenizeCommentsResult<I1>,
                    Err<Error<LocatedSpan<I1, TokenPosition>>>,
                > = o
                    .into_iter()
                    .map(
                        |comment: DocumentStructureToken<LocatedSpan<I1, TokenPosition>>| {
                            find_applicability_structure_for_document_structure(
                                self.config,
                                comment,
                            )
                        },
                    )
                    .flatten_ok()
                    .collect();
                Ok(res)
            })
            .map(|x| unsafe { x.unwrap_unchecked() });
        parser.process::<OM>(input)
    }
}

#[cfg(test)]
mod tests {
    use applicability_lexer_config_markdown::ApplicabilityMarkdownLexerConfig;
    use nom_locate::LocatedSpan;

    use crate::lexer::tokenize_comments;

    #[test]
    fn test_with_else() {
        let sample_markdown_input = r#"
- `Note` ``Feature [FEATURE_A]``
Text that is only included with feature a.
``Feature Else``
Text that is only included when feature a is excluded.
``End Feature``
"#;
        let doc_config: ApplicabilityMarkdownLexerConfig =
            ApplicabilityMarkdownLexerConfig::default();
        let results = tokenize_comments(
            &doc_config,
            LocatedSpan::new_extra(
                sample_markdown_input,
                ((0usize, 0, 0usize), (0usize, 0, 0usize)),
            ),
        );
        assert!(results.is_ok_and(|x| x.1.len() == 10))
    }
}
