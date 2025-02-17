use nom::{bytes::take_till, character::multispace0, error::ParseError, AsChar, Compare, Input, Parser};

use crate::base::comment::single_line::{EndCommentSingleLine, StartCommentSingleLine};

use super::token::FirstStageToken;

pub trait IdentifySingleLineComment{
fn identify_comment_single_line<'x, I, E>(&self) -> impl Parser<I,Output = FirstStageToken<I>, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl <T> IdentifySingleLineComment for T where T:StartCommentSingleLine + EndCommentSingleLine {
    fn identify_comment_single_line<'x, I, E>(&self) -> impl Parser<I,Output = FirstStageToken<I>, Error = E>
        where
            I: Input + Compare<&'x str>,
            I::Item: AsChar,
            E: ParseError<I> {
                let start = self.start_comment_single_line().and(take_till(|x|self.is_end_comment_single_line::<I>(x)));
                let start_and_end = start.and(self.end_comment_single_line());
                let parser = start_and_end.and(multispace0());
        parser.map(|x| FirstStageToken::SingleLineTerminatedComment(()))
    }
}