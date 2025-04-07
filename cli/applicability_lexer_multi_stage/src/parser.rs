use nom::{
    error::{Error, ParseError},
    AsBytes, AsChar, Compare, ExtendInto, FindSubstring, Input, Offset, Parser,
};
use nom_locate::LocatedSpan;
use rayon::prelude::*;
use std::fmt::{Debug, Display};
use tracing::error;

use crate::second_stage::{
    multi_line::multi_line::MultiLine,
    single_line_non_terminated::non_terminated::SingleLineNonTerminated,
    single_line_terminated::terminated::SingleLineTerminated, token::LexerToken,
};
use applicability_lexer_base::utils::{as_str::AsStr, has_length::HasLength};
use applicability_lexer_first_stage::{
    first_stage_parser::IdentifyComments, token::FirstStageToken,
};

pub fn tokenize_comments<T, I1>(
    doc: &T,
    input: LocatedSpan<I1, ((usize, u32), (usize, u32))>,
) -> Vec<LexerToken<LocatedSpan<I1, ((usize, u32), (usize, u32))>>>
where
    T: IdentifyComments + SingleLineTerminated + SingleLineNonTerminated + MultiLine + Sync,
    I1: Input
        + for<'x> Compare<&'x str>
        + for<'x> FindSubstring<&'x str>
        + AsBytes
        + Offset
        + Send
        + Sync
        + Debug
        + Display,
    <I1 as Input>::Item: AsChar,
{
    let results = match doc.identify_comments::<LocatedSpan<I1, ((usize, u32), (usize, u32))>>().parse_complete(input) {
        Ok((_i, o1)) => Ok(o1.into_par_iter().flat_map(|comment:FirstStageToken<LocatedSpan<I1, ((usize, u32), (usize, u32))>>| {
            let res = match comment {
                FirstStageToken::SingleLineComment(content, start, _end) => {
                    match doc
                        .get_single_line_non_terminated::<LocatedSpan<I1, ((usize, u32), (usize, u32))>, Error<LocatedSpan<I1, ((usize, u32), (usize, u32))>>>()
                        .parse_complete(content)
                    {
                        Ok((_i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<LocatedSpan<I1,((usize, u32), (usize, u32))>>>>(),
                        Err(e) => {
                            error!("Error parsing single line non terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::SingleLineTerminatedComment(content, start, _end) => {
                    match doc
                        .get_single_line_terminated::<LocatedSpan<I1, ((usize, u32), (usize, u32))>, Error<LocatedSpan<I1, ((usize, u32), (usize, u32))>>>()
                        .parse_complete(content)
                    {
                        Ok((_i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<LocatedSpan<I1,((usize, u32), (usize, u32))>>>>(),
                        Err(e) => {
                            error!("Error parsing single line terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::MultiLineComment(content, start, _end) => {
                    match doc
                        .get_multi_line::<LocatedSpan<I1,((usize, u32), (usize, u32))>, Error<LocatedSpan<I1,((usize, u32), (usize, u32))>>>()
                        .parse_complete(content)
                    {
                        Ok((_i2, o2)) => o2
                            .into_iter()
                            .map(|x| {
                                let mut y = x.clone();
                                if start.1 > 1 {
                                    y = x.increment_line_number(start.1);
                                }
                                y = y.increment_offset(start.0);

                                y
                            })
                            .collect::<Vec<LexerToken<LocatedSpan<I1,((usize, u32), (usize, u32))>>>>(),
                        Err(e) => {
                            error!("Error parsing single line non terminated comment. {:#?}", e);
                            vec![]
                        }
                    }
                }
                FirstStageToken::Text(content, start, end) => vec![LexerToken::Text(
                    content,
                    start,
                    end,
                )]
                .into_iter()
                .map(|x| {
                    let mut y = x.clone();
                    if start.1 > 1 {
                        y = x.increment_line_number(start.1);
                    }
                    y = y.increment_offset(start.0);

                    y
                })
                .collect::<Vec<LexerToken<LocatedSpan<I1, ((usize, u32), (usize, u32))>>>>(),
            };
            res.into_par_iter()
        }).collect()),
        Err(e) => Err(e),
    };
    results.unwrap_or(vec![])
}

#[cfg(test)]
mod tests {
    use applicability_lexer_config_markdown::ApplicabiltyMarkdownLexerConfig;
    use nom::error::{Error, ParseError};
    use nom_locate::LocatedSpan;

    use super::tokenize_comments;

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
        let doc_config: ApplicabiltyMarkdownLexerConfig = ApplicabiltyMarkdownLexerConfig {};
        let results = tokenize_comments::<ApplicabiltyMarkdownLexerConfig, &str>(
            &doc_config,
            LocatedSpan::new_extra(sample_markdown_input, ((0usize, 0), (0usize, 0))),
        );
        assert_eq!(results, vec![])
    }
}
