use std::marker::PhantomData;

use nom::{error::ParseError, Input, Mode, OutputMode, PResult, Parser};

pub fn success_no_value<I, E: ParseError<I>>() -> impl Parser<I, Output = I, Error = E>
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
