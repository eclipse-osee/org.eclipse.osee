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

#[derive(Clone)]
pub struct ApplicabilityMarkdownLexerConfig<'a, 'b, 'c, 'd> {
    start_comment_finder: memmem::Finder<'a>,
    end_comment_finder: memmem::Finder<'b>,
    start_code_block_finder: memmem::Finder<'c>,
    end_code_block_finder: memmem::Finder<'d>,
}
impl DefaultApplicabilityLexer for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
    fn is_default() -> bool {
        true
    }
}
impl ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
    pub fn new() -> Self {
        ApplicabilityMarkdownLexerConfig {
            start_comment_finder: memmem::Finder::new("``"),
            end_comment_finder: memmem::Finder::new("``"),
            start_code_block_finder: memmem::Finder::new("```"),
            end_code_block_finder: memmem::Finder::new("```"),
        }
    }
}

impl Default for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
    fn default() -> Self {
        ApplicabilityMarkdownLexerConfig::new()
    }
}

impl StartCommentSingleLineTerminated for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
    fn is_start_comment_single_line_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: nom::AsChar,
    {
        input.as_char() == '`'
    }
    fn start_comment_single_line_terminated_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.start_comment_finder.find(input.as_bytes())
    }

    fn has_start_comment_single_line_terminated_support(&self) -> bool {
        true
    }

    fn start_comment_single_line_terminated_tag<'x>(&self) -> &'x str {
        "``"
    }
}

impl EndCommentSingleLineTerminated for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
    fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '`'
    }

    fn end_comment_single_line_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.end_comment_finder.find(input.as_bytes())
    }
    fn has_end_comment_single_line_terminated_support(&self) -> bool {
        true
    }

    fn end_comment_single_line_tag<'x>(&self) -> &'x str {
        "``"
    }
}

impl StartCommentSingleLineNonTerminated for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
    fn is_start_comment_single_line_non_terminated<I>(&self, _input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        false
    }

    fn has_start_comment_single_line_non_terminated_support(&self) -> bool {
        false
    }

    fn start_comment_single_line_non_terminated_tag<'x>(&self) -> &'x str {
        ""
    }
}

impl StartCommentMultiLine for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
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
impl EndCommentMultiLine for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
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

impl StartCodeBlock for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
    fn is_start_code_block<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '`'
    }

    fn has_start_code_block_support(&self) -> bool {
        true
    }

    fn start_code_block_tag<'x>(&self) -> &'x str {
        "```"
    }
    fn start_code_block_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.start_code_block_finder.find(input.as_bytes())
    }
}
impl EndCodeBlock for ApplicabilityMarkdownLexerConfig<'_, '_, '_, '_> {
    fn is_end_code_block<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '`'
    }

    fn has_end_code_block_support(&self) -> bool {
        true
    }

    fn end_code_block_tag<'x>(&self) -> &'x str {
        "```"
    }
    fn end_code_block_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.end_code_block_finder.find(input.as_bytes())
    }
}
