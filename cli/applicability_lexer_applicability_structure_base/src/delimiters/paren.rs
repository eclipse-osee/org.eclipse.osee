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
    delimiters::paren::{EndParen, StartParen},
    position::Position,
    utils::locatable::{Locatable, position},
};

pub trait LexStartParen {
    fn lex_start_paren<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_start_paren_tag<'x>(&self) -> &'x str;
}

impl<T> LexStartParen for T
where
    T: StartParen,
{
    fn lex_start_paren<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.start_paren()).and(position()).map(
            |((start, _), end): ((Position, _), Position)| LexerToken::StartParen((start, end)),
        )
    }

    fn lex_start_paren_tag<'x>(&self) -> &'x str {
        self.start_paren_tag()
    }
}
pub trait LexEndParen {
    fn lex_end_paren<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_end_paren_tag<'x>(&self) -> &'x str;
}

impl<T> LexEndParen for T
where
    T: EndParen,
{
    fn lex_end_paren<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.end_paren())
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| LexerToken::EndParen((start, end)))
    }

    fn lex_end_paren_tag<'x>(&self) -> &'x str {
        self.end_paren_tag()
    }
}
