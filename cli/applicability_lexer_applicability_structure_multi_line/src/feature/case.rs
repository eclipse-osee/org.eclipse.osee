use nom::{AsChar, Compare, FindSubstring, Input, Parser, error::ParseError, multi::many0};

use crate::utils::tag_multi_line::TagMultiLine;
use applicability_lexer_applicability_structure_base::{
    delimiters::{space::LexSpace, tab::LexTab},
    feature::case::LexFeatureCase,
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait FeatureCaseMultiLine {
    fn feature_case_multi_line<I, E>(
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

impl<T> FeatureCaseMultiLine for T
where
    T: TagMultiLine + LexFeatureCase + LexSpace + LexTab,
{
    fn feature_case_multi_line<I, E>(
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
        let tag = self.multi_line_tag();
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
