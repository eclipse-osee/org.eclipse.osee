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
use nom::error::{Error, ErrorKind, FromExternalError, ParseError};
use thiserror::Error;

#[derive(Debug, PartialEq, Error)]
pub enum ApplicabilityParserError<I, E = Error<I>> {
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
    #[error("nom map res error")]
    NomMapRes(I, ErrorKind, E),
    #[error("unsupported for document type")]
    Unsupported,
    #[error("undefined error")]
    UndefinedError,
}
impl<I> ParseError<I> for ApplicabilityParserError<I> {
    fn from_error_kind(input: I, kind: ErrorKind) -> Self {
        ApplicabilityParserError::Nom(input, kind)
    }
    fn append(_input: I, _kind: ErrorKind, other: Self) -> Self {
        other
    }
}

impl<I, E> FromExternalError<I, E> for ApplicabilityParserError<I, E> {
    fn from_external_error(input: I, kind: ErrorKind, error: E) -> Self {
        ApplicabilityParserError::NomMapRes(input, kind, error)
    }
}
