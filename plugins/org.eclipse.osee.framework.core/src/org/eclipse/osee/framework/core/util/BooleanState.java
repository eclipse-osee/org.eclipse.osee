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
package org.eclipse.osee.framework.core.util;

/**
 * @author Donald G. Dunne
 */
public enum BooleanState {
   Yes,
   No,
   UnSet;

   public boolean isUnSet() {
      return this == BooleanState.UnSet;
   }

   public boolean isYes() {
      return this == BooleanState.Yes;
   }

   public boolean isNo() {
      return this == BooleanState.No;
   }
}
