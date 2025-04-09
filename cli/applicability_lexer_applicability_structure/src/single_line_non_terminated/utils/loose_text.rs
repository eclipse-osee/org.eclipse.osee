use nom::{error::ParseError, AsChar, Compare, FindSubstring, Input, Parser};


use applicability_lexer_base::{
    applicability_structure::LexerToken, config::{
        applic_else::ConfigurationElse, base::ConfigurationBase, case::ConfigurationCase,
        else_if::ConfigurationElseIf, end::ConfigurationEnd, not::ConfigurationNot,
        switch::ConfigurationSwitch,
    }, config_group::{
        applic_else::ConfigurationGroupElse, base::ConfigurationGroupBase,
        case::ConfigurationGroupCase, else_if::ConfigurationGroupElseIf,
        end::ConfigurationGroupEnd, not::ConfigurationGroupNot, switch::ConfigurationGroupSwitch,
    }, feature::{
        applic_else::FeatureElse, base::FeatureBase, case::FeatureCase, else_if::FeatureElseIf,
        end::FeatureEnd, not::FeatureNot, switch::FeatureSwitch,
    }, line_terminations::{carriage_return::CarriageReturn, new_line::NewLine}, substitution::Substitution, utils::{
        locatable::{position, Locatable},
        take_first::take_until_first24,
    }
};

pub trait LooseTextNonTerminated {
    fn loose_text_non_terminated<I, E>(
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
impl<T> LooseTextNonTerminated for T
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
        + Substitution
        + CarriageReturn
        + NewLine,
{
    fn loose_text_non_terminated<I, E>(
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
                self.carriage_return_tag(),
                self.new_line_tag(),
            ))
            .and(position())
            .map(|((start, x), end): (((usize, u32), I), (usize, u32))| {
                vec![LexerToken::Text(x.into(), start, end)]
            })
    }
}
