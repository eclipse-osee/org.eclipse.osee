use std::marker::PhantomData;

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
        // match i.find_substring(self.tag.clone()) {
        //   None => {
        //     if OM::Incomplete::is_streaming() {
        //       Err(Err::Incomplete(Needed::Unknown))
        //     } else {
        //       Err(Err::Error(OM::Error::bind(|| {
        //         let e: ErrorKind = ErrorKind::TakeFirst;
        //         Error::from_error_kind(i, e)
        //       })))
        //     }
        //   }
        //   Some(index) => Ok((i.take_from(index), OM::Output::bind(|| i.take(index)))),
        // }
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
        // match i.find_substring(self.tag.clone()) {
        //   None => {
        //     if OM::Incomplete::is_streaming() {
        //       Err(Err::Incomplete(Needed::Unknown))
        //     } else {
        //       Err(Err::Error(OM::Error::bind(|| {
        //         let e: ErrorKind = ErrorKind::TakeFirst;
        //         Error::from_error_kind(i, e)
        //       })))
        //     }
        //   }
        //   Some(index) => Ok((i.take_from(index), OM::Output::bind(|| i.take(index)))),
        // }
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
                if tag1 > tag2 {
                    Ok((i.take_from(tag2), OM::Output::bind(|| i.take(tag2))))
                } else {
                    Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1))))
                }
            }
            (Some(tag1), None, Some(tag3)) => {
                if tag1 > tag3 {
                    Ok((i.take_from(tag3), OM::Output::bind(|| i.take(tag3))))
                } else {
                    Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1))))
                }
            }
            (None, Some(tag2), Some(tag3)) => {
                if tag2 > tag3 {
                    Ok((i.take_from(tag3), OM::Output::bind(|| i.take(tag3))))
                } else {
                    Ok((i.take_from(tag2), OM::Output::bind(|| i.take(tag2))))
                }
            }
            (Some(tag1), Some(tag2), Some(tag3)) => {
                if tag1 > tag2 && tag2 > tag3 {
                    Ok((i.take_from(tag3), OM::Output::bind(|| i.take(tag3))))
                } else if tag1 > tag2 {
                    Ok((i.take_from(tag2), OM::Output::bind(|| i.take(tag2))))
                } else if tag2 > tag1 && tag1 > tag3 {
                    Ok((i.take_from(tag3), OM::Output::bind(|| i.take(tag3))))
                } else if tag2 > tag1 {
                    Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1))))
                } else {
                    Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1))))
                }
            }
        }
    }
}
