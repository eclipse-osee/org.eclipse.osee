use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use applicability_lexer_applicability_structure_base::{
        delimiters::{space::LexSpace, tab::LexTab},
        feature::case::LexFeatureCase,
    };
use crate::utils::tag_non_terminated::TagNonTerminated;
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait FeatureCaseSingleLineNonTerminated {
    fn feature_case_non_terminated<I, E>(
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

impl<T> FeatureCaseSingleLineNonTerminated for T
where
    T: TagNonTerminated + LexFeatureCase + LexSpace + LexTab,
{
    fn feature_case_non_terminated<I, E>(
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
        let feature_case_tag = self
            .lex_feature_case()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            });
        feature_case_tag
    }
}
