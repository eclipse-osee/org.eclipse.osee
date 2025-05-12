use nom::{AsBytes, Input, Offset};
use nom_locate::LocatedSpan;

use crate::position::{Position, TokenPosition};

#[derive(Debug, Clone, PartialEq, Eq, Default)]
pub enum LexerToken<I: Input + Send + Sync> {
    #[default]
    Nothing,
    Illegal,
    Identity,
    Text(I, TokenPosition),
    // This is text we will end up discarding as it is random text included in a comment with a feature,configuration,configuration group, or substitution tag
    TextToDiscard(I, TokenPosition),
    // Eof, This path should be illegal now
    // StartCommentSingleLineTerminated(TokenPosition),
    StartCommentMultiLine(I, TokenPosition),
    SingleLineCommentCharacter(I, TokenPosition),
    // EndCommentSingleLineTerminated(TokenPosition),
    EndCommentMultiLine(I, TokenPosition),
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
            LexerToken::TextToDiscard(contents, position) => {
                LexerToken::<I>::TextToDiscard(contents.into_fragment(), position)
            }
            LexerToken::Text(contents, position) => {
                LexerToken::<I>::Text(contents.into_fragment(), position)
            }
            // LexerToken::StartCommentSingleLineTerminated(position) => {
            //     LexerToken::<I>::StartCommentSingleLineTerminated(position)
            // }
            LexerToken::StartCommentMultiLine(contents, position) => {
                LexerToken::<I>::StartCommentMultiLine(contents.into_fragment(), position)
            }
            LexerToken::SingleLineCommentCharacter(contents, position) => {
                LexerToken::<I>::SingleLineCommentCharacter(contents.into_fragment(), position)
            }
            // LexerToken::EndCommentSingleLineTerminated(position) => {
            //     LexerToken::<I>::EndCommentSingleLineTerminated(position)
            // }
            LexerToken::EndCommentMultiLine(contents, position) => {
                LexerToken::<I>::EndCommentMultiLine(contents.into_fragment(), position)
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
pub fn update_start_position<I: Input + Send + Sync>(
    token: LexerToken<I>,
    start_pos: Position,
) -> LexerToken<I> {
    match token {
        LexerToken::Nothing => LexerToken::Nothing,
        LexerToken::Illegal => LexerToken::Illegal,
        LexerToken::Identity => LexerToken::Identity,
        LexerToken::Text(contents, (_start, end)) => LexerToken::Text(contents, (start_pos, end)),
        LexerToken::TextToDiscard(contents, (_start, end)) => {
            LexerToken::TextToDiscard(contents, (start_pos, end))
        }
        // LexerToken::StartCommentSingleLineTerminated((_start, end)) => {
        //     LexerToken::StartCommentSingleLineTerminated((start_pos, end))
        // }
        LexerToken::StartCommentMultiLine(contents, (_start, end)) => {
            LexerToken::StartCommentMultiLine(contents, (start_pos, end))
        }
        LexerToken::SingleLineCommentCharacter(contents, (_start, end)) => {
            LexerToken::SingleLineCommentCharacter(contents, (start_pos, end))
        }
        // LexerToken::EndCommentSingleLineTerminated((_start, end)) => {
        //     LexerToken::EndCommentSingleLineTerminated((start_pos, end))
        // }
        LexerToken::EndCommentMultiLine(contents, (_start, end)) => {
            LexerToken::EndCommentMultiLine(contents, (start_pos, end))
        }
        LexerToken::Feature((_start, end)) => LexerToken::Feature((start_pos, end)),
        LexerToken::FeatureNot((_start, end)) => LexerToken::FeatureNot((start_pos, end)),
        LexerToken::FeatureSwitch((_start, end)) => LexerToken::FeatureSwitch((start_pos, end)),
        LexerToken::FeatureCase((_start, end)) => LexerToken::FeatureCase((start_pos, end)),
        LexerToken::FeatureElse((_start, end)) => LexerToken::FeatureElse((start_pos, end)),
        LexerToken::FeatureElseIf((_start, end)) => LexerToken::FeatureElseIf((start_pos, end)),
        LexerToken::EndFeature((_start, end)) => LexerToken::EndFeature((start_pos, end)),
        LexerToken::Configuration((_start, end)) => LexerToken::Configuration((start_pos, end)),
        LexerToken::ConfigurationNot((_start, end)) => {
            LexerToken::ConfigurationNot((start_pos, end))
        }
        LexerToken::ConfigurationSwitch((_start, end)) => {
            LexerToken::ConfigurationSwitch((start_pos, end))
        }
        LexerToken::ConfigurationCase((_start, end)) => {
            LexerToken::ConfigurationCase((start_pos, end))
        }
        LexerToken::ConfigurationElse((_start, end)) => {
            LexerToken::ConfigurationElse((start_pos, end))
        }
        LexerToken::ConfigurationElseIf((_start, end)) => {
            LexerToken::ConfigurationElseIf((start_pos, end))
        }
        LexerToken::EndConfiguration((_start, end)) => {
            LexerToken::EndConfiguration((start_pos, end))
        }
        LexerToken::ConfigurationGroup((_start, end)) => {
            LexerToken::ConfigurationGroup((start_pos, end))
        }
        LexerToken::ConfigurationGroupNot((_start, end)) => {
            LexerToken::ConfigurationGroupNot((start_pos, end))
        }
        LexerToken::ConfigurationGroupSwitch((_start, end)) => {
            LexerToken::ConfigurationGroupSwitch((start_pos, end))
        }
        LexerToken::ConfigurationGroupCase((_start, end)) => {
            LexerToken::ConfigurationGroupCase((start_pos, end))
        }
        LexerToken::ConfigurationGroupElse((_start, end)) => {
            LexerToken::ConfigurationGroupElse((start_pos, end))
        }
        LexerToken::ConfigurationGroupElseIf((_start, end)) => {
            LexerToken::ConfigurationGroupElseIf((start_pos, end))
        }
        LexerToken::EndConfigurationGroup((_start, end)) => {
            LexerToken::EndConfigurationGroup((start_pos, end))
        }
        LexerToken::Substitution((_start, end)) => LexerToken::Substitution((start_pos, end)),
        LexerToken::Space((_start, end)) => LexerToken::Space((start_pos, end)),
        LexerToken::CarriageReturn((_start, end)) => LexerToken::CarriageReturn((start_pos, end)),
        LexerToken::UnixNewLine((_start, end)) => LexerToken::UnixNewLine((start_pos, end)),
        LexerToken::Tab((_start, end)) => LexerToken::Tab((start_pos, end)),
        LexerToken::StartBrace((_start, end)) => LexerToken::StartBrace((start_pos, end)),
        LexerToken::EndBrace((_start, end)) => LexerToken::EndBrace((start_pos, end)),
        LexerToken::StartParen((_start, end)) => LexerToken::StartParen((start_pos, end)),
        LexerToken::EndParen((_start, end)) => LexerToken::EndParen((start_pos, end)),
        LexerToken::Not((_start, end)) => LexerToken::Not((start_pos, end)),
        LexerToken::And((_start, end)) => LexerToken::And((start_pos, end)),
        LexerToken::Or((_start, end)) => LexerToken::Or((start_pos, end)),
        LexerToken::Tag(contents, (_start, end)) => LexerToken::Tag(contents, (start_pos, end)),
    }
}
pub fn update_end_position<I: Input + Send + Sync>(
    token: LexerToken<I>,
    end_pos: Position,
) -> LexerToken<I> {
    match token {
        LexerToken::Nothing => LexerToken::Nothing,
        LexerToken::Illegal => LexerToken::Illegal,
        LexerToken::Identity => LexerToken::Identity,
        LexerToken::Text(contents, (start, _end)) => LexerToken::Text(contents, (start, end_pos)),
        LexerToken::TextToDiscard(contents, (start, _end)) => {
            LexerToken::TextToDiscard(contents, (start, end_pos))
        }
        // LexerToken::StartCommentSingleLineTerminated((start, _end)) => {
        //     LexerToken::StartCommentSingleLineTerminated((start, end_pos))
        // }
        LexerToken::StartCommentMultiLine(contents, (start, _end)) => {
            LexerToken::StartCommentMultiLine(contents, (start, end_pos))
        }
        LexerToken::SingleLineCommentCharacter(contents, (start, _end)) => {
            LexerToken::SingleLineCommentCharacter(contents, (start, end_pos))
        }
        // LexerToken::EndCommentSingleLineTerminated((start, _end)) => {
        //     LexerToken::EndCommentSingleLineTerminated((start, end_pos))
        // }
        LexerToken::EndCommentMultiLine(contents, (start, _end)) => {
            LexerToken::EndCommentMultiLine(contents, (start, end_pos))
        }
        LexerToken::Feature((start, _end)) => LexerToken::Feature((start, end_pos)),
        LexerToken::FeatureNot((start, _end)) => LexerToken::FeatureNot((start, end_pos)),
        LexerToken::FeatureSwitch((start, _end)) => LexerToken::FeatureSwitch((start, end_pos)),
        LexerToken::FeatureCase((start, _end)) => LexerToken::FeatureCase((start, end_pos)),
        LexerToken::FeatureElse((start, _end)) => LexerToken::FeatureElse((start, end_pos)),
        LexerToken::FeatureElseIf((start, _end)) => LexerToken::FeatureElseIf((start, end_pos)),
        LexerToken::EndFeature((start, _end)) => LexerToken::EndFeature((start, end_pos)),
        LexerToken::Configuration((start, _end)) => LexerToken::Configuration((start, end_pos)),
        LexerToken::ConfigurationNot((start, _end)) => {
            LexerToken::ConfigurationNot((start, end_pos))
        }
        LexerToken::ConfigurationSwitch((start, _end)) => {
            LexerToken::ConfigurationSwitch((start, end_pos))
        }
        LexerToken::ConfigurationCase((start, _end)) => {
            LexerToken::ConfigurationCase((start, end_pos))
        }
        LexerToken::ConfigurationElse((start, _end)) => {
            LexerToken::ConfigurationElse((start, end_pos))
        }
        LexerToken::ConfigurationElseIf((start, _end)) => {
            LexerToken::ConfigurationElseIf((start, end_pos))
        }
        LexerToken::EndConfiguration((start, _end)) => {
            LexerToken::EndConfiguration((start, end_pos))
        }
        LexerToken::ConfigurationGroup((start, _end)) => {
            LexerToken::ConfigurationGroup((start, end_pos))
        }
        LexerToken::ConfigurationGroupNot((start, _end)) => {
            LexerToken::ConfigurationGroupNot((start, end_pos))
        }
        LexerToken::ConfigurationGroupSwitch((start, _end)) => {
            LexerToken::ConfigurationGroupSwitch((start, end_pos))
        }
        LexerToken::ConfigurationGroupCase((start, _end)) => {
            LexerToken::ConfigurationGroupCase((start, end_pos))
        }
        LexerToken::ConfigurationGroupElse((start, _end)) => {
            LexerToken::ConfigurationGroupElse((start, end_pos))
        }
        LexerToken::ConfigurationGroupElseIf((start, _end)) => {
            LexerToken::ConfigurationGroupElseIf((start, end_pos))
        }
        LexerToken::EndConfigurationGroup((start, _end)) => {
            LexerToken::EndConfigurationGroup((start, end_pos))
        }
        LexerToken::Substitution((start, _end)) => LexerToken::Substitution((start, end_pos)),
        LexerToken::Space((start, _end)) => LexerToken::Space((start, end_pos)),
        LexerToken::CarriageReturn((start, _end)) => LexerToken::CarriageReturn((start, end_pos)),
        LexerToken::UnixNewLine((start, _end)) => LexerToken::UnixNewLine((start, end_pos)),
        LexerToken::Tab((start, _end)) => LexerToken::Tab((start, end_pos)),
        LexerToken::StartBrace((start, _end)) => LexerToken::StartBrace((start, end_pos)),
        LexerToken::EndBrace((start, _end)) => LexerToken::EndBrace((start, end_pos)),
        LexerToken::StartParen((start, _end)) => LexerToken::StartParen((start, end_pos)),
        LexerToken::EndParen((start, _end)) => LexerToken::EndParen((start, end_pos)),
        LexerToken::Not((start, _end)) => LexerToken::Not((start, end_pos)),
        LexerToken::And((start, _end)) => LexerToken::And((start, end_pos)),
        LexerToken::Or((start, _end)) => LexerToken::Or((start, end_pos)),
        LexerToken::Tag(contents, (start, _end)) => LexerToken::Tag(contents, (start, end_pos)),
    }
}

impl<I: Input + Send + Sync> LexerToken<I> {
    pub fn get_start_position(&self) -> Position {
        match self {
            LexerToken::Nothing => (0, 0),
            LexerToken::Illegal => (0, 0),
            LexerToken::Identity => (0, 0),
            LexerToken::Text(_, (start, __end)) => *start,
            LexerToken::TextToDiscard(_, (start, _end)) => *start,
            // LexerToken::StartCommentSingleLineTerminated((start, _end)) => *start,
            LexerToken::StartCommentMultiLine(_, (start, _end)) => *start,
            LexerToken::SingleLineCommentCharacter(contents, (start, _end)) => *start,
            // LexerToken::EndCommentSingleLineTerminated((start, _end)) => *start,
            LexerToken::EndCommentMultiLine(_, (start, _end)) => *start,
            LexerToken::Feature((start, _end)) => *start,
            LexerToken::FeatureNot((start, _end)) => *start,
            LexerToken::FeatureSwitch((start, _end)) => *start,
            LexerToken::FeatureCase((start, _end)) => *start,
            LexerToken::FeatureElse((start, _end)) => *start,
            LexerToken::FeatureElseIf((start, _end)) => *start,
            LexerToken::EndFeature((start, _end)) => *start,
            LexerToken::Configuration((start, _end)) => *start,
            LexerToken::ConfigurationNot((start, _end)) => *start,
            LexerToken::ConfigurationSwitch((start, _end)) => *start,
            LexerToken::ConfigurationCase((start, _end)) => *start,
            LexerToken::ConfigurationElse((start, _end)) => *start,
            LexerToken::ConfigurationElseIf((start, _end)) => *start,
            LexerToken::EndConfiguration((start, _end)) => *start,
            LexerToken::ConfigurationGroup((start, _end)) => *start,
            LexerToken::ConfigurationGroupNot((start, _end)) => *start,
            LexerToken::ConfigurationGroupSwitch((start, _end)) => *start,
            LexerToken::ConfigurationGroupCase((start, _end)) => *start,
            LexerToken::ConfigurationGroupElse((start, _end)) => *start,
            LexerToken::ConfigurationGroupElseIf((start, _end)) => *start,
            LexerToken::EndConfigurationGroup((start, _end)) => *start,
            LexerToken::Substitution((start, _end)) => *start,
            LexerToken::Space((start, _end)) => *start,
            LexerToken::CarriageReturn((start, _end)) => *start,
            LexerToken::UnixNewLine((start, _end)) => *start,
            LexerToken::Tab((start, _end)) => *start,
            LexerToken::StartBrace((start, _end)) => *start,
            LexerToken::EndBrace((start, _end)) => *start,
            LexerToken::StartParen((start, _end)) => *start,
            LexerToken::EndParen((start, _end)) => *start,
            LexerToken::Not((start, _end)) => *start,
            LexerToken::And((start, _end)) => *start,
            LexerToken::Or((start, _end)) => *start,
            LexerToken::Tag(_, (start, _end)) => *start,
        }
    }
    pub fn get_end_position(self) -> Position {
        match self {
            LexerToken::Nothing => (0, 0),
            LexerToken::Illegal => (0, 0),
            LexerToken::Identity => (0, 0),
            LexerToken::Text(_, (_start, end)) => end,
            LexerToken::TextToDiscard(_, (_start, end)) => end,
            // LexerToken::StartCommentSingleLineTerminated((_start, end)) => end,
            LexerToken::StartCommentMultiLine(_, (_start, end)) => end,
            LexerToken::SingleLineCommentCharacter(contents, (_start, end)) => end,
            // LexerToken::EndCommentSingleLineTerminated((_start, end)) => end,
            LexerToken::EndCommentMultiLine(_, (_start, end)) => end,
            LexerToken::Feature((_start, end)) => end,
            LexerToken::FeatureNot((_start, end)) => end,
            LexerToken::FeatureSwitch((_start, end)) => end,
            LexerToken::FeatureCase((_start, end)) => end,
            LexerToken::FeatureElse((_start, end)) => end,
            LexerToken::FeatureElseIf((_start, end)) => end,
            LexerToken::EndFeature((_start, end)) => end,
            LexerToken::Configuration((_start, end)) => end,
            LexerToken::ConfigurationNot((_start, end)) => end,
            LexerToken::ConfigurationSwitch((_start, end)) => end,
            LexerToken::ConfigurationCase((_start, end)) => end,
            LexerToken::ConfigurationElse((_start, end)) => end,
            LexerToken::ConfigurationElseIf((_start, end)) => end,
            LexerToken::EndConfiguration((_start, end)) => end,
            LexerToken::ConfigurationGroup((_start, end)) => end,
            LexerToken::ConfigurationGroupNot((_start, end)) => end,
            LexerToken::ConfigurationGroupSwitch((_start, end)) => end,
            LexerToken::ConfigurationGroupCase((_start, end)) => end,
            LexerToken::ConfigurationGroupElse((_start, end)) => end,
            LexerToken::ConfigurationGroupElseIf((_start, end)) => end,
            LexerToken::EndConfigurationGroup((_start, end)) => end,
            LexerToken::Substitution((_start, end)) => end,
            LexerToken::Space((_start, end)) => end,
            LexerToken::CarriageReturn((_start, end)) => end,
            LexerToken::UnixNewLine((_start, end)) => end,
            LexerToken::Tab((_start, end)) => end,
            LexerToken::StartBrace((_start, end)) => end,
            LexerToken::EndBrace((_start, end)) => end,
            LexerToken::StartParen((_start, end)) => end,
            LexerToken::EndParen((_start, end)) => end,
            LexerToken::Not((_start, end)) => end,
            LexerToken::And((_start, end)) => end,
            LexerToken::Or((_start, end)) => end,
            LexerToken::Tag(_, (_start, end)) => end,
        }
    }
    pub fn increment_offset(self, offset: usize) -> Self {
        match self {
            LexerToken::Nothing => self,
            LexerToken::Illegal => self,
            LexerToken::Identity => self,
            LexerToken::TextToDiscard(contents, (mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::TextToDiscard(contents, (start, end))
            }
            LexerToken::Text(contents, (mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::Text(contents, (start, end))
            }
            // LexerToken::StartCommentSingleLineTerminated((mut start, mut end)) => {
            //     start.0 += offset;
            //     end.0 += offset;
            //     LexerToken::StartCommentSingleLineTerminated((start, end))
            // }
            LexerToken::StartCommentMultiLine(contents, (mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::StartCommentMultiLine(contents, (start, end))
            }
            LexerToken::SingleLineCommentCharacter(contents, (mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::SingleLineCommentCharacter(contents, (start, end))
            }
            // LexerToken::EndCommentSingleLineTerminated((mut start, mut end)) => {
            //     start.0 += offset;
            //     end.0 += offset;
            //     LexerToken::EndCommentSingleLineTerminated((start, end))
            // }
            LexerToken::EndCommentMultiLine(contents, (mut start, mut end)) => {
                start.0 += offset;
                end.0 += offset;
                LexerToken::EndCommentMultiLine(contents, (start, end))
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
            LexerToken::TextToDiscard(content, (mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::TextToDiscard(content, (start, end))
            }
            LexerToken::Text(content, (mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::Text(content, (start, end))
            }
            // LexerToken::StartCommentSingleLineTerminated((mut start, mut end)) => {
            //     start.1 = start.1 + line_number - 1;
            //     end.1 = end.1 + line_number - 1;
            //     LexerToken::StartCommentSingleLineTerminated((start, end))
            // }
            LexerToken::StartCommentMultiLine(contents, (mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::StartCommentMultiLine(contents, (start, end))
            }
            LexerToken::SingleLineCommentCharacter(contents, (mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::SingleLineCommentCharacter(contents, (start, end))
            }
            // LexerToken::EndCommentSingleLineTerminated((mut start, mut end)) => {
            //     start.1 = start.1 + line_number - 1;
            //     end.1 = end.1 + line_number - 1;
            //     LexerToken::EndCommentSingleLineTerminated((start, end))
            // }
            LexerToken::EndCommentMultiLine(contents, (mut start, mut end)) => {
                start.1 = start.1 + line_number - 1;
                end.1 = end.1 + line_number - 1;
                LexerToken::EndCommentMultiLine(contents, (start, end))
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
