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
    use applicability_lexer_config_markdown::ApplicabilityMarkdownLexerConfig;
    use applicability_parser_types::applic_tokens::{
        ApplicTokens, ApplicabilityAndTag, ApplicabilityNestedAndTag, ApplicabilityNestedNotAndTag,
        ApplicabilityNoTag, ApplicabilityOrTag,
    };
    use applicability_tokens_to_ast::{
        tree::{
            ApplicabilityExprContainer, ApplicabilityExprContainerWithPosition,
            ApplicabilityExprKind, ApplicabilityExprSubstitution, ApplicabilityExprTag,
            ApplicabilityKind, Text,
        },
        updatable::UpdatableValue,
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
        let doc_config: ApplicabilityMarkdownLexerConfig =
            ApplicabilityMarkdownLexerConfig::default();
        let results = parse_applicability(
            LocatedSpan::new_extra(sample_markdown_input, ((0usize, 0), (0usize, 0))),
            &doc_config,
        );
        assert_eq!(results.len(), 71);
        assert_eq!(
            results,
            vec![
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "# Overview\n\nThis is a test file for using PLE\n\n## Feature Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (0, 1),
                            current_value: (0, 1)
                        },
                        end_position: UpdatableValue {
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
                                    text: "Tag 1\n",
                                    start_position: UpdatableValue {
                                        previous_value: (96, 8),
                                        current_value: (96, 8)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (102, 9),
                                        current_value: (102, 9)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (65, 7),
                                    current_value: (65, 7)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (74, 7),
                                    current_value: (118, 10)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (65, 7),
                                current_value: (65, 7)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (118, 10)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (118, 10),
                            current_value: (118, 10)
                        },
                        end_position: UpdatableValue {
                            previous_value: (119, 11),
                            current_value: (119, 11)
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
                                    text: "Tag 2\n",
                                    start_position: UpdatableValue {
                                        previous_value: (141, 12),
                                        current_value: (141, 12)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (147, 13),
                                        current_value: (147, 13)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (119, 11),
                                    current_value: (119, 11)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (128, 11),
                                    current_value: (163, 14)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (119, 11),
                                current_value: (119, 11)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (163, 14)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (163, 14),
                            current_value: (163, 14)
                        },
                        end_position: UpdatableValue {
                            previous_value: (164, 15),
                            current_value: (164, 15)
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
                                    text: "Included Text\n",
                                    start_position: UpdatableValue {
                                        previous_value: (195, 16),
                                        current_value: (195, 16)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (209, 17),
                                        current_value: (209, 17)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (164, 15),
                                    current_value: (164, 15)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (173, 15),
                                    current_value: (225, 18)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (164, 15),
                                current_value: (164, 15)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (225, 18)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (225, 18),
                            current_value: (225, 18)
                        },
                        end_position: UpdatableValue {
                            previous_value: (226, 19),
                            current_value: (226, 19)
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
                                    text: "Excluded Text\n",
                                    start_position: UpdatableValue {
                                        previous_value: (257, 20),
                                        current_value: (257, 20)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (271, 21),
                                        current_value: (271, 21)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (226, 19),
                                    current_value: (226, 19)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (235, 19),
                                    current_value: (287, 22)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (226, 19),
                                current_value: (226, 19)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (287, 22)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n\n## Else Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (287, 22),
                            current_value: (287, 22)
                        },
                        end_position: UpdatableValue {
                            previous_value: (304, 26),
                            current_value: (304, 26)
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
                                        text: "Tag 1\n",
                                        start_position: UpdatableValue {
                                            previous_value: (326, 27),
                                            current_value: (326, 27)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (332, 28),
                                            current_value: (332, 28)
                                        }
                                    })],
                                    start_position: UpdatableValue {
                                        previous_value: (304, 26),
                                        current_value: (304, 26)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (313, 26),
                                        current_value: (332, 28)
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
                                        text: "Not Tag 1\n",
                                        start_position: UpdatableValue {
                                            previous_value: (349, 29),
                                            current_value: (349, 29)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (359, 30),
                                            current_value: (359, 30)
                                        }
                                    })],
                                    start_position: UpdatableValue {
                                        previous_value: (332, 28),
                                        current_value: (332, 28)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (349, 29),
                                        current_value: (375, 31)
                                    }
                                })
                            ],
                            start_position: UpdatableValue {
                                previous_value: (304, 26),
                                current_value: (304, 26)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (375, 31)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (375, 31),
                            current_value: (375, 31)
                        },
                        end_position: UpdatableValue {
                            previous_value: (376, 32),
                            current_value: (376, 32)
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
                                        text: "Tag 2\n",
                                        start_position: UpdatableValue {
                                            previous_value: (398, 33),
                                            current_value: (398, 33)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (404, 34),
                                            current_value: (404, 34)
                                        }
                                    })],
                                    start_position: UpdatableValue {
                                        previous_value: (376, 32),
                                        current_value: (376, 32)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (385, 32),
                                        current_value: (404, 34)
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
                                        text: "Not Tag 2\n",
                                        start_position: UpdatableValue {
                                            previous_value: (421, 35),
                                            current_value: (421, 35)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (431, 36),
                                            current_value: (431, 36)
                                        }
                                    })],
                                    start_position: UpdatableValue {
                                        previous_value: (404, 34),
                                        current_value: (404, 34)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (421, 35),
                                        current_value: (447, 37)
                                    }
                                })
                            ],
                            start_position: UpdatableValue {
                                previous_value: (376, 32),
                                current_value: (376, 32)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (447, 37)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## Boolean Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (447, 37),
                            current_value: (447, 37)
                        },
                        end_position: UpdatableValue {
                            previous_value: (466, 40),
                            current_value: (466, 40)
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
                                    text: "Included `OR` Excluded Feature\n",
                                    start_position: UpdatableValue {
                                        previous_value: (499, 41),
                                        current_value: (499, 41)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (530, 42),
                                        current_value: (530, 42)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (466, 40),
                                    current_value: (466, 40)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (475, 40),
                                    current_value: (546, 43)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (466, 40),
                                current_value: (466, 40)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (546, 43)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n",
                        start_position: UpdatableValue {
                            previous_value: (546, 43),
                            current_value: (546, 43)
                        },
                        end_position: UpdatableValue {
                            previous_value: (547, 44),
                            current_value: (547, 44)
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
                                    text: "Included `AND` Excluded Feature\n",
                                    start_position: UpdatableValue {
                                        previous_value: (580, 45),
                                        current_value: (580, 45)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (612, 46),
                                        current_value: (612, 46)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (547, 44),
                                    current_value: (547, 44)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (556, 44),
                                    current_value: (628, 47)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (547, 44),
                                current_value: (547, 44)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (628, 47)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## Substitution Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (628, 47),
                            current_value: (628, 47)
                        },
                        end_position: UpdatableValue {
                            previous_value: (652, 50),
                            current_value: (652, 50)
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
                            start_position: UpdatableValue {
                                previous_value: (652, 50),
                                current_value: (652, 50)
                            },
                            end_position: UpdatableValue {
                                previous_value: (668, 51),
                                current_value: (668, 51)
                            }
                        }
                    )]
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
                            start_position: UpdatableValue {
                                previous_value: (668, 51),
                                current_value: (668, 51)
                            },
                            end_position: UpdatableValue {
                                previous_value: (684, 52),
                                current_value: (684, 52)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n- ",
                        start_position: UpdatableValue {
                            previous_value: (684, 52),
                            current_value: (684, 52)
                        },
                        end_position: UpdatableValue {
                            previous_value: (687, 53),
                            current_value: (687, 53)
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
                            start_position: UpdatableValue {
                                previous_value: (687, 53),
                                current_value: (687, 53)
                            },
                            end_position: UpdatableValue {
                                previous_value: (703, 54),
                                current_value: (703, 54)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "- ",
                        start_position: UpdatableValue {
                            previous_value: (703, 54),
                            current_value: (703, 54)
                        },
                        end_position: UpdatableValue {
                            previous_value: (705, 54),
                            current_value: (705, 54)
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
                            start_position: UpdatableValue {
                                previous_value: (705, 54),
                                current_value: (705, 54)
                            },
                            end_position: UpdatableValue {
                                previous_value: (721, 55),
                                current_value: (721, 55)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## List Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (721, 55),
                            current_value: (721, 55)
                        },
                        end_position: UpdatableValue {
                            previous_value: (737, 58),
                            current_value: (737, 58)
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
                                    text: "1. Tag 1\n",
                                    start_position: UpdatableValue {
                                        previous_value: (759, 59),
                                        current_value: (759, 59)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (768, 60),
                                        current_value: (768, 60)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (737, 58),
                                    current_value: (737, 58)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (746, 58),
                                    current_value: (784, 61)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (737, 58),
                                current_value: (737, 58)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (784, 61)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "2. Common Row 1\n",
                        start_position: UpdatableValue {
                            previous_value: (784, 61),
                            current_value: (784, 61)
                        },
                        end_position: UpdatableValue {
                            previous_value: (800, 62),
                            current_value: (800, 62)
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
                                    text: "    - Tag 2.1\n",
                                    start_position: UpdatableValue {
                                        previous_value: (822, 63),
                                        current_value: (822, 63)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (836, 64),
                                        current_value: (836, 64)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (800, 62),
                                    current_value: (800, 62)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (809, 62),
                                    current_value: (852, 65)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (800, 62),
                                current_value: (800, 62)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (852, 65)
                            }
                        }
                    )]
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
                                    text: "3. Tag 2\n    - Tag 2 Subbullet\n",
                                    start_position: UpdatableValue {
                                        previous_value: (874, 66),
                                        current_value: (874, 66)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (905, 68),
                                        current_value: (905, 68)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (852, 65),
                                    current_value: (852, 65)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (861, 65),
                                    current_value: (921, 69)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (852, 65),
                                current_value: (852, 65)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (921, 69)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "4. Common Row 2\n\n## Nested Tests\n\n",
                        start_position: UpdatableValue {
                            previous_value: (921, 69),
                            current_value: (921, 69)
                        },
                        end_position: UpdatableValue {
                            previous_value: (955, 73),
                            current_value: (955, 73)
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
                                        text: "Level 1\n\n",
                                        start_position: UpdatableValue {
                                            previous_value: (977, 74),
                                            current_value: (977, 74)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (986, 76),
                                            current_value: (986, 76)
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
                                                            text: "Level 2\n",
                                                            start_position: UpdatableValue {
                                                                previous_value: (1008, 77),
                                                                current_value: (1008, 77)
                                                            },
                                                            end_position: UpdatableValue {
                                                                previous_value: (1016, 78),
                                                                current_value: (1016, 78)
                                                            }
                                                        }
                                                    )],
                                                    start_position: UpdatableValue {
                                                        previous_value: (986, 76),
                                                        current_value: (986, 76)
                                                    },
                                                    end_position: UpdatableValue {
                                                        previous_value: (995, 76),
                                                        current_value: (1032, 79)
                                                    }
                                                }
                                            )],
                                            start_position: UpdatableValue {
                                                previous_value: (986, 76),
                                                current_value: (986, 76)
                                            },
                                            end_position: UpdatableValue {
                                                previous_value: (0, 0),
                                                current_value: (1032, 79)
                                            }
                                        }
                                    ),
                                ],
                                start_position: UpdatableValue {
                                    previous_value: (955, 73),
                                    current_value: (955, 73)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (964, 73),
                                    current_value: (1048, 80)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (955, 73),
                                current_value: (955, 73)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1048, 80)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## Feature and Substitution Test\n\n",
                        start_position: UpdatableValue {
                            previous_value: (1048, 80),
                            current_value: (1048, 80)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1083, 83),
                            current_value: (1083, 83)
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
                                        text: "Tag1\n\n",
                                        start_position: UpdatableValue {
                                            previous_value: (1105, 84),
                                            current_value: (1105, 84)
                                        },
                                        end_position: UpdatableValue {
                                            previous_value: (1111, 86),
                                            current_value: (1111, 86)
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
                                            start_position: UpdatableValue {
                                                previous_value: (1111, 86),
                                                current_value: (1111, 86)
                                            },
                                            end_position: UpdatableValue {
                                                previous_value: (1127, 87),
                                                current_value: (1127, 87)
                                            }
                                        }
                                    ),
                                ],
                                start_position: UpdatableValue {
                                    previous_value: (1083, 83),
                                    current_value: (1083, 83)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1092, 83),
                                    current_value: (1143, 88)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1083, 83),
                                current_value: (1083, 83)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1143, 88)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "\n## Tables\n\n### Table Rows\n\n| Col A | Col B | Col C | Col D | Col E |\n|---|---|---|---|---:|\n",
                        start_position: UpdatableValue {
                            previous_value: (1143, 88),
                            current_value: (1143, 88)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1236, 95),
                            current_value: (1236, 95)
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
                                    start_position: UpdatableValue {
                                        previous_value: (1257, 95),
                                        current_value: (1257, 95)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1284, 95),
                                        current_value: (1284, 95)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1236, 95),
                                    current_value: (1236, 95)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1245, 95),
                                    current_value: (1300, 96)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1236, 95),
                                current_value: (1236, 95)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1300, 96)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "| 1a | 1b | 1c | 1d | 1e |\n",
                        start_position: UpdatableValue {
                            previous_value: (1300, 96),
                            current_value: (1300, 96)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1327, 97),
                            current_value: (1327, 97)
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
                                    start_position: UpdatableValue {
                                        previous_value: (1348, 97),
                                        current_value: (1348, 97)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1375, 97),
                                        current_value: (1375, 97)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1327, 97),
                                    current_value: (1327, 97)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1336, 97),
                                    current_value: (1391, 98)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1327, 97),
                                current_value: (1327, 97)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1391, 98)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "| 3a | 3b | 3c | 3d | 3e |\n| ",
                        start_position: UpdatableValue {
                            previous_value: (1391, 98),
                            current_value: (1391, 98)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1420, 99),
                            current_value: (1420, 99)
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
                                    start_position: UpdatableValue {
                                        previous_value: (1441, 99),
                                        current_value: (1441, 99)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1463, 99),
                                        current_value: (1463, 99)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1420, 99),
                                    current_value: (1420, 99)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1429, 99),
                                    current_value: (1478, 99)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1420, 99),
                                current_value: (1420, 99)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1478, 99)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 5a | 5b | 5c | 5d | 5e |\n\n### Table Cells\n\n| Col A | Col B | Col C | Col D | Col E |\n|---|---|---|---|---:|\n| 1a | 1b | 1c | 1d | 1e |\n| ",
                        start_position: UpdatableValue {
                            previous_value: (1478, 99),
                            current_value: (1478, 99)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1620, 107),
                            current_value: (1620, 107)
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
                                    start_position: UpdatableValue {
                                        previous_value: (1641, 107),
                                        current_value: (1641, 107)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1663, 107),
                                        current_value: (1663, 107)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1620, 107),
                                    current_value: (1620, 107)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1629, 107),
                                    current_value: (1678, 107)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1620, 107),
                                current_value: (1620, 107)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1678, 107)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 3a | 3b | 3c | 3d | 3e |\n| ",
                        start_position: UpdatableValue {
                            previous_value: (1678, 107),
                            current_value: (1678, 107)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1710, 109),
                            current_value: (1710, 109)
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
                                    start_position: UpdatableValue {
                                        previous_value: (1731, 109),
                                        current_value: (1731, 109)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1743, 109),
                                        current_value: (1743, 109)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1710, 109),
                                    current_value: (1710, 109)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1719, 109),
                                    current_value: (1758, 109)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1710, 109),
                                current_value: (1710, 109)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1758, 109)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " | 4d | 4e |\n| 5a | 5b | 5c | 5d | 5e |\n| ",
                        start_position: UpdatableValue {
                            previous_value: (1758, 109),
                            current_value: (1758, 109)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1800, 111),
                            current_value: (1800, 111)
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
                                    start_position: UpdatableValue {
                                        previous_value: (1821, 111),
                                        current_value: (1821, 111)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1823, 111),
                                        current_value: (1823, 111)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1800, 111),
                                    current_value: (1800, 111)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1809, 111),
                                    current_value: (1838, 111)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1800, 111),
                                current_value: (1800, 111)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1838, 111)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " | 6b | 6c | 6d | ",
                        start_position: UpdatableValue {
                            previous_value: (1838, 111),
                            current_value: (1838, 111)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1856, 111),
                            current_value: (1856, 111)
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
                                    start_position: UpdatableValue {
                                        previous_value: (1877, 111),
                                        current_value: (1877, 111)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1879, 111),
                                        current_value: (1879, 111)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1856, 111),
                                    current_value: (1856, 111)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1865, 111),
                                    current_value: (1894, 111)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1856, 111),
                                current_value: (1856, 111)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1894, 111)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " |\n| 7a | 7b | 7c | 7d | 7e |\n\n### Table Columns\n\n| Col A | ",
                        start_position: UpdatableValue {
                            previous_value: (1894, 111),
                            current_value: (1894, 111)
                        },
                        end_position: UpdatableValue {
                            previous_value: (1954, 116),
                            current_value: (1954, 116)
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
                                    start_position: UpdatableValue {
                                        previous_value: (1975, 116),
                                        current_value: (1975, 116)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (1982, 116),
                                        current_value: (1982, 116)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (1954, 116),
                                    current_value: (1954, 116)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (1963, 116),
                                    current_value: (1997, 116)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (1954, 116),
                                current_value: (1954, 116)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (1997, 116)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " Col C | Col D ",
                        start_position: UpdatableValue {
                            previous_value: (1997, 116),
                            current_value: (1997, 116)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2012, 116),
                            current_value: (2012, 116)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2033, 116),
                                        current_value: (2033, 116)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2041, 116),
                                        current_value: (2041, 116)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2012, 116),
                                    current_value: (2012, 116)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2021, 116),
                                    current_value: (2056, 116)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2012, 116),
                                current_value: (2012, 116)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2056, 116)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n|---|",
                        start_position: UpdatableValue {
                            previous_value: (2056, 116),
                            current_value: (2056, 116)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2063, 117),
                            current_value: (2063, 117)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2084, 117),
                                        current_value: (2084, 117)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2088, 117),
                                        current_value: (2088, 117)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2063, 117),
                                    current_value: (2063, 117)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2072, 117),
                                    current_value: (2103, 117)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2063, 117),
                                current_value: (2063, 117)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2103, 117)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "---|---",
                        start_position: UpdatableValue {
                            previous_value: (2103, 117),
                            current_value: (2103, 117)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2110, 117),
                            current_value: (2110, 117)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2131, 117),
                                        current_value: (2131, 117)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2136, 117),
                                        current_value: (2136, 117)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2110, 117),
                                    current_value: (2110, 117)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2119, 117),
                                    current_value: (2151, 117)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2110, 117),
                                current_value: (2110, 117)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2151, 117)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 1a | ",
                        start_position: UpdatableValue {
                            previous_value: (2151, 117),
                            current_value: (2151, 117)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2160, 118),
                            current_value: (2160, 118)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2181, 118),
                                        current_value: (2181, 118)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2185, 118),
                                        current_value: (2185, 118)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2160, 118),
                                    current_value: (2160, 118)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2169, 118),
                                    current_value: (2200, 118)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2160, 118),
                                current_value: (2160, 118)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2200, 118)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 1c | 1d ",
                        start_position: UpdatableValue {
                            previous_value: (2200, 118),
                            current_value: (2200, 118)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2209, 118),
                            current_value: (2209, 118)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2230, 118),
                                        current_value: (2230, 118)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2235, 118),
                                        current_value: (2235, 118)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2209, 118),
                                    current_value: (2209, 118)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2218, 118),
                                    current_value: (2250, 118)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2209, 118),
                                current_value: (2209, 118)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2250, 118)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 2a | ",
                        start_position: UpdatableValue {
                            previous_value: (2250, 118),
                            current_value: (2250, 118)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2259, 119),
                            current_value: (2259, 119)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2280, 119),
                                        current_value: (2280, 119)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2284, 119),
                                        current_value: (2284, 119)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2259, 119),
                                    current_value: (2259, 119)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2268, 119),
                                    current_value: (2299, 119)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2259, 119),
                                current_value: (2259, 119)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2299, 119)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 2c | 2d ",
                        start_position: UpdatableValue {
                            previous_value: (2299, 119),
                            current_value: (2299, 119)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2308, 119),
                            current_value: (2308, 119)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2329, 119),
                                        current_value: (2329, 119)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2334, 119),
                                        current_value: (2334, 119)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2308, 119),
                                    current_value: (2308, 119)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2317, 119),
                                    current_value: (2349, 119)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2308, 119),
                                current_value: (2308, 119)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2349, 119)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 3a | ",
                        start_position: UpdatableValue {
                            previous_value: (2349, 119),
                            current_value: (2349, 119)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2358, 120),
                            current_value: (2358, 120)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2379, 120),
                                        current_value: (2379, 120)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2383, 120),
                                        current_value: (2383, 120)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2358, 120),
                                    current_value: (2358, 120)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2367, 120),
                                    current_value: (2398, 120)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2358, 120),
                                current_value: (2358, 120)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2398, 120)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 3c | 3d ",
                        start_position: UpdatableValue {
                            previous_value: (2398, 120),
                            current_value: (2398, 120)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2407, 120),
                            current_value: (2407, 120)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2428, 120),
                                        current_value: (2428, 120)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2433, 120),
                                        current_value: (2433, 120)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2407, 120),
                                    current_value: (2407, 120)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2416, 120),
                                    current_value: (2448, 120)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2407, 120),
                                current_value: (2407, 120)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2448, 120)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n| 3a | ",
                        start_position: UpdatableValue {
                            previous_value: (2448, 120),
                            current_value: (2448, 120)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2457, 121),
                            current_value: (2457, 121)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2478, 121),
                                        current_value: (2478, 121)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2482, 121),
                                        current_value: (2482, 121)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2457, 121),
                                    current_value: (2457, 121)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2466, 121),
                                    current_value: (2497, 121)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2457, 121),
                                current_value: (2457, 121)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2497, 121)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: " 3c | 3d ",
                        start_position: UpdatableValue {
                            previous_value: (2497, 121),
                            current_value: (2497, 121)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2506, 121),
                            current_value: (2506, 121)
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
                                    start_position: UpdatableValue {
                                        previous_value: (2527, 121),
                                        current_value: (2527, 121)
                                    },
                                    end_position: UpdatableValue {
                                        previous_value: (2532, 121),
                                        current_value: (2532, 121)
                                    }
                                })],
                                start_position: UpdatableValue {
                                    previous_value: (2506, 121),
                                    current_value: (2506, 121)
                                },
                                end_position: UpdatableValue {
                                    previous_value: (2515, 121),
                                    current_value: (2547, 121)
                                }
                            })],
                            start_position: UpdatableValue {
                                previous_value: (2506, 121),
                                current_value: (2506, 121)
                            },
                            end_position: UpdatableValue {
                                previous_value: (0, 0),
                                current_value: (2547, 121)
                            }
                        }
                    )]
                }),
                ApplicabilityExprKind::None(ApplicabilityExprContainer {
                    contents: vec![ApplicabilityExprKind::Text(Text {
                        text: "|\n\n",
                        start_position: UpdatableValue {
                            previous_value: (2547, 121),
                            current_value: (2547, 121)
                        },
                        end_position: UpdatableValue {
                            previous_value: (2550, 123),
                            current_value: (2550, 123)
                        }
                    })]
                })
            ]
        );
    }
}
