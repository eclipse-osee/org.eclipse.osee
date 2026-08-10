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
use memchr::memmem;
use nom::{
    AsBytes, AsChar, Compare, FindSubstring, Input, Parser,
    bytes::{take_till, take_until},
    error::ParseError,
};

pub trait NewLine {
    fn is_new_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn new_line<'x, I, O, E>(&self) -> impl Parser<I, Output = O, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        O: From<I>,
        E: ParseError<I>;
    fn take_till_new_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_new_line::<I>(x))
    }
    fn new_line_tag<'x>(&self) -> &'x str {
        "\n"
    }
    fn take_until_new_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.new_line_tag())
    }
    fn new_line_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + AsBytes,
    {
        let finder = memmem::Finder::new(self.new_line_tag());
        finder.find(input.as_bytes())
    }
}
