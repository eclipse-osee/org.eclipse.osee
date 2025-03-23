use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::utils::locatable::Locatable,
    second_stage::{
        base::config_group::{
            applic_else::LexConfigurationGroupElse, end::LexConfigurationGroupEnd,
            switch::LexConfigurationGroupSwitch,
        },
        token::LexerToken,
    },
};

use super::{
    base::ConfigGroupBaseSingleLineNonTerminated, case::ConfigGroupCaseSingleLineNonTerminated,
    else_if::ConfigGroupElseIfSingleLineNonTerminated, not::ConfigGroupNotSingleLineNonTerminated,
};

pub trait ConfigGroupTagSingleLineNonTerminated {
    fn config_group_tag_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupTagSingleLineNonTerminated for T
where
    T: ConfigGroupBaseSingleLineNonTerminated
        + ConfigGroupNotSingleLineNonTerminated
        + ConfigGroupCaseSingleLineNonTerminated
        + ConfigGroupElseIfSingleLineNonTerminated
        + LexConfigurationGroupElse
        + LexConfigurationGroupEnd
        + LexConfigurationGroupSwitch,
{
    fn config_group_tag_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let config_group_base_tag = self.get_config_group_base_non_terminated();
        let config_group_not_tag = self.config_group_not_non_terminated();
        let config_group_case_tag = self.config_group_case_non_terminated();
        let config_group_else_if_tag = self.config_group_else_if_non_terminated();
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
