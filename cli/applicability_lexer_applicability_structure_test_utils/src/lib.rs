use applicability_lexer_base::applicability_structure::LexerToken;
use nom::{IResult, error::Error};
use nom_locate::LocatedSpan;

pub type ResultType<I> =
    IResult<LocatedSpan<I>, Vec<LexerToken<LocatedSpan<I>>>, Error<LocatedSpan<I>>>;
