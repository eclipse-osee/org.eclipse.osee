use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::second_stage::{
    base::{
        config_group::not::LexConfigurationGroupNot,
        delimiters::{space::LexSpace, tab::LexTab},
    },
    single_line_terminated::utils::tag_terminated::TagTerminated,
    token::LexerToken,
};
use applicability_lexer_base::utils::locatable::Locatable;

pub trait ConfigGroupNotSingleLineTerminated {
    fn config_group_not_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupNotSingleLineTerminated for T
where
    T: TagTerminated + LexConfigurationGroupNot + LexSpace + LexTab,
{
    fn config_group_not_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let config_group_not_tag = self
            .lex_config_group_not()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        config_group_not_tag
    }
}
