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
use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use applicability_parser_errors::AstTransformError;
use nom::Input;
use std::fmt::Debug;

use crate::{
    state_machine::StateMachine,
    tree::{ApplicabilityExprContainerWithPosition, ApplicabilityExprKind},
    updatable::UpdatableValue,
};

use super::case::process_config_group_case;

pub(crate) fn process_config_group_switch<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> Result<ApplicabilityExprKind<I>, AstTransformError>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default + Debug,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut container = ApplicabilityExprContainerWithPosition {
        contents: vec![],
        start_position: UpdatableValue::new(base_position.0),
        end_position: UpdatableValue::new((0, 0, 0)),
    };
    while transformer.next().is_some()
        && !matches!(
            transformer.current_token,
            LexerToken::EndConfigurationGroup(_)
        )
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
            LexerToken::Text(_, _) => {
                //discard
            }
            LexerToken::TextToDiscard(_, _) => {
                //discard
            }
            LexerToken::Feature(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedFeature(*position));
            }
            LexerToken::FeatureNot(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedFeatureNot(*position));
            }
            LexerToken::FeatureSwitch(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroupSwitch(
                    *position,
                ));
            }
            LexerToken::FeatureCase(position) => {
                return Err(AstTransformError::UnexpectedFeatureCase(*position));
            }
            LexerToken::FeatureElse(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedFeatureElse(*position));
            }
            LexerToken::FeatureElseIf(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedFeatureElseIf(*position));
            }
            LexerToken::EndFeature(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedEndFeature(*position));
            }
            LexerToken::Configuration(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfiguration(*position));
            }
            LexerToken::ConfigurationNot(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationNot(*position));
            }
            LexerToken::ConfigurationSwitch(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroupSwitch(
                    *position,
                ));
            }
            LexerToken::ConfigurationCase(position) => {
                return Err(AstTransformError::UnexpectedConfigurationCase(*position));
            }
            LexerToken::ConfigurationElse(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationElse(*position));
            }
            LexerToken::ConfigurationElseIf(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationElseIf(*position));
            }
            LexerToken::EndConfiguration(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedEndConfiguration(*position));
            }
            LexerToken::ConfigurationGroup(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroup(*position));
            }
            LexerToken::ConfigurationGroupNot(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroupNot(
                    *position,
                ));
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroupSwitch(
                    *position,
                ));
            }
            LexerToken::ConfigurationGroupCase(position) => {
                let node_to_add = process_config_group_case(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        container.contents.push(successful_node);
                    }
                    Err(e) => return Err(e),
                };
                if matches!(
                    transformer.current_token,
                    LexerToken::EndConfigurationGroup(_)
                ) {
                    //if we ended the case with an end configuration group, we shouldn't increment, and we should just return up a level, as the level above will increment
                    break;
                }
            }
            LexerToken::ConfigurationGroupElse(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroupElse(
                    *position,
                ));
            }
            LexerToken::ConfigurationGroupElseIf(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroupElseIf(
                    *position,
                ));
            }
            LexerToken::EndConfigurationGroup(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedEndConfigurationGroup(
                    *position,
                ));
            }
            LexerToken::Substitution(_) => {}
            LexerToken::Space(_) => {}
            LexerToken::CarriageReturn(_) => {}
            LexerToken::UnixNewLine(_) => {}
            LexerToken::Tab(_) => {}
            LexerToken::StartBrace(_) => {}
            LexerToken::EndBrace(_) => {}
            LexerToken::StartParen(_) => {}
            LexerToken::EndParen(_) => {}
            LexerToken::Not(_) => {}
            LexerToken::And(_) => {}
            LexerToken::Or(_) => {}
            LexerToken::Tag(_, _) => {}
            LexerToken::StartCommentMultiLine(_, _) => todo!(),
            LexerToken::EndCommentMultiLine(_, _) => todo!(),
            LexerToken::SingleLineCommentCharacter(_, _) => todo!(),
        }
    }
    if let LexerToken::EndConfigurationGroup(x) = transformer.current_token {
        container.end_position.next(x.1);
    }

    Ok(ApplicabilityExprKind::TagContainer(container))
}
