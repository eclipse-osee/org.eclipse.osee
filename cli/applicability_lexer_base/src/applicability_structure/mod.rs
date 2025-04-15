use nom::Input;

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
