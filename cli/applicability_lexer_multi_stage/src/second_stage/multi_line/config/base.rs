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
        multi_line::utils::tag_multi_line::TagMultiLine,
        token::LexerToken,
    },
};

pub trait ConfigBaseMultiLine {
    fn get_config_base_multi_line<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigBaseMultiLine for T
where
    T: LexConfigurationBase + LexSpace + LexTab + TagMultiLine,
{
    fn get_config_base_multi_line<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.multi_line_tag();
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
