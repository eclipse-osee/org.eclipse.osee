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

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    comment::{multi_line::EndCommentMultiLine, single_line::StartCommentSingleLineNonTerminated},
    config::{
        applic_else::ConfigurationElse, base::ConfigurationBase, case::ConfigurationCase,
        else_if::ConfigurationElseIf, end::ConfigurationEnd, not::ConfigurationNot,
        switch::ConfigurationSwitch,
    },
    config_group::{
        applic_else::ConfigurationGroupElse, base::ConfigurationGroupBase,
        case::ConfigurationGroupCase, else_if::ConfigurationGroupElseIf,
        end::ConfigurationGroupEnd, not::ConfigurationGroupNot, switch::ConfigurationGroupSwitch,
    },
    feature::{
        applic_else::FeatureElse, base::FeatureBase, case::FeatureCase, else_if::FeatureElseIf,
        end::FeatureEnd, not::FeatureNot, switch::FeatureSwitch,
    },
    position::Position,
    substitution::Substitution,
    utils::{
        locatable::{Locatable, position},
        take_first::take_until_first84,
    },
};

pub trait LooseTextMultiLine {
    fn loose_text_multi_line<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
impl<T> LooseTextMultiLine for T
where
    T: FeatureBase
        + FeatureNot
        + FeatureCase
        + FeatureElse
        + FeatureElseIf
        + FeatureSwitch
        + FeatureEnd
        + ConfigurationBase
        + ConfigurationNot
        + ConfigurationCase
        + ConfigurationElse
        + ConfigurationElseIf
        + ConfigurationSwitch
        + ConfigurationEnd
        + ConfigurationGroupBase
        + ConfigurationGroupNot
        + ConfigurationGroupCase
        + ConfigurationGroupElse
        + ConfigurationGroupElseIf
        + ConfigurationGroupSwitch
        + ConfigurationGroupEnd
        + EndCommentMultiLine
        + Substitution
        + StartCommentSingleLineNonTerminated,
{
    fn loose_text_multi_line<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
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
        position()
            .and(take_until_first84(
                self.feature_base_tag_nl(),
                self.feature_not_tag_nl(),
                self.feature_case_tag_nl(),
                self.feature_else_if_tag_nl(),
                self.feature_else_tag_nl(),
                self.feature_end_tag_nl(),
                self.feature_base_tag_cr(),
                self.feature_not_tag_cr(),
                self.feature_case_tag_cr(),
                self.feature_else_if_tag_cr(),
                self.feature_else_tag_cr(),
                self.feature_end_tag_cr(),
                self.feature_base_tag_t(),
                self.feature_not_tag_t(),
                self.feature_case_tag_t(),
                self.feature_else_if_tag_t(),
                self.feature_else_tag_t(),
                self.feature_end_tag_t(),
                self.feature_base_tag_s(),
                self.feature_not_tag_s(),
                self.feature_case_tag_s(),
                self.feature_else_if_tag_s(),
                self.feature_else_tag_s(),
                self.feature_end_tag_s(),
                self.feature_switch_tag(),
                self.config_base_tag_nl(),
                self.config_not_tag_nl(),
                self.config_case_tag_nl(),
                self.config_else_if_tag_nl(),
                self.config_else_tag_nl(),
                self.config_switch_tag_nl(),
                self.config_end_tag_nl(),
                self.config_base_tag_cr(),
                self.config_not_tag_cr(),
                self.config_case_tag_cr(),
                self.config_else_if_tag_cr(),
                self.config_switch_tag_cr(),
                self.config_end_tag_cr(),
                self.config_else_tag_cr(),
                self.config_base_tag_t(),
                self.config_not_tag_t(),
                self.config_case_tag_t(),
                self.config_else_if_tag_t(),
                self.config_switch_tag_t(),
                self.config_else_tag_t(),
                self.config_end_tag_t(),
                self.config_base_tag_s(),
                self.config_not_tag_s(),
                self.config_case_tag_s(),
                self.config_else_if_tag_s(),
                self.config_else_tag_s(),
                self.config_switch_tag_s(),
                self.config_end_tag_s(),
                self.config_group_base_tag_nl(),
                self.config_group_not_tag_nl(),
                self.config_group_case_tag_nl(),
                self.config_group_else_if_tag_nl(),
                self.config_group_else_tag_nl(),
                self.config_group_switch_tag_nl(),
                self.config_group_end_tag_nl(),
                self.config_group_base_tag_cr(),
                self.config_group_not_tag_cr(),
                self.config_group_case_tag_cr(),
                self.config_group_else_if_tag_cr(),
                self.config_group_else_tag_cr(),
                self.config_group_switch_tag_cr(),
                self.config_group_end_tag_cr(),
                self.config_group_base_tag_t(),
                self.config_group_not_tag_t(),
                self.config_group_case_tag_t(),
                self.config_group_else_if_tag_t(),
                self.config_group_else_tag_t(),
                self.config_group_switch_tag_t(),
                self.config_group_end_tag_t(),
                self.config_group_base_tag_s(),
                self.config_group_not_tag_s(),
                self.config_group_case_tag_s(),
                self.config_group_else_if_tag_s(),
                self.config_group_else_tag_s(),
                self.config_group_switch_tag_s(),
                self.config_group_end_tag_s(),
                self.substitution_tag(),
                self.end_comment_multi_line_tag(),
                self.start_comment_single_line_non_terminated_tag(),
            ))
            .and(position())
            .map(|((start, x), end): ((Position, I), Position)| {
                vec![LexerToken::TextToDiscard(x, (start, end))]
            })
    }
}
