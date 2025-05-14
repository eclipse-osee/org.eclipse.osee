use memchr::memmem;
use nom::{
    AsBytes, AsChar, Compare, FindSubstring, Input, Parser,
    bytes::{tag, take_till, take_until},
    error::ParseError,
};

pub trait StartCodeBlock {
    fn is_start_code_block<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn is_start_code_block_predicate<I>(&self, input: I::Item, index: usize) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        match self.start_code_block_tag().chars().nth(index) {
            Some(character) => input.as_char() == character,
            None => false,
        }
    }
    fn start_code_block_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + AsBytes,
    {
        let finder = memmem::Finder::new(self.start_code_block_tag());
        finder.find(input.as_bytes())
    }
    fn is_start_code_block_predicate_length<I>(&self, index: usize) -> usize
    where
        I: Input,
        I::Item: AsChar,
    {
        match self.start_code_block_tag().chars().nth(index) {
            Some(character) => character.len(),
            None => 0,
        }
    }
    fn has_start_code_block_support(&self) -> bool;
    fn start_code_block<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.start_code_block_tag())
    }
    fn take_till_start_code_block<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_start_code_block::<I>(x))
    }
    fn start_code_block_tag<'x>(&self) -> &'x str;
    fn take_until_start_code_block<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.start_code_block_tag())
    }
}

pub trait EndCodeBlock {
    fn is_end_code_block<I>(&self, input: I::Item) -> bool
    where
        I: Input,
        I::Item: AsChar;
    fn is_end_code_block_predicate<I>(&self, input: I::Item, index: usize) -> bool
    where
        I: Input,
        I::Item: AsChar,
    {
        match self.end_code_block_tag().chars().nth(index) {
            Some(character) => input.as_char() == character,
            None => false,
        }
    }
    fn end_code_block_position<I>(&self, input: &I) -> Option<usize>
    where
        I: Input + AsBytes,
    {
        let finder = memmem::Finder::new(self.end_code_block_tag());
        finder.find(input.as_bytes())
    }
    fn is_end_code_block_predicate_length<I>(&self, index: usize) -> usize
    where
        I: Input,
        I::Item: AsChar,
    {
        match self.end_code_block_tag().chars().nth(index) {
            Some(character) => character.len(),
            None => 0,
        }
    }
    fn has_end_code_block_support(&self) -> bool;
    fn end_code_block<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        tag(self.end_code_block_tag())
    }
    fn take_till_end_code_block<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_till(|x| self.is_end_code_block::<I>(x))
    }
    fn end_code_block_tag<'x>(&self) -> &'x str;
    fn take_until_end_code_block<'x, I, E>(&self) -> impl Parser<I, Output = I, Error = E>
    where
        I: Input + Compare<&'x str> + FindSubstring<&'x str>,
        I::Item: AsChar,
        E: ParseError<I>,
    {
        take_until(self.end_code_block_tag())
    }
}
