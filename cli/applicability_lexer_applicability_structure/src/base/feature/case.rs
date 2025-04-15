use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    feature::case::FeatureCase,
    utils::locatable::{position, Locatable},
    position::Position
};

pub trait LexFeatureCase {
    fn lex_feature_case<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_case_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureCase for T
where
    T: FeatureCase,
{
    fn lex_feature_case<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position().and(self.feature_case()).and(position()).map(
            |((start, _), end): ((Position, _), Position)| LexerToken::FeatureCase((start, end)),
        )
    }

    fn lex_feature_case_tag<'x>(&self) -> &'x str {
        self.feature_case_tag()
    }
}
