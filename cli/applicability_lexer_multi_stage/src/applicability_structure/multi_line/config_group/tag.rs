use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::applicability_structure::{
    base::config_group::{
        applic_else::LexConfigurationGroupElse, end::LexConfigurationGroupEnd,
        switch::LexConfigurationGroupSwitch,
    },
    token::LexerToken,
};
use applicability_lexer_base::utils::locatable::Locatable;

use super::{
    base::ConfigGroupBaseMultiLine, case::ConfigGroupCaseMultiLine,
    else_if::ConfigGroupElseIfMultiLine, not::ConfigGroupNotMultiLine,
};

pub trait ConfigGroupTagMultiLine {
    fn config_group_tag_multi_line<I, E>(
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

impl<T> ConfigGroupTagMultiLine for T
where
    T: ConfigGroupBaseMultiLine
        + ConfigGroupNotMultiLine
        + ConfigGroupCaseMultiLine
        + ConfigGroupElseIfMultiLine
        + LexConfigurationGroupElse
        + LexConfigurationGroupEnd
        + LexConfigurationGroupSwitch,
{
    fn config_group_tag_multi_line<I, E>(
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
        let config_group_base_tag = self.get_config_group_base_multi_line();
        let config_group_not_tag = self.config_group_not_multi_line();
        let config_group_case_tag = self.config_group_case_multi_line();
        let config_group_else_if_tag = self.config_group_else_if_multi_line();
        let config_group_tag = config_group_not_tag
            .or(config_group_case_tag)
            .or(config_group_else_if_tag)
            .or(self.lex_config_group_else().map(|x| vec![x]))
            .or(self.lex_config_group_end().map(|x| vec![x]))
            .or(self.lex_config_group_switch().map(|x| vec![x]))
            .or(config_group_base_tag);
        config_group_tag
    }
}
