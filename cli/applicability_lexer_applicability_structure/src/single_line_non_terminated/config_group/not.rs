use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};
use crate::{
        base::{
            config_group::not::LexConfigurationGroupNot,
            delimiters::{space::LexSpace, tab::LexTab},
        },
        single_line_non_terminated::utils::tag_non_terminated::TagNonTerminated,
        
    };

pub trait ConfigGroupNotSingleLineNonTerminated {
    fn config_group_not_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupNotSingleLineNonTerminated for T
where
    T: TagNonTerminated + LexConfigurationGroupNot + LexSpace + LexTab,
{
    fn config_group_not_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let tag = self.non_terminated_tag();
        let config_group_not_tag = self
            .lex_config_group_not()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        config_group_not_tag
    }
}
