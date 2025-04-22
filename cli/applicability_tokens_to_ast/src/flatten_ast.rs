use applicability_lexer_base::position::Position;
use applicability_parser_types::applic_tokens::ApplicTokens;

pub trait HasContents<I> {
    fn push(&mut self, value: FlattenApplicabilityAst<I>);
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum FlattenApplicabilityAst<I> {
    #[default]
    NoToken,
    Head(HeadNode<I>),
    MultiLineComment(CommentNode<I>),
    NonTerminatedComment(CommentNode<I>),
    TerminatedComment(CommentNode<I>),
    Text(TextNode<I>),
    Feature(ApplicabilityNode<I>),
    FeatureSwitch(PositionNode),
    FeatureNot(ApplicabilityNode<I>),
    FeatureCase(ApplicabilityNode<I>),
    FeatureElse(PositionNode),
    FeatureElseIf(ApplicabilityNode<I>),
    EndFeature(PositionNode),
    Configuration(ApplicabilityNode<I>),
    ConfigurationSwitch(PositionNode),
    ConfigurationNot(ApplicabilityNode<I>),
    ConfigurationCase(ApplicabilityNode<I>),
    ConfigurationElse(PositionNode),
    ConfigurationElseIf(ApplicabilityNode<I>),
    EndConfiguration(PositionNode),
    ConfigurationGroup(ApplicabilityNode<I>),
    ConfigurationGroupSwitch(PositionNode),
    ConfigurationGroupNot(ApplicabilityNode<I>),
    ConfigurationGroupCase(ApplicabilityNode<I>),
    ConfigurationGroupElse(PositionNode),
    ConfigurationGroupElseIf(ApplicabilityNode<I>),
    EndConfigurationGroup(PositionNode),
    Applicability(ApplicabilityNode<I>),
    Substitution(SubstitutionNode<I>),
}

impl<I> FlattenApplicabilityAst<I> {
    pub fn set_end_position(&mut self, position: Position) {
        match self {
            FlattenApplicabilityAst::NoToken => {}
            FlattenApplicabilityAst::Head(head_node) => {}
            FlattenApplicabilityAst::TerminatedComment(comment_node)
            | FlattenApplicabilityAst::NonTerminatedComment(comment_node)
            | FlattenApplicabilityAst::MultiLineComment(comment_node) => {
                comment_node.set_end_position(position);
            }
            FlattenApplicabilityAst::Text(text_node) => {}
            FlattenApplicabilityAst::Feature(applicability_node)
            | FlattenApplicabilityAst::FeatureNot(applicability_node)
            | FlattenApplicabilityAst::FeatureCase(applicability_node)
            | FlattenApplicabilityAst::FeatureElseIf(applicability_node)
            | FlattenApplicabilityAst::Configuration(applicability_node)
            | FlattenApplicabilityAst::ConfigurationNot(applicability_node)
            | FlattenApplicabilityAst::ConfigurationCase(applicability_node)
            | FlattenApplicabilityAst::ConfigurationElseIf(applicability_node)
            | FlattenApplicabilityAst::ConfigurationGroup(applicability_node)
            | FlattenApplicabilityAst::ConfigurationGroupNot(applicability_node)
            | FlattenApplicabilityAst::ConfigurationGroupCase(applicability_node)
            | FlattenApplicabilityAst::ConfigurationGroupElseIf(applicability_node) => {
                applicability_node.set_end_position(position)
            }
            FlattenApplicabilityAst::FeatureSwitch(position_node)
            | FlattenApplicabilityAst::FeatureElse(position_node)
            | FlattenApplicabilityAst::EndFeature(position_node)
            | FlattenApplicabilityAst::ConfigurationSwitch(position_node)
            | FlattenApplicabilityAst::ConfigurationElse(position_node)
            | FlattenApplicabilityAst::EndConfiguration(position_node)
            | FlattenApplicabilityAst::ConfigurationGroupSwitch(position_node)
            | FlattenApplicabilityAst::ConfigurationGroupElse(position_node)
            | FlattenApplicabilityAst::EndConfigurationGroup(position_node) => {
                position_node.set_end_position(position);
            }
            FlattenApplicabilityAst::Applicability(applicability_node) => {
                applicability_node.set_end_position(position)
            }
            FlattenApplicabilityAst::Substitution(substitution_node) => {
                substitution_node.set_end_position(position)
            }
        }
    }
}
#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct HeadNode<I> {
    pub contents: Vec<FlattenApplicabilityAst<I>>,
}

impl<I> HasContents<I> for HeadNode<I> {
    fn push(&mut self, value: FlattenApplicabilityAst<I>) {
        self.contents.push(value);
    }
}

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub struct TextNode<I> {
    pub start_position: Position,
    pub end_position: Position,
    pub content: I,
}
impl<I> TextNode<I> {
    pub fn new(position: Position, content: I) -> Self {
        TextNode {
            start_position: position,
            end_position: (0, 0),
            content,
        }
    }
    pub fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct CommentNode<I> {
    pub start_position: Position,
    pub end_position: Position,
    pub contents: Vec<FlattenApplicabilityAst<I>>,
}
impl<I> CommentNode<I> {
    pub fn new(position: Position) -> Self {
        CommentNode {
            start_position: position,
            end_position: (0, 0),
            contents: vec![],
        }
    }
    pub fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}
impl<I> HasContents<I> for CommentNode<I> {
    fn push(&mut self, value: FlattenApplicabilityAst<I>) {
        self.contents.push(value);
    }
}

impl<I> Default for CommentNode<I> {
    fn default() -> Self {
        Self::new((0, 0))
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct ApplicabilityNode<I> {
    pub start_position: Position,
    pub end_position: Position,
    pub tag: Vec<ApplicTokens<I>>,
}
impl<I> ApplicabilityNode<I> {
    pub fn new(position: Position) -> Self {
        ApplicabilityNode {
            start_position: position,
            end_position: (0, 0),
            tag: vec![],
        }
    }
    pub fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}

impl<I> Default for ApplicabilityNode<I> {
    fn default() -> Self {
        Self::new((0, 0))
    }
}
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct SubstitutionNode<I> {
    pub start_position: Position,
    pub end_position: Position,
    pub tag: Vec<ApplicTokens<I>>,
}

impl<I> SubstitutionNode<I> {
    pub fn new(position: Position) -> Self {
        SubstitutionNode {
            start_position: position,
            end_position: (0, 0),
            tag: vec![],
        }
    }
}
impl<I> SubstitutionNode<I> {
    pub fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}
impl<I> Default for SubstitutionNode<I>
where
    I: Default,
{
    fn default() -> Self {
        Self::new((0, 0))
    }
}

#[derive(Debug, Clone, PartialEq, Eq)]
pub struct PositionNode {
    pub start_position: Position,
    pub end_position: Position,
}

impl PositionNode {
    pub fn new(position: Position) -> Self {
        PositionNode {
            start_position: position,
            end_position: (0, 0),
        }
    }
    pub fn set_end_position(&mut self, position: Position) {
        self.end_position = position;
    }
}
