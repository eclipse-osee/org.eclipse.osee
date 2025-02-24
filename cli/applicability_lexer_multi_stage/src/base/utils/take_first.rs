use std::{
    cmp::{max, min},
    marker::PhantomData,
};

use nom::{
    error::{ErrorKind, ParseError},
    Err, FindSubstring, Input, IsStreaming, Mode, Needed, OutputMode, PResult, Parser,
};

pub fn take_until_first2<T1, T2, I, Error: ParseError<I>>(
    tag1: T1,
    tag2: T2,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input + FindSubstring<T1> + FindSubstring<T2>,
    T1: Clone,
    T2: Clone,
{
    TakeUntilFirst2 {
        tag1,
        tag2,
        e: PhantomData,
    }
}

pub struct TakeUntilFirst2<T1, T2, E> {
    tag1: T1,
    tag2: T2,
    e: PhantomData<E>,
}

impl<I, T1, T2, Error: ParseError<I>> Parser<I> for TakeUntilFirst2<T1, T2, Error>
where
    I: Input + FindSubstring<T1> + FindSubstring<T2>,
    T1: Clone,
    T2: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        match (result1, result2) {
            (None, None) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            (None, Some(tag2)) => Ok((i.take_from(tag2), OM::Output::bind(|| i.take(tag2)))),
            (Some(tag1), None) => Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1)))),
            (Some(tag1), Some(tag2)) => {
                if tag1 > tag2 {
                    Ok((i.take_from(tag2), OM::Output::bind(|| i.take(tag2))))
                } else {
                    Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1))))
                }
            }
        }
    }
}
pub fn take_until_first3<T1, T2, T3, I, Error: ParseError<I>>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input + FindSubstring<T1> + FindSubstring<T2> + FindSubstring<T3>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
{
    TakeUntilFirst3 {
        tag1,
        tag2,
        tag3,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst3<T1, T2, T3, E> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    e: PhantomData<E>,
}

impl<I, T1, T2, T3, Error: ParseError<I>> Parser<I> for TakeUntilFirst3<T1, T2, T3, Error>
where
    I: Input + FindSubstring<T1> + FindSubstring<T2> + FindSubstring<T3>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        match (result1, result2, result3) {
            (None, None, None) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            (Some(tag1), None, None) => Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1)))),
            (None, Some(tag2), None) => Ok((i.take_from(tag2), OM::Output::bind(|| i.take(tag2)))),
            (None, None, Some(tag3)) => Ok((i.take_from(tag3), OM::Output::bind(|| i.take(tag3)))),
            (Some(tag1), Some(tag2), None) => {
                let tag = min(tag1, tag2);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, Some(tag3)) => {
                let tag = min(tag1, tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), Some(tag3)) => {
                let tag = min(tag2, tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), Some(tag3)) => {
                let tag = min(min(tag1, tag2), tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
        }
    }
}
pub fn take_until_first4<T1, T2, T3, T4, I, Error: ParseError<I>>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input + FindSubstring<T1> + FindSubstring<T2> + FindSubstring<T3> + FindSubstring<T4>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
{
    TakeUntilFirst4 {
        tag1,
        tag2,
        tag3,
        tag4,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst4<T1, T2, T3, T4, E> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    e: PhantomData<E>,
}

impl<I, T1, T2, T3, T4, Error: ParseError<I>> Parser<I> for TakeUntilFirst4<T1, T2, T3, T4, Error>
where
    I: Input + FindSubstring<T1> + FindSubstring<T2> + FindSubstring<T3> + FindSubstring<T4>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        let result4 = i.find_substring(self.tag4.clone());
        match (result1, result2, result3, result4) {
            (None, None, None, None) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            (Some(tag1), None, None, None) => {
                Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1))))
            }
            (None, Some(tag2), None, None) => {
                Ok((i.take_from(tag2), OM::Output::bind(|| i.take(tag2))))
            }
            (None, None, Some(tag3), None) => {
                Ok((i.take_from(tag3), OM::Output::bind(|| i.take(tag3))))
            }
            (Some(tag1), Some(tag2), None, None) => {
                let tag = min(tag1, tag2);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, Some(tag3), None) => {
                let tag = min(tag1, tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), Some(tag3), None) => {
                let tag = min(tag2, tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), Some(tag3), None) => {
                let tag = min(min(tag1, tag2), tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, None, None, Some(tag4)) => {
                Ok((i.take_from(tag4), OM::Output::bind(|| i.take(tag4))))
            }
            (None, None, Some(tag3), Some(tag4)) => {
                let tag = min(tag3, tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), None, Some(tag4)) => {
                let tag = min(tag2, tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), Some(tag3), Some(tag4)) => {
                let tag = min(min(tag2, tag3), tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, None, Some(tag4)) => {
                let tag = min(tag1, tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, Some(tag3), Some(tag4)) => {
                let tag = min(min(tag1, tag3), tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), None, Some(tag4)) => {
                let tag = min(min(tag1, tag2), tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), Some(tag3), Some(tag4)) => {
                let tag = min(min(tag1, tag2), min(tag3, tag4));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
        }
    }
}
