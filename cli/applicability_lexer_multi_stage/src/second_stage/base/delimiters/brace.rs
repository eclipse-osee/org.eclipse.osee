use nom::{bytes::take, error::ParseError, AsChar, Compare, Input, Parser};

use crate::{
    base::{
        delimiters::brace::{EndBrace, StartBrace},
        utils::locatable::{position, Locatable},
    },
    second_stage::token::LexerToken,
};

pub trait LexStartBrace {
    fn lex_start_brace<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_start_brace_tag<'x>(&self) -> &'x str;
}

impl<T> LexStartBrace for T
where
    T: StartBrace,
{
    fn lex_start_brace<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.start_brace()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| {
                LexerToken::StartBrace(start, end)
            },
        )
    }

    fn lex_start_brace_tag<'x>(&self) -> &'x str {
        self.start_brace_tag()
    }
}
pub trait LexEndBrace {
    fn lex_end_brace<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_end_brace_tag<'x>(&self) -> &'x str;
}

impl<T> LexEndBrace for T
where
    T: EndBrace,
{
    fn lex_end_brace<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.end_brace()).and(position()).map(
            |((start, _), end): (((usize, u32), _), (usize, u32))| LexerToken::EndBrace(start, end),
        )
    }

    fn lex_end_brace_tag<'x>(&self) -> &'x str {
        self.end_brace_tag()
    }
}
#[cfg(test)]
mod tests {
    use std::marker::PhantomData;

    use nom::{error::Error, IResult, Parser};
    use nom_locate::LocatedSpan;

    use super::LexStartBrace;
    use crate::{default::DefaultApplicabilityLexer, second_stage::token::LexerToken};
    pub struct TestDoc<'a> {
        _ph: PhantomData<&'a str>,
    }
    impl<'a> DefaultApplicabilityLexer for TestDoc<'a> {
        fn is_default() -> bool {
            true
        }
    }
    #[test]
    fn test() {
        let doc = TestDoc { _ph: PhantomData };
        let mut parser = doc.lex_start_brace();
        let result: IResult<
            LocatedSpan<&str>,
            LexerToken<LocatedSpan<&str>>,
            Error<LocatedSpan<&str>>,
        > = Ok((
            unsafe { LocatedSpan::new_from_raw_offset(1, 1, "", ()) },
            LexerToken::StartBrace((0, 1), (1, 1)),
        ));
        assert_eq!(parser.parse_complete(LocatedSpan::new("[")), result);
    }
}
