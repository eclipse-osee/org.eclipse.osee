use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::utils::locatable::Locatable,
    second_stage::{
        base::feature::{
            applic_else::LexFeatureElse, end::LexFeatureEnd, switch::LexFeatureSwitch,
        },
        token::LexerToken,
    },
};

use super::{
    base::FeatureBaseSingleLineTerminated, case::FeatureCaseSingleLineTerminated,
    else_if::FeatureElseIfSingleLineTerminated, not::FeatureNotSingleLineTerminated,
};

pub trait FeatureTagSingleLineTerminated {
    fn feature_tag_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> FeatureTagSingleLineTerminated for T
where
    T: FeatureBaseSingleLineTerminated
        + FeatureNotSingleLineTerminated
        + FeatureCaseSingleLineTerminated
        + FeatureElseIfSingleLineTerminated
        + LexFeatureElse
        + LexFeatureEnd
        + LexFeatureSwitch,
{
    fn feature_tag_terminated<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str> + Locatable+ Send+ Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let feature_base_tag = self.get_feature_base_terminated();
        let feature_not_tag = self.feature_not_terminated();
        let feature_case_tag = self.feature_case_terminated();
        let feature_else_if_tag = self.feature_else_if_terminated();
        let feature_tag = feature_not_tag
            .or(feature_case_tag)
            .or(feature_else_if_tag)
            .or(self.lex_feature_else().map(|x| vec![x]))
            .or(self.lex_feature_end().map(|x| vec![x]))
            .or(self.lex_feature_switch().map(|x| vec![x]))
            .or(feature_base_tag);
        feature_tag
    }
}
