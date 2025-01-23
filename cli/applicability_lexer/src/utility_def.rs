use nom::{combinator::value, error::ParseError, Err, Parser};

use crate::LexerToken;

/**
 * This function will be responsible for parsing a space character...typically will be a space1()
 */
pub fn lex_space_def<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::Space, inner)
}

/**
 * This function will be responsible for parsing a start paren character...typically will be a tag("[")
 */
pub fn lex_start_paren_def<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::StartParen, inner)
}

/**
 * This function will be responsible for parsing a end paren character...typically will be a tag("]")
 */
pub fn lex_end_paren_def<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::EndParen, inner)
}
