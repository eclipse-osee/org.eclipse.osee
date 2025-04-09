use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::applicability_structure::{
    base::{
        delimiters::{space::LexSpace, tab::LexTab},
        feature::case::LexFeatureCase,
    },
    multi_line::utils::tag_multi_line::TagMultiLine,
    token::LexerToken,
};
use applicability_lexer_base::utils::locatable::Locatable;

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
        //TODO: verify many0 works instead of many_till
        let tag = self.multi_line_tag();
        let feature_case_tag = self
            .lex_feature_case()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        feature_case_tag
    }
}
