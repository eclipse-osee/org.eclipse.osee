use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::{
        config_group::base::LexConfigurationGroupBase,
        delimiters::{space::LexSpace, tab::LexTab},
    },
    single_line_terminated::utils::tag_terminated::TagTerminated,
    
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait ConfigGroupBaseSingleLineTerminated {
    fn get_config_group_base_terminated<I, E>(
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

impl<T> ConfigGroupBaseSingleLineTerminated for T
where
    T: LexConfigurationGroupBase + LexSpace + LexTab + TagTerminated,
{
    fn get_config_group_base_terminated<I, E>(
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
        let tag = self.terminated_tag();
        let config_group_base_tag = self
            .lex_config_group_base()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        config_group_base_tag
    }
}
