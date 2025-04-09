use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::config::{
        applic_else::LexConfigurationElse, end::LexConfigurationEnd, switch::LexConfigurationSwitch,
    },
    
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

use super::{
    base::ConfigBaseSingleLineNonTerminated, case::ConfigCaseSingleLineNonTerminated,
    else_if::ConfigElseIfSingleLineNonTerminated, not::ConfigNotSingleLineNonTerminated,
};

pub trait ConfigTagSingleLineNonTerminated {
    fn config_tag_non_terminated<I, E>(
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

impl<T> ConfigTagSingleLineNonTerminated for T
where
    T: ConfigBaseSingleLineNonTerminated
        + ConfigNotSingleLineNonTerminated
        + ConfigCaseSingleLineNonTerminated
        + ConfigElseIfSingleLineNonTerminated
        + LexConfigurationElse
        + LexConfigurationEnd
        + LexConfigurationSwitch,
{
    fn config_tag_non_terminated<I, E>(
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
        let config_base_tag = self.get_config_base_non_terminated();
        let config_not_tag = self.config_not_non_terminated();
        let config_case_tag = self.config_case_non_terminated();
        let config_else_if_tag = self.config_else_if_non_terminated();
        let config_tag = config_not_tag
            .or(config_case_tag)
            .or(config_else_if_tag)
            .or(self.lex_config_else().map(|x| vec![x]))
            .or(self.lex_config_end().map(|x| vec![x]))
            .or(self.lex_config_switch().map(|x| vec![x]))
            .or(config_base_tag);
        config_tag
    }
}
