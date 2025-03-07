use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::{config::case::ConfigurationCase, utils::locatable::Locatable},
    second_stage::{
        base::{
            config::case::LexConfigurationCase,
            line_terminations::{space::LexSpace, tab::LexTab},
        },
        single_line_terminated::utils::tag_terminated::TagTerminated,
        token::LexerToken,
    },
};

pub trait ConfigCaseSingleLineTerminated {
    fn config_case_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigCaseSingleLineTerminated for T
where
    T: TagTerminated + LexConfigurationCase + LexSpace + LexTab,
{
    fn config_case_terminated<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let config_case_tag = self
            .lex_config_case()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        config_case_tag
    }
}
