use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::default::DefaultApplicabilityLexer;

pub trait ConfigurationElseIf {
    fn config_else_if<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.config_else_if_tag())
    }
    fn config_else_if_tag<'x>(&self) -> &'x str {
        "Configuration Else If"
    }
    fn take_until_config_else_if<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.config_else_if_tag())
    }
}
impl<T> ConfigurationElseIf for T where T: DefaultApplicabilityLexer {}
