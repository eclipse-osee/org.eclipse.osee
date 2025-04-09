use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::applicability_structure::{
    base::{
        config::else_if::LexConfigurationElseIf,
        delimiters::{space::LexSpace, tab::LexTab},
    },
    multi_line::utils::tag_multi_line::TagMultiLine,
    token::LexerToken,
};
use applicability_lexer_base::utils::locatable::Locatable;

pub trait ConfigElseIfMultiLine {
    fn config_else_if_multi_line<I, E>(
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

impl<T> ConfigElseIfMultiLine for T
where
    T: TagMultiLine + LexConfigurationElseIf + LexSpace + LexTab,
{
    fn config_else_if_multi_line<I, E>(
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
        let tag = self.multi_line_tag();
        let config_else_if_tag = self
            .lex_config_else_if()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        config_else_if_tag
    }
}
