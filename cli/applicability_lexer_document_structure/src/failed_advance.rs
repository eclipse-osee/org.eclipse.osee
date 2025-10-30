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
use applicability_parser_errors::ApplicabilityParserInternalErrorWithNomInputs;
use nom::{AsBytes, AsChar, Compare, Input, Mode, Parser};

use applicability_lexer_base::{
    comment::{
        code_block::{EndCodeBlock, StartCodeBlock},
        multi_line::{EndCommentMultiLine, StartCommentMultiLine},
        single_line::{
            EndCommentSingleLineTerminated, StartCommentSingleLineNonTerminated,
            StartCommentSingleLineTerminated,
        },
    },
    document_structure::DocumentStructureToken,
    line_terminations::{carriage_return::CarriageReturn, new_line::NewLine},
    utils::locatable::Locatable,
};

pub trait AdvanceByFailedToken {
    fn advance_by_failed_token<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = ApplicabilityParserInternalErrorWithNomInputs<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
        <I as Input>::Item: AsChar;
}

impl<T> AdvanceByFailedToken for T
where
    T: StartCodeBlock
        + EndCodeBlock
        + StartCommentMultiLine
        + StartCommentSingleLineNonTerminated
        + StartCommentSingleLineTerminated
        + EndCommentMultiLine
        + EndCommentSingleLineTerminated
        + CarriageReturn
        + NewLine,
{
    fn advance_by_failed_token<I>(
        &self,
    ) -> impl Parser<I, Output = DocumentStructureToken<I>, Error = ApplicabilityParserInternalErrorWithNomInputs<I>>
    where
        I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
        <I as Input>::Item: AsChar,
    {
        FailedTokenParser { doc: self }
    }
}
struct FailedTokenParser<'single_line_parser, T> {
    doc: &'single_line_parser T,
}

impl<I, T> Parser<I> for FailedTokenParser<'_, T>
where
    I: Input + for<'x> Compare<&'x str> + Locatable + Send + Sync + AsBytes,
    <I as Input>::Item: AsChar,
    T: StartCodeBlock
        + EndCodeBlock
        + StartCommentMultiLine
        + StartCommentSingleLineNonTerminated
        + StartCommentSingleLineTerminated
        + EndCommentMultiLine
        + EndCommentSingleLineTerminated
        + CarriageReturn
        + NewLine,
{
    type Output = DocumentStructureToken<I>;
    type Error = ApplicabilityParserInternalErrorWithNomInputs<I>;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        if self.doc.has_start_code_block_support() && self.doc.has_end_code_block_support() {
            // check to see if position of code block is 0, and advance
            let start_code_block = self.doc.start_code_block_position(&input.as_bytes());
            if start_code_block.unwrap_or(1) == 0 {
                let final_position = self.doc.start_code_block_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
            let end_code_block = self.doc.end_code_block_position(&input.as_bytes());
            if end_code_block.unwrap_or(1) == 0 {
                let final_position = self.doc.end_code_block_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
        }
        if self.doc.has_start_comment_single_line_terminated_support()
            && self.doc.has_end_comment_single_line_terminated_support()
        {
            // check to see if position of terminated is 0, and advance
            let start_comment_block = self
                .doc
                .start_comment_single_line_terminated_position(&input.as_bytes());
            if start_comment_block.unwrap_or(1) == 0 {
                let final_position = self.doc.start_comment_single_line_terminated_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
            let end_comment_block = self.doc.end_comment_single_line_position(&input.as_bytes());
            if end_comment_block.unwrap_or(1) == 0 {
                let final_position = self.doc.end_comment_single_line_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
        }
        if self.doc.has_start_comment_multi_line_support()
            && self.doc.has_end_comment_multi_line_support()
        {
            // check to see if position of multi line is 0, and advance
            let start_comment_block = self
                .doc
                .start_comment_multi_line_position(&input.as_bytes());
            if start_comment_block.unwrap_or(1) == 0 {
                let final_position = self.doc.start_comment_multi_line_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
            let end_comment_block = self.doc.end_comment_multi_line_position(&input.as_bytes());
            if end_comment_block.unwrap_or(1) == 0 {
                let final_position = self.doc.end_comment_multi_line_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
        }
        if self
            .doc
            .has_start_comment_single_line_non_terminated_support()
        {
            // check to see if position of code block is 0, and advance
            let start_comment_block = self
                .doc
                .start_comment_single_line_non_terminated_position(&input.as_bytes());
            if start_comment_block.unwrap_or(1) == 0 {
                let final_position = self.doc.start_comment_single_line_terminated_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
            let carriage = self.doc.carriage_return_position(&input.as_bytes());
            if carriage.unwrap_or(1) == 0 {
                let final_position = self.doc.carriage_return_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
            let new_line = self.doc.new_line_position(&input.as_bytes());
            if new_line.unwrap_or(1) == 0 {
                let final_position = self.doc.new_line_tag().len();
                let remaining_input = input.take_from(final_position);
                let remaining_input_position = remaining_input.get_position();
                return Ok((
                    remaining_input,
                    OM::Output::bind(|| {
                        let start_pos = input.get_position();
                        let resulting_input = input.take(final_position);
                        DocumentStructureToken::Text(
                            resulting_input,
                            start_pos,
                            remaining_input_position,
                        )
                    }),
                ));
            }
        }
        Err(nom::Err::Error(OM::Error::bind(|| {
            ApplicabilityParserInternalErrorWithNomInputs::IncorrectSequence
        })))
    }
}
