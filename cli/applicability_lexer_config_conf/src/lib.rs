/*********************************************************************
 * Copyright (c) 2026 Boeing
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
use applicability_lexer_base::{
    comment::{
        code_block::{EndCodeBlock, StartCodeBlock},
        multi_line::{EndCommentMultiLine, StartCommentMultiLine},
        single_line::{
            EndCommentSingleLineTerminated, StartCommentSingleLineNonTerminated,
            StartCommentSingleLineTerminated,
        },
    },
    default::DefaultApplicabilityLexer,
};
use memchr::memmem;
use nom::{AsChar, Input};

/// Lexer config for conf-style files (e.g. `.conf`) which use `;` to begin a
/// single-line, non-terminated comment.
pub struct ApplicabilityConfLexerConfig<'a> {
    start_comment_finder: memmem::Finder<'a>,
}
impl DefaultApplicabilityLexer for ApplicabilityConfLexerConfig<'_> {
    fn is_default() -> bool {
        true
    }
}
impl ApplicabilityConfLexerConfig<'_> {
    pub fn new() -> Self {
        ApplicabilityConfLexerConfig {
            start_comment_finder: memmem::Finder::new(";"),
        }
    }
}

impl Default for ApplicabilityConfLexerConfig<'_> {
    fn default() -> Self {
        ApplicabilityConfLexerConfig::new()
    }
}

impl StartCommentSingleLineTerminated for ApplicabilityConfLexerConfig<'_> {
    fn is_start_comment_single_line_terminated<I>(&self, _input: I::Item) -> bool
    where
        I: Input,
        I::Item: nom::AsChar,
    {
        false
    }

    fn has_start_comment_single_line_terminated_support(&self) -> bool {
        false
    }

    fn start_comment_single_line_terminated_tag<'x>(&self) -> &'x str {
        ""
    }
}

impl EndCommentSingleLineTerminated for ApplicabilityConfLexerConfig<'_> {
    fn is_end_comment_single_line<I>(&self, _input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        false
    }

    fn has_end_comment_single_line_terminated_support(&self) -> bool {
        false
    }

    fn end_comment_single_line_tag<'x>(&self) -> &'x str {
        ""
    }
}

impl StartCommentSingleLineNonTerminated for ApplicabilityConfLexerConfig<'_> {
    fn is_start_comment_single_line_non_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == ';'
    }

    fn has_start_comment_single_line_non_terminated_support(&self) -> bool {
        true
    }
    fn start_comment_single_line_non_terminated_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.start_comment_finder.find(input.as_bytes())
    }

    fn start_comment_single_line_non_terminated_tag<'x>(&self) -> &'x str {
        ";"
    }
}

impl StartCommentMultiLine for ApplicabilityConfLexerConfig<'_> {
    fn is_start_comment_multi_line<I>(&self, _input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        false
    }

    fn start_comment_multi_line_tag<'x>(&self) -> &'x str {
        ""
    }

    fn has_start_comment_multi_line_support(&self) -> bool {
        false
    }
}
impl EndCommentMultiLine for ApplicabilityConfLexerConfig<'_> {
    fn is_end_comment_multi_line<I>(&self, _input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        false
    }

    fn end_comment_multi_line_tag<'x>(&self) -> &'x str {
        ""
    }

    fn has_end_comment_multi_line_support(&self) -> bool {
        false
    }
}
impl StartCodeBlock for ApplicabilityConfLexerConfig<'_> {
    fn is_start_code_block<I>(&self, _input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        false
    }

    fn has_start_code_block_support(&self) -> bool {
        false
    }

    fn start_code_block_tag<'x>(&self) -> &'x str {
        ""
    }
}
impl EndCodeBlock for ApplicabilityConfLexerConfig<'_> {
    fn is_end_code_block<I>(&self, _input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        false
    }

    fn has_end_code_block_support(&self) -> bool {
        false
    }

    fn end_code_block_tag<'x>(&self) -> &'x str {
        ""
    }
}
