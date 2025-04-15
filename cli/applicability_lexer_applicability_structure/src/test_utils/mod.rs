use applicability_lexer_base::applicability_structure::LexerToken;
use nom::{error::Error, IResult};
use nom_locate::LocatedSpan;

pub type ResultType<I> =
    IResult<LocatedSpan<I>, Vec<LexerToken<LocatedSpan<I>>>, Error<LocatedSpan<I>>>;
