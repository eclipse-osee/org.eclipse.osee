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
        take_first::take_until_first24,
    },
};

use crate::single_line_non_terminated::non_terminated::SingleLineNonTerminated;

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
            .and(take_until_first24(
                self.feature_base_tag(),
                self.feature_not_tag(),
                self.feature_case_tag(),
                self.feature_else_tag(),
                self.feature_else_if_tag(),
                self.feature_switch_tag(),
                self.feature_end_tag(),
                self.config_base_tag(),
                self.config_not_tag(),
                self.config_case_tag(),
                self.config_else_tag(),
                self.config_else_if_tag(),
                self.config_switch_tag(),
                self.config_end_tag(),
                self.config_group_base_tag(),
                self.config_group_not_tag(),
                self.config_group_case_tag(),
                self.config_group_else_tag(),
                self.config_group_else_if_tag(),
                self.config_group_switch_tag(),
                self.config_group_end_tag(),
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
