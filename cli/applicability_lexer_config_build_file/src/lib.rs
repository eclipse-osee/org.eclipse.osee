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

pub struct ApplicabilityBuildFileLexerConfig<'a> {
    start_comment_finder: memmem::Finder<'a>,
}
impl DefaultApplicabilityLexer for ApplicabilityBuildFileLexerConfig<'_> {
    fn is_default() -> bool {
        true
    }
}
impl ApplicabilityBuildFileLexerConfig<'_> {
    pub fn new() -> Self {
        ApplicabilityBuildFileLexerConfig {
            start_comment_finder: memmem::Finder::new("#"),
        }
    }
}

impl Default for ApplicabilityBuildFileLexerConfig<'_> {
    fn default() -> Self {
        ApplicabilityBuildFileLexerConfig::new()
    }
}

impl StartCommentSingleLineTerminated for ApplicabilityBuildFileLexerConfig<'_> {
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

impl EndCommentSingleLineTerminated for ApplicabilityBuildFileLexerConfig<'_> {
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

impl StartCommentSingleLineNonTerminated for ApplicabilityBuildFileLexerConfig<'_> {
    fn is_start_comment_single_line_non_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '#'
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
        "#"
    }
}

impl StartCommentMultiLine for ApplicabilityBuildFileLexerConfig<'_> {
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
impl EndCommentMultiLine for ApplicabilityBuildFileLexerConfig<'_> {
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

// impl CarriageReturn for ApplicabiltyBuildFileLexerConfig<'_, '_> {
//     fn is_carriage_return<I>(&self, input: I::Item) -> bool
//     where
//         I: Input,
//         I::Item: AsChar,
//     {
//         input.as_char() == '\r'
//     }
//     fn carriage_return_position<I>(&self, input: &I) -> Option<usize>
//     where
//         I: Input + AsBytes,
//     {
//         self.carriage_return_finder.find(input.as_bytes())
//     }
//     fn carriage_return<'x, I, O, E>(&self) -> impl nom::Parser<I, Output = O, Error = E>
//     where
//         I: Input + nom::Compare<&'x str>,
//         I::Item: AsChar,
//         E: nom::error::ParseError<I>,
//         O: From<I>,
//     {
//         tag("\r").map(|x: I| x.into())
//     }

//     fn take_till_carriage_return<'x, I, E>(&self) -> impl nom::Parser<I, Output = I, Error = E>
//     where
//         I: Input + nom::Compare<&'x str>,
//         I::Item: AsChar,
//         E: nom::error::ParseError<I>,
//     {
//         nom::bytes::take_till(|x| self.is_carriage_return::<I>(x))
//     }

//     fn carriage_return_tag<'x>(&self) -> &'x str {
//         "\r"
//     }

//     fn take_until_carriage_return<'x, I, E>(&self) -> impl nom::Parser<I, Output = I, Error = E>
//     where
//         I: Input + nom::Compare<&'x str> + nom::FindSubstring<&'x str>,
//         I::Item: AsChar,
//         E: nom::error::ParseError<I>,
//     {
//         nom::bytes::take_until(self.carriage_return_tag())
//     }
// }
