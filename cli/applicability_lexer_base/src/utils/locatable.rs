use std::marker::PhantomData;

use nom::{
    error::{ErrorKind, ParseError},
    Err, Input, IsStreaming, Mode, Parser,
};
use nom_locate::LocatedSpan;

pub trait Locatable {
    fn get_position(&self) -> (usize, u32);
}

impl<T, X> Locatable for LocatedSpan<T, X> {
    fn get_position(&self) -> (usize, u32) {
        return (self.location_offset(), self.location_line());
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

// impl<'x> From<LocatedSpan<&'x str>> for String {
//     fn from(value: LocatedSpan<&'x str>) -> Self {
//         value.into()
//     }
// }
