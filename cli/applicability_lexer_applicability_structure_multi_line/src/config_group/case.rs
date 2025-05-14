use nom::{AsChar, Compare, FindSubstring, Input, Parser, error::ParseError, multi::many0};

use crate::utils::tag_multi_line::TagMultiLine;
use applicability_lexer_applicability_structure_base::{
    config_group::case::LexConfigurationGroupCase,
    delimiters::{space::LexSpace, tab::LexTab},
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait ConfigGroupCaseMultiLine {
    fn config_group_case_multi_line<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupCaseMultiLine for T
where
    T: TagMultiLine + LexConfigurationGroupCase + LexSpace + LexTab,
{
    fn config_group_case_multi_line<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let tag = self.multi_line_tag();
        let config_group_case_tag = self
            .lex_config_group_case()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        config_group_case_tag
    }
}
