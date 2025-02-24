use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::feature::{applic_else::FeatureElse, end::FeatureEnd, switch::FeatureSwitch},
    second_stage::token::LexerToken,
};

use super::{
    base::FeatureBaseSingleLineTerminated, case::FeatureCaseSingleLineTerminated,
    else_if::FeatureElseIfSingleLineTerminated, not::FeatureNotSingleLineTerminated,
};

pub trait FeatureTagSingleLineTerminated {
    fn feature_tag_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> FeatureTagSingleLineTerminated for T
where
    T: FeatureBaseSingleLineTerminated
        + FeatureNotSingleLineTerminated
        + FeatureCaseSingleLineTerminated
        + FeatureElseIfSingleLineTerminated
        + FeatureElse
        + FeatureEnd
        + FeatureSwitch,
{
    fn feature_tag_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
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
            .or(self.feature_else().map(|_| vec![LexerToken::FeatureElse]))
            .or(self.feature_end().map(|_| vec![LexerToken::EndFeature]))
            .or(self
                .feature_switch()
                .map(|_| vec![LexerToken::FeatureSwitch]))
            .or(feature_base_tag);
        feature_tag
    }
}
