/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
use nom::{AsChar, Compare, FindSubstring, Input, Parser, error::ParseError};

use applicability_lexer_applicability_structure_base::feature::{
    applic_else::LexFeatureElse, end::LexFeatureEnd, switch::LexFeatureSwitch,
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

use super::{
    base::FeatureBaseMultiLine, case::FeatureCaseMultiLine, else_if::FeatureElseIfMultiLine,
    not::FeatureNotMultiLine,
};

pub trait FeatureTagMultiLine {
    fn feature_tag_start<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn feature_tag_multi_line<I, E>(
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

impl<T> FeatureTagMultiLine for T
where
    T: FeatureBaseMultiLine
        + FeatureNotMultiLine
        + FeatureCaseMultiLine
        + FeatureElseIfMultiLine
        + LexFeatureElse
        + LexFeatureEnd
        + LexFeatureSwitch,
{
    fn feature_tag_start<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
        let feature_base_tag = self.get_feature_base();
        let feature_not_tag = self.feature_not();
        let feature_case_tag = self.feature_case();
        let feature_else_if_tag = self.feature_else_if();

        feature_not_tag
            .or(feature_case_tag)
            .or(feature_else_if_tag)
            .or(self.lex_feature_else().map(|x| vec![x]))
            .or(self.lex_feature_end().map(|x| vec![x]))
            .or(self.lex_feature_switch().map(|x| vec![x]))
            .or(feature_base_tag)
    }
    fn feature_tag_multi_line<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
        let feature_base_tag = self.get_feature_base_multi_line();
        let feature_not_tag = self.feature_not_multi_line();
        let feature_case_tag = self.feature_case_multi_line();
        let feature_else_if_tag = self.feature_else_if_multi_line();

        feature_not_tag
            .or(feature_case_tag)
            .or(feature_else_if_tag)
            .or(self.lex_feature_else_multi_line().map(|x| vec![x]))
            .or(self.lex_feature_end_multi_line().map(|x| vec![x]))
            .or(self.lex_feature_switch_multi_line().map(|x| vec![x]))
            .or(feature_base_tag)
    }
}
