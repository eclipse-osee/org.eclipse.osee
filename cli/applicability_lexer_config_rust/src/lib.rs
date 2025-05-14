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

pub struct ApplicabilityRustLexerConfig<'a, 'b, 'c, 'd, 'e> {
    start_terminated_comment_finder: memmem::Finder<'a>,
    end_terminated_comment_finder: memmem::Finder<'b>,
    start_non_terminated_comment_finder: memmem::Finder<'c>,
    start_multiline_comment_finder: memmem::Finder<'d>,
    end_multiline_comment_finder: memmem::Finder<'e>,
}
impl DefaultApplicabilityLexer for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
    fn is_default() -> bool {
        true
    }
}
impl ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
    pub fn new() -> Self {
        ApplicabilityRustLexerConfig {
            start_terminated_comment_finder: memmem::Finder::new("/*"),
            end_terminated_comment_finder: memmem::Finder::new("*/"),
            start_non_terminated_comment_finder: memmem::Finder::new("//"),
            start_multiline_comment_finder: memmem::Finder::new("/*"),
            end_multiline_comment_finder: memmem::Finder::new("*/"),
        }
    }
}

impl Default for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
    fn default() -> Self {
        ApplicabilityRustLexerConfig::new()
    }
}

impl StartCommentSingleLineTerminated for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
    fn is_start_comment_single_line_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: nom::AsChar,
    {
        input.as_char() == '/'
    }
    fn start_comment_single_line_terminated_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.start_terminated_comment_finder.find(input.as_bytes())
    }

    fn has_start_comment_single_line_terminated_support(&self) -> bool {
        true
    }

    fn start_comment_single_line_terminated_tag<'x>(&self) -> &'x str {
        "/*"
    }
}

impl EndCommentSingleLineTerminated for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
    fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '*'
    }

    fn end_comment_single_line_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.end_terminated_comment_finder.find(input.as_bytes())
    }
    fn has_end_comment_single_line_terminated_support(&self) -> bool {
        true
    }

    fn end_comment_single_line_tag<'x>(&self) -> &'x str {
        "*/"
    }
}

impl StartCommentSingleLineNonTerminated for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
    fn is_start_comment_single_line_non_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '/'
    }

    fn has_start_comment_single_line_non_terminated_support(&self) -> bool {
        true
    }
    fn start_comment_single_line_non_terminated_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.start_non_terminated_comment_finder
            .find(input.as_bytes())
    }

    fn start_comment_single_line_non_terminated_tag<'x>(&self) -> &'x str {
        "//"
    }
}

impl StartCommentMultiLine for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
    fn is_start_comment_multi_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '/'
    }

    fn start_comment_multi_line_tag<'x>(&self) -> &'x str {
        "/*"
    }
    fn start_comment_multi_line_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.start_multiline_comment_finder.find(input.as_bytes())
    }

    fn has_start_comment_multi_line_support(&self) -> bool {
        true
    }
}
impl EndCommentMultiLine for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
    fn is_end_comment_multi_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '*'
    }

    fn end_comment_multi_line_tag<'x>(&self) -> &'x str {
        "*/"
    }

    fn end_comment_multi_line_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.end_multiline_comment_finder.find(input.as_bytes())
    }
    fn has_end_comment_multi_line_support(&self) -> bool {
        true
    }
}

impl StartCodeBlock for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
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
impl EndCodeBlock for ApplicabilityRustLexerConfig<'_, '_, '_, '_, '_> {
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
