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
use thiserror::Error;

mod ast_transform_error;
mod nom_parser_error;
pub use ast_transform_error::AstTransformError;
pub use nom_parser_error::ApplicabilityParserInternalErrorWithNomInputs;

#[derive(Debug, PartialEq, Error)]
pub enum ApplicabilityParserError {
    #[error("Missing or incorrect start comment")]
    MissingOrIncorrectStartComment,
    #[error("Missing or incorrect end comment")]
    MissingOrIncorrectEndComment,
    #[error("Incorrect sequence")]
    IncorrectSequence,
    #[error("Has no end to input")]
    HasNoEnd,
    #[error("nom error")]
    Nom,
    #[error("unsupported for document type")]
    Unsupported,
    #[error("undefined error")]
    UndefinedError,
    #[error("Needs an undetermined amount of data")]
    NeedsUnknownMoreData,
    #[error("Need {0} more bytes")]
    NeedsMoreData(usize),
    #[error("AST Transform error")]
    AstTransformError(#[from] AstTransformError),
}
impl<I> From<ApplicabilityParserInternalErrorWithNomInputs<I>> for ApplicabilityParserError {
    fn from(value: ApplicabilityParserInternalErrorWithNomInputs<I>) -> Self {
        match value {
            ApplicabilityParserInternalErrorWithNomInputs::MissingOrIncorrectStartComment => {
                ApplicabilityParserError::MissingOrIncorrectStartComment
            }
            ApplicabilityParserInternalErrorWithNomInputs::MissingOrIncorrectEndComment => {
                ApplicabilityParserError::MissingOrIncorrectEndComment
            }
            ApplicabilityParserInternalErrorWithNomInputs::IncorrectSequence => {
                ApplicabilityParserError::IncorrectSequence
            }
            ApplicabilityParserInternalErrorWithNomInputs::HasNoEnd => {
                ApplicabilityParserError::HasNoEnd
            }
            ApplicabilityParserInternalErrorWithNomInputs::Nom(_, _error_kind) => {
                ApplicabilityParserError::Nom
            }
            ApplicabilityParserInternalErrorWithNomInputs::NomMapRes(_, _error_kind, _) => {
                ApplicabilityParserError::Nom
            }
            ApplicabilityParserInternalErrorWithNomInputs::Unsupported => {
                ApplicabilityParserError::Unsupported
            }
            ApplicabilityParserInternalErrorWithNomInputs::UndefinedError => {
                ApplicabilityParserError::UndefinedError
            }
            ApplicabilityParserInternalErrorWithNomInputs::NeedsUnknownMoreData => {
                ApplicabilityParserError::NeedsUnknownMoreData
            }
            ApplicabilityParserInternalErrorWithNomInputs::NeedsMoreData(size) => {
                ApplicabilityParserError::NeedsMoreData(size)
            }
        }
    }
}
