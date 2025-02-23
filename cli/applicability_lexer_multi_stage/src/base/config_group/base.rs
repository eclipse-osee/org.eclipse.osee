use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait ConfigurationGroupBase {
    fn is_config_group_base<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn config_group_base<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.config_group_base_tag())
    }
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming config_group_base into LexerToken
    fn take_till_config_group_base<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_config_group_base::<I>(x))
    }
    fn config_group_base_tag<'x>(&self) -> &'x str{
        "ConfigurationGroup"
    }
    fn take_until_config_group_base<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.config_group_base_tag())
    }
}