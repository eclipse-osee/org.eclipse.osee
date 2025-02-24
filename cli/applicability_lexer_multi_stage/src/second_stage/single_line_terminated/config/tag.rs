use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::config::{
        applic_else::ConfigurationElse, end::ConfigurationEnd, switch::ConfigurationSwitch,
    },
    second_stage::token::LexerToken,
};

use super::{
    base::ConfigBaseSingleLineTerminated, case::ConfigCaseSingleLineTerminated,
    else_if::ConfigElseIfSingleLineTerminated, not::ConfigNotSingleLineTerminated,
};

pub trait ConfigTagSingleLineTerminated {
    fn config_tag_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigTagSingleLineTerminated for T
where
    T: ConfigBaseSingleLineTerminated
        + ConfigNotSingleLineTerminated
        + ConfigCaseSingleLineTerminated
        + ConfigElseIfSingleLineTerminated
        + ConfigurationElse
        + ConfigurationEnd
        + ConfigurationSwitch,
{
    fn config_tag_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let config_base_tag = self.get_config_base_terminated();
        let config_not_tag = self.config_not_terminated();
        let config_case_tag = self.config_case_terminated();
        let config_else_if_tag = self.config_else_if_terminated();
        let config_tag = config_not_tag
            .or(config_case_tag)
            .or(config_else_if_tag)
            .or(self
                .config_else()
                .map(|_| vec![LexerToken::ConfigurationElse]))
            .or(self
                .config_end()
                .map(|_| vec![LexerToken::EndConfiguration]))
            .or(self
                .config_switch()
                .map(|_| vec![LexerToken::ConfigurationSwitch]))
            .or(config_base_tag);
        config_tag
    }
}
