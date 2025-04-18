use nom::{AsBytes, Input, Offset};
use nom_locate::LocatedSpan;

use crate::position::TokenPosition;

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum LexerToken<I: Input + Send + Sync> {
    #[default]
    Nothing,
    Illegal,
    Identity,
    Text(I, TokenPosition),
    // Eof, This path should be illegal now
    StartCommentSingleLineTerminated(TokenPosition),
    StartCommentMultiLine(TokenPosition),
    SingleLineCommentCharacter(TokenPosition),
    EndCommentSingleLineTerminated(TokenPosition),
    EndCommentMultiLine(TokenPosition),
    // TODO: I don't think we need this
    // MultilineCommentCharacter(TokenPosition),
    Feature(TokenPosition),
    FeatureNot(TokenPosition),
    FeatureSwitch(TokenPosition),
    FeatureCase(TokenPosition),
    FeatureElse(TokenPosition),
    FeatureElseIf(TokenPosition),
    EndFeature(TokenPosition),
    Configuration(TokenPosition),
    ConfigurationNot(TokenPosition),
    ConfigurationSwitch(TokenPosition),
    ConfigurationCase(TokenPosition),
    ConfigurationElse(TokenPosition),
    ConfigurationElseIf(TokenPosition),
    EndConfiguration(TokenPosition),
    ConfigurationGroup(TokenPosition),
    ConfigurationGroupNot(TokenPosition),
    ConfigurationGroupSwitch(TokenPosition),
    ConfigurationGroupCase(TokenPosition),
    ConfigurationGroupElse(TokenPosition),
    ConfigurationGroupElseIf(TokenPosition),
    EndConfigurationGroup(TokenPosition),
    Substitution(TokenPosition),
    Space(TokenPosition),
    CarriageReturn(TokenPosition),
    UnixNewLine(TokenPosition),
    Tab(TokenPosition),
    //the following should only be tokenized following one of the Feature|Configuration|ConfigurationGroup Base|Not|Switch|Case
    StartBrace(TokenPosition),
    EndBrace(TokenPosition),
    // the following should only be tokenized following a StartBrace and preceding an EndBrace
    StartParen(TokenPosition),
    EndParen(TokenPosition),
    Not(TokenPosition),
    And(TokenPosition),
    Or(TokenPosition),
    Tag(I, TokenPosition),
}

impl<I: Input + Send + Sync + Default + AsBytes + Offset, X: Clone + Send + Sync>
    From<LexerToken<LocatedSpan<I, X>>> for LexerToken<I>
{
    fn from(value: LexerToken<LocatedSpan<I, X>>) -> Self {
        match value {
            LexerToken::Nothing => LexerToken::<I>::Nothing,
            LexerToken::Illegal => LexerToken::<I>::Illegal,
            LexerToken::Identity => LexerToken::<I>::Identity,
            LexerToken::Text(contents, position) => {
                LexerToken::<I>::Text(contents.into_fragment(), position)
            }
            LexerToken::StartCommentSingleLineTerminated(position) => {
                LexerToken::<I>::StartCommentSingleLineTerminated(position)
            }
            LexerToken::StartCommentMultiLine(position) => {
                LexerToken::<I>::StartCommentMultiLine(position)
            }
            LexerToken::SingleLineCommentCharacter(position) => {
                LexerToken::<I>::SingleLineCommentCharacter(position)
            }
            LexerToken::EndCommentSingleLineTerminated(position) => {
                LexerToken::<I>::EndCommentSingleLineTerminated(position)
            }
            LexerToken::EndCommentMultiLine(position) => {
                LexerToken::<I>::EndCommentMultiLine(position)
            }
            LexerToken::Feature(position) => LexerToken::<I>::Feature(position),
            LexerToken::FeatureNot(position) => LexerToken::<I>::FeatureNot(position),
            LexerToken::FeatureSwitch(position) => LexerToken::<I>::FeatureSwitch(position),
            LexerToken::FeatureCase(position) => LexerToken::<I>::FeatureCase(position),
            LexerToken::FeatureElse(position) => LexerToken::<I>::FeatureElse(position),
            LexerToken::FeatureElseIf(position) => LexerToken::<I>::FeatureElseIf(position),
            LexerToken::EndFeature(position) => LexerToken::<I>::EndFeature(position),
            LexerToken::Configuration(position) => LexerToken::<I>::Configuration(position),
            LexerToken::ConfigurationNot(position) => LexerToken::<I>::ConfigurationNot(position),
            LexerToken::ConfigurationSwitch(position) => {
                LexerToken::<I>::ConfigurationSwitch(position)
            }
            LexerToken::ConfigurationCase(position) => LexerToken::<I>::ConfigurationCase(position),
            LexerToken::ConfigurationElse(position) => LexerToken::<I>::ConfigurationElse(position),
            LexerToken::ConfigurationElseIf(position) => {
                LexerToken::<I>::ConfigurationElseIf(position)
            }
            LexerToken::EndConfiguration(position) => LexerToken::<I>::EndConfiguration(position),
            LexerToken::ConfigurationGroup(position) => {
                LexerToken::<I>::ConfigurationGroup(position)
            }
            LexerToken::ConfigurationGroupNot(position) => {
                LexerToken::<I>::ConfigurationGroupNot(position)
            }
            LexerToken::ConfigurationGroupSwitch(position) => {
                LexerToken::<I>::ConfigurationGroupSwitch(position)
            }
            LexerToken::ConfigurationGroupCase(position) => {
                LexerToken::<I>::ConfigurationGroupCase(position)
            }
            LexerToken::ConfigurationGroupElse(position) => {
                LexerToken::<I>::ConfigurationGroupElse(position)
            }
            LexerToken::ConfigurationGroupElseIf(position) => {
                LexerToken::<I>::ConfigurationGroupElseIf(position)
            }
            LexerToken::EndConfigurationGroup(position) => {
                LexerToken::<I>::EndConfigurationGroup(position)
            }
            LexerToken::Substitution(position) => LexerToken::Substitution(position),
            LexerToken::Space(position) => LexerToken::Space(position),
            LexerToken::CarriageReturn(position) => LexerToken::CarriageReturn(position),
            LexerToken::UnixNewLine(position) => LexerToken::UnixNewLine(position),
            LexerToken::Tab(position) => LexerToken::Tab(position),
            LexerToken::StartBrace(position) => LexerToken::StartBrace(position),
            LexerToken::EndBrace(position) => LexerToken::EndBrace(position),
            LexerToken::StartParen(position) => LexerToken::StartParen(position),
            LexerToken::EndParen(position) => LexerToken::EndParen(position),
            LexerToken::Not(position) => LexerToken::Not(position),
            LexerToken::And(position) => LexerToken::And(position),
            LexerToken::Or(position) => LexerToken::Or(position),
            LexerToken::Tag(contents, position) => {
                LexerToken::Tag(contents.into_fragment(), position)
            }
        }
    }
}

impl<I: Input + Send + Sync> LexerToken<I> {
    pub fn increment_offset(self, offset: usize) -> Self {
        match self {
            LexerToken::Nothing => self,
            LexerToken::Illegal => self,
            LexerToken::Identity => self,
            LexerToken::Text(contents, (mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Text(contents, (start, end))
            }
            LexerToken::StartCommentSingleLineTerminated((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::StartCommentSingleLineTerminated((start, end))
            }
            LexerToken::StartCommentMultiLine((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::StartCommentMultiLine((start, end))
            }
            LexerToken::SingleLineCommentCharacter((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::SingleLineCommentCharacter((start, end))
            }
            LexerToken::EndCommentSingleLineTerminated((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::EndCommentSingleLineTerminated((start, end))
            }
            LexerToken::EndCommentMultiLine((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::EndCommentMultiLine((start, end))
            }
            LexerToken::Feature((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Feature((start, end))
            }
            LexerToken::FeatureNot((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::FeatureNot((start, end))
            }
            LexerToken::FeatureSwitch((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::FeatureSwitch((start, end))
            }
            LexerToken::FeatureCase((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::FeatureCase((start, end))
            }
            LexerToken::FeatureElse((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::FeatureElse((start, end))
            }
            LexerToken::FeatureElseIf((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::FeatureElseIf((start, end))
            }
            LexerToken::EndFeature((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::EndFeature((start, end))
            }
            LexerToken::Configuration((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Configuration((start, end))
            }
            LexerToken::ConfigurationNot((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationNot((start, end))
            }
            LexerToken::ConfigurationSwitch((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationSwitch((start, end))
            }
            LexerToken::ConfigurationCase((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationCase((start, end))
            }
            LexerToken::ConfigurationElse((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationElse((start, end))
            }
            LexerToken::ConfigurationElseIf((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationElseIf((start, end))
            }
            LexerToken::EndConfiguration((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::EndConfiguration((start, end))
            }
            LexerToken::ConfigurationGroup((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationGroup((start, end))
            }
            LexerToken::ConfigurationGroupNot((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationGroupNot((start, end))
            }
            LexerToken::ConfigurationGroupSwitch((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationGroupSwitch((start, end))
            }
            LexerToken::ConfigurationGroupCase((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationGroupCase((start, end))
            }
            LexerToken::ConfigurationGroupElse((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationGroupElse((start, end))
            }
            LexerToken::ConfigurationGroupElseIf((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::ConfigurationGroupElseIf((start, end))
            }
            LexerToken::EndConfigurationGroup((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::EndConfigurationGroup((start, end))
            }
            LexerToken::Substitution((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Substitution((start, end))
            }
            LexerToken::Space((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Space((start, end))
            }
            LexerToken::CarriageReturn((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::CarriageReturn((start, end))
            }
            LexerToken::UnixNewLine((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::UnixNewLine((start, end))
            }
            LexerToken::Tab((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Tab((start, end))
            }
            LexerToken::StartBrace((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::StartBrace((start, end))
            }
            LexerToken::EndBrace((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::EndBrace((start, end))
            }
            LexerToken::StartParen((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::StartParen((start, end))
            }
            LexerToken::EndParen((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::EndParen((start, end))
            }
            LexerToken::Not((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Not((start, end))
            }
            LexerToken::And((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::And((start, end))
            }
            LexerToken::Or((mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Or((start, end))
            }
            LexerToken::Tag(contents, (mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Tag(contents, (start, end))
            }
        }
    }
    pub fn increment_line_number(self, line_number: u32) -> Self {
        match self {
            LexerToken::Nothing => self,
            LexerToken::Illegal => self,
            LexerToken::Identity => self,
            LexerToken::Text(content, (mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Text(content, (start, end))
            }
            LexerToken::StartCommentSingleLineTerminated((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::StartCommentSingleLineTerminated((start, end))
            }
            LexerToken::StartCommentMultiLine((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::StartCommentMultiLine((start, end))
            }
            LexerToken::SingleLineCommentCharacter((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::SingleLineCommentCharacter((start, end))
            }
            LexerToken::EndCommentSingleLineTerminated((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::EndCommentSingleLineTerminated((start, end))
            }
            LexerToken::EndCommentMultiLine((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::EndCommentMultiLine((start, end))
            }
            LexerToken::Feature((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Feature((start, end))
            }
            LexerToken::FeatureNot((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::FeatureNot((start, end))
            }
            LexerToken::FeatureSwitch((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::FeatureSwitch((start, end))
            }
            LexerToken::FeatureCase((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::FeatureCase((start, end))
            }
            LexerToken::FeatureElse((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::FeatureElse((start, end))
            }
            LexerToken::FeatureElseIf((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::FeatureElseIf((start, end))
            }
            LexerToken::EndFeature((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::EndFeature((start, end))
            }
            LexerToken::Configuration((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Configuration((start, end))
            }
            LexerToken::ConfigurationNot((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationNot((start, end))
            }
            LexerToken::ConfigurationSwitch((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationSwitch((start, end))
            }
            LexerToken::ConfigurationCase((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationCase((start, end))
            }
            LexerToken::ConfigurationElse((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationElse((start, end))
            }
            LexerToken::ConfigurationElseIf((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationElseIf((start, end))
            }
            LexerToken::EndConfiguration((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::EndConfiguration((start, end))
            }
            LexerToken::ConfigurationGroup((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationGroup((start, end))
            }
            LexerToken::ConfigurationGroupNot((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationGroupNot((start, end))
            }
            LexerToken::ConfigurationGroupSwitch((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationGroupSwitch((start, end))
            }
            LexerToken::ConfigurationGroupCase((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationGroupCase((start, end))
            }
            LexerToken::ConfigurationGroupElse((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationGroupElse((start, end))
            }
            LexerToken::ConfigurationGroupElseIf((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::ConfigurationGroupElseIf((start, end))
            }
            LexerToken::EndConfigurationGroup((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::EndConfigurationGroup((start, end))
            }
            LexerToken::Substitution((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Substitution((start, end))
            }
            LexerToken::Space((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Space((start, end))
            }
            LexerToken::CarriageReturn((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::CarriageReturn((start, end))
            }
            LexerToken::UnixNewLine((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::UnixNewLine((start, end))
            }
            LexerToken::Tab((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Tab((start, end))
            }
            LexerToken::StartBrace((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::StartBrace((start, end))
            }
            LexerToken::EndBrace((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::EndBrace((start, end))
            }
            LexerToken::StartParen((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::StartParen((start, end))
            }
            LexerToken::EndParen((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::EndParen((start, end))
            }
            LexerToken::Not((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Not((start, end))
            }
            LexerToken::And((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::And((start, end))
            }
            LexerToken::Or((mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Or((start, end))
            }
            LexerToken::Tag(content, (mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Tag(content, (start, end))
            }
        }
    }
}
