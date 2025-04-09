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
    base::ConfigGroupBaseSingleLineTerminated, case::ConfigGroupCaseSingleLineTerminated,
    else_if::ConfigGroupElseIfSingleLineTerminated, not::ConfigGroupNotSingleLineTerminated,
};

pub trait ConfigGroupTagSingleLineTerminated {
    fn config_group_tag_terminated<I, E>(
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

impl<T> ConfigGroupTagSingleLineTerminated for T
where
    T: ConfigGroupBaseSingleLineTerminated
        + ConfigGroupNotSingleLineTerminated
        + ConfigGroupCaseSingleLineTerminated
        + ConfigGroupElseIfSingleLineTerminated
        + LexConfigurationGroupElse
        + LexConfigurationGroupEnd
        + LexConfigurationGroupSwitch,
{
    fn config_group_tag_terminated<I, E>(
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
        let config_group_base_tag = self.get_config_group_base_terminated();
        let config_group_not_tag = self.config_group_not_terminated();
        let config_group_case_tag = self.config_group_case_terminated();
        let config_group_else_if_tag = self.config_group_else_if_terminated();
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
