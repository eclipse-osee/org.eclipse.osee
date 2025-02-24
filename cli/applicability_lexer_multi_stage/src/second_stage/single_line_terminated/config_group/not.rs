use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::{
        config_group::not::ConfigurationGroupNot,
        delimiters::{space::Space, tab::Tab},
    },
    second_stage::{
        single_line_terminated::utils::tag_terminated::TagTerminated, token::LexerToken,
    },
};

pub trait ConfigGroupNotSingleLineTerminated {
    fn config_group_not_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupNotSingleLineTerminated for T
where
    T: TagTerminated + ConfigurationGroupNot + Space + Tab,
{
    fn config_group_not_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let config_group_not_tag = self
            .config_group_not()
            .map(|_| LexerToken::ConfigurationGroupNot)
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
        config_group_not_tag
    }
}
