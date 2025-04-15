use nom::{
    error::{ErrorKind, ParseError},
    Input,
};
use thiserror::Error;

use crate::position::Position;

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum DocumentStructureToken<I: Input + Send + Sync> {
    SingleLineComment(I, Position, Position),
    SingleLineTerminatedComment(I, Position, Position),
    MultiLineComment(I, Position, Position),
    Text(I, Position, Position),
}

impl<I: Input + Send + Sync> DocumentStructureToken<I> {
    pub fn get_inner(&self) -> &I {
        match self {
            DocumentStructureToken::SingleLineComment(i, _, _) => i,
            DocumentStructureToken::SingleLineTerminatedComment(i, _, _) => i,
            DocumentStructureToken::MultiLineComment(i, _, _) => i,
            DocumentStructureToken::Text(i, _, _) => i,
        }
    }
}

#[derive(Debug, PartialEq, Error)]
pub enum DocumentStructureError<I> {
    #[error("Missing or incorrect start comment")]
    MissingOrIncorrectStartComment,
    #[error("Missing or incorrect end comment")]
    MissingOrIncorrectEndComment,
    #[error("Incorrect sequence")]
    IncorrectSequence,
    #[error("Has no end to input")]
    HasNoEnd,
    #[error("nom error")]
    Nom(I, ErrorKind),
    #[error("unsupported for document type")]
    Unsupported,
    #[error("undefined error")]
    UndefinedError,
}
impl<I> ParseError<I> for DocumentStructureError<I> {
    fn from_error_kind(input: I, kind: ErrorKind) -> Self {
        DocumentStructureError::Nom(input, kind)
    }
    fn append(_input: I, _kind: ErrorKind, other: Self) -> Self {
        other
    }
}
