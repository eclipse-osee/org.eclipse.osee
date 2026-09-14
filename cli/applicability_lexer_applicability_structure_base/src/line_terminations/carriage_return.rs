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
use nom::{AsChar, Compare, Input, Parser, error::ParseError};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    line_terminations::carriage_return::CarriageReturn,
    position::Position,
    utils::locatable::{Locatable, position},
};

pub trait LexCarriageReturn {
    fn lex_carriage_return<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_carriage_return_tag<'x>(&self) -> &'x str;
}

impl<T> LexCarriageReturn for T
where
    T: CarriageReturn,
{
    fn lex_carriage_return<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(Parser::into::<I, _>(self.carriage_return()))
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| {
                LexerToken::CarriageReturn((start, end))
            })
    }

    fn lex_carriage_return_tag<'x>(&self) -> &'x str {
        self.carriage_return_tag()
    }
}
