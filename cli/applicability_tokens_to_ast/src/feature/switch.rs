use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use nom::Input;
use std::fmt::Debug;
use tracing::error;

use crate::{
    latch::LatchedValue,
    state_machine::StateMachine,
    tree::{ApplicabilityExprContainerWithPosition, ApplicabilityExprKind},
};

use super::case::process_feature_case;

pub(crate) fn process_feature_switch<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> ApplicabilityExprKind<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default + Debug,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut container = ApplicabilityExprContainerWithPosition {
        contents: vec![],
        start_position: LatchedValue::new(base_position.0),
        end_position: LatchedValue::new((0, 0)),
    };
    while transformer.next().is_some()
        && !matches!(transformer.current_token, LexerToken::EndFeature(_))
    {
        let current_token = transformer.current_token.clone();
        println!("switch {:#?}", current_token);
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
            LexerToken::Text(_, _) => {
                //discard
            }
            LexerToken::TextToDiscard(_, _) => {
                //discard
            }
            LexerToken::Feature(position) => {
                //throw an error here
                error!(
                    "Feature found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureNot(position) => {
                //throw an error here
                error!(
                    "Feature Not found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureSwitch(position) => {
                //throw an error here
                error!(
                    "Feature Switch found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureCase(position) => {
                let node_to_add = process_feature_case(transformer, position);
                container.contents.push(node_to_add);
                if matches!(transformer.current_token, LexerToken::EndFeature(_)) {
                    //if we ended the case with an end feature, we shouldn't increment, and we should just return up a level, as the level above will increment
                    break;
                }
            }
            LexerToken::FeatureElse(position) => {
                //throw an error here
                error!(
                    "Feature Else found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureElseIf(position) => {
                //throw an error here
                error!(
                    "Feature Else If found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::EndFeature(position) => {
                //throw an error here
                error!(
                    "End Feature found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Configuration(position) => {
                //throw an error here
                error!(
                    "Configuration found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationNot(position) => {
                //throw an error here
                error!(
                    "Configuration Not found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationSwitch(position) => {
                //throw an error here
                error!(
                    "Configuration Switch found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationCase(position) => {
                //throw an error here
                error!(
                    "Configuration Case found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationElse(position) => {
                //throw an error here
                error!(
                    "Configuration Else found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationElseIf(position) => {
                //throw an error here
                error!(
                    "Configuration Else If found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::EndConfiguration(position) => {
                //throw an error here
                error!(
                    "End Configuration found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroup(position) => {
                //throw an error here
                error!(
                    "Configuration Group found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupNot(position) => {
                //throw an error here
                error!(
                    "Configuration Group Not found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                //throw an error here
                error!(
                    "Configuration Group Switch found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupCase(position) => {
                //throw an error here
                error!(
                    "Configuration Group Case found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupElse(position) => {
                //throw an error here
                error!(
                    "Configuration Group Else found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupElseIf(position) => {
                //throw an error here
                error!(
                    "Configuration Group Else If found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::EndConfigurationGroup(position) => {
                //throw an error here
                error!(
                    "End Configuration Group found at {:#?} to {:#?} in Feature Switch at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Substitution(_) => todo!(),
            LexerToken::Space(_) => todo!(),
            LexerToken::CarriageReturn(_) => todo!(),
            LexerToken::UnixNewLine(_) => todo!(),
            LexerToken::Tab(_) => todo!(),
            LexerToken::StartBrace(_) => todo!(),
            LexerToken::EndBrace(_) => todo!(),
            LexerToken::StartParen(_) => todo!(),
            LexerToken::EndParen(_) => todo!(),
            LexerToken::Not(_) => todo!(),
            LexerToken::And(_) => todo!(),
            LexerToken::Or(_) => todo!(),
            LexerToken::Tag(_, _) => todo!(),
        }
    }
    if let LexerToken::EndFeature(x) = transformer.current_token {
        container.end_position.next(x.1);
    }

    ApplicabilityExprKind::TagContainer(container)
}
