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
use nom::{Parser, combinator::fail, error::ParseError};

pub fn cond_with_failure<I, E: ParseError<I>, P>(
    condition: bool,
    parser: P,
) -> impl Parser<I, Output = P::Output, Error = E>
where
    P: Parser<I, Error = E>,
{
    CondWithFailure { condition, parser }
}

pub struct CondWithFailure<P> {
    condition: bool,
    parser: P,
}

impl<I, P> Parser<I> for CondWithFailure<P>
where
    P: Parser<I>,
{
    type Output = P::Output;

    type Error = P::Error;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        match self.condition {
            true => self.parser.process::<OM>(input),
            false => fail::<I, P::Output, P::Error>().process::<OM>(input),
        }
    }
}
