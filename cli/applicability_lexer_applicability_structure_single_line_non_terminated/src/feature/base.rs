use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use applicability_lexer_applicability_structure_base::{
        delimiters::{space::LexSpace, tab::LexTab},
        feature::base::LexFeatureBase,
    };
use crate::utils::tag_non_terminated::TagNonTerminated;
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait FeatureBaseSingleLineNonTerminated {
    fn get_feature_base_non_terminated<I, E>(
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

impl<T> FeatureBaseSingleLineNonTerminated for T
where
    T: LexFeatureBase + LexSpace + LexTab + TagNonTerminated,
{
    fn get_feature_base_non_terminated<I, E>(
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
        let tag = self.non_terminated_tag();
        let feature_base_tag = self
            .lex_feature_base()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        feature_base_tag
    }
}
