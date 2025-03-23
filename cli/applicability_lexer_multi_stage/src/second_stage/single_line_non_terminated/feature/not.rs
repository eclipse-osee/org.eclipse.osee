use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::utils::locatable::Locatable,
    second_stage::{
        base::{
            delimiters::{space::LexSpace, tab::LexTab},
            feature::not::LexFeatureNot,
        },
        single_line_non_terminated::utils::tag_non_terminated::TagNonTerminated,
        token::LexerToken,
    },
};

pub trait FeatureNotSingleLineNonTerminated {
    fn feature_not_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> FeatureNotSingleLineNonTerminated for T
where
    T: TagNonTerminated + LexFeatureNot + LexSpace + LexTab,
{
    fn feature_not_non_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.non_terminated_tag();
        let feature_not_tag = self
            .lex_feature_not()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        feature_not_tag
    }
}
