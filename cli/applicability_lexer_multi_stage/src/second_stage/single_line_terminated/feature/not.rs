use nom::{error::ParseError, multi::many0, AsChar, Compare, FindSubstring, Input, Parser};

use crate::{
    base::{
        delimiters::{space::Space, tab::Tab},
        feature::not::FeatureNot,
    },
    second_stage::{
        single_line_terminated::utils::tag_terminated::TagTerminated, token::LexerToken,
    },
};

pub trait FeatureNotSingleLineTerminated {
    fn feature_not_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>;
}

impl<T> FeatureNotSingleLineTerminated for T
where
    T: TagTerminated + FeatureNot + Space + Tab,
{
    fn feature_not_terminated<I, E>(
        &self,
    ) -> impl Parser<I, Output = Vec<LexerToken<String>>, Error = E>
    where
        I: Input + Into<String> + for<'x> FindSubstring<&'x str> + for<'x> Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        //TODO: verify many0 works instead of many_till
        let tag = self.terminated_tag();
        let feature_not_tag = self
            .feature_not()
            .map(|_| LexerToken::FeatureNot)
            .and(many0(
                self.space()
                    .map(|_| LexerToken::Space)
                    .or(self.tab().map(|_| LexerToken::Tab)),
            ))
            .and(tag)
            .map(|((f, mut spaces), t)| {
                spaces.insert(0, f);
                spaces.extend(t.into_iter());
                spaces
            });
        feature_not_tag
    }
}
