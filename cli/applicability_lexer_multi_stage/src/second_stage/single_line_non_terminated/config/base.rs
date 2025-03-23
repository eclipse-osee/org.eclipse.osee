use nom::{
    combinator::success, error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input,
    Parser,
};

use crate::{
    base::utils::locatable::Locatable,
    second_stage::{
        base::{
            config::base::LexConfigurationBase,
            delimiters::{space::LexSpace, tab::LexTab},
        },
        single_line_non_terminated::utils::tag_non_terminated::TagNonTerminated,
        token::LexerToken,
    },
};

pub trait ConfigBaseSingleLineNonTerminated {
    fn get_config_base_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigBaseSingleLineNonTerminated for T
where
    T: LexConfigurationBase + LexSpace + LexTab + TagNonTerminated,
{
    fn get_config_base_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.non_terminated_tag();
        let config_base_tag = self
            .lex_config_base()
            .and(many0(self.lex_space().or(self.lex_tab())).or(success(vec![])))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        config_base_tag
    }
}
