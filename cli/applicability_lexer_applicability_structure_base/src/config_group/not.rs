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
    config_group::not::ConfigurationGroupNot,
    position::Position,
    utils::locatable::{Locatable, position},
};

pub trait LexConfigurationGroupNot {
    fn lex_config_group_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_not_multi_line<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_config_group_not_tag<'x>(&self) -> &'x str;
}

impl<T> LexConfigurationGroupNot for T
where
    T: ConfigurationGroupNot,
{
    fn lex_config_group_not_multi_line<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.config_group_not_multi_line())
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| {
                LexerToken::ConfigurationGroupNot((start, end))
            })
    }
    fn lex_config_group_not<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.config_group_not()).and(position()).map(
            |((start, _), end): ((Position, _), Position)| {
                LexerToken::ConfigurationGroupNot((start, end))
            },
        )
    }

    fn lex_config_group_not_tag<'x>(&self) -> &'x str {
        self.config_group_not_tag()
    }
}
