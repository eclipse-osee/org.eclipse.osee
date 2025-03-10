use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::utils::locatable::Locatable,
    second_stage::{
        base::{
            config_group::base::LexConfigurationGroupBase,
            delimiters::{space::LexSpace, tab::LexTab},
        },
        single_line_non_terminated::utils::tag_non_terminated::TagNonTerminated,
        token::LexerToken,
    },
};

pub trait ConfigGroupBaseSingleLineNonTerminated {
    fn get_config_group_base_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupBaseSingleLineNonTerminated for T
where
    T: LexConfigurationGroupBase + LexSpace + LexTab + TagNonTerminated,
{
    fn get_config_group_base_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.non_terminated_tag();
        let config_group_base_tag = self
            .lex_config_group_base()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        config_group_base_tag
    }
}
