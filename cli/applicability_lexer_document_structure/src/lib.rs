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
mod code_block;
pub mod document_structure_parser;
mod document_structure_text;
mod failed_advance;
mod multi_line_terminated;
#[cfg(test)]
mod result_type;
mod single_line_non_terminated;
mod single_line_terminated;
