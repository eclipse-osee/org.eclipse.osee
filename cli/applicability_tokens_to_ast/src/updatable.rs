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
#[derive(Debug, Clone, PartialEq, Eq)]
pub struct UpdatableValue<T>
where
    T: Copy + PartialEq,
{
    pub previous_value: T,
    pub current_value: T,
}
impl<T> UpdatableValue<T>
where
    T: Copy + PartialEq,
{
    pub fn new(value: T) -> Self {
        Self {
            previous_value: value,
            current_value: value,
        }
    }
    pub fn next(&mut self, value: T) {
        self.previous_value = self.current_value;
        self.current_value = value;
    }
    pub fn has_changed(&self) -> bool {
        self.previous_value != self.current_value
    }
    pub fn get_value(&self) -> T {
        self.current_value
    }
}
