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
pub struct ApplicabilityPlantumlLexerConfig<'a, 'b, 'c, 'd> {
    start_comment_finder: memmem::Finder<'a>,
    end_comment_finder: memmem::Finder<'b>,
    start_block_finder: memmem::Finder<'c>,
    end_block_finder: memmem::Finder<'d>,
}
impl DefaultApplicabilityLexer for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    fn is_default() -> bool {
        true
    }
}
impl ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    pub fn new() -> Self {
        ApplicabilityPlantumlLexerConfig {
            start_comment_finder: memmem::Finder::new("'"),
            end_comment_finder: memmem::Finder::new("'"),
            start_block_finder: memmem::Finder::new("/`"),
            end_block_finder: memmem::Finder::new("'/"),
        }
    }
}

impl Default for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    fn default() -> Self {
        ApplicabilityPlantumlLexerConfig::new()
    }
}

impl StartCommentSingleLineTerminated for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
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
        self.start_block_finder.find(input.as_bytes())
    }

    fn has_start_comment_single_line_terminated_support(&self) -> bool {
        true
    }

    fn start_comment_single_line_terminated_tag<'x>(&self) -> &'x str {
        "/'"
    }
}

impl EndCommentSingleLineTerminated for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '\''
    }

    fn end_comment_single_line_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.end_block_finder.find(input.as_bytes())
    }
    fn has_end_comment_single_line_terminated_support(&self) -> bool {
        true
    }

    fn end_comment_single_line_tag<'x>(&self) -> &'x str {
        "'/"
    }
}

impl StartCommentSingleLineNonTerminated for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    fn is_start_comment_single_line_non_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '\''
    }

    fn has_start_comment_single_line_non_terminated_support(&self) -> bool {
        true
    }

    fn start_comment_single_line_non_terminated_tag<'x>(&self) -> &'x str {
        "'"
    }
    fn start_comment_single_line_non_terminated_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.start_comment_finder.find(input.as_bytes())
    }
}

impl StartCommentMultiLine for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    fn is_start_comment_multi_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '/'
    }

    fn start_comment_multi_line_tag<'x>(&self) -> &'x str {
        "/'"
    }

    fn has_start_comment_multi_line_support(&self) -> bool {
        true
    }
    fn start_comment_multi_line_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.start_block_finder.find(input.as_bytes())
    }
}
impl EndCommentMultiLine for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    fn is_end_comment_multi_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '\''
    }

    fn end_comment_multi_line_tag<'x>(&self) -> &'x str {
        "'/"
    }

    fn has_end_comment_multi_line_support(&self) -> bool {
        true
    }
    fn end_comment_multi_line_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + nom::AsBytes,
    {
        self.end_block_finder.find(input.as_bytes())
    }
}

impl StartCodeBlock for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    fn is_start_code_block<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '`'
    }

    fn has_start_code_block_support(&self) -> bool {
        false
    }

    fn start_code_block_tag<'x>(&self) -> &'x str {
        ""
    }
}
impl EndCodeBlock for ApplicabilityPlantumlLexerConfig<'_, '_, '_, '_> {
    fn is_end_code_block<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '`'
    }

    fn has_end_code_block_support(&self) -> bool {
        false
    }

    fn end_code_block_tag<'x>(&self) -> &'x str {
        ""
    }
}
