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
use crate::{
    state_machine::StateMachine,
    tree::{ApplicabilityExprKind, ApplicabilityExprSubstitution},
    updatable::UpdatableValue,
};
use applicability::applic_tag::ApplicabilityTag;
use applicability_lexer_base::{applicability_structure::LexerToken, position::TokenPosition};
use nom::Input;

pub(crate) fn process_substitution<I, Iter>(
    transformer: &mut StateMachine<I, Iter>,
    base_position: &TokenPosition,
) -> ApplicabilityExprKind<I>
where
    Iter: Iterator<Item = LexerToken<I>>,
    I: Input + Send + Sync + Default,
    ApplicabilityTag<I, String>: From<I>,
{
    let tag = ApplicabilityExprSubstitution {
        tag: transformer.process_tags(),
        start_position: UpdatableValue::new(base_position.0),
        end_position: UpdatableValue::new(transformer.current_token.clone().get_end_position()),
    };
    ApplicabilityExprKind::Substitution(tag)
}
