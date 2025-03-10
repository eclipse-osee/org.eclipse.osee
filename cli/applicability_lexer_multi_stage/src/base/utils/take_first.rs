use std::{cmp::min, marker::PhantomData};

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
        //create a vector of results that aren't None
        let mut result_vec = vec![];
        if let Some(x) = result1 {
            result_vec.push(x)
        };
        if let Some(x) = result2 {
            result_vec.push(x)
        };
        if let Some(x) = result3 {
            result_vec.push(x)
        };
        let tag = result_vec.into_iter().min().unwrap_or(0);

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
            _ => {
                if tag > 0 {
                    Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
                } else {
                    if OM::Incomplete::is_streaming() {
                        Err(Err::Incomplete(Needed::Unknown))
                    } else {
                        Err(Err::Error(OM::Error::bind(|| {
                            let e: ErrorKind = ErrorKind::TakeUntil;
                            Error::from_error_kind(i, e)
                        })))
                    }
                }
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
pub fn take_until_first5<T1, T2, T3, T4, T5, I, Error: ParseError<I>>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
{
    TakeUntilFirst5 {
        tag1,
        tag2,
        tag3,
        tag4,
        tag5,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst5<T1, T2, T3, T4, T5, E> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    e: PhantomData<E>,
}

impl<I, T1, T2, T3, T4, T5, Error: ParseError<I>> Parser<I>
    for TakeUntilFirst5<T1, T2, T3, T4, T5, Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        let result4 = i.find_substring(self.tag4.clone());
        let result5 = i.find_substring(self.tag5.clone());
        match (result1, result2, result3, result4, result5) {
            (None, None, None, None, None) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            (Some(tag1), None, None, None, None) => {
                Ok((i.take_from(tag1), OM::Output::bind(|| i.take(tag1))))
            }
            (None, Some(tag2), None, None, None) => {
                Ok((i.take_from(tag2), OM::Output::bind(|| i.take(tag2))))
            }
            (None, None, Some(tag3), None, None) => {
                Ok((i.take_from(tag3), OM::Output::bind(|| i.take(tag3))))
            }
            (Some(tag1), Some(tag2), None, None, None) => {
                let tag = min(tag1, tag2);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, Some(tag3), None, None) => {
                let tag = min(tag1, tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), Some(tag3), None, None) => {
                let tag = min(tag2, tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), Some(tag3), None, None) => {
                let tag = min(min(tag1, tag2), tag3);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, None, None, Some(tag4), None) => {
                Ok((i.take_from(tag4), OM::Output::bind(|| i.take(tag4))))
            }
            (None, None, Some(tag3), Some(tag4), None) => {
                let tag = min(tag3, tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), None, Some(tag4), None) => {
                let tag = min(tag2, tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), Some(tag3), Some(tag4), None) => {
                let tag = min(min(tag2, tag3), tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, None, Some(tag4), None) => {
                let tag = min(tag1, tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, Some(tag3), Some(tag4), None) => {
                let tag = min(min(tag1, tag3), tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), None, Some(tag4), None) => {
                let tag = min(min(tag1, tag2), tag4);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), Some(tag3), Some(tag4), None) => {
                let tag = min(min(tag1, tag2), min(tag3, tag4));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, None, None, None, Some(tag5)) => {
                Ok((i.take_from(tag5), OM::Output::bind(|| i.take(tag5))))
            }
            (None, None, None, Some(tag4), Some(tag5)) => {
                let tag = min(tag4, tag5);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, None, Some(tag3), None, Some(tag5)) => {
                let tag = min(tag3, tag5);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, None, Some(tag3), Some(tag4), Some(tag5)) => {
                let tag = min(tag3, min(tag4, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), None, None, Some(tag5)) => {
                let tag = min(tag2, tag5);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), None, Some(tag4), Some(tag5)) => {
                let tag = min(tag2, min(tag4, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), Some(tag3), None, Some(tag5)) => {
                let tag = min(tag2, min(tag3, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (None, Some(tag2), Some(tag3), Some(tag4), Some(tag5)) => {
                let tag = min(min(tag2, tag3), min(tag4, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, None, None, Some(tag5)) => {
                let tag = min(tag1, tag5);
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, None, Some(tag4), Some(tag5)) => {
                let tag = min(tag1, min(tag4, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, Some(tag3), None, Some(tag5)) => {
                let tag = min(tag1, min(tag3, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), None, Some(tag3), Some(tag4), Some(tag5)) => {
                let tag = min(min(tag1, tag3), min(tag4, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), None, None, Some(tag5)) => {
                let tag = min(tag1, min(tag2, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), None, Some(tag4), Some(tag5)) => {
                let tag = min(min(tag1, tag2), min(tag4, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), Some(tag3), None, Some(tag5)) => {
                let tag = min(min(tag1, tag2), min(tag3, tag5));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
            (Some(tag1), Some(tag2), Some(tag3), Some(tag4), Some(tag5)) => {
                let tag = min(tag1, min(min(tag2, tag3), min(tag4, tag5)));
                Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
            }
        }
    }
}
pub fn take_until_first6<T1, T2, T3, T4, T5, T6, I, Error: ParseError<I>>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
{
    TakeUntilFirst6 {
        tag1,
        tag2,
        tag3,
        tag4,
        tag5,
        tag6,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst6<T1, T2, T3, T4, T5, T6, E> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    e: PhantomData<E>,
}

impl<I, T1, T2, T3, T4, T5, T6, Error: ParseError<I>> Parser<I>
    for TakeUntilFirst6<T1, T2, T3, T4, T5, T6, Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        let result4 = i.find_substring(self.tag4.clone());
        let result5 = i.find_substring(self.tag5.clone());
        let result6 = i.find_substring(self.tag6.clone());
        //create a vector of results that aren't None
        let mut result_vec = vec![];
        if let Some(x) = result1 {
            result_vec.push(x)
        };
        if let Some(x) = result2 {
            result_vec.push(x)
        };
        if let Some(x) = result3 {
            result_vec.push(x)
        };
        if let Some(x) = result4 {
            result_vec.push(x)
        };
        if let Some(x) = result5 {
            result_vec.push(x)
        };
        if let Some(x) = result6 {
            result_vec.push(x)
        };
        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (result1, result2, result3, result4, result5, result6) {
            (None, None, None, None, None, None) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            _ => {
                if tag > 0 {
                    Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
                } else {
                    if OM::Incomplete::is_streaming() {
                        Err(Err::Incomplete(Needed::Unknown))
                    } else {
                        Err(Err::Error(OM::Error::bind(|| {
                            let e: ErrorKind = ErrorKind::TakeUntil;
                            Error::from_error_kind(i, e)
                        })))
                    }
                }
            }
        }
    }
}
pub fn take_until_first7<T1, T2, T3, T4, T5, T6, T7, I, Error: ParseError<I>>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
{
    TakeUntilFirst7 {
        tag1,
        tag2,
        tag3,
        tag4,
        tag5,
        tag6,
        tag7,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst7<T1, T2, T3, T4, T5, T6, T7, E> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    e: PhantomData<E>,
}

impl<I, T1, T2, T3, T4, T5, T6, T7, Error: ParseError<I>> Parser<I>
    for TakeUntilFirst7<T1, T2, T3, T4, T5, T6, T7, Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        let result4 = i.find_substring(self.tag4.clone());
        let result5 = i.find_substring(self.tag5.clone());
        let result6 = i.find_substring(self.tag6.clone());
        let result7 = i.find_substring(self.tag7.clone());
        //create a vector of results that aren't None
        let mut result_vec = vec![];
        if let Some(x) = result1 {
            result_vec.push(x)
        };
        if let Some(x) = result2 {
            result_vec.push(x)
        };
        if let Some(x) = result3 {
            result_vec.push(x)
        };
        if let Some(x) = result4 {
            result_vec.push(x)
        };
        if let Some(x) = result5 {
            result_vec.push(x)
        };
        if let Some(x) = result6 {
            result_vec.push(x)
        };
        if let Some(x) = result7 {
            result_vec.push(x)
        };
        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7,
        ) {
            (None, None, None, None, None, None, None) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            _ => {
                if tag > 0 {
                    Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
                } else {
                    if OM::Incomplete::is_streaming() {
                        Err(Err::Incomplete(Needed::Unknown))
                    } else {
                        Err(Err::Error(OM::Error::bind(|| {
                            let e: ErrorKind = ErrorKind::TakeUntil;
                            Error::from_error_kind(i, e)
                        })))
                    }
                }
            }
        }
    }
}
pub fn take_until_first8<T1, T2, T3, T4, T5, T6, T7, T8, I, Error: ParseError<I>>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    tag8: T8,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>
        + FindSubstring<T8>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
    T8: Clone,
{
    TakeUntilFirst8 {
        tag1,
        tag2,
        tag3,
        tag4,
        tag5,
        tag6,
        tag7,
        tag8,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst8<T1, T2, T3, T4, T5, T6, T7, T8, E> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    tag8: T8,
    e: PhantomData<E>,
}

impl<I, T1, T2, T3, T4, T5, T6, T7, T8, Error: ParseError<I>> Parser<I>
    for TakeUntilFirst8<T1, T2, T3, T4, T5, T6, T7, T8, Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>
        + FindSubstring<T8>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
    T8: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        let result4 = i.find_substring(self.tag4.clone());
        let result5 = i.find_substring(self.tag5.clone());
        let result6 = i.find_substring(self.tag6.clone());
        let result7 = i.find_substring(self.tag7.clone());
        let result8 = i.find_substring(self.tag8.clone());
        //create a vector of results that aren't None
        let mut result_vec = vec![];
        if let Some(x) = result1 {
            result_vec.push(x)
        };
        if let Some(x) = result2 {
            result_vec.push(x)
        };
        if let Some(x) = result3 {
            result_vec.push(x)
        };
        if let Some(x) = result4 {
            result_vec.push(x)
        };
        if let Some(x) = result5 {
            result_vec.push(x)
        };
        if let Some(x) = result6 {
            result_vec.push(x)
        };
        if let Some(x) = result7 {
            result_vec.push(x)
        };
        if let Some(x) = result8 {
            result_vec.push(x)
        };
        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7, result8,
        ) {
            (None, None, None, None, None, None, None, None) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            _ => {
                if tag > 0 {
                    Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
                } else {
                    if OM::Incomplete::is_streaming() {
                        Err(Err::Incomplete(Needed::Unknown))
                    } else {
                        Err(Err::Error(OM::Error::bind(|| {
                            let e: ErrorKind = ErrorKind::TakeUntil;
                            Error::from_error_kind(i, e)
                        })))
                    }
                }
            }
        }
    }
}
pub fn take_until_first9<T1, T2, T3, T4, T5, T6, T7, T8, T9, I, Error: ParseError<I>>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    tag8: T8,
    tag9: T9,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>
        + FindSubstring<T8>
        + FindSubstring<T9>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
    T8: Clone,
    T9: Clone,
{
    TakeUntilFirst9 {
        tag1,
        tag2,
        tag3,
        tag4,
        tag5,
        tag6,
        tag7,
        tag8,
        tag9,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst9<T1, T2, T3, T4, T5, T6, T7, T8, T9, E> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    tag8: T8,
    tag9: T9,
    e: PhantomData<E>,
}

impl<I, T1, T2, T3, T4, T5, T6, T7, T8, T9, Error: ParseError<I>> Parser<I>
    for TakeUntilFirst9<T1, T2, T3, T4, T5, T6, T7, T8, T9, Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>
        + FindSubstring<T8>
        + FindSubstring<T9>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
    T8: Clone,
    T9: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        let result4 = i.find_substring(self.tag4.clone());
        let result5 = i.find_substring(self.tag5.clone());
        let result6 = i.find_substring(self.tag6.clone());
        let result7 = i.find_substring(self.tag7.clone());
        let result8 = i.find_substring(self.tag8.clone());
        let result9 = i.find_substring(self.tag9.clone());
        //create a vector of results that aren't None
        let mut result_vec = vec![];
        if let Some(x) = result1 {
            result_vec.push(x)
        };
        if let Some(x) = result2 {
            result_vec.push(x)
        };
        if let Some(x) = result3 {
            result_vec.push(x)
        };
        if let Some(x) = result4 {
            result_vec.push(x)
        };
        if let Some(x) = result5 {
            result_vec.push(x)
        };
        if let Some(x) = result6 {
            result_vec.push(x)
        };
        if let Some(x) = result7 {
            result_vec.push(x)
        };
        if let Some(x) = result8 {
            result_vec.push(x)
        };
        if let Some(x) = result9 {
            result_vec.push(x)
        };
        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7, result8, result9,
        ) {
            (None, None, None, None, None, None, None, None, None) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            _ => {
                if tag > 0 {
                    Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
                } else {
                    if OM::Incomplete::is_streaming() {
                        Err(Err::Incomplete(Needed::Unknown))
                    } else {
                        Err(Err::Error(OM::Error::bind(|| {
                            let e: ErrorKind = ErrorKind::TakeUntil;
                            Error::from_error_kind(i, e)
                        })))
                    }
                }
            }
        }
    }
}
//note from this point on, functions are implemented ad-hoc as needed, as 9 seems sufficient for general use.
pub fn take_until_first23<
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    T21,
    T22,
    T23,
    I,
    Error: ParseError<I>,
>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    tag8: T8,
    tag9: T9,
    tag10: T10,
    tag11: T11,
    tag12: T12,
    tag13: T13,
    tag14: T14,
    tag15: T15,
    tag16: T16,
    tag17: T17,
    tag18: T18,
    tag19: T19,
    tag20: T20,
    tag21: T21,
    tag22: T22,
    tag23: T23,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>
        + FindSubstring<T8>
        + FindSubstring<T9>
        + FindSubstring<T10>
        + FindSubstring<T11>
        + FindSubstring<T12>
        + FindSubstring<T13>
        + FindSubstring<T14>
        + FindSubstring<T15>
        + FindSubstring<T16>
        + FindSubstring<T17>
        + FindSubstring<T18>
        + FindSubstring<T19>
        + FindSubstring<T20>
        + FindSubstring<T21>
        + FindSubstring<T22>
        + FindSubstring<T23>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
    T8: Clone,
    T9: Clone,
    T10: Clone,
    T11: Clone,
    T12: Clone,
    T13: Clone,
    T14: Clone,
    T15: Clone,
    T16: Clone,
    T17: Clone,
    T18: Clone,
    T19: Clone,
    T20: Clone,
    T21: Clone,
    T22: Clone,
    T23: Clone,
{
    TakeUntilFirst23 {
        tag1,
        tag2,
        tag3,
        tag4,
        tag5,
        tag6,
        tag7,
        tag8,
        tag9,
        tag10,
        tag11,
        tag12,
        tag13,
        tag14,
        tag15,
        tag16,
        tag17,
        tag18,
        tag19,
        tag20,
        tag21,
        tag22,
        tag23,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst23<
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    T21,
    T22,
    T23,
    E,
> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    tag8: T8,
    tag9: T9,
    tag10: T10,
    tag11: T11,
    tag12: T12,
    tag13: T13,
    tag14: T14,
    tag15: T15,
    tag16: T16,
    tag17: T17,
    tag18: T18,
    tag19: T19,
    tag20: T20,
    tag21: T21,
    tag22: T22,
    tag23: T23,
    e: PhantomData<E>,
}

impl<
        I,
        T1,
        T2,
        T3,
        T4,
        T5,
        T6,
        T7,
        T8,
        T9,
        T10,
        T11,
        T12,
        T13,
        T14,
        T15,
        T16,
        T17,
        T18,
        T19,
        T20,
        T21,
        T22,
        T23,
        Error: ParseError<I>,
    > Parser<I>
    for TakeUntilFirst23<
        T1,
        T2,
        T3,
        T4,
        T5,
        T6,
        T7,
        T8,
        T9,
        T10,
        T11,
        T12,
        T13,
        T14,
        T15,
        T16,
        T17,
        T18,
        T19,
        T20,
        T21,
        T22,
        T23,
        Error,
    >
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>
        + FindSubstring<T8>
        + FindSubstring<T9>
        + FindSubstring<T10>
        + FindSubstring<T11>
        + FindSubstring<T12>
        + FindSubstring<T13>
        + FindSubstring<T14>
        + FindSubstring<T15>
        + FindSubstring<T16>
        + FindSubstring<T17>
        + FindSubstring<T18>
        + FindSubstring<T19>
        + FindSubstring<T20>
        + FindSubstring<T21>
        + FindSubstring<T22>
        + FindSubstring<T23>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
    T8: Clone,
    T9: Clone,
    T10: Clone,
    T11: Clone,
    T12: Clone,
    T13: Clone,
    T14: Clone,
    T15: Clone,
    T16: Clone,
    T17: Clone,
    T18: Clone,
    T19: Clone,
    T20: Clone,
    T21: Clone,
    T22: Clone,
    T23: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        let result4 = i.find_substring(self.tag4.clone());
        let result5 = i.find_substring(self.tag5.clone());
        let result6 = i.find_substring(self.tag6.clone());
        let result7 = i.find_substring(self.tag7.clone());
        let result8 = i.find_substring(self.tag8.clone());
        let result9 = i.find_substring(self.tag9.clone());
        let result10 = i.find_substring(self.tag10.clone());
        let result11 = i.find_substring(self.tag11.clone());
        let result12 = i.find_substring(self.tag12.clone());
        let result13 = i.find_substring(self.tag13.clone());
        let result14 = i.find_substring(self.tag14.clone());
        let result15 = i.find_substring(self.tag15.clone());
        let result16 = i.find_substring(self.tag16.clone());
        let result17 = i.find_substring(self.tag17.clone());
        let result18 = i.find_substring(self.tag18.clone());
        let result19 = i.find_substring(self.tag19.clone());
        let result20 = i.find_substring(self.tag20.clone());
        let result21 = i.find_substring(self.tag21.clone());
        let result22 = i.find_substring(self.tag22.clone());
        let result23 = i.find_substring(self.tag23.clone());

        //create a vector of results that aren't None
        let mut result_vec = vec![];
        if let Some(x) = result1 {
            result_vec.push(x)
        };
        if let Some(x) = result2 {
            result_vec.push(x)
        };
        if let Some(x) = result3 {
            result_vec.push(x)
        };
        if let Some(x) = result4 {
            result_vec.push(x)
        };
        if let Some(x) = result5 {
            result_vec.push(x)
        };
        if let Some(x) = result6 {
            result_vec.push(x)
        };
        if let Some(x) = result7 {
            result_vec.push(x)
        };
        if let Some(x) = result8 {
            result_vec.push(x)
        };
        if let Some(x) = result9 {
            result_vec.push(x)
        };
        if let Some(x) = result10 {
            result_vec.push(x)
        };
        if let Some(x) = result11 {
            result_vec.push(x)
        };
        if let Some(x) = result12 {
            result_vec.push(x)
        };
        if let Some(x) = result13 {
            result_vec.push(x)
        };
        if let Some(x) = result14 {
            result_vec.push(x)
        };
        if let Some(x) = result15 {
            result_vec.push(x)
        };
        if let Some(x) = result16 {
            result_vec.push(x)
        };
        if let Some(x) = result17 {
            result_vec.push(x)
        };
        if let Some(x) = result18 {
            result_vec.push(x)
        };
        if let Some(x) = result19 {
            result_vec.push(x)
        };
        if let Some(x) = result20 {
            result_vec.push(x)
        };
        if let Some(x) = result21 {
            result_vec.push(x)
        };
        if let Some(x) = result22 {
            result_vec.push(x)
        };
        if let Some(x) = result23 {
            result_vec.push(x)
        }
        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7, result8, result9,
            result10, result11, result12, result13, result14, result15, result16, result17,
            result18, result19, result20, result21, result22, result23,
        ) {
            (
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
            ) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            _ => {
                if tag > 0 {
                    Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
                } else {
                    if OM::Incomplete::is_streaming() {
                        Err(Err::Incomplete(Needed::Unknown))
                    } else {
                        Err(Err::Error(OM::Error::bind(|| {
                            let e: ErrorKind = ErrorKind::TakeUntil;
                            Error::from_error_kind(i, e)
                        })))
                    }
                }
            }
        }
    }
}

pub fn take_until_first24<
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    T21,
    T22,
    T23,
    T24,
    I,
    Error: ParseError<I>,
>(
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    tag8: T8,
    tag9: T9,
    tag10: T10,
    tag11: T11,
    tag12: T12,
    tag13: T13,
    tag14: T14,
    tag15: T15,
    tag16: T16,
    tag17: T17,
    tag18: T18,
    tag19: T19,
    tag20: T20,
    tag21: T21,
    tag22: T22,
    tag23: T23,
    tag24: T24,
) -> impl Parser<I, Output = I, Error = Error>
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>
        + FindSubstring<T8>
        + FindSubstring<T9>
        + FindSubstring<T10>
        + FindSubstring<T11>
        + FindSubstring<T12>
        + FindSubstring<T13>
        + FindSubstring<T14>
        + FindSubstring<T15>
        + FindSubstring<T16>
        + FindSubstring<T17>
        + FindSubstring<T18>
        + FindSubstring<T19>
        + FindSubstring<T20>
        + FindSubstring<T21>
        + FindSubstring<T22>
        + FindSubstring<T23>
        + FindSubstring<T24>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
    T8: Clone,
    T9: Clone,
    T10: Clone,
    T11: Clone,
    T12: Clone,
    T13: Clone,
    T14: Clone,
    T15: Clone,
    T16: Clone,
    T17: Clone,
    T18: Clone,
    T19: Clone,
    T20: Clone,
    T21: Clone,
    T22: Clone,
    T23: Clone,
    T24: Clone,
{
    TakeUntilFirst24 {
        tag1,
        tag2,
        tag3,
        tag4,
        tag5,
        tag6,
        tag7,
        tag8,
        tag9,
        tag10,
        tag11,
        tag12,
        tag13,
        tag14,
        tag15,
        tag16,
        tag17,
        tag18,
        tag19,
        tag20,
        tag21,
        tag22,
        tag23,
        tag24,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst24<
    T1,
    T2,
    T3,
    T4,
    T5,
    T6,
    T7,
    T8,
    T9,
    T10,
    T11,
    T12,
    T13,
    T14,
    T15,
    T16,
    T17,
    T18,
    T19,
    T20,
    T21,
    T22,
    T23,
    T24,
    E,
> {
    tag1: T1,
    tag2: T2,
    tag3: T3,
    tag4: T4,
    tag5: T5,
    tag6: T6,
    tag7: T7,
    tag8: T8,
    tag9: T9,
    tag10: T10,
    tag11: T11,
    tag12: T12,
    tag13: T13,
    tag14: T14,
    tag15: T15,
    tag16: T16,
    tag17: T17,
    tag18: T18,
    tag19: T19,
    tag20: T20,
    tag21: T21,
    tag22: T22,
    tag23: T23,
    tag24: T24,
    e: PhantomData<E>,
}

impl<
        I,
        T1,
        T2,
        T3,
        T4,
        T5,
        T6,
        T7,
        T8,
        T9,
        T10,
        T11,
        T12,
        T13,
        T14,
        T15,
        T16,
        T17,
        T18,
        T19,
        T20,
        T21,
        T22,
        T23,
        T24,
        Error: ParseError<I>,
    > Parser<I>
    for TakeUntilFirst24<
        T1,
        T2,
        T3,
        T4,
        T5,
        T6,
        T7,
        T8,
        T9,
        T10,
        T11,
        T12,
        T13,
        T14,
        T15,
        T16,
        T17,
        T18,
        T19,
        T20,
        T21,
        T22,
        T23,
        T24,
        Error,
    >
where
    I: Input
        + FindSubstring<T1>
        + FindSubstring<T2>
        + FindSubstring<T3>
        + FindSubstring<T4>
        + FindSubstring<T5>
        + FindSubstring<T6>
        + FindSubstring<T7>
        + FindSubstring<T8>
        + FindSubstring<T9>
        + FindSubstring<T10>
        + FindSubstring<T11>
        + FindSubstring<T12>
        + FindSubstring<T13>
        + FindSubstring<T14>
        + FindSubstring<T15>
        + FindSubstring<T16>
        + FindSubstring<T17>
        + FindSubstring<T18>
        + FindSubstring<T19>
        + FindSubstring<T20>
        + FindSubstring<T21>
        + FindSubstring<T22>
        + FindSubstring<T23>
        + FindSubstring<T24>,
    T1: Clone,
    T2: Clone,
    T3: Clone,
    T4: Clone,
    T5: Clone,
    T6: Clone,
    T7: Clone,
    T8: Clone,
    T9: Clone,
    T10: Clone,
    T11: Clone,
    T12: Clone,
    T13: Clone,
    T14: Clone,
    T15: Clone,
    T16: Clone,
    T17: Clone,
    T18: Clone,
    T19: Clone,
    T20: Clone,
    T21: Clone,
    T22: Clone,
    T23: Clone,
    T24: Clone,
{
    type Output = I;
    type Error = Error;

    fn process<OM: OutputMode>(&mut self, i: I) -> PResult<OM, I, Self::Output, Self::Error> {
        let result1 = i.find_substring(self.tag1.clone());
        let result2 = i.find_substring(self.tag2.clone());
        let result3 = i.find_substring(self.tag3.clone());
        let result4 = i.find_substring(self.tag4.clone());
        let result5 = i.find_substring(self.tag5.clone());
        let result6 = i.find_substring(self.tag6.clone());
        let result7 = i.find_substring(self.tag7.clone());
        let result8 = i.find_substring(self.tag8.clone());
        let result9 = i.find_substring(self.tag9.clone());
        let result10 = i.find_substring(self.tag10.clone());
        let result11 = i.find_substring(self.tag11.clone());
        let result12 = i.find_substring(self.tag12.clone());
        let result13 = i.find_substring(self.tag13.clone());
        let result14 = i.find_substring(self.tag14.clone());
        let result15 = i.find_substring(self.tag15.clone());
        let result16 = i.find_substring(self.tag16.clone());
        let result17 = i.find_substring(self.tag17.clone());
        let result18 = i.find_substring(self.tag18.clone());
        let result19 = i.find_substring(self.tag19.clone());
        let result20 = i.find_substring(self.tag20.clone());
        let result21 = i.find_substring(self.tag21.clone());
        let result22 = i.find_substring(self.tag22.clone());
        let result23 = i.find_substring(self.tag23.clone());
        let result24 = i.find_substring(self.tag24.clone());

        //create a vector of results that aren't None
        let mut result_vec = vec![];
        if let Some(x) = result1 {
            result_vec.push(x)
        };
        if let Some(x) = result2 {
            result_vec.push(x)
        };
        if let Some(x) = result3 {
            result_vec.push(x)
        };
        if let Some(x) = result4 {
            result_vec.push(x)
        };
        if let Some(x) = result5 {
            result_vec.push(x)
        };
        if let Some(x) = result6 {
            result_vec.push(x)
        };
        if let Some(x) = result7 {
            result_vec.push(x)
        };
        if let Some(x) = result8 {
            result_vec.push(x)
        };
        if let Some(x) = result9 {
            result_vec.push(x)
        };
        if let Some(x) = result10 {
            result_vec.push(x)
        };
        if let Some(x) = result11 {
            result_vec.push(x)
        };
        if let Some(x) = result12 {
            result_vec.push(x)
        };
        if let Some(x) = result13 {
            result_vec.push(x)
        };
        if let Some(x) = result14 {
            result_vec.push(x)
        };
        if let Some(x) = result15 {
            result_vec.push(x)
        };
        if let Some(x) = result16 {
            result_vec.push(x)
        };
        if let Some(x) = result17 {
            result_vec.push(x)
        };
        if let Some(x) = result18 {
            result_vec.push(x)
        };
        if let Some(x) = result19 {
            result_vec.push(x)
        };
        if let Some(x) = result20 {
            result_vec.push(x)
        };
        if let Some(x) = result21 {
            result_vec.push(x)
        };
        if let Some(x) = result22 {
            result_vec.push(x)
        };
        if let Some(x) = result23 {
            result_vec.push(x)
        }
        if let Some(x) = result24 {
            result_vec.push(x)
        };

        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7, result8, result9,
            result10, result11, result12, result13, result14, result15, result16, result17,
            result18, result19, result20, result21, result22, result23, result24,
        ) {
            (
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
                None,
            ) => {
                if OM::Incomplete::is_streaming() {
                    Err(Err::Incomplete(Needed::Unknown))
                } else {
                    Err(Err::Error(OM::Error::bind(|| {
                        let e: ErrorKind = ErrorKind::TakeUntil;
                        Error::from_error_kind(i, e)
                    })))
                }
            }
            _ => {
                if tag > 0 {
                    Ok((i.take_from(tag), OM::Output::bind(|| i.take(tag))))
                } else {
                    if OM::Incomplete::is_streaming() {
                        Err(Err::Incomplete(Needed::Unknown))
                    } else {
                        Err(Err::Error(OM::Error::bind(|| {
                            let e: ErrorKind = ErrorKind::TakeUntil;
                            Error::from_error_kind(i, e)
                        })))
                    }
                }
            }
        }
    }
}
