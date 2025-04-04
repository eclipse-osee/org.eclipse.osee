use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::second_stage::{
    base::config::{
        applic_else::LexConfigurationElse, end::LexConfigurationEnd, switch::LexConfigurationSwitch,
    },
    token::LexerToken,
};
use applicability_lexer_base::utils::locatable::Locatable;

use super::{
    base::ConfigBaseSingleLineTerminated, case::ConfigCaseSingleLineTerminated,
    else_if::ConfigElseIfSingleLineTerminated, not::ConfigNotSingleLineTerminated,
};

pub trait ConfigTagSingleLineTerminated {
    fn config_tag_terminated<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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

impl<T> ConfigTagSingleLineTerminated for T
where
    T: ConfigBaseSingleLineTerminated
        + ConfigNotSingleLineTerminated
        + ConfigCaseSingleLineTerminated
        + ConfigElseIfSingleLineTerminated
        + LexConfigurationElse
        + LexConfigurationEnd
        + LexConfigurationSwitch,
{
    fn config_tag_terminated<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
        let config_base_tag = self.get_config_base_terminated();
        let config_not_tag = self.config_not_terminated();
        let config_case_tag = self.config_case_terminated();
        let config_else_if_tag = self.config_else_if_terminated();
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
