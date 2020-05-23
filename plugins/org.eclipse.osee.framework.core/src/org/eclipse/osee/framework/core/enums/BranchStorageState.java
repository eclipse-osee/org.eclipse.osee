/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

/**
 * @author Jeff C. Phillips
 */
public enum BranchStorageState {

   UN_ARCHIVED(0),
   ARCHIVED(1);
   private final int value;

   BranchStorageState(int value) {
      this.value = value;
   }

   public final int getValue() {
      return value;
   }
}
