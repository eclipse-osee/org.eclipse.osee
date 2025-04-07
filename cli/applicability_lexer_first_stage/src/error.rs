use nom::error::{ErrorKind, ParseError};
use thiserror::Error;
#[derive(Debug, PartialEq, Error)]
pub enum FirstStageError<I> {
    #[error("Missing or incorrect start comment")]
    MissingOrIncorrectStartComment,
    #[error("Incorrect sequence")]
    IncorrectSequence,
    #[error("Has no end to input")]
    HasNoEnd,
    #[error("nom error")]
    Nom(I, ErrorKind),
    #[error("undefined error")]
    UndefinedError,
}
impl<I> ParseError<I> for FirstStageError<I> {
    fn from_error_kind(input: I, kind: ErrorKind) -> Self {
        FirstStageError::Nom(input, kind)
    }
    fn append(_input: I, _kind: ErrorKind, other: Self) -> Self {
        other
    }
}
