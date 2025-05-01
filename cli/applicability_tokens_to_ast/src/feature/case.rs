use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use nom::Input;
use std::fmt::Debug;
use tracing::error;

use crate::{
    latch::LatchedValue,
    state_machine::StateMachine,
    substitution::process_substitution,
    tree::{ApplicabilityExprKind, ApplicabilityExprTag, ApplicabilityKind, Text},
};

use super::{base::process_feature, not::process_feature_not, switch::process_feature_switch};

pub fn process_feature_case<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> ApplicabilityExprKind<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default + Debug,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut tag = ApplicabilityExprTag {
        tag: transformer.process_tags(),
        kind: ApplicabilityKind::Feature,
        contents: vec![],
        start_position: LatchedValue::new(base_position.0),
        end_position: LatchedValue::new(base_position.1),
    };
    while !matches!(
        transformer.current_token,
        LexerToken::EndFeature(_) | LexerToken::FeatureCase(_) | LexerToken::FeatureElse(_)
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
                    start_position: LatchedValue::new(position.0),
                    end_position: LatchedValue::new(position.1),
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
                    "Feature Case found at {:#?} to {:#?} in Feature Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureElse(position) => {
                //throw an error here
                error!(
                    "Feature Else found at {:#?} to {:#?} in Feature Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::FeatureElseIf(position) => {
                error!(
                    "Feature Else If found at {:#?} to {:#?} in Feature Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::EndFeature(position) => {
                //throw an error here
                error!(
                    "End Feature found at {:#?} to {:#?} in Feature Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::Configuration(_) => todo!(),
            LexerToken::ConfigurationNot(_) => todo!(),
            LexerToken::ConfigurationSwitch(_) => todo!(),
            LexerToken::ConfigurationCase(position) => {
                //throw an error here
                error!(
                    "Configuration Case found at {:#?} to {:#?} in Feature Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationElse(_) => todo!(),
            LexerToken::ConfigurationElseIf(_) => todo!(),
            LexerToken::EndConfiguration(position) => {
                //throw an error here
                error!(
                    "End Configuration found at {:#?} to {:#?} in Feature Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroup(_) => todo!(),
            LexerToken::ConfigurationGroupNot(_) => todo!(),
            LexerToken::ConfigurationGroupSwitch(_) => todo!(),
            LexerToken::ConfigurationGroupCase(position) => {
                //throw an error here
                error!(
                    "Configuration Group Case found at {:#?} to {:#?} in Feature Case at {:#?} to {:#?}",
                    position.0, position.1, base_position.0, base_position.1
                );
            }
            LexerToken::ConfigurationGroupElse(_) => todo!(),
            LexerToken::ConfigurationGroupElseIf(_) => todo!(),
            LexerToken::EndConfigurationGroup(position) => {
                //throw an error here
                error!(
                    "End Configuration Group found at {:#?} to {:#?} in Feature Case at {:#?} to {:#?}",
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
        }
        if !matches!(transformer.next_token, Some(LexerToken::FeatureCase(_))) {
            transformer.next();
        } else {
            break;
        }
    }
    if let LexerToken::EndFeature(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::FeatureCase(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::FeatureElse(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::Text(_, x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    ApplicabilityExprKind::Tag(tag)
}
