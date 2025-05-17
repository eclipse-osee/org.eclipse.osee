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

pub struct ApplicabilityCustomLexerConfig<'a, 'b, 'c, 'd> {
    start_comment_finder: memmem::Finder<'a>,
    end_comment_finder: memmem::Finder<'b>,
    #[allow(dead_code)]
    start_comment_tag: &'c str,
    #[allow(dead_code)]
    end_comment_tag: &'d str,
}
impl DefaultApplicabilityLexer for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
    fn is_default() -> bool {
        true
    }
}
impl<'a, 'b, 'c, 'd> ApplicabilityCustomLexerConfig<'a, 'b, 'c, 'd>
where
    'a: 'c,
    'b: 'd,
{
    pub fn new(start: &'a str, end: &'b str) -> Self {
        ApplicabilityCustomLexerConfig {
            start_comment_finder: memmem::Finder::new(start),
            end_comment_finder: memmem::Finder::new(end),
            start_comment_tag: start,
            end_comment_tag: end,
        }
    }
}

impl Default for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
    fn default() -> Self {
        ApplicabilityCustomLexerConfig::new("", "")
    }
}

impl StartCommentSingleLineTerminated for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
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
        ""
        //TODO
    }
}

impl EndCommentSingleLineTerminated for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
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
        ""
        //TODO replace this with tag present in config
    }
}

impl StartCommentSingleLineNonTerminated for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
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

impl StartCommentMultiLine for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
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
impl EndCommentMultiLine for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
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
impl StartCodeBlock for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
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
impl EndCodeBlock for ApplicabilityCustomLexerConfig<'_, '_, '_, '_> {
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
