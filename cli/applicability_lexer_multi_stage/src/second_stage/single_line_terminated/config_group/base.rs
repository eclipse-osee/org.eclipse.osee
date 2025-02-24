use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::{
        comment::single_line::{EndCommentSingleLine, StartCommentSingleLine},
        config_group::base::ConfigurationGroupBase,
        delimiters::{space::Space, tab::Tab},
        logic::{and::And, not::Not, or::Or},
    },
    second_stage::{
        single_line_terminated::utils::tag_terminated::TagTerminated, token::LexerToken,
    },
};

pub trait ConfigGroupBaseSingleLineTerminated {
    fn get_config_group_base_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupBaseSingleLineTerminated for T
where
    T: StartCommentSingleLine
        + EndCommentSingleLine
        + ConfigurationGroupBase
        + Space
        + Tab
        + Not
        + And
        + Or
        + TagTerminated,
{
    fn get_config_group_base_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let config_group_base_tag = self
            .config_group_base()
            .map(|_| LexerToken::ConfigurationGroup)
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
        config_group_base_tag
    }
}
