use std::fmt::Debug;

use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use nom::{AsBytes, Input, Offset};
use nom_locate::LocatedSpan;

#[derive(Debug, Clone, PartialEq, Eq, Default, Copy)]
enum Rate {
    #[default]
    Neutral,
    Increasing,
    Decreasing,
}

pub fn chunk<I>(
    input: Vec<LexerToken<LocatedSpan<I, TokenPosition>>>,
) -> Vec<Vec<LexerToken<LocatedSpan<I, TokenPosition>>>>
where
    I: Input + AsBytes + Offset + Send + Sync + Debug,
{
    input
        .into_iter()
        .scan(
            (
                0,
                0,
                0,
                0,
                0,
                0,
                Rate::Neutral,
                Rate::Neutral,
                Rate::Neutral,
                Rate::Neutral,
                Rate::Neutral,
                Rate::Neutral,
                LexerToken::<LocatedSpan<I, TokenPosition>>::Nothing,
            ),
            |(
                feature_state,
                config_state,
                group_state,
                terminated_comment_state,
                non_terminated_comment_state,
                multiline_comment_state,
                feature_rate,
                config_rate,
                group_rate,
                terminated_comment_rate,
                non_terminated_comment_rate,
                multiline_comment_rate,
                _previous_element,
            ),
             current| {
                // let previous_element_is_carriage_return = match previous_element {
                //     LexerToken::CarriageReturn((_,_)) => true,
                //     _ => false,
                // };
                *non_terminated_comment_rate = match current {
                    LexerToken::SingleLineCommentCharacter((_, _)) => Rate::Increasing,
                    LexerToken::UnixNewLine((_, _)) => {
                        if *non_terminated_comment_state > 0 {
                            Rate::Decreasing
                        } else {
                            Rate::Neutral
                        }
                    }
                    _ => Rate::Neutral,
                };
                *terminated_comment_rate = match current {
                    LexerToken::StartCommentSingleLineTerminated((_, _)) => Rate::Increasing,
                    LexerToken::EndCommentSingleLineTerminated((_, _)) => Rate::Decreasing,
                    _ => Rate::Neutral,
                };
                *multiline_comment_rate = match current {
                    LexerToken::StartCommentMultiLine((_, _)) => Rate::Increasing,
                    LexerToken::EndCommentMultiLine((_, _)) => Rate::Decreasing,
                    _ => Rate::Neutral,
                };
                *feature_rate = match current {
                    LexerToken::Feature((_, _))
                    | LexerToken::FeatureNot((_, _))
                    | LexerToken::FeatureSwitch((_, _)) => Rate::Increasing,
                    LexerToken::EndFeature((_, _)) => Rate::Decreasing,
                    _ => Rate::Neutral,
                };
                *config_rate = match current {
                    LexerToken::Configuration((_, _))
                    | LexerToken::ConfigurationNot((_, _))
                    | LexerToken::ConfigurationSwitch((_, _)) => Rate::Increasing,
                    LexerToken::EndConfiguration((_, _)) => Rate::Decreasing,
                    _ => Rate::Neutral,
                };
                *group_rate = match current {
                    LexerToken::ConfigurationGroup((_, _))
                    | LexerToken::ConfigurationGroupNot((_, _))
                    | LexerToken::ConfigurationGroupSwitch((_, _)) => Rate::Increasing,
                    LexerToken::EndConfigurationGroup((_, _)) => Rate::Decreasing,
                    _ => Rate::Neutral,
                };
                *feature_state = match feature_rate {
                    Rate::Neutral => *feature_state,
                    Rate::Increasing => *feature_state + 1,
                    Rate::Decreasing => *feature_state - 1,
                };
                *config_state = match config_rate {
                    Rate::Neutral => *config_state,
                    Rate::Increasing => *config_state + 1,
                    Rate::Decreasing => *config_state - 1,
                };
                *group_state = match group_rate {
                    Rate::Neutral => *group_state,
                    Rate::Increasing => *group_state + 1,
                    Rate::Decreasing => *group_state - 1,
                };
                *terminated_comment_state = match terminated_comment_rate {
                    Rate::Neutral => *terminated_comment_state,
                    Rate::Increasing => *terminated_comment_state + 1,
                    Rate::Decreasing => *terminated_comment_state - 1,
                };
                *non_terminated_comment_state = match non_terminated_comment_rate {
                    Rate::Neutral => *non_terminated_comment_state,
                    Rate::Increasing => *non_terminated_comment_state + 1,
                    Rate::Decreasing => *non_terminated_comment_state - 1,
                };
                *multiline_comment_state = match multiline_comment_rate {
                    Rate::Neutral => *multiline_comment_state,
                    Rate::Increasing => *multiline_comment_state + 1,
                    Rate::Decreasing => *multiline_comment_state - 1,
                };
                match current {
                    LexerToken::Illegal => None,
                    _ => Some((
                        *feature_state,
                        *config_state,
                        *group_state,
                        *terminated_comment_state,
                        *non_terminated_comment_state,
                        *multiline_comment_state,
                        *feature_rate,
                        *config_rate,
                        *group_rate,
                        *terminated_comment_rate,
                        *non_terminated_comment_rate,
                        *multiline_comment_rate,
                        current,
                    )),
                }
            },
        )
        .collect::<Vec<_>>()
        .chunk_by(
            |(
                current_feature_state,
                current_config_state,
                current_group_state,
                current_terminated_state,
                current_non_terminated_state,
                current_multiline_state,
                _current_feature_rate,
                _current_config_rate,
                _current_group_rate,
                _current_terminated_rate,
                _current_non_terminated_rate,
                _current_multiline_rate,
                _current_token,
            ),
             (
                _next_feature_state,
                _next_config_state,
                _next_group_state,
                _next_terminated_state,
                _next_non_terminated_state,
                _next_multiline_state,
                _next_feature_rate,
                _next_config_rate,
                _next_group_rate,
                _next_terminated_rate,
                _next_non_terminated_rate,
                _next_multiline_rate,
                _next_token,
            )| {
                *current_feature_state != 0
                    || *current_config_state != 0
                    || *current_group_state != 0
                    || *current_terminated_state != 0
                    || *current_non_terminated_state != 0
                    || *current_multiline_state != 0
            },
        )
        .map(|slice| {
            slice
                .iter()
                .cloned()
                .map(
                    |(
                        _feature_state,
                        _config_state,
                        _group_state,
                        _non_terminated_state,
                        _terminated_state,
                        _multiline_state,
                        _feature_rate,
                        _config_rate,
                        _group_rate,
                        _non_terminated_rate,
                        _terminated_rate,
                        _multiline_rate,
                        token,
                    )| { token },
                )
                .collect::<Vec<_>>()
        })
        .collect::<Vec<_>>()
}

#[cfg(test)]
mod tests {
    use applicability_lexer_config_markdown::ApplicabiltyMarkdownLexerConfig;
    use applicability_lexer_multi_stage_lexer::lexer::tokenize_comments;
    use nom_locate::LocatedSpan;

    use crate::chunk;

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
        let results = chunk(tokenize_comments::<ApplicabiltyMarkdownLexerConfig, &str>(
            &doc_config,
            LocatedSpan::new_extra(sample_markdown_input, ((0usize, 0), (0usize, 0))),
        ));
        assert_eq!(results.len(), 73)
    }
}
