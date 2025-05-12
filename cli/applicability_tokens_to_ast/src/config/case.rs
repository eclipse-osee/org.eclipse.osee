use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use nom::Input;
use tracing::error;

use crate::{
    config_group::{process_config_group, process_config_group_not, process_config_group_switch},
    feature::{process_feature, process_feature_not, process_feature_switch},
    state_machine::StateMachine,
    substitution::process_substitution,
    tree::{ApplicabilityExprKind, ApplicabilityExprTag, ApplicabilityKind, Text},
    updatable::UpdatableValue,
};

use super::{base::process_config, not::process_config_not, switch::process_config_switch};

pub fn process_config_case<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> ApplicabilityExprKind<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut tag = ApplicabilityExprTag {
        tag: transformer.process_tags(),
        kind: ApplicabilityKind::Configuration,
        contents: vec![],
        start_position: UpdatableValue::new(base_position.0),
        end_position: UpdatableValue::new(base_position.1),
    };
    while !matches!(
        transformer.current_token,
        LexerToken::EndConfiguration(_)
            | LexerToken::ConfigurationCase(_)
            | LexerToken::ConfigurationElse(_)
    ) && transformer.next_token.is_some()
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
                tag.add_text(text);
            }
            LexerToken::TextToDiscard(_, _) => {
                //discard
            }
            LexerToken::Feature(position) => {
                let node_to_add = process_feature(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::FeatureNot(position) => {
                let node_to_add = process_feature_not(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::FeatureSwitch(position) => {
                let node_to_add = process_feature_switch(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::FeatureCase(position) => {
                //throw an error here
                error!(
                    "Feature Case found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureElse(position) => {
                //throw an error here
                error!(
                    "Feature Else found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureElseIf(position) => {
                error!(
                    "Feature Else If found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::EndFeature(position) => {
                //throw an error here
                error!(
                    "End Feature found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Configuration(position) => {
                let node_to_add = process_config(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::ConfigurationNot(position) => {
                let node_to_add = process_config_not(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::ConfigurationSwitch(position) => {
                let node_to_add = process_config_switch(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::ConfigurationCase(position) => {
                //throw an error here
                error!(
                    "Configuration Case found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationElse(position) => {
                error!(
                    "Configuration Else found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationElseIf(position) => {
                error!(
                    "Configuration Else If found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::EndConfiguration(position) => {
                //throw an error here
                error!(
                    "End Configuration found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroup(position) => {
                let node_to_add = process_config_group(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::ConfigurationGroupNot(position) => {
                let node_to_add = process_config_group_not(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                let node_to_add = process_config_group_switch(transformer, position);
                tag.add_expr(node_to_add);
            }
            LexerToken::ConfigurationGroupCase(position) => {
                //throw an error here
                error!(
                    "Configuration Group Case found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupElse(position) => {
                error!(
                    "Configuration Group Else found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupElseIf(position) => {
                error!(
                    "Configuration Group Else If found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::EndConfigurationGroup(position) => {
                //throw an error here
                error!(
                    "End Configuration Group found at {:#?} to {:#?} in Configuration Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Substitution(position) => {
                let node_to_add = process_substitution(transformer, position);
                tag.add_expr(node_to_add);
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
            LexerToken::StartCommentMultiLine(_, _) => todo!(),
            LexerToken::EndCommentMultiLine(_, _) => todo!(),
            LexerToken::SingleLineCommentCharacter(_, _) => todo!(),
        }
        if !matches!(
            transformer.next_token,
            Some(LexerToken::ConfigurationCase(_))
        ) {
            transformer.next();
        } else {
            break;
        }
    }
    if let LexerToken::EndConfiguration(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::ConfigurationCase(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::ConfigurationElse(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::Text(_, x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    ApplicabilityExprKind::Tag(tag)
}
