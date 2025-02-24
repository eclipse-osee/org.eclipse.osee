use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::{
        delimiters::{
            brace::{EndBrace, StartBrace},
            paren::{EndParen, StartParen},
            space::Space,
            tab::Tab,
        },
        logic::{and::And, not::Not, or::Or},
        utils::take_first::take_until_first8,
    },
    second_stage::token::LexerToken,
};

pub trait TagTerminated {
    fn terminated_tag<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}
impl<T> TagTerminated for T
where
    T: StartBrace + EndBrace + StartParen + EndParen + And + Or + Not + Space + Tab,
{
    fn terminated_tag<I, E>(&self) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        let start_brace = self.start_brace().map(|_| LexerToken::StartBrace);
        let end_brace = self.end_brace().map(|_| LexerToken::EndBrace);

        let start_paren = self.start_paren().map(|_| LexerToken::StartParen);
        let end_paren = self.end_paren().map(|_| LexerToken::EndParen);

        let and = self.and().map(|_| LexerToken::And);
        let or = self.or().map(|_| LexerToken::Or);
        let not = self.not().map(|_| LexerToken::Not);
        let tag_text = take_until_first8(
            self.space_tag(),
            self.tab_tag(),
            self.or_tag(),
            self.not_tag(),
            self.and_tag(),
            self.start_paren_tag(),
            self.end_paren_tag(),
            self.end_brace_tag(),
        )
        .map(|x: I| LexerToken::Tag(x.into()));
        //TODO: verify many0 works instead of many_till
        let tag = start_brace
            .and(many0(
                self.space()
                    .map(|_| LexerToken::Space)
                    .or(self.tab().map(|_| LexerToken::Tab))
                    .or(and)
                    .or(or)
                    .or(not)
                    .or(start_paren)
                    .or(end_paren)
                    .or(tag_text),
            ))
            .and(end_brace)
            .map(|((start, mut t), end)| {
                t.insert(0, start);
                t.push(end);
                t
            });
        tag
    }
}
