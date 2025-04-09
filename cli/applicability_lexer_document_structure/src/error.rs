use nom::error::{ErrorKind, ParseError};
use thiserror::Error;
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
