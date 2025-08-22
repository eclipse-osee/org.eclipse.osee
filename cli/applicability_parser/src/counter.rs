/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
use nom::{
    character::complete::{line_ending, multispace1, space1},
    combinator::{map, not, opt},
    multi::{many0, many_till},
    sequence::terminated,
    IResult,
};

///
/// This function should only be called after a multispace0, to count the number of \r\n, and \n present in the multispace0
pub fn count_new_lines<'a>() -> impl FnMut(&'a str) -> IResult<&'a str, u8> {
    map(
        many_till(
            terminated(opt(line_ending), many0(space1)),
            not(multispace1),
        ),
        |(line_endings, _)| {
            line_endings
                .iter()
                .map(|x| match x {
                    Some(_) => 1,
                    None => 0,
                })
                .sum()
        },
    )
}
#[cfg(test)]
mod count_new_lines_tests {
    use super::count_new_lines;

    #[test]
    fn starts_with_spaces() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n"), Ok(("", 1)))
    }

    #[test]
    fn starts_with_spaces_and_has_leftovers() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n\t "), Ok(("", 1)))
    }

    #[test]
    fn starts_with_spaces_and_has_leftovers_and_extras() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n\t abcd"), Ok(("abcd", 1)))
    }

    #[test]
    fn starts_with_spaces_and_has_repeating_leftovers() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n\t\t\t "), Ok(("", 1)))
    }

    #[test]
    fn starts_with_spaces_and_has_repeating_leftovers_and_extras() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n\t\t\t abcd"), Ok(("abcd", 1)))
    }

    #[test]
    fn starts_with_spaces_and_has_leftovers_and_ends() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n\t\r\n "), Ok(("", 2)))
    }

    #[test]
    fn starts_with_spaces_and_has_leftovers_and_extras_and_ends() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n\t\r\n abcd"), Ok(("abcd", 2)))
    }

    #[test]
    fn starts_with_spaces_and_has_repeating_leftovers_and_ends() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n\t\t\t\r\n "), Ok(("", 2)))
    }

    #[test]
    fn starts_with_spaces_and_has_repeating_leftovers_and_extras_and_ends() {
        let mut parser = count_new_lines();
        assert_eq!(parser("\r\n\t\t\t\r\n abcd"), Ok(("abcd", 2)))
    }

    #[test]
    fn starts_with_invalid_char() {
        let mut parser = count_new_lines();
        assert_eq!(parser("]"), Ok(("]", 0)))
    }
}
