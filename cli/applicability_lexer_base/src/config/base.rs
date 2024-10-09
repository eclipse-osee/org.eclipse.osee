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
use nom::{
    AsChar, Compare, FindSubstring, Input, Parser,
    bytes::{tag, take_until},
    error::ParseError,
};

pub trait ConfigurationBase {
    fn config_base<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.config_base_tag())
    }
    fn config_base_multi_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.config_base_tag_nl())
            .or(tag(self.config_base_tag_cr()))
            .or(tag(self.config_base_tag_t()))
            .or(tag(self.config_base_tag_s()))
    }
    fn config_base_tag<'x>(&self) -> &'x str {
        "Configuration"
    }
    fn config_base_tag_nl<'x>(&self) -> &'x str {
        "\nConfiguration"
    }
    fn config_base_tag_cr<'x>(&self) -> &'x str {
        "\r\nConfiguration"
    }
    fn config_base_tag_t<'x>(&self) -> &'x str {
        "\tConfiguration"
    }
    fn config_base_tag_s<'x>(&self) -> &'x str {
        " Configuration"
    }
    fn take_until_config_base<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.config_base_tag())
    }
}
