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
mod tests {
    use applicability_lexer_config_markdown::ApplicabiltyMarkdownLexerConfig;
    use nom_locate::LocatedSpan;

    use crate::parse_applicability;

    #[test]
    fn sample_text() {
        let sample_markdown_input = "# Overview

This is a test file for using PLE

## Feature Tests

``Feature[APPLIC_1=Included]``
Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``End Feature``

``Feature[APPLIC_1=Included]``
Included Text
``End Feature``

``Feature[APPLIC_1=Excluded]``
Excluded Text
``End Feature``


## Else Tests

``Feature[APPLIC_1]``
Tag 1
``Feature Else``
Not Tag 1
``End Feature``

``Feature[APPLIC_2]``
Tag 2
``Feature Else``
Not Tag 2
``End Feature``

## Boolean Tests

``Feature[APPLIC_1 | APPLIC_2]``
Included `OR` Excluded Feature
``End Feature``

``Feature[APPLIC_1 & APPLIC_2]``
Included `AND` Excluded Feature
``End Feature``

## Substitution Tests

``Eval[SUB_1]``
``Eval[SUB_2]``

- ``Eval[SUB_1]``
- ``Eval[SUB_2]``

## List Tests

``Feature[APPLIC_1]``
1. Tag 1
``End Feature``
2. Common Row 1
``Feature[APPLIC_1]``
    - Tag 2.1
``End Feature``
``Feature[APPLIC_2]``
3. Tag 2
    - Tag 2 Subbullet
``End Feature``
4. Common Row 2

## Nested Tests

``Feature[APPLIC_1]``
Level 1

``Feature[APPLIC_2]``
Level 2
``End Feature``
``End Feature``

## Feature and Substitution Test

``Feature[APPLIC_1]``
Tag1

``Eval[SUB_1]``
``End Feature``

## Tables

### Table Rows

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
``Feature[APPLIC_1]``| 0a | 0b | 0c | 0d  | 0e |``End Feature``
| 1a | 1b | 1c | 1d | 1e |
``Feature[APPLIC_2]``| 2a | 2b | 2c | 2d  | 2e |``End Feature``
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c | 4d | 4e``End Feature`` |
| 5a | 5b | 5c | 5d | 5e |

### Table Cells

| Col A | Col B | Col C | Col D | Col E |
|---|---|---|---|---:|
| 1a | 1b | 1c | 1d | 1e |
| ``Feature[APPLIC_1]``2a | 2b | 2c | 2d | 2e``End Feature`` |
| 3a | 3b | 3c | 3d | 3e |
| ``Feature[APPLIC_1]``4a | 4b | 4c``End Feature`` | 4d | 4e |
| 5a | 5b | 5c | 5d | 5e |
| ``Feature[APPLIC_1]``6a``End Feature`` | 6b | 6c | 6d | ``Feature[APPLIC_2]``6e``End Feature`` |
| 7a | 7b | 7c | 7d | 7e |

### Table Columns

| Col A | ``Feature[APPLIC_1]``Col B |``End Feature`` Col C | Col D ``Feature[APPLIC_2]``| Col E ``End Feature``|
|---|``Feature[APPLIC_1]``---|``End Feature``---|---``Feature[APPLIC_2]``|---:``End Feature``|
| 1a | ``Feature[APPLIC_1]``1b |``End Feature`` 1c | 1d ``Feature[APPLIC_2]``| 1e ``End Feature``|
| 2a | ``Feature[APPLIC_1]``2b |``End Feature`` 2c | 2d ``Feature[APPLIC_2]``| 2e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|
| 3a | ``Feature[APPLIC_1]``3b |``End Feature`` 3c | 3d ``Feature[APPLIC_2]``| 3e ``End Feature``|

";
        let doc_config: ApplicabiltyMarkdownLexerConfig =
            ApplicabiltyMarkdownLexerConfig::default();
        let results = parse_applicability(
            LocatedSpan::new_extra(sample_markdown_input, ((0usize, 0), (0usize, 0))),
            &doc_config,
        );
        assert_eq!(results.len(), 85)
    }
}
