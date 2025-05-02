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
    use applicability::applic_tag::ApplicabilityTag;
    use applicability_lexer_config_markdown::ApplicabiltyMarkdownLexerConfig;
    use applicability_parser_types::applic_tokens::{
        ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedAndTag, ApplicabilityNestedNotAndTag,
        ApplicabilityNoTag, ApplicabilityOrTag,
    };
    use applicability_tokens_to_ast::{
        latch::LatchedValue,
        tree::{
            ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition,
            ApplicabilityExprKind, ApplicabilityExprSubstitution, ApplicabilityExprTag,
            ApplicabilityKind, Text,
        },
    };
    use nom_locate::LocatedSpan;

    use crate::parse_applicability;
    use pretty_assertions::assert_eq;

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
        assert_eq!(results.len(), 73);
        assert_eq!(
            results,
            vec![
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "# Overview\n\nThis is a test file for using PLE\n\n## Feature Tests\n\n",
                        start_position: LatchedValue {
                            previous_value: (0, 1),
                            current_value: (0, 1)
                        },
                        end_position: LatchedValue {
                            previous_value: (65, 7),
                            current_value: (65, 7)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\nTag 1\n",
                                    start_position: LatchedValue {
                                        previous_value: (95, 13),
                                        current_value: (95, 13)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (102, 15),
                                        current_value: (102, 15)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (65, 13),
                                    current_value: (65, 13)
                                },
                                end_position: LatchedValue {
                                    previous_value: (74, 13),
                                    current_value: (117, 17)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (65, 13),
                                current_value: (65, 13)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (117, 17)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n",
                        start_position: LatchedValue {
                            previous_value: (117, 17),
                            current_value: (117, 17)
                        },
                        end_position: LatchedValue {
                            previous_value: (119, 19),
                            current_value: (119, 19)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\nTag 2\n",
                                    start_position: LatchedValue {
                                        previous_value: (140, 21),
                                        current_value: (140, 21)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (147, 23),
                                        current_value: (147, 23)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (119, 21),
                                    current_value: (119, 21)
                                },
                                end_position: LatchedValue {
                                    previous_value: (128, 21),
                                    current_value: (162, 25)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (119, 21),
                                current_value: (119, 21)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (162, 25)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n",
                        start_position: LatchedValue {
                            previous_value: (162, 25),
                            current_value: (162, 25)
                        },
                        end_position: LatchedValue {
                            previous_value: (164, 27),
                            current_value: (164, 27)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\nIncluded Text\n",
                                    start_position: LatchedValue {
                                        previous_value: (194, 29),
                                        current_value: (194, 29)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (209, 31),
                                        current_value: (209, 31)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (164, 29),
                                    current_value: (164, 29)
                                },
                                end_position: LatchedValue {
                                    previous_value: (173, 29),
                                    current_value: (224, 33)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (164, 29),
                                current_value: (164, 29)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (224, 33)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n",
                        start_position: LatchedValue {
                            previous_value: (224, 33),
                            current_value: (224, 33)
                        },
                        end_position: LatchedValue {
                            previous_value: (226, 35),
                            current_value: (226, 35)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Excluded".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\nExcluded Text\n",
                                    start_position: LatchedValue {
                                        previous_value: (256, 37),
                                        current_value: (256, 37)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (271, 39),
                                        current_value: (271, 39)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (226, 37),
                                    current_value: (226, 37)
                                },
                                end_position: LatchedValue {
                                    previous_value: (235, 37),
                                    current_value: (286, 41)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (226, 37),
                                current_value: (226, 37)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (286, 41)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n\n## Else Tests\n\n",
                        start_position: LatchedValue {
                            previous_value: (286, 41),
                            current_value: (286, 41)
                        },
                        end_position: LatchedValue {
                            previous_value: (304, 46),
                            current_value: (304, 46)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![
                                ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                    tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_1",
                                            value: "Included".to_string()
                                        },
                                        None
                                    ))],
                                    kind: ApplicabilityKind::Feature,
                                    contents: vec![ApplicabilityExprKind::Text(Text {
                                        text: "\nTag 1\n",
                                        start_position: LatchedValue {
                                            previous_value: (325, 51),
                                            current_value: (325, 51)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (332, 53),
                                            current_value: (332, 53)
                                        }
                                    })],
                                    start_position: LatchedValue {
                                        previous_value: (304, 51),
                                        current_value: (304, 51)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (313, 51),
                                        current_value: (332, 55)
                                    }
                                }),
                                ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                    tag: vec![ApplicTokens::NestedNotAnd(
                                        ApplicabilityNestedNotAndTag(
                                            vec![ApplicTokens::NestedAnd(
                                                ApplicabilityNestedAndTag(
                                                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                                        ApplicabilityTag {
                                                            tag: "APPLIC_1",
                                                            value: "Included".to_string()
                                                        },
                                                        None
                                                    ))],
                                                    None
                                                )
                                            )],
                                            None
                                        )
                                    )],
                                    kind: ApplicabilityKind::Feature,
                                    contents: vec![ApplicabilityExprKind::Text(Text {
                                        text: "\nNot Tag 1\n",
                                        start_position: LatchedValue {
                                            previous_value: (348, 55),
                                            current_value: (348, 55)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (359, 57),
                                            current_value: (359, 57)
                                        }
                                    })],
                                    start_position: LatchedValue {
                                        previous_value: (332, 55),
                                        current_value: (332, 55)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (348, 55),
                                        current_value: (374, 59)
                                    }
                                })
                            ],
                            start_position: LatchedValue {
                                previous_value: (304, 51),
                                current_value: (304, 51)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (374, 59)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n",
                        start_position: LatchedValue {
                            previous_value: (374, 59),
                            current_value: (374, 59)
                        },
                        end_position: LatchedValue {
                            previous_value: (376, 61),
                            current_value: (376, 61)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![
                                ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                    tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_2",
                                            value: "Included".to_string()
                                        },
                                        None
                                    ))],
                                    kind: ApplicabilityKind::Feature,
                                    contents: vec![ApplicabilityExprKind::Text(Text {
                                        text: "\nTag 2\n",
                                        start_position: LatchedValue {
                                            previous_value: (397, 63),
                                            current_value: (397, 63)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (404, 65),
                                            current_value: (404, 65)
                                        }
                                    })],
                                    start_position: LatchedValue {
                                        previous_value: (376, 63),
                                        current_value: (376, 63)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (385, 63),
                                        current_value: (404, 67)
                                    }
                                }),
                                ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                    tag: vec![ApplicTokens::NestedNotAnd(
                                        ApplicabilityNestedNotAndTag(
                                            vec![ApplicTokens::NestedAnd(
                                                ApplicabilityNestedAndTag(
                                                    vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                                        ApplicabilityTag {
                                                            tag: "APPLIC_2",
                                                            value: "Included".to_string()
                                                        },
                                                        None
                                                    ))],
                                                    None
                                                )
                                            )],
                                            None
                                        )
                                    )],
                                    kind: ApplicabilityKind::Feature,
                                    contents: vec![ApplicabilityExprKind::Text(Text {
                                        text: "\nNot Tag 2\n",
                                        start_position: LatchedValue {
                                            previous_value: (420, 67),
                                            current_value: (420, 67)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (431, 69),
                                            current_value: (431, 69)
                                        }
                                    })],
                                    start_position: LatchedValue {
                                        previous_value: (404, 67),
                                        current_value: (404, 67)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (420, 67),
                                        current_value: (446, 71)
                                    }
                                })
                            ],
                            start_position: LatchedValue {
                                previous_value: (376, 63),
                                current_value: (376, 63)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (446, 71)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n## Boolean Tests\n\n",
                        start_position: LatchedValue {
                            previous_value: (446, 71),
                            current_value: (446, 71)
                        },
                        end_position: LatchedValue {
                            previous_value: (466, 75),
                            current_value: (466, 75)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![
                                    ApplicTokens::NoTag(ApplicabilityNoTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_1",
                                            value: "Included".to_string()
                                        },
                                        None
                                    )),
                                    ApplicTokens::Or(ApplicabilityOrTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_2",
                                            value: "Included".to_string()
                                        },
                                        None
                                    ))
                                ],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\nIncluded `OR` Excluded Feature\n",
                                    start_position: LatchedValue {
                                        previous_value: (498, 79),
                                        current_value: (498, 79)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (530, 81),
                                        current_value: (530, 81)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (466, 79),
                                    current_value: (466, 79)
                                },
                                end_position: LatchedValue {
                                    previous_value: (475, 79),
                                    current_value: (545, 83)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (466, 79),
                                current_value: (466, 79)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (545, 83)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n",
                        start_position: LatchedValue {
                            previous_value: (545, 83),
                            current_value: (545, 83)
                        },
                        end_position: LatchedValue {
                            previous_value: (547, 85),
                            current_value: (547, 85)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![
                                    ApplicTokens::NoTag(ApplicabilityNoTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_1",
                                            value: "Included".to_string()
                                        },
                                        None
                                    )),
                                    ApplicTokens::And(ApplicabilityAndTag(
                                        ApplicabilityTag {
                                            tag: "APPLIC_2",
                                            value: "Included".to_string()
                                        },
                                        None
                                    ))
                                ],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\nIncluded `AND` Excluded Feature\n",
                                    start_position: LatchedValue {
                                        previous_value: (579, 87),
                                        current_value: (579, 87)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (612, 89),
                                        current_value: (612, 89)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (547, 87),
                                    current_value: (547, 87)
                                },
                                end_position: LatchedValue {
                                    previous_value: (556, 87),
                                    current_value: (627, 91)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (547, 87),
                                current_value: (547, 87)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (627, 91)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n## Substitution Tests\n\n",
                        start_position: LatchedValue {
                            previous_value: (627, 91),
                            current_value: (627, 91)
                        },
                        end_position: LatchedValue {
                            previous_value: (652, 95),
                            current_value: (652, 95)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Substitution(
                        ApplicabilityExprSubstitution {
                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "SUB_1",
                                    value: "Included".to_string(),
                                },
                                None,
                            ),),],
                            start_position: LatchedValue {
                                previous_value: (652, 99),
                                current_value: (652, 99)
                            },
                            end_position: LatchedValue {
                                previous_value: (667, 99),
                                current_value: (667, 99)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n", //TODO fix this bug
                        start_position: LatchedValue {
                            previous_value: (667, 99),
                            current_value: (667, 99)
                        },
                        end_position: LatchedValue {
                            previous_value: (668, 100),
                            current_value: (668, 100)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Substitution(
                        ApplicabilityExprSubstitution {
                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "SUB_2",
                                    value: "Included".to_string(),
                                },
                                None,
                            ),),],
                            start_position: LatchedValue {
                                previous_value: (668, 101),
                                current_value: (668, 101)
                            },
                            end_position: LatchedValue {
                                previous_value: (683, 101),
                                current_value: (683, 101)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n- ",
                        start_position: LatchedValue {
                            previous_value: (683, 101),
                            current_value: (683, 101)
                        },
                        end_position: LatchedValue {
                            previous_value: (687, 103),
                            current_value: (687, 103)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Substitution(
                        ApplicabilityExprSubstitution {
                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "SUB_1",
                                    value: "Included".to_string(),
                                },
                                None,
                            ),),],
                            start_position: LatchedValue {
                                previous_value: (687, 105),
                                current_value: (687, 105)
                            },
                            end_position: LatchedValue {
                                previous_value: (702, 105),
                                current_value: (702, 105)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n- ",
                        start_position: LatchedValue {
                            previous_value: (702, 105),
                            current_value: (702, 105)
                        },
                        end_position: LatchedValue {
                            previous_value: (705, 106),
                            current_value: (705, 106)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Substitution(
                        ApplicabilityExprSubstitution {
                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                ApplicabilityTag {
                                    tag: "SUB_2",
                                    value: "Included".to_string(),
                                },
                                None,
                            ),),],
                            start_position: LatchedValue {
                                previous_value: (705, 107),
                                current_value: (705, 107)
                            },
                            end_position: LatchedValue {
                                previous_value: (720, 107),
                                current_value: (720, 107)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n## List Tests\n\n",
                        start_position: LatchedValue {
                            previous_value: (720, 107),
                            current_value: (720, 107)
                        },
                        end_position: LatchedValue {
                            previous_value: (737, 111),
                            current_value: (737, 111)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\n1. Tag 1\n",
                                    start_position: LatchedValue {
                                        previous_value: (758, 115),
                                        current_value: (758, 115)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (768, 117),
                                        current_value: (768, 117)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (737, 115),
                                    current_value: (737, 115)
                                },
                                end_position: LatchedValue {
                                    previous_value: (746, 115),
                                    current_value: (783, 119)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (737, 115),
                                current_value: (737, 115)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (783, 119)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n2. Common Row 1\n",
                        start_position: LatchedValue {
                            previous_value: (783, 119),
                            current_value: (783, 119)
                        },
                        end_position: LatchedValue {
                            previous_value: (800, 121),
                            current_value: (800, 121)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\n    - Tag 2.1\n",
                                    start_position: LatchedValue {
                                        previous_value: (821, 123),
                                        current_value: (821, 123)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (836, 125),
                                        current_value: (836, 125)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (800, 123),
                                    current_value: (800, 123)
                                },
                                end_position: LatchedValue {
                                    previous_value: (809, 123),
                                    current_value: (851, 127)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (800, 123),
                                current_value: (800, 123)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (851, 127)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: LatchedValue {
                            previous_value: (851, 127),
                            current_value: (851, 127)
                        },
                        end_position: LatchedValue {
                            previous_value: (852, 128),
                            current_value: (852, 128)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "\n3. Tag 2\n    - Tag 2 Subbullet\n",
                                    start_position: LatchedValue {
                                        previous_value: (873, 129),
                                        current_value: (873, 129)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (905, 132),
                                        current_value: (905, 132)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (852, 129),
                                    current_value: (852, 129)
                                },
                                end_position: LatchedValue {
                                    previous_value: (861, 129),
                                    current_value: (920, 135)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (852, 129),
                                current_value: (852, 129)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (920, 135)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n4. Common Row 2\n\n## Nested Tests\n\n",
                        start_position: LatchedValue {
                            previous_value: (920, 135),
                            current_value: (920, 135)
                        },
                        end_position: LatchedValue {
                            previous_value: (955, 140),
                            current_value: (955, 140)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![
                                    ApplicabilityExprKind::Text(Text {
                                        text: "\nLevel 1\n\n",
                                        start_position: LatchedValue {
                                            previous_value: (976, 145),
                                            current_value: (976, 145)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (986, 148),
                                            current_value: (986, 148)
                                        }
                                    }),
                                    ApplicabilityExprKind::TagContainer(
                                        ApplicabilityExprContainerWithPosition {
                                            contents: vec![ApplicabilityExprKind::Tag(
                                                ApplicabilityExprTag {
                                                    tag: vec![ApplicTokens::NoTag(
                                                        ApplicabilityNoTag(
                                                            ApplicabilityTag {
                                                                tag: "APPLIC_2",
                                                                value: "Included".to_string()
                                                            },
                                                            None
                                                        )
                                                    )],
                                                    kind: ApplicabilityKind::Feature,
                                                    contents: vec![ApplicabilityExprKind::Text(
                                                        Text {
                                                            text: "\nLevel 2\n",
                                                            start_position: LatchedValue {
                                                                previous_value: (1007, 151),
                                                                current_value: (1007, 151)
                                                            },
                                                            end_position: LatchedValue {
                                                                previous_value: (1016, 153),
                                                                current_value: (1016, 153)
                                                            }
                                                        }
                                                    )],
                                                    start_position: LatchedValue {
                                                        previous_value: (986, 151),
                                                        current_value: (986, 151)
                                                    },
                                                    end_position: LatchedValue {
                                                        previous_value: (995, 151),
                                                        current_value: (1031, 155)
                                                    }
                                                }
                                            )],
                                            start_position: LatchedValue {
                                                previous_value: (986, 151),
                                                current_value: (986, 151)
                                            },
                                            end_position: LatchedValue {
                                                previous_value: (0, 0),
                                                current_value: (1031, 155)
                                            }
                                        }
                                    ),
                                    ApplicabilityExprKind::Text(Text {
                                        text: "\n",
                                        start_position: LatchedValue {
                                            previous_value: (1031, 155),
                                            current_value: (1031, 155)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (1032, 156),
                                            current_value: (1032, 156)
                                        }
                                    })
                                ],
                                start_position: LatchedValue {
                                    previous_value: (955, 145),
                                    current_value: (955, 145)
                                },
                                end_position: LatchedValue {
                                    previous_value: (964, 145),
                                    current_value: (1047, 157)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (955, 145),
                                current_value: (955, 145)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1047, 157)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n## Feature and Substitution Test\n\n",
                        start_position: LatchedValue {
                            previous_value: (1047, 157),
                            current_value: (1047, 157)
                        },
                        end_position: LatchedValue {
                            previous_value: (1083, 161),
                            current_value: (1083, 161)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![
                                    ApplicabilityExprKind::Text(Text {
                                        text: "\nTag1\n\n",
                                        start_position: LatchedValue {
                                            previous_value: (1104, 165),
                                            current_value: (1104, 165)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (1111, 168),
                                            current_value: (1111, 168)
                                        }
                                    }),
                                    ApplicabilityExprKind::Substitution(
                                        ApplicabilityExprSubstitution {
                                            tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                                ApplicabilityTag {
                                                    tag: "SUB_1",
                                                    value: "Included".to_string()
                                                },
                                                None
                                            ))],
                                            start_position: LatchedValue {
                                                previous_value: (1111, 171),
                                                current_value: (1111, 171)
                                            },
                                            end_position: LatchedValue {
                                                previous_value: (1126, 171),
                                                current_value: (1126, 171)
                                            }
                                        }
                                    ),
                                    ApplicabilityExprKind::Text(Text {
                                        text: "\n",
                                        start_position: LatchedValue {
                                            previous_value: (1126, 171),
                                            current_value: (1126, 171)
                                        },
                                        end_position: LatchedValue {
                                            previous_value: (1127, 172),
                                            current_value: (1127, 172)
                                        }
                                    })
                                ],
                                start_position: LatchedValue {
                                    previous_value: (1083, 165),
                                    current_value: (1083, 165)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1092, 165),
                                    current_value: (1142, 173)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1083, 165),
                                current_value: (1083, 165)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1142, 173)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n## Tables\n\n### Table Rows\n\n| Col A | Col B | Col C | Col D | Col E |\n|---|---|---|---|---:|\n",
                        start_position: LatchedValue {
                            previous_value: (1142, 173),
                            current_value: (1142, 173)
                        },
                        end_position: LatchedValue {
                            previous_value: (1236, 181),
                            current_value: (1236, 181)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 0a | 0b | 0c | 0d  | 0e |",
                                    start_position: LatchedValue {
                                        previous_value: (1257, 189),
                                        current_value: (1257, 189)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (1284, 189),
                                        current_value: (1284, 189)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (1236, 189),
                                    current_value: (1236, 189)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1245, 189),
                                    current_value: (1299, 189)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1236, 189),
                                current_value: (1236, 189)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1299, 189)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n| 1a | 1b | 1c | 1d | 1e |\n",
                        start_position: LatchedValue {
                            previous_value: (1299, 189),
                            current_value: (1299, 189)
                        },
                        end_position: LatchedValue {
                            previous_value: (1327, 191),
                            current_value: (1327, 191)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 2a | 2b | 2c | 2d  | 2e |",
                                    start_position: LatchedValue {
                                        previous_value: (1348, 193),
                                        current_value: (1348, 193)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (1375, 193),
                                        current_value: (1375, 193)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (1327, 193),
                                    current_value: (1327, 193)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1336, 193),
                                    current_value: (1390, 193)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1327, 193),
                                current_value: (1327, 193)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1390, 193)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n| 3a | 3b | 3c | 3d | 3e |\n| ",
                        start_position: LatchedValue {
                            previous_value: (1390, 193),
                            current_value: (1390, 193)
                        },
                        end_position: LatchedValue {
                            previous_value: (1420, 195),
                            current_value: (1420, 195)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "4a | 4b | 4c | 4d | 4e",
                                    start_position: LatchedValue {
                                        previous_value: (1441, 197),
                                        current_value: (1441, 197)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (1463, 197),
                                        current_value: (1463, 197)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (1420, 197),
                                    current_value: (1420, 197)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1429, 197),
                                    current_value: (1478, 197)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1420, 197),
                                current_value: (1420, 197)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1478, 197)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 5a | 5b | 5c | 5d | 5e |\n\n### Table Cells\n\n| Col A | Col B | Col C | Col D | Col E |\n|---|---|---|---|---:|\n| 1a | 1b | 1c | 1d | 1e |\n| ",
                        start_position: LatchedValue {
                            previous_value: (1478, 197),
                            current_value: (1478, 197)
                        },
                        end_position: LatchedValue {
                            previous_value: (1620, 205),
                            current_value: (1620, 205)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "2a | 2b | 2c | 2d | 2e",
                                    start_position: LatchedValue {
                                        previous_value: (1641, 213),
                                        current_value: (1641, 213)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (1663, 213),
                                        current_value: (1663, 213)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (1620, 213),
                                    current_value: (1620, 213)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1629, 213),
                                    current_value: (1678, 213)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1620, 213),
                                current_value: (1620, 213)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1678, 213)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 3a | 3b | 3c | 3d | 3e |\n| ",
                        start_position: LatchedValue {
                            previous_value: (1678, 213),
                            current_value: (1678, 213)
                        },
                        end_position: LatchedValue {
                            previous_value: (1710, 215),
                            current_value: (1710, 215)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "4a | 4b | 4c",
                                    start_position: LatchedValue {
                                        previous_value: (1731, 217),
                                        current_value: (1731, 217)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (1743, 217),
                                        current_value: (1743, 217)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (1710, 217),
                                    current_value: (1710, 217)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1719, 217),
                                    current_value: (1758, 217)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1710, 217),
                                current_value: (1710, 217)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1758, 217)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " | 4d | 4e |\n| 5a | 5b | 5c | 5d | 5e |\n| ",
                        start_position: LatchedValue {
                            previous_value: (1758, 217),
                            current_value: (1758, 217)
                        },
                        end_position: LatchedValue {
                            previous_value: (1800, 219),
                            current_value: (1800, 219)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "6a",
                                    start_position: LatchedValue {
                                        previous_value: (1821, 221),
                                        current_value: (1821, 221)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (1823, 221),
                                        current_value: (1823, 221)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (1800, 221),
                                    current_value: (1800, 221)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1809, 221),
                                    current_value: (1838, 221)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1800, 221),
                                current_value: (1800, 221)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1838, 221)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " | 6b | 6c | 6d | ",
                        start_position: LatchedValue {
                            previous_value: (1838, 221),
                            current_value: (1838, 221)
                        },
                        end_position: LatchedValue {
                            previous_value: (1856, 221),
                            current_value: (1856, 221)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "6e",
                                    start_position: LatchedValue {
                                        previous_value: (1877, 221),
                                        current_value: (1877, 221)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (1879, 221),
                                        current_value: (1879, 221)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (1856, 221),
                                    current_value: (1856, 221)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1865, 221),
                                    current_value: (1894, 221)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1856, 221),
                                current_value: (1856, 221)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1894, 221)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 7a | 7b | 7c | 7d | 7e |\n\n### Table Columns\n\n| Col A | ",
                        start_position: LatchedValue {
                            previous_value: (1894, 221),
                            current_value: (1894, 221)
                        },
                        end_position: LatchedValue {
                            previous_value: (1954, 226),
                            current_value: (1954, 226)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "Col B |",
                                    start_position: LatchedValue {
                                        previous_value: (1975, 231),
                                        current_value: (1975, 231)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (1982, 231),
                                        current_value: (1982, 231)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (1954, 231),
                                    current_value: (1954, 231)
                                },
                                end_position: LatchedValue {
                                    previous_value: (1963, 231),
                                    current_value: (1997, 231)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (1954, 231),
                                current_value: (1954, 231)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (1997, 231)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " Col C | Col D ",
                        start_position: LatchedValue {
                            previous_value: (1997, 231),
                            current_value: (1997, 231)
                        },
                        end_position: LatchedValue {
                            previous_value: (2012, 231),
                            current_value: (2012, 231)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| Col E ",
                                    start_position: LatchedValue {
                                        previous_value: (2033, 231),
                                        current_value: (2033, 231)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2041, 231),
                                        current_value: (2041, 231)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2012, 231),
                                    current_value: (2012, 231)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2021, 231),
                                    current_value: (2056, 231)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2012, 231),
                                current_value: (2012, 231)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2056, 231)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n|---|",
                        start_position: LatchedValue {
                            previous_value: (2056, 231),
                            current_value: (2056, 231)
                        },
                        end_position: LatchedValue {
                            previous_value: (2063, 232),
                            current_value: (2063, 232)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "---|",
                                    start_position: LatchedValue {
                                        previous_value: (2084, 233),
                                        current_value: (2084, 233)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2088, 233),
                                        current_value: (2088, 233)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2063, 233),
                                    current_value: (2063, 233)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2072, 233),
                                    current_value: (2103, 233)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2063, 233),
                                current_value: (2063, 233)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2103, 233)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "---|---",
                        start_position: LatchedValue {
                            previous_value: (2103, 233),
                            current_value: (2103, 233)
                        },
                        end_position: LatchedValue {
                            previous_value: (2110, 233),
                            current_value: (2110, 233)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "|---:",
                                    start_position: LatchedValue {
                                        previous_value: (2131, 233),
                                        current_value: (2131, 233)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2136, 233),
                                        current_value: (2136, 233)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2110, 233),
                                    current_value: (2110, 233)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2119, 233),
                                    current_value: (2151, 233)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2110, 233),
                                current_value: (2110, 233)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2151, 233)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 1a | ",
                        start_position: LatchedValue {
                            previous_value: (2151, 233),
                            current_value: (2151, 233)
                        },
                        end_position: LatchedValue {
                            previous_value: (2160, 234),
                            current_value: (2160, 234)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "1b |",
                                    start_position: LatchedValue {
                                        previous_value: (2181, 235),
                                        current_value: (2181, 235)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2185, 235),
                                        current_value: (2185, 235)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2160, 235),
                                    current_value: (2160, 235)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2169, 235),
                                    current_value: (2200, 235)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2160, 235),
                                current_value: (2160, 235)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2200, 235)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 1c | 1d ",
                        start_position: LatchedValue {
                            previous_value: (2200, 235),
                            current_value: (2200, 235)
                        },
                        end_position: LatchedValue {
                            previous_value: (2209, 235),
                            current_value: (2209, 235)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 1e ",
                                    start_position: LatchedValue {
                                        previous_value: (2230, 235),
                                        current_value: (2230, 235)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2235, 235),
                                        current_value: (2235, 235)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2209, 235),
                                    current_value: (2209, 235)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2218, 235),
                                    current_value: (2250, 235)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2209, 235),
                                current_value: (2209, 235)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2250, 235)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 2a | ",
                        start_position: LatchedValue {
                            previous_value: (2250, 235),
                            current_value: (2250, 235)
                        },
                        end_position: LatchedValue {
                            previous_value: (2259, 236),
                            current_value: (2259, 236)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "2b |",
                                    start_position: LatchedValue {
                                        previous_value: (2280, 237),
                                        current_value: (2280, 237)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2284, 237),
                                        current_value: (2284, 237)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2259, 237),
                                    current_value: (2259, 237)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2268, 237),
                                    current_value: (2299, 237)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2259, 237),
                                current_value: (2259, 237)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2299, 237)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 2c | 2d ",
                        start_position: LatchedValue {
                            previous_value: (2299, 237),
                            current_value: (2299, 237)
                        },
                        end_position: LatchedValue {
                            previous_value: (2308, 237),
                            current_value: (2308, 237)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 2e ",
                                    start_position: LatchedValue {
                                        previous_value: (2329, 237),
                                        current_value: (2329, 237)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2334, 237),
                                        current_value: (2334, 237)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2308, 237),
                                    current_value: (2308, 237)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2317, 237),
                                    current_value: (2349, 237)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2308, 237),
                                current_value: (2308, 237)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2349, 237)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 3a | ",
                        start_position: LatchedValue {
                            previous_value: (2349, 237),
                            current_value: (2349, 237)
                        },
                        end_position: LatchedValue {
                            previous_value: (2358, 238),
                            current_value: (2358, 238)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "3b |",
                                    start_position: LatchedValue {
                                        previous_value: (2379, 239),
                                        current_value: (2379, 239)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2383, 239),
                                        current_value: (2383, 239)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2358, 239),
                                    current_value: (2358, 239)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2367, 239),
                                    current_value: (2398, 239)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2358, 239),
                                current_value: (2358, 239)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2398, 239)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 3c | 3d ",
                        start_position: LatchedValue {
                            previous_value: (2398, 239),
                            current_value: (2398, 239)
                        },
                        end_position: LatchedValue {
                            previous_value: (2407, 239),
                            current_value: (2407, 239)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 3e ",
                                    start_position: LatchedValue {
                                        previous_value: (2428, 239),
                                        current_value: (2428, 239)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2433, 239),
                                        current_value: (2433, 239)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2407, 239),
                                    current_value: (2407, 239)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2416, 239),
                                    current_value: (2448, 239)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2407, 239),
                                current_value: (2407, 239)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2448, 239)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 3a | ",
                        start_position: LatchedValue {
                            previous_value: (2448, 239),
                            current_value: (2448, 239)
                        },
                        end_position: LatchedValue {
                            previous_value: (2457, 240),
                            current_value: (2457, 240)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_1",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "3b |",
                                    start_position: LatchedValue {
                                        previous_value: (2478, 241),
                                        current_value: (2478, 241)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2482, 241),
                                        current_value: (2482, 241)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2457, 241),
                                    current_value: (2457, 241)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2466, 241),
                                    current_value: (2497, 241)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2457, 241),
                                current_value: (2457, 241)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2497, 241)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 3c | 3d ",
                        start_position: LatchedValue {
                            previous_value: (2497, 241),
                            current_value: (2497, 241)
                        },
                        end_position: LatchedValue {
                            previous_value: (2506, 241),
                            current_value: (2506, 241)
                        }
                    })]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::TagContainer(
                        ApplicabilityExprContainerWithPosition {
                            contents: vec![ApplicabilityExprKind::Tag(ApplicabilityExprTag {
                                tag: vec![ApplicTokens::NoTag(ApplicabilityNoTag(
                                    ApplicabilityTag {
                                        tag: "APPLIC_2",
                                        value: "Included".to_string()
                                    },
                                    None
                                ))],
                                kind: ApplicabilityKind::Feature,
                                contents: vec![ApplicabilityExprKind::Text(Text {
                                    text: "| 3e ",
                                    start_position: LatchedValue {
                                        previous_value: (2527, 241),
                                        current_value: (2527, 241)
                                    },
                                    end_position: LatchedValue {
                                        previous_value: (2532, 241),
                                        current_value: (2532, 241)
                                    }
                                })],
                                start_position: LatchedValue {
                                    previous_value: (2506, 241),
                                    current_value: (2506, 241)
                                },
                                end_position: LatchedValue {
                                    previous_value: (2515, 241),
                                    current_value: (2547, 241)
                                }
                            })],
                            start_position: LatchedValue {
                                previous_value: (2506, 241),
                                current_value: (2506, 241)
                            },
                            end_position: LatchedValue {
                                previous_value: (0, 0),
                                current_value: (2547, 241)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n\n",
                        start_position: LatchedValue {
                            previous_value: (2547, 241),
                            current_value: (2547, 241)
                        },
                        end_position: LatchedValue {
                            previous_value: (2550, 243),
                            current_value: (2550, 243)
                        }
                    })]
                })
            ]
        );
    }
}
