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
 * This function will be responsible for parsing a unix new line character...typically will be a newline()
 */
pub fn lex_unix_new_line_def<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::UnixNewLine, inner)
}

/**
 * This function will be responsible for parsing a unix new line character...typically will be a tag("\r")
 */
pub fn lex_carriage_return_def<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::CarriageReturn, inner)
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

/**
 * This function will be responsible for parsing a not character...typically will be a tag("!")
 */
pub fn lex_not_def<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::Not, inner)
}

/**
 * This function will be responsible for parsing an and character...typically will be a tag("&")
 */
pub fn lex_and_def<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::And, inner)
}

/**
 * This function will be responsible for parsing a or character...typically will be a tag("|")
 */
pub fn lex_or_def<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::Or, inner)
}

/**
 * This function will be responsible for parsing a start comment (single line)
 */
pub fn lex_start_comment_single_line<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::StartCommentSingleLine, inner)
}

/**
 * This function will be responsible for parsing a end comment (single line)
 */
pub fn lex_end_comment_single_line<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::EndCommentSingleLine, inner)
}

/**
 * This function will be responsible for parsing a start comment (multi line)
 */
pub fn lex_start_comment_multi_line<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::StartCommentMultiLine, inner)
}

/**
 * This function will be responsible for parsing a end comment (multi line)
 */
pub fn lex_end_comment_multi_line<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::EndCommentMultiLine, inner)
}

/**
 * This function will be responsible for parsing a end comment (multi line)
 */
pub fn lex_multi_line_comment_character<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::MultilineCommentCharacter, inner)
}

/**
 * This function will be responsible for parsing a single line comment
 */
pub fn lex_start_single_line_comment<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::SingleLineCommentCharacter, inner)
}

/**
 * This function will be responsible for parsing end of file typically the parser here will be an eof()
 */
pub fn lex_eof<I, O, E>(
    inner: impl Parser<I, O, E>,
) -> impl FnMut(I) -> Result<(I, LexerToken), Err<E>>
where
    E: ParseError<I>,
{
    value(LexerToken::Eof, inner)
}
