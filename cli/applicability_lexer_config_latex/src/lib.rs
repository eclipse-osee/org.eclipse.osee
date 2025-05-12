use applicability_lexer_base::{
    comment::{
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

pub struct ApplicabilityLatexLexerConfig<'a, 'b> {
    start_comment_finder: memmem::Finder<'a>,
    end_comment_finder: memmem::Finder<'b>,
}
impl DefaultApplicabilityLexer for ApplicabilityLatexLexerConfig<'_, '_> {
    fn is_default() -> bool {
        true
    }
}
impl ApplicabilityLatexLexerConfig<'_, '_> {
    pub fn new() -> Self {
        ApplicabilityLatexLexerConfig {
            start_comment_finder: memmem::Finder::new("\\if"),
            end_comment_finder: memmem::Finder::new("{}"),
        }
    }
}

impl Default for ApplicabilityLatexLexerConfig<'_, '_> {
    fn default() -> Self {
        ApplicabilityLatexLexerConfig::new()
    }
}

impl StartCommentSingleLineTerminated for ApplicabilityLatexLexerConfig<'_, '_> {
    fn is_start_comment_single_line_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: nom::AsChar,
    {
        input.as_char() == '\\'
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
        "\\if"
    }
}

impl EndCommentSingleLineTerminated for ApplicabilityLatexLexerConfig<'_, '_> {
    fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '{'
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
        "{}"
    }
}

impl StartCommentSingleLineNonTerminated for ApplicabilityLatexLexerConfig<'_, '_> {
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

impl StartCommentMultiLine for ApplicabilityLatexLexerConfig<'_, '_> {
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
impl EndCommentMultiLine for ApplicabilityLatexLexerConfig<'_, '_> {
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
