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
use nom::{AsChar, Compare, FindSubstring, Input, Parser, error::ParseError, multi::many0};

use crate::utils::tag_terminated::TagTerminated;
use applicability_lexer_applicability_structure_base::{
    config_group::not::LexConfigurationGroupNot,
    delimiters::{space::LexSpace, tab::LexTab},
};
use applicability_lexer_base::{applicability_structure::LexerToken, utils::locatable::Locatable};

pub trait ConfigGroupNotSingleLineTerminated {
    fn config_group_not_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> ConfigGroupNotSingleLineTerminated for T
where
    T: TagTerminated + LexConfigurationGroupNot + LexSpace + LexTab,
{
    fn config_group_not_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<I>>, Error = E>
    where
        I: Input
            + for<'x> FindSubstring<&'x str>
            + for<'x> Compare<&'x str>
            + Locatable
            + Send
            + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let tag = self.terminated_tag();

        self.lex_config_group_not()
            .and(many0(self.lex_space().or(self.lex_tab())))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t);
                spaces
            })
    }
}
