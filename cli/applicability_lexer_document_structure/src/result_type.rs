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
#[cfg(test)]
use applicability_lexer_base::document_structure::DocumentStructureToken;
use applicability_parser_errors::ApplicabilityParserInternalErrorWithNomInputs;
#[cfg(test)]
use nom::IResult;
#[cfg(test)]
use nom_locate::LocatedSpan;

#[cfg(test)]
pub type ResultType<I> = IResult<
    LocatedSpan<I>,
    Vec<DocumentStructureToken<LocatedSpan<I>>>,
    ApplicabilityParserInternalErrorWithNomInputs<LocatedSpan<I>>,
>;
