use applicability_lexer_base::position::Position;
use applicability_parser_types::applic_tokens::ApplicTokens;

pub enum ApplicabilityKind {
    Feature,
    Configuration,
    ConfigurationGroup,
}

pub enum ApplicabilityExprKind<Input> {
    None(ApplicabilityExprContainer<Input>),
    Text(Text<Input>),
    TagContainer(ApplicabilityExprContainerWithPosition<Input>),
    Tag(ApplicabilityExprTag<Input>),
    TagNot(ApplicabilityExprTag<Input>),
    Substitution,
}

pub struct ApplicabilityExprTag<Input> {
    pub tag: Vec<ApplicTokens<Input>>,
    pub kind: ApplicabilityKind,
    pub contents: Vec<ApplicabilityExprKind<Input>>,
    pub start_position: Position,
    pub end_position: Position,
}
pub struct ApplicabilityExprSubstitution<Input> {
    pub tag: Vec<ApplicTokens<Input>>,
    pub start_position: Position,
    pub end_position: Position,
}
pub struct ApplicabilityExprContainer<Input> {
    pub contents: Vec<ApplicabilityExprKind<Input>>,
}
pub struct ApplicabilityExprContainerWithPosition<Input> {
    pub contents: Vec<ApplicabilityExprKind<Input>>,
    pub start_position: Position,
    pub end_position: Position,
}
impl<Input> ApplicabilityExprContainerWithPosition<Input> {
    pub fn add_text_to_latest_tag(&mut self, text: Text<Input>) {
        self.add_expr_to_latest_tag(ApplicabilityExprKind::Text(text));
    }
    pub fn add_expr_to_latest_tag(&mut self, tag_to_insert: ApplicabilityExprKind<Input>) {
        if let Some(idx) = self
            .contents
            .iter()
            .position(|expr| matches!(expr, ApplicabilityExprKind::Tag(_)))
        {
            let mut tag = self.contents.remove(idx);
            match tag {
                ApplicabilityExprKind::Tag(ref mut tag_result)
                | ApplicabilityExprKind::TagNot(ref mut tag_result) => {
                    tag_result.contents.push(tag_to_insert);
                }
                _ => {}
            }
            self.contents.push(tag);
        }
    }
}
pub struct Text<I> {
    pub text: I,
    pub start_position: Position,
    pub end_position: Position,
}
