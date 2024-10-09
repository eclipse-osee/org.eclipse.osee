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

use nom::{
    Err, Input, IsStreaming, Mode, Parser,
    error::{ErrorKind, ParseError},
};
use nom_locate::LocatedSpan;

pub trait Locatable {
    fn get_position(&self) -> (usize, u32);
}

impl<T, X> Locatable for LocatedSpan<T, X> {
    fn get_position(&self) -> (usize, u32) {
        (self.location_offset(), self.location_line())
    }
}

pub fn position<I, Error: ParseError<I>>() -> impl Parser<I, Output = (usize, u32), Error = Error>
where
    I: Input + Locatable,
{
    PositionData { e: PhantomData }
}

pub struct PositionData<E> {
    e: PhantomData<E>,
}

impl<I, Error: ParseError<I>> Parser<I> for PositionData<Error>
where
    I: Input + Locatable,
{
    type Output = (usize, u32);

    type Error = Error;

    fn process<OM: nom::OutputMode>(
        &mut self,
        input: I,
    ) -> nom::PResult<OM, I, Self::Output, Self::Error> {
        match input.slice_index(0) {
            Err(needed) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(needed))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::Eof;
                        Error::from_error_kind(input, e)
                    })))
                }
            }
            Ok(index) => Ok((
                input.take_from(index),
                OM::Output::bind(|| input.get_position()),
            )),
        }
    }
}
