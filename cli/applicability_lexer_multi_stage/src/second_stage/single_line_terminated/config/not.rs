use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::utils::locatable::Locatable,
    second_stage::{
        base::{
            config::not::LexConfigurationNot,
            delimiters::{space::LexSpace, tab::LexTab},
        },
        single_line_terminated::utils::tag_terminated::TagTerminated,
        token::LexerToken,
    },
};

pub trait ConfigNotSingleLineTerminated {
    fn config_not_terminated<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigNotSingleLineTerminated for T
where
    T: TagTerminated + LexConfigurationNot + LexSpace + LexTab,
{
    fn config_not_terminated<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let config_not_tag = self
            .lex_config_not()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        config_not_tag
    }
}
