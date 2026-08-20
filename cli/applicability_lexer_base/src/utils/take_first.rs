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
use std::{cmp::min, marker::PhantomData};

use nom::{
    Err, FindSubstring, Input, IsStreaming, Mode, Needed, OutputMode, PResult, Parser,
    error::{ErrorKind, ParseError},
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
                } else if OM::Incomplete::is_streaming() {
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
                } else if OM::Incomplete::is_streaming() {
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
                } else if OM::Incomplete::is_streaming() {
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
#[allow(clippy::too_many_arguments)]
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
                } else if OM::Incomplete::is_streaming() {
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
#[allow(clippy::too_many_arguments)]
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
                } else if OM::Incomplete::is_streaming() {
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
#[allow(clippy::too_many_arguments)]
pub fn take_until_first10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, I, Error: ParseError<I>>(
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
        + FindSubstring<T10>,
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
{
    TakeUntilFirst10 {
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
        e: PhantomData,
    }
}
pub struct TakeUntilFirst10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, E> {
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
    e: PhantomData<E>,
}

impl<I, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Error: ParseError<I>> Parser<I>
    for TakeUntilFirst10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, Error>
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
        + FindSubstring<T10>,
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
        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7, result8, result9,
            result10,
        ) {
            (None, None, None, None, None, None, None, None, None, None) => {
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
                } else if OM::Incomplete::is_streaming() {
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
//note from this point on, functions are implemented ad-hoc as needed, as 10 seems sufficient for general use.
#[allow(clippy::too_many_arguments)]
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
                } else if OM::Incomplete::is_streaming() {
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
#[allow(clippy::too_many_arguments)]
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
                } else if OM::Incomplete::is_streaming() {
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

#[allow(clippy::too_many_arguments)]
pub fn take_until_first35<
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
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
    tag25: T25,
    tag26: T26,
    tag27: T27,
    tag28: T28,
    tag29: T29,
    tag30: T30,
    tag31: T31,
    tag32: T32,
    tag33: T33,
    tag34: T34,
    tag35: T35,
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
        + FindSubstring<T24>
        + FindSubstring<T25>
        + FindSubstring<T26>
        + FindSubstring<T27>
        + FindSubstring<T28>
        + FindSubstring<T29>
        + FindSubstring<T30>
        + FindSubstring<T31>
        + FindSubstring<T32>
        + FindSubstring<T33>
        + FindSubstring<T34>
        + FindSubstring<T35>,
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
    T25: Clone,
    T26: Clone,
    T27: Clone,
    T28: Clone,
    T29: Clone,
    T30: Clone,
    T31: Clone,
    T32: Clone,
    T33: Clone,
    T34: Clone,
    T35: Clone,
{
    TakeUntilFirst35 {
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
        tag25,
        tag26,
        tag27,
        tag28,
        tag29,
        tag30,
        tag31,
        tag32,
        tag33,
        tag34,
        tag35,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst35<
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
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
    tag25: T25,
    tag26: T26,
    tag27: T27,
    tag28: T28,
    tag29: T29,
    tag30: T30,
    tag31: T31,
    tag32: T32,
    tag33: T33,
    tag34: T34,
    tag35: T35,
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
    Error: ParseError<I>,
> Parser<I>
    for TakeUntilFirst35<
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
        T25,
        T26,
        T27,
        T28,
        T29,
        T30,
        T31,
        T32,
        T33,
        T34,
        T35,
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
        + FindSubstring<T24>
        + FindSubstring<T25>
        + FindSubstring<T26>
        + FindSubstring<T27>
        + FindSubstring<T28>
        + FindSubstring<T29>
        + FindSubstring<T30>
        + FindSubstring<T31>
        + FindSubstring<T32>
        + FindSubstring<T33>
        + FindSubstring<T34>
        + FindSubstring<T35>,
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
    T25: Clone,
    T26: Clone,
    T27: Clone,
    T28: Clone,
    T29: Clone,
    T30: Clone,
    T31: Clone,
    T32: Clone,
    T33: Clone,
    T34: Clone,
    T35: Clone,
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
        let result25 = i.find_substring(self.tag25.clone());
        let result26 = i.find_substring(self.tag26.clone());
        let result27 = i.find_substring(self.tag27.clone());
        let result28 = i.find_substring(self.tag28.clone());
        let result29 = i.find_substring(self.tag29.clone());
        let result30 = i.find_substring(self.tag30.clone());
        let result31 = i.find_substring(self.tag31.clone());
        let result32 = i.find_substring(self.tag32.clone());
        let result33 = i.find_substring(self.tag33.clone());
        let result34 = i.find_substring(self.tag34.clone());
        let result35 = i.find_substring(self.tag35.clone());

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
        if let Some(x) = result25 {
            result_vec.push(x)
        };
        if let Some(x) = result26 {
            result_vec.push(x)
        };
        if let Some(x) = result27 {
            result_vec.push(x)
        };
        if let Some(x) = result28 {
            result_vec.push(x)
        };
        if let Some(x) = result29 {
            result_vec.push(x)
        };
        if let Some(x) = result30 {
            result_vec.push(x)
        };
        if let Some(x) = result31 {
            result_vec.push(x)
        };
        if let Some(x) = result32 {
            result_vec.push(x)
        };
        if let Some(x) = result33 {
            result_vec.push(x)
        };
        if let Some(x) = result34 {
            result_vec.push(x)
        };
        if let Some(x) = result35 {
            result_vec.push(x)
        };

        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7, result8, result9,
            result10, result11, result12, result13, result14, result15, result16, result17,
            result18, result19, result20, result21, result22, result23, result24, result25,
            result26, result27, result28, result29, result30, result31, result32, result33,
            result34, result35,
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
                } else if OM::Incomplete::is_streaming() {
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

#[allow(clippy::too_many_arguments)]
pub fn take_until_first59<
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
    T36,
    T37,
    T38,
    T39,
    T40,
    T41,
    T42,
    T43,
    T44,
    T45,
    T46,
    T47,
    T48,
    T49,
    T50,
    T51,
    T52,
    T53,
    T54,
    T55,
    T56,
    T57,
    T58,
    T59,
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
    tag25: T25,
    tag26: T26,
    tag27: T27,
    tag28: T28,
    tag29: T29,
    tag30: T30,
    tag31: T31,
    tag32: T32,
    tag33: T33,
    tag34: T34,
    tag35: T35,
    tag36: T36,
    tag37: T37,
    tag38: T38,
    tag39: T39,
    tag40: T40,
    tag41: T41,
    tag42: T42,
    tag43: T43,
    tag44: T44,
    tag45: T45,
    tag46: T46,
    tag47: T47,
    tag48: T48,
    tag49: T49,
    tag50: T50,
    tag51: T51,
    tag52: T52,
    tag53: T53,
    tag54: T54,
    tag55: T55,
    tag56: T56,
    tag57: T57,
    tag58: T58,
    tag59: T59,
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
        + FindSubstring<T24>
        + FindSubstring<T25>
        + FindSubstring<T26>
        + FindSubstring<T27>
        + FindSubstring<T28>
        + FindSubstring<T29>
        + FindSubstring<T30>
        + FindSubstring<T31>
        + FindSubstring<T32>
        + FindSubstring<T33>
        + FindSubstring<T34>
        + FindSubstring<T35>
        + FindSubstring<T36>
        + FindSubstring<T37>
        + FindSubstring<T38>
        + FindSubstring<T39>
        + FindSubstring<T40>
        + FindSubstring<T41>
        + FindSubstring<T42>
        + FindSubstring<T43>
        + FindSubstring<T44>
        + FindSubstring<T45>
        + FindSubstring<T46>
        + FindSubstring<T47>
        + FindSubstring<T48>
        + FindSubstring<T49>
        + FindSubstring<T50>
        + FindSubstring<T51>
        + FindSubstring<T52>
        + FindSubstring<T53>
        + FindSubstring<T54>
        + FindSubstring<T55>
        + FindSubstring<T56>
        + FindSubstring<T57>
        + FindSubstring<T58>
        + FindSubstring<T59>,
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
    T25: Clone,
    T26: Clone,
    T27: Clone,
    T28: Clone,
    T29: Clone,
    T30: Clone,
    T31: Clone,
    T32: Clone,
    T33: Clone,
    T34: Clone,
    T35: Clone,
    T36: Clone,
    T37: Clone,
    T38: Clone,
    T39: Clone,
    T40: Clone,
    T41: Clone,
    T42: Clone,
    T43: Clone,
    T44: Clone,
    T45: Clone,
    T46: Clone,
    T47: Clone,
    T48: Clone,
    T49: Clone,
    T50: Clone,
    T51: Clone,
    T52: Clone,
    T53: Clone,
    T54: Clone,
    T55: Clone,
    T56: Clone,
    T57: Clone,
    T58: Clone,
    T59: Clone,
{
    TakeUntilFirst59 {
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
        tag25,
        tag26,
        tag27,
        tag28,
        tag29,
        tag30,
        tag31,
        tag32,
        tag33,
        tag34,
        tag35,
        tag36,
        tag37,
        tag38,
        tag39,
        tag40,
        tag41,
        tag42,
        tag43,
        tag44,
        tag45,
        tag46,
        tag47,
        tag48,
        tag49,
        tag50,
        tag51,
        tag52,
        tag53,
        tag54,
        tag55,
        tag56,
        tag57,
        tag58,
        tag59,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst59<
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
    T36,
    T37,
    T38,
    T39,
    T40,
    T41,
    T42,
    T43,
    T44,
    T45,
    T46,
    T47,
    T48,
    T49,
    T50,
    T51,
    T52,
    T53,
    T54,
    T55,
    T56,
    T57,
    T58,
    T59,
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
    tag25: T25,
    tag26: T26,
    tag27: T27,
    tag28: T28,
    tag29: T29,
    tag30: T30,
    tag31: T31,
    tag32: T32,
    tag33: T33,
    tag34: T34,
    tag35: T35,
    tag36: T36,
    tag37: T37,
    tag38: T38,
    tag39: T39,
    tag40: T40,
    tag41: T41,
    tag42: T42,
    tag43: T43,
    tag44: T44,
    tag45: T45,
    tag46: T46,
    tag47: T47,
    tag48: T48,
    tag49: T49,
    tag50: T50,
    tag51: T51,
    tag52: T52,
    tag53: T53,
    tag54: T54,
    tag55: T55,
    tag56: T56,
    tag57: T57,
    tag58: T58,
    tag59: T59,
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
    T36,
    T37,
    T38,
    T39,
    T40,
    T41,
    T42,
    T43,
    T44,
    T45,
    T46,
    T47,
    T48,
    T49,
    T50,
    T51,
    T52,
    T53,
    T54,
    T55,
    T56,
    T57,
    T58,
    T59,
    Error: ParseError<I>,
> Parser<I>
    for TakeUntilFirst59<
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
        T25,
        T26,
        T27,
        T28,
        T29,
        T30,
        T31,
        T32,
        T33,
        T34,
        T35,
        T36,
        T37,
        T38,
        T39,
        T40,
        T41,
        T42,
        T43,
        T44,
        T45,
        T46,
        T47,
        T48,
        T49,
        T50,
        T51,
        T52,
        T53,
        T54,
        T55,
        T56,
        T57,
        T58,
        T59,
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
        + FindSubstring<T24>
        + FindSubstring<T25>
        + FindSubstring<T26>
        + FindSubstring<T27>
        + FindSubstring<T28>
        + FindSubstring<T29>
        + FindSubstring<T30>
        + FindSubstring<T31>
        + FindSubstring<T32>
        + FindSubstring<T33>
        + FindSubstring<T34>
        + FindSubstring<T35>
        + FindSubstring<T36>
        + FindSubstring<T37>
        + FindSubstring<T38>
        + FindSubstring<T39>
        + FindSubstring<T40>
        + FindSubstring<T41>
        + FindSubstring<T42>
        + FindSubstring<T43>
        + FindSubstring<T44>
        + FindSubstring<T45>
        + FindSubstring<T46>
        + FindSubstring<T47>
        + FindSubstring<T48>
        + FindSubstring<T49>
        + FindSubstring<T50>
        + FindSubstring<T51>
        + FindSubstring<T52>
        + FindSubstring<T53>
        + FindSubstring<T54>
        + FindSubstring<T55>
        + FindSubstring<T56>
        + FindSubstring<T57>
        + FindSubstring<T58>
        + FindSubstring<T59>,
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
    T25: Clone,
    T26: Clone,
    T27: Clone,
    T28: Clone,
    T29: Clone,
    T30: Clone,
    T31: Clone,
    T32: Clone,
    T33: Clone,
    T34: Clone,
    T35: Clone,
    T36: Clone,
    T37: Clone,
    T38: Clone,
    T39: Clone,
    T40: Clone,
    T41: Clone,
    T42: Clone,
    T43: Clone,
    T44: Clone,
    T45: Clone,
    T46: Clone,
    T47: Clone,
    T48: Clone,
    T49: Clone,
    T50: Clone,
    T51: Clone,
    T52: Clone,
    T53: Clone,
    T54: Clone,
    T55: Clone,
    T56: Clone,
    T57: Clone,
    T58: Clone,
    T59: Clone,
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
        let result25 = i.find_substring(self.tag25.clone());
        let result26 = i.find_substring(self.tag26.clone());
        let result27 = i.find_substring(self.tag27.clone());
        let result28 = i.find_substring(self.tag28.clone());
        let result29 = i.find_substring(self.tag29.clone());
        let result30 = i.find_substring(self.tag30.clone());
        let result31 = i.find_substring(self.tag31.clone());
        let result32 = i.find_substring(self.tag32.clone());
        let result33 = i.find_substring(self.tag33.clone());
        let result34 = i.find_substring(self.tag34.clone());
        let result35 = i.find_substring(self.tag35.clone());
        let result36 = i.find_substring(self.tag36.clone());
        let result37 = i.find_substring(self.tag37.clone());
        let result38 = i.find_substring(self.tag38.clone());
        let result39 = i.find_substring(self.tag39.clone());
        let result40 = i.find_substring(self.tag40.clone());
        let result41 = i.find_substring(self.tag41.clone());
        let result42 = i.find_substring(self.tag42.clone());
        let result43 = i.find_substring(self.tag43.clone());
        let result44 = i.find_substring(self.tag44.clone());
        let result45 = i.find_substring(self.tag45.clone());
        let result46 = i.find_substring(self.tag46.clone());
        let result47 = i.find_substring(self.tag47.clone());
        let result48 = i.find_substring(self.tag48.clone());
        let result49 = i.find_substring(self.tag49.clone());
        let result50 = i.find_substring(self.tag50.clone());
        let result51 = i.find_substring(self.tag51.clone());
        let result52 = i.find_substring(self.tag52.clone());
        let result53 = i.find_substring(self.tag53.clone());
        let result54 = i.find_substring(self.tag54.clone());
        let result55 = i.find_substring(self.tag55.clone());
        let result56 = i.find_substring(self.tag56.clone());
        let result57 = i.find_substring(self.tag57.clone());
        let result58 = i.find_substring(self.tag58.clone());
        let result59 = i.find_substring(self.tag59.clone());

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
        };
        if let Some(x) = result24 {
            result_vec.push(x)
        };
        if let Some(x) = result25 {
            result_vec.push(x)
        };
        if let Some(x) = result26 {
            result_vec.push(x)
        };
        if let Some(x) = result27 {
            result_vec.push(x)
        };
        if let Some(x) = result28 {
            result_vec.push(x)
        };
        if let Some(x) = result29 {
            result_vec.push(x)
        };
        if let Some(x) = result30 {
            result_vec.push(x)
        };
        if let Some(x) = result31 {
            result_vec.push(x)
        };
        if let Some(x) = result32 {
            result_vec.push(x)
        };
        if let Some(x) = result33 {
            result_vec.push(x)
        };
        if let Some(x) = result34 {
            result_vec.push(x)
        };
        if let Some(x) = result35 {
            result_vec.push(x)
        };
        if let Some(x) = result36 {
            result_vec.push(x)
        };
        if let Some(x) = result37 {
            result_vec.push(x)
        };
        if let Some(x) = result38 {
            result_vec.push(x)
        };
        if let Some(x) = result39 {
            result_vec.push(x)
        };
        if let Some(x) = result40 {
            result_vec.push(x)
        };
        if let Some(x) = result41 {
            result_vec.push(x)
        };
        if let Some(x) = result42 {
            result_vec.push(x)
        };
        if let Some(x) = result43 {
            result_vec.push(x)
        };
        if let Some(x) = result44 {
            result_vec.push(x)
        };
        if let Some(x) = result45 {
            result_vec.push(x)
        };
        if let Some(x) = result46 {
            result_vec.push(x)
        };
        if let Some(x) = result47 {
            result_vec.push(x)
        };
        if let Some(x) = result48 {
            result_vec.push(x)
        };
        if let Some(x) = result49 {
            result_vec.push(x)
        };
        if let Some(x) = result50 {
            result_vec.push(x)
        };
        if let Some(x) = result51 {
            result_vec.push(x)
        };
        if let Some(x) = result52 {
            result_vec.push(x)
        };
        if let Some(x) = result53 {
            result_vec.push(x)
        };
        if let Some(x) = result54 {
            result_vec.push(x)
        };
        if let Some(x) = result55 {
            result_vec.push(x)
        };
        if let Some(x) = result56 {
            result_vec.push(x)
        };
        if let Some(x) = result57 {
            result_vec.push(x)
        };
        if let Some(x) = result58 {
            result_vec.push(x)
        };
        if let Some(x) = result59 {
            result_vec.push(x)
        };

        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7, result8, result9,
            result10, result11, result12, result13, result14, result15, result16, result17,
            result18, result19, result20, result21, result22, result23, result24, result25,
            result26, result27, result28, result29, result30, result31, result32, result33,
            result34, result35, result36, result37, result38, result39, result40, result41,
            result42, result43, result44, result45, result46, result47, result48, result49,
            result50, result51, result52, result53, result54, result55, result56, result57,
            result58, result59,
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
                } else if OM::Incomplete::is_streaming() {
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

#[allow(clippy::too_many_arguments)]
pub fn take_until_first84<
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
    T36,
    T37,
    T38,
    T39,
    T40,
    T41,
    T42,
    T43,
    T44,
    T45,
    T46,
    T47,
    T48,
    T49,
    T50,
    T51,
    T52,
    T53,
    T54,
    T55,
    T56,
    T57,
    T58,
    T59,
    T60,
    T61,
    T62,
    T63,
    T64,
    T65,
    T66,
    T67,
    T68,
    T69,
    T70,
    T71,
    T72,
    T73,
    T74,
    T75,
    T76,
    T77,
    T78,
    T79,
    T80,
    T81,
    T82,
    T83,
    T84,
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
    tag25: T25,
    tag26: T26,
    tag27: T27,
    tag28: T28,
    tag29: T29,
    tag30: T30,
    tag31: T31,
    tag32: T32,
    tag33: T33,
    tag34: T34,
    tag35: T35,
    tag36: T36,
    tag37: T37,
    tag38: T38,
    tag39: T39,
    tag40: T40,
    tag41: T41,
    tag42: T42,
    tag43: T43,
    tag44: T44,
    tag45: T45,
    tag46: T46,
    tag47: T47,
    tag48: T48,
    tag49: T49,
    tag50: T50,
    tag51: T51,
    tag52: T52,
    tag53: T53,
    tag54: T54,
    tag55: T55,
    tag56: T56,
    tag57: T57,
    tag58: T58,
    tag59: T59,
    tag60: T60,
    tag61: T61,
    tag62: T62,
    tag63: T63,
    tag64: T64,
    tag65: T65,
    tag66: T66,
    tag67: T67,
    tag68: T68,
    tag69: T69,
    tag70: T70,
    tag71: T71,
    tag72: T72,
    tag73: T73,
    tag74: T74,
    tag75: T75,
    tag76: T76,
    tag77: T77,
    tag78: T78,
    tag79: T79,
    tag80: T80,
    tag81: T81,
    tag82: T82,
    tag83: T83,
    tag84: T84,
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
        + FindSubstring<T24>
        + FindSubstring<T25>
        + FindSubstring<T26>
        + FindSubstring<T27>
        + FindSubstring<T28>
        + FindSubstring<T29>
        + FindSubstring<T30>
        + FindSubstring<T31>
        + FindSubstring<T32>
        + FindSubstring<T33>
        + FindSubstring<T34>
        + FindSubstring<T35>
        + FindSubstring<T36>
        + FindSubstring<T37>
        + FindSubstring<T38>
        + FindSubstring<T39>
        + FindSubstring<T40>
        + FindSubstring<T41>
        + FindSubstring<T42>
        + FindSubstring<T43>
        + FindSubstring<T44>
        + FindSubstring<T45>
        + FindSubstring<T46>
        + FindSubstring<T47>
        + FindSubstring<T48>
        + FindSubstring<T49>
        + FindSubstring<T50>
        + FindSubstring<T51>
        + FindSubstring<T52>
        + FindSubstring<T53>
        + FindSubstring<T54>
        + FindSubstring<T55>
        + FindSubstring<T56>
        + FindSubstring<T57>
        + FindSubstring<T58>
        + FindSubstring<T59>
        + FindSubstring<T60>
        + FindSubstring<T61>
        + FindSubstring<T62>
        + FindSubstring<T63>
        + FindSubstring<T64>
        + FindSubstring<T65>
        + FindSubstring<T66>
        + FindSubstring<T67>
        + FindSubstring<T68>
        + FindSubstring<T69>
        + FindSubstring<T70>
        + FindSubstring<T71>
        + FindSubstring<T72>
        + FindSubstring<T73>
        + FindSubstring<T74>
        + FindSubstring<T75>
        + FindSubstring<T76>
        + FindSubstring<T77>
        + FindSubstring<T78>
        + FindSubstring<T79>
        + FindSubstring<T80>
        + FindSubstring<T81>
        + FindSubstring<T82>
        + FindSubstring<T83>
        + FindSubstring<T84>,
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
    T25: Clone,
    T26: Clone,
    T27: Clone,
    T28: Clone,
    T29: Clone,
    T30: Clone,
    T31: Clone,
    T32: Clone,
    T33: Clone,
    T34: Clone,
    T35: Clone,
    T36: Clone,
    T37: Clone,
    T38: Clone,
    T39: Clone,
    T40: Clone,
    T41: Clone,
    T42: Clone,
    T43: Clone,
    T44: Clone,
    T45: Clone,
    T46: Clone,
    T47: Clone,
    T48: Clone,
    T49: Clone,
    T50: Clone,
    T51: Clone,
    T52: Clone,
    T53: Clone,
    T54: Clone,
    T55: Clone,
    T56: Clone,
    T57: Clone,
    T58: Clone,
    T59: Clone,
    T60: Clone,
    T61: Clone,
    T62: Clone,
    T63: Clone,
    T64: Clone,
    T65: Clone,
    T66: Clone,
    T67: Clone,
    T68: Clone,
    T69: Clone,
    T70: Clone,
    T71: Clone,
    T72: Clone,
    T73: Clone,
    T74: Clone,
    T75: Clone,
    T76: Clone,
    T77: Clone,
    T78: Clone,
    T79: Clone,
    T80: Clone,
    T81: Clone,
    T82: Clone,
    T83: Clone,
    T84: Clone,
{
    TakeUntilFirst84 {
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
        tag25,
        tag26,
        tag27,
        tag28,
        tag29,
        tag30,
        tag31,
        tag32,
        tag33,
        tag34,
        tag35,
        tag36,
        tag37,
        tag38,
        tag39,
        tag40,
        tag41,
        tag42,
        tag43,
        tag44,
        tag45,
        tag46,
        tag47,
        tag48,
        tag49,
        tag50,
        tag51,
        tag52,
        tag53,
        tag54,
        tag55,
        tag56,
        tag57,
        tag58,
        tag59,
        tag60,
        tag61,
        tag62,
        tag63,
        tag64,
        tag65,
        tag66,
        tag67,
        tag68,
        tag69,
        tag70,
        tag71,
        tag72,
        tag73,
        tag74,
        tag75,
        tag76,
        tag77,
        tag78,
        tag79,
        tag80,
        tag81,
        tag82,
        tag83,
        tag84,
        e: PhantomData,
    }
}
pub struct TakeUntilFirst84<
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
    T36,
    T37,
    T38,
    T39,
    T40,
    T41,
    T42,
    T43,
    T44,
    T45,
    T46,
    T47,
    T48,
    T49,
    T50,
    T51,
    T52,
    T53,
    T54,
    T55,
    T56,
    T57,
    T58,
    T59,
    T60,
    T61,
    T62,
    T63,
    T64,
    T65,
    T66,
    T67,
    T68,
    T69,
    T70,
    T71,
    T72,
    T73,
    T74,
    T75,
    T76,
    T77,
    T78,
    T79,
    T80,
    T81,
    T82,
    T83,
    T84,
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
    tag25: T25,
    tag26: T26,
    tag27: T27,
    tag28: T28,
    tag29: T29,
    tag30: T30,
    tag31: T31,
    tag32: T32,
    tag33: T33,
    tag34: T34,
    tag35: T35,
    tag36: T36,
    tag37: T37,
    tag38: T38,
    tag39: T39,
    tag40: T40,
    tag41: T41,
    tag42: T42,
    tag43: T43,
    tag44: T44,
    tag45: T45,
    tag46: T46,
    tag47: T47,
    tag48: T48,
    tag49: T49,
    tag50: T50,
    tag51: T51,
    tag52: T52,
    tag53: T53,
    tag54: T54,
    tag55: T55,
    tag56: T56,
    tag57: T57,
    tag58: T58,
    tag59: T59,
    tag60: T60,
    tag61: T61,
    tag62: T62,
    tag63: T63,
    tag64: T64,
    tag65: T65,
    tag66: T66,
    tag67: T67,
    tag68: T68,
    tag69: T69,
    tag70: T70,
    tag71: T71,
    tag72: T72,
    tag73: T73,
    tag74: T74,
    tag75: T75,
    tag76: T76,
    tag77: T77,
    tag78: T78,
    tag79: T79,
    tag80: T80,
    tag81: T81,
    tag82: T82,
    tag83: T83,
    tag84: T84,
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
    T25,
    T26,
    T27,
    T28,
    T29,
    T30,
    T31,
    T32,
    T33,
    T34,
    T35,
    T36,
    T37,
    T38,
    T39,
    T40,
    T41,
    T42,
    T43,
    T44,
    T45,
    T46,
    T47,
    T48,
    T49,
    T50,
    T51,
    T52,
    T53,
    T54,
    T55,
    T56,
    T57,
    T58,
    T59,
    T60,
    T61,
    T62,
    T63,
    T64,
    T65,
    T66,
    T67,
    T68,
    T69,
    T70,
    T71,
    T72,
    T73,
    T74,
    T75,
    T76,
    T77,
    T78,
    T79,
    T80,
    T81,
    T82,
    T83,
    T84,
    Error: ParseError<I>,
> Parser<I>
    for TakeUntilFirst84<
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
        T25,
        T26,
        T27,
        T28,
        T29,
        T30,
        T31,
        T32,
        T33,
        T34,
        T35,
        T36,
        T37,
        T38,
        T39,
        T40,
        T41,
        T42,
        T43,
        T44,
        T45,
        T46,
        T47,
        T48,
        T49,
        T50,
        T51,
        T52,
        T53,
        T54,
        T55,
        T56,
        T57,
        T58,
        T59,
        T60,
        T61,
        T62,
        T63,
        T64,
        T65,
        T66,
        T67,
        T68,
        T69,
        T70,
        T71,
        T72,
        T73,
        T74,
        T75,
        T76,
        T77,
        T78,
        T79,
        T80,
        T81,
        T82,
        T83,
        T84,
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
        + FindSubstring<T24>
        + FindSubstring<T25>
        + FindSubstring<T26>
        + FindSubstring<T27>
        + FindSubstring<T28>
        + FindSubstring<T29>
        + FindSubstring<T30>
        + FindSubstring<T31>
        + FindSubstring<T32>
        + FindSubstring<T33>
        + FindSubstring<T34>
        + FindSubstring<T35>
        + FindSubstring<T36>
        + FindSubstring<T37>
        + FindSubstring<T38>
        + FindSubstring<T39>
        + FindSubstring<T40>
        + FindSubstring<T41>
        + FindSubstring<T42>
        + FindSubstring<T43>
        + FindSubstring<T44>
        + FindSubstring<T45>
        + FindSubstring<T46>
        + FindSubstring<T47>
        + FindSubstring<T48>
        + FindSubstring<T49>
        + FindSubstring<T50>
        + FindSubstring<T51>
        + FindSubstring<T52>
        + FindSubstring<T53>
        + FindSubstring<T54>
        + FindSubstring<T55>
        + FindSubstring<T56>
        + FindSubstring<T57>
        + FindSubstring<T58>
        + FindSubstring<T59>
        + FindSubstring<T60>
        + FindSubstring<T61>
        + FindSubstring<T62>
        + FindSubstring<T63>
        + FindSubstring<T64>
        + FindSubstring<T65>
        + FindSubstring<T66>
        + FindSubstring<T67>
        + FindSubstring<T68>
        + FindSubstring<T69>
        + FindSubstring<T70>
        + FindSubstring<T71>
        + FindSubstring<T72>
        + FindSubstring<T73>
        + FindSubstring<T74>
        + FindSubstring<T75>
        + FindSubstring<T76>
        + FindSubstring<T77>
        + FindSubstring<T78>
        + FindSubstring<T79>
        + FindSubstring<T80>
        + FindSubstring<T81>
        + FindSubstring<T82>
        + FindSubstring<T83>
        + FindSubstring<T84>,
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
    T25: Clone,
    T26: Clone,
    T27: Clone,
    T28: Clone,
    T29: Clone,
    T30: Clone,
    T31: Clone,
    T32: Clone,
    T33: Clone,
    T34: Clone,
    T35: Clone,
    T36: Clone,
    T37: Clone,
    T38: Clone,
    T39: Clone,
    T40: Clone,
    T41: Clone,
    T42: Clone,
    T43: Clone,
    T44: Clone,
    T45: Clone,
    T46: Clone,
    T47: Clone,
    T48: Clone,
    T49: Clone,
    T50: Clone,
    T51: Clone,
    T52: Clone,
    T53: Clone,
    T54: Clone,
    T55: Clone,
    T56: Clone,
    T57: Clone,
    T58: Clone,
    T59: Clone,
    T60: Clone,
    T61: Clone,
    T62: Clone,
    T63: Clone,
    T64: Clone,
    T65: Clone,
    T66: Clone,
    T67: Clone,
    T68: Clone,
    T69: Clone,
    T70: Clone,
    T71: Clone,
    T72: Clone,
    T73: Clone,
    T74: Clone,
    T75: Clone,
    T76: Clone,
    T77: Clone,
    T78: Clone,
    T79: Clone,
    T80: Clone,
    T81: Clone,
    T82: Clone,
    T83: Clone,
    T84: Clone,
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
        let result25 = i.find_substring(self.tag25.clone());
        let result26 = i.find_substring(self.tag26.clone());
        let result27 = i.find_substring(self.tag27.clone());
        let result28 = i.find_substring(self.tag28.clone());
        let result29 = i.find_substring(self.tag29.clone());
        let result30 = i.find_substring(self.tag30.clone());
        let result31 = i.find_substring(self.tag31.clone());
        let result32 = i.find_substring(self.tag32.clone());
        let result33 = i.find_substring(self.tag33.clone());
        let result34 = i.find_substring(self.tag34.clone());
        let result35 = i.find_substring(self.tag35.clone());
        let result36 = i.find_substring(self.tag36.clone());
        let result37 = i.find_substring(self.tag37.clone());
        let result38 = i.find_substring(self.tag38.clone());
        let result39 = i.find_substring(self.tag39.clone());
        let result40 = i.find_substring(self.tag40.clone());
        let result41 = i.find_substring(self.tag41.clone());
        let result42 = i.find_substring(self.tag42.clone());
        let result43 = i.find_substring(self.tag43.clone());
        let result44 = i.find_substring(self.tag44.clone());
        let result45 = i.find_substring(self.tag45.clone());
        let result46 = i.find_substring(self.tag46.clone());
        let result47 = i.find_substring(self.tag47.clone());
        let result48 = i.find_substring(self.tag48.clone());
        let result49 = i.find_substring(self.tag49.clone());
        let result50 = i.find_substring(self.tag50.clone());
        let result51 = i.find_substring(self.tag51.clone());
        let result52 = i.find_substring(self.tag52.clone());
        let result53 = i.find_substring(self.tag53.clone());
        let result54 = i.find_substring(self.tag54.clone());
        let result55 = i.find_substring(self.tag55.clone());
        let result56 = i.find_substring(self.tag56.clone());
        let result57 = i.find_substring(self.tag57.clone());
        let result58 = i.find_substring(self.tag58.clone());
        let result59 = i.find_substring(self.tag59.clone());
        let result60 = i.find_substring(self.tag60.clone());
        let result61 = i.find_substring(self.tag61.clone());
        let result62 = i.find_substring(self.tag62.clone());
        let result63 = i.find_substring(self.tag63.clone());
        let result64 = i.find_substring(self.tag64.clone());
        let result65 = i.find_substring(self.tag65.clone());
        let result66 = i.find_substring(self.tag66.clone());
        let result67 = i.find_substring(self.tag67.clone());
        let result68 = i.find_substring(self.tag68.clone());
        let result69 = i.find_substring(self.tag69.clone());
        let result70 = i.find_substring(self.tag70.clone());
        let result71 = i.find_substring(self.tag71.clone());
        let result72 = i.find_substring(self.tag72.clone());
        let result73 = i.find_substring(self.tag73.clone());
        let result74 = i.find_substring(self.tag74.clone());
        let result75 = i.find_substring(self.tag75.clone());
        let result76 = i.find_substring(self.tag76.clone());
        let result77 = i.find_substring(self.tag77.clone());
        let result78 = i.find_substring(self.tag78.clone());
        let result79 = i.find_substring(self.tag79.clone());
        let result80 = i.find_substring(self.tag80.clone());
        let result81 = i.find_substring(self.tag81.clone());
        let result82 = i.find_substring(self.tag82.clone());
        let result83 = i.find_substring(self.tag83.clone());
        let result84 = i.find_substring(self.tag84.clone());
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
        };
        if let Some(x) = result24 {
            result_vec.push(x)
        };
        if let Some(x) = result25 {
            result_vec.push(x)
        };
        if let Some(x) = result26 {
            result_vec.push(x)
        };
        if let Some(x) = result27 {
            result_vec.push(x)
        };
        if let Some(x) = result28 {
            result_vec.push(x)
        };
        if let Some(x) = result29 {
            result_vec.push(x)
        };
        if let Some(x) = result30 {
            result_vec.push(x)
        };
        if let Some(x) = result31 {
            result_vec.push(x)
        };
        if let Some(x) = result32 {
            result_vec.push(x)
        };
        if let Some(x) = result33 {
            result_vec.push(x)
        };
        if let Some(x) = result34 {
            result_vec.push(x)
        };
        if let Some(x) = result35 {
            result_vec.push(x)
        };
        if let Some(x) = result36 {
            result_vec.push(x)
        };
        if let Some(x) = result37 {
            result_vec.push(x)
        };
        if let Some(x) = result38 {
            result_vec.push(x)
        };
        if let Some(x) = result39 {
            result_vec.push(x)
        };
        if let Some(x) = result40 {
            result_vec.push(x)
        };
        if let Some(x) = result41 {
            result_vec.push(x)
        };
        if let Some(x) = result42 {
            result_vec.push(x)
        };
        if let Some(x) = result43 {
            result_vec.push(x)
        };
        if let Some(x) = result44 {
            result_vec.push(x)
        };
        if let Some(x) = result45 {
            result_vec.push(x)
        };
        if let Some(x) = result46 {
            result_vec.push(x)
        };
        if let Some(x) = result47 {
            result_vec.push(x)
        };
        if let Some(x) = result48 {
            result_vec.push(x)
        };
        if let Some(x) = result49 {
            result_vec.push(x)
        };
        if let Some(x) = result50 {
            result_vec.push(x)
        };
        if let Some(x) = result51 {
            result_vec.push(x)
        };
        if let Some(x) = result52 {
            result_vec.push(x)
        };
        if let Some(x) = result53 {
            result_vec.push(x)
        };
        if let Some(x) = result54 {
            result_vec.push(x)
        };
        if let Some(x) = result55 {
            result_vec.push(x)
        };
        if let Some(x) = result56 {
            result_vec.push(x)
        };
        if let Some(x) = result57 {
            result_vec.push(x)
        };
        if let Some(x) = result58 {
            result_vec.push(x)
        };
        if let Some(x) = result59 {
            result_vec.push(x)
        };
        if let Some(x) = result60 {
            result_vec.push(x)
        };
        if let Some(x) = result61 {
            result_vec.push(x)
        };
        if let Some(x) = result62 {
            result_vec.push(x)
        };
        if let Some(x) = result63 {
            result_vec.push(x)
        };
        if let Some(x) = result64 {
            result_vec.push(x)
        };
        if let Some(x) = result65 {
            result_vec.push(x)
        };
        if let Some(x) = result66 {
            result_vec.push(x)
        };
        if let Some(x) = result67 {
            result_vec.push(x)
        };
        if let Some(x) = result68 {
            result_vec.push(x)
        };
        if let Some(x) = result69 {
            result_vec.push(x)
        };
        if let Some(x) = result70 {
            result_vec.push(x)
        };
        if let Some(x) = result71 {
            result_vec.push(x)
        };
        if let Some(x) = result72 {
            result_vec.push(x)
        };
        if let Some(x) = result73 {
            result_vec.push(x)
        };
        if let Some(x) = result74 {
            result_vec.push(x)
        };
        if let Some(x) = result75 {
            result_vec.push(x)
        };
        if let Some(x) = result76 {
            result_vec.push(x)
        };
        if let Some(x) = result77 {
            result_vec.push(x)
        };
        if let Some(x) = result78 {
            result_vec.push(x)
        };
        if let Some(x) = result79 {
            result_vec.push(x)
        };
        if let Some(x) = result80 {
            result_vec.push(x)
        };
        if let Some(x) = result81 {
            result_vec.push(x)
        };
        if let Some(x) = result82 {
            result_vec.push(x)
        };
        if let Some(x) = result83 {
            result_vec.push(x)
        };
        if let Some(x) = result84 {
            result_vec.push(x)
        };

        let tag = result_vec.into_iter().min().unwrap_or(0);

        match (
            result1, result2, result3, result4, result5, result6, result7, result8, result9,
            result10, result11, result12, result13, result14, result15, result16, result17,
            result18, result19, result20, result21, result22, result23, result24, result25,
            result26, result27, result28, result29, result30, result31, result32, result33,
            result34, result35, result36, result37, result38, result39, result40, result41,
            result42, result43, result44, result45, result46, result47, result48, result49,
            result50, result51, result52, result53, result54, result55, result56, result57,
            result58, result59, result60, result61, result62, result63, result64, result65,
            result66, result67, result68, result69, result70, result71, result72, result73,
            result74, result75, result76, result77, result78, result79, result80, result81,
            result82, result83, result84,
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
                } else if OM::Incomplete::is_streaming() {
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
