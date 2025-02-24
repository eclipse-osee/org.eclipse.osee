use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::{
        config_group::case::ConfigurationGroupCase,
        delimiters::{space::Space, tab::Tab},
    },
    second_stage::{
        single_line_terminated::utils::tag_terminated::TagTerminated, token::LexerToken,
    },
};

pub trait ConfigGroupCaseSingleLineTerminated {
    fn config_group_case_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupCaseSingleLineTerminated for T
where
    T: TagTerminated + ConfigurationGroupCase + Space + Tab,
{
    fn config_group_case_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let config_group_case_tag = self
            .config_group_case()
            .map(|_| LexerToken::ConfigurationGroupCase)
            .and(many0(
                self.space()
                    .map(|_| LexerToken::Space)
                    .or(self.tab().map(|_| LexerToken::Tab)),
            ))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        config_group_case_tag
    }
}
