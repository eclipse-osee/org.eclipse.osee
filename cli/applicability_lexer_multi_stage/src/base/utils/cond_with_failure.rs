use nom::{combinator::fail, error::ParseError, Parser};

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
