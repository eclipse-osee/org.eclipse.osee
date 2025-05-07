use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use applicability_parser_types::applic_tokens::{ApplicTokens, ApplicabilityNestedNotAndTag};
use nom::Input;
use tracing::error;

use crate::{
    config::{process_config, process_config_not, process_config_switch},
    config_group::{process_config_group, process_config_group_not, process_config_group_switch},
    updatable::UpdatableValue,
    state_machine::StateMachine,
    substitution::process_substitution,
    tree::{
        ApplicabilityExprContainerWithPosition, ApplicabilityExprKind, ApplicabilityExprTag,
        ApplicabilityKind, Text,
    },
};

use super::{
    applic_else::process_feature_else, base::process_feature, switch::process_feature_switch,
};

pub fn process_feature_not<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> ApplicabilityExprKind<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    let tag = ApplicabilityExprKind::TagNot(ApplicabilityExprTag {
        tag: transformer.process_tags(),
        kind: ApplicabilityKind::Feature,
        contents: vec![],
        start_position: UpdatableValue::new(base_position.0),
        end_position: UpdatableValue::new(base_position.1),
    });
    let mut container = ApplicabilityExprContainerWithPosition {
        contents: vec![tag],
        start_position: UpdatableValue::new(base_position.0),
        end_position: UpdatableValue::new((0, 0)),
    };
    while transformer.next().is_some()
        && !matches!(transformer.current_token, LexerToken::EndFeature(_))
    {
        let current_token = transformer.current_token.clone();
        match &current_token {
            LexerToken::Nothing => {
                //discard
            }
            LexerToken::Illegal => {
                //discard
            }
            LexerToken::Identity => {
                //discard
            }
            LexerToken::Text(content, position) => {
                let text = Text {
                    text: content.to_owned(),
                    start_position: UpdatableValue::new(position.0),
                    end_position: UpdatableValue::new(position.1),
                };
                container.add_text_to_latest_tag(text);
            }
            LexerToken::TextToDiscard(_, _) => {
                //discard
            }
            LexerToken::Feature(position) => {
                let node_to_add = process_feature(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::FeatureNot(position) => {
                let node_to_add = process_feature_not(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::FeatureSwitch(position) => {
                let node_to_add = process_feature_switch(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::FeatureCase(position) => {
                //throw an error here
                error!(
                    "Feature Case found at {:#?} to {:#?} in Feature Not at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureElse(position) => {
                let tokens = container.get_total_tags();
                if !container.contents[0].has_end_position_changed() {
                    container.contents[0].set_end_position(position.0);
                }
                let node_to_add = process_feature_else(
                    transformer,
                    position,
                    vec![ApplicTokens::NestedNotAnd(ApplicabilityNestedNotAndTag(
                        tokens, None,
                    ))],
                );
                container.add_expr(node_to_add);
            }
            LexerToken::FeatureElseIf(position) => {
                let node_to_add = process_feature(transformer, position);
                if !container.contents[0].has_end_position_changed() {
                    container.contents[0].set_end_position(position.0);
                }
                container.add_expr(node_to_add);
            }
            LexerToken::EndFeature(position) => {
                error!(
                    "End Feature found at {:#?} to {:#?} in Feature Not at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Configuration(position) => {
                let node_to_add = process_config(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::ConfigurationNot(position) => {
                let node_to_add = process_config_not(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::ConfigurationSwitch(position) => {
                let node_to_add = process_config_switch(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::ConfigurationCase(position) => {
                //throw an error here
                error!(
                    "Configuration Case found at {:#?} to {:#?} in Feature Not at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationElse(position) => {
                error!(
                    "Configuration Else found at {:#?} to {:#?} in Feature Not at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationElseIf(position) => {
                error!(
                    "Feature Else If found at {:#?} to {:#?} in Feature Not at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::EndConfiguration(position) => {
                //throw an error here
                error!(
                    "End Configuration found at {:#?} to {:#?} in Feature Not at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroup(position) => {
                let node_to_add = process_config_group(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::ConfigurationGroupNot(position) => {
                let node_to_add = process_config_group_not(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                let node_to_add = process_config_group_switch(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::ConfigurationGroupCase(position) => {
                //throw an error here
                error!(
                    "Configuration Group Case found at {:#?} to {:#?} in Feature Not at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupElse(_) => todo!(),
            LexerToken::ConfigurationGroupElseIf(_) => todo!(),
            LexerToken::EndConfigurationGroup(position) => {
                //throw an error here
                error!(
                    "End Configuration found at {:#?} to {:#?} in Feature Not at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Substitution(position) => {
                let node_to_add = process_substitution(transformer, position);
                container.add_expr_to_latest_tag(node_to_add);
            }
            LexerToken::Space(_) => {
                //discard
            }
            LexerToken::CarriageReturn(_) => {
                //discard TODO: remove
            }
            LexerToken::UnixNewLine(_) => {
                //discard TODO: remove
            }
            LexerToken::Tab(_) => {
                //discard
            }
            LexerToken::StartBrace(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::EndBrace(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::StartParen(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::EndParen(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::Not(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::And(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::Or(_) => {
                //discard it's an error if it gets here
            }
            LexerToken::Tag(_, _) => {
                //discard it's an error if it gets here
            }
        }
    }
    if let LexerToken::EndFeature(x) = transformer.current_token {
        if !container.contents[0].has_end_position_changed() {
            container.contents[0].set_end_position(x.1);
        }
        container.end_position.next(x.1);
    }

    ApplicabilityExprKind::TagContainer(container)
}
