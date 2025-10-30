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
    config::{process_config, process_config_not, process_config_switch},
    feature::{process_feature, process_feature_not, process_feature_switch},
    state_machine::StateMachine,
    substitution::process_substitution,
    tree::{ApplicabilityExprKind, ApplicabilityExprTag, ApplicabilityKind, Text},
    updatable::UpdatableValue,
};

use super::{
    base::process_config_group, not::process_config_group_not, switch::process_config_group_switch,
};

pub fn process_config_group_case<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> Result<ApplicabilityExprKind<I>, AstTransformError>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default + Debug,
    ApplicabilityTag<I, String>: From<I>,
{
    let mut tag = ApplicabilityExprTag {
        tag: transformer.process_tags(),
        kind: ApplicabilityKind::ConfigurationGroup,
        contents: vec![],
        start_position: UpdatableValue::new(base_position.0),
        end_position: UpdatableValue::new(base_position.1),
    };
    while !matches!(
        transformer.current_token,
        LexerToken::EndConfigurationGroup(_)
            | LexerToken::ConfigurationGroupCase(_)
            | LexerToken::ConfigurationGroupElse(_)
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
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureNot(position) => {
                let node_to_add = process_feature_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureSwitch(position) => {
                let node_to_add = process_feature_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::FeatureCase(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedFeatureCase(*position));
            }
            LexerToken::FeatureElse(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedFeatureElse(*position));
            }
            LexerToken::FeatureElseIf(position) => {
                return Err(AstTransformError::UnexpectedFeatureElseIf(*position));
            }
            LexerToken::EndFeature(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedEndFeature(*position));
            }
            LexerToken::Configuration(position) => {
                let node_to_add = process_config(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationNot(position) => {
                let node_to_add = process_config_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationSwitch(position) => {
                let node_to_add = process_config_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationCase(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationCase(*position));
            }
            LexerToken::ConfigurationElse(position) => {
                return Err(AstTransformError::UnexpectedConfigurationElse(*position));
            }
            LexerToken::ConfigurationElseIf(position) => {
                return Err(AstTransformError::UnexpectedConfigurationElseIf(*position));
            }
            LexerToken::EndConfiguration(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedEndConfiguration(*position));
            }
            LexerToken::ConfigurationGroup(position) => {
                let node_to_add = process_config_group(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupNot(position) => {
                let node_to_add = process_config_group_not(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                let node_to_add = process_config_group_switch(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
            }
            LexerToken::ConfigurationGroupCase(position) => {
                //throw an error here
                return Err(AstTransformError::UnexpectedConfigurationGroupCase(
                    *position,
                ));
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
            LexerToken::Substitution(position) => {
                let node_to_add = process_substitution(transformer, position);
                match node_to_add {
                    Ok(successful_node) => {
                        tag.add_expr(successful_node);
                    }
                    Err(e) => return Err(e),
                };
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
            Some(LexerToken::ConfigurationGroupCase(_))
        ) {
            transformer.next();
        } else {
            break;
        }
    }
    if let LexerToken::EndConfigurationGroup(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::ConfigurationGroupCase(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::ConfigurationGroupElse(x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    if let LexerToken::Text(_, x) = transformer.current_token {
        tag.set_end_position(x.1);
    }
    Ok(ApplicabilityExprKind::Tag(tag))
}
