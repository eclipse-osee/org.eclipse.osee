use nom::{
    bytes::{tag, take_till, take_until},
    error::ParseError,
    AsChar, Compare, FindSubstring, Input, Parser,
};

pub trait StartCommentSingleLineTerminated {
    fn is_start_comment_single_line_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn is_start_comment_single_line_terminated_predicate<I>(
        &self,
        input: I::Item,
        index: usize,
    ) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        match self
            .start_comment_single_line_terminated_tag()
            .chars()
            .nth(index)
        {
            Some(character) => input.as_char() == character,
            None => false,
        }
    }
    fn is_start_comment_single_line_terminated_predicate_length<I>(&self, index: usize) -> usize
    where
        I: Input,
        I::Item: AsChar,
    {
        match self
            .start_comment_single_line_terminated_tag()
            .chars()
            .nth(index)
        {
            Some(character) => character.len(),
            None => 0,
        }
    }
    fn has_start_comment_single_line_terminated_support(&self) -> bool;
    fn start_comment_single_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.start_comment_single_line_terminated_tag())
    }
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming start_comment_single_line_terminated into LexerToken
    fn take_till_start_comment_single_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_start_comment_single_line_terminated::<I>(x))
    }
    fn start_comment_single_line_terminated_tag<'x>(&self) -> &'x str;
    fn take_until_start_comment_single_line_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.start_comment_single_line_terminated_tag())
    }
}
pub trait StartCommentSingleLineNonTerminated {
    fn is_start_comment_single_line_non_terminated<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn is_start_comment_single_line_non_terminated_predicate<I>(
        &self,
        input: I::Item,
        index: usize,
    ) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        match self
            .start_comment_single_line_non_terminated_tag()
            .chars()
            .nth(index)
        {
            Some(character) => input.as_char() == character,
            None => false,
        }
    }
    fn is_start_comment_single_line_non_terminated_predicate_length<I>(&self, index: usize) -> usize
    where
        I: Input,
        I::Item: AsChar,
    {
        match self
            .start_comment_single_line_non_terminated_tag()
            .chars()
            .nth(index)
        {
            Some(character) => character.len(),
            None => 0,
        }
    }
    fn has_start_comment_single_line_non_terminated_support(&self) -> bool;
    fn start_comment_single_line_non_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.start_comment_single_line_non_terminated_tag())
    }
    //TODO implementation of this should look like char(comment_part1).and(comment_part2)...
    //TODO add default impl for transforming start_comment_single_line_terminated into LexerToken
    fn take_till_start_comment_single_line_non_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_start_comment_single_line_non_terminated::<I>(x))
    }
    fn start_comment_single_line_non_terminated_tag<'x>(&self) -> &'x str;
    fn take_until_start_comment_single_line_non_terminated<'x, I, E>(
        &self,
    ) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.start_comment_single_line_non_terminated_tag())
    }
}

pub trait EndCommentSingleLineTerminated {
    fn is_end_comment_single_line<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn is_end_comment_single_line_predicate<I>(&self, input: I::Item, index: usize) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        match self.end_comment_single_line_tag().chars().nth(index) {
            Some(character) => input.as_char() == character,
            None => false,
        }
    }
    fn is_end_comment_single_line_predicate_length<I>(&self, index: usize) -> usize
    where
        I: Input,
        I::Item: AsChar,
    {
        match self.end_comment_single_line_tag().chars().nth(index) {
            Some(character) => character.len(),
            None => 0,
        }
    }
    fn has_end_comment_single_line_terminated_support(&self) -> bool;
    fn end_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.end_comment_single_line_tag())
    }
    fn take_till_end_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_end_comment_single_line::<I>(x))
    }
    fn end_comment_single_line_tag<'x>(&self) -> &'x str;
    fn take_until_end_comment_single_line<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.end_comment_single_line_tag())
    }
}
