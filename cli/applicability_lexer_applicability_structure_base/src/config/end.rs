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
    config::end::ConfigurationEnd,
    position::Position,
    utils::locatable::{Locatable, position},
};

pub trait LexConfigurationEnd {
    fn lex_config_end<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_end_multi_line<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_end_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationEnd for T
where
    T: ConfigurationEnd,
{
    fn lex_config_end_multi_line<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.config_end_multi_line())
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| {
                LexerToken::EndConfiguration((start, end))
            })
    }
    fn lex_config_end<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_end()).and(position()).map(
            |((start, _), end): ((Position, _), Position)| {
                LexerToken::EndConfiguration((start, end))
            },
        )
    }

    fn lex_config_end_tag<'x>(&self) -> &'x str {
        self.config_end_tag()
    }
}
