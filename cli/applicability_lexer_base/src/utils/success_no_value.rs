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
use std::marker::PhantomData;

use nom::{Input, Mode, OutputMode, PResult, Parser, error::ParseError};

pub fn success_no_value<I, E>() -> impl Parser<I, Output = I, Error = E>
where
    I: Input,
    E: ParseError<I>,
{
    SuccessNoValue { e: PhantomData }
}

/// Parser implementation for [success_no_value]
pub struct SuccessNoValue<E> {
    e: PhantomData<E>,
}

impl<I, E> Parser<I> for SuccessNoValue<E>
where
    I: Input,
    E: ParseError<I>,
{
    type Output = I;
    type Error = E;

    fn process<OM: OutputMode>(&mut self, input: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let return_val = input.clone();
        Ok((input, OM::Output::bind(|| return_val.take(0))))
    }
}
