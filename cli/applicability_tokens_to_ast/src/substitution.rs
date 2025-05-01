use crate::{
    latch::LatchedValue,
    state_machine::StateMachine,
    tree::{ApplicabilityExprKind, ApplicabilityExprSubstitution},
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
        start_position: LatchedValue::new(base_position.0),
        end_position: LatchedValue::new(transformer.current_token.clone().get_end_position()),
    };
    ApplicabilityExprKind::Substitution(tag)
}
