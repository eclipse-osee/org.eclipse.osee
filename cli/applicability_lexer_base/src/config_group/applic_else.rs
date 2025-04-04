use nom::{
    bytes::{tag,  take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

use crate::default::DefaultApplicabilityLexer;

pub trait ConfigurationGroupElse {
    fn config_group_else<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.config_group_else_tag())
    }
    fn config_group_else_tag<'x>(&self) -> &'x str {
        "ConfigurationGroup Else"
    }
    fn take_until_config_group_else<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.config_group_else_tag())
    }
}
impl<T> ConfigurationGroupElse for T where T: DefaultApplicabilityLexer {}
