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
use nom::{bytes::complete::tag, IResult};

pub fn end_tag_parser<'a>(
    custom_comment_syntax: &'a str,
) -> impl FnMut(&'a str) -> IResult<&str, &str> {
    tag(custom_comment_syntax)
}
