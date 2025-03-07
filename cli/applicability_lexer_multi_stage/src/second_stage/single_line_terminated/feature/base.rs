use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::utils::locatable::Locatable,
    second_stage::{
        base::{
            feature::base::LexFeatureBase,
            line_terminations::{space::LexSpace, tab::LexTab},
        },
        single_line_terminated::utils::tag_terminated::TagTerminated,
        token::LexerToken,
    },
};

pub trait FeatureBaseSingleLineTerminated {
    fn get_feature_base_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> FeatureBaseSingleLineTerminated for T
where
    T: LexFeatureBase + LexSpace + LexTab + TagTerminated,
{
    fn get_feature_base_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let feature_base_tag = self
            .lex_feature_base()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        feature_base_tag
    }
}
