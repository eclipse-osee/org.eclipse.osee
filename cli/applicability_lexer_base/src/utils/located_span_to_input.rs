/*********************************************************************
 * Copyright (c) 2025 Boeing
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
use nom::Input;
use nom_locate::LocatedSpan;

pub trait LocatedSpanToInput {
    fn get_input(&self) -> &impl Input;
}

impl<T, X> LocatedSpanToInput for LocatedSpan<T, X>
where
    T: Input,
{
    fn get_input(&self) -> &impl Input {
        self.fragment()
    }
}
