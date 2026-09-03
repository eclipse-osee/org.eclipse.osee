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
use nom::Input;

use crate::position::Position;

#[derive(Debug, Clone, PartialEq, Eq)]
pub enum DocumentStructureToken<I: Input + Send + Sync> {
    SingleLineComment(I, Position, Position),
    SingleLineTerminatedComment(I, Position, Position),
    MultiLineComment(I, Position, Position),
    // DocComment(I, Position, Position),
    CodeBlock(I, Position, Position),
    Text(I, Position, Position),
}

impl<I: Input + Send + Sync> DocumentStructureToken<I> {
    pub fn get_inner(&self) -> &I {
        match self {
            DocumentStructureToken::SingleLineComment(i, _, _) => i,
            DocumentStructureToken::SingleLineTerminatedComment(i, _, _) => i,
            DocumentStructureToken::MultiLineComment(i, _, _) => i,
            DocumentStructureToken::Text(i, _, _) => i,
            DocumentStructureToken::CodeBlock(i, _, _) => i,
        }
    }
}
