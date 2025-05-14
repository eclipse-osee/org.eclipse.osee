use nom::{error::ParseError, AsChar, Compare, Input, Parser};

use applicability_lexer_base::{
    applicability_structure::LexerToken,
    feature::base::FeatureBase,
    utils::locatable::{position, Locatable},
    position::Position,
};

pub trait LexFeatureBase {
    fn lex_feature_base<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>;
    fn lex_feature_base_tag<'x>(&self) -> &'x str;
}

impl<T> LexFeatureBase for T
where
    T: FeatureBase,
{
    fn lex_feature_base<'x, I, E>(&self) -> impl Parser<I, Output = LexerToken<I>, Error = E>
    where
        I: Input + Compare<&'x str> + Locatable + Send + Sync,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        position()
            .and(self.feature_base())
            .and(position())
            .map(|((start, _), end): ((Position, _), Position)| LexerToken::Feature((start, end)))
    }

    fn lex_feature_base_tag<'x>(&self) -> &'x str {
        self.feature_base_tag()
    }
}
