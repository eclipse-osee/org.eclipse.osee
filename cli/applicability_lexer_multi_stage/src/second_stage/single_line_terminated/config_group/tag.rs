use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::config_group::{
        applic_else::ConfigurationGroupElse, end::ConfigurationGroupEnd,
        switch::ConfigurationGroupSwitch,
    },
    second_stage::token::LexerToken,
};

use super::{
    base::ConfigGroupBaseSingleLineTerminated, case::ConfigGroupCaseSingleLineTerminated,
    else_if::ConfigGroupElseIfSingleLineTerminated, not::ConfigGroupNotSingleLineTerminated,
};

pub trait ConfigGroupTagSingleLineTerminated {
    fn config_group_tag_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupTagSingleLineTerminated for T
where
    T: ConfigGroupBaseSingleLineTerminated
        + ConfigGroupNotSingleLineTerminated
        + ConfigGroupCaseSingleLineTerminated
        + ConfigGroupElseIfSingleLineTerminated
        + ConfigurationGroupElse
        + ConfigurationGroupEnd
        + ConfigurationGroupSwitch,
{
    fn config_group_tag_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
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
            .or(self
                .config_group_else()
                .map(|_| vec![LexerToken::ConfigurationGroupElse]))
            .or(self
                .config_group_end()
                .map(|_| vec![LexerToken::EndConfigurationGroup]))
            .or(self
                .config_group_switch()
                .map(|_| vec![LexerToken::ConfigurationGroupSwitch]))
            .or(config_group_base_tag);
        config_group_tag
    }
}
