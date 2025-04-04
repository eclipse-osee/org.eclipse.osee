use crate::{
    config::{
        applic_else::ConfigurationElse, base::ConfigurationBase, case::ConfigurationCase,
        else_if::ConfigurationElseIf, end::ConfigurationEnd, not::ConfigurationNot,
        switch::ConfigurationSwitch,
    },
    config_group::{
        applic_else::ConfigurationGroupElse, base::ConfigurationGroupBase,
        case::ConfigurationGroupCase, else_if::ConfigurationGroupElseIf,
        end::ConfigurationGroupEnd, not::ConfigurationGroupNot, switch::ConfigurationGroupSwitch,
    },
    delimiters::{
        brace::{EndBrace, StartBrace},
        paren::{EndParen, StartParen},
        space::Space,
        tab::Tab,
    },
    feature::{
        applic_else::FeatureElse, base::FeatureBase, case::FeatureCase, else_if::FeatureElseIf,
        end::FeatureEnd, not::FeatureNot, switch::FeatureSwitch,
    },
    line_terminations::{carriage_return::CarriageReturn, eof::Eof, new_line::NewLine},
    logic::{and::And, not::Not, or::Or},
    substitution::Substitution,
};
use nom::{
    bytes::tag,
    combinator::eof,
    error::ParseError,
    AsChar, Compare, Input, Parser,
};

pub trait DefaultApplicabilityLexer {
    fn is_default() -> bool;
}
impl<T> ConfigurationElse for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationBase for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationCase for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationElseIf for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationEnd for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationNot for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationSwitch for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationGroupElse for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationGroupBase for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationGroupCase for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationGroupElseIf for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationGroupEnd for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationGroupNot for T where T: DefaultApplicabilityLexer {}
impl<T> ConfigurationGroupSwitch for T where T: DefaultApplicabilityLexer {}
impl<T> StartBrace for T where T: DefaultApplicabilityLexer {}
impl<T> EndBrace for T where T: DefaultApplicabilityLexer {}
impl<T> StartParen for T where T: DefaultApplicabilityLexer {}
impl<T> EndParen for T where T: DefaultApplicabilityLexer {}
impl<T> Space for T where T: DefaultApplicabilityLexer {}
impl<T> Tab for T where T: DefaultApplicabilityLexer {}
impl<T> FeatureElse for T where T: DefaultApplicabilityLexer {}
impl<T> FeatureBase for T where T: DefaultApplicabilityLexer {}
impl<T> FeatureCase for T where T: DefaultApplicabilityLexer {}
impl<T> FeatureElseIf for T where T: DefaultApplicabilityLexer {}
impl<T> FeatureEnd for T where T: DefaultApplicabilityLexer {}
impl<T> FeatureNot for T where T: DefaultApplicabilityLexer {}
impl<T> FeatureSwitch for T where T: DefaultApplicabilityLexer {}
impl<T> And for T where T: DefaultApplicabilityLexer {}
impl<T> Not for T where T: DefaultApplicabilityLexer {}
impl<T> Or for T where T: DefaultApplicabilityLexer {}
impl<T> CarriageReturn for T
where
    T: DefaultApplicabilityLexer,
{
    fn is_carriage_return<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '\r'
    }

    fn carriage_return<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
        O: From<I>,
    {
        tag("\r").map(|x: I| x.into())
    }
}
impl<T> Eof for T
where
    T: DefaultApplicabilityLexer,
{
    fn is_eof<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char().len() == 0
    }

    fn eof<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        eof
    }
}
impl<T> NewLine for T
where
    T: DefaultApplicabilityLexer,
{
    fn is_new_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        input.as_char() == '\n'
    }

    fn new_line<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        O: From<I>,
        E: ParseError<I>,
    {
        tag("\n").map(|x: I| x.into())
    }
}
impl<T> Substitution for T where T: DefaultApplicabilityLexer {}
